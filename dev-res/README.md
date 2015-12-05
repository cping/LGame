# Java游戏开发领域常用资源

### Java游戏开发框架及资源(仅包含还在更新的)

* jMonkeyEngine: 目前最完善的Java3D游戏开发引擎，也可以跨平台，不过非台式机效率有限…… [项目地址](https://github.com/jMonkeyEngine/jmonkeyengine/)
* libGDX: 支持2D/3D的Java跨平台框架。[项目地址](https://github.com/libgdx/libgdx/)
* LGame: 包括Java/C#/C++等多种语法版本，可能运行于全平台，并且支持2D/3D（含Live2D这类伪3D模型的导入）的游戏引擎。 [项目地址](https://github.com/cping/lgame/)
* playn: 2D的Java游戏引擎。 [项目地址](https://github.com/threerings/playn/)
* LWJGL: 目前最常用的，对OpenGL/CL/AL等渲染API进行本地封装的Java开发包。[官网](http://lwjgl.org/)
* JavaFX: Java官方目前最推荐的UI库，号称是Swing以及Applet的后继者，也可以用于游戏开发官方提供有Android环境的本地支持库，通过Robovm也可以支持IOS平台。[官网](http://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html)
* Scene Builder: 开发JavaFX应用的可视化布局工具。[官网](http://www.oracle.com/technetwork/java/javase/downloads/javafxscenebuilder-ino-2157684.html)

### Java的跨平台工具(仅包含还在更新的)

* Robovm: 基于LLVM开发，运行时转换字节码为本地机器码，并提供有完善的本地API支持，为IOS提供了完整的Java运行时环境，附带可视化IDE。 [官网](https://robovm.com/)
* CodenameOne: 最初源自XMLVM的Java跨平台项目（XMLVM目前已经停止开发），原始版本的运行原理是转换Java代码为XML描述的中间语言，然后再转换为具体环境下的本地开发语言，并不具备具体平台的API支持功能。但是，CodenameOne为其增加了WP、Android以及IOS的本地环境API（不过提供的仅仅是CodenameOne自己的封装包，而不是完整的官方API，这点与Robovm差异很大），所以也就可以直接让程序运行于上述平台之上。[项目地址](https://github.com/codenameone/CodenameOne/)
* avian: 基于openjdk二次开发，Java运行时环境的轻量级二次封装库，据作者说未来将会完整支持IOS和Android平台(目前也可以跑IOS，但需要越狱……)。[项目地址](https://github.com/ReadyTalk/avian/)
* GWT: 经典的Java to JavaScript代码转换，以及页面开发工具，附带有较为完善的JS仿写Java的本地支持库，可以满足大多数【平台无关】的Java代码直接转换到浏览器环境。[项目地址](https://github.com/gwtproject/gwt/)
* j2obj: 仿造GWT原理的Java to Objective-C代码转换器，在IOS上拥有较为完整的JRE本地支持，可以满足大多数【平台无关】的Java代码直接转换到IOS环境。[项目地址](https://github.com/google/j2objc/)

### Java的脚本语言支持库(仅包含还在更新的)

* Scala: 融合了面向对象和函数式编程思想的静态类型编程语言，也是twitter使用的服务器开发语言之一。[官网](http://www.scala-lang.org/)
* Groovy: 类型可选（Optionally typed）的动态语言，支持静态类型和静态编译。目前是一个Apache孵化器项目。[官网](http://www.groovy-lang.org/)
* JRuby: Ruby的Java环境下运行库，包含有较为完整的Ruby语言支持。[项目地址](https://github.com/jruby/jruby/)
* luajava: Lua脚本的Java本地支持库。 [项目地址](https://github.com/jasonsantos/luajava/)
* Clojure: 可看做现代版Lisp的动态类型语言。[官网](http://clojure.org/)
* Ceylon: RedHat开发的面向对象静态类型编程语言。[官网](http://ceylon-lang.org/)
* Kotlin: JetBrain针对JVM、安卓和浏览器提供的静态类型编程语言。[官网](http://kotlinlang.org/)
* Xtend: 一种静态编程语言，能够将其代码转换为简洁高效的Java代码，并基于JVM运行。[官网](http://www.eclipse.org/xtend)

### 无版权的游戏资源下载（原作者已经放弃版权，可以随意使用的资源）

* opengameart: 一个完全无版权问题的游戏资源下载站。[地址](http://opengameart.org/) 
* 在线RPG角色生成器。[地址](http://gaurav.munjal.us/Universal-LPC-Spritesheet-Character-Generator/#)