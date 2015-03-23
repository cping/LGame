package loon;

import java.util.HashMap;

public class LConfig {

	boolean autofilterColor = true;

	int[] filterColors = null;

	String[] filterFiles = null;

	String[] filterkeywords = null;
	
	HashMap<String, String> contexts;

	public String getItem(String name) {
		return contexts.get(name);
	}

	public int[] getColors() {
		return filterColors;
	}

	public String[] getFilterFiles() {
		return filterFiles;
	}

	public String[] getFilterkeywords() {
		return filterkeywords;
	}
	
	public boolean isAutoColorFilter() {
		return autofilterColor;
	}

}
