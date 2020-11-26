using loon.font;
using loon.utils;

namespace loon
{
    public class LSetting
    {
		/// <summary>
		/// 是否支持临时系统字体(此项为true时,本地字体渲染不会直接建字典表,而是优先将drawString提交的信息完整渲染到屏幕)<br>
		/// , 只有当文字较多时才会切换到建立字典表查询。(两者区别在于,建表是"活字印刷",每次打印默认都是单字拼句子,100个<br>
		/// 字就是100次查字运算,而直接渲染就是提交什么显示什么,100个字1000个字都是一次运算,但是对系统资源占用比较多(因<br>
		/// 为是整句存储,大量重复的单字空间被浪费了))
		/// 
		/// 默认为false,如果字太多(默认为LFont生成的桌面环境图片超过1024x1024,其它环境超过512x512,设定太大的话系统无法预估LFont数量,<br>
		/// 担心用户反复构建新的文字渲染导致缓存图太多带崩程序),则删除所有缓存,转化为字典模式<br>
		/// 
		/// 是否开启应该根据文字显示的类型决定,如果是文字频繁变动的游戏(会反复创建和释放文字图纹理),开启后效果反而会变差,因为不能预料游戏类型,所以默认不开启.
		/// (HTML5环境由于js效率问题,建议开启)
		/// </summary>
		public bool supportTempSysFont = false;

		/// <summary>
		/// 若此项不为-1,则loon的Display类中LTimerContext在被传参时,以此数值替换动态计算出的paint刷新数值(也就是强制锁值),
		/// 默认单位是毫秒，比如锁定1/60帧就是(long)((1f/60f) * 1000)
		/// </summary>
		public long fixedPaintLoopTime = -1;

		/// <summary>
		/// 若此项不为-1,则loon的Display类中LTimerContext在被传参时,以此数值替换动态计算出的update刷新数值(也就是强制锁值),
		/// 默认单位是毫秒，比如锁定1/60帧就是(long)((1f/60f) * 1000)
		/// </summary>
		public long fixedUpdateLoopTime = -1;

		/// <summary>
		/// 默认游戏字体设置
		/// </summary>
		public IFont defaultGameFont;

		/// <summary>
		/// 默认log字体设置
		/// </summary>
		public IFont defaultLogFont;

		/// <summary>
		/// loon自带图的存放路径和文件前缀(默认assets起,不用写)
		/// </summary>
		public string systemImgPath = "loon_";

		/// <summary>
		/// Loon自带的模拟按键的缩放比率(Screen实现EmulatorListener接口自动出现,8个按钮)
		/// </summary>
		public float emulatorScale = 1f;

		/// <summary>
		/// 如果此项为true,则Loon会检查resize缩放行为,原本宽高比例是横屏，改成竖屏，或者竖屏改成横屏的resize将不被允许
		/// </summary>
		public bool isCheckReisze = false;

		/// <summary>
		/// 如果此项为true,则Loon中的缓动动画会和图像渲染同步(为false时缓动刷新次数会比画面渲染次数少),true时缓动动画会更加流畅,
		/// <para>
		/// 但是缓动资源较多则可能延迟画面渲染(因为都卡在一起执行了)
		/// </para>
		/// </summary>
		public bool isSyncTween = false;

		/// <summary>
		/// 若此处true,则fps,memory以及sprite数量之类数据强制显示
		/// </summary>
		public bool isDebug = false;

		/// <summary>
		/// 是否显示viewlog,此项为true时,log信息也将同时打印到游戏窗体中
		/// </summary>
		public bool isDisplayLog = false;

		/// <summary>
		/// 是否显示consolelog,此项为false时,关闭所有后台log信息
		/// </summary>
		public bool isConsoleLog = true;

		/// <summary>
		/// 是否显示FPS帧率
		/// </summary>
		public bool isFPS = false;

		/// <summary>
		/// 是否显示游戏内存消耗
		/// </summary>
		public bool isMemory = false;

		/// <summary>
		/// 是否显示精灵与桌面组件数量
		/// </summary>
		public bool isSprites = false;

		/// <summary>
		/// 是否显示logo（替换logo使用logoPath指定地址）
		/// </summary>
		public bool isLogo = false;

		/// <summary>
		/// 生成系统默认的LFont时,是否使用剪切生成
		/// </summary>
		public bool useTrueFontClip = false;

		/// <summary>
		/// 帧率
		/// </summary>
		public int fps = 60;

		/// <summary>
		/// 游戏画面实际宽度
		/// </summary>
		public int width = 480;

		/// <summary>
		/// 游戏画面实际高度
		/// </summary>
		public int height = 320;

		/// <summary>
		/// 游戏画面缩放大小,假如原始画面大小480x320,下列项为640x480,则会拉伸画布,缩放到640x480显示（不需要则维持在-1即可）
		/// </summary>
		public int width_zoom = -1;

		public int height_zoom = -1;

		/// <summary>
		/// 是否全屏
		/// </summary>
		public bool fullscreen = false;

		/// <summary>
		/// 是否使用虚拟触屏按钮(针对非手机平台)
		/// </summary>
		public bool emulateTouch = false;

