package org.bbn.gittag;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TagPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		Tag tag = project.getTasks().create("tag", Tag.class);
		tag.setDescription("Tags a git repo with name composed of custom prefix and project version.");
		project.getExtensions().create("tag", TagExtension.class);
	}
}
