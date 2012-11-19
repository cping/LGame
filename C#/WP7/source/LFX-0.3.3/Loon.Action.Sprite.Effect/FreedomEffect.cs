using System;
using System.Collections.Generic;
using Loon.Core;
using Loon.Core.Timer;
using Loon.Core.Graphics.OpenGL;
using Loon.Java.Collections;
using Loon.Java;
using Loon.Utils;
using Loon.Core.Geom;

namespace Loon.Action.Sprite.Effect
{
    public class FreedomEffect : LObject, ISprite
    {

        private int  width, height, count;

        private LTimer timer;

        private LTexture texture;

        private IKernel[] kernels;

        private bool visible;

        private List<LTexture> tex2ds;

        /// <summary>
        /// 返回默认数量的飘雪
        /// </summary>
        ///
        /// <returns></returns>
        public static FreedomEffect GetSnowEffect()
        {
            return FreedomEffect.GetSnowEffect(60);
        }

        /// <summary>
        /// 返回指定数量的飘雪
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <returns></returns>
        public static FreedomEffect GetSnowEffect(int count_0)
        {
            return FreedomEffect.GetSnowEffect(count_0, 0, 0);
        }

        /// <summary>
        /// 返回指定数量的飘雪
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <param name="x_1"></param>
        /// <param name="y_2"></param>
        /// <returns></returns>
        public static FreedomEffect GetSnowEffect(int count_0, int x_1, int y_2)
        {
            return FreedomEffect.GetSnowEffect(count_0, x_1, y_2,
                    LSystem.screenRect.width, LSystem.screenRect.height);
        }

        /// <summary>
        /// 返回指定数量的飘雪
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <param name="x_1"></param>
        /// <param name="y_2"></param>
        /// <param name="w"></param>
        /// <param name="h"></param>
        /// <returns></returns>
        public static FreedomEffect GetSnowEffect(int count_0, int x_1, int y_2, int w,
                int h)
        {
            return new FreedomEffect(typeof(SnowKernel), count_0, 4, x_1, y_2, w, h);
        }

        /// <summary>
        /// 返回默认数量的落雨
        /// </summary>
        ///
        /// <returns></returns>
        public static FreedomEffect GetRainEffect()
        {
            return FreedomEffect.GetRainEffect(60);
        }

        /// <summary>
        /// 返回指定数量的落雨
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <returns></returns>
        public static FreedomEffect GetRainEffect(int count_0)
        {
            return FreedomEffect.GetRainEffect(count_0, 0, 0);
        }

        /// <summary>
        /// 返回指定数量的落雨
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <param name="x_1"></param>
        /// <param name="y_2"></param>
        /// <returns></returns>
        public static FreedomEffect GetRainEffect(int count_0, int x_1, int y_2)
        {
            return FreedomEffect.GetRainEffect(count_0, x_1, y_2,
                    LSystem.screenRect.width, LSystem.screenRect.height);
        }

        /// <summary>
        /// 返回指定数量的落雨
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <param name="x_1"></param>
        /// <param name="y_2"></param>
        /// <param name="w"></param>
        /// <param name="h"></param>
        /// <returns></returns>
        public static FreedomEffect GetRainEffect(int count_0, int x_1, int y_2, int w,
                int h)
        {
            return new FreedomEffect(typeof(RainKernel), count_0, 3, x_1, y_2, w, h);
        }

        /// <summary>
        /// 返回指定数量的樱花
        /// </summary>
        ///
        /// <returns></returns>
        public static FreedomEffect GetPetalEffect()
        {
            return FreedomEffect.GetPetalEffect(25);
        }

        /// <summary>
        /// 返回指定数量的樱花
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <returns></returns>
        public static FreedomEffect GetPetalEffect(int count_0)
        {
            return FreedomEffect.GetPetalEffect(count_0, 0, 0);
        }

