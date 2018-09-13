package org.bbn.gittag.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;

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
