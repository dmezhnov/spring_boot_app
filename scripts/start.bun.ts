import { spawn } from "bun";
import path from "node:path";

const projectRoot = path.resolve(import.meta.dir, "..");

// {
// 	const build = spawn(["bun", "run", "scripts/gradle.bun.ts", "bootJar"], {
// 		cwd: projectRoot,
// 		stdin: "inherit",
// 		stdout: "inherit",
// 		stderr: "inherit",
// 	});
// 	const code = await build.exited;
// 	if (code !== 0) process.exit(code);
// }

{
	const run = spawn(["bun", "run", "scripts/java-run.bun.ts"], {
		cwd: projectRoot,
		stdin: "inherit",
		stdout: "inherit",
		stderr: "inherit",
	});
	const code = await run.exited;
	process.exit(code);
}
