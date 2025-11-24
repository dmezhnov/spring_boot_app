import { mkdir, readdir } from "node:fs/promises";
import { platform } from "node:process";
import { join } from "node:path";
import { spawn } from "bun";
import { ProcessRunner } from "./process-runner.bun.ts";
import { RunEnv } from "./run-env.bun.ts";

export class StartScript {
  private readonly runner = new ProcessRunner();
  private readonly stateFile = RunEnv.stateFile;
  private readonly logFile = RunEnv.logFile;
  private readonly pidFile = RunEnv.pidFile;
  private readonly projectRoot = RunEnv.projectRoot;
  private readonly containerName = "spring-boot-app-postgres";
  private readonly networkName = "configs_default";

  async run(): Promise<void> {
    await this.ensureDirs();
    await Bun.write(this.stateFile, "");

    const whichDocker = await this.runner.runCapture(platform === "win32" ? ["where", "docker"] : ["which", "docker"]);
    if (whichDocker.code !== 0) {
      console.error("Error: docker is not installed or not in PATH.");
      process.exit(1);
    }

    const wasRunning = await this.dockerAvailable();
    if (!wasRunning) {
      const started = await this.tryStartDocker();
      if (!started) {
        console.error("Error: docker daemon failed to start or is not reachable.");
        process.exit(1);
      }
      await this.appendState([`STARTED_DOCKER=1`]);
    } else {
      await this.appendState([`STARTED_DOCKER=0`]);
    }

    const compose = await this.pickCompose();
    if (!compose) {
      console.error("Error: docker compose is not available (neither 'docker compose' nor 'docker-compose').");
      process.exit(1);
    }
    await this.appendState([`CONTAINER_NAME=${this.containerName}`, `NETWORK_NAME=${this.networkName}`]);

    const preStatus = await this.getContainerStatus(this.containerName);
    const preNet = await this.networkExists(this.networkName);
    const upCmd =
      compose === "compose"
        ? ["docker", "compose", "-f", "configs/docker-compose.yml", "up", "-d", "postgres"]
        : ["docker-compose", "-f", "configs/docker-compose.yml", "up", "-d", "postgres"];
    const up = await this.runner.run(upCmd);
    if (up.code !== 0) {
      console.error("Error: failed to start PostgreSQL via docker compose.");
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }
    if (!preNet && (await this.networkExists(this.networkName))) {
      await this.appendState([`STARTED_NETWORK=1`]);
    } else {
      await this.appendState([`STARTED_NETWORK=0`]);
    }

    let ok = false;
    for (let i = 0; i < 30; i++) {
      const { status, health } = await this.getContainerStatus(this.containerName);
      if (status === "running" && (health === "" || health === "healthy")) {
        ok = true;
        break;
      }
      if (status === "exited" || status === "dead") {
        console.error(
          `Error: PostgreSQL container '${this.containerName}' exited or failed to start (status: ${status}, health: ${health}).`,
        );
        if (preStatus.status !== "running") {
          await this.runner.runCapture(["docker", "rm", "-f", this.containerName]);
        }
        if (!wasRunning) {
          const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
          if (ps.stdout.trim().length === 0 && platform === "linux") {
            await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
            await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
          }
        }
        process.exit(1);
      }
      await Bun.sleep(1000);
    }
    if (!ok) {
      const { status, health } = await this.getContainerStatus(this.containerName);
      console.error(
        `Error: PostgreSQL container '${this.containerName}' did not become healthy in time (status: ${status}, health: ${health}).`,
      );
      if (preStatus.status !== "running") {
        await this.runner.runCapture(["docker", "rm", "-f", this.containerName]);
      }
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }
    await this.appendState([`STARTED_POSTGRES=${preStatus.status !== "running" ? 1 : 0}`]);

    if (await this.isServerUp()) {
      console.log("Spring Boot server is already running; reusing existing instance.");
      await this.appendState([`STARTED_APP=0`]);
      return;
    }

    const build = await this.runner.run(["gradle", "build", "bootJar"]);
    if (build.code !== 0) {
      console.error("Error: failed to build Spring Boot application (bootJar).");
      if (preStatus.status !== "running") {
        await this.runner.runCapture(["docker", "rm", "-f", this.containerName]);
      }
      if (!preNet && (await this.networkExists(this.networkName))) {
        await this.runner.runCapture(["docker", "network", "rm", this.networkName]);
      }
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }

    const libsDir = join(this.projectRoot, "build", "libs");
    let jarName: string | undefined;
    try {
      const files = await readdir(libsDir);
      jarName = files.find((f) => f.endsWith(".jar") && !f.endsWith("-plain.jar"));
    } catch {
      jarName = undefined;
    }
    if (!jarName) {
      console.error(`Error: no runnable Spring Boot JAR found in ${libsDir}.`);
      if (preStatus.status !== "running") {
        await this.runner.runCapture(["docker", "rm", "-f", this.containerName]).catch(() => {});
      }
      if (!preNet && (await this.networkExists(this.networkName))) {
        await this.runner.runCapture(["docker", "network", "rm", this.networkName]).catch(() => {});
      }
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }
    const jarRelPath = join("build", "libs", jarName);

    const logHandle = await Bun.file(this.logFile).writer();
    let app;
    try {
      app = spawn(["java", "-jar", jarRelPath], {
        cwd: this.projectRoot,
        stdout: "pipe",
        stderr: "pipe",
      });
    } catch (error) {
      await logHandle.end();
      const message = error instanceof Error ? error.message : String(error);
      console.error("Error: failed to start Spring Boot process.");
      console.error(message);
      if (preStatus.status !== "running") {
        await this.runner.runCapture(["docker", "rm", "-f", this.containerName]).catch(() => {});
      }
      if (!preNet && (await this.networkExists(this.networkName))) {
        await this.runner.runCapture(["docker", "network", "rm", this.networkName]).catch(() => {});
      }
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }
    app.stdout?.pipeTo(logHandle.writable, { preventClose: true }).catch((error) => {
      const message = error instanceof Error ? error.message : String(error);
      console.warn("Warning: failed to pipe application stdout to log file.");
      console.warn(message);
    });
    app.stderr?.pipeTo(logHandle.writable, { preventClose: true }).catch((error) => {
      const message = error instanceof Error ? error.message : String(error);
      console.warn("Warning: failed to pipe application stderr to log file.");
      console.warn(message);
    });

    if ((await Promise.race([app.exited, Bun.sleep(2000).then(() => -1)])) !== -1) {
      await logHandle.end();
      console.error(`Error: Spring Boot failed to start (process exited) — see ${this.logFile}`);
      if (preStatus.status !== "running") {
        await this.runner.runCapture(["docker", "rm", "-f", this.containerName]).catch(() => {});
      }
      if (!preNet && (await this.networkExists(this.networkName))) {
        await this.runner.runCapture(["docker", "network", "rm", this.networkName]).catch(() => {});
      }
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }

    const healthy = await this.checkServerHealth();
    if (!healthy) {
      await logHandle.end();
      console.error(`Error: Spring Boot application did not become healthy in time — see ${this.logFile}`);
      if (preStatus.status !== "running") {
        await this.runner.runCapture(["docker", "rm", "-f", this.containerName]).catch(() => {});
      }
      if (!preNet && (await this.networkExists(this.networkName))) {
        await this.runner.runCapture(["docker", "network", "rm", this.networkName]).catch(() => {});
      }
      if (!wasRunning) {
        const ps = await this.runner.runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await this.runner.runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await this.runner.runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }

    await Bun.write(this.pidFile, String(app.pid));
    await this.appendState([`STARTED_APP=1`]);
    await logHandle.end();
    console.log(`Spring Boot started in background (PID ${app.pid}). Logs: ${this.logFile}`);
  }

  private async ensureDirs(): Promise<void> {
    await mkdir(RunEnv.runDir, { recursive: true });
  }

  private async appendState(lines: string[]): Promise<void> {
    const content = lines.map((l) => l + "\n").join("");
    await Bun.write(this.stateFile, content, { append: true });
  }

  private async dockerAvailable(): Promise<boolean> {
    const r = await this.runner.runCapture(["docker", "info"]);
    return r.code === 0;
  }

  private async tryStartDocker(): Promise<boolean> {
    if (await this.dockerAvailable()) return true;
    if (platform === "linux") {
      await this.runner.runCapture(["systemctl", "--user", "start", "docker"]).catch(() => {});
      await this.runner.runCapture(["systemctl", "start", "docker"]).catch(() => {});
    } else if (platform === "darwin") {
      await this.runner.runCapture(["open", "-a", "Docker"]).catch(() => {});
    } else if (platform === "win32") {
      await this.runner
        .runCapture(["powershell", "-NoProfile", "-Command", "Start-Process -FilePath 'Docker Desktop'"])
        .catch(() => {});
      await this.runner
        .runCapture([
          "powershell",
          "-NoProfile",
          "-Command",
          `Start-Process -FilePath 'C:\\\\Program Files\\\\Docker\\\\Docker\\\\Docker Desktop.exe'`,
        ])
        .catch(() => {});
    }
    for (let i = 0; i < 30; i++) {
      if (await this.dockerAvailable()) return true;
      await Bun.sleep(1000);
    }
    return false;
  }

  private async pickCompose(): Promise<"compose" | "docker-compose" | null> {
    if ((await this.runner.runCapture(["docker", "compose", "version"])).code === 0) return "compose";
    if ((await this.runner.runCapture(["docker-compose", "version"])).code === 0) return "docker-compose";
    return null;
  }

  private async getContainerStatus(
    name: string,
  ): Promise<{
    status: string;
    health: string;
  }> {
    const status = await this.runner.runCapture(["docker", "inspect", "-f", "{{ .State.Status }}", name]);
    const health = await this.runner.runCapture([
      "docker",
      "inspect",
      "-f",
      "{{ if .State.Health }}{{ .State.Health.Status }}{{ end }}",
      name,
    ]);
    return {
      status: status.code === 0 ? status.stdout.trim() : "unknown",
      health: health.code === 0 ? health.stdout.trim() : "",
    };
  }

  private async networkExists(name: string): Promise<boolean> {
    const r = await this.runner.runCapture(["docker", "network", "inspect", name]);
    return r.code === 0;
  }

  private async checkServerHealth(): Promise<boolean> {
    const maxAttempts = 30;
    for (let attempt = 0; attempt < maxAttempts; attempt++) {
      try {
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 2000);
        const res = await fetch("http://localhost:8080/api/users/health", {
          signal: controller.signal,
        });
        clearTimeout(timeout);
        if (res.status === 200) {
          return true;
        }
      } catch {
        // ignore and retry
      }
      await Bun.sleep(1000);
    }
    return false;
  }

  private async isServerUp(): Promise<boolean> {
    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 2000);
      await fetch("http://localhost:8080/api/users/health", {
        signal: controller.signal,
      });
      clearTimeout(timeout);
      return true;
    } catch {
      return false;
    }
  }
}
