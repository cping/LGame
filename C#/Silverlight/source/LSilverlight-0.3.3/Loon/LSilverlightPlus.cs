#region LGame License
/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
#endregion
namespace Loon
{
    using System;
    using System.IO;
    using System.Text;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework;
    using Microsoft.Xna.Framework.Graphics;
    using Loon.Java;
    using Loon.Utils;
    using Loon.Action;
    using Loon.Core.Graphics;
    using Loon.Core.Input;
    using Loon.Core.Geom;
    using Loon.Core.Timer;
    using Loon.Core.Resource;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core;
    using Loon.Utils.Debug;
    using System.Windows.Navigation;
    using System.Globalization;

    public interface SilverlightListener
    {
        void Create(Microsoft.Phone.Controls.PhoneApplicationPage game);

        void Initialize(Microsoft.Phone.Controls.PhoneApplicationPage game);

        void LoadContent(Microsoft.Phone.Controls.PhoneApplicationPage game);

        void UnloadContent(Microsoft.Phone.Controls.PhoneApplicationPage game);

        void Update(Microsoft.Phone.Controls.PhoneApplicationPage game, GameTimerEventArgs gameTime);

        void Draw(Microsoft.Phone.Controls.PhoneApplicationPage game, GameTimerEventArgs gameTime);

        void Dispose(Microsoft.Phone.Controls.PhoneApplicationPage game, bool disposing);
    }

    public class LSetting
    {

        public int width = LSystem.MAX_SCREEN_WIDTH;

        public int height = LSystem.MAX_SCREEN_HEIGHT;

        public int fps = LSystem.DEFAULT_MAX_FPS;

        public String title;

        public bool showFPS;

        public bool showMemory;

        public bool showLogo;

        public bool landscape;

        public LMode mode = LMode.Fill;

    }

    public static class XNAConfig
    {
        private static string fileName;

        private static bool isActive = false;

        private static Dictionary<string, LTexture> texCaches = new Dictionary<string, LTexture>(CollectionUtils.INITIAL_CAPACITY);

        private static Dictionary<string, BMFont> fontCaches = new Dictionary<string, BMFont>(CollectionUtils.INITIAL_CAPACITY);

        public static bool IsActive()
        {
            return isActive;
        }

        public static void Load(string path)
        {
            fileName = path;
            isActive = true;
        }

        private static void VaildLoon()
        {
            if (!isActive)
            {
                throw new NotImplementedException("Has not been loaded .def file !");
            }
        }

        public static LTexture LoadTexture(string name)
        {
            VaildLoon();
            LTexture texture = (LTexture)CollectionUtils.Get(texCaches, name);
            if (texture == null || texture.isClose)
            {
                texture = LPKResource.OpenTexture(fileName, name);
                texCaches.Add(name, texture);
            }
            return texture;
        }

        public static BMFont LoadBMFont(string name)
        {
            VaildLoon();
            string newName = FileUtils.GetNoExtensionName(name);
            BMFont font = (BMFont)CollectionUtils.Get(fontCaches, name);
            if (font == null || font.IsClose())
            {
                Stream fnt = LPKResource.OpenStream(fileName, name + ".fnt");
                Stream img = LPKResource.OpenStream(fileName, name + ".png");
                font = new BMFont(fnt, img);
                fontCaches.Add(name, font);
            }
            return font;
        }

        public static void Dispose()
        {
            if (texCaches != null)
            {
                foreach (LTexture tex in texCaches.Values)
                {
                    if (tex != null)
                    {
                        tex.Destroy();
                    }
                }
                texCaches.Clear();
            }
            if (fontCaches != null)
            {
                foreach (BMFont font in fontCaches.Values)
                {
                    if (font != null)
                    {
                        font.Dispose();
                    }
                }
                fontCaches.Clear();
            }
        }

    }

    public static class MeasureSpec
    {
        private static readonly int MODE_SHIFT = 30;

        private static readonly int MODE_MASK = 0x3 << MODE_SHIFT;

