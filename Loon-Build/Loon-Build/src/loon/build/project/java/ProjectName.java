package loon.build.project.java;

public class ProjectName {

	protected String name;
	protected Projects projects;

	public ProjectName(Projects prjs) {
		this.projects = prjs;
	}

	public void setName(String prjName) {
		name = prjName;
	}
}
