package org.bbn.gittag.util;

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