        public static readonly int UNSPECIFIED = 0 << MODE_SHIFT;

        public static readonly int EXACTLY = 1 << MODE_SHIFT;

        public static readonly int AT_MOST = 2 << MODE_SHIFT;

        public static int MakeMeasureSpec(int size, int mode)
        {
            return size + mode;
        }

        public static int GetMode(int measureSpec)
        {
            return (measureSpec & MODE_MASK);
        }

        public static int GetSize(int measureSpec)
        {
            return (measureSpec & ~MODE_MASK);
        }
    }


    internal class Logo
    {

        private int type, centerX, centerY;

        private LTimer timer = new LTimer(50);

        private float alpha = 0.0f;

        internal bool finish;

        internal LTexture logo;

        private Color color = new Color(255, 255, 255, 0);

        public Logo(LTexture texture)
        {
            this.logo = texture;
        }

        public void Draw(GLEx gl, long elapsed)
        {
            if (logo == null || finish)
            {
                return;
            }
            if (!logo.isLoaded)
            {
                this.logo.LoadTexture();
                this.centerX = (int)(LSystem.screenRect.width * LSystem.scaleWidth) / 2 - logo.Width / 2;
                this.centerY = (int)(LSystem.screenRect.height * LSystem.scaleHeight) / 2 - logo.Height / 2;
            }
            if (logo == null || !logo.isLoaded)
            {
                finish = true;
                return;
            }
            lock (logo)
            {
                gl.Clear();
                color.A = (byte)(255 * alpha);
                gl.DrawTexture(logo, centerX, centerY, color);
            }
            switch (type)
            {
                case 0:
                    if (alpha >= 1f)
                    {
                        alpha = 1f;
                        type = 1;
                    }
                    if (alpha < 1.0f)
                    {
                        if (timer.Action(elapsed))
                        {
                            alpha += 0.015f;
                        }
                    }
                    break;
                case 1:
                    if (timer.Action(elapsed))
                    {
                        alpha = 1f;
                        type = 2;
                    }
                    break;
                case 2:
                    if (alpha > 0.0f)
                    {
                        if (timer.Action(elapsed))
                        {
                            alpha -= 0.015f;
                        }
                    }
                    else if (alpha <= 0f)
                    {
                        alpha = 0;
                        type = 3;
                        finish = true;
                        if (logo != null)
                        {
                            logo.Destroy();
                            logo = null;
                        }
                        return;
                    }
                    break;
            }
        }
    }


    /// <summary>
    /// 游戏全屏模式
    /// </summary>
    public enum LMode
    {
        Defalut, Max, Fill, FitFill, Ratio, MaxRatio
    }

    public delegate void OnMainEvent(LSilverlightPlus plus);

    public delegate void EndOfUpdateDelegate();

    public class LSilverlightPlus 
    {

        private GameTimer TargetElapsedTime;

        private SilverlightListener sl_listener;

        private bool useXNAListener;

        public void SetSilverlightListener(SilverlightListener l)
        {
            if (l != null)
            {
                this.sl_listener = l;
                this.useXNAListener = true;
            }
            else
            {
                this.useXNAListener = false;
            }
        }

        public SilverlightListener GetSilverlightListener()
        {
            return sl_listener;
        }

        private static Type GetType(object o)
        {
            if (o is Int32)
            {
                return typeof(Int32);
            }
            else if (o is Single)
            {
                return typeof(Single);
            }
            else if (o is Double)
            {
                return typeof(Double);
            }
            else if (o is Int64)
            {
                return typeof(Int64);
            }
            else if (o is Int16)
            {
                return typeof(Int16);
            }
            else if (o is Int16)
            {
                return typeof(Int16);
            }
            else if (o is Boolean)
            {
                return typeof(Boolean);
            }
            else
            {
                return o.GetType();
            }
        }

