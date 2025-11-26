import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

export class RunEnv {
  private static readonly libFile = fileURLToPath(import.meta.url);
  private static readonly libDir = dirname(RunEnv.libFile);
  private static readonly scriptsDir = dirname(RunEnv.libDir);

  static readonly projectRoot = dirname(RunEnv.scriptsDir);
  static readonly runDir = join(RunEnv.projectRoot, "build", ".run");
  static readonly stateFile = join(RunEnv.runDir, "state.env");
  static readonly logFile = join(RunEnv.runDir, "app.log");
  static readonly pidFile = join(RunEnv.runDir, "app.pid");
}
