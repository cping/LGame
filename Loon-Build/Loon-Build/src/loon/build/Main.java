package loon.build;

import loon.build.project.java.JavaBuild;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args != null && args.length > 0) {
			String mode = args[0].trim().toLowerCase();
			if ("javase".equals(mode)) {
				JavaBuild.load(new String[] { args[1],
						"clean" });
			}
		}
	/*	JavaBuild.load(new String[] { ".",
		"clean" });*/
	}
}
