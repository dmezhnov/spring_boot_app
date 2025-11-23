#!/usr/bin/env bun

import { spawn } from "bun";
import { writeFile, mkdir, readFile, rm } from "node:fs/promises";
import { platform } from "node:process";
import { join, dirname } from "node:path";

type RunResult = { code: number; stdout: string; stderr: string };

const projectRoot = dirname(new URL(import.meta.url).pathname).replace(/\/scripts$/, "");
const runDir = join(projectRoot, "build", ".run");
const stateFile = join(runDir, "state.env");
const logFile = join(runDir, "app.log");
const pidFile = join(runDir, "app.pid");
const containerName = "spring-boot-app-postgres";
const networkName = "spring_boot_app_default";

async function ensureDirs() {
  await mkdir(runDir, { recursive: true });
}

async function run(cmd: string[], cwd = projectRoot, inherit = true): Promise<RunResult> {
  const proc = spawn(cmd, {
    cwd,
    stdout: inherit ? "inherit" : "pipe",
    stderr: inherit ? "inherit" : "pipe",
  });
  let stdout: Uint8Array = new Uint8Array();
  let stderr: Uint8Array = new Uint8Array();
  if (!inherit) {
    const outBuf = proc.stdout ? await new Response(proc.stdout).arrayBuffer() : new ArrayBuffer(0);
    const errBuf = proc.stderr ? await new Response(proc.stderr).arrayBuffer() : new ArrayBuffer(0);
    stdout = new Uint8Array(outBuf);
    stderr = new Uint8Array(errBuf);
  }
  const code = await proc.exited;
  return {
    code,
    stdout: inherit ? "" : new TextDecoder().decode(stdout),
    stderr: inherit ? "" : new TextDecoder().decode(stderr),
  };
}

async function runCapture(cmd: string[], cwd = projectRoot): Promise<RunResult> {
  return run(cmd, cwd, false);
}

async function appendState(lines: string[]) {
  const content = lines.map((l) => l + "\n").join("");
  await writeFile(stateFile, content, { flag: "a" });
}

async function dockerAvailable(): Promise<boolean> {
  const r = await runCapture(["docker", "info"]);
  return r.code === 0;
}

async function tryStartDocker(): Promise<boolean> {
  if (await dockerAvailable()) return true;
  if (platform === "linux") {
    await runCapture(["systemctl", "--user", "start", "docker"]).catch(() => {});
    await runCapture(["systemctl", "start", "docker"]).catch(() => {});
  } else if (platform === "darwin") {
    await runCapture(["open", "-a", "Docker"]).catch(() => {});
  } else if (platform === "win32") {
    // Try typical ways to start Docker Desktop on Windows
    await runCapture(["powershell", "-NoProfile", "-Command", "Start-Process -FilePath 'Docker Desktop'"]).catch(() => {});
    await runCapture([
      "powershell",
      "-NoProfile",
      "-Command",
      `Start-Process -FilePath 'C:\\\\Program Files\\\\Docker\\\\Docker\\\\Docker Desktop.exe'`,
    ]).catch(() => {});
  }
  // wait up to 30s
  for (let i = 0; i < 30; i++) {
    if (await dockerAvailable()) return true;
    await Bun.sleep(1000);
  }
  return false;
}

async function pickCompose(): Promise<"compose" | "docker-compose" | null> {
  if ((await runCapture(["docker", "compose", "version"])).code === 0) return "compose";
  if ((await runCapture(["docker-compose", "version"])).code === 0) return "docker-compose";
  return null;
}

async function getContainerStatus(name: string): Promise<{ status: string; health: string }> {
  const status = await runCapture(["docker", "inspect", "-f", "{{ .State.Status }}", name]);
  const health = await runCapture(["docker", "inspect", "-f", "{{ if .State.Health }}{{ .State.Health.Status }}{{ end }}", name]);
  return {
    status: status.code === 0 ? status.stdout.trim() : "unknown",
    health: health.code === 0 ? health.stdout.trim() : "",
  };
}

async function networkExists(name: string): Promise<boolean> {
  const r = await runCapture(["docker", "network", "inspect", name]);
  return r.code === 0;
}

async function checkServerHealth(): Promise<boolean> {
  try {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 2000);
    const res = await fetch("http://localhost:8080/api/users/health", {
      signal: controller.signal,
    });
    clearTimeout(timeout);
    return res.status === 200;
  } catch {
    return false;
  }
}

