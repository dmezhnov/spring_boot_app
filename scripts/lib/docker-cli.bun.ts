import { platform } from "node:process";
import { access } from "node:fs/promises";
import { ProcessRunner } from "./process-runner.bun.ts";

export class DockerCli {
  private readonly runner: ProcessRunner;

  constructor(runner?: ProcessRunner) {
    this.runner = runner ?? new ProcessRunner();
  }

  async resolveDockerBinary(): Promise<string | null> {
    if (platform === "win32") {
      const viaWhere = await this.runner.runCapture(["where", "docker"]);
      if (viaWhere.code === 0 && viaWhere.stdout.trim().length > 0) {
        return "docker";
      }
      const defaultPath = "C:\\\\Program Files\\\\Docker\\\\Docker\\\\resources\\\\bin\\\\docker.exe";
      if (await this.exists(defaultPath)) {
        return defaultPath;
      }
      return null;
    }

    const which = await this.runner.runCapture(["which", "docker"]);
    if (which.code === 0 && which.stdout.trim().length > 0) {
      return "docker";
    }
    return null;
  }

  private async exists(path: string): Promise<boolean> {
    try {
      await access(path);
      return true;
    } catch {
      return false;
    }
  }
}


