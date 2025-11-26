#!/usr/bin/env bun

import { StartScript } from "./lib/start-script.bun.ts";

async function main() {
  const script = new StartScript();
  await script.run();
}

await main().catch((error) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error("Unexpected error while starting Spring Boot app.");
  console.error(message);
  process.exit(1);
});
