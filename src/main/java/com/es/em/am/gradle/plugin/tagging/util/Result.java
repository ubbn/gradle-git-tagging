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

/**
 * @author eulzbay
 */
public final class Result {
	private String standardOutput;
	private String standardError;
	private int terminationCode;

	public Result(int terminationCode, String standardOutput, String standardError) {
		this.terminationCode = terminationCode;
		this.standardOutput = standardOutput;
		this.standardError = standardError;
	}

	public String getOutput() {
		return standardOutput;
	}

	public String getError() {
		return standardError;
	}

	public boolean isSuccessful(){
		return terminationCode == 0;
	}
}
