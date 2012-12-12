using System;
using Loon.Core.Graphics.Opengl;
using Microsoft.Xna.Framework;
using System.Text;
using Loon.Java;
using Loon.Core;
using Loon.Core.Timer;
using Loon.Utils;
using Loon.Core.Geom;
using Loon.Core.Input;
using Loon.Core.Graphics;
using Loon.Action;
using System.Globalization;
namespace Loon
{

    class Logo : LRelease
    {

        private int centerX, centerY;

        private float alpha = 0f;

        private float curFrame, curTime;

        internal bool finish,inToOut;

        internal LTexture logo;

        public Logo(LTexture texture)
        {
            this.logo = texture;
            this.curTime = 60;
            this.curFrame = 0;
            this.inToOut = true;
        }

        public void Draw(GLEx gl)
        {
            if (logo == null || finish)
            {
                return;
            }
            if (!logo.IsLoaded())
            {
                this.logo.LoadTexture();
            }
            if (centerX == 0 || centerY == 0)
            {
                this.centerX = (int)(LSystem.screenRect.width)
                     / 2 - logo.GetWidth() / 2;
                this.centerY = (int)(LSystem.screenRect.height)
                        / 2 - logo.GetHeight() / 2;
            }
            if (logo == null || !logo.IsLoaded())
            {
                return;
            }
            alpha = (curFrame / curTime);
            if (inToOut)
            {
                curFrame++;
                if (curFrame == curTime)
                {
                    alpha = 1f;
                    inToOut = false;
                }
            }
            else if (!inToOut)
            {
                curFrame--;
                if (curFrame == 0)
                {
                    alpha = 0f;
                    finish = true;
                }
            }
            gl.Reset(true);
            gl.SetAlpha(alpha);
            gl.DrawTexture(logo, centerX, centerY);
        }

