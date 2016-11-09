#Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

* 本引擎开发环境是JDK 1.8，设置的最低运行环境为JRE 1.7，请注意低版本JRE不能识别高版本编译的jar，如果直接使用jar（而非导入代码），而无法运行，请注意替换为高版本JRE，或者以源码编译为低版本类库。

* BTW : 前一阵病了(自2016年3月起)，严重干眼症+结膜炎，用了6个月环孢素滴眼液，干眼总算缓解了，11月开始继续垒码……

loon-0.5-java-beta下载（总共约50多MB，十几MB的源码及jar资源，四十多MB的游戏示例，暂时不正式releases，因为0.5还不完整，目前只有java部分，并且还在修改中）:

Dropbox:https://www.dropbox.com/s/46dfz2hqw1znpud/loon-Java-beta.7z?dl=0

360网盘:https://yunpan.cn/cxAejmHmsiynn  (访问密码 663d)

其它下载:http://xrpmoon.com/files/loon-Java-beta.7z

PS: 正在制作专用构建工具loon-build，现阶段还无法自动构建全部0.5版项目，beta阶段请根据文件中template自行修改。

附带一提，针对windows平台生成的游戏jar文件，可用exe4j或install4j之类打包为exe程序，不过只是暂时的。我准备重写GreenJVM，附带一个超小的loon专用openjdk，以后就可以直接一键构建本地exe了(我初步精简的openjdk，压缩后大约4MB多一点，也就是单纯运行一个Loon的hello world的话，exe有5MB多一点足够了)。

BWT：想自己开发游戏又找不到美工的网友，可以见dev-res目录，小弟已经列举了很多的开放资源，并且在不断更新（都是无版权问题的资源，要么就是作者放弃版权的，要么就是只要注明来源，就可以免费使用的）。

