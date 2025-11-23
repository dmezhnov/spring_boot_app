import { spawn } from "bun";
import { RunEnv } from "./run-env.bun.ts";

export type RunResult = { code: number; stdout: string; stderr: string };

export class ProcessRunner {
  constructor(private readonly cwd: string = RunEnv.projectRoot) {}

  async run(cmd: string[], inherit = true): Promise<RunResult> {
    try {
      const proc = spawn(cmd, {
        cwd: this.cwd,
        stdout: inherit ? "inherit" : "pipe",
        stderr: inherit ? "inherit" : "pipe",
      });
      let stdout: Uint8Array = new Uint8Array();
      let stderr: Uint8Array = new Uint8Array();
      if (!inherit) {
        const outBuf = proc.stdout ? await new Response(proc.stdout).arrayBuffer() : new ArrayBuffer(0);
        const errBuf = proc.stderr ? await new Response(proc.stderr).arrayBuffer() : new ArrayBuffer(0);
        stdout = new Uint8Array(outBuf);
        stderr = new Uint8Array(errBuf);
      }
      const code = await proc.exited;
      return {
        code,
        stdout: inherit ? "" : new TextDecoder().decode(stdout),
        stderr: inherit ? "" : new TextDecoder().decode(stderr),
      };
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      console.error(`Failed to run command: ${cmd.join(" ")}`);
      console.error(message);
      return {
        code: -1,
        stdout: "",
        stderr: message,
      };
    }
  }

  async runCapture(cmd: string[]): Promise<RunResult> {
    return this.run(cmd, false);
  }

  async runExitCode(cmd: string[], inherit = true): Promise<number> {
    const result = await this.run(cmd, inherit);
    return result.code;
  }
}
