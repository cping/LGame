#Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

#2015-12-03

PS:正在全力开发C#版（也就是MonoGame和Unity3D的封装版），等2D部分稳定了再翻过头来写3D支持，另外增加了Live2D伪3D模型的全平台支持（移植于官方Android包，重写了渲染部分，完美支持所有live2d建模）

![LGame](https://raw.github.com/cping/LGame/master/live2dsupport.png "live2d_support")

关于LGame的0.5版（(临时目录为：Loon-0.5-Alpha)，开发中的重大改版，此版本有重大升级，以及引入3D模块。另外，此版本开始将不支持Java多线程交互(因为跨平台移植不方便……)，请不要在游戏中交互使用Thread，否则将导致游戏无法一键迁移到其他平台(在旧版中，也有个别示例游戏用了，所以这部分例子有待重写)）:

1、此版有两套核心库。一者为Java版核心库，以JavaSE、Robovm、J2Objc以及GWT等不同本地方式，分别实现核心库的本地方法，然后跨平台移植。而另一套，则为C#版核心库，走Xamarin(具体来说，其实是走MonoGame实现(因为基于MonoGame开发的游戏，将可以免费使用Xamarin，主要是为了混免费许可方便。另外，以前已经写过一个XNA版本的实现，不搞MonoGame版，代码就浪费了))、以及Unity（二次封装）分别实现核心库的本地方法，然后走代码转化工具，直接Java2C#跨平台。后续还会添加C++版本，以及其它语言的实现版本，毕竟Loon-0.5改版后核心库基本从本地环境脱离，只有算法，没有了具体的实现，分别在不同平台进行实现就可以完成跨平台开发（不过，需要GLSL脚本的支持，否则部分特效将没有办法实现，所以不能跑GLSL的就没办法了）。等上述版本稳定后，也会移植到C++环境去。

2、开始使用GLSL着色语言，因此不再支持OpenGL 1.0的API，转而采用OpenGL 2.0以及3.0，目前Loon-0.5底层渲染部分都已经替换完毕。

3、由于已经将核心库彻底脱离具体环境，使用新版的话需要把本地库和核心库共同引入lib才能运行（0.5以前的版本的核心代码，是有平台依赖的，系统调用和具体实现混在一起，所以移植起来很麻烦，这次彻底分开了）。

4、删除了原来部分过时API（或者说不适合多平台移植的），以及部分像素相关算法（凡是直接在界面显示中像素处理的，都删了，但留下了一些像素过滤类的算法），相关效果会使用GLSL着色语言实现（因为GLSL利用GPU处理更快，并且现成的特效也更多）。另外，此次改版后，将放弃已经无人使用的环境，因此jdk 1.7以下版本，以及Android3.0以下版本，将完全无法运行Loon-0.5以后的版本，并且为了向HTML5兼容，也删除了一些妨碍跨平台的的方法（具体来讲，少数调用可能变得不太直观，但整体来说变化不大），但以此为代价，将可以完美支持IOS和HTML5平台。

5、默认情况下，已经强制所有资源目录统一到assets，Loon-0.5开始默认将只从此路径下加载数据，把资源放入此目录下系统即可。

6、统一替换LImage为Image，LGraphics为Canvas，LTouch为GameTouch，LKey为GameKey，Touch为SysTouch，Key为SysKey，以及Screen需要的抽象实现函数也有所扩展，并将原来仿JavaSE的CPU渲染接口LImage和LGraphics，改为仿HTML5以及Android中Canvas标准的Image和Canvas.并且，LTexture和新修改后的Image，将不再允许自身的直接new，而只能走封装后的静态方法调用(这样构建时方便我做内部处理与判定，虽然不这样也可以处理，但是不方便统一接口……)。

7、Loon-0.5暂时测试不足，只能保证基本的API实现，但缺少足够多的测试，开发正在继续进行中，预计12月初可以满足JavaSE和Android的稳定使用，12月中旬可以满足GWT、Robovm这两个环境的稳定使用，1月满足Unity下的稳定使用，明年2月搞定Xamarin。(比最初预计的开发速度慢……)

8、目前alpha-0.5的GWT版本导出HTML5游戏，暂定分为两个模式:

一是普通模式（示例见html5demo文件，内有微型静态页面服务器，可以直接启动服务），此模式以常规方式导出数据为HTML5页面（也就是js脚本），虽然在理论上，GWT导出的数据完全可以通过网页静态访问，但是，由于程序中大量使用XMLHttpRequest，默认情况下破不了Chrome和IE12之类的跨域文件访问限制（Firefox访问本地页面则无此限制），所以打包出的数据实际上无法脱离服务器运行，在没有服务器的场合仍然无法在本地直接运行(不过有其它环境版本在，其实完全本地运行意义不是太大)。

二是内部加载模式(示例见html5demo2(no-cross-domain))，在这个模式下，Loon程序会将所有数据完全打包到js中，然后本地js读取，因此没有任何跨域问题，支持webgl的浏览器就能正常读取，完全不必有服务器的存在，把js和html传给任何人他都能本机直接运行。但代价是js体积会过大，并且只能利用src方式加载数据，不利于轻量级发布和异步加载。

暂时来说，两模式的切换是走xml配置来决定的，需要手动修改些许参数，不过稍后小弟会增加开发工具，直接让用户选择导出方式，不必改变任何配置就能自行切换。

9、另外，还有一套开发中的，跨平台的游戏中货币支付与转换系统(底层依赖Ripple支付系统)，将在基本移植平台完成后，以扩展包的形式上线。（BTC暴涨，我依旧对XRP充满信心，继续装死不看价，坐等资产破千万……）
_________

#LGame (formal name : Loon)

A fast, simple & powerful game framework, powered by Java (also supports C# and C++).

Please see [XRPMoon.com](http://www.xrpmoon.com) for downloads, build and installation instructions and other documentation.

20140906:

LGame Project Restart,The game's just started.

<a href="https://ripple.com//send?to=rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp&name=cping&label=Thank you donate to LGame&amount=100/XRP&dt=20140906"><img src="https://raw.github.com/cping/LGame/master/rippledonate.png" alt="RippleDonate" /></a>

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

License : http://www.apache.org/licenses/LICENSE-2.0

#This Java implemented game framework(but will provide a cross-compiler contain C#,C/C++,Go,JavaScript or other programming language implemented),Support IOS\Android\WP7\PSM\Mac\Windows\Linux.

## 代碼貢獻者(拼音首字母排序，不分先後)

-   暗夜星辰 

-   鹏凌三千 [http://www.xrpmoon.com/blog](http://www.xrpmoon.com/blog)