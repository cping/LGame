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
package loon.core.graphics.opengl;

import loon.core.graphics.device.LColor;

public class GLUtils {

	private static int currentHardwareBufferID = -1;

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

	public static void reset(final GL20 gl10) {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.disableBlend(gl10);
		GLUtils.disableCulling(gl10);
		GLUtils.disableTextures(gl10);
	}

	public static void reload() {
		GLUtils.currentHardwareBufferID = -1;
		GLUtils.currentSourceBlendMode = -1;
		GLUtils.currentDestinationBlendMode = -1;
		GLUtils.enableDither = true;
		GLUtils.enableLightning = true;
		GLUtils.enableDepthTest = true;
		GLUtils.enableMultisample = true;
		GLUtils.enablecissorTest = false;
		GLUtils.enableBlend = false;
		GLUtils.enableCulling = false;
		GLUtils.enableTextures = false;
	}

	public static void setClearColor(final GL20 gl10, float r, float g,
			float b, float a) {
		gl10.glClearColor(r, g, b, a);
		gl10.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	public static void setClearColor(final GL20 gl10, LColor c) {
		GLUtils.setClearColor(gl10, c.r, c.g, c.b, c.a);
	}

	public static void enablecissorTest(final GL20 gl10) {
		try {
			if (!GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = true;
				gl10.glEnable(GL20.GL_SCISSOR_TEST);
			}
		} catch (Exception e) {
		}
	}

	public static void disablecissorTest(final GL20 gl10) {
		try {
			if (GLUtils.enablecissorTest) {
				GLUtils.enablecissorTest = false;
				gl10.glDisable(GL20.GL_SCISSOR_TEST);
			}
		} catch (Exception e) {
		}
	}

	public static void enableBlend(final GL20 gl10) {
		try {
			if (!GLUtils.enableBlend) {
				gl10.glEnable(GL20.GL_BLEND);
				GLUtils.enableBlend = true;
			}
		} catch (Exception e) {
		}
	}

	public static void disableBlend(final GL20 gl10) {
		try {
			if (GLUtils.enableBlend) {
				gl10.glDisable(GL20.GL_BLEND);
				GLUtils.enableBlend = false;
			}
		} catch (Exception e) {
		}
	}

	public static void disableCulling(final GL20 gl10) {
		try {
			if (GLUtils.enableCulling) {
				GLUtils.enableCulling = false;
				gl10.glDisable(GL20.GL_CULL_FACE);
			}
		} catch (Exception e) {
		}
	}

	public static void enableTextures(final GL20 gl10) {
		try {
			if (!GLUtils.enableTextures) {
				GLUtils.enableTextures = true;
				gl10.glEnable(GL20.GL_TEXTURE_2D);
			}
		} catch (Exception e) {
		}
	}

	public static void disableTextures(final GL20 gl10) {
		try {
			if (GLUtils.enableTextures) {
				GLUtils.enableTextures = false;
				gl10.glDisable(GL20.GL_TEXTURE_2D);
			}
		} catch (Exception e) {
		}
	}

	public static void enableLightning(final GL20 gl) {
		try {
			if (!GLUtils.enableLightning) {
				GLUtils.enableLightning = true;
				gl.glEnable(GL.GL_LIGHTING);
			}
		} catch (Exception e) {
		}
	}

	public static void disableLightning(final GL20 gl) {
		try {
			if (GLUtils.enableLightning) {
				GLUtils.enableLightning = false;
				gl.glDisable(GL.GL_LIGHTING);
			}
		} catch (Exception e) {
		}
	}

	public static void enableDither(final GL20 gl10) {
		try {
			if (!GLUtils.enableDither) {
				GLUtils.enableDither = true;
				gl10.glEnable(GL20.GL_DITHER);
			}
		} catch (Exception e) {
		}
	}

	public static void disableDither(final GL20 gl10) {
		try {
			if (GLUtils.enableDither) {
				GLUtils.enableDither = false;
				gl10.glDisable(GL20.GL_DITHER);
			}
		} catch (Exception e) {
		}
	}

	public static void enableDepthTest(final GL20 gl10) {
		try {
			if (!GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = true;
				gl10.glEnable(GL20.GL_DEPTH_TEST);
				gl10.glDepthMask(true);
			}
		} catch (Exception e) {
		}
	}

	public static void disableDepthTest(final GL20 gl10) {
		try {
			if (GLUtils.enableDepthTest) {
				GLUtils.enableDepthTest = false;
				gl10.glDisable(GL20.GL_DEPTH_TEST);
				gl10.glDepthMask(false);
			}
		} catch (Exception e) {
		}
	}

	public static void enableMultisample(final GL20 gl) {
		try {
			if (!GLUtils.enableMultisample) {
				GLUtils.enableMultisample = true;
				gl.glEnable(GL.GL_MULTISAMPLE);
			}
		} catch (Exception e) {
		}
	}

	public static void disableMultisample(final GL20 gl) {
		try {
			if (GLUtils.enableMultisample) {
				GLUtils.enableMultisample = false;
				gl.glDisable(GL.GL_MULTISAMPLE);
			}
		} catch (Exception e) {
		}
	}

	public static void bindBuffer(final GL20 gl11, final int hardwareBufferID) {
		try {
			if (GLUtils.currentHardwareBufferID != hardwareBufferID) {
				GLUtils.currentHardwareBufferID = hardwareBufferID;
				gl11.glBindBuffer(GL20.GL_ARRAY_BUFFER, hardwareBufferID);
			}
		} catch (Exception e) {
		}
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
		} catch (Exception e) {
		}
	}

}
