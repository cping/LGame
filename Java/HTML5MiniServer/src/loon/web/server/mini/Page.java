package loon.web.server.mini;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Page {

	private String name;

	public static ArrayList<Page> pages = new ArrayList<Page>();

	public static void loadPages() {
		pages.add(new Bootstrap());
	}

	public Page(String name) {
		this.name = name;
	}

	public void called(Request request, Response response) throws IOException {
	}

	public String read(String fileName) throws IOException {
		File file = new File(fileName);
		FileReader reader = new FileReader(file);
		char[] text = new char[(int) file.length()];
		reader.read(text, 0, (int) file.length());
		reader.close();
		return new String(text);
	}

	public static List<Page> load(String directory) {
		List<Page> pages = new ArrayList<Page>();
		File dir = new File(directory);
		if (!dir.exists()) {
			return pages;
		}
		URLClassLoader loader;
		try {
			loader = new URLClassLoader(new URL[] { dir.toURI().toURL() }, Page.class.getClassLoader());
		} catch (MalformedURLException ex) {
			return pages;
		}
		for (File file : dir.listFiles()) {
			if (!file.getName().endsWith(".class")) {
				continue;
			}
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));

			try {
				Class<?> clazz = loader.loadClass(name);
				Object object = clazz.newInstance();
				if (!(object instanceof Page)) {
					continue;
				}
				Page page = (Page) object;
				pages.add(page);
			} catch (Exception ex) {
				ex.printStackTrace();
			} catch (Error ex) {
				ex.printStackTrace();
			}
		}
		try {
			loader.close();
		} catch (IOException e) {
		}
		return pages;
	}

	public static Page load(File file) {
		Page page = null;
		if (!file.exists()) {
			return null;
		}
		URLClassLoader loader;
		try {
			File folder = new File(file.getPath().substring(0, file.getPath().lastIndexOf("\\")));
			loader = new URLClassLoader(new URL[] { folder.toURI().toURL() }, Page.class.getClassLoader());
		} catch (MalformedURLException ex) {
			return null;
		}
		try {
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			Class<?> clazz = loader.loadClass(name);
			Object object = clazz.newInstance();

			if (!(object instanceof Page)) {
				try {
					loader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			page = (Page) object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			loader.close();
		} catch (IOException e) {
		}
		return page;
	}

	public Server getServer() {
		return Server.getServer();
	}

	public String getName() {
		return name;
	}

}
