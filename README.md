## Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[EN](README.md) / [KR](README.kr.md)

[Free Game Resources Links](https://github.com/cping/LGame/blob/master/dev-res/README.md "Game Resources of Free")

[Download Loon Game Engine](https://github.com/cping/LGame/releases/tag/LGame-0.5-Beta-fix1 "Loon Game Engine")

Only Android-studio Template : androidstudio-template

All Java code Run Template : loon-gradle-template

('task run' call main methond , 'task dist' packager game to jar)

![LGame](https://raw.github.com/cping/LGame/master/gradle_test.png "gradle_test")

* 2019 year resurrection continues to update - 2019-03-08.

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
	        // Whether to display the basic debug data (memory, sprite, desktop components, etc.)
		setting.isDebug = true;
		// Whether to display log data to the form
		setting.isDisplayLog = false;
		// Whether to display the initial logo
		setting.isLogo = false;
		// The initial page logo
		setting.logoPath = "loon_logo.png";
		// Original size
		setting.width = 480;
		setting.height = 320;
		// Zoom to
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		// Set FPS
		setting.fps = 60;
		// Game Font
		setting.fontName = "Dialog";
		// App Name
		setting.appName = "test";
		// Whether to simulate touch screen events (only desktop is valid)
		setting.emulateTouch = false;
		/* Set the global font to BMFont */
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
### Create a LGame project

LGame comes with a file called LGameProjectMake.jar which is an executable UI and command line tool. You can simply execute the JAR file which will open the setup UI.


![LGame](https://raw.github.com/cping/LGame/master/install.png "install")

Built-in over 30 game example(Part screenshots):

![LGame](https://raw.github.com/cping/LGame/master/e0x.png "0")

![LGame](https://raw.github.com/cping/LGame/master/e1x.png "1")

![LGame](https://raw.github.com/cping/LGame/master/e2x.png "2")

![LGame](https://raw.github.com/cping/LGame/master/e3x.png "3")

![LGame](https://raw.github.com/cping/LGame/master/e4x.png "4")

![LGame](https://raw.github.com/cping/LGame/master/live2dsupport.png "live2d_support")

PS : If there is a dependency problem such as a Loon-method NoSuchMethodError, the best way is of course to reset the environment yourself to ensure that the relevant jars are recognized by the compiler. But if not, then there is a simple and feasible solution, which is to delete all loon related jars, and then directly copy the relevant source code to your running environment src directory, so no matter what environment, as long as you can run, They Never have a dependency problem...
_________

#### 关于LGame

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

PS : 由于Java/Android运行环境的逐渐碎片化，Gradle乃至Android系统本身也【越来越不能】保证运行时环境的绝对一致性(Gradle这货自己就是个坑，大家回头看看Gradle5.x能完美兼容Gradle2.x乃至4.x配置吗？)，某些原因下，可能会造成loon的相关jar导入失败(其实不光loon有，隔壁'LXXXXx'也有)，导致APK中缺少相关代码，从而阻碍程序正常运行(也就是运行时会出针对loon包的NoSuchMethodError之类依赖错误)。从本质上讲，这不是Loon的问题，而是环境配置的问题。

如果出现loon相关的NoSuchMethodError之类依赖问题，最好的方法当然是自行重新设置环境，保证相关jar能够被编译器识别到。但是如果不行，那么，还有一个简单可行的解决方案，那就是删掉所有loon相关jar，然后直接copy相关源码到您的运行环境src目录中，这样无论什么环境，只要能跑起来，都不会出现依赖问题了……
_________

License : http://www.apache.org/licenses/LICENSE-2.0

This Java implemented game framework(but will provide a cross-compiler contain C#,C/C++,Go,JavaScript or other programming language implemented),Support IOS\Android\WP7\PSM\Mac\Windows\Linux.