        public void Register(LSetting setting, Type clazz,
                params object[] args)
        {
            MaxScreen(setting.width, setting.height);
            Initialization(setting.landscape, setting.mode);
            SetShowFPS(setting.showFPS);
            SetShowMemory(setting.showMemory);
            SetShowLogo(setting.showLogo);
            SetFPS(setting.fps);
            if (GamePage != null)
            {
                GamePage.Title = setting.title;
            }
            if (clazz != null)
            {
                if (args != null)
                {
                    try
                    {
                        int funs = args.Length;
                        if (funs == 0)
                        {
                            SetScreen((Screen)JavaRuntime.NewInstance(clazz));
                            ShowScreen();
                        }
                        else
                        {
                            Type[] functions = new Type[funs];
                            for (int i = 0; i < funs; i++)
                            {
                                functions[i] = GetType(args[i]);
                            }
                            System.Reflection.ConstructorInfo constructor = JavaRuntime.GetConstructor(clazz, functions);
                            Object o = JavaRuntime.Invoke(constructor, args);
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                }
            }
        }

        private Log log = LogFactory.GetInstance(typeof(LSilverlightPlus));

        private static Microsoft.Xna.Framework.Content.ContentManager content;

        private LProcess process;

        private LTimerContext timerContext = new LTimerContext();

        private SharedGraphicsDeviceManager graphics;

        private Logo logoFlag;

        private long elapsedTime;

        private int maxFrames;

        private long sleepTime = 1000L;

        private int width, height, maxWidth, maxHeight;

        private bool setupSensors;

        private bool fullScreen = true;

        private bool landscape;

        private bool clear = true;

        private bool isScale;

        private DisplayMode displayMode;

        private NumberFormatInfo numformat;

        private int frameRate;

        private string fps;

        private float updateInterval = 1.0f;

        private float timeSinceLastUpdate = 0.0f;

        private float framecount = 0;

        private LMode mode = LMode.Ratio;

        private SystemTimer timer;

        private BMFont font;

        private bool isFPS, isMemory;

        private bool isClose;

        public void SetScreen(Screen screen)
        {
            if (process != null)
            {
                process.SetScreen(screen);
            }
        }

        private bool supportSensors = false;

        /// <summary>
        /// 加载并显示Screen到设备屏幕
        /// </summary>
        public void ShowScreen()
        {
            if (setupSensors)
            {
                Type accelerometer = null;
                if (accelerometer == null)
                {
                    try
                    {
                        accelerometer = JavaRuntime.ClassforName("Loon.Core.Input.Sensors.AccelerometerExecute");
                        supportSensors = true;
                    }
                    catch (Exception)
                    {
                        supportSensors = false;
                    }
                }
                if (supportSensors)
                {
                    try
                    {
                        object o = JavaRuntime.NewInstance(accelerometer);
                        System.Reflection.MethodInfo initialize = JavaRuntime.GetMethod(accelerometer, "Initialize");
                        initialize.Invoke(o, null);
                        System.Reflection.MethodInfo isActive = JavaRuntime.GetMethod(accelerometer, "IsActive");
                        supportSensors = (bool)isActive.Invoke(o, null);
                    }
                    catch (Exception)
                    {
                        supportSensors = false;
                    }
                }
            }
        }

        public bool IsSupportSensors()
        {
            return supportSensors;
        }

        public void Initialization(bool landscape)
        {
            Initialization(landscape, LMode.Ratio);
        }

        public void Initialization(bool landscape, LMode mode)
        {
            Initialization(landscape, true, mode);
        }

        public void Initialization(int width, int height,
                bool landscape)
        {
            Initialization(width, height, landscape, LMode.Ratio);
        }

        public void Initialization(int width, int height,
                bool landscape, LMode mode)
        {
            MaxScreen(width, height);
            Initialization(landscape, mode);
        }

        public UIElementRenderer UIElementRenderer;

        public void Initialization(bool l,
                bool f, LMode m)
        {

            log.I("GPU surface");

            this.landscape = l;
            this.fullScreen = f;
            this.mode = m;

            if (landscape == false)
            {
                if (LSystem.MAX_SCREEN_HEIGHT > LSystem.MAX_SCREEN_WIDTH)
                {
                    int tmp_height = LSystem.MAX_SCREEN_HEIGHT;
                    LSystem.MAX_SCREEN_HEIGHT = LSystem.MAX_SCREEN_WIDTH;
                    LSystem.MAX_SCREEN_WIDTH = tmp_height;
                }
            }

            this.CheckDisplayMode();

            RectBox d = GetScreenDimension();

            LSystem.SCREEN_LANDSCAPE = landscape;

            this.maxWidth = (int)d.GetWidth();
            this.maxHeight = (int)d.GetHeight();

            if (landscape && (d.GetWidth() > d.GetHeight()))
            {
                maxWidth = (int)d.GetWidth();
                maxHeight = (int)d.GetHeight();
            }
            else if (landscape && (d.GetWidth() < d.GetHeight()))
            {
                maxHeight = (int)d.GetWidth();
                maxWidth = (int)d.GetHeight();
            }
            else if (!landscape && (d.GetWidth() < d.GetHeight()))
            {
                maxWidth = (int)d.GetWidth();
                maxHeight = (int)d.GetHeight();
            }
            else if (!landscape && (d.GetWidth() > d.GetHeight()))
            {
                maxHeight = (int)d.GetWidth();
                maxWidth = (int)d.GetHeight();
            }

            if (mode != LMode.Max)
            {
                if (landscape)
                {
                    this.width = LSystem.MAX_SCREEN_WIDTH;
                    this.height = LSystem.MAX_SCREEN_HEIGHT;
                }
                else
                {
                    this.width = LSystem.MAX_SCREEN_HEIGHT;
                    this.height = LSystem.MAX_SCREEN_WIDTH;
                }
            }
            else
            {
                if (landscape)
                {
                    this.width = maxWidth >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH
                            : maxWidth;
                    this.height = maxHeight >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT
                            : maxHeight;
                }
                else
                {
                    this.width = maxWidth >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT
                            : maxWidth;
                    this.height = maxHeight >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH
                            : maxHeight;
                }
            }

            if (mode == LMode.Fill)
            {

                LSystem.scaleWidth = ((float)maxWidth) / width;
                LSystem.scaleHeight = ((float)maxHeight) / height;

            }
            else if (mode == LMode.FitFill)
            {

                RectBox res = GraphicsUtils.FitLimitSize(width, height,
                        maxWidth, maxHeight);
                maxWidth = res.width;
                maxHeight = res.height;
                LSystem.scaleWidth = ((float)maxWidth) / width;
                LSystem.scaleHeight = ((float)maxHeight) / height;

            }
            else if (mode == LMode.Ratio)
            {

                maxWidth = MeasureSpec.GetSize(maxWidth);
                maxHeight = MeasureSpec.GetSize(maxHeight);

                float userAspect = (float)width / (float)height;
                float realAspect = (float)maxWidth / (float)maxHeight;

                if (realAspect < userAspect)
                {
                    maxHeight = MathUtils.Round(maxWidth / userAspect);
                }
                else
                {
                    maxWidth = MathUtils.Round(maxHeight * userAspect);
                }

                LSystem.scaleWidth = ((float)maxWidth) / width;
                LSystem.scaleHeight = ((float)maxHeight) / height;

            }
            else if (mode == LMode.MaxRatio)
            {

                maxWidth = MeasureSpec.GetSize(maxWidth);
                maxHeight = MeasureSpec.GetSize(maxHeight);

                float userAspect = (float)width / (float)height;
                float realAspect = (float)maxWidth / (float)maxHeight;

                if ((realAspect < 1 && userAspect > 1)
                        || (realAspect > 1 && userAspect < 1))
                {
                    userAspect = (float)height / (float)width;
                }

                if (realAspect < userAspect)
                {
                    maxHeight = MathUtils.Round(maxWidth / userAspect);
                }
                else
                {
                    maxWidth = MathUtils.Round(maxHeight * userAspect);
                }

                LSystem.scaleWidth = ((float)maxWidth) / width;
                LSystem.scaleHeight = ((float)maxHeight) / height;

            }
            else
            {
                LSystem.scaleWidth = 1;
                LSystem.scaleHeight = 1;
            }

            if (landscape)
            {
                GamePage.Orientation = Microsoft.Phone.Controls.PageOrientation.Landscape | Microsoft.Phone.Controls.PageOrientation.LandscapeLeft | Microsoft.Phone.Controls.PageOrientation.LandscapeRight;
                GamePage.SupportedOrientations = Microsoft.Phone.Controls.SupportedPageOrientation.Landscape;
            }
            else
            {
                GamePage.Orientation = Microsoft.Phone.Controls.PageOrientation.Portrait | Microsoft.Phone.Controls.PageOrientation.PortraitDown | Microsoft.Phone.Controls.PageOrientation.PortraitUp;
                GamePage.SupportedOrientations = Microsoft.Phone.Controls.SupportedPageOrientation.Portrait;
            }

            LSystem.screenRect = new RectBox(0, 0, width, height);

            graphics.PreferredBackBufferFormat = displayMode.Format;
            graphics.PreferredBackBufferWidth = maxWidth;
            graphics.PreferredBackBufferHeight = maxHeight;

            if (GamePage != null)
            {
                UIElementRenderer = new UIElementRenderer(GamePage, maxWidth, maxHeight);
            }
      
            //画面渲染与显示器同步
            graphics.SynchronizeWithVerticalRetrace = true;

            graphics.ApplyChanges();

            isFPS = false;

            if (maxFrames <= 0)
            {
                SetFPS(LSystem.DEFAULT_MAX_FPS);
            }

            LSystem.screenActivity = this;
            LSystem.screenProcess = (process = new LProcess(this, width, height));

            StringBuilder sbr = new StringBuilder();
            sbr.Append("Mode:").Append(mode);
            log.I(sbr.ToString());
            sbr.Clear();
            sbr.Append("Width:").Append(width).Append(",Height:" + height);
            log.I(sbr.ToString());
            sbr.Clear();
            sbr.Append("MaxWidth:").Append(maxWidth)
                    .Append(",MaxHeight:" + maxHeight);
            log.I(sbr.ToString());
            sbr.Clear();
            sbr.Append("Scale:").Append(IsScale());
            log.I(sbr.ToString());

            if (GamePage != null)
            {
                GamePage.Background = null;
                GamePage.Foreground = null;
                GamePage.Style = null;
                GamePage.AllowDrop = false;
                GamePage.Visibility = System.Windows.Visibility.Collapsed;
                /*System.Windows.Interop.Settings settings = new System.Windows.Interop.Settings();
                settings.EnableFrameRateCounter = true;
                settings.EnableCacheVisualization = true;
                settings.EnableRedrawRegions = true;
                settings.MaxFrameRate = maxFrames;
                System.Windows.Media.BitmapCache cache = new System.Windows.Media.BitmapCache();
                cache.RenderAtScale = 1;
                GamePage.CacheMode = cache;*/
                GamePage.Opacity = 1f;
            }

        }

        public LFont XNAFont
        {
            set
            {
                LFont.SetDefaultFont(value);
            }
            get
            {
                return LFont.GetDefaultFont();
            }
        }

        public void LoadDefaultFont(LFont font)
        {
            LFont.SetDefaultFont(font);
        }

        public void SetupGravity()
        {
            this.setupSensors = true;
        }

        public void SetRunning(bool f)
        {
            isClose = f;
        }

        public bool IsScale()
        {
            return LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1;
        }

        public void MaxScreen(int width, int height)
        {
            LSystem.MAX_SCREEN_WIDTH = width;
            LSystem.MAX_SCREEN_HEIGHT = height;
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public int GetMaxWidth()
        {
            return maxWidth;
        }

        public int GetMaxHeight()
        {
            return maxHeight;
        }

        public static LSilverlightPlus Load(Microsoft.Phone.Controls.PhoneApplicationPage page, Microsoft.Xna.Framework.Content.ContentManager c, OnMainEvent main)
        {
            return new LSilverlightPlus(page, c, main);
        }

        private LSilverlightPlus(Microsoft.Phone.Controls.PhoneApplicationPage page, Microsoft.Xna.Framework.Content.ContentManager c, OnMainEvent main)
            : base()
        {
            isClose = false;
            numformat = new NumberFormatInfo();
            numformat.NumberDecimalSeparator = ".";

            GamePage = page;

            try
            {
                Microsoft.Phone.Shell.SystemTray.IsVisible = false;
            }
            catch (Exception)
            {
            }

            content = c;
            content.RootDirectory = "";
            
            TargetElapsedTime = new GameTimer();
            TargetElapsedTime.UpdateInterval = TimeSpan.FromTicks(333333);
            TargetElapsedTime.Update += OnUpdate;
            TargetElapsedTime.Draw += OnDraw;

            if (useXNAListener)
            {
                sl_listener.Create(GamePage);
            }

            log.I("LGame 2D Engine Start");
            Initialize();
            XNA_Graphics_Loading();
            if (main != null)
            {
                main(this);
            }
        }

        public static Microsoft.Xna.Framework.Content.ContentManager Get
        {
            get
            {
                return content;
            }
        }

        public void MemoryUssage()
        {
#if WINDOWS_PHONE
            long num = (long)Microsoft.Phone.Info.DeviceExtendedProperties.GetValue("ApplicationCurrentMemoryUsage");
            long num2 = (long)Microsoft.Phone.Info.DeviceExtendedProperties.GetValue("ApplicationPeakMemoryUsage");
            float single1 = ((float)num) / 1048576f;
            float single2 = ((float)num2) / 1048576f;
            log.I(single1 + "," + single2);
#endif
        }

        /// <summary>
        /// 调用XNA初始构造(禁止重载)
        /// </summary>
        public void Initialize()
        {
            this.XNA_Initialize();
            if (useXNAListener)
            {
                sl_listener.Initialize(GamePage);
            }
        }

        /// <summary>
        /// 提供给用户调用的XNA等价函数
        /// </summary>
        protected virtual void XNA_Initialize()
        {
        }

        /// <summary>
        /// 调用XNA资源加载函数(禁止重载)
        /// </summary>
        public void OnNavigatedTo(NavigationEventArgs e)
        {
            try
            {
                SharedGraphicsDeviceManager.Current.GraphicsDevice.SetSharingMode(true);
                SharedGraphicsDeviceManager.Current.GraphicsDevice.Clear(Color.Black);
            }
            catch
            {
            }
            if (process != null)
            {
                process.Load(SharedGraphicsDeviceManager.Current.GraphicsDevice);
                process.Begin();
                isScale = process.IsScale();
            }
            if (useXNAListener)
            {
                sl_listener.LoadContent(GamePage);
            }
            XNA_LoadContent();
            TargetElapsedTime.Start();
        }

        /// <summary>
        /// 提供给用户调用的XNA等价函数
        /// </summary>
        protected virtual void XNA_LoadContent()
        {
        }

        /// <summary>
        /// 调用XNA资源卸载函数(禁止重载)
        /// </summary>
        public void OnNavigatedFrom(NavigationEventArgs e)
        {
            if (process != null)
            {
                process.End();
                process.OnDestroy();
            }
            if (useXNAListener)
            {
                sl_listener.UnloadContent(GamePage);
            }
            XNA_UnloadContent();
            TargetElapsedTime.Stop();
            try
            {
                SharedGraphicsDeviceManager.Current.GraphicsDevice.SetSharingMode(false);
                this.Destory();
            }
            catch
            {
            }
            if (process != null)
            {
                process = null;
            }
        }

        /// <summary>
        /// 提供给用户调用的XNA等价函数
        /// </summary>
        protected virtual void XNA_UnloadContent()
        {
        }

        /// <summary>
        /// 加载XNA图像资源管理器
        /// </summary>
        private void XNA_Graphics_Loading()
        {
            this.graphics = SharedGraphicsDeviceManager.Current;
            this.timer = new SystemTimer();
        }

        public long GetMaxFPS()
        {
            return this.maxFrames;
        }

        public float GetScalex()
        {
            return LSystem.scaleWidth;
        }

        public float GetScaley()
        {
            return LSystem.scaleHeight;
        }

        public LSilverlightPlus GetView()
        {
            return this;
        }

        public LTexture GetLogo()
        {
            return logoFlag.logo;
        }

        public void SetShowMemory(bool m)
        {
            this.isMemory = m;
        }

        public void SetLogo(LTexture img)
        {
            if (logoFlag == null)
            {
                this.logoFlag = new Logo(img);
            }
        }

        public void SetLogo(string path)
        {
            SetLogo(LTextures.LoadTexture(path));
        }

        public void SetShowLogo(bool showLogo)
        {
            LSystem.isLogo = showLogo;
            if (logoFlag == null)
            {
                SetLogo(XNAConfig.LoadTexture("logo.png"));
            }
        }

        public void SetShowFPS(bool s)
        {
            this.isFPS = s;
        }

        public bool GetShowFPS()
        {
            return isFPS;
        }

        public void SetFPS(int fps)
        {
            if (fps <= 0)
            {
                TargetElapsedTime.UpdateInterval = TimeSpan.Zero;
            }
            else
            {
                if (30 == fps)
                {
                    TargetElapsedTime.UpdateInterval = TimeSpan.FromTicks(333333);
                    maxFrames = 30;
                }
                else
                {
                    try
                    {
                        TargetElapsedTime.UpdateInterval = System.TimeSpan.FromTicks((System.TimeSpan.TicksPerSecond / fps) + 1);
                        maxFrames = fps;
                    }
                    catch
                    {
                        TargetElapsedTime.UpdateInterval = TimeSpan.FromTicks(333333);
                        maxFrames = 30;
                    }
                }
            }
        }

        public int GetFPS()
        {
            return maxFrames;
        }

        public int GetCurrentFPS()
        {
            return frameRate;
        }

        public bool IsPortrait()
        {
            CheckDisplayMode();
            return displayMode.Width < displayMode.Height;
        }

        public bool IsLandscape()
        {
            CheckDisplayMode();
            return displayMode.Width > displayMode.Height;
        }

        public bool IsSquare()
        {
            CheckDisplayMode();
            return displayMode.Width == displayMode.Height;
        }

        public void SetClearFrame(bool clearFrame)
        {
            this.clear = clearFrame;
        }

        public RectBox GetScreenDimension()
        {
            CheckDisplayMode();
            return new RectBox(0, 0, displayMode.Width, displayMode.Height);
        }

        private void CheckDisplayMode()
        {
            if (displayMode == null)
            {
                this.displayMode = GraphicsAdapter.DefaultAdapter.CurrentDisplayMode;
            }
        }

        public string RootContent
        {
            set
            {
                LSystem.BASE_ASSETS = value;
            }
            get
            {
                return LSystem.BASE_ASSETS;
            }
        }

        public Microsoft.Phone.Controls.PhoneApplicationPage GamePage
        {
            set;
            get;
        }

        private void OnUpdate(object sender, GameTimerEventArgs e)
        {
            if (isClose)
            {
                return;
            }
            if (process != null)
            {
                process.Load();
                process.Calls();

                elapsedTime = e.ElapsedTime.Ticks / 10000L;

                process.InputUpdate(elapsedTime);

                ActionControl.Update(elapsedTime);

                timerContext.millisSleepTime = sleepTime;
                timerContext.timeSinceLastUpdate = elapsedTime;

                process.RunTimer(timerContext);
                process.Unload();
            }
            if (useXNAListener)
            {
                sl_listener.Update(GamePage, e);
            }
        }


        public virtual void DrawXNA()
        {
            DrawXNA(0);
        }

        protected virtual void DrawXNA(float totalSeconds)
        {

            GLEx gl = process.GL;

            if (gl != null)
            {

                if (!process.Next())
                {
                    return;
                }
                
                if (isScale)
                {
                    gl.Scale(LSystem.scaleWidth,
                            LSystem.scaleHeight);
                }

                int repaintMode = process.GetRepaintMode();
              
                switch (repaintMode)
                {
                    case Screen.SCREEN_BITMAP_REPAINT:
                        gl.Reset(clear);
                        if (process.GetX() == 0 && process.GetY() == 0)
                        {
                            gl.DrawTexture(process.GetBackground(), 0, 0);
                        }
                        else
                        {
                            gl.DrawTexture(process.GetBackground(), process.GetX(),
                                    process.GetY());
                        }
                        break;
                    case Screen.SCREEN_COLOR_REPAINT:
                        LColor c = process.GetColor();
                        if (c != null)
                        {
                            gl.DrawClear(c);
                        }
                        break;
                    case Screen.SCREEN_CANVAS_REPAINT:
                        gl.Reset(clear);
                        break;
                    case Screen.SCREEN_NOT_REPAINT:
                        gl.Reset(clear);
                        break;
                    default:
                        gl.Reset(clear);
                        if (process.GetX() == 0 && process.GetY() == 0)
                        {
                            gl.DrawTexture(
                                    process.GetBackground(),
                                    repaintMode / 2
                                            - LSystem.random.Next(repaintMode),
                                    repaintMode / 2
                                            - LSystem.random.Next(repaintMode));
                        }
                        else
                        {
                            gl.DrawTexture(process.GetBackground(),
                                    process.GetX() + repaintMode / 2
                                            - LSystem.random.Next(repaintMode),
                                    process.GetY() + repaintMode / 2
                                            - LSystem.random.Next(repaintMode));
                        }
                        break;
                }

                process.Draw();

                process.Drawable(elapsedTime);
                
                if (isFPS)
                {
                    gl.Reset(false);
                    framecount++;
                    timeSinceLastUpdate += totalSeconds;
                    if (timeSinceLastUpdate > updateInterval)
                    {
                        frameRate = Convert.ToInt16(framecount / timeSinceLastUpdate);
                        framecount = 0;
                        timeSinceLastUpdate -= updateInterval;

                    }

                    fps = string.Format(numformat, "FPS:{0}", frameRate);
                    
                    if (gl.UseFont)
                    {
                        gl.DrawString(fps, 5, 5, LColor.white);
                    }
                    else
                    {
                        if (XNAConfig.IsActive() && font == null)
                        {
                            font = XNAConfig.LoadBMFont("system");
                        }
                        if (font != null)
                        {
                            font.DrawBatchString(5, 5, fps, LColor.white);
                        }
                    }
                }
                process.DrawEmulator();

                gl.RestoreMatrix();

            }
        }

        private void OnDraw(object sender, GameTimerEventArgs e)
        {
            if (isClose)
            {
                return;
            }
            if (LSystem.isLogo)
            {
                if (logoFlag == null)
                {
                    LSystem.isLogo = false;
                    return;
                }
                lock (logoFlag)
                {
                    logoFlag.Draw(process.GL, (long)e.ElapsedTime.TotalMilliseconds);
                }
                if (logoFlag.finish)
                {
                    process.GL.Clear();
                    LSystem.isLogo = false;
                    logoFlag = null;
                    return;
                }
                return;
            }
            if (LSystem.AUTO_REPAINT)
            {
                DrawXNA((float)e.ElapsedTime.TotalSeconds);
            }
            if (useXNAListener)
            {
                sl_listener.Draw(GamePage,e);
            }
        }

        public void Destory()
        {
            log.I("LGame 2D Engine Shutdown");
            isClose = true;
            XNAConfig.Dispose();
            useXNAListener = false;
            if (this.sl_listener != null)
            {
                this.sl_listener.Dispose(GamePage, true);
                this.sl_listener = null;
            }
            if (UIElementRenderer != null)
            {
                UIElementRenderer.Dispose();
                UIElementRenderer = null;
            }
            if (content != null)
            {
                content.Unload();
                content = null;
            }
        }

    }
}
