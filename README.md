#Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

*干眼症（角膜干燥综合症）基本控制住了（用了两年环孢素+各种中药-_-|||），2019年复活继续更新 - 2019-03-08。

*目前的0.5版已经初步可用，我会逐渐增加测试用例和文档，以后这引擎就不会再有大的结构变动了，然后我会据此出C++，C#之类其它语法版本，当然核心还是Java，只是方便做Java版一键语法转换和平台迁移。顺便我会出一个API精简的"非OpenGL"Java版封装(其实就是把最古老的AWT版重写一次)，这个封装会基于Android和JavaScript的Canvas进行渲染(而不是像现在一样渲染和主窗口是默认绑定的)，方便用户把Loon功能嵌入一些非游戏应用中(比如给应用UI加个特效什么)，或者一些更轻度的小游戏开发，这个精简版本只会支持JavaSE，JavaFX，Android，JavaScript(HTML5)。

* 本引擎开发环境是JDK 1.8，设置的最低运行环境为JRE 1.7，请注意低版本JRE不能识别高版本编译的jar，如果直接使用jar（而非导入代码），而无法运行，请注意替换为高版本JRE，或者以源码编译为低版本类库。
_________

#LGame (formal name : Loon)

A fast, simple & powerful game framework, powered by Java (also supports C# and C++).

LGame Project Restart,The game's just started.

源自中國本土的Java遊戲引擎項目

International Entertainment Machines

## Features
LGame(LoonGame) is a very cool and small game library designed to simplify the complex and shorten the tedious for beginners and veterans alike. With it, you can use the best aspects of OpenGL/OpenGLES in an easy and organized way optimized for game programming. It is built around the concept that beginners should be able to start with the basics and then move up into a more complex plane of development with the veterans, all on the same platform.

LGame puts all of its effort into keeping things short and simple. The initial setup of a game consists only of making a single class; then you are done. The interface is entirely documented for easy and fast learning, so once you are started, there is nothing between you and your killer game but coding and creativity.

LGame is built around the users wishes, so do not hesitate to suggest and critique!

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