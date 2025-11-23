#!/usr/bin/env bun

import { StopScript } from "./lib/stop-script.bun.ts";

async function main() {
  const script = new StopScript();
  await script.run();
}

await main().catch((error) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error("Unexpected error while stopping Spring Boot app environment.");
  console.error(message);
  process.exit(1);
});
