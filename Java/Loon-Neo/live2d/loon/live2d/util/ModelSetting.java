package loon.live2d.util;

import loon.utils.ListMap;

public interface ModelSetting {

	String getModelName();

	String getModelFile();

	int getTextureNum();

	String getTextureFile(int n);

	String[] getTextureFiles();

	int getHitAreasNum();

	String getHitAreaID(int n);

	String getHitAreaName(int n);

	String getPhysicsFile();

	String getPoseFile();

	int getExpressionNum();

	String getExpressionFile(int n);

	String[] getExpressionFiles();

	String getExpressionName(int n);

	String[] getExpressionNames();

	String[] getMotionGroupNames();

	int getMotionNum(String name);

	String getMotionFile(String name, int n);

	String getMotionSound(String name, int n);

	int getMotionFadeIn(String name, int n);

	int getMotionFadeOut(String name, int n);

	boolean getLayout(ListMap<String, Float> layout);

	int getInitParamNum();

	float getInitParamValue(int n);

	String getInitParamID(int n);

	int getInitPartsVisibleNum();

	float getInitPartsVisibleValue(int n);

	String getInitPartsVisibleID(int n);

	String[] getSoundPaths();
}
