package loon.utils;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.GL20;

public class GLUtils {

	private static int currentHardwareBufferID = -1;

	private static int currentHardwareTextureID = -1;

	private static int currentSourceBlendMode = -1;

	private static int currentDestinationBlendMode = -1;

	private static boolean enableDither = true;

	private static boolean enableDepthTest = true;

	private static boolean enablecissorTest = false;

	private static boolean enableBlend = false;

	private static boolean enableCulling = false;

	private static boolean enableTextures = false;

	public static void reset(final GL20 gl) {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentHardwareTextureID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.currentBlendMode = -1;
		GLUtils.disableBlend(gl);
		GLUtils.disableCulling(gl);
		GLUtils.disableTextures(gl);
	}

	public static void reload() {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentHardwareTextureID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.currentBlendMode = -1;
		GLUtils.enableDither = true;
		GLUtils.enableDepthTest = true;
		GLUtils.enablecissorTest = false;
		GLUtils.enableBlend = false;
		GLUtils.enableCulling = false;
		GLUtils.enableTextures = false;
	}

	public static int nextPOT(int value) {
		assert value < 0x10000;
		int bit = 0x8000, highest = -1, count = 0;
		for (int ii = 15; ii >= 0; ii--, bit >>= 1) {
			if ((value & bit) == 0)
				continue;
			count++;
			if (highest == -1)
				highest = ii;
		}
		return (count > 1) ? (1 << (highest + 1)) : value;
	}

	public static int powerOfTwo(int value) {
		if (value == 0) {
			return 1;
		}
		if ((value & value - 1) == 0) {
			return value;
		}
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	private static int currentBlendMode = -1;

	public static final int getBlendMode() {
		return currentBlendMode;
	}

	public static final void setBlendMode(GL20 gl, int mode) {
		if (currentBlendMode == mode) {
			return;
		}
		currentBlendMode = mode;
		if (gl == null) {
			return;
		}
		if (currentBlendMode == LSystem.MODE_NORMAL) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, false);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == LSystem.MODE_SPEED) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == LSystem.MODE_ALPHA_MAP) {
			GLUtils.disableBlend(gl);
			gl.glColorMask(false, false, false, true);
			return;
		} else if (currentBlendMode == LSystem.MODE_ALPHA_BLEND) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, false);
			gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
			return;
		} else if (currentBlendMode == LSystem.MODE_COLOR_MULTIPLY) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_SRC_COLOR);
			return;
		} else if (currentBlendMode == LSystem.MODE_ADD) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == LSystem.MODE_SCREEN) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR);
			return;
		} else if (currentBlendMode == LSystem.MODE_ALPHA_ONE) {
			GLUtils.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == LSystem.MODE_ALPHA) {
			GLUtils.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == LSystem.MODE_NONE) {
			GLUtils.disableBlend(gl);
			gl.glColorMask(true, true, true, false);
			return;
		}
		return;
	}

	public static void setClearColor(final GL20 gl, float r, float g, float b,
			float a) {
		gl.glClearColor(r, g, b, a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	public static void setClearColor(final GL20 gl, LColor c) {
		GLUtils.setClearColor(gl, c.r, c.g, c.b, c.a);
	}

	public static void enablecissorTest(final GL20 gl) {
		try {
			if (!GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = true;
				gl.glEnable(GL20.GL_SCISSOR_TEST);
			}
		} catch (Throwable e) {
		}
	}

	public static void disablecissorTest(final GL20 gl) {
		try {
			if (GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = false;
				gl.glDisable(GL20.GL_SCISSOR_TEST);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableBlend(final GL20 gl) {
		try {
			if (!GLUtils.enableBlend) {
				gl.glEnable(GL20.GL_BLEND);
				GLUtils.enableBlend = true;
			}
		} catch (Throwable e) {
		}
	}

	public static void disableBlend(final GL20 gl) {
		try {
			if (GLUtils.enableBlend) {
				gl.glDisable(GL20.GL_BLEND);
				GLUtils.enableBlend = false;
			}
		} catch (Throwable e) {
		}
	}

	public static void disableCulling(final GL20 gl) {
		try {
			if (GLUtils.enableCulling) {
				GLUtils.enableCulling = false;
				gl.glDisable(GL20.GL_CULL_FACE);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableTextures(final GL20 gl) {
		try {
			if (!GLUtils.enableTextures) {
				GLUtils.enableTextures = true;
				gl.glEnable(GL20.GL_TEXTURE_2D);
			}
		} catch (Throwable e) {
		}
	}

	public static void disableTextures(final GL20 gl) {
		try {
			if (GLUtils.enableTextures) {
				GLUtils.enableTextures = false;
				gl.glDisable(GL20.GL_TEXTURE_2D);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableDither(final GL20 gl) {
		try {
			if (!GLUtils.enableDither) {
				GLUtils.enableDither = true;
				gl.glEnable(GL20.GL_DITHER);
			}
		} catch (Throwable e) {
		}
	}

	public static void disableDither(final GL20 gl) {
		try {
			if (GLUtils.enableDither) {
				GLUtils.enableDither = false;
				gl.glDisable(GL20.GL_DITHER);
			}
		} catch (Throwable e) {
		}
	}

	public static void enableDepthTest(final GL20 gl) {
		try {
			if (!GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = true;
				gl.glEnable(GL20.GL_DEPTH_TEST);
				gl.glDepthMask(true);
			}
		} catch (Throwable e) {
		}
	}

	public static void disableDepthTest(final GL20 gl) {
		try {
			if (GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = false;
				gl.glDisable(GL20.GL_DEPTH_TEST);
				gl.glDepthMask(false);
			}
		} catch (Throwable e) {
		}
	}

	public static void bindBuffer(final GL20 gl, final int hardwareBufferID) {
		try {
			if (GLUtils.currentHardwareBufferID != hardwareBufferID) {
				GLUtils.currentHardwareBufferID = hardwareBufferID;
				gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, hardwareBufferID);
			}
		} catch (Throwable e) {
		}
	}

	public static void bindTexture(final GL20 gl, final int hardwareTextureID) {
		GLUtils.enableTextures(gl);
		try {
			if (GLUtils.currentHardwareTextureID != hardwareTextureID) {
				GLUtils.currentHardwareTextureID = hardwareTextureID;
				gl.glBindTexture(GL20.GL_TEXTURE_2D, hardwareTextureID);
			}
		} catch (Throwable e) {
		}
	}

	public static void deleteTexture(GL20 gl, int id) {
		gl.glDeleteTexture(id);
		currentHardwareTextureID = -1;
	}

	public static void bindTexture(GL20 gl, LTexture tex2d) {
		if (!tex2d.isLoaded()) {
			tex2d.loadTexture();
		}
		bindTexture(gl, tex2d.getID());
	}

	public static void blendFunction(final GL20 gl, final int pSourceBlendMode,
			final int pDestinationBlendMode) {
		try {
			if (GLUtils.currentSourceBlendMode != pSourceBlendMode
					|| GLUtils.currentDestinationBlendMode != pDestinationBlendMode) {
				GLUtils.currentSourceBlendMode = pSourceBlendMode;
				GLUtils.currentDestinationBlendMode = pDestinationBlendMode;
				gl.glBlendFunc(pSourceBlendMode, pDestinationBlendMode);
			}
		} catch (Throwable e) {
		}
	}

}
