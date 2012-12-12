namespace Loon.Action.Sprite
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework;
    using Loon.Core.Graphics.Opengl;
    using Loon.Utils;
    using Loon.Core.Graphics;
    using Loon.Core;

    public class AnimationHelper
    {

        private static readonly Dictionary<string, AnimationHelper> animations = new Dictionary<string, AnimationHelper>();

        // 角色向下
        public LTexture[] downImages;

        // 角色向上
        public LTexture[] upImages;

        // 角色向左
        public LTexture[] leftImages;

        // 角色向右
        public LTexture[] rightImages;

        /// <summary>
        /// 以RMVX的角色格式创建对象(总图大小96x128，每格大小32x32)
        /// </summary>
        ///
        /// <param name="fileName"></param>
        /// <returns></returns>
        public static AnimationHelper MakeRMVXObject(string fileName)
        {
            return MakeObject(fileName, 4, 3, 32, 32);
        }

        /// <summary>
        /// 以RMXP的角色格式创建对象(总图大小128x192，每格大小32x48)
        /// </summary>
        ///
        /// <param name="fileName"></param>
        /// <returns></returns>
        public static AnimationHelper MakeRMXPObject(string fileName)
        {
            return MakeObject(fileName, 4, 4, 32, 48);
        }

        /// <summary>
        /// 以E社的角色格式创建对象(总图大小200x200，每格大小40x50)
        /// </summary>
        ///
        /// <param name="fileName"></param>
        /// <returns></returns>
        public static AnimationHelper MakeEObject(string fileName)
        {
            return MakeObject(fileName, 40, 50, LColor.green);
        }

        /// <summary>
        /// 以RMVX的角色格式创建分解头象
        /// </summary>
        ///
        /// <param name="fileName"></param>
        /// <returns></returns>
        public static LTexture[] MakeFace(string fileName)
        {
            return TextureUtils.GetSplitTextures(fileName, 96, 96);
        }

        /// <summary>
        /// 绘制一个RMVX样式的游标
        /// </summary>
        ///
        /// <returns></returns>
        public static LTexture MakeCursor(int w, int h)
        {
            /*LPixmap cursor = new LPixmap(w, h, true);
            cursor.SetColor(0, 0, 0, 255);
            cursor.FillRect(0, 0, w, h);
            cursor.SetColor(255, 255, 255, 255);
            cursor.FillRect(1, 1, w - 2, h - 2);
            cursor.SetColor(0, 0, 0, 255);
            cursor.FillRect(4, 4, w - 8, h - 8);
            cursor.SetColor(0, 0, 0, 255);
            cursor.FillRect(w / 4, 0, w / 2, h);
            cursor.SetColor(0, 0, 0, 255);
            cursor.FillRect(0, h / 4, w, h / 2);
            Color[] basePixels = cursor.GetData();
            int length = basePixels.Length;
            Color c = Color.Black;
            for (int i = 0; i < length; i++)
            {
                if (c.Equals(basePixels[i]))
                {
                    basePixels[i].PackedValue = LSystem.TRANSPARENT;
                }
            }
            cursor.SetData(basePixels);
            return cursor.Texture;*/
            return null;
        }
        
        public static AnimationHelper MakeObject(string fileName, int row, int col,
                int tileWidth, int tileHeight)
        {
           /* string key = fileName.Trim().ToLower();
            AnimationHelper animation = (AnimationHelper)CollectionUtils.Get(animations, key);
            if (animation == null)
            {
                LTexture[][] images = TextureUtils.GetSplit2Textures(fileName,
                        tileWidth, tileHeight);
                LTexture[][] result = (LTexture[][])CollectionUtils.XNA_CreateJaggedArray(typeof(LTexture), row, col);
                for (int y = 0; y < col; y++)
                {
                    for (int x = 0; x < row; x++)
                    {
                        result[x][y] = images[y][x];
                    }
                }
                images = null;
                CollectionUtils.Put(animations, key, animation = MakeObject(result[0], result[1], result[2],
                                            result[3]));
            }
            return animation;*/
            return null;
        }

        public static AnimationHelper MakeObject(string fileName, int tileWidth,
                int tileHeight, LColor col)
        {
            /*string key = fileName.Trim().ToLower();
            AnimationHelper animation = (AnimationHelper)CollectionUtils.Get(animations, key);
            if (animation == null)
            {

                LTexture texture = TextureUtils.FilterColor(fileName, col);

                int wlength = texture.GetWidth() / tileWidth;
                int hlength = texture.GetHeight() / tileHeight;

                LTexture[][] images = TextureUtils.GetSplit2Textures(texture,
                        tileWidth, tileHeight);

                LTexture[][] result = (LTexture[][])CollectionUtils.XNA_CreateJaggedArray(typeof(LTexture), hlength, wlength);
                for (int y = 0; y < wlength; y++)
                {
                    for (int x = 0; x < hlength; x++)
                    {
                        result[x][y] = images[y][x];
                    }
                }

                images = null;

                CollectionUtils.Put(animations, key, animation = MakeObject(result[0], result[1], result[3],
                                            result[2]));
            }
            return animation;*/
            return null;

        }

        public static AnimationHelper MakeObject(LTexture[] down,
                LTexture[] left, LTexture[] right, LTexture[] up)
        {
            AnimationHelper animation = new AnimationHelper();
            animation.downImages = down;
            animation.leftImages = left;
            animation.rightImages = right;
            animation.upImages = up;
            return animation;
        }
           
        public static void Dispose(LTexture[] images)
        {
            if (images == null)
            {
                return;
            }
            for (int i = 0; i < images.Length; i++)
            {
                images[i].Dispose();
                images[i] = null;
            }
        }
     
        internal AnimationHelper()
        {

        }

        public AnimationHelper(AnimationHelper animation)
        {
            leftImages = (LTexture[])CollectionUtils.CopyOf(animation.leftImages);
            downImages = (LTexture[])CollectionUtils.CopyOf(animation.downImages);
            upImages = (LTexture[])CollectionUtils.CopyOf(animation.upImages);
            rightImages = (LTexture[])CollectionUtils
                    .CopyOf(animation.rightImages);
        }

        public void Dispose()
        {
            Dispose(downImages);
            Dispose(upImages);
            Dispose(leftImages);
            Dispose(rightImages);
            CollectionUtils.Remove(animations, this);
        }
    }
}
