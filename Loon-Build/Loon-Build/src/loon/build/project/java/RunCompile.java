package loon.build.project.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import loon.build.sys.LSystem;
import loon.build.sys.Log;
import loon.build.tools.LanguageUtils;

public class RunCompile {

	private ProjectName prj;

	List<String> list;

	public RunCompile(ProjectName prj) {
		this.prj = prj;
	}

	public void setCmd(String executable) {
		list = new ArrayList<>();
		list.add(executable);
	}

	public void addArg(String s) {
		list.add(s);
	}

	public void addArg(String s1, String s2) {
		list.add(s1);
		list.add(s2);
	}

	public int execute() throws Exception {

		if (prj.projects.verbose) {

			StringBuilder outString = new StringBuilder();
			for (String s : list) {
				outString.append(s);
				outString.append(' ');

			}
			
			Log.log("" + prj.name + ":command: " + outString);
		}

		Process p = new ProcessBuilder().command(list).start();
		StreamOut errorGobbler = new StreamOut(p.getErrorStream(), "err");
		StreamOut outputGobbler = new StreamOut(p.getInputStream(), "out");
		outputGobbler.start();
		errorGobbler.start();
		return p.waitFor();
	}

	private class StreamOut extends Thread {
		InputStream is;
		String type;
		private PrintWriter out;

		private StreamOut(InputStream is, String type) {
			this.is = is;
			this.type = type;
			this.out = Log.getWriter();
		}

		@Override
		public void run() {
			try {
				if (LanguageUtils.isEast()) {
					InputStreamReader isr = new InputStreamReader(is, "GBK");
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						out.println(type + "> " + line);
					}
				} else {
					InputStreamReader isr = new InputStreamReader(is,
							LSystem.ENCODING);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						out.println(type + "> " + line);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
