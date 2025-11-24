import { ProcessRunner } from "./process-runner.bun.ts";

export class TestScript {
  private readonly runner = new ProcessRunner();

  async run(): Promise<void> {
    const runGradleEnv = process.env.TEST_RUN_GRADLE;
    const runBrunoEnv = process.env.TEST_RUN_BRUNO;

    const runGradle = runGradleEnv === undefined ? true : runGradleEnv === "true";
    const runBruno = runBrunoEnv === "true";

    let started = false;
    const startCode = await this.runner.runExitCode(["mise", "x", "bun", "--", "bun", "run", "scripts/start.bun.ts"]);
    if (startCode !== 0) {
      process.exit(startCode);
    }
    started = true;

    let exitCode = 0;
    if (runGradle) {
      const gradle = await this.runner.runExitCode(["gradle", "test"]);
      if (gradle !== 0) exitCode = gradle;
    }

    if (runBruno) {
      const bruno = await this.runner.runExitCode(["bash", "-lc", "cd bruno && mise run test-bruno"]);
      if (bruno !== 0) exitCode = exitCode || bruno;
    }

    if (started) {
      await this.runner
        .runExitCode(["mise", "x", "bun", "--", "bun", "run", "scripts/stop.bun.ts"])
        .catch(() => -1);
    }
    process.exit(exitCode);
  }
}
