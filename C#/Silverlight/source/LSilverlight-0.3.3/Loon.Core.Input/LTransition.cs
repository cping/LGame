using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics;
using Loon.Utils;
using Loon.Core.Graphics.OpenGL;
using Loon.Action.Sprite.Effect;
using Loon.Action.Map;
using Loon.Action.Sprite;

namespace Loon.Core.Input
{
    public class LTransition
    {

        public interface TransitionListener
        {
            void Update(long elapsedTime);

            void Draw(GLEx g);

            bool Completed();

            void Dispose();
        }

        public static LTransition NewCrossRandom()
        {
            return NewCrossRandom(LColor.black);
        }

        public static LTransition NewCrossRandom(LColor c)
        {
            return NewCross(MathUtils.Random(0, 1), TextureUtils
                    .CreateTexture(LSystem.screenRect.width,
                            LSystem.screenRect.height, c));
        }

        public static LTransition NewCross(int c)
        {
            return NewCross(c, ScreenUtils.ToScreenCaptureTexture());
        }

        private class _Cross : TransitionListener
        {

            CrossEffect cross;

            public _Cross(int c, LTexture texture)
            {
                cross = new CrossEffect(c, texture);
            }

            public void Draw(GLEx g)
            {
                cross.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                cross.Update(elapsedTime);
            }

            public bool Completed()
            {
                return cross.IsComplete();
            }

            public void Dispose()
            {
                cross.Dispose();
            }

        }



        public static LTransition NewCross(int c, LTexture texture)
        {
            if (GLEx.Self != null)
            {
                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Cross(c, texture));
                transition.SetDisplayGameUI(true);
                transition.code = 1;
                return transition;
            }
            return null;
        }

        private class _Arc : TransitionListener
        {

            ArcEffect arc;

            public _Arc(LColor c)
            {
                arc = new ArcEffect(c);
            }

            public void Draw(GLEx g)
            {
                arc.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                arc.Update(elapsedTime);
            }

            public bool Completed()
            {
                return arc.IsComplete();
            }

            public void Dispose()
            {
                arc.Dispose();
            }
        }

