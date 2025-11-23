const $ = Bun.$;

const DRAFT_BRANCH_NAME = "draft";

export class SaveScript {
  async run(): Promise<void> {
    await this.ensureInsideGitRepo();

    const remoteName = await this.resolveRemoteName();

    await this.ensureDraftBranchExists();
    await this.checkoutDraftBranch();
    await this.stageAllChanges();

    const committed = await this.commitIfNeeded();

    await this.pushDraftBranch(remoteName);

    if (!committed) {
      console.log('Draft branch has been pushed (no new commit was necessary).');
    }
  }

  private async ensureInsideGitRepo(): Promise<void> {
    const result = await $`git rev-parse --is-inside-work-tree`.nothrow();
    if (result.exitCode !== 0) {
      throw new Error("Current directory is not inside a Git repository.");
    }
  }

  private async resolveRemoteName(): Promise<string> {
    const output = await $`git remote`.text();
    const remotes = output
      .split("\n")
      .map((r) => r.trim())
      .filter(Boolean);

    if (remotes.length === 0) {
      throw new Error('No Git remotes configured. Please add a remote (for example, "origin").');
    }

    if (remotes.includes("origin")) {
      return "origin";
    }

    if (remotes.length === 1) {
      return remotes[0]!;
    }

    throw new Error(
      `Multiple Git remotes detected (${remotes.join(
        ", ",
      )}). Please add "origin" or adjust the script to select a remote explicitly.`,
    );
  }

  private async ensureDraftBranchExists(): Promise<void> {
    const result = await $`git rev-parse --verify ${DRAFT_BRANCH_NAME}`.nothrow();
    if (result.exitCode === 0) {
      return;
    }
    await $`git branch ${DRAFT_BRANCH_NAME}`;
  }

  private async checkoutDraftBranch(): Promise<void> {
    const currentBranch = (await $`git rev-parse --abbrev-ref HEAD`.text()).trim();
    if (currentBranch === DRAFT_BRANCH_NAME) {
      return;
    }
    await $`git checkout ${DRAFT_BRANCH_NAME}`;
  }

  private formatDraftCommitMessage(now: Date): string {
    const pad = (value: number): string => value.toString().padStart(2, "0");

    const year = now.getFullYear();
    const month = pad(now.getMonth() + 1);
    const day = pad(now.getDate());
    const hours = pad(now.getHours());
    const minutes = pad(now.getMinutes());
    const seconds = pad(now.getSeconds());

    return `draft-${year}-${month}-${day}-${hours}-${minutes}-${seconds}`;
  }

  private async stageAllChanges(): Promise<void> {
    await $`git add -A`;
  }

  private async hasStagedChanges(): Promise<boolean> {
    const result = await $`git diff --cached --quiet`.nothrow();
    return result.exitCode !== 0;
  }

  private async commitIfNeeded(): Promise<boolean> {
    const hasChanges = await this.hasStagedChanges();
    if (!hasChanges) {
      console.log('No changes to commit on the "draft" branch.');
      return false;
    }

    const message = this.formatDraftCommitMessage(new Date());
    await $`git commit -m ${message}`;
    console.log(`Created commit with message: ${message}`);
    return true;
  }

  private async pushDraftBranch(remoteName: string): Promise<void> {
    await $`git push -u ${remoteName} ${DRAFT_BRANCH_NAME}`;
  }
}
