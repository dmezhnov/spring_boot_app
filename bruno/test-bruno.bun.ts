#!/usr/bin/env bun

import { spawn } from "bun";
import { readdir } from "fs/promises";
import { setTimeout } from "timers/promises";

// Check if server is already running
let serverReady = false;
let serverProcess: ReturnType<typeof spawn> | null = null;
let serverWasRunning = false;

async function checkServerHealth(): Promise<boolean> {
  try {
    const response = await fetch("http://localhost:8080/api/users/health", {
      signal: AbortSignal.timeout(2000),
    });
    return response.status === 200;
  } catch {
    return false;
  }
}

async function waitForServer(maxAttempts: number = 30): Promise<boolean> {
  for (let attempt = 0; attempt < maxAttempts; attempt++) {
    if (await checkServerHealth()) {
      return true;
    }
    console.log(`Waiting for server... (${attempt + 1}/${maxAttempts})`);
    await setTimeout(2000);
  }
  return false;
}

// Check if server is already running
if (await checkServerHealth()) {
  serverReady = true;
  serverWasRunning = true;
  console.log("Server is already running!");
} else {
  // Build and start Spring Boot server only for tests
  console.log("Building Spring Boot application JAR...");
  const buildProcess = spawn(["gradle", "build", "bootJar"], {
    stdout: "inherit",
    stderr: "inherit",
    cwd: "..",
  });
  await buildProcess.exited;
  const buildExitCode = buildProcess.exitCode ?? 0;
  if (buildExitCode !== 0) {
    console.log("Build did not complete successfully");
    process.exit(buildExitCode);
  }

  // Resolve absolute path to the Spring Boot JAR dynamically (avoid hardcoding version)
  const libsDirUrl = new URL("../build/libs/", import.meta.url);
  const libsDir = libsDirUrl.pathname;
  let jarPath: string | undefined;
  try {
    const files = await readdir(libsDir);
    const jarName = files.find((f) => f.endsWith(".jar") && !f.endsWith("-plain.jar"));
    if (jarName) {
      jarPath = new URL(jarName, libsDirUrl).pathname;
    }
  } catch {
    jarPath = undefined;
  }
  if (!jarPath) {
    console.error(`Error: no runnable Spring Boot JAR found in ${libsDir}.`);
    process.exit(1);
  }

  console.log("Starting Spring Boot server...");
  serverProcess = spawn(["java", "-jar", jarPath], {
    stdout: "inherit",
    stderr: "inherit",
  });

  // Wait for server to be ready
  serverReady = await waitForServer();

  if (!serverReady) {
    console.log("Server failed to start within timeout");
    if (serverProcess) {
      serverProcess.kill();
    }
    process.exit(1);
  } else {
    console.log("Server is ready!");
  }
}

// Run Bruno tests
let testExitCode = 0;
try {
  console.log("Running Bruno tests...");
  process.chdir(".");
  const testProcess = spawn(["bun", "run", "test-bruno"], {
    stdout: "inherit",
    stderr: "inherit",
  });
  await testProcess.exited;
  testExitCode = testProcess.exitCode ?? 0;
} finally {
  if (!serverWasRunning && serverProcess) {
    console.log("Stopping server...");
    serverProcess.kill();
    await setTimeout(2000);
  } else {
    console.log("Server was already running, leaving it running...");
  }
}

process.exit(testExitCode);
