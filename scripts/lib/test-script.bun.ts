import { ProcessRunner } from "./process-runner.bun.ts";
import { RunEnv } from "./run-env.bun.ts";
import { join } from "node:path";
import { StartScript } from "./start-script.bun.ts";
import { StopScript } from "./stop-script.bun.ts";

export class TestScript {
  private readonly runner = new ProcessRunner();

  async run(): Promise<void> {
    const brunoFlag = process.env.usage_bruno === "true";
    const fullFlag = process.env.usage_full === "true";
    let runGradle = true;
    let runBruno = false;

    if (brunoFlag && !fullFlag) {
      runGradle = false;
      runBruno = true;
    } else if (fullFlag) {
      runGradle = true;
      runBruno = true;
    }

    let started = false;
    const startScript = new StartScript();
    await startScript.run();
    started = true;

    let exitCode = 0;
    if (runGradle) {
      const gradle = await this.runner.runExitCode(["gradle", "test"]);
      if (gradle !== 0) {
        exitCode = gradle;
      }
    }

    if (runBruno) {
      const brunoCwd = join(RunEnv.projectRoot, "bruno");
      let brunoExit = 0;
      const brunoRunner = new ProcessRunner(brunoCwd);
      const trustExit = await brunoRunner.runExitCode(["mise", "trust", "mise.toml"]);
      if (trustExit !== 0) {
        console.error("Error: failed to trust bruno/mise.toml for mise. Bruno tests will be skipped.");
        brunoExit = trustExit;
      } else {
        brunoExit = await brunoRunner.runExitCode(["mise", "run", "test-bruno"]);
      }
      if (brunoExit !== 0) {
        exitCode = exitCode || brunoExit;
      }
    }

    if (started) {
      const stopScript = new StopScript();
      await stopScript.run().catch(() => undefined);
    }
    process.exit(exitCode);
  }
}
