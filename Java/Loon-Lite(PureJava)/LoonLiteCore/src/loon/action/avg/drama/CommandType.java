/**
 * Copyright 2008 - 2009
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.avg.drama;

/**
 * 针对AVGScreen的游戏脚本命令，具体可以结合AVG示例使用
 */
public interface CommandType {

	// 增加选项按钮
	// 用法1:
	// 创建选项,字符显示"跳转一下",选项x=25,y=25,width=100,height=100,选中后跳转向标签label1(内部调用goto指令)
	// 写法:opt "跳转一下" 25,25,100,100 {label1}
	// 用法2:
	// 创建选项,字符显示"跳转一下",选项x=25,y=25,width=100,height=100,选中后跳转向标签label1,按钮图使用assets/background.png
	// (最多可以注入三张按钮图，分别为鼠标（或触屏）空闲时图，徘徊时图，以及按下时图，以','分割即可)
	// 写法:opt "跳转一下" 25,25,100,100 {label1,assets/background.png}
	// 写法:opt "跳转一下" 25,25,100,100
	// {label1,assets/background.png,assets/background1.png,assets/background2.png}
	// 用法3: 清除所有opt按钮
	// 写法:opt clear
	// 用法4: 清除带有指定标或者出现在指定行的按钮
	// 写法:opt clear 跳转一下
	// 写法:opt clear 11(删除第11行出现的选择按钮添加)
	String L_OPTION = "opt";

	// 清空屏幕
	String L_CLEAR = "clear";

	// 锁定AVGScreen的点击出发脚本功能，让点击无法触发脚本继续
	String L_LOCK = "lock";

	// 解除锁定AVGScreen的点击出发脚本功能，让脚本可以继续
	String L_UNLOCK = "unlock";

	// 画面等待
	String L_WAIT = "wait";

	// 运行任务(任务需要设定好后，手动注入AVGScreen才能生效)
	// 格式:task 任务名
	String L_TASK = "task";

	// 显示信息
	String L_MES = "mes";

	// 信息框移动
	String L_MESMOVE = "mesmove";

	// 信息框每行最多显示字数
	String L_MESLEN = "meslen";

	// 停止信息显示
	String L_MESTOP = "mestop";

	// 信息框中文字顶部偏移
	String L_MESSTOP = "messtop";

	// 信息框中文字左侧偏移(正负值皆可,将改变信息框中内容可显示范围，会影响自动换行)
	String L_MESLEFT = "mesleft";

	// 信息框中文字右侧偏移(正负值皆可,将改变信息框中内容可显示范围，会影响自动换行)
	String L_MESRIGHT = "mesright";

	// 改变信息框中内容偏移X轴(正负值皆可，单纯移动信息框中内容显示坐标，不会影响自动换行)
	String L_MESX = "mesx";

	// 改变信息框中文字偏移X轴(正负值皆可，单纯移动信息框中内容显示坐标，不会影响自动换行)
	String L_MESY = "mesy";

	// 信息框直接移动X轴(正负值皆可，此为移动信息框，而非信息框中内容坐标)
	String L_MES_BOX_X = "mesboxx";

	// 信息框直接移动Y轴(正负值皆可，此为移动信息框，而非信息框中内容坐标)
	String L_MES_BOX_Y = "mesboxy";

	// 信息框中文字颜色
	String L_MESCOLOR = "mescolor";

	// 选择方式1:格式[select "如果你想要跳转，那么？",{跳转到标记1,label1,跳转到标记2,label2}]
	String L_SELECT = "select";

	/**
	 * 选择方式2: selects in A.很帅。 B.超级帅。 C.帅到天下无帅。 out
	 */
	String L_SELECTS = "selects";

	// 选择框中文字顶部偏移
	String L_SELTOP = "seltop";
	// 选择框中文字左侧偏移
	String L_SELLEN = "selleft";

	// 文字显示速度模式:
	// 格式:speed fast
	// 有7个参数,SuperSlow, Slow,FewSlow, Normal, Fast,Quickly, Flash
	String L_SPEED = "speed";

	// 画面振动
	String L_SHAKE = "shake";

	// 暂停cg
	String L_CGWAIT = "cgwait";

	// cg延迟时间
	String L_SLEEP = "sleep";

	// 画面闪烁
	String L_FLASH = "flash";

	// 背景
	String L_GB = "gb";

	// 单独图片
	String L_CG = "cg";

	// 播放音乐
	String L_PLAY = "play";

	// 循环播放音乐
	String L_PLAYLOOP = "playloop";

	// 停止播放音乐
	String L_PLAYSTOP = "playstop";

	// 淡出
	String L_FADEOUT = "fadeout";

	// 淡入
	String L_FADEIN = "fadein";

	// 删除指定土图片
	String L_DEL = "del";

	// 雪
	String L_SNOW = "snow";

	// 雨
	String L_RAIN = "rain";

	// 花(体现风)
	String L_PETAL = "petal";

	// 雷
	String L_THUNDER = "thunder";

	// 停雪
	String L_SNOWSTOP = "snowstop";

	// 止雨
	String L_RAINSTOP = "rainstop";

	// 消花
	String L_PETALSTOP = "petalstop";

	// 宁雷
	String L_THUNDERSTOP = "thunderstop";

	// 移动到XXX
	String L_TO = "to";

	// 播放一个an动画文件
	String L_APLAY = "aplay";

	// 停止an文件播放
	String L_ASTOP = "astop";

	// 延迟an文件
	String L_ADELAY = "adelay";

	// 触发退出标记（无实际操作，只调用AVGScreen中同名函数）
	String L_EXIT = "exit";
}
