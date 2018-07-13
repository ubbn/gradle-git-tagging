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

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import com.es.em.am.gradle.plugin.tagging.command.Executor;
import com.es.em.am.gradle.plugin.tagging.command.Result;


/**
 * @author eulzbay
 */
public class Tag extends DefaultTask {
	@TaskAction
	void act() {
		TagExtension extension = getProject().getExtensions().findByType(TagExtension.class);
		String tagPrefix = extension.getTagPrefix();

		if(!extension.isActive()){
			return;
		}

		Result result = Executor.execute("git rev-parse --abbrev-ref HEAD");
		if (!result.isSuccessful()){
			throw new GradleException("Could not determine current branch: " +
					result.getError());
		}

		String branch = result.getOutput();
		Project project = this.getProject();
		if (!project.getProperties().containsKey("version")) {
			throw new GradleException("Failed to determine branch name");
		}
		String version = project.getProperties().get("version").toString();
		String tagName = String.format("%s%s", tagPrefix, version);

		String cmdCheckRemote = String.format("git log origin/%s..%s | cat", branch, branch);
		result = Executor.execute(cmdCheckRemote);
		if (!result.isSuccessful()) {
			throw new GradleException(String.format("Could not diff between origin/%s and %s: ", branch, branch) +
					result.getError());
		}
		else if(!result.getOutput().trim().isEmpty()) {
			throw new GradleException("Git local repository have unpushed commits: " +
					result.getOutput());
		}

		String cmdCreateTag = String.format("git tag -a -m \"%s\" %s", tagName, tagName);
		result = Executor.execute(cmdCreateTag);
		if (!result.isSuccessful()) {
			throw new GradleException("Could not tag: " + result.getError());
		}

		String cmdPushTag = String.format("git push origin %s", tagName);
		result = Executor.execute(cmdPushTag);
		if (!result.isSuccessful()) {
			throw new GradleException("Could not push tag: " + result.getError());
		}
	}
}
