using loon.canvas;
using loon.opengl;

namespace loon.utils
{
   public class GLUtils
{

		private static int currentBlendMode = -1;

		private static int currentHardwareBufferID = -1;

		private static int currentHardwareTextureID = -1;

		private static int currentSourceBlendMode = -1;

		private static int currentDestinationBlendMode = -1;

		private static bool enableDither = false;

		private static bool enableDepthTest = false;

		private static bool enablecissorTest = false;

		private static bool enableBlend = false;

		private static bool enableCulling = false;

		private static bool enableTextures = false;

		public static int GetBlendMode()
		{
			return currentBlendMode;
		}

		public static void SetBlendMode(GL20 gl, int mode)
		{
			if (currentBlendMode == mode)
			{
				return;
			}
			currentBlendMode = mode;
			if (gl == null)
			{
				return;
			}
			if (currentBlendMode == BlendMethod.MODE_NORMAL)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, false);
				gl.GLBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_SPEED)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, true);
				gl.GLBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_ALPHA_MAP)
			{
				GLUtils.DisableBlend(gl);
				gl.GLColorMask(false, false, false, true);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_ALPHA_BLEND)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, false);
				gl.GLBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_COLOR_MULTIPLY)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, true);
				gl.GLBlendFunc(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_SRC_COLOR);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_ADD)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, true);
				gl.GLBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_SCREEN)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, true);
				gl.GLBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_ALPHA_ONE)
			{
				GLUtils.EnableBlend(gl);
				gl.GLColorMask(true, true, true, true);
				gl.GLBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_ALPHA)
			{
				GLUtils.EnableBlend(gl);
				gl.GLBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_MASK)
			{
				GLUtils.EnableBlend(gl);
				gl.GLBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_LIGHT)
			{
				GLUtils.EnableBlend(gl);
				gl.GLBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_ALPHA_ADD)
			{
				GLUtils.EnableBlend(gl);
				gl.GLBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_MULTIPLY)
			{
				GLUtils.EnableBlend(gl);
				gl.GLBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);
				return;
			}
			else if (currentBlendMode == BlendMethod.MODE_NONE)
			{
				GLUtils.DisableBlend(gl);
				gl.GLColorMask(true, true, true, false);
				return;
			}
			return;
		}

		public static void Reset(GL20 gl)
		{
			GLUtils.Reload();
		}

		public static void Reload()
		{
			GLUtils.currentHardwareBufferID = -1;
			GLUtils.currentHardwareTextureID = -1;
			GLUtils.currentSourceBlendMode = -1;
			GLUtils.currentDestinationBlendMode = -1;
			GLUtils.currentBlendMode = -1;
			GLUtils.enableDither = false;
			GLUtils.enableDepthTest = false;
			GLUtils.enablecissorTest = false;
			GLUtils.enableBlend = false;
			GLUtils.enableCulling = false;
			GLUtils.enableTextures = false;
		}

		public static int NextPOT(int value)
		{
			int bit = 0x8000, highest = -1, count = 0;
			for (int ii = 15; ii >= 0; ii--, bit >>= 1)
			{
				if ((value & bit) == 0)
				{
					continue;
				}
				count++;
				if (highest == -1)
				{
					highest = ii;
				}
			}
			return (count > 1) ? (1 << (highest + 1)) : value;
		}

		public static int PowerOfTwo(int value)
		{
			if (value == 0)
			{
				return 1;
			}
			if ((value & value - 1) == 0)
			{
				return value;
			}
			value |= value >> 1;
			value |= value >> 2;
			value |= value >> 4;
			value |= value >> 8;
			value |= value >> 16;
			return value + 1;
		}

		public static bool IsPowerOfTwo(int value)
		{
			return (value > 0 && (value & (value - 1)) == 0);
		}

		public static bool IsPowerOfTwo(int width, int height)
		{
			return (width > 0 && (width & (width - 1)) == 0 && height > 0 && (height & (height - 1)) == 0);
		}

		public static void SetClearColor(GL20 gl, float r, float g, float b, float a)
		{
			gl.GLClearColor(r, g, b, a);
			gl.GLClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		}

		public static void SetClearColor(GL20 gl, LColor c)
		{
			GLUtils.SetClearColor(gl, c.r, c.g, c.b, c.a);
		}

		public static void EnablecissorTest(GL20 gl)
		{
				if (!GLUtils.enablecissorTest)
				{
					GLUtils.enablecissorTest = true;
					gl.GLEnable(GL20.GL_SCISSOR_TEST);
				}
		}

		public static void DisablecissorTest(GL20 gl)
		{

				if (GLUtils.enablecissorTest)
				{
					GLUtils.enablecissorTest = false;
					gl.GLDisable(GL20.GL_SCISSOR_TEST);
				}
		}
		public static void EnableBlend(GL20 gl)
		{
				if (!GLUtils.enableBlend)
				{
					gl.GLEnable(GL20.GL_BLEND);
					GLUtils.enableBlend = true;
				}
		}

		public static void DisableBlend(GL20 gl)
		{
				if (GLUtils.enableBlend)
				{
					gl.GLDisable(GL20.GL_BLEND);
					GLUtils.enableBlend = false;
				}
		}
		public static void BindTexture(GL20 gl, int hardwareTextureID)
		{
				if (GLUtils.currentHardwareTextureID != hardwareTextureID)
				{
					gl.GLBindTexture(GL20.GL_TEXTURE_2D, hardwareTextureID);
					GLUtils.currentHardwareTextureID = hardwareTextureID;
				}
		}

		public static void DisableCulling(GL20 gl)
		{
				if (GLUtils.enableCulling)
				{
					GLUtils.enableCulling = false;
					gl.GLDisable(GL20.GL_CULL_FACE);
				}
		}

		public static void EnableTextures(GL20 gl)
		{
				if (!GLUtils.enableTextures)
				{
					GLUtils.enableTextures = true;
					gl.GLEnable(GL20.GL_TEXTURE_2D);
				}
		}

		public static void DisableTextures(GL20 gl)
		{
				if (GLUtils.enableTextures)
				{
					GLUtils.enableTextures = false;
					gl.GLDisable(GL20.GL_TEXTURE_2D);
				}
		}

		public static void EnableDither(GL20 gl)
		{
				if (!GLUtils.enableDither)
				{
					GLUtils.enableDither = true;
					gl.GLEnable(GL20.GL_DITHER);
				}
		}
		public static void DisableDither(GL20 gl)
		{
				if (GLUtils.enableDither)
				{
					GLUtils.enableDither = false;
					gl.GLDisable(GL20.GL_DITHER);
				}
		}
		public static void EnableDepthTest(GL20 gl)
		{
				if (!GLUtils.enableDepthTest)
				{
					GLUtils.enableDepthTest = true;
					gl.GLEnable(GL20.GL_DEPTH_TEST);
					gl.GLDepthMask(true);
				}
		}
		public static void DisableDepthTest(GL20 gl)
		{
				if (GLUtils.enableDepthTest)
				{
					GLUtils.enableDepthTest = false;
					gl.GLDisable(GL20.GL_DEPTH_TEST);
					gl.GLDepthMask(false);
				}
		}

		public static void DeleteTexture(GL20 gl, int id)
		{
			gl.GLDeleteTexture(id);
			currentHardwareTextureID = -1;
		}

		public static void BindTexture(GL20 gl, LTexture tex2d)
		{
			if (!tex2d.IsLoaded())
			{
				tex2d.LoadTexture();
			}
			BindTexture(gl, tex2d.ID);
		}

		
		public static void BlendFunction(GL20 gl, int pSourceBlendMode, int pDestinationBlendMode)
		{
				if (GLUtils.currentSourceBlendMode != pSourceBlendMode || GLUtils.currentDestinationBlendMode != pDestinationBlendMode)
				{
					GLUtils.currentSourceBlendMode = pSourceBlendMode;
					GLUtils.currentDestinationBlendMode = pDestinationBlendMode;
					gl.GLBlendFunc(pSourceBlendMode, pDestinationBlendMode);
				}
		}

	}
}