        public static LTransition NewArc(LColor c)
        {
            if (GLEx.Self != null)
            {

                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Arc(c));
                transition.SetDisplayGameUI(true);
                transition.code = 1;
                return transition;
            }
            return null;
        }

        public static LTransition NewArc()
        {
            return NewArc(LColor.black);
        }

        private class _Split : TransitionListener
        {

            SplitEffect split;

            public _Split(int d, LTexture texture)
            {
                split = new SplitEffect(texture, d);
            }

            public void Draw(GLEx g)
            {
                split.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                split.Update(elapsedTime);
            }

            public bool Completed()
            {
                return split.IsComplete();
            }

            public void Dispose()
            {
                split.Dispose();
            }

        }

        public static LTransition NewSplit(int d)
        {
            return NewSplit(d, ScreenUtils.ToScreenCaptureTexture());
        }

        public static LTransition NewSplit(int d, LTexture texture)
        {
            if (GLEx.Self != null)
            {

                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Split(d, texture));
                transition.SetDisplayGameUI(true);
                transition.code = 1;
                return transition;
            }
            return null;
        }

        public static LTransition NewSplitRandom()
        {
            return NewSplit(MathUtils.Random(0, Config.TDOWN));
        }

        public static LTransition NewSplitRandom(LTexture texture)
        {
            return NewSplit(MathUtils.Random(0, Config.TDOWN), texture);
        }

        public static LTransition NewSplitRandom(LColor c)
        {
            return NewSplitRandom(TextureUtils.CreateTexture(
                    LSystem.screenRect.width, LSystem.screenRect.height, c));
        }


        private class _Out : TransitionListener
        {

            OutEffect split;

            public _Out(int d, LTexture texture)
            {
                split = new OutEffect(texture, d);
            }

            public void Draw(GLEx g)
            {
                split.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                split.Update(elapsedTime);
            }

            public bool Completed()
            {
                return split.IsComplete();
            }

            public void Dispose()
            {
                split.Dispose();
            }

        }

        public static LTransition NewOut(int d, LTexture texture)
        {
            if (GLEx.Self != null)
            {

                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Out(d, texture));
                transition.SetDisplayGameUI(true);
                transition.code = 1;
                return transition;
            }
            return null;
        }

        private class _Fade : TransitionListener
        {

            FadeEffect fade;

            public _Fade(int type, LColor c)
            {
                fade = FadeEffect.GetInstance(type, c);
            }

            public void Draw(GLEx g)
            {
                fade.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                fade.Update(elapsedTime);
            }

            public bool Completed()
            {
                return fade.IsStop();
            }

            public void Dispose()
            {
                fade.Dispose();
            }

        }

        public static LTransition NewFade(int type, LColor c)
        {
            if (GLEx.Self != null)
            {
                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Fade(type, c));
                transition.SetDisplayGameUI(true);
                transition.code = 1;
                return transition;
            }
            return null;
        }

        public static LTransition NewFadeIn()
        {
            return LTransition.NewFade(FadeEffect.TYPE_FADE_IN);
        }

        public static LTransition NewFadeOut()
        {
            return LTransition.NewFade(FadeEffect.TYPE_FADE_OUT);
        }

        public static LTransition NewFade(int type)
        {
            return LTransition.NewFade(type, LColor.black);
        }


        private class _Cycle : TransitionListener
        {

            WaitSprite wait;

            public _Cycle(int d)
            {
                wait = new WaitSprite(d);
            }

            public void Draw(GLEx g)
            {
                wait.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                wait.Update(elapsedTime);
            }

            public bool Completed()
            {
                return true;
            }

            public void Dispose()
            {
                wait.Dispose();
            }

        }

        public static LTransition NewCycle(int type)
        {
            if (GLEx.Self != null)
            {
                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Cycle(type));
                transition.SetDisplayGameUI(false);
                transition.code = 0;
                return transition;
            }
            return null;
        }

        public static LTransition NewCycleRandom()
        {
            return NewCycle(MathUtils.Random(0, 11));
        }

        private class _PShadow : TransitionListener
        {

            PShadowEffect effect;

            public _PShadow(PShadowEffect e)
            {
                effect = e;
            }

            public void Draw(GLEx g)
            {
                effect.CreateUI(g);
            }

            public void Update(long elapsedTime)
            {
                effect.Update(elapsedTime);
            }

            public bool Completed()
            {
                return effect.IsComplete();
            }

            public void Dispose()
            {
                effect.Dispose();
            }

        }

        public static LTransition NewPShadow(string fileName, float alhpa)
        {
            PShadowEffect shadow = new PShadowEffect(fileName);
            shadow.SetAlpha(alhpa);
            return NewPShadow(shadow);
        }

        public static LTransition NewPShadow(string fileName)
        {
            return NewPShadow(fileName, 0.5f);
        }

        public static LTransition newPShadowGameUI(string fileName)
        {
            return NewPShadow(PShadowEffect.NewScreenEffect(fileName));
        }

        public static LTransition NewPShadow(PShadowEffect effect)
        {
            if (GLEx.Self != null)
            {
                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _PShadow(effect));
                transition.SetDisplayGameUI(true);
                transition.code = 1;
                return transition;
            }
            return null;
        }

        private class _Empty : TransitionListener
        {


            public _Empty()
            {

            }

            public void Draw(GLEx g)
            {

            }

            public void Update(long elapsedTime)
            {

            }

            public bool Completed()
            {
                return true;
            }

            public void Dispose()
            {

            }

        }


        public static LTransition NewEmpty()
        {
            if (GLEx.Self != null)
            {
                LTransition transition = new LTransition();
                transition.SetTransitionListener(new _Empty());
                transition.SetDisplayGameUI(false);
                transition.code = 0;
                return transition;
            }
            return null;
        }

        // 是否在在启动过渡效果同时显示游戏画面（即是否顶层绘制过渡画面，底层同时绘制标准游戏画面）
        internal bool isDisplayGameUI;

        internal int code;

        internal TransitionListener listener;

        public void SetDisplayGameUI(bool s)
        {
            this.isDisplayGameUI = s;
        }

        public bool IsDisplayGameUI()
        {
            return this.isDisplayGameUI;
        }

        public void SetTransitionListener(TransitionListener l)
        {
            this.listener = l;
        }

        public TransitionListener GetTransitionListener()
        {
            return this.listener;
        }

        internal void Update(long elapsedTime)
        {
            if (listener != null)
            {
                listener.Update(elapsedTime);
            }
        }

        internal void Draw(GLEx g)
        {
            if (listener != null)
            {
                listener.Draw(g);
            }
        }

        internal bool Completed()
        {
            if (listener != null)
            {
                return listener.Completed();
            }
            return false;
        }

        internal void Dispose()
        {
            if (listener != null)
            {
                listener.Dispose();
            }
        }
    }
}
