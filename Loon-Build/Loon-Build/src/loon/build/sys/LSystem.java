package loon.build.sys;

import java.util.Random;

public class LSystem {

	final static public String PROJECT = "Loon-Build";

	final static public String FONT_NAME = "Dialog";

	final static public int FONT_TYPE = 1;

	final static public String ENCODING = "UTF-8";

	// 行分隔符
	final static public String LS = System.getProperty("line.separator", "\n");

	// 文件分割符
	final static public String FS = System.getProperty("file.separator", "/");

	final static public Random random = new Random();

	final static public int DEFAULT_MAX_CACHE_SIZE = 30;
}
