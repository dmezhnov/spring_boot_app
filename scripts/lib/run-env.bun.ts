import { dirname, join } from "node:path";

export class RunEnv {
  private static readonly libDir = dirname(new URL(import.meta.url).pathname);
  private static readonly scriptsDir = dirname(RunEnv.libDir);

  static readonly projectRoot = dirname(RunEnv.scriptsDir);
  static readonly runDir = join(RunEnv.projectRoot, "build", ".run");
  static readonly stateFile = join(RunEnv.runDir, "state.env");
  static readonly logFile = join(RunEnv.runDir, "app.log");
  static readonly pidFile = join(RunEnv.runDir, "app.pid");
}
