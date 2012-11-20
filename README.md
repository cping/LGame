#LGame (formal name : Loon)

#This Java implemented game framework(but will provide a cross-compiler contain C#、C/C++、Go、JavaScript or other programming language implemented)，Support IOS，Android，WP7，PSM，Mac，Windows，Linux.

License : http://www.apache.org/licenses/LICENSE-2.0

20121118:

原Android版LGameAndroid2DActivity类，更名为LGame，使用方式无任何修正，有疑问请参看examples文件夹下示例（取这个名字是为了同C#版匹配，和XNA继承Game类学的……）

原Action包下Event类，更名为ActionBind，并删除了Bind类（该类具备非特定的反射支持，自由度太大在C/C++环境不好实现，所以删了），原Bind效果可通过实现ActionBind接口完成。

本次修改后Android环境armeabi和armeabi-v7a下文件，JavaSE环境lplus和lplus64文件有修改。也请注意更新，否则运行会出现异常（LGame尽可能以纯Java语法实现，相关本地类库删除后也可以正常运行，然而误用不匹配的类库则无法运行，因为调用本地API时会出错）

本次修改删除了Android版中许多非必要的代码（和游戏逻辑无关部分），稳定性绝对能够保证（如果MTC和iTestin等测试工具靠谱的话——今天MTC人品爆发都跑出99%通过了（有一台未启动））。话说Android碎片化的结果，零散设置越多崩的几率越高，把针对Android环境的额外配置全删后稳定多了……

PS：有网友提到4.x环境的状态栏无法消除，但小弟上次更新确实设置了setSystemUiVisibility，理论上讲不可能出现这种问题。而小弟刚刚（20121118）做的MTC测试也支持这一推论，除数字手机右侧有特殊按钮组外(具体可参看附带截图，数字这货的按钮栏特殊存在，似乎不是setSystemUiVisibility能控制的，强删View我又怕有其它后遗症……)，其余4.x版本StatusBar状态栏皆已被setSystemUiVisibility函数隐藏。建议检查lib是否为最近更新的版本，或直接以此版替代。

![image](https://raw.github.com/cping/LGame/4.xbar.png "image")

再者，由于Android SDK更新比较频繁（目前最新4.2），我使用的ADT环境也经常会进行升级，如果您的Eclipse载入示例工程出现问题，请更新到新版ADT即可解决。

另：

JavaScript，C/C++，C#（增加了PSM环境，另外把MonoTouch和Mono for Android的支持加上了，此包中C#旧版已废弃，勿再使用）版确实已经写完，做好Java的语法迁移工具小弟就会上传，代码量大勿急。

20121026:

今天看到有网友提到back键退出提示有问题，刚刚已经修正，在此鸣谢告知小弟问题的网友。另外一提，当设置LSystem.isBackLocked = true时，可以让back键退出无效。

这是一次常规更新，在API上基本没有变动，唯一例外是我将重力感应部分从Screen分离，封装到了单独的LAccelerometer类。该类是重力感应的基本功能封装，可以自动获得手机重感xyz坐标，屏幕朝向（8方向与4方向），手机顺时针旋转角度等数据，内部也提供有监听器。

同时经过MTC和iTestin多机型测试后，小弟对部分易出错区域进行了算法调整，稳定性有所提高（跑分的话成绩已经不错了）。

但有一个会影响操作的算法变更，某种情况下可能会增加出错几率。

那就是，小弟将Screen中onLoad函数的数据都变成了异步加载（任何情况下都是异步），而最早的设定是首个Screen中onLoad函数会同步加载（实际上最初这个设定很脑残，纯粹为了调试省事-_-）。

所以，目前在Screen（或任意Screen衍生类）操作中请注意通过isOnLoadComplete函数判定onLoad加载是否已经完成。如果在Onload没有完成时使用其中加载的数据，则肯定会产生空指针(null)异常。PS：这样做的目地是可以在数据加载完毕前进行其它操作，比如绘制进度条之类，而且同步加载数据较多时显示速度会很慢，长期黑屏影响效果，因此统一异步。

最简单的防异常处理方式，在可能会用到onLoad中加载内容的地方，加入下列判定即可：

if(!isOnLoadComplete()){
  return;
}

所有Android版示例已经用新版替换，但考量到发布包大小，部分不适宜移植到Android环境的游戏（就是前几版JavaSE目录下的），暂时被删除。剩余下的空间会被正式发布时的新示例取代（除了在博客提到的两个比较大的示例外，又新增了一个切绳子的物理游戏）

另：

小弟十一前夕误删了C#版VS2010项目工程，手头没有备份，干脆开VS2012重来中，XNA已替换为MonoGame（这个，API都一样），反正旧版代码也挺乱的……

其实C++版通过iTestin测试目前发布已经没有问题，不过考虑到程序体积因素，我准备删除些没用的类和API，再耗两天吧（另外我给C#版开了个PSM分支，其实可以直接上MonoGame，不过能不用第三方类库小弟通常就不用，根据PSM的具体环境我还会增减些接口）。

话说假如某天MonoTouch，Mono for Android之类免费了，未来智能机领域很可能被Mono一统江山，因为这货不只能做游戏，应用也成。总体上看它跨平台能力虽然不如纯C，但综合比较下还是Mono的易用性和开发效率高，您用C/C++调一个跨平台APP，Mono做3个都够了。这样发展下去，迟早Mono将获得Java在90年代中期的地位，C#当道，哎。

最后，小弟额外准备“周边”中，正式发布时有惊喜。