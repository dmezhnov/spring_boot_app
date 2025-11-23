#!/usr/bin/env bun

import { SaveScript } from "./lib/save-script.bun.ts";

async function main(): Promise<void> {
  const script = new SaveScript();
  await script.run();
}

await main().catch((err) => {
  console.error(err);
  process.exitCode = 1;
});
