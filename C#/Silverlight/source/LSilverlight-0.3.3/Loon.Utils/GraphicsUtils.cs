using System;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core.Graphics;
using Loon.Core;

namespace Loon.Utils
{
    public class GraphicsUtils
    {

        public static LPixmap GetResize(LPixmap image, int w, int h)
        {
            if (image == null)
            {
                return null;
            }
            if (image.Width == w && image.Height == h)
            {
                return image;
            }
            LPixmap result = new LPixmap(w, h, image.IsAlpha());
            result.DrawPixmap(image, 0, 0, w, h, 0, 0, image.GetWidth(),
            image.GetHeight());
            return result;
        }

        public static LPixmap DrawClipImage(LPixmap image,
            int objectWidth, int objectHeight, int x1, int y1, int x2, int y2)
        {
            LPixmap buffer = new LPixmap(objectWidth,
            objectHeight, true);
            buffer.DrawPixmap(image, 0, 0, objectWidth, objectHeight, x1, y1,
            x2 - x1, y2 - y1);
            return buffer;
        }

        public static LPixmap DrawClipImage(LPixmap image,
            int objectWidth, int objectHeight, int x, int y)
        {
            LPixmap buffer = new LPixmap(objectWidth,
            objectHeight, true);
            buffer.DrawPixmap(image, 0, 0, objectWidth, objectHeight, x, y, objectWidth, objectHeight);
            return buffer;
        }

        public static LPixmap DrawCropImage(LPixmap image, int x, int y,
            int objectWidth, int objectHeight)
        {

            LPixmap buffer = new LPixmap(objectWidth,
            objectHeight, true);
            buffer.DrawPixmap(image, 0, 0, objectWidth, objectHeight, x, y, objectWidth, objectHeight);
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
