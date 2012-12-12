using Loon.Core.Graphics.Opengl;
using System;
using Loon.Core.Graphics;
namespace Loon.Utils
{
    public class GLUtils
    {

        private static int currentHardwareTextureID = -1;

        private static int currentSourceBlendMode = -1;

        private static int currentDestinationBlendMode = -1;

        private static bool enableDither = true;

        private static bool enableLightning = true;

        private static bool enableDepthTest = true;

        private static bool enablecissorTest = false;

        private static bool enableBlend = false;

        private static bool enableCulling = false;

        private static bool enableTextures = false;

        private static bool enableTexCoordArray = false;

        private static bool enableTexColorArray = false;

        private static bool enableVertexArray = false;

        private static float red = -1;

        private static float green = -1;

        private static float blue = -1;

        private static float alpha = -1;


        public static void Reset(GL gl)
        {
            GLUtils.currentHardwareTextureID = -1;
            GLUtils.currentSourceBlendMode = -1;
            GLUtils.currentDestinationBlendMode = -1;
            GLUtils.DisableBlend(gl);
            GLUtils.DisableCulling(gl);
            GLUtils.DisableTextures(gl);
            GLUtils.DisableTexCoordArray(gl);
            GLUtils.DisableTexColorArray(gl);
            GLUtils.DisableVertexArray(gl);
            GLUtils.red = -1;
            GLUtils.green = -1;
            GLUtils.blue = -1;
            GLUtils.alpha = -1;
        }


        public static void SetClearColor(GL gl10, LColor c)
        {
            GLUtils.SetColor(gl10, c.r, c.g, c.b, c.a);
        }

        public static void SetColor(GL gl10, float r, float g,
                 float b, float a)
        {
            if (a != GLUtils.alpha || r != GLUtils.red || g != GLUtils.green
                    || b != GLUtils.blue)
            {
                GLUtils.alpha = a;
                GLUtils.red = r;
                GLUtils.green = g;
                GLUtils.blue = b;
                gl10.GLColor4f(r, g, b, a);
            }
        }

        public static void EnableVertexArray(GL gl10)
        {
            if (!GLUtils.enableVertexArray)
            {
                GLUtils.enableVertexArray = true;
                gl10.GLEnableClientState(GL.GL_VERTEX_ARRAY);
            }
        }

        public static void DisableVertexArray(GL gl10)
        {
            if (GLUtils.enableVertexArray)
            {
                GLUtils.enableVertexArray = false;
                gl10.GLDisableClientState(GL.GL_VERTEX_ARRAY);
            }
        }

        public static void Reload()
        {
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
            GLUtils.enablecissorTest = false;
            GLUtils.enableBlend = false;
            GLUtils.enableCulling = false;
            GLUtils.enableTextures = false;
            GLUtils.enableTexCoordArray = false;
            GLUtils.enableTexColorArray = false;
            GLUtils.enableVertexArray = false;
        }

        public static void EnableTexCoordArray(GL gl10)
        {
            if (!GLUtils.enableTexCoordArray)
            {
                GLUtils.enableTexCoordArray = true;
                gl10.GLEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            }
        }

        public static void DisableTexCoordArray(GL gl10)
        {
            if (GLUtils.enableTexCoordArray)
            {
                GLUtils.enableTexCoordArray = false;
                gl10.GLDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            }
        }

        public static void EnableTexColorArray(GL gl10)
        {
            if (!GLUtils.enableTexColorArray)
            {
                GLUtils.enableTexColorArray = true;
                gl10.GLEnableClientState(GL.GL_COLOR_ARRAY);
            }
        }

        public static void DisableTexColorArray(GL gl10)
        {
            if (GLUtils.enableTexColorArray)
            {
                GLUtils.enableTexColorArray = false;
                gl10.GLDisableClientState(GL.GL_COLOR_ARRAY);
            }
        }

        public static void EnablecissorTest(GL gl10)
        {
            if (!GLUtils.enablecissorTest)
            {
                GLUtils.enablecissorTest = true;
                gl10.GLEnable(GL.GL_SCISSOR_TEST);
            }
        }

