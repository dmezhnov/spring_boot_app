import { unlink } from "node:fs/promises";
import { platform } from "node:process";
import { spawn } from "bun";
import { ProcessRunner } from "./process-runner.bun.ts";
import { RunEnv } from "./run-env.bun.ts";
import { DockerCli } from "./docker-cli.bun.ts";

export class StopScript {
  private readonly runner = new ProcessRunner();
  private readonly stateFile = RunEnv.stateFile;
  private readonly pidFile = RunEnv.pidFile;
  private readonly defaultContainerName = "spring-boot-app-postgres";
  private readonly defaultNetworkName = "configs_default";
  private readonly dockerCli = new DockerCli(this.runner);
  private dockerCommand: string | null = null;

  async run(): Promise<void> {
    const state = await this.readState();
    const startedApp = Number(state["STARTED_APP"] ?? 0) === 1;
    const startedPg = Number(state["STARTED_POSTGRES"] ?? 0) === 1;
    const startedNet = Number(state["STARTED_NETWORK"] ?? 0) === 1;
    const startedDocker = Number(state["STARTED_DOCKER"] ?? 0) === 1;
    const containerName = state["CONTAINER_NAME"] ?? this.defaultContainerName;
    const networkName = state["NETWORK_NAME"] ?? this.defaultNetworkName;

    if (startedPg || startedNet || startedDocker) {
      const dockerBinary = await this.dockerCli.resolveDockerBinary();
      if (!dockerBinary) {
        console.warn("Warning: docker CLI is not available; skipping Docker cleanup in StopScript.");
      } else {
        this.dockerCommand = dockerBinary;
      }
    }

    if (startedApp) {
      try {
        const pidRaw = await Bun.file(this.pidFile).text();
        const pid = Number(pidRaw.trim());
        if (!Number.isNaN(pid)) {
          await this.killPid(pid);
        }
      } catch {}
      await unlink(this.pidFile).catch(() => {});
    }

    if (startedPg) {
      if (this.dockerCommand) {
        await this.runner.runExitCode([this.dockerCommand, "rm", "-f", containerName]).catch(() => -1);
      }
    }

    if (startedNet) {
      if (this.dockerCommand) {
        await this.runner.runExitCode([this.dockerCommand, "network", "rm", networkName]).catch(() => -1);
      }
    }

    if (startedDocker) {
      await this.maybeStopDockerDaemon();
    }

    await unlink(this.stateFile).catch(() => {});
  }

  private async readState(): Promise<Record<string, string>> {
    try {
      const raw = await Bun.file(this.stateFile).text();
      const map: Record<string, string> = {};
      for (const line of raw.split(/\r?\n/)) {
        const m = line.match(/^([^=]+)=(.*)$/);
        if (m) map[m[1]] = m[2];
      }
      return map;
    } catch {
      return {};
    }
  }

  private async killPid(pid: number): Promise<void> {
    if (platform === "win32") {
      await this.runner.runExitCode(["taskkill", "/PID", String(pid), "/T", "/F"]).catch(() => -1);
    } else {
      await this.runner.runExitCode(["kill", String(pid)]).catch(() => -1);
      for (let i = 0; i < 10; i++) {
        try {
          const check = spawn(["kill", "-0", String(pid)], { stdout: "ignore", stderr: "ignore" });
          const code = await check.exited;
          if (code !== 0) break;
        } catch (error) {
          const message = error instanceof Error ? error.message : String(error);
          console.warn("Warning: failed to check process liveness with 'kill -0'.");
          console.warn(message);
          break;
        }
        await Bun.sleep(1000);
      }
      await this.runner.runExitCode(["kill", "-9", String(pid)]).catch(() => -1);
    }
  }

  private async maybeStopDockerDaemon(): Promise<void> {
    try {
      if (!this.dockerCommand) {
        return;
      }
      const ps = spawn([this.dockerCommand, "ps", "-q"], { stdout: "pipe", stderr: "ignore" });
      const [code, buf] = await Promise.all([ps.exited, ps.stdout!.arrayBuffer()]);
      const alive = code === 0 && new TextDecoder().decode(buf).trim().length > 0;
      if (!alive) {
        if (platform === "linux") {
          await this.runner.runExitCode(["systemctl", "--user", "stop", "docker"]).catch(() => -1);
          await this.runner.runExitCode(["systemctl", "stop", "docker"]).catch(() => -1);
        } else if (platform === "darwin") {
          await this.runner.runExitCode(["osascript", "-e", 'quit app "Docker"']).catch(() => -1);
        } else if (platform === "win32") {
          await this.runner
            .runExitCode([
              "powershell",
              "-NoProfile",
              "-Command",
              "Get-Process 'Docker Desktop' -ErrorAction SilentlyContinue | Stop-Process -Force",
            ])
            .catch(() => -1);
        }
      }
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      console.warn("Warning: failed to inspect running containers with 'docker ps'.");
      console.warn(message);
    }
  }
}
