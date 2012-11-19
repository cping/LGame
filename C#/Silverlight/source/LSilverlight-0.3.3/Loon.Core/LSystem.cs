namespace Loon.Core
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework.Content;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Input;
    using Loon.Core.Event;
    using Loon.Core.Geom;
    using Loon.Java;
    using Loon.Utils;

    public class LSystem
    {

        public enum ApplicationType
        {
            Android, JavaSE, XNA, IOS, HTML5
        }

        public static ApplicationType type = ApplicationType.XNA;

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

        public static void Wait(int milis)
        {
            try
            {
                System.Threading.Thread.Sleep(milis);
            }
            catch (Exception)
            {
            }
        }

        public static void Wait()
        {
            try
            {
                System.Threading.Thread.Sleep(1);
            }
            catch (Exception)
            {
            }
        }

        // 行分隔符
        public const string LS = "\n";

        // 文件分割符
        public const string FS = "\\";

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
        static public readonly String FRAMEWORK = "LFX";

        // 框架版本信息
        static public readonly String VERSION = "0.3.3";

        static public LSilverlightPlus screenActivity;

        static public bool isPaused;

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

        static public int MAX_SCREEN_WIDTH = 800;

        static public int MAX_SCREEN_HEIGHT = 480;

        static public bool AUTO_REPAINT = true;

        static public bool SCREEN_LANDSCAPE;
        // 随机数
        static public readonly Random random = new Random();

        public static LProcess screenProcess;

        public static float scaleWidth = 1, scaleHeight = 1;

        public static RectBox screenRect;

        // 最大缓存数量
        public static int DEFAULT_MAX_CACHE_SIZE = 30;

        public static String encoding = "UTF-8";

        public static String FONT_NAME = "Monospaced";

        public static int DEFAULT_MAX_FPS = 60;

        public static bool isLogo;

        public static void SetFPS(int fps)
        {
            if (screenProcess != null)
            {
                screenProcess.SetFPS(fps);
            }
        }

        public static int GetMaxFPS()
        {
            if (screenProcess != null)
            {
                return screenProcess.GetMaxFPS();
            }
            return 0;
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