#!/usr/bin/env bun

import { spawn } from "bun";
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
  // Start Spring Boot server
  console.log("Starting Spring Boot server...");
  serverProcess = spawn(["mvn", "spring-boot:run"], {
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
  process.chdir("bruno");
  const testProcess = spawn(["bunx", "bru", "run"], {
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
