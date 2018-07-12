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

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

/**
 * @author eulzbay
 */
public class TagExtension {
	private final Property<Boolean> enabled;

	public TagExtension(Project project) {
		enabled = project.getObjects().property(Boolean.class);
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}
}