async function main() {
  await ensureDirs();
  await writeFile(stateFile, ""); // reset

  // 1) docker installed
  const whichDocker = await runCapture(platform === "win32" ? ["where", "docker"] : ["which", "docker"]);
  if (whichDocker.code !== 0) {
    console.error("Error: docker is not installed or not in PATH.");
    process.exit(1);
  }

  // 2) ensure daemon running (and mark if we started it)
  const wasRunning = await dockerAvailable();
  if (!wasRunning) {
    const started = await tryStartDocker();
    if (!started) {
      console.error("Error: docker daemon failed to start or is not reachable.");
      process.exit(1);
    }
    await appendState([`STARTED_DOCKER=1`]);
  } else {
    await appendState([`STARTED_DOCKER=0`]);
  }

  // 3) compose command
  const compose = await pickCompose();
  if (!compose) {
    console.error("Error: docker compose is not available (neither 'docker compose' nor 'docker-compose').");
    process.exit(1);
  }
  await appendState([`CONTAINER_NAME=${containerName}`, `NETWORK_NAME=${networkName}`]);

  // 4) start postgres
  const preStatus = await getContainerStatus(containerName);
  const preNet = await networkExists(networkName);
  const upCmd =
    compose === "compose"
      ? ["docker", "compose", "-f", "docker/docker-compose.yml", "up", "-d", "postgres"]
      : ["docker-compose", "-f", "docker/docker-compose.yml", "up", "-d", "postgres"];
  const up = await run(upCmd);
  if (up.code !== 0) {
    console.error("Error: failed to start PostgreSQL via docker compose.");
    if (!wasRunning) {
      const ps = await runCapture(["docker", "ps", "-q"]);
      if (ps.stdout.trim().length === 0) {
        if (platform === "linux") {
          await runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
    }
    process.exit(1);
  }
  if (!preNet && (await networkExists(networkName))) {
    await appendState([`STARTED_NETWORK=1`]);
  } else {
    await appendState([`STARTED_NETWORK=0`]);
  }

  // 5) wait healthy
  let ok = false;
  for (let i = 0; i < 30; i++) {
    const { status, health } = await getContainerStatus(containerName);
    if (status === "running" && (health === "" || health === "healthy")) {
      ok = true;
      break;
    }
    if (status === "exited" || status === "dead") {
      console.error(`Error: PostgreSQL container '${containerName}' exited or failed to start (status: ${status}, health: ${health}).`);
      if (preStatus.status !== "running") {
        await runCapture(["docker", "rm", "-f", containerName]);
      }
      if (!wasRunning) {
        const ps = await runCapture(["docker", "ps", "-q"]);
        if (ps.stdout.trim().length === 0 && platform === "linux") {
          await runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
          await runCapture(["systemctl", "stop", "docker"]).catch(() => {});
        }
      }
      process.exit(1);
    }
    await Bun.sleep(1000);
  }
  if (!ok) {
    const { status, health } = await getContainerStatus(containerName);
    console.error(`Error: PostgreSQL container '${containerName}' did not become healthy in time (status: ${status}, health: ${health}).`);
    if (preStatus.status !== "running") {
      await runCapture(["docker", "rm", "-f", containerName]);
    }
    if (!wasRunning) {
      const ps = await runCapture(["docker", "ps", "-q"]);
      if (ps.stdout.trim().length === 0 && platform === "linux") {
        await runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
        await runCapture(["systemctl", "stop", "docker"]).catch(() => {});
      }
    }
    process.exit(1);
  }
  await appendState([`STARTED_POSTGRES=${preStatus.status !== "running" ? 1 : 0}`]);

  // 6) if app already running (for example started manually), reuse it and do not manage it here
  if (await checkServerHealth()) {
    console.log("Spring Boot server is already running; reusing existing instance.");
    await appendState([`STARTED_APP=0`]);
    return;
  }

  // 7) build and start app
  const build = await run(["gradle", "build", "bootJar"]);
  if (build.code !== 0) {
    console.error("Error: failed to build Spring Boot application (bootJar).");
    if (preStatus.status !== "running") {
      await runCapture(["docker", "rm", "-f", containerName]);
    }
    if (!preNet && (await networkExists(networkName))) {
      await runCapture(["docker", "network", "rm", networkName]);
    }
    if (!wasRunning) {
      const ps = await runCapture(["docker", "ps", "-q"]);
      if (ps.stdout.trim().length === 0 && platform === "linux") {
        await runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
        await runCapture(["systemctl", "stop", "docker"]).catch(() => {});
      }
    }
    process.exit(1);
  }

  // Start app in background and write PID
  const logHandle = await Bun.file(logFile).writer();
  const app = spawn(["mise", "x", "java", "--", "java", "-jar", "build/libs/spring-boot-app-1.0.0.jar"], {
    cwd: projectRoot,
    stdout: "pipe",
    stderr: "pipe",
  });
  app.stdout?.pipeTo(logHandle.writable, { preventClose: true }).catch(() => {});
  app.stderr?.pipeTo(logHandle.writable, { preventClose: true }).catch(() => {});
  await Bun.sleep(2000);
  // crude liveness check: process not exited in 2s
  if ((await Promise.race([app.exited, Bun.sleep(2000).then(() => -1)])) !== -1) {
    await logHandle.end();
    console.error(`Error: Spring Boot failed to start (process exited) — see ${logFile}`);
    if (preStatus.status !== "running") {
      await runCapture(["docker", "rm", "-f", containerName]);
    }
    if (!preNet && (await networkExists(networkName))) {
      await runCapture(["docker", "network", "rm", networkName]);
    }
    if (!wasRunning) {
      const ps = await runCapture(["docker", "ps", "-q"]);
      if (ps.stdout.trim().length === 0 && platform === "linux") {
        await runCapture(["systemctl", "--user", "stop", "docker"]).catch(() => {});
        await runCapture(["systemctl", "stop", "docker"]).catch(() => {});
      }
    }
    process.exit(1);
  }
  await writeFile(pidFile, String(app.pid));
  await appendState([`STARTED_APP=1`]);
  await logHandle.end();
  console.log(`Spring Boot started in background (PID ${app.pid}). Logs: ${logFile}`);
}

await main();
