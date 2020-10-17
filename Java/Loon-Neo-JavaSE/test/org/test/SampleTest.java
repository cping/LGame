package org.test;

import java.util.ArrayList;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;
import loon.utils.TArray;

public class SampleTest  {


	public static void main(String[] args) {
		LSetting setting = new LSetting();
		//是否显示基础的debug数据(内存，精灵，桌面组件等使用情况)
		setting.isDebug = true;
		
		//是否显示log数据到窗体
		setting.isDisplayLog = false;
		//是否显示初始logo
		setting.isLogo = false;
		// 初始化页面用logo
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 缩放为
		setting.width_zoom = 640;
		setting.height_zoom = 480;
//		setting.emulateTouch = true;
		//帧率
		setting.fps = 60;
		//字体
		setting.fontName = "黑体";
		//应用名
		setting.appName = "test";
		//版本号
		setting.version = "0.1";
		//是否模拟触屏事件（仅桌面有效）
		setting.isSyncTween = false;
		/*
		 * 设置全局IFont字体为BMFont字体,fnt和png文件默认使用loon的jar中自带<br>
		 * (不填写时默认使用内置的LFont贴图，用户也可以自定义IFont字体)<br>*/
		//setting.setSystemGameFont(BMFont.getDefaultFont());
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				//此Screen位于sample文件夹下，引入相关资源即可加载
				return new MyAVGScreen();
			}
		});
	}
}
