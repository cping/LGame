package loon.core.graphics.opengl;

import java.nio.FloatBuffer;

import loon.core.graphics.LColor;


/**
 * Copyright 2008 - 2011
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
public class GLUtils {

	private static int currentHardwareBufferID = -1;

	private static int currentHardwareTextureID = -1;

	private static int currentSourceBlendMode = -1;

	private static int currentDestinationBlendMode = -1;

	private static boolean enableDither = true;

	private static boolean enableLightning = true;

	private static boolean enableDepthTest = true;

	private static boolean enableMultisample = true;

	private static boolean enablecissorTest = false;

	private static boolean enableBlend = false;

	private static boolean enableCulling = false;

	private static boolean enableTextures = false;

	private static boolean enableTexCoordArray = false;

	private static boolean enableTexColorArray = false;

	private static boolean enableVertexArray = false;

	private static float red = -1;

	private static float green = -1;

	private static float blue = -1;

	private static float alpha = -1;

	public static void reset(final GL10 gl10) {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentHardwareTextureID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.disableBlend(gl10);
		GLUtils.disableCulling(gl10);
		GLUtils.disableTextures(gl10);
		GLUtils.disableTexCoordArray(gl10);
		GLUtils.disableTexColorArray(gl10);
		GLUtils.disableVertexArray(gl10);
		GLUtils.red = -1;
		GLUtils.green = -1;
		GLUtils.blue = -1;
		GLUtils.alpha = -1;
	}

	public static void reload() {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentHardwareTextureID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.red = -1;
		GLUtils.green = -1;
		GLUtils.blue = -1;
		GLUtils.alpha = -1;
		GLUtils.enableDither = true;
		GLUtils.enableLightning = true;
		GLUtils.enableDepthTest = true;
		GLUtils.enableMultisample = true;
		GLUtils.enablecissorTest = false;
		GLUtils.enableBlend = false;
		GLUtils.enableCulling = false;
		GLUtils.enableTextures = false;
		GLUtils.enableTexCoordArray = false;
		GLUtils.enableTexColorArray = false;
		GLUtils.enableVertexArray = false;
	}

	public static void vertexPointer(GL10 gl10, int size, FloatBuffer buffer) {
		gl10.glVertexPointer(size, GL.GL_FLOAT, 0, buffer);
	}

	public static void setClearColor(final GL10 gl10, float r, float g,
			float b, float a) {
		try {
			if (a != GLUtils.alpha || r != GLUtils.red || g != GLUtils.green
					|| b != GLUtils.blue) {
				GLUtils.alpha = a;
				GLUtils.red = r;
				GLUtils.green = g;
				GLUtils.blue = b;
				gl10.glClearColor(r, g, b, a);
				gl10.glClear(GL.GL_COLOR_BUFFER_BIT
						| GL.GL_DEPTH_BUFFER_BIT);
			}
		} catch (Exception e) {
		}
	}

	public static void setClearColor(final GL10 gl10, LColor c) {
		GLUtils.setClearColor(gl10, c.r, c.g, c.b, c.a);
	}

	public static void setColor(final GL10 gl10, final float r, final float g,
			final float b, final float a) {
		try {
			if (a != GLUtils.alpha || r != GLUtils.red || g != GLUtils.green
					|| b != GLUtils.blue) {
				GLUtils.alpha = a;
				GLUtils.red = r;
				GLUtils.green = g;
				GLUtils.blue = b;
				gl10.glColor4f(r, g, b, a);
			}
		} catch (Exception e) {
		}
	}

	public static void enableVertexArray(final GL10 gl10) {
		try {
			if (!GLUtils.enableVertexArray) {
				GLUtils.enableVertexArray = true;
				gl10.glEnableClientState(GL.GL_VERTEX_ARRAY);
			}
		} catch (Exception e) {
		}
	}

	public static void disableVertexArray(final GL10 gl10) {
		try {
			if (GLUtils.enableVertexArray) {
				GLUtils.enableVertexArray = false;
				gl10.glDisableClientState(GL.GL_VERTEX_ARRAY);
			}
		} catch (Exception e) {
		}
	}

	public static void enableTexCoordArray(final GL10 gl10) {
		try {
			if (!GLUtils.enableTexCoordArray) {
				GLUtils.enableTexCoordArray = true;
				gl10.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			}
		} catch (Exception e) {
		}
	}

	public static void disableTexCoordArray(final GL10 gl10) {
		try {
			if (GLUtils.enableTexCoordArray) {
				GLUtils.enableTexCoordArray = false;
				gl10.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			}
		} catch (Exception e) {
		}
	}

	public static void enableTexColorArray(final GL10 gl10) {
		try {
			if (!GLUtils.enableTexColorArray) {
				GLUtils.enableTexColorArray = true;
				gl10.glEnableClientState(GL.GL_COLOR_ARRAY);
			}
		} catch (Exception e) {
		}
	}

	public static void disableTexColorArray(final GL10 gl10) {
		try {
			if (GLUtils.enableTexColorArray) {
				GLUtils.enableTexColorArray = false;
				gl10.glDisableClientState(GL.GL_COLOR_ARRAY);
			}
		} catch (Exception e) {
		}
	}

	public static void enablecissorTest(final GL10 gl10) {
		try {
			if (!GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = true;
				gl10.glEnable(GL.GL_SCISSOR_TEST);
			}
		} catch (Exception e) {
		}
	}

	public static void disablecissorTest(final GL10 gl10) {
		try {
			if (GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = false;
				gl10.glDisable(GL.GL_SCISSOR_TEST);
			}
		} catch (Exception e) {
		}
	}

	public static void enableBlend(final GL10 gl10) {
		try {
			if (!GLUtils.enableBlend) {
				gl10.glEnable(GL.GL_BLEND);
				GLUtils.enableBlend = true;
			}
		} catch (Exception e) {
		}
	}

	public static void disableBlend(final GL10 gl10) {
		try {
			if (GLUtils.enableBlend) {
				gl10.glDisable(GL.GL_BLEND);
				GLUtils.enableBlend = false;
			}
		} catch (Exception e) {
		}
	}

	public static void disableCulling(final GL10 gl10) {
		try {
			if (GLUtils.enableCulling) {
				GLUtils.enableCulling = false;
				gl10.glDisable(GL.GL_CULL_FACE);
			}
		} catch (Exception e) {
		}
	}

	public static void enableTextures(final GL10 gl10) {
		try {
			if (!GLUtils.enableTextures) {
				GLUtils.enableTextures = true;
				gl10.glEnable(GL.GL_TEXTURE_2D);
			}
		} catch (Exception e) {
		}
	}

	public static void disableTextures(final GL10 gl10) {
		try {
			if (GLUtils.enableTextures) {
				GLUtils.enableTextures = false;
				gl10.glDisable(GL.GL_TEXTURE_2D);
			}
		} catch (Exception e) {
		}
	}

	public static void enableLightning(final GL10 gl10) {
		try {
			if (!GLUtils.enableLightning) {
				GLUtils.enableLightning = true;
				gl10.glEnable(GL.GL_LIGHTING);
			}
		} catch (Exception e) {
		}
	}

	public static void disableLightning(final GL10 gl10) {
		try {
			if (GLUtils.enableLightning) {
				GLUtils.enableLightning = false;
				gl10.glDisable(GL.GL_LIGHTING);
			}
		} catch (Exception e) {
		}
	}

	public static void enableDither(final GL10 gl10) {
		try {
			if (!GLUtils.enableDither) {
				GLUtils.enableDither = true;
				gl10.glEnable(GL.GL_DITHER);
			}
		} catch (Exception e) {
		}
	}

	public static void disableDither(final GL10 gl10) {
		try {
			if (GLUtils.enableDither) {
				GLUtils.enableDither = false;
				gl10.glDisable(GL.GL_DITHER);
			}
		} catch (Exception e) {
		}
	}

	public static void enableDepthTest(final GL10 gl10) {
		try {
			if (!GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = true;
				gl10.glEnable(GL.GL_DEPTH_TEST);
				gl10.glDepthMask(true);
			}
		} catch (Exception e) {
		}
	}

	public static void disableDepthTest(final GL10 gl10) {
		try {
			if (GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = false;
				gl10.glDisable(GL.GL_DEPTH_TEST);
				gl10.glDepthMask(false);
			}
		} catch (Exception e) {
		}
	}

	public static void enableMultisample(final GL10 gl10) {
		try {
			if (!GLUtils.enableMultisample) {
				GLUtils.enableMultisample = true;
				gl10.glEnable(GL.GL_MULTISAMPLE);
			}
		} catch (Exception e) {
		}
	}

	public static void disableMultisample(final GL10 gl10) {
		try {
			if (GLUtils.enableMultisample) {
				GLUtils.enableMultisample = false;
				gl10.glDisable(GL.GL_MULTISAMPLE);
			}
		} catch (Exception e) {
		}
	}

	public static void bindBuffer(final GL11 gl11, final int hardwareBufferID) {
		try {
			if (GLUtils.currentHardwareBufferID != hardwareBufferID) {
				GLUtils.currentHardwareBufferID = hardwareBufferID;
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, hardwareBufferID);
			}
		} catch (Exception e) {
		}
	}

	public static void bindTexture(final GL10 gl10, final int hardwareTextureID) {
		try {
			if (GLUtils.currentHardwareTextureID != hardwareTextureID) {
				GLUtils.currentHardwareTextureID = hardwareTextureID;
				gl10.glBindTexture(GL.GL_TEXTURE_2D, hardwareTextureID);
			}
		} catch (Exception e) {
		}
	}

	public static void texCoordZeroPointer(final GL11 gl11) {
		try {
			gl11.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
		} catch (Exception e) {
		}
	}

	public static void vertexZeroPointer(final GL11 gl11) {
		try {
			gl11.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
		} catch (Exception e) {
		}
	}

	public static void blendFunction(final GL10 gl10,
			final int pSourceBlendMode, final int pDestinationBlendMode) {
		try {
			if (GLUtils.currentSourceBlendMode != pSourceBlendMode
					|| GLUtils.currentDestinationBlendMode != pDestinationBlendMode) {
				GLUtils.currentSourceBlendMode = pSourceBlendMode;
				GLUtils.currentDestinationBlendMode = pDestinationBlendMode;
				gl10.glBlendFunc(pSourceBlendMode, pDestinationBlendMode);
			}
		} catch (Exception e) {
		}
	}

	public static void setShadeModelSmooth(final GL10 gl10) {
		try {
			gl10.glShadeModel(GL.GL_SMOOTH);
		} catch (Exception e) {
		}
	}

	public static void setShadeModelFlat(final GL10 gl10) {
		try {
			gl10.glShadeModel(GL.GL_FLAT);
		} catch (Exception e) {
		}
	}

	public static void setHintFastest(final GL10 gl10) {
		try {
			gl10.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		} catch (Exception e) {
		}
	}

	public static void setHintHicest(final GL10 gl10) {
		try {
			gl10.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		} catch (Exception e) {
		}
	}
}
