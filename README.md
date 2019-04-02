#Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

[免费的游戏素材可见此](https://github.com/cping/LGame/blob/master/dev-res/README.md "Game Source of Free")

*干眼症（角膜干燥综合症）基本控制住了（用了两年环孢素+各种中药-_-|||），2019年复活继续更新 - 2019-03-08。

*目前的0.5版已经初步可用，我会逐渐增加测试用例和文档，以后这引擎就不会再有大的结构变动了，然后我会据此改出C++(这个比较麻烦，需要多平台适配)，C#(基于MonoGame，Unity3D之类，反正底层都是Mono库，基于第三方架构开发是因为不用自己适配多平台)之类其它语法版本，当然核心还是Java，只是方便做Java版一键语法转换和平台迁移。顺便我会出一个API精简的"非OpenGL"Java版封装(其实就是把最古老的AWT版重写一次)，这个封装会基于Android和JavaScript的Canvas进行渲染(而不是像现在一样渲染和主窗口是默认绑定的)，方便用户把Loon功能嵌入一些非游戏应用中(比如给应用UI加个特效什么)，或者一些更轻度的小游戏开发，这个精简版本只会支持JavaSE，JavaFX，Android，JavaScript(HTML5)。

* 本引擎开发环境是JDK 1.8，设置的最低运行环境为JRE 1.7，请注意低版本JRE不能识别高版本编译的jar，如果直接使用jar（而非导入代码），而无法运行，请注意替换为高版本JRE，或者以源码编译为低版本类库。
_________

源自中國本土的Java遊戲引擎項目

International Entertainment Machines

#LGame

formal name : Loon

A fast, simple & powerful game framework, powered by Java (also supports C# and C++).

LGame Project Restart,The game's just started.

## Features
LGame(LoonGame) is a very cool and small game library designed to simplify the complex and shorten the tedious for beginners and veterans alike. With it, you can use the best aspects of OpenGL/OpenGLES in an easy and organized way optimized for game programming. It is built around the concept that beginners should be able to start with the basics and then move up into a more complex plane of development with the veterans, all on the same platform.

LGame puts all of its effort into keeping things short and simple. The initial setup of a game consists only of making a single class; then you are done. The interface is entirely documented for easy and fast learning, so once you are started, there is nothing between you and your killer game but coding and creativity.

LGame is built around the users wishes, so do not hesitate to suggest and critique!

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

#This Java implemented game framework(but will provide a cross-compiler contain C#,C/C++,Go,JavaScript or other programming language implemented),Support IOS\Android\WP7\PSM\Mac\Windows\Linux.