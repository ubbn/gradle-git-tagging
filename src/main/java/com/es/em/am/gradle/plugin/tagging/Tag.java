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
import org.gradle.api.tasks.TaskAction;

import com.es.em.am.gradle.plugin.tagging.command.Executor;
import com.es.em.am.gradle.plugin.tagging.command.Result;

/**
 * @author eulzbay
 */
public class Tag extends DefaultTask {
	@TaskAction
	void run() {
		System.out.println("Test");
		Result result = Executor.execute("ls");
		if (!result.isSuccessful()){
			throw new GradleException("Could not determine current branch: " +
					result.getError());
		}

		System.out.println(result.getOutput());
	}
}