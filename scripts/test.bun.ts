#!/usr/bin/env bun

import { TestScript } from "./lib/test-script.bun.ts";

async function main() {
  const script = new TestScript();
  await script.run();
}

await main().catch((error) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error("Unexpected error while running tests.");
  console.error(message);
  process.exit(1);
});
