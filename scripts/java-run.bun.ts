import { spawn } from "bun";
import path from "node:path";
import fs from "node:fs";

const projectRoot = path.resolve(import.meta.dir, "..");
const libsDir = path.join(projectRoot, "build", "libs");

function findBootJar(): string {
	const all = fs.readdirSync(libsDir).filter(f => f.endsWith(".jar"));
	// Prefer boot repackage (non-plain) jars
	const candidates = all.filter(f => !f.endsWith("-plain.jar"));
	if (candidates.length === 0) {
		throw new Error(`Boot jar not found in ${libsDir}. Run 'bootJar' first.`);
	}
	// Prefer spring-boot repackage jar
	const preferred = candidates.find(f => f.includes("spring-boot"));
	const file = preferred ?? candidates[0];
	return path.join(libsDir, file);
}

const jarPath = findBootJar();

const JAVA_EXE = "C:/Users/dmitr/.cursor/extensions/redhat.java-1.47.0-win32-x64/jre/21.0.8-win32-x86_64/bin/java.exe";
const proc = spawn([JAVA_EXE, "-jar", jarPath], {
	cwd: projectRoot,
	stdin: "inherit",
	stdout: "inherit",
	stderr: "inherit",
});
const code = await proc.exited;
process.exit(code);


