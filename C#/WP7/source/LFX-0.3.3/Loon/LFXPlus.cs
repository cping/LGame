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
    using Microsoft.Xna.Framework.Content;
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
    using System.Globalization;
    using System.Diagnostics;

    public interface XNAListener
    {
        void Create(Microsoft.Xna.Framework.Game game);

        void Initialize(Microsoft.Xna.Framework.Game game);

        void LoadContent(Microsoft.Xna.Framework.Game game);

        void UnloadContent(Microsoft.Xna.Framework.Game game);

        void Update(Microsoft.Xna.Framework.Game game,GameTime gameTime);

        void Draw(Microsoft.Xna.Framework.Game game, GameTime gameTime);

        void Dispose(Microsoft.Xna.Framework.Game game, bool disposing);
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

    internal static class XNAConfig
    {

        private static System.Resources.ResourceManager resourceMan;

        private static bool isActive = false;

        private static Dictionary<string, LTexture> texCaches = new Dictionary<string, LTexture>(CollectionUtils.INITIAL_CAPACITY);

        private static Dictionary<string, BMFont> fontCaches = new Dictionary<string, BMFont>(CollectionUtils.INITIAL_CAPACITY);

        public static bool IsActive()
        {
            return isActive;
        }

        internal static System.Resources.ResourceManager ResourceManager
        {
            get
            {
                if (object.ReferenceEquals(resourceMan, null))
                {
                    System.Resources.ResourceManager resMan = new System.Resources.ResourceManager("LFX.g", typeof(LFXPlus).Assembly);
                    resourceMan = resMan;
                    resourceMan.IgnoreCase = true;
                }
                return resourceMan;
            }
        }

        public static void Load()
        {
            if (ResourceManager != null)
            {
                isActive = true;
            }
        }

        private static void VaildLoon()
        {
            if (!isActive)
            {
                throw new NotImplementedException("Has not been loaded .def file !");
            }
        }

        public static LTexture LoadTex(string name)
        {
            VaildLoon();
            LTexture texture = (LTexture)CollectionUtils.Get(texCaches, name);
            if (texture == null || texture.isClose)
            {
                try
                {
                    texture = new LTexture(ResourceManager.GetStream(name));
                }
                catch (Exception ex)
                {
                    Log.DebugWrite(name);
                    Log.Exception(ex);
                }
                texCaches.Add(name, texture);
            }
            return texture;
        }

        public static BMFont LoadFnt(string name)
        {
            VaildLoon();
            string newName = FileUtils.GetNoExtensionName(name);
            BMFont font = (BMFont)CollectionUtils.Get(fontCaches, name);
            if (font == null || font.IsClose())
            {
                Stream fnt = ResourceManager.GetStream(name + ".fnt");
                Stream img = ResourceManager.GetStream(name + ".png");
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

    public delegate void EndOfUpdateDelegate();

    public abstract class LFXPlus : Microsoft.Xna.Framework.Game
    {

        private XNAListener xna_listener;

        private bool useXNAListener;

        public void SetXNAListener(XNAListener l)
        {
            if (l != null)
            {
                this.xna_listener = l;
                this.useXNAListener = true;
            }
            else
            {
                this.useXNAListener = false;
            }
        }

        public XNAListener GetXNAListener()
        {
            return xna_listener;
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
            XNAConfig.Load();
            MaxScreen(setting.width, setting.height);
            Initialization(setting.landscape, setting.mode);
            SetShowFPS(setting.showFPS);
            SetShowMemory(setting.showMemory);
            SetShowLogo(setting.showLogo);
            SetFPS(setting.fps);
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

        private Log log = LogFactory.GetInstance(typeof(LFXPlus));

        private static Microsoft.Xna.Framework.Content.ContentManager content;

        private LProcess process;

        private LTimerContext timerContext = new LTimerContext();

        private GraphicsDeviceManager graphics;

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

        /// <summary>
        /// LGame初始化构建函数
        /// </summary>
        public abstract void OnMain();

        public abstract void OnGameResumed();

        public abstract void OnGamePaused();

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

            LSystem.screenRect = new RectBox(0, 0, width, height);

            graphics.PreferredBackBufferFormat = displayMode.Format;
            graphics.PreferredBackBufferWidth = maxWidth;
            graphics.PreferredBackBufferHeight = maxHeight;

            if (landscape)
            {
               graphics.SupportedOrientations = DisplayOrientation.LandscapeLeft | DisplayOrientation.LandscapeRight;
            }
            else
            {
                graphics.SupportedOrientations = DisplayOrientation.Portrait;
            }

            //画面渲染与显示器同步
            graphics.SynchronizeWithVerticalRetrace = true;
            graphics.PreferMultiSampling = true;


#if WINDOWS
            graphics.IsFullScreen = false;
            IsMouseVisible = true;
#elif XBOX || WINDOWS_PHONE
            //全屏
            graphics.IsFullScreen = true;
#endif


            graphics.ApplyChanges();

            base.IsFixedTimeStep = false;

            isFPS = false;

            if (maxFrames <= 0)
            {
                SetFPS(LSystem.DEFAULT_MAX_FPS);
            }

            SetSleepTime(1);

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

        public LFXPlus()
            : base()
        {
            numformat = new NumberFormatInfo();
            numformat.NumberDecimalSeparator = ".";

            XNAConfig.Load();

            Content.RootDirectory = "";
            content = Content;
            if (useXNAListener)
            {
                xna_listener.Create(this); 
            }
            log.I("LGame 2D Engine Start");
            XNA_Graphics_Loading();
            OnMain();
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
        protected sealed override void Initialize()
        {
            this.UseXNA = true;
            this.XNA_Initialize();
            if (useXNAListener)
            {
                xna_listener.Initialize(this);
            }
            base.Initialize();
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
        protected sealed override void LoadContent()
        {
            GraphicsDevice.Clear(Color.Black);
            if (process != null)
            {
                process.Load(GraphicsDevice);
                process.Begin();
                isScale = process.IsScale();
            }
            if (useXNAListener)
            {
                xna_listener.LoadContent(this);
            }
            XNA_LoadContent();
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
        protected sealed override void UnloadContent()
        {
            if (process != null)
            {
                process.End();
            }
            if (useXNAListener)
            {
                xna_listener.UnloadContent(this);
            }
            XNA_UnloadContent();
            if (content != null)
            {
                content.Unload();
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
            this.graphics = new GraphicsDeviceManager(this);
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

        public LFXPlus GetView()
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
                SetLogo(XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "logo.png"));
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

        public void SetSleepTime(int time)
        {
            if (time <= 0)
            {
                InactiveSleepTime = TimeSpan.Zero;
            }
            else
            {
                InactiveSleepTime = TimeSpan.FromSeconds(time);
                sleepTime = time * 1000L;
            }
        }

        public float GetSleepTime()
        {
            return sleepTime;
        }

        public void SetFPS(int fps)
        {
            if (fps <= 0)
            {
                TargetElapsedTime = TimeSpan.Zero;
            }
            else
            {
                if (30 == fps)
                {
                    TargetElapsedTime = TimeSpan.FromTicks(333333);
                    maxFrames = 30;
                }
                else
                {
                    try
                    {
                        TargetElapsedTime = System.TimeSpan.FromTicks((System.TimeSpan.TicksPerSecond / fps) + 1);
                        maxFrames = fps;
                    }
                    catch
                    {
                        TargetElapsedTime = TimeSpan.FromTicks(333333);
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

        protected override void OnActivated(object sender, EventArgs args)
        {
            LSystem.isPaused = false;
            this.OnGameResumed();
            if (process != null)
            {
                process.OnResume();
            }
            base.OnActivated(sender, args);
        }

        protected override void OnDeactivated(object sender, EventArgs args)
        {
            LSystem.isPaused = true;
            this.OnGamePaused();
            if (process != null)
            {
                process.OnPause();
            }
            base.OnDeactivated(sender, args);
        }

        protected override void OnExiting(object sender, EventArgs args)
        {
            LSystem.isPaused = true;
            base.OnExiting(sender, args);
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

        public bool UseXNA
        {
            set;
            get;
        }

        protected override void Update(GameTime gameTime)
        {
            if (isClose)
            {
                return;
            }
            if (process != null)
            {
                process.Load();
                process.Calls();

                elapsedTime = gameTime.ElapsedGameTime.Ticks / 10000L;

                process.InputUpdate(elapsedTime);

                ActionControl.Update(elapsedTime);

                timerContext.millisSleepTime = sleepTime;
                timerContext.timeSinceLastUpdate = elapsedTime;

                process.RunTimer(timerContext);
                process.Unload();

            }
            if (useXNAListener)
            {
                xna_listener.Update(this,gameTime);
            }
            if (UseXNA)
            {
                base.Update(gameTime);
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
                            font = XNAConfig.LoadFnt(LSystem.FRAMEWORK_IMG_NAME+"system");
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

        protected override void Draw(GameTime gameTime)
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
                    logoFlag.Draw(process.GL, (long)gameTime.ElapsedGameTime.TotalMilliseconds);
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
                DrawXNA((float)gameTime.ElapsedGameTime.TotalSeconds);
            }
            if (useXNAListener)
            {
                xna_listener.Draw(this,gameTime);
            }
            if (UseXNA)
            {
                base.Draw(gameTime);
            }
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
            if (disposing)
            {
                useXNAListener = false;
                if (this.xna_listener != null)
                {
                    this.xna_listener.Dispose(this,disposing);
                    this.xna_listener = null;
                }
            }
        }

        public void GameDestory()
        {
            log.I("LGame 2D Engine Shutdown");
            try
            {
                isClose = true;
                if (process != null)
                {
                    process.OnDestroy();
                }
                if (content != null)
                {
                    content.Unload();
                    content = null;
                }
                XNAConfig.Dispose();
                Exit();
            }
            catch
            {
            }
        }

    }
}
