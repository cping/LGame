namespace Loon.Action.Avg
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework;
    using Loon.Core.Graphics.Opengl;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Core;
    using Loon.Core.Graphics.Device;

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
            LImage dialog = LImage.CreateImage(fileName);
            int w = dialog.GetWidth();
            Color[] pixels = dialog.GetPixels();
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
            return GetRMXPloadBuoyage(LImage.CreateImage(fileName), width,
                    height);
        }

        public static LTexture GetRMXPloadBuoyage(LImage rmxpImage,
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
                LImage lazyImage;
                LImage image, left, right, center, up, down = null;
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
                    lazyImage = LImage.CreateImage(width, height, true);
                    LGraphics g = lazyImage.GetLGraphics(); 

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
                    g.DrawImage(center, 0, 0);
                    g.DrawImage(left, 0, 0);
                    g.DrawImage(right, width - k, 0);
                    g.DrawImage(up, 0, 0);
                    g.DrawImage(down, 0, height - k);
                    g.Dispose();

                    lazy = lazyImage.GetTexture();

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

        private static LTexture GetRMXPDialog(LImage rmxpImage, int width,
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

                    LImage lazyImage = null;

                    LImage image = null;

                    LImage messageImage = null;

                    image = GraphicsUtils.DrawClipImage(rmxpImage, objWidth,
                            objHeight, x1, y1, x2, y2);

                    LImage centerTop = GraphicsUtils.DrawClipImage(image,
                            center_size, size, size, 0);

                    LImage centerDown = GraphicsUtils.DrawClipImage(image,
                            center_size, size, size, objHeight - size);

                    LImage leftTop = GraphicsUtils.DrawClipImage(image, size, size,
                            0, 0);

                    LImage leftCenter = GraphicsUtils.DrawClipImage(image, size,
                            center_size, 0, size);

                    LImage leftDown = GraphicsUtils.DrawClipImage(image, size,
                            size, 0, objHeight - size);

                    LImage rightTop = GraphicsUtils.DrawClipImage(image, size,
                            size, objWidth - size, 0);

                    LImage rightCenter = GraphicsUtils.DrawClipImage(image, size,
                            center_size, objWidth - size, size);

                    LImage rightDown = GraphicsUtils.DrawClipImage(image, size,
                            size, objWidth - size, objHeight - size);

                    lazyImage = centerTop;

                    lazyImage = LImage.CreateImage(width, height, true);

                    LGraphics g = lazyImage.GetLGraphics();

                    g.SetAlpha(0.5f);

                    messageImage = GraphicsUtils.DrawClipImage(rmxpImage, 128, 128,
                            0, 0, 128, 128);

                    messageImage = GraphicsUtils.GetResize(messageImage, width
                            - offset, height - offset);
                    messageImage.XNAUpdateAlpha(125);
 
                    g.DrawImage(messageImage, (lazyImage.Width - messageImage.Width) / 2 + 1, (lazyImage.Height - messageImage
                            .Height) / 2 + 1);

                    LImage tmp = GraphicsUtils.GetResize(centerTop, width
                            - (size * 2), size);

                    g.DrawImage(tmp, size, 0);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }

                    tmp = GraphicsUtils.GetResize(centerDown, width - (size * 2),
                            size);

                    g.DrawImage(tmp, size, height - size);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }

                    g.DrawImage(leftTop, 0, 0);

                    tmp = GraphicsUtils.GetResize(leftCenter,
                            leftCenter.GetWidth(), width - (size * 2));

                    g.DrawImage(tmp, 0, size);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }
                    g.DrawImage(leftDown, 0, height - size);

                    int right = width - size;

                    g.DrawImage(rightTop, right, 0);

                    tmp = GraphicsUtils.GetResize(rightCenter, leftCenter
                            .Width, width - (size * 2));

                    g.DrawImage(tmp, right, size);
                    if (tmp != null)
                    {
                        tmp.Dispose();
                        tmp = null;
                    }
                    g.DrawImage(rightDown, right, height - size);
                    g.Dispose();
                    lazy = lazyImage.GetTexture();
               
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
                catch(Exception ex)
                {
                    Loon.Utils.Debugging.Log.Exception(ex);

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
