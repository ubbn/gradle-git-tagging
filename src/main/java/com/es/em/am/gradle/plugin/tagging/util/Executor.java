/*
 * Copyright (c) 2009-2018 Ericsson AB, Sweden. All rights reserved.
 *
 * The Copyright to the computer program(s) herein is the property of Ericsson AB, Sweden.
 * The program(s) may be used  and/or copied with the written permission from Ericsson AB
 * or in accordance with the terms and conditions stipulated in the agreement/contract under
 * which the program(s) have been supplied.
 *
 */
package com.es.em.am.gradle.plugin.tagging.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;

/**
 * @author eulzbay
 */
public final class Executor {
	static String reader(InputStream inputStream)
			throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			return bufferedReader.lines().collect(Collectors.joining());
		}
	}

	public static Result execute(String command) {
		Result result;
		try {
			Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
			int terminationCode = process.waitFor();
			String standardOutput = reader(process.getInputStream());
			String standardError = reader(process.getErrorStream());

			result = new Result(terminationCode, standardOutput, standardError);
		} catch (Exception e) {
			throw new GradleException("Failed to execute bash command: " + e.getMessage());
		}

		return result;
	}

}
