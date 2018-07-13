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

/**
 * @author eulzbay
 */
public class TagExtension {
	private String tagPrefix = "v";
	private boolean active = true;

	public String getTagPrefix() {
		return tagPrefix;
	}

	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