##Loon Game 0.5 ver 开发目标
关于LGame的0.5版，是一次重大改版，此版本有较大结构升级，并引入了3D模块。另外，此版本开始将不支持Java多线程交互(为了跨平台移植不方便，当然，主要是因为html5方面)，建议不要在游戏中交互使用Thread，否则将导致游戏无法一键迁移到其他平台(在旧版中，也有个别示例游戏用了，所以这部分例子有待重写，或者等到TeaVM的支持包发布后才能使用）,0.5版主要变化如下:

1、此版有两套核心库。一者为Java版核心库，以JavaSE、Robovm、J2Objc以及GWT等不同本地方式，分别实现核心库的本地方法，然后跨平台移植。而另一套，则为C#版核心库，走Xamarin(具体来说，其实是走MonoGame实现(因为基于MonoGame开发的游戏，将可以免费使用Xamarin，主要是为了混免费许可方便。另外，以前已经写过一个XNA版本的实现，不搞MonoGame版，代码就浪费了))、以及Unity（二次封装）分别实现核心库的本地方法，然后走代码转化工具，直接Java2C#跨平台。后续还会添加C++版本，以及其它语言的实现版本，毕竟Loon-0.5改版后核心库基本从本地环境脱离，只有算法，没有了具体的实现，分别在不同平台进行实现就可以完成跨平台开发（不过，需要GLSL脚本的支持，否则部分特效将没有办法实现，所以不能跑GLSL的就没办法了）。等上述版本稳定后，也会移植到C++环境去。

2、开始使用GLSL着色语言，因此不再支持OpenGL 1.0的API，转而采用OpenGL 2.0以及3.0，目前Loon-0.5底层渲染部分都已经替换完毕。

3、由于已经将核心库彻底脱离具体环境，使用新版的话需要把本地库和核心库共同引入lib才能运行（0.5以前的版本的核心代码，是有平台依赖的，系统调用和具体实现混在一起，所以移植起来很麻烦，这次彻底分开了）。

4、删除了原来部分过时API（或者说不适合多平台移植的），以及部分像素相关算法（凡是直接在界面显示中像素处理的，都删了，但留下了一些像素过滤类的算法），相关效果会使用GLSL着色语言实现（因为GLSL利用GPU处理更快，并且现成的特效也更多）。另外，此次改版后，将放弃已经无人使用的环境，因此jdk 1.7以下版本，以及Android3.0以下版本，将完全无法运行Loon-0.5以后的版本，并且为了向HTML5兼容，也删除了一些妨碍跨平台的的方法（具体来讲，少数调用可能变得不太直观，但整体来说变化不大），但以此为代价，将可以完美支持IOS和HTML5平台。

5、默认情况下，已经强制所有资源目录统一到assets，Loon-0.5开始默认将只从此路径下加载数据，把资源放入此目录下系统即可(另外也兼容旧版写法，填上assets路径也没问题)。

6、统一替换LImage为Image，LGraphics为Canvas，LTouch为GameTouch，LKey为GameKey，Touch为SysTouch，Key为SysKey，以及Screen需要的抽象实现函数也有所扩展，并将原来仿JavaSE的CPU渲染接口LImage和LGraphics，改为仿HTML5以及Android中Canvas标准的Image和Canvas.并且，LTexture和新修改后的Image，将不再允许自身的直接new，而只能走封装后的静态方法调用(这样构建时方便我做内部处理与判定，虽然不这样也可以处理，但是不方便统一接口……)。

7、目前loon-0.5的GWT版本导出HTML5游戏，分为两种模式:

一是普通模式，此模式以常规方式导出数据为HTML5页面（也就是js脚本），虽然在理论上，GWT导出的数据完全可以通过网页静态访问，但是，由于程序中大量使用XMLHttpRequest，默认情况下破不了Chrome和IE还有各种衍生品的跨域文件访问限制（Firefox访问本地页面则无此限制，其它浏览器纹理不能“脏”，所以无解）。所以此模式打包出的数据，实际上无法脱离服务器运行，在没有服务器的场合仍然无法在本地直接运行(不过有其它环境版本在，其实完全本地运行意义不是太大)。

二是内部加载模式(示例见html5demo2(no-cross-domain))，在这个模式下，Loon程序会将所有数据完全打包到js中，然后本地js读取，因此没有任何跨域问题，支持webgl的浏览器就能正常读取，完全不必有服务器的存在，把js和html传给任何人他都能本机直接运行。但代价是js体积会过大，并且只能利用src方式加载数据，不利于轻量级发布和异步加载。

暂时来说，两模式的切换是走xml配置来决定的，需要手动修改些许参数，不过稍后小弟会增加开发工具，直接让用户选择导出方式，不必改变任何配置就能自行切换。

8、关于Java版本到其它语法版本的自动转换：

这个代码转换工具不难做，无论是语法翻译，或者字节码翻译，都有现成的第三方工具，只要把对应语法版本的基础库开发完毕，然后整合一下第三方工具就可以实现。

但无论是走语法翻译，还是走ikvm字节码翻译，原则上都不推荐使用java标准类库。因为用ikvm完全转换Java代码的话，同时还需要用到ikvm的java支持库(也就是openjdk那些dll)，本来mono支持库就大，再加上ikvm的，打个hello world出来，打包后不多出20MB左右的支持库也基本没戏。走语法翻译打包大小上要好很多，也容易自动化精简代码，不过无法完整重现java支持库（只会实现部分常用java包，比如字符处理和集合处理部分）。

所以如果想最小化运行程序，最好只用LGame自带的类库，否则以后打包到某些平台时，大小可能难以控制，甚至无法转换程序到对应平台。

9、为什么C#部分不直接自己实现，而是要利用第三方框架做封装：

原因很简单，因为Windows手机部分默认不支持OpenGL，而是使用DirectX，如果完全自己写C#实现，渲染部分就需要自己重新写了。虽然可以比较容易的照抄MonoGame使用SharpDX在Windows环境的实现部分，不过那样不如直接用MonoGame以及Unity3D做封装省事，至少能利用现成的第三方框架，多跨几个平台，反正无论如何，目前java版的opengl实现也无法照抄过去，同时减少自己做不同平台适配的时间（-_-）。

PS:正在全力开发C#版（也就是MonoGame和Unity3D的封装版,等2D部分稳定了再翻过头来写3D支持,另外增加了Live2D伪3D模型的全平台支持(移植于官方Android包，重写了渲染部分，完美支持所有live2d建模)

另外，还有个新坑，准备加个NScripter的脚本实现（扩展包形式，不在核心jar中），以后NScripter或ONS的游戏，可以直接导入到Loon中来，作为自己的游戏使用了，能干什么用大家懂的（理论上讲，多大的游戏都可以搬来，不过HTML5之类环境中，太大的还是不建议，毕竟网页加载几G资源不现实）。

_________

#LGame (formal name : Loon)

A fast, simple & powerful game framework, powered by Java (also supports C# and C++).

Please see [XRPMoon.com](http://www.xrpmoon.com) for downloads, build and installation instructions and other documentation.

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