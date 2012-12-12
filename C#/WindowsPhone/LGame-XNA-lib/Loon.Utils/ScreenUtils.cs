using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Loon.Core.Graphics.Opengl;
using Microsoft.Xna.Framework;
using Loon.Core;
using Loon.Core.Graphics;

namespace Loon.Utils
{
    public class ScreenUtils
    {
        public static LImage ToScreenCaptureTexturePixmap()
        {
            Texture2D screen = ToScreenCaptureTexture2D();
            return screen == null ? null : LImage.CreateImage(screen);
        }

        public static LTexture ToScreenCaptureTexture()
        {
            Texture2D screen = ToScreenCaptureTexture2D();
            return screen == null ? null : LImage.CreateImage(screen).GetTexture();
        }

        public static Texture2D ToScreenCaptureTexture2D()
        {
            if (LSystem.screenActivity != null)
            {
                RenderTarget2D render = new RenderTarget2D(GL.device, GL.device.PresentationParameters.BackBufferWidth, GL.device.PresentationParameters.BackBufferHeight, false, GL.device.PresentationParameters.BackBufferFormat, GL.device.PresentationParameters.DepthStencilFormat);

                GL.device.SetRenderTarget(render);

                LSystem.screenActivity.RequestRender();

                GL.device.SetRenderTarget(null);

                return render;
            }
            else
            {
                return null;
            }
        }
  
    }

}
