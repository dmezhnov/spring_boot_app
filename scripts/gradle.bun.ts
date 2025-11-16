import { spawn } from "bun";
import path from "node:path";
import process from "node:process";

const args = process.argv.slice(2);
const projectRoot = path.resolve(import.meta.dir, "..");
const wrapperJar = path.join(projectRoot, "gradle", "wrapper", "gradle-wrapper.jar");

const command = [
	"java",
	"-Dorg.gradle.appname=gradlew",
	"-classpath",
	wrapperJar,
	"org.gradle.wrapper.GradleWrapperMain",
	...args,
];

const proc = spawn(command, {
	cwd: projectRoot,
	stdin: "inherit",
	stdout: "inherit",
	stderr: "inherit",
});

const exitCode = await proc.exited;
process.exit(exitCode);
