package org.bbn.gittag;

import org.bbn.gittag.util.Executor;
import org.bbn.gittag.util.Result;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.util.Optional;

public class Tag extends DefaultTask {
	@TaskAction
	void tag() {
		String gitExec = "git";
		Optional<String> gitDirProperty = getPropertyIgnoreCase("gitdir");
		if (gitDirProperty.isPresent()) {
			gitExec += " --git-dir " + getProject().property(gitDirProperty.get());
		}

		String version = getProject().getVersion().toString();
		if (!getPropertyIgnoreCase("allowsnapshot").isPresent() &&
				version.toLowerCase().contains("snapshot".toLowerCase())) {
			getLogger().quiet("Skipped git tagging as project version is snapshot: " + version +
					", force tagging with option -Pallowsnapshot");
			return;
		}

		String commandToGetBranch = String.format("%s rev-parse --abbrev-ref HEAD", gitExec);
		Result result = Executor.execute(commandToGetBranch);
		if (!result.isSuccessful()){
			throw new GradleException("Could not determine current branch: " +
					result.getError());
		}
		String branch = result.getOutput();

		String commandToCheckRemote = String.format("%s log origin/%s..%s | cat", gitExec, branch, branch);
		result = Executor.execute(commandToCheckRemote);
		if (!result.isSuccessful()) {
			throw new GradleException(String.format("Could not diff between origin/%s and %s: ", branch, branch) +
					result.getError());
		} else if(!result.getOutput().trim().isEmpty()) {
			throw new GradleException("Git local repository have unpushed commits: " +
					result.getOutput());
		}

		TagExtension extension = getProject().getExtensions().findByType(TagExtension.class);
		String tagPrefix = extension.getTagPrefix();
		String gitTagName = String.format("%s%s", tagPrefix, version);
		String commandToCreateTag = String.format("%s tag -a -m \"%s\" %s", gitExec, gitTagName, gitTagName);
		result = Executor.execute(commandToCreateTag);
		if (!result.isSuccessful()) {
			throw new GradleException("Could not tag: " + result.getError());
		}

		String commandToPushTag = String.format("%s push origin %s", gitExec, gitTagName);
		if (getPropertyIgnoreCase("noHostKeyCheck").isPresent()) {
			String skipHostKeyCheck = "GIT_SSH_COMMAND=\"ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no\"";
			commandToPushTag = String.format("%s %s", skipHostKeyCheck, commandToPushTag);
		}
		result = Executor.execute(commandToPushTag);
		if (!result.isSuccessful()) {
			throw new GradleException("Could not push tag: " + result.getError());
		}

		getLogger().quiet("Project repo is successfully tagged as: " + gitTagName);
	}

	private Optional<String> getPropertyIgnoreCase(String property) {
		return getProject()
				.getProperties()
				.entrySet()
				.stream()
				.map(x -> x.getKey())
				.filter(x -> x.equalsIgnoreCase(property))
				.findFirst();
	}
}
