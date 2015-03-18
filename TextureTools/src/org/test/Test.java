package org.test;

import java.io.File;

import loon.texture.utils.DDSUtils;

public class Test {

	public static void main(String[] args) throws Exception {
		// 转换图片文件/文件夹为DDS文件（默认转为DXT1格式）
		DDSUtils.createImageToDDS(new File("D:\\png_test"));
		// 转换DDS文件/文件夹为PNG文件（默认为png）
		//DDSUtils.createDDSToImage(new File("D:\\png_test"));
	}
}
