package loon;

import java.util.HashMap;

import loon.core.graphics.opengl.GL;

public class LConfig {

	int blend = GL.MODE_NORMAL;
	
	boolean autofilterColor = true;

	boolean autofilterAll = false;

	int[] filterColors = null;

	String[] filterFiles = null;

	String[] filterkeywords = null;

	HashMap<String, String> contexts;
	
	public int getBlend(){
		return blend;
	}

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

	public boolean isAutoAllFilter() {
		return autofilterAll;
	}

}
