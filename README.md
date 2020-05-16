## Loon Game Engine (Java Game Framework)

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
_________

License : http://www.apache.org/licenses/LICENSE-2.0

This Java implemented game framework(but will provide a cross-compiler contain C#,C/C++,Go,JavaScript or other programming language implemented),Support IOS\Android\WP7\PSM\Mac\Windows\Linux.