namespace Loon.Core
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework.Content;
    using Loon.Core.Graphics.Opengl;
    using Loon.Core.Input;
    using Loon.Core.Event;
    using Loon.Core.Geom;
    using Loon.Java;
    using Loon.Utils;
    using Loon.Core.Timer;
    using Loon.Core.Graphics;

    public class LSystem
    {

        public string GetLanguage()
        {
            return System.Globalization.CultureInfo.CurrentCulture.DisplayName;
        }

        public enum ApplicationType
        {
            Android, JavaSE, XNA, IOS, HTML5, PSM
        }

        public static bool IsWP8
        {
            get
            {
                return (Environment.OSVersion.Version >= new Version(7, 10, 0x229a));
            }
        }

        public static CallQueue global_queue;

        public static ApplicationType type = ApplicationType.XNA;

        public static bool isStringTexture = false, isBackLocked = false;

        public static float EMULATOR_BUTTIN_SCALE = 1f;

        public const int RESOLUTION_LOW = 0;

        public const int RESOLUTION_MEDIUM = 1;

        public const int RESOLUTION_HIGH = 2;

        public static int GetResolutionType()
        {
            int max = MathUtils.Max(screenRect.width, screenRect.height);
            if (max < 480)
            {
                return RESOLUTION_LOW;
            }
            else if (max <= 800 && max >= 480)
            {
                return RESOLUTION_MEDIUM;
            }
            else
            {
                return RESOLUTION_HIGH;
            }
        }
        
	    public static void Post(Updateable update) {
		    if (global_queue != null) {
			    global_queue.InvokeLater(update);
		    } else {
			    LSystem.Load(update);
		    }
	    }

	    // 包内默认的图片路径
	    public const string FRAMEWORK_IMG_NAME = "assets/loon_";

        // 行分隔符
        public const string LS = "\n";

        // 文件分割符
        public const string FS = "\\";

        public static bool IsEmulator()
        {
            return false;
        }

        public static SystemTimer GetSystemTimer()
        {
            return new SystemTimer();
        }

        public static void Close(LTexture texture)
        {
            if (texture != null)
            {
                texture.Destroy();
                texture = null;
            }
        }


        public static void Close(Stream s)
        {
            if (s != null)
            {
                s.Close();
                s = null;
            }
        }

        public const uint TRANSPARENT = 0x00000000;

        public static string BASE_ASSETS = "Assets";

        // public static string FRAMEWORK_IMG_NAME = BASE_ASSETS + "/loon_";

        public readonly static JavaRuntime runtime = JavaRuntime.GetJavaRuntime();

        // 框架名
        static public readonly string FRAMEWORK = "loon";

        // 框架版本信息
        static public readonly string VERSION = "0.4.0";

        static public LGameXNA2DActivity screenActivity;

        static public bool isPaused,isRunning,isResume,isDestroy;
 
        // 秒
        static public readonly long SECOND = 1000;

        // 分
        static public readonly long MINUTE = SECOND * 60;

        // 小时
        static public readonly long HOUR = MINUTE * 60;

        // 天
        static public readonly long DAY = HOUR * 24;

        // 周
        static public readonly long WEEK = DAY * 7;

        // 理论上一年
        static public readonly long YEAR = DAY * 365;

        static public int MAX_SCREEN_WIDTH = 480;

        static public int MAX_SCREEN_HEIGHT = 320;

        static public bool AUTO_REPAINT = true;

        static public bool SCREEN_LANDSCAPE;
        // 随机数
        static public readonly Random random = new Random();

        public static LProcess screenProcess;

        public static float scaleWidth = 1, scaleHeight = 1;

        public static RectBox screenRect;

        // 最大缓存数量
        public static int DEFAULT_MAX_CACHE_SIZE = 30;

        public static string encoding = "UTF-8";

        public static string FONT_NAME = "Monospaced";

        public static int DEFAULT_MAX_FPS = 60;

        public static bool isLogo;

        public static void CallScreenRunnable(Runnable runnable)
        {
            LProcess process = LSystem.screenProcess;
            if (process != null)
            {
              
                Screen screen = process.GetScreen();
                if (screen != null)
                {
                    lock (screen)
                    {
                        screen.CallEvent(runnable);
                    }
                }
            }
        }

        public static void Load(Updateable u)
        {
            LProcess process = LSystem.screenProcess;
            if (process != null)
            {
                process.AddLoad(u);
            }
        }

        public static void Unload(Updateable u)
        {
            LProcess process = LSystem.screenProcess;
            if (process != null)
            {
                process.AddUnLoad(u);
            }
        }

        public static void ClearUpdate()
        {
            LProcess process = LSystem.screenProcess;
            if (process != null)
            {
                process.RemoveAllDrawing();
            }
        }

        public static void Drawing(Drawable d)
        {
            LProcess process = LSystem.screenProcess;
            if (process != null)
            {
                process.AddDrawing(d);
            }
        }

        public static void ClearDrawing()
        {
            LProcess process = LSystem.screenProcess;
            if (process != null)
            {
                process.RemoveAllDrawing();
            }
        }

        public static void WriteInt(Stream o, int number)
        {
            byte[] bytes = new byte[4];
            try
            {
                for (int i = 0; i < 4; i++)
                {
                    bytes[i] = (byte)((number >> (i * 8)) & 0xff);
                }
                o.Write(bytes, 0, bytes.Length);
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }

        public static int ReadInt(Stream o)
        {
            int data = -1;
            try
            {
                data = (o.ReadByte() & 0xff);
                data |= ((o.ReadByte() & 0xff) << 8);
                data |= ((o.ReadByte() & 0xff) << 16);
                data |= ((o.ReadByte() & 0xff) << 24);
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
            return data;
        }

        public static int Unite(int hashCode, bool value)
        {
            int v = value ? 1231 : 1237;
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, long value)
        {
            int v = (int)(value ^ ((long)((ulong)value >> 32)));
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, float value)
        {
            int v = (int)(value);
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, double value)
        {
            long v = (long)(value);
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, object value)
        {
            return Unite(hashCode, value.GetHashCode());
        }

        public static int Unite(int hashCode, int value)
        {
            return 31 * hashCode + value;
        }

    }
}