#!/usr/bin/env bun

import { BuildScript } from "./lib/build-script.bun.ts";

async function main() {
  const script = new BuildScript();
  await script.run();
}

await main().catch((error) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error("Unexpected error while running build.");
  console.error(message);
  process.exit(1);
});