        public static void DisablecissorTest(GL gl10)
        {
            if (GLUtils.enablecissorTest)
            {
                GLUtils.enablecissorTest = false;
                gl10.GLDisable(GL.GL_SCISSOR_TEST);
            }
        }

        public static void EnableBlend(GL gl10)
        {
            if (!GLUtils.enableBlend)
            {
                gl10.GLEnable(GL.GL_BLEND);
                GLUtils.enableBlend = true;
            }
        }

        public static void DisableBlend(GL gl10)
        {
            if (GLUtils.enableBlend)
            {
                gl10.GLDisable(GL.GL_BLEND);
                GLUtils.enableBlend = false;
            }
        }

        public static void DisableCulling(GL gl10)
        {
            if (GLUtils.enableCulling)
            {
                GLUtils.enableCulling = false;
                gl10.GLDisable(GL.GL_CULL_FACE);
            }
        }

        public static void EnableTextures(GL gl10)
        {
            if (!GLUtils.enableTextures)
            {
                GLUtils.enableTextures = true;
                gl10.GLEnable(GL.GL_TEXTURE_2D);
            }
        }

        public static void DisableTextures(GL gl10)
        {
            if (GLUtils.enableTextures)
            {
                GLUtils.enableTextures = false;
                gl10.GLDisable(GL.GL_TEXTURE_2D);
            }
        }

        public static void EnableLightning(GL gl10)
        {
            if (!GLUtils.enableLightning)
            {
                GLUtils.enableLightning = true;
                gl10.GLEnable(GL.GL_LIGHTING);
            }
        }

        public static void DisableLightning(GL gl10)
        {
            if (GLUtils.enableLightning)
            {
                GLUtils.enableLightning = false;
                gl10.GLDisable(GL.GL_LIGHTING);
            }
        }

        public static void EnableDither(GL gl10)
        {
            if (!GLUtils.enableDither)
            {
                GLUtils.enableDither = true;
                gl10.GLEnable(GL.GL_DITHER);
            }
        }

        public static void DisableDither(GL gl10)
        {
            if (GLUtils.enableDither)
            {
                GLUtils.enableDither = false;
                gl10.GLDisable(GL.GL_DITHER);
            }
        }

        public static void EnableDepthTest(GL gl10)
        {
            if (!GLUtils.enableDepthTest)
            {
                GLUtils.enableDepthTest = true;
                gl10.GLEnable(GL.GL_DEPTH_TEST);
                gl10.GLDepthMask(true);
            }
        }

        public static void DisableDepthTest(GL gl10)
        {
            if (GLUtils.enableDepthTest)
            {
                GLUtils.enableDepthTest = false;
                gl10.GLDisable(GL.GL_DEPTH_TEST);
                gl10.GLDepthMask(false);
            }
        }

        public static void BindTexture(GL gl10, int hardwareTextureID)
        {
            if (GLUtils.currentHardwareTextureID != hardwareTextureID)
            {
                GLUtils.currentHardwareTextureID = hardwareTextureID;
                gl10.GLBindTexture(GL.GL_TEXTURE_2D, hardwareTextureID);
            }
        }

        public static void BlendFunction(GL gl10,
                 int pSourceBlendMode, int pDestinationBlendMode)
        {
            if (GLUtils.currentSourceBlendMode != pSourceBlendMode
                    || GLUtils.currentDestinationBlendMode != pDestinationBlendMode)
            {
                GLUtils.currentSourceBlendMode = pSourceBlendMode;
                GLUtils.currentDestinationBlendMode = pDestinationBlendMode;
                gl10.GLBlendFunc(pSourceBlendMode, pDestinationBlendMode);
            }
        }

        public static void SetShadeModelSmooth(GL gl10)
        {
            gl10.GLShadeModel(GL.GL_SMOOTH);
        }

        public static void SetShadeModelFlat(GL gl10)
        {
            gl10.GLShadeModel(GL.GL_FLAT);
        }

        public static void SetHintFastest(GL gl10)
        {
            gl10.GLHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
        }


    }
}