        public void Dispose()
        {
            if (logo != null)
            {
                logo.Destroy();
                logo = null;
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

    public interface XNAListener
    {
        void Create(Microsoft.Xna.Framework.Game game);

        void Initialize(Microsoft.Xna.Framework.Game game);

        void LoadContent(Microsoft.Xna.Framework.Game game);

        void UnloadContent(Microsoft.Xna.Framework.Game game);

        void Update(Microsoft.Xna.Framework.Game game, Microsoft.Xna.Framework.GameTime gameTime);

        void Draw(Microsoft.Xna.Framework.Game game, Microsoft.Xna.Framework.GameTime gameTime);

        void Dispose(Microsoft.Xna.Framework.Game game, bool disposing);
    }

    public class LSetting
    {
        public int width = LSystem.MAX_SCREEN_WIDTH;

        public int height = LSystem.MAX_SCREEN_HEIGHT;

        public int fps = LSystem.DEFAULT_MAX_FPS;

        public string title;

        public bool full = true;

        public bool showFPS;

        public bool showMemory;

        public bool showLogo;

        public bool landscape;

        public LMode mode = LMode.Fill;

    }

    public class GameType
    {
        public LSetting setting = new LSetting();

        public Type mainType = null;

        public object[] args = null;
    }

    public class LGameXNA2DActivity : XNAMainActivity
    {

        private LSTRFont fpsFont;

        private Logo logoFlag;

        private Loon.Utils.Debug.Log m_log = Loon.Utils.Debug.LogFactory.GetInstance(typeof(LGame));

        private static bool suspend;

        private object m_sync = new object();

        private long lastTimeMicros, currTimeMicros, goalTimeMicros,
                elapsedTimeMicros, remainderMicros, elapsedTime;

        private long maxFrames = LSystem.DEFAULT_MAX_FPS;

        private float frameRate;

        private bool isFPS, isMemory;

        private bool m_onRunning, m_onPause, m_onDestroy, m_onResume;

        private float frames;

        private float totalFrames;

        private LTimerContext timerContext;

        private SystemTimer timer;

        private XNABind m_game;

        private LProcess m_process;

        private NumberFormatInfo numformat;

        public LGameXNA2DActivity(XNABind game, int width, int height)
            : base(width, height)
        {
            LSystem.screenActivity = this;
            this.numformat = new NumberFormatInfo();
            this.numformat.NumberDecimalSeparator = ".";
            this.m_game = game;
        }


        public LTexture GetLogo()
        {
            return logoFlag.logo;
        }

        public void SetLogo(LTexture img)
        {
            if (logoFlag == null)
            {
                this.logoFlag = new Logo(new LTexture(img));
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
        
	    private string pFontString = "FPS0123456789:";

        public void SetShowFPS(bool showFps)
        {
            this.isFPS = showFps;
            if (showFps && fpsFont == null)
            {
                this.fpsFont = new LSTRFont(LFont.GetFont(20), pFontString);
            }
        }

        public bool GetShowFPS()
        {
            return isFPS;
        }

        public void SetShowMemory(bool m)
        {
            this.isMemory = m;
        }

        public void SetFPS(int fps)
        {
            this.maxFrames = fps;
        }

        public int GetFPS()
        {
            return (int)maxFrames;
        }

        public void Register(LSetting setting, Type clazz,
              params object[] args)
        {
            XNAConfig.Load();
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
                            object o = JavaRuntime.Invoke(constructor, args);
                        }
                    }
                    catch (Exception ex)
                    {
                        Loon.Utils.Debug.Log.Exception(ex);
                    }
                }
            }
        }

        public void ShowScreen()
        {
            LSystem.isRunning = true;
        }

        public void SetScreen(Screen screen)
        {
            if (m_process != null)
            {
                m_process.SetScreen(screen);
            }
        }

        public void SetFPS(long frames)
        {
            this.maxFrames = frames;
        }

        public long GetMaxFPS()
        {
            return this.maxFrames;
        }

        public long GetCurrentFPS()
        {
            return (int)this.frameRate;
        }

        public float GetScalex()
        {
            return LSystem.scaleWidth;
        }

        public float GetScaley()
        {
            return LSystem.scaleHeight;
        }

        public bool IsRunning()
        {
            return LSystem.isRunning;
        }

        public void SetRunning(bool isRunning)
        {
            LSystem.isRunning = isRunning;
        }

        public void SetPause(bool isPause)
        {
            LSystem.isPaused = isPause;
        }

        private void Printf(string mes)
        {
            if (m_log != null)
            {
                m_log.I(mes);
            }
        }

        public void LoadApp()
        {
            Printf("loadApp");
        }

        public void UnloadApp()
        {
            Printf("unloadApp");
        }

        public override void CreateApp(GL gl, int width, int height)
        {
            if (LSystem.screenRect != null)
            {
                LSystem.screenRect.SetBounds(0, 0, width, height);
            }
            else
            {
                LSystem.screenRect = new RectBox(0, 0, width, height);
            }
            if (m_process == null)
            {
                m_process = new LProcess(this, width, height);
            }
            m_process.Load(gl);
            LSystem.screenProcess = m_process;
        }

        public override void DoMain()
        {
            m_game.OnStateLog(m_log);

            LSystem.isResume = false;
            LSystem.isPaused = false;
            LSystem.isDestroy = false;

            timerContext = new LTimerContext();
            timer = LSystem.GetSystemTimer();

            if (m_game != null)
            {
                GameType type = m_game.GetGameType();
                if (type != null)
                {
                    Register(type.setting, type.mainType, type.args);
                }
            }

            Printf("doMain");
        }

        public void Wait()
        {
            JavaRuntime.Wait(m_sync);
        }

        public void Wait(long m)
        {
            JavaRuntime.Wait(m_sync, m);
        }

        public void Notify()
        {
            JavaRuntime.Notify(m_sync);
        }

        public void NotifyAll()
        {
            JavaRuntime.NotifyAll(m_sync);
        }

        public override void Draw(GL g)
        {
            this.m_onRunning = false;
            this.m_onPause = false;
            this.m_onDestroy = false;
            this.m_onResume = false;

            lock (m_sync)
            {
                m_onRunning = LSystem.isRunning;
                m_onPause = LSystem.isPaused;
                m_onDestroy = LSystem.isDestroy;
                m_onResume = LSystem.isResume;

                if (LSystem.isResume)
                {
                    LSystem.isResume = false;
                }

                if (LSystem.isPaused)
                {
                    LSystem.isPaused = false;
                    JavaRuntime.NotifyAll(m_sync);
                }

                if (LSystem.isDestroy)
                {
                    LSystem.isDestroy = false;
                    JavaRuntime.NotifyAll(m_sync);
                }

                if (m_onResume)
                {
                    m_log.I("m_onResume");
                    timer = LSystem.GetSystemTimer();
                    lastTimeMicros = timer.GetTimeMicros();
                    elapsedTime = 0;
                    remainderMicros = 0;
                    m_process.OnResume();
                }


                if (m_onRunning)
                {
                    if (LSystem.isLogo)
                    {
                            if (logoFlag == null)
                            {
                                LSystem.isLogo = false;
                                return;
                            }
                            logoFlag.Draw(m_process.gl);
                            if (logoFlag.finish)
                            {
                                m_process.gl.SetAlpha(1.0f);
                                m_process.gl.DrawClear(LColor.black);
                                LSystem.isLogo = false;
                                logoFlag.Dispose();
                                logoFlag = null;
                                return;
                            }
                        return;
                    }

                    if (!m_process.Next())
                    {
                        return;
                    }

                    m_process.Load();

                    m_process.Calls();

                    goalTimeMicros = lastTimeMicros + 1000000L / maxFrames;
                    currTimeMicros = timer.SleepTimeMicros(goalTimeMicros);
                    elapsedTimeMicros = currTimeMicros - lastTimeMicros
                            + remainderMicros;
                    elapsedTime = MathUtils.Max(0, (elapsedTimeMicros / 1000));
                    remainderMicros = elapsedTimeMicros - elapsedTime * 1000;
                    lastTimeMicros = currTimeMicros;
                    timerContext.millisSleepTime = remainderMicros;
                    timerContext.timeSinceLastUpdate = elapsedTime;

                    ActionControl.Update(elapsedTime);

                    m_process.RunTimer(timerContext);

                    if (LSystem.AUTO_REPAINT)
                    {
                        GLEx gl = m_process.gl;

                        if (gl != null)
                        {

                            if (!m_process.Next())
                            {
                                return;
                            }

                            //设定graphics.PreferredBackBufferWidth和graphics.PreferredBackBufferHeight时WP会自动缩放画面，
                            //此处无需再缩放一次。
                            /*if (LSystem.scaleWidth != 1f || LSystem.scaleHeight != 1f)
                            {
                                gl.Scale(LSystem.scaleWidth,
                                        LSystem.scaleHeight);
                            }*/

                            int repaintMode = m_process.GetRepaintMode();

                            switch (repaintMode)
                            {
                                case Screen.SCREEN_BITMAP_REPAINT:
                                    gl.Reset(true);
                                    if (m_process.GetX() == 0 && m_process.GetY() == 0)
                                    {
                                        gl.DrawTexture(m_process.GetBackground(), 0, 0);
                                    }
                                    else
                                    {
                                        gl.DrawTexture(m_process.GetBackground(), m_process.GetX(),
                                                m_process.GetY());
                                    }
                                    break;
                                case Screen.SCREEN_COLOR_REPAINT:
                                    LColor c = m_process.GetColor();
                                    if (c != null)
                                    {
                                        gl.DrawClear(c);
                                    }
                                    break;
                                case Screen.SCREEN_CANVAS_REPAINT:
                                    gl.Reset(true);
                                    break;
                                case Screen.SCREEN_NOT_REPAINT:
                                    gl.Reset(true);
                                    break;
                                default:
                                    gl.Reset(true);
                                    if (m_process.GetX() == 0 && m_process.GetY() == 0)
                                    {
                                        gl.DrawTexture(
                                                m_process.GetBackground(),
                                                repaintMode / 2
                                                        - LSystem.random.Next(repaintMode),
                                                repaintMode / 2
                                                        - LSystem.random.Next(repaintMode));
                                    }
                                    else
                                    {
                                        gl.DrawTexture(m_process.GetBackground(),
                                                m_process.GetX() + repaintMode / 2
                                                        - LSystem.random.Next(repaintMode),
                                                m_process.GetY() + repaintMode / 2
                                                        - LSystem.random.Next(repaintMode));
                                    }
                                    break;
                            }

                            m_process.Draw();

                            m_process.Drawable(elapsedTime);

                            if (isFPS)
                            {
                                this.TickFrames();
                                fpsFont.DrawString(string.Format(numformat, "FPS:{0}", frameRate), 5, 5, 0, LColor.white);
                            }

                            m_process.DrawEmulator();

                            m_process.Unload();

                            gl.Restore();

                        }

                    }

                }

            }

        }

        public void OnDraw(Microsoft.Xna.Framework.GameTime gameTime)
        {
            if (!m_onRunning)
            {
                GL.device.Clear(Color.Black);
            }
        }

        public void OnUpdate(Microsoft.Xna.Framework.GameTime gameTime)
        {
            if (m_onRunning)
            {
                m_process.InputUpdate(gameTime.ElapsedGameTime.Ticks / 10000L);
            }
        }

        private void TickFrames()
        {
            int time = Environment.TickCount;
            if (time - totalFrames > 1000)
            {
                frameRate = Math.Min(maxFrames, frames);
                frames = 0;
                totalFrames = time;
            }
            frames++;

        }

        public override void InitApp(XNAContext context, GL gl)
        {
            if (m_process == null)
            {
                m_process = new LProcess(this, context._width, context._height);
            }
            m_process.Load(gl);
            Printf("initApp");
        }

        public override void PauseApp()
        {
            if (LSystem.isDestroy)
            {
                return;
            }
            if (!suspend)
            {
                LSystem.isPaused = true;
                Printf("pauseApp");
                try
                {

                    suspend = true;
                    if (m_game != null)
                    {
                        m_game.OnGamePaused();
                    }
                }
                catch (Exception)
                {
                }
            }
        }

        public override void ResumeApp()
        {
            if (LSystem.isDestroy)
            {
                return;
            }
            if (suspend)
            {
                LSystem.isResume = true;
                Printf("resumeApp");
                try
                {
                    suspend = false;
                    if (m_game != null)
                    {
                        m_game.OnGameResumed();
                    }
                }
                catch (Exception)
                {
                }
            }
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

        public bool IsPause()
        {
            return m_onPause;
        }

        public bool IsResume()
        {
            return m_onResume;
        }

        public bool IsDestroy()
        {
            return m_onDestroy;
        }

        public override int GetWidth()
        {
            return LSystem.screenRect.width;
        }

        public override int GetHeight()
        {
            return LSystem.screenRect.height;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public void FinishApp()
        {
            if (!LSystem.isDestroy)
            {
                lock (this.m_sync)
                {
                    LSystem.isRunning = false;
                    LSystem.isDestroy = true;
                    if (LSystem.screenProcess != null)
                    {
                        LSystem.screenProcess.OnDestroy();
                        ActionControl.GetInstance().StopAll();
                    }
                    XNAConfig.Dispose();
                    if (fpsFont != null)
                    {
                        fpsFont.Dispose();
                        fpsFont = null;
                    }
                }
                Printf("finishApp");
            }
        }

       public void Exit()
       {
           if (m_game != null)
           {
               m_game.OnGameExit();
           }
       }

    }
}
