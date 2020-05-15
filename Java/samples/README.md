BaseSample(executable).jar 文件使用java1.8编译

如果在java9或以上版本无法运行(比如java14什么的),请自行替换jdk或者重新编译java文件.(因为java9是一个分界线,9以上对旧版开始兼容性变差,一些旧版打包jar的方式可能不兼容于java9及以上版本,所以直接跑loon源码在java14是没问题的,但是javase打包时用的一些反射配置方式却不通用了（简单说就是loon打包javase用的多jar反射加载方式在java9以上环境可能有问题，最直接的解决方式是把源码编译后打包到一个jar里，或者通过MANIFEST.MF设置，把其它jar放在运行时jar外面让jvm加载class，例如……）)

MANIFEST.MF:

Manifest-Version: 1.0
Class-Path: . libs/lwjgl.jar libs/loon-core-0.5.jar libs/loon-javase-0.5.jar libs/loon-node-0.5.jar libs/loon-live2d.jar libs/loon-srpg.jar
Main-Class: org.test.SampleTest

总之，如果loon在java14不能运行，本质上是环境配置的事情，不是代码的事情……