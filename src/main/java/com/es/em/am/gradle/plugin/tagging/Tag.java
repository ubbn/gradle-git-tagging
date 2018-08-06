/*
 * Copyright (c) 2009-2018 Ericsson AB, Sweden. All rights reserved.
 *
 * The Copyright to the computer program(s) herein is the property of Ericsson AB, Sweden.
 * The program(s) may be used  and/or copied with the written permission from Ericsson AB
 * or in accordance with the terms and conditions stipulated in the agreement/contract under
 * which the program(s) have been supplied.
 *
 */
package com.es.em.am.gradle.plugin.tagging;

import java.util.Optional;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import com.es.em.am.gradle.plugin.tagging.util.Executor;
import com.es.em.am.gradle.plugin.tagging.util.Result;


/**
 * @author eulzbay
 */
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
