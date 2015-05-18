namespace Loon
{
	using System;
	using System.Collections.Generic;
	using System.IO;
	using Loon.Core.Timer;
	using Loon.Core.Geom;
	using Loon.Utils;
	using Loon.Java;
	
	public class LSystem
	{

		
		public static int DEFAULT_MAX_FPS = 60;

		static public int MAX_SCREEN_WIDTH = 480;
		
		static public int MAX_SCREEN_HEIGHT = 320;

		public static RectBox screenRect;

		
		public static void Close(Stream s)
		{
			if (s != null)
			{
				s.Close();
				s = null;
			}
		}

		// 随机数
		static public readonly Random random = new Random();
		
		public static string encoding = "UTF-8";
		
		// 最大缓存数量
		public static int DEFAULT_MAX_CACHE_SIZE = 30;

		public string GetLanguage()
		{
			return System.Globalization.CultureInfo.CurrentCulture.DisplayName;
		}
		
		public enum ApplicationType
		{
			Android, JavaSE, XNA, IOS, HTML5, PSM
		}
		
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
		
		// 包内默认的图片路径
		public const string FRAMEWORK_IMG_NAME = "assets/loon_";
		
		// 行分隔符
		public const string LS = "\n";
		
		// 文件分割符
		public const string FS = "\\";
		
		public static SystemTimer GetSystemTimer()
		{
			return new SystemTimer();
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