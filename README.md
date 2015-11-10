#2015-10-23

关于LGame的0.5版（(临时目录为：Loon-0.5-Alpha)，开发中的重大改版）:

1、此版有两套核心库。一者为Java版核心库，以JavaSE、Robovm、J2Objc以及GWT等不同本地方式，分别实现核心库的本地方法，然后跨平台移植。而另一套，则为C#版核心库，走Xamarin、以及Unity（二次封装）分别实现核心库的本地方法，然后跨平台。后续可能还会添加其它语言的实现版本，因为Loon-0.5改版后核心库基本从本地环境脱离，只有算法，没有了具体的实现，分别在不同平台进行实现就可以完成跨平台开发（不过，需要GLSL脚本的支持，否则部分特效将没有办法实现，所以不能跑GLSL的就没办法了）。

2、因为开始使用GLSL着色语言，因此不再支持OpenGL 1.0的API，转而采用OpenGL 2.0以及3.0，目前Loon-0.5底层渲染部分都已经替换完毕。

3、由于已经将核心库彻底脱离具体环境，使用新版的话需要把本地库和核心库共同引入lib才能运行（0.5以前的版本的核心代码，是有平台依赖的，系统调用和具体实现混在一起，所以移植起来很麻烦，这次彻底分开了）。

4、删除了原来部分过时API（或者说不适合多平台移植的），以及部分像素相关算法（凡是直接在界面显示中像素处理的，都删了，但留下了一些像素过滤类的算法），相关效果会使用GLSL着色语言实现（因为GLSL利用GPU处理更快，并且现成的特效也更多）。另外，此次改版后，将放弃已经无人使用的环境，因此jdk 1.7以下版本，以及Android3.0以下版本，将完全无法运行Loon-0.5以后的版本，并且为了向HTML5兼容，也删除了一些妨碍跨平台的的方法（具体来讲，少数调用可能变得不太直观，但整体来说变化不大），但以此为代价，将可以完美支持IOS和HTML5平台。

5、默认情况下，已经强制所有资源目录统一到assets，Loon-0.5开始默认将只从此路径下加载数据，把资源放入此目录下系统即可。

6、统一替换LImage为Image，LGraphics为Canvas，LTouch为GameTouch，LKey为GameKey，Touch为SysTouch，Key为SysKey，以及Screen需要的抽象实现函数也有所扩展，并将原来仿JavaSE的CPU渲染接口LImage和LGraphics，改为仿HTML5以及Android中Canvas标准的Image和Canvas.并且，LTexture和新修改后的Image，将不再允许自身的直接new，而只能走封装后的静态方法调用(这样构建时方便我做内部处理与判定，虽然不这样也可以处理，但是不方便统一接口……)。

7、Loon-0.5暂时测试不足，只能保证基本的API实现，但缺少足够多的测试，开发正在继续进行中，预计11月初可以满足JavaSE和Android的稳定使用，11月中旬可以满足GWT、Robovm这两个环境的稳定使用，12月满足Unity下的稳定使用，明年1月搞定Xamarin。

8、目前alpha-0.5的GWT版本导出HTML5游戏，暂定分为两个模式:

一是普通模式（示例见html5demo文件，内有微型静态页面服务器，可以直接启动服务），此模式以常规方式导出数据为HTML5页面（也就是js脚本），虽然在理论上，GWT导出的数据完全可以通过网页静态访问，但是，由于程序中大量使用XMLHttpRequest，默认情况下破不了Chrome和IE12之类的跨域文件访问限制（Firefox访问本地页面则无此限制），所以打包出的数据实际上无法脱离服务器运行，在没有服务器的场合仍然无法在本地直接运行(不过有其它环境版本在，其实完全本地运行意义不是太大)。

二是内部加载模式(示例见html5demo2(no-cross-domain))，在这个模式下，Loon程序会将所有数据完全打包到js中，然后本地js读取，因此没有任何跨域问题，支持webgl的浏览器就能正常读取，完全不必有服务器的存在。但代价是js体积会过大，并且只能利用src方式加载数据，不利于轻量级发布和异步加载。

暂时来说，两模式的切换是走xml配置来决定的，需要手动修改些许参数，不过稍后小弟会增加开发工具，直接让用户选择导出方式，不必改变任何配置就能自行切换。
__________

关于GWT版在不同浏览器下的运行效果：

在正宗Chrome环境下，无论手机或桌面浏览器都正常，速度上几乎等于Android本地应用(除了无法jni，所有嘛，有些需要cpu的地方你懂的……)。

在360浏览器环境下，无论手机或桌面浏览器都正常，速度上几乎等于Android本地应用。

在QQ浏览器下，无论手机或桌面浏览器都正常，速度上几乎等于Android本地应用。

PS:但不推荐用QQ调试，因为手机上QQ浏览器会自动缓存你访问过的网站内容，而且释放不及时，调试页面时，他会先完整下载你的页面到本地以及它的服务器，再加载给你，实时调试很有可能不同步……

上述三个应该都是正经的Chromium内核，完美支持webgl的所有api，且无“负向优化”存在。

UC桌面正常，但在UC手机环境下，似乎有站点保护，在非受信任的网站下（我走192.168.x.x方式的内网测试）开不了webgl服务，而放在github.io上就能看到（肯定不是代码的事，因为一开始放在内网，构建GWT提供的WebGLRenderingContext对象就直接失败（此组件封装的是Canvas的3D对象生成），直接用不了webgl，根本进入不到主逻辑，放github.io服务器上就能启动，应该是有可信域名与不可信域名的差异）。

百度浏览器桌面正常，但手机版有些部分异常，应该是它对webgl的api支持不完整所致。恐怕不完整的移植了Chromium移动版，导致webgl支持半吊子，对付它我需要针对性修改下动态设置(也就是一些高级API不能用，做个low的api使用状态出来，这部分大约双十一过后2-3天改好……)。

Firefox桌面正常，手机版效果同样无误，但是卡成狗，就比猎豹强一点，严重怀疑它的WegGL在手机上是Canvas渲染模拟的，我100%肯定它的移动版没有获得本地GPU支持。

猎豹浏览器桌面版正常，手机版直接卡死，但是这货对某个合作的JS游戏引擎却兼容的很好，估计是有特殊的高速API支持（但肯定不是标准webgl），以及可能对部分webgl的api不支持，稍后我尝试走人家的API看看。
__________

综合来讲，把游戏html5化后，如果是给台式机用户看，随便他什么浏览器都可以，怎么玩都没事，但如果走手机，则应推荐用户走Chrome或360、QQ浏览器，同样随便什么版本都可以，如果自己可以建站，也可以走UC。其它的各种悲剧。我正在做低支持环境下的最小webgl使用接口，以及另一套canvas实现的api，用来对付不支持或者支持不佳的浏览器，不过理论上讲，canvas效率肯定比不了webgl（除非firefox那种伪webgl），所以目前依旧是走完整的Chromium内核浏览器为佳。(也就是所有浏览器都能跑，我肯定有办法做到(大约再过半个月吧，纯Canvas实现部分就出来了)，但要想都跑流畅，非完整Chromium内核的，基本没希望……)

9、另外，还有一套开发中的，跨平台的游戏中货币支付与转换系统(底层依赖Ripple支付系统)，将在基本移植平台完成后，以扩展包的形式上线。（BTC暴涨，我依旧对XRP充满信心，继续装死不看价，坐等资产破千万……）

_________

#LGame (formal name : Loon)

A fast, simple & powerful game framework, powered by Java.

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