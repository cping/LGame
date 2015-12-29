package loon.build.project.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loon.build.tools.ArrayMap;

public class Projects {
	
	public Map<String, ProjectItem> maps;
	public String sourceDir = "";
	public boolean multithread = true;
	public String javaHome;
	public boolean verbose;
	public int totalJavac;
	public int totalCopy;
	public int totalJava;
	public int totalJar;
	public long totalCopys;

	public Projects() {
		maps = new HashMap<String, ProjectItem>();
	}

	@SuppressWarnings("unchecked")
	public void addPrjs(List<Object> prjs) {
		for (int i = 0; i < prjs.size(); i++) {
			if (!(prjs.get(i) instanceof List)) {
				continue;
			}
			List<Object> list = (List<Object>) prjs.get(i);
			ProjectItem prj = new ProjectItem();
			maps.put((String) list.get(0), prj);
			prj.name = (String) list.get(0);
			prj.dir = (String) list.get(1);
			if (list.size() >= 3) {
				ArrayMap m = (ArrayMap) list.get(2);
				prj.depends = (List<Object>) m.get("dep");
				prj.cp = (List<Object>) m.get("cp");
				prj.jars = (List<Object>) m.get("jars");
				prj.classpaths = (List<Object>)m.get("classpath");
				prj.manifests = (List<Object>) m.get("manifests");
				prj.mainClass = (String) m.get("main");
				prj.run = (List<Object>) m.get("run");
	
			}
		}
	}
}
