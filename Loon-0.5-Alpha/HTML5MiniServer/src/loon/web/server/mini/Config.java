package loon.web.server.mini;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.Random;

public class Config {

	private File file;
	private int port;
	private Properties properties;
	private String webDir = "www";
	//一个唯一的随机值，避免重复启动
	private int key;

	public Config(String path) throws Exception {
		file = new File(path);
		read();
		save();
	}

	public Config(File file) throws Exception {
		this.file = file;
		read();
		save();
	}

	public void read() throws Exception {
		Properties properties = new Properties();
		properties.load(new FileReader(file));
		this.properties = properties;
		
		port = checkPort(properties.getProperty("port"));
		webDir = properties.getProperty("dir", "www");
		Random random = new Random();
		key = random.nextInt(Integer.MAX_VALUE);
		properties.setProperty("key", key + "");
	}
	

	private void save() throws Exception {
		properties.store(new FileWriter(file), "WebServer config file");
	}
	
	public int checkPort(String string) throws Exception {
		int port = Integer.parseInt(string);
		if (port < 1 && port > 65535) {
			throw new ConfigException("Port config out range.");
		}
		return port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int p) {
		this.port = p;
	}
	
	public int getKey() {
		return key;
	}

	public String getWebDir() {
		return webDir;
	}

	public void setWebDir(String dir) {
		this.webDir = dir;
	}
}
