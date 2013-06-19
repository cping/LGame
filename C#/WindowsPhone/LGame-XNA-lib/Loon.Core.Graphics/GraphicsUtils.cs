using System;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core.Graphics;
using Loon.Core;
using Loon.Core.Graphics.Device;

namespace Loon.Core.Graphics
{
    public class GraphicsUtils
    {


        public static LImage GetResize(LImage image, int w, int h)
        {
            if (image == null)
            {
                return null;
            }
            if (image.width == w && image.height == h)
            {
                return image;
            }
            LImage result = LImage.CreateImage(w, h, image.HasAlpha());
            LGraphics g = result.GetLGraphics();
            g.DrawImage(image, 0, 0, w, h, 0, 0, image.GetWidth(),
            image.GetHeight());
            g.Dispose();
            return result;
        }

        public static LImage DrawClipImage(LImage image,
            int objectWidth, int objectHeight, int x1, int y1, int x2, int y2)
        {
            LImage buffer = LImage.CreateImage(objectWidth,
            objectHeight, true);
            LGraphics g = buffer.GetLGraphics();
            g.DrawImage(image, 0, 0, objectWidth, objectHeight, x1, y1,
            x2 - x1, y2 - y1);
            g.Dispose();
            return buffer;
        }

        public static LImage DrawClipImage(LImage image,
            int objectWidth, int objectHeight, int x, int y)
        {
            LImage buffer = LImage.CreateImage(objectWidth,
            objectHeight, true);
            LGraphics g = buffer.GetLGraphics();
            g.DrawImage(image, 0, 0, objectWidth, objectHeight, x, y, objectWidth, objectHeight);
            g.Dispose();
            return buffer;
        }

        public static LImage DrawCropImage(LImage image, int x, int y,
            int objectWidth, int objectHeight)
        {

            LImage buffer = LImage.CreateImage(objectWidth,
            objectHeight, true);
            LGraphics g = buffer.GetLGraphics();
            g.DrawImage(image, 0, 0, objectWidth, objectHeight, x, y, objectWidth, objectHeight);
            g.Dispose();
            return buffer;
        }

        public static RectBox FitLimitSize(float srcWidth, float srcHeight,
            float dstWidth, float dstHeight)
        {
            float dw = dstWidth;
            float dh = dstHeight;
            if (dw != 0 && dh != 0)
            {
                float waspect = dw / srcWidth;
                float haspect = dh / srcHeight;
                if (waspect > haspect)
                {
                    dw = (srcWidth * haspect);
                }
                else
                {
                    dh = (srcHeight * waspect);
                }
            }
            return new RectBox(0, 0, dw, dh);
        }
    }

}
