# Java(Android)游戏开发领域常用资源 (游戏素材，游戏资源，不定期更新:上次更新日期 2016-02-10)

### Java游戏开发框架及资源(仅包含还在更新的)

* jMonkeyEngine: 目前最完善的Java3D游戏开发引擎，也可以跨平台，不过非台式机效率有限…… [项目地址](https://github.com/jMonkeyEngine/jmonkeyengine/)
* libGDX: 支持2D/3D的Java跨平台框架。[项目地址](https://github.com/libgdx/libgdx/)
* LGame: 包括Java/C#/C++等多种语法版本，可能运行于全平台，并且支持2D/3D（含Live2D这类伪3D模型的导入）的游戏引擎。 [项目地址](https://github.com/cping/lgame/)
* playn: 2D的Java游戏引擎。 [项目地址](https://github.com/threerings/playn/)
* LWJGL: 目前最常用的，对OpenGL/CL/AL等渲染API进行本地封装的Java开发包。[官网](http://lwjgl.org/)
* JavaFX: Java官方目前最推荐的UI库，号称是Swing以及Applet的后继者，也可以用于游戏开发官方提供有Android环境的本地支持库，通过Robovm也可以支持IOS平台。[官网](http://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html)
* Scene Builder: 开发JavaFX应用的可视化布局工具。[下载地址](http://gluonhq.com/open-source/scene-builder/)

### Java的跨平台工具(仅包含还在更新的)

* Robovm: 基于LLVM开发，运行时转换字节码为本地机器码，并提供有完善的本地API支持，为IOS提供了完整的Java运行时环境，附带可视化IDE。 [官网](https://robovm.com/)
* Multi-OS Engine: 由Intel推出的，免费的Java跨平台工具（特意注明了： It’s free! ），使用AOT静态代码转换方式的Java跨平台工具（目前支持Android和IOS），现阶段只有预览版，程序示例:https://github.com/moe-java-samples/moe-java-samples。另外，这玩意还有很强的跨平台补丁功能（而且不必改变原有Java文件，可以直接注明替换），比如LibGDXMissileCommand的示例中，演示了直接通过add_moe_support.patch打补丁方式，转换libgdx项目到它的IOS支持库。[官网](https://software.intel.com/en-us/multi-os-engine/)
* CodenameOne: 最初源自XMLVM的Java跨平台项目（XMLVM目前已经停止开发），原始版本的运行原理是转换Java代码为XML描述的中间语言，然后再转换为具体环境下的本地开发语言，并不具备具体平台的API支持功能。但是，CodenameOne为其增加了WP、Android以及IOS的本地环境API（不过提供的仅仅是CodenameOne自己的封装包，而不是完整的官方API，这点与Robovm差异很大），所以也就可以直接让程序运行于上述平台之上。[项目地址](https://github.com/codenameone/CodenameOne/)
* avian: 基于openjdk二次开发，Java运行时环境的轻量级二次封装库，据作者说未来将会完整支持IOS和Android平台(目前也可以跑IOS，但需要越狱……)。[项目地址](https://github.com/ReadyTalk/avian/)
* GWT: 经典的Java to JavaScript代码转换，以及页面开发工具，附带有较为完善的JS仿写Java的本地支持库，可以满足大多数【平台无关】的Java代码直接转换到浏览器环境。[项目地址](https://github.com/gwtproject/gwt/)
* TeaVM: 新一代的Java to JavaScript(更准确的说，是Bytecode to JavaScript)转换项目。相比GWT而言，TeaVM支持反射，支持对象的同步和异步，能在JS上模拟出真实的Thread功能，也不必配置gwt.xml描述java文件就可以编译，并且支持scala和kotlin脚本，更有比GWT更完整的Java类库实现，优化也比GWT做的更好（GWT是Java代码转译为JS，而TeaVM是直接把bytecode转译为JS，在浏览器运行时等于是面向过程的瀑布式运行，所以解释时损耗更少），通常teaVM转换的JS代码，在执行效率上，要比GWT转换的高10%左右。唯一的缺点是，还在开发阶段，不够成熟，所以bug比较多…… [项目地址](https://github.com/konsoletyper/teavm/)
* j2obj: 仿造GWT原理的Java to Objective-C代码转换器，在IOS上拥有较为完整的JRE本地支持，可以满足大多数【平台无关】的Java代码直接转换到IOS环境。[项目地址](https://github.com/google/j2objc/)
* openjdk: 完全开源的JDK（JRE）支持库，运行API上与Oracle版并无区别，但是实现上全部开源，也没有商用限制，也就是用户可以自行删减内容，方便精简出游戏专用JRE（而官方版在使用协议上是不能自行精简的）。 [完整编译版地址](https://github.com/alexkasko/openjdk-unofficial-builds/)

### Java的程序打包工具

* packr: 一个基于命令行的Java部署工具，可以把jar和jre混合到windows、mac以及linux平台的可执行文件中去。[官网](https://github.com/libgdx/packr/)
* izpack: 一个基于Java制作的跨平台部署工具，优点是跨平台，缺点是——这货的运行本身就基于Java，也就是没有jre无法运行…… [官网](http://izpack.org/)
* Launch4j: 一款支持多平台的Java应用部署工具，可以把jre和jar打包到相应平台的可执行程序中去，附带一提，这和名字近似的install4j不同，是完全免费的。 [官网](http://launch4j.sourceforge.net/)

### Java的脚本语言支持库(仅包含还在更新的)

* Scala: 融合了面向对象和函数式编程思想的静态类型编程语言，也是twitter使用的服务器开发语言之一。[官网](http://www.scala-lang.org/)
* Groovy: 类型可选（Optionally typed）的动态语言，支持静态类型和静态编译。目前是一个Apache孵化器项目。[官网](http://www.groovy-lang.org/)
* JRuby: Ruby的Java环境下运行库，包含有较为完整的Ruby语言支持。[项目地址](https://github.com/jruby/jruby/)
* luajava: Lua脚本的Java本地支持库。 [项目地址](https://github.com/jasonsantos/luajava/)
* Clojure: 可看做现代版Lisp的动态类型语言。[官网](http://clojure.org/)
* Ceylon: RedHat开发的面向对象静态类型编程语言。[官网](http://ceylon-lang.org/)
* Kotlin: JetBrain针对JVM、安卓和浏览器提供的静态类型编程语言。[官网](http://kotlinlang.org/)
* Xtend: 一种静态编程语言，能够将其代码转换为简洁高效的Java代码，并基于JVM运行。[官网](http://www.eclipse.org/xtend)

### 无版权的游戏资源下载（原作者已经放弃版权，或者声明可以随意使用的资源）

PS:使用下列免费资源时，建议注明引用的资源出处，免得引发不必要的版权官司（比如被人投诉说你抄袭资源之类，游戏介绍中注明了资源出处，直接声明使用的资源来源，省得麻烦）……

* opengameart: 一个欧美的完全无版权问题的游戏资源下载站。[地址](http://opengameart.org/) 
* 消失点: 日式AVG（galgame）以及RMXP和RMVX素材站，素材以背景图和地图为主。[地址](http://www.aj.undo.jp/)  
* k-after: 日式AVG（galgame）游戏的免费背景素材下载站。  [地址](http://k-after.at.webry.info/)  
* tokudaya: 日式AVG（galgame）游戏的免费人设下载站。 [地址](http://tokudaya.net/index.html) 
* nicovideo: 日本站点，这个站点各种图像和音频资源都有，内容很杂。 [地址](http://commons.nicovideo.jp/search/material/image)
* 俺得素材库: 日站，人物立绘。[地址](http://ichimedou.sakura.ne.jp/free/freemate.htm)
* 星宝转生: 日本站点，全图片资源公开的AVG（galgame），注明来源即可随意使用。 [地址](http://www.jewel-s.jp/)
* 月风: 日站，包含游戏图片以及音频的综合资源站。[地址](http://moonwind.pw/)
* 森の奥の隠れ里: 日本站点，以RMXP和RMVX素材为主，包括人脸图、行走图、以及图标等素材。[地址](http://fayforest.sakura.ne.jp/resource.html)
* stock.freem: 日站，包含游戏图片以及音频的综合资源站，资源有免费、个人开发、标准商用三种类型，免费的不太多。[地址](http://stock.freem.ne.jp/)
* 空彩: 日站，全部都是背景图资源。 [地址](http://loo.sakura.ne.jp/photo.html) 
* あひる小屋: 日站，提供RMXP和RMVX风格的图片资源，包括行走图以及立绘。[地址](http://momomohouse.moo.jp/) 
* 背景写真补完会：日站，全部是背景图。[地址](http://masato.ciao.jp/haikei/furemu.html)
* kenney: 一个欧美的无版权问题的游戏图片资源下载站。[地址](http://kenney.nl/) 
* nantoka: 一个日本的游戏图片资源（仓库）站，资源很杂。[地址](http://nantoka.main.jp/)
* BlueForest: 一个日本的游戏图片站，以脸图以及Q版立绘为主。[地址](http://blue-forest.sakura.ne.jp/index.html)
* gameart2d: 一个【版权问题不大】的游戏图片资源下载站，确实都是公开且免费的资源，但是它的资源【撞车率】较高，做同类游戏时很容易和人发生冲突…… [地址](http://www.gameart2d.com/) 
* members.jcom.home: 主要提供怪物类图片的日本资源站。 [地址](http://members.jcom.home.ne.jp/hide.mats/new_m.htm)
* 音人: 一个日本的游戏音频资源下载站（PS:该站协议要求在非营利场合使用，也就用它的资源开发游戏，不能直接收费）。 [地址](http://on-jin.com/)
* 炼狱庭院:  一个日本的游戏音频资源下载站。 [地址](http://www.rengoku-teien.com/index.html)
* 魔王魂:  一个日本的游戏音频资源下载站。（此站也有大量收费资源，注意看清链接类型，并不是所有资源都能下载的）  [地址](http://maoudamashii.jokersounds.com/)
* CANDY MUSIC!: 日本的音乐资源下载站。[地址](http://candy-music.fine.to/)
* indiegamemusic: 一个欧美的无版权问题的游音频下载站。[地址](http://www.indiegamemusic.com/) 
* openmusicarchive: 一个欧美的无版权问题的音频资源下载站。[地址](http://openmusicarchive.org/)
* freesound: 欧美的无版权问题的音频资源下载站。[地址](http://freesound.org/)
* hmix: 日本的音频资源站，非商用不受限制，商用的话则要求购买。[地址](http://www.hmix.net/)
* whitecafe: 日站，大触的blog，经常发点免费图片资源出来。[地址](http://whitecafe.sakura.ne.jp/)
* 欧美的在线RPG角色生成器。[地址](http://gaurav.munjal.us/Universal-LPC-Spritesheet-Character-Generator/#)

### 免费的开发辅助工具

* gimp: 经典的免费图像资源开发工具，基本可以替代PS使用（不想被人发现使用破解版PS时，可以用它充数……）。[地址](http://www.gimp.org/)
* live2d: 伪3D人物动态表情开发工具(基本上就是设计AVG游戏动态角色用的)。[地址](http://www.live2d.com/)
* mapeditor: 瓦片地图开发工具。[地址](http://mapeditor.org/)
* bfxr: 音频制作工具。[地址](http://www.bfxr.net/)
* audiotool: 音频制作工具。[地址](http://www.audiotool.com/)
* thumbnailator:纯Java实现的图像缩放处理库。[地址](https://github.com/coobird/thumbnailator)