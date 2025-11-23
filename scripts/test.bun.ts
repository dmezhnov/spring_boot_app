#!/usr/bin/env bun

import { spawn } from "bun";
import { dirname } from "node:path";

const projectRoot = dirname(new URL(import.meta.url).pathname).replace(/\/scripts$/, "");

async function run(cmd: string[], inherit = true): Promise<number> {
  const p = spawn(cmd, { cwd: projectRoot, stdout: inherit ? "inherit" : "pipe", stderr: inherit ? "inherit" : "pipe" });
  return p.exited;
}

async function main() {
  // Start
  let started = false;
  const startCode = await run(["mise", "x", "bun", "--", "bun", "run", "scripts/start.bun.ts"]);
  if (startCode !== 0) {
    process.exit(startCode);
  }
  started = true;

  // Run Gradle tests then Bruno tests
  let exitCode = 0;
  const gradle = await run(["gradle", "test"]);
  if (gradle !== 0) exitCode = gradle;
  const bruno = await run(["bash", "-lc", "cd bruno && mise run test-bruno"]);
  if (bruno !== 0) exitCode = exitCode || bruno;

  // Stop what we started
  if (started) {
    await run(["mise", "x", "bun", "--", "bun", "run", "scripts/stop.bun.ts"]).catch(() => {});
  }
  process.exit(exitCode);
}

await main();


