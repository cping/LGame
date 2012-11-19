using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Loon.Core.Graphics.OpenGL;
using Microsoft.Xna.Framework;
using Loon.Core;
using Loon.Core.Graphics;

namespace Loon.Utils
{
    public class ScreenUtils
    {
        public static LPixmap ToScreenCaptureTexturePixmap()
        {
            LSilverlightPlus plus = LSystem.screenActivity;
            if (plus != null)
            {
                return new LPixmap(ToScreenCaptureTexture());
            }
            else
            {
                return null;
            }
        }

        public static LTexture ToScreenCaptureTexture()
        {
            Texture2D screen = ToScreenCaptureTexture2D();
            return screen == null ? null : new LTexture(screen);
        }

        public static Texture2D ToScreenCaptureTexture2D()
        {
            LSilverlightPlus plus = LSystem.screenActivity;
            if (plus != null)
            {
                RenderTarget2D render = new RenderTarget2D(GLEx.Device, GLEx.Device.PresentationParameters.BackBufferWidth, GLEx.Device.PresentationParameters.BackBufferHeight, false, GLEx.Device.PresentationParameters.BackBufferFormat, GLEx.Device.PresentationParameters.DepthStencilFormat);

                GLEx.Device.SetRenderTarget(render);

                plus.DrawXNA();

                GLEx.Device.SetRenderTarget(null);

                return render;
            }
            else
            {
                return null;
            }
        }
  
    }

}
