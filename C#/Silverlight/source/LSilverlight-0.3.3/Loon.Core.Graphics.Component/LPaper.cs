using System;
using System.Collections.Generic;
using Loon.Action.Sprite;
using Loon.Core.Graphics.OpenGL;

namespace Loon.Core.Graphics.Component
{
    public class LPaper : LContainer
    {

        private Animation animation;

        public LPaper(LTexture background, int x, int y):base(x, y, background.GetWidth(), background.GetHeight())
        {
            
            this.animation = new Animation();
            this.customRendering = true;
            this.SetBackground(background);
            this.SetElastic(true);
            this.SetLocked(true);
            this.SetLayer(100);
        }

        public LPaper(LTexture background): this(background, 0, 0)
        {
           
        }

        public LPaper(String fileName, int x, int y):this(LTextures.LoadTexture(fileName), x, y)
        {
            
        }

        public LPaper(String fileName): this(fileName, 0, 0)
        {
           
        }

        public LPaper(int x, int y, int w, int h):this(new LTexture((w < 1) ? w = 1 : w, (h < 1) ? h = 1 : h, true), x, y)
        {
            
        }

        public Animation GetAnimation()
        {
            return this.animation;
        }

        public void SetAnimation(Animation animation_0)
        {
            this.animation = animation_0;
        }

        public void AddAnimationFrame(String fileName, long timer)
        {
            animation.AddFrame(fileName, timer);
        }

        public void AddAnimationFrame(LTexture image, long timer)
        {
            animation.AddFrame(image, timer);
        }

        public virtual void DoClick()
        {
            if (Click != null)
            {
                Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
                Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        public virtual void DownClick()
        {
            if (Click != null)
            {
                Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        public virtual void UpClick()
        {
            if (Click != null)
            {
                Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        protected internal override void ProcessTouchClicked()
        {
            if (!input.IsMoving())
            {
                this.DoClick();
            }
        }

        protected internal override void ProcessKeyPressed()
        {
            if (this.IsSelected())
            {
                this.DoClick();
            }
        }

        protected override void CreateCustomUI(GLEx g, int x, int y, int w, int h)
        {
            if (visible)
            {
                if (animation.GetSpriteImage() != null)
                {
                    g.DrawTexture(animation.GetSpriteImage(), x, y);
                }
                if (x != 0 && y != 0)
                {
                    g.Translate(x, y);
                    Paint(g);
                    g.Translate(-x, -y);
                }
                else
                {
                    Paint(g);
                }
            }
        }

        public void Paint(GLEx g)
        {

        }

        public override void Update(long elapsedTime)
        {
            if (visible)
            {
                base.Update(elapsedTime);
                animation.Update(elapsedTime);
            }
        }

        protected internal override void ProcessTouchDragged()
        {
            if (!locked)
            {
                if (GetContainer() != null)
                {
                    GetContainer().SendToFront(this);
                }
                this.Move(this.input.GetTouchDX(), this.input.GetTouchDY());
            }
        }

        protected internal override void ProcessTouchPressed()
        {
            if (!input.IsMoving())
            {
                this.DownClick();
            }
        }

        protected internal override void ProcessTouchReleased()
        {
            if (!input.IsMoving())
            {
                this.UpClick();
            }
        }

        public bool IsLocked()
        {
            return locked;
        }

        public void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {

        }

        public override String GetUIName()
        {
            return "Paper";
        }

        public override void Dispose()
        {
            base.Dispose();
            if (animation != null)
            {
                animation.Dispose();
                animation = null;
            }
        }

    }
}
