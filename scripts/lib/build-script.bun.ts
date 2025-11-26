import { ProcessRunner } from "./process-runner.bun.ts";

export class BuildScript {
  private readonly runner = new ProcessRunner();

  async run(): Promise<void> {
    const testFlag = process.env.usage_test === "true";
    const fullFlag = process.env.usage_full === "true";

    let exitCode = 0;

    if (fullFlag) {
      exitCode = await this.runner.runExitCode(["gradle", "compileJava", "compileTestJava"]);
    } else if (testFlag) {
      exitCode = await this.runner.runExitCode(["gradle", "compileTestJava"]);
    } else {
      exitCode = await this.runner.runExitCode(["gradle", "compileJava"]);
    }

    if (exitCode !== 0) {
      process.exit(exitCode);
    }
  }
}
