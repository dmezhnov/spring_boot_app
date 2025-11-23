#!/usr/bin/env bun

import { spawn } from "bun";
import { readFile, unlink } from "node:fs/promises";
import { platform } from "node:process";
import { join, dirname } from "node:path";

const projectRoot = dirname(new URL(import.meta.url).pathname).replace(/\/scripts$/, "");
const runDir = join(projectRoot, "build", ".run");
const stateFile = join(runDir, "state.env");
const pidFile = join(runDir, "app.pid");
const containerName = "spring-boot-app-postgres";
const networkName = "spring_boot_app_default";

async function run(cmd: string[], inherit = true) {
  const p = spawn(cmd, { cwd: projectRoot, stdout: inherit ? "inherit" : "pipe", stderr: inherit ? "inherit" : "pipe" });
  return p.exited;
}

async function readState(): Promise<Record<string, string>> {
  try {
    const raw = await readFile(stateFile, "utf-8");
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

async function killPid(pid: number) {
  if (platform === "win32") {
    await run(["taskkill", "/PID", String(pid), "/T", "/F"]).catch(() => {});
  } else {
    await run(["kill", String(pid)]).catch(() => {});
    for (let i = 0; i < 10; i++) {
      const check = spawn(["kill", "-0", String(pid)], { stdout: "ignore", stderr: "ignore" });
      const code = await check.exited;
      if (code !== 0) break;
      await Bun.sleep(1000);
    }
    // force
    await run(["kill", "-9", String(pid)]).catch(() => {});
  }
}

async function main() {
  const state = await readState();
  const startedApp = Number(state["STARTED_APP"] ?? 0) === 1;
  const startedPg = Number(state["STARTED_POSTGRES"] ?? 0) === 1;
  const startedNet = Number(state["STARTED_NETWORK"] ?? 0) === 1;
  const startedDocker = Number(state["STARTED_DOCKER"] ?? 0) === 1;

  // stop app
  if (startedApp) {
    try {
      const pidRaw = await readFile(pidFile, "utf-8");
      const pid = Number(pidRaw.trim());
      if (!Number.isNaN(pid)) {
        await killPid(pid);
      }
    } catch {}
    await unlink(pidFile).catch(() => {});
  }

  // remove container if we started it
  if (startedPg) {
    await run(["docker", "rm", "-f", containerName]).catch(() => {});
  }
  // remove network if created
  if (startedNet) {
    await run(["docker", "network", "rm", networkName]).catch(() => {});
  }

  // possible docker stop (cross-platform best-effort, only if we started it and nothing else runs)
  if (startedDocker) {
    const ps = spawn(["docker", "ps", "-q"], { stdout: "pipe", stderr: "ignore" });
    const buf = await ps.stdout!.arrayBuffer();
    const alive = new TextDecoder().decode(buf).trim().length > 0;
    if (!alive) {
      if (platform === "linux") {
        await run(["systemctl", "--user", "stop", "docker"]).catch(() => {});
        await run(["systemctl", "stop", "docker"]).catch(() => {});
      } else if (platform === "darwin") {
        // Try to quit Docker.app on macOS
        await run(["osascript", "-e", 'quit app "Docker"']).catch(() => {});
      } else if (platform === "win32") {
        // Try to stop Docker Desktop on Windows
        await run([
          "powershell",
          "-NoProfile",
          "-Command",
          "Get-Process 'Docker Desktop' -ErrorAction SilentlyContinue | Stop-Process -Force",
        ]).catch(() => {});
      }
    }
  }

  // cleanup state
  await unlink(stateFile).catch(() => {});
}

await main();
