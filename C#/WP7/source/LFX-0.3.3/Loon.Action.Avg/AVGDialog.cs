namespace Loon.Action.Avg
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Core;

    public class AVGDialog
    {

        private static Dictionary<string, LTexture> lazyImages;

        public static LTexture GetRMXPDialog(string fileName, int width,
                int height)
        {
            if (lazyImages == null)
            {
                lazyImages = new Dictionary<string, LTexture>(10);
            }
            LPixmap dialog = new LPixmap(fileName);
            int w = dialog.Width;
            Color[] pixels = dialog.GetData();
            int index = -1;
            int count = 0;
            uint pixel;
            for (int i = 0; i < 5; i++)
            {
                pixel = pixels[(141 + i) + w * 12].PackedValue;

                if (index == -1)
                {
                    index = (int)pixel;
                }
                if (index == pixel)
                {
                    count++;
                }
            }
            if (count == 5)
            {
                return GetRMXPDialog(dialog, width, height, 16, 5);
            }
            else if (count == 1)
            {
                return GetRMXPDialog(dialog, width, height, 27, 5);
            }
            else if (count == 2)
            {
                return GetRMXPDialog(dialog, width, height, 20, 5);
            }
            else
            {
                return GetRMXPDialog(dialog, width, height, 27, 5);
            }
        }

        public static LTexture GetRMXPloadBuoyage(string fileName, int width,
                int height)
        {
            return GetRMXPloadBuoyage(new LPixmap(fileName), width,
                    height);
        }

        public static LTexture GetRMXPloadBuoyage(LPixmap rmxpImage,
                int width, int height)
        {
            if (lazyImages == null)
            {
                lazyImages = new Dictionary<string, LTexture>(10);
            }
            string keyName = ("buoyage" + width + "|" + height);
            LTexture lazy = (LTexture)CollectionUtils.Get(lazyImages, keyName);
            if (lazy == null)
            {
                LPixmap lazyImage;
                LPixmap image, left, right, center, up, down = null;
                int objWidth = 32;
                int objHeight = 32;
                int x1 = 128;
                int x2 = 160;
                int y1 = 64;
                int y2 = 96;
                int k = 1;

                try
                {
                    image = GraphicsUtils.DrawClipImage(rmxpImage, objWidth,
                            objHeight, x1, y1, x2, y2);
                    lazyImage = new LPixmap(width, height, true);
                    left = GraphicsUtils.DrawClipImage(image, k, height, 0, 0, k,
                            objHeight);
                    right = GraphicsUtils.DrawClipImage(image, k, height, objWidth
                            - k, 0, objWidth, objHeight);
                    center = GraphicsUtils.DrawClipImage(image, width, height, k,
                            k, objWidth - k, objHeight - k);
                    up = GraphicsUtils.DrawClipImage(image, width, k, 0, 0,
                            objWidth, k);
                    down = GraphicsUtils.DrawClipImage(image, width, k, 0,
                            objHeight - k, objWidth, objHeight);
                    lazyImage.DrawPixmap(center, 0, 0);
                    lazyImage.DrawPixmap(left, 0, 0);
                    lazyImage.DrawPixmap(right, width - k, 0);
                    lazyImage.DrawPixmap(up, 0, 0);
                    lazyImage.DrawPixmap(down, 0, height - k);


                    lazy = lazyImage.Texture;

                    if (lazyImage != null)
                    {
                        lazyImage.Dispose();
                        lazyImage = null;
                    }

                    lazyImages.Add(keyName, lazy);
                }
                catch
                {
                    return null;
                }
                finally
                {
                    left = null;
                    right = null;
                    center = null;
                    up = null;
                    down = null;
                    image = null;
                }
            }
            return lazy;

        }

        private static LTexture GetRMXPDialog(LPixmap rmxpImage, int width,
                int height, int size, int offset)
        {
            if (lazyImages == null)
            {
                lazyImages = new Dictionary<string, LTexture>(10);
            }
            string keyName = "dialog" + width + "|" + height;
            LTexture lazy = (LTexture)CollectionUtils.Get(lazyImages, keyName);
            if (lazy == null)
            {
                try
                {
                    int objWidth = 64;
                    int objHeight = 64;
                    int x1 = 128;
                    int x2 = 192;
                    int y1 = 0;
                    int y2 = 64;

                    int center_size = objHeight - size * 2;

                    LPixmap lazyImage = null;

                    LPixmap image = null;

                    LPixmap messageImage = null;

                    image = GraphicsUtils.DrawClipImage(rmxpImage, objWidth,
                            objHeight, x1, y1, x2, y2);

                    LPixmap centerTop = GraphicsUtils.DrawClipImage(image,
                            center_size, size, size, 0);

                    LPixmap centerDown = GraphicsUtils.DrawClipImage(image,
                            center_size, size, size, objHeight - size);

                    LPixmap leftTop = GraphicsUtils.DrawClipImage(image, size, size,
                            0, 0);

                    LPixmap leftCenter = GraphicsUtils.DrawClipImage(image, size,
                            center_size, 0, size);

                    LPixmap leftDown = GraphicsUtils.DrawClipImage(image, size,
                            size, 0, objHeight - size);

                    LPixmap rightTop = GraphicsUtils.DrawClipImage(image, size,
                            size, objWidth - size, 0);

                    LPixmap rightCenter = GraphicsUtils.DrawClipImage(image, size,
                            center_size, objWidth - size, size);

                    LPixmap rightDown = GraphicsUtils.DrawClipImage(image, size,
                            size, objWidth - size, objHeight - size);
                    lazyImage = centerTop;

                    lazyImage = new LPixmap(width, height, true);

                    messageImage = GraphicsUtils.DrawClipImage(rmxpImage, 128, 128,
                            0, 0, 128, 128);

                    messageImage = GraphicsUtils.GetResize(messageImage, width
                            - offset + 1, height - offset + 1);
                    messageImage.UpdateAlpha(125);

                    lazyImage.DrawPixmap(messageImage, (lazyImage.Width - messageImage.Width) / 2, (lazyImage.Height - messageImage
                            .Height) / 2);

                    LPixmap tmp = GraphicsUtils.GetResize(centerTop, width
                            - (size * 2), size);

                    lazyImage.DrawPixmap(tmp, size, 0);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }

                    tmp = GraphicsUtils.GetResize(centerDown, width - (size * 2),
                            size);

                    lazyImage.DrawPixmap(tmp, size, height - size);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }

                    lazyImage.DrawPixmap(leftTop, 0, 0);

                    tmp = GraphicsUtils.GetResize(leftCenter,
                            leftCenter.GetWidth(), width - (size * 2));

                    lazyImage.DrawPixmap(tmp, 0, size);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }
                    lazyImage.DrawPixmap(leftDown, 0, height - size);

                    int right = width - size;

                    lazyImage.DrawPixmap(rightTop, right, 0);

                    tmp = GraphicsUtils.GetResize(rightCenter, leftCenter
                            .Width, width - (size * 2));

                    lazyImage.DrawPixmap(tmp, right, size);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }
                    lazyImage.DrawPixmap(rightDown, right, height - size);

                    lazy = lazyImage.Texture;
                    lazy.isExt = true;

                    lazyImages.Add(keyName, lazy);

                    image.Dispose();
                    messageImage.Dispose();
                    centerTop.Dispose();
                    centerDown.Dispose();
                    leftTop.Dispose();
                    leftCenter.Dispose();
                    leftDown.Dispose();
                    rightTop.Dispose();
                    rightCenter.Dispose();
                    rightDown.Dispose();

                    image = null;
                    messageImage = null;
                    centerTop = null;
                    centerDown = null;
                    leftTop = null;
                    leftCenter = null;
                    leftDown = null;
                    rightTop = null;
                    rightCenter = null;
                    rightDown = null;
                }
                catch
                {

                }
            }
            return lazy;
        }

        public static void Clear()
        {
            foreach (LTexture tex2d in lazyImages.Values)
            {
                if (tex2d != null)
                {
                    tex2d.Destroy();
                }
            }
            lazyImages.Clear();
        }
    }
}
