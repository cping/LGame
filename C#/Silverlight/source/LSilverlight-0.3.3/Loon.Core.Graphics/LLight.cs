using System;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Java;

namespace Loon.Core.Graphics
{
    public abstract class LLight
    {

        protected LColor[] colors;

        // 图层光源是否被开启
        protected bool lightingOn;

        protected bool colouredLights;

        protected bool isLightDirty;

        // 图层光源
        protected float[, ,] lightValue;

        // 光源集合
        protected List<Light> lights = new List<Light>();

        // 默认的主控光源
        protected Light mainLight;

        private int width;

        private int height;

        public static Light Create(float x, float y, float str)
        {
            return new Light(x, y, str);
        }

        protected void MaxLightSize(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        public void SetColor(int corner, float r, float g, float b, float a)
        {
            if (colors == null)
            {
                colors = new LColor[] { new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f), new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f) };
            }
            colors[corner].SetColor(r, g, b, a);
        }

        /**
         * 设定地图光影颜色
         * 
         * @param image
         * @param x
         * @param y
         */
        public void SetLightColor(int x, int y)
        {
            if (x < width && y < height)
            {
                SetColor(0, lightValue[x, y, 0], lightValue[x, y, 1],
                        lightValue[x, y, 2], 1);
                SetColor(1, lightValue[x + 1, y, 0], lightValue[x + 1, y, 1],
                        lightValue[x + 1, y, 2], 1);
                SetColor(2, lightValue[x + 1, y + 1, 0],
                        lightValue[x + 1, y + 1, 1], lightValue[x + 1, y + 1, 2], 1);
                SetColor(3, lightValue[x, y + 1, 0], lightValue[x, y + 1, 1],
                        lightValue[x, y + 1, 2], 1);
                isLightDirty = true;
            }
        }

        private void CreateLight()
        {
            if (lightValue == null)
            {
                lightValue = new float[width + 1, height + 1, 3];
            }
            lights.Clear();
            if (mainLight != null)
            {
                lights.Add(mainLight);
            }
            UpdateLight();
        }

        public void SetMainLight(Light l)
        {
            this.mainLight = l;
            this.isLightDirty = true;
        }

        public Light GetMainLight()
        {
            return mainLight;
        }

        public void AddLight(Light l)
        {
            if (l != null)
            {
                this.lights.Add(l);
                this.isLightDirty = true;
            }
        }

        public void RemoveLight(Light l)
        {
            if (l != null)
            {
                this.lights.Remove(l);
                this.isLightDirty = true;
            }
        }

        public void SetLight(bool l)
        {
            if (lightValue == null && l)
            {
                CreateLight();
            }
            this.lightingOn = l;
            if (!l)
            {
                colors = null;
            }
        }

        public bool IsLight()
        {
            return lightingOn;
        }

        public void ClearLight()
        {
            if (lights != null)
            {
                this.lights.Clear();
                this.isLightDirty = true;
            }
        }

        public void UpdateLight()
        {
            if (mainLight == null)
            {
                throw new RuntimeException("the main light is null !");
            }
            for (int y = 0; y < height + 1; y++)
            {
                for (int x = 0; x < width + 1; x++)
                {
                    for (int component = 0; component < 3; component++)
                    {
                        lightValue[x, y, component] = 0;
                    }
                    for (int i = 0; i < lights.Count; i++)
                    {
                        float[] effect = ((Light)lights[i]).GetEffectAt(x, y,
                                colouredLights);
                        for (int component = 0; component < 3; component++)
                        {
                            lightValue[x, y, component] += effect[component];
                        }
                    }
                    for (int component = 0; component < 3; component++)
                    {
                        if (lightValue[x, y, component] > 1)
                        {
                            lightValue[x, y, component] = 1;
                        }
                    }
                }
            }
            this.isLightDirty = true;
        }

        // 地图光源用类
        public class Light
        {

            private float xpos;

            private float ypos;

            private float strength;

            private LColor color;

            public Light(float x, float y, float str, LColor col)
            {
                this.xpos = x;
                this.ypos = y;
                this.strength = str;
                this.color = col;
            }

            public Light(float x, float y, float str)
                : this(x, y, str, null)
            {

            }

            public void SetLocation(float x, float y)
            {
                xpos = x;
                ypos = y;
            }

            public float[] GetEffectAt(float x, float y, bool colouredLights)
            {
                float dx = (x - xpos);
                float dy = (y - ypos);
                float distance2 = (dx * dx) + (dy * dy);
                float effect = 1 - (distance2 / (strength * strength));
                if (effect < 0)
                {
                    effect = 0;
                }
                if (colouredLights)
                {
                    return new float[] { color.R * effect, color.G * effect,
						color.B * effect };
                }
                else
                {
                    return new float[] { effect, effect, effect };
                }
            }

        }
    }
}
