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

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author eulzbay
 */
public class TagPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		Tag tag = project.getTasks().create("tag", Tag.class);
		tag.setDescription("Tags a git repo with name composed of custom prefix and project version.");
		project.getExtensions().create("tag", TagExtension.class);
	}
}
