![LGame Logo](https://raw.github.com/cping/LGame/master/loon_logo.svg)
## 🎮 Loon Game — A Java Game Framework (Java Game Engine)
![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**LGame** is a cross-platform java game engine(framework), offering comprehensive foundational modules for 2D games (with 3D support planned for future releases). It supports platforms including Windows, Linux, macOS, Android, web browsers, and iOS. Additionally, it provides native implementations in C# and C++ alongside syntax conversion to accommodate as many systems as possible.

[EN](README.md) / [KR](README.kr.md)

[Free Game Resources Links](https://github.com/cping/LGame/blob/master/dev-res/README.md "Game Resources of Free")

[Download Loon Game Engine](https://github.com/cping/LGame "Loon Game Engine")

Only Android-studio Template : androidstudio-template

All Java code Run Template : loon-gradle-template

('task run' call main methond , 'task dist' packager game to jar)

![LGame](https://raw.github.com/cping/LGame/master/gradle_test.png "gradle_test")

* 2019 year resurrection continues to update - 2019-03-08.

International Entertainment Machines

## Loon

![LGame](https://raw.github.com/cping/LGame/master/loon_framework.png "loonframework")

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

![LGame](https://raw.github.com/cping/LGame/master/e0x.png "0")

![LGame](https://raw.github.com/cping/LGame/master/e1x.png "1")

![LGame](https://raw.github.com/cping/LGame/master/e2x.jpg "2")

![LGame](https://raw.github.com/cping/LGame/master/sample.jpg "samplelist")
_________

#### 关于LGame

总之就是一个非常懒人化的2D全功能游戏库(标准版会增加3D支持)，基于OpenGL（OpenGLES）开发，有多平台适配，基本上可以一个jar满足绝大多数的2D游戏需求（暂时不含网络部分，准备有时间单开项目），目前仅以Java语法来说，算是很方便的2D游戏库了。

主版本3個，Java版(0.5版)基本构建完毕，C#版构建中，C++版构建中，理论上Java版是核心，以后会提供工具相互转化语法为其它版本，因为Java跨平台是天坑，多个语言多条路……

此外还有一个和标准版同API(略有精简)的纯Java环境lite版(Loon-Lite)，不使用任何第三方库，仅使用Java运行环境提供的图形接口（也就是只用Graphics和Canvas的API封装），目前只能跑在JavaFX、JavaSE(标准JDK)以及Android、GWT、TeaVM这些环境，主要是当作2D图形特效库以及UI库使用，不过基本游戏功能都有，纯血javaer专用，万一以后有人把GraalVM扩展出多平台的渲染库，也可能在更多平台运行。以后还可能有一个基于精简版的TypeScript版本。