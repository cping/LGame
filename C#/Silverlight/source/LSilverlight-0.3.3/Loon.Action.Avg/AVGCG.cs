using System;
using System.Collections.Generic;
using Loon.Core.Event;
using Loon.Core.Graphics.OpenGL;
using Loon.Utils.Collection;
using Loon.Action.Sprite.Effect;
using Loon.Utils;
using Loon.Core;
using Loon.Core.Geom;
using Loon.Java;

namespace Loon.Action.Avg
{
    public class AVGCG
    {

        public sealed class _Updateable : Updateable
        {
            private readonly AVGChara c;

            public _Updateable(AVGChara c)
            {
                this.c = c;
            }

            public void Action()
            {
                c.Dispose();
            }
        }

        private long charaShowDelay = 60;

        private LTexture background;

        private ArrayMap charas;

        private bool style, loop;

        internal int sleep, sleepMax, shakeNumber;

        public AVGCG()
        {
            this.charas = new ArrayMap(10);
            this.style = true;
            this.loop = true;
        }

        public LTexture getBackgroundCG()
        {
            return background;
        }

        public void NoneBackgroundCG()
        {
            if (background != null)
            {
                background.Destroy();
                background = null;
            }
        }

        public void SetBackgroundCG(LTexture backgroundCG)
        {
            if (backgroundCG == this.background)
            {
                return;
            }
            if (background != null)
            {
                background.Destroy();
                background = null;
            }
            this.background = backgroundCG;
        }

        private static String _Update(String n)
        {
            String name = n;
            if (name.StartsWith("\""))
            {
                name = name.Replace("\"", "");
            }
            return name;
        }

        public void SetBackgroundCG(String resName)
        {
            this.SetBackgroundCG(new LTexture(_Update(resName)));
        }

        public void Add(String resName, AVGChara chara)
        {
            if (chara == null)
            {
                return;
            }
            String path = _Update(resName);
            lock (charas)
            {
                chara.SetFlag(FadeEffect.TYPE_FADE_OUT, charaShowDelay);
                this.charas.Put(path.Replace(" ", "").ToLower(), chara);
            }
        }

        public void Add(String resName, int x, int y)
        {
            Add(resName, x, y, LSystem.screenRect.width, LSystem.screenRect.height);
        }

        public void Add(String resName, int x, int y, int w, int h)
        {
            String path = _Update(resName);
            lock (charas)
            {
                String keyName = path.Replace(" ", "").ToLower();
                AVGChara chara = (AVGChara)charas.Get(keyName);
                if (chara == null)
                {
                    chara = new AVGChara(path, x, y, w, h);
                    chara.SetFlag(FadeEffect.TYPE_FADE_OUT, charaShowDelay);
                    charas.Put(keyName, chara);
                }
                else
                {
                    chara.SetFlag(FadeEffect.TYPE_FADE_OUT, charaShowDelay);
                    chara.SetX(x);
                    chara.SetY(y);
                }
            }
        }

        public AVGChara Remove(String resName)
        {
            String path = _Update(resName);
            lock (charas)
            {
                String name = path.Replace(" ", "").ToLower();
                AVGChara chara = null;
                if (style)
                {
                    chara = (AVGChara)charas.Get(name);
                    if (chara != null)
                    {
                        chara.SetFlag(FadeEffect.TYPE_FADE_IN, charaShowDelay);
                    }
                }
                else
                {
                    chara = (AVGChara)charas.Remove(name);
                    if (chara != null)
                    {
                        XNA_dispose(chara);
                    }
                }
                return chara;
            }
        }

        public void Replace(String res1, String res2)
        {
            String path1 = _Update(res1);
            String path2 = _Update(res2);
            lock (charas)
            {
                String name = path1.Replace(" ", "").ToLower();
                AVGChara old = null;
                if (style)
                {
                    old = (AVGChara)charas.Get(name);
                    if (old != null)
                    {
                        old.SetFlag(FadeEffect.TYPE_FADE_IN, charaShowDelay);
                    }
                }
                else
                {
                    old = (AVGChara)charas.Remove(name);
                    if (old != null)
                    {
                        XNA_dispose(old);
                    }
                }
                if (old != null)
                {
                    int x = old.GetX();
                    int y = old.GetY();
                    AVGChara newObject = new AVGChara(path2, 0, 0, old.maxWidth,
                            old.maxHeight);
                    newObject.SetMove(false);
                    newObject.SetX(x);
                    newObject.SetY(y);
                    Add(path2, newObject);
                }
            }
        }

        private static void XNA_dispose(AVGChara c)
        {
            Updateable remove = new _Updateable(c);
            LSystem.Load(remove);
        }

        public void Paint(GLEx g)
        {
            if (background != null)
            {
                if (shakeNumber > 0)
                {
                    g.DrawTexture(background,
                            shakeNumber / 2 - LSystem.random.Next(shakeNumber),
                            shakeNumber / 2 - LSystem.random.Next(shakeNumber));
                }
                else
                {
                    g.DrawTexture(background, 0, 0);
                }
            }
            lock (charas)
            {
                for (int i = 0; i < charas.Size(); i++)
                {
                    AVGChara chara = (AVGChara)charas.Get(i);
                    if (chara == null || !chara.isVisible)
                    {
                        continue;
                    }
                    if (style)
                    {
                        if (chara.flag != -1)
                        {
                            if (chara.flag == FadeEffect.TYPE_FADE_IN)
                            {
                                chara.currentFrame--;
                                if (chara.currentFrame == 0)
                                {
                                    chara.opacity = 0;
                                    chara.flag = -1;
                                    chara.Dispose();
                                    charas.Remove(chara);
                                }
                            }
                            else
                            {
                                chara.currentFrame++;
                                if (chara.currentFrame == chara.time)
                                {
                                    chara.opacity = 0;
                                    chara.flag = -1;
                                }
                            }
                            chara.opacity = (chara.currentFrame / chara.time) * 255;
                            if (chara.opacity > 0)
                            {
                                g.SetAlpha(chara.opacity / 255);
                            }
                        }
                    }
                    if (chara.isAnimation)
                    {
                        AVGAnm animation = chara.anm;
                        if (animation.load)
                        {
                            if (animation.loop && animation.startTime == -1)
                            {
                                animation.Start(0, loop);
                            }
                            Point.Point2i point = animation.GetPos(JavaRuntime
                                    .CurrentTimeMillis());
                            if (animation.alpha != 1f)
                            {
                                g.SetAlpha(animation.alpha);
                            }
                            g.DrawTexture(animation.texture, chara.x, chara.y,
                                    animation.width, animation.height, point.x,
                                    point.y, point.x + animation.imageWidth,
                                    point.y + animation.imageHeight,
                                    animation.angle, animation.color);
                            if (animation.alpha != 1f)
                            {
                                g.SetAlpha(1f);
                            }
                        }
                    }
                    else
                    {
                        chara.Next();
                        chara.draw(g);
                    }
                    if (style)
                    {
                        if (chara.flag != -1 && chara.opacity > 0)
                        {
                            g.SetAlpha(1f);
                        }
                    }
                }
            }
        }

        public void Clear()
        {
            lock (charas)
            {
                charas.Clear();
            }
        }

        public ArrayMap getCharas()
        {
            return charas;
        }

        public int Count()
        {
            if (charas != null)
            {
                return charas.Size();
            }
            return 0;
        }

        public long GetCharaShowDelay()
        {
            return charaShowDelay;
        }

        public void SetCharaShowDelay(long charaShowDelay)
        {
            this.charaShowDelay = charaShowDelay;
        }

        public bool IsStyle()
        {
            return style;
        }

        public void SetStyle(bool style)
        {
            this.style = style;
        }

        public bool IsLoop()
        {
            return loop;
        }

        public void SetLoop(bool loop)
        {
            this.loop = loop;
        }

        public void Dispose()
        {
            lock (charas)
            {
                if (style)
                {
                    for (int i = 0; i < charas.Size(); i++)
                    {
                        AVGChara ch = (AVGChara)charas.Get(i);
                        if (ch != null)
                        {
                            ch.SetFlag(FadeEffect.TYPE_FADE_IN, charaShowDelay);
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < charas.Size(); i++)
                    {
                        AVGChara ch = (AVGChara)charas.Get(i);
                        if (ch != null)
                        {
                            ch.Dispose();
                            ch = null;
                        }
                    }
                    charas.Clear();

                }
            }
        }
    }
}