		/// <summary>
		/// 仅对JavaSE环境有效,不为-1时对应的按键触发后,窗体停止活动
		/// </summary>
		public int activationKey = -1;

		/// <summary>
		/// 仅对JavaSE环境有效,为true时后台强制转换所有Image为TYPE_INT_ARGB_PRE类型
		/// </summary>
		public bool convertImagesOnLoad = true;

		/// <summary>
		/// 当前游戏或应用名
		/// </summary>
		public string appName = "Loon";

		/// <summary>
		/// 使用的初始化logo
		/// </summary>
		public string logoPath = "loon_logo.png";

		/// <summary>
		/// 当前字体名
		/// </summary>
		public string fontName = "Dialog";

		/// <summary>
		/// 当前应用版本号
		/// </summary>
		public string version = LSystem.UNKNOWN;

		/// <summary>
		/// 允许注销纹理(为false所有纹理都不被注销)
		/// </summary>
		public bool disposeTexture = true;

		/// <summary>
		/// 保存注入纹理的像素(为false不保存)
		/// </summary>
		public bool saveTexturePixels = true;

		/// <summary>
		/// 此项为true时,drag与move事件全游戏无效
		/// </summary>
		public bool notAllowDragAndMove = false;

		/// <summary>
		/// 锁定全部Touch事件,此项为true时,Loon中所有触屏(鼠标)事件不生效
		/// </summary>
		public bool lockAllTouchEvent = false;

		/// <summary>
		/// 初始化游戏时传参用，默认无数据
		/// </summary>
		public string[] args = new string[] { "" };

		/// <summary>
		/// 复制setting设置到自身
		/// </summary>
		/// <param name="setting"> </param>
		public virtual void Copy(LSetting setting)
		{
			this.isSyncTween = setting.isSyncTween;
			this.isFPS = setting.isFPS;
			this.isLogo = setting.isLogo;
			this.isCheckReisze = setting.isCheckReisze;
			this.isConsoleLog = setting.isConsoleLog;
			this.disposeTexture = setting.disposeTexture;
			this.fps = setting.fps;
			this.width = setting.width;
			this.height = setting.height;
			this.width_zoom = setting.width_zoom;
			this.height_zoom = setting.height_zoom;
			this.fullscreen = setting.fullscreen;
			this.emulateTouch = setting.emulateTouch;
			this.activationKey = setting.activationKey;
			this.convertImagesOnLoad = setting.convertImagesOnLoad;
			this.saveTexturePixels = setting.saveTexturePixels;
			this.appName = setting.appName;
			this.logoPath = setting.logoPath;
			this.fontName = setting.fontName;
			this.version = setting.version;
			this.fixedPaintLoopTime = setting.fixedPaintLoopTime;
			this.fixedUpdateLoopTime = setting.fixedUpdateLoopTime;
			this.useTrueFontClip = setting.useTrueFontClip;
			this.emulatorScale = setting.emulatorScale;
			this.notAllowDragAndMove = setting.notAllowDragAndMove;
			this.lockAllTouchEvent = setting.lockAllTouchEvent;
			this.args = setting.args;
		}

		public LSetting SetSystemLogFont(IFont font)
		{
			defaultLogFont = font;
			return this;
		}

		public LSetting SetSystemGameFont(IFont font)
		{
			defaultGameFont = font;
			return this;
		}

		public LSetting SetSystemGlobalFont(IFont font)
		{
			SetSystemLogFont(font);
			SetSystemGameFont(font);
			return this;
		}

		public LSetting FixedPaintTime()
		{
			FixedPaintTime(1f / 60f);
			return this;
		}

		public LSetting FixedPaintTime(float time)
		{
			this.fixedPaintLoopTime = (long)(time * 1000f);
			return this;
		}

		public LSetting FixedUpdateTime()
		{
			FixedPaintTime(3.5f / 60f);
			return this;
		}

		public LSetting FixedUpdateTime(float time)
		{
			this.fixedUpdateLoopTime = (long)(time * 1000f);
			return this;
		}

		public bool Landscape()
		{
			return this.height < this.width;
		}

		public bool Portrait()
		{
			return this.height >= this.width;
		}

		public LSetting UpdateScale()
		{
			if (Scaling())
			{
				/*LSystem.SetScaleWidth((float)width_zoom / (float)width);
				LSystem.SetScaleHeight((float)height_zoom / (float)height);
				LSystem.viewSize.SetSize(width, height);
				if (LSystem.GetProcess() != null)
				{
					LSystem.GetProcess().Resize(width, height);
				}*/
			}
			return this;
		}

		public bool Scaling()
		{
			return this.width_zoom > 0 && this.height_zoom > 0
					&& (this.width_zoom != this.width || this.height_zoom != this.height);
		}

		public int Width
        {
            get
            {
				return GetShowWidth();
            }
        }
		public int Height
		{
			get
			{
				return GetShowHeight();
			}
		}

		public int GetShowWidth()
		{
			return this.width_zoom > 0 ? this.width_zoom : this.width;
		}

		public int GetShowHeight()
		{
			return this.height_zoom > 0 ? this.height_zoom : this.height;
		}

		public bool WideScreen()
		{
			return NumberUtils.Compare(GetShowWidth() / GetShowHeight(), 1.777777f) == 0;
		}


	}
}