        /// <summary>
        /// 返回指定数量的樱花
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <param name="x_1"></param>
        /// <param name="y_2"></param>
        /// <returns></returns>
        public static FreedomEffect GetPetalEffect(int count_0, int x_1, int y_2)
        {
            return FreedomEffect.GetPetalEffect(count_0, x_1, y_2,
                    LSystem.screenRect.width, LSystem.screenRect.height);
        }

        /// <summary>
        /// 返回指定数量的樱花
        /// </summary>
        ///
        /// <param name="count_0"></param>
        /// <param name="x_1"></param>
        /// <param name="y_2"></param>
        /// <param name="w"></param>
        /// <param name="h"></param>
        /// <returns></returns>
        public static FreedomEffect GetPetalEffect(int count_0, int x_1, int y_2, int w,
                int h)
        {
            return new FreedomEffect(typeof(PetalKernel), count_0, 1, x_1, y_2, w, h);
        }

        public FreedomEffect(Type clazz, int count_0, int limit): this(clazz, count_0, limit, 0, 0)
        {
           
        }

        public FreedomEffect(Type clazz, int count_0, int limit, int x_1, int y_2): this(clazz, count_0, limit, x_1, y_2, LSystem.screenRect.width,
                    LSystem.screenRect.height)
        {
           
        }

        public FreedomEffect(Type clazz, int count_0, int limit, int x_1, int y_2,
                int w, int h)
        {
            this.visible = true;
            this.tex2ds = new List<LTexture>(10);
            this.SetLocation(x_1, y_2);
            this.width = w;
            this.height = h;
            this.count = count_0;
            this.timer = new LTimer(80);
            this.kernels = (IKernel[])Arrays.NewInstance(clazz, count_0);
            try
            {
                System.Reflection.ConstructorInfo constructor = JavaRuntime.GetConstructor(clazz, new Type[] { typeof(int), typeof(int),
											typeof(int) });
                for (int i = 0; i < count_0; i++)
                {
                    int no = MathUtils.Random(0, limit);
                    kernels[i] = (IKernel)JavaRuntime.Invoke(constructor, new Object[] {
											((int)(no)), ((int)(w)), ((int)(h)) });
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.StackTrace);
            }
        }

        public override void Update(long elapsedTime)
        {
            if (visible && timer.Action(elapsedTime))
            {
                for (int i = 0; i < count; i++)
                {
                    kernels[i].Update();
                }
            }
        }

        public void CreateUI(GLEx g)
        {
            if (visible)
            {
                    CollectionUtils.Clear(tex2ds);
                    for (int i = 0; i < count; i++)
                    {
                        texture = kernels[i].Get();
                        if (!tex2ds.Contains(texture))
                        {
                            CollectionUtils.Add(tex2ds, texture);
                            texture.GLBegin();
                        }
                        kernels[i].Draw(g);
                    }
                    for (int i_1 = 0; i_1 < tex2ds.Count; i_1++)
                    {
                        texture = tex2ds[i_1];
                        texture.GLEnd();
                    }
            }
        }

        public long GetDelay()
        {
            return timer.GetDelay();
        }

        public void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool visible_0)
        {
            this.visible = visible_0;
        }

        public override int GetHeight()
        {
            return height;
        }

        public void SetHeight(int height_0)
        {
            this.height = height_0;
        }

        public override int GetWidth()
        {
            return width;
        }

        public void SetWidth(int width_0)
        {
            this.width = width_0;
        }

        public IKernel[] GetKernels()
        {
            return kernels;
        }

        public void SetKernels(IKernel[] kernels_0)
        {
            this.kernels = kernels_0;
        }

        public RectBox GetCollisionBox()
        {
            return GetRect(x, y, width, height);
        }

        public LTexture GetBitmap()
        {
            return null;
        }

        public void Dispose()
        {
            this.visible = false;
            if (kernels != null)
            {
                for (int i = 0; i < kernels.Length; i++)
                {
                    kernels[i].Dispose();
                    kernels[i] = null;
                }
            }
            CollectionUtils.Clear(tex2ds);
        }
    }
}
