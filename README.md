## Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[免费的游戏素材可见此](https://github.com/cping/LGame/blob/master/dev-res/README.md "Game Source of Free")

单纯android-studio运行模板见:androidstudio-template

全java应用gradle运行模板见:loon-gradle-template
(模块的task run执行对应环境程序,task dist打包程序)

![LGame](https://raw.github.com/cping/LGame/master/gradle_test.png "gradle_test")

PS:本来eclipse下是可以直接生成项目的，不过前两年没更新，现在ADT已经作古了，需要花时间重写这部分，暂不提供……

*2019年复活继续更新 - 2019-03-08。

*LGame目前版本虽然基于标准OpenGLES API开发,但项目0.1版最早脱胎于AWT Java2D时代,所以采用了AWT Graphics2D的坐标系（画布左上角为原点(x=0,y=0),Y轴向下为正值,与JavaFx或Android,JavaScript的Canvas渲染坐标一致），渲染接口也都被保存下来，Graphics2D以及Android Canvas的大部分API可以在GLEx这一全局渲染类中直接使用，甚至连J2ME时代的游戏你都能无缝移植过来(有完整API支持)。

*LGame在设计上追求一切从简，能自己实现的，绝不依赖第三方类库(One jar)，所以无论是XML或Json解析，TMX地图构建，物理引擎，游戏脚本，抑或二维码构建，都有自己的完整实现（当然，肯定也允许使用第三方的），并且能自然和游戏组件绑定。所以学习难度低(很多事情后台都自动做了)，上手容易，堪称学习难度最低的Java游戏引擎，没有之一。

*支持多语言开发，LGame同时支持Java、C#、C++等多种语言，并且可以让Java语法向其它版本自动转化，能满足任意环境的语言需求。

*有完善的组件库支持，时间轴动画，缓动、UI系统、粒子动画、物理系统等并且针对不同游戏特性开发都能在引擎中得到满足，无论是ACT，RPG或者AVG(Galgame)游戏都可以直接套用现成组件构建，最简化你的代码和工作时间。

*目前的0.5版已经初步可用，引擎代码开源并托管到github，引擎本身及其素材全部免费使用，包括商用。

*关于项目进度 - 2019-04-20。

0.5版功能大体上就是目前这些，api和功能已经基本定型，再添加就是后续0.6版的事情，另外还有一个正在写的json布局器，通过json设置的方式构建组件与精灵之类，然后直接渲染到屏幕上，并借助脚本进行游戏操作，不过0.5版也就是添加基本功能，完善要等0.6版。目前0.5这个版本最主要的目标是跨平台移植，多语言支持以及查bug补bug，功能(API)的完善已经不在此版本计划中。

等我把C#版和C++版按照Java重写一遍，然后借此再查几回bug，基本就可以发布0.5正式版了。

而0.5版后续的0.6版，主要会围绕三个方面展开：

1：多语言，多平台，多机种适配，主要是搞各大游戏机。

2：json布局器结合ioc控制反转，让游戏逻辑与变量，功能视图化，借此拖拽式生成游戏，以及可视化编辑器的开发。

2：还有就是一些效果上的优化，例如减少内存占用，更多的音频和图像格式支持，构建Shader效果库，为精灵加入脏矩渲染之类，也都是后面的事情，不可能一个版本就完成所有事情，总要分个主次的。
_________

源自中國本土的Java遊戲引擎項目

International Entertainment Machines

## Loon
formal name : Loon

A fast, simple & powerful game framework, powered by Java (also supports C# and C++).

LGame Project Restart,The game's just started.

## Features
LGame(LoonGame) is a very cool and small game library designed to simplify the complex and shorten the tedious for beginners and veterans alike. With it, you can use the best aspects of OpenGL/OpenGLES in an easy and organized way optimized for game programming. It is built around the concept that beginners should be able to start with the basics and then move up into a more complex plane of development with the veterans, all on the same platform.

LGame puts all of its effort into keeping things short and simple. The initial setup of a game consists only of making a single class; then you are done. The interface is entirely documented for easy and fast learning, so once you are started, there is nothing between you and your killer game but coding and creativity.

LGame is built around the users wishes, so do not hesitate to suggest and critique!

### Games Code Samples

![LGame](https://raw.github.com/cping/LGame/master/sample.png "samples")

[Samples](https://github.com/cping/LGame/tree/master/Java/samples "Game Sample")

[Examples](https://github.com/cping/LGame/tree/master/Java/Examples "Game Example")

### Game Run the Example(JavaSE)
```java

package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class Main  {

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
		//帧率
		setting.fps = 60;
		//字体
		setting.fontName = "黑体";
		//应用名
		setting.appName = "test";
		//是否模拟触屏事件（仅桌面有效）
		setting.emulateTouch = false;
		/*
		 * 设置全局IFont字体为BMFont字体,fnt和png文件默认使用loon的jar中自带<br>
		 * (不填写时默认使用内置的LFont贴图，用户也可以自定义IFont字体)<br>*/
		//setting.setSystemGameFont(BMFont.getDefaultFont());
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new YourScreen();
			}
		});
	}
}
```
## Create a LGame project

LGame comes with a file called LGameProjectMake.jar which is an executable UI and command line tool. You can simply execute the JAR file which will open the setup UI.


![LGame](https://raw.github.com/cping/LGame/master/install.png "install")

Built-in over 30 game example(Part screenshots):

![LGame](https://raw.github.com/cping/LGame/master/e0x.png "0")

![LGame](https://raw.github.com/cping/LGame/master/e1x.png "1")

![LGame](https://raw.github.com/cping/LGame/master/e2x.png "2")

![LGame](https://raw.github.com/cping/LGame/master/e3x.png "3")

![LGame](https://raw.github.com/cping/LGame/master/e4x.png "4")

![LGame](https://raw.github.com/cping/LGame/master/live2dsupport.png "live2d_support")

License : http://www.apache.org/licenses/LICENSE-2.0

This Java implemented game framework(but will provide a cross-compiler contain C#,C/C++,Go,JavaScript or other programming language implemented),Support IOS\Android\WP7\PSM\Mac\Windows\Linux.