using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Loon.Action.Sprite;
using Loon.Core.Graphics.OpenGL;
using Loon.Core.Input;

namespace Loon.Core.Graphics.Component
{
    public class LMessage : LContainer
    {
        private Animation animation;

        private LFont messageFont = LFont.GetDefaultFont();

        private LColor fontColor = LColor.white;

        private long printTime, totalDuration;

        private int dx, dy, dw, dh;

        private Print print;

        public LMessage(int width, int height): this(0, 0, width, height)
        {
           
        }

        public LMessage(int x, int y, int width, int height):this((LTexture)null, x, y, width, height)
        {
            
        }

        public LMessage(String fileName, int x, int y):this(LTextures.LoadTexture(fileName), x, y)
        {
            
        }

        public LMessage(LTexture formImage, int x, int y):this(formImage, x, y, formImage.GetWidth(), formImage.GetHeight())
        {
            
        }

        public LMessage(LTexture formImage, int x, int y, int width, int height): base(x, y, width, height)
        {
            this.animation = new Animation();
            if (formImage == null)
            {
                this.SetBackground(new LTexture(width, height, true));
                this.SetAlpha(0.3F);
            }
            else
            {
                this.SetBackground(formImage);
                if (width == -1)
                {
                    width = formImage.GetWidth();
                }
                if (height == -1)
                {
                    height = formImage.GetHeight();
                }
            }
            this.print = new Print(GetLocation(), messageFont, width, height);
            if (XNAConfig.IsActive())
            {
                this.SetTipIcon(XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "creese.png"));
            }
            this.totalDuration = 80;
            this.customRendering = true;
            this.SetWait(false);
            this.SetElastic(true);
            this.SetLocked(true);
            this.SetLayer(100);
        }

        public void SetWait(bool flag)
        {
            print.SetWait(flag);
        }

        public bool IsWait()
        {
            return print.IsWait();
        }

        public void Complete()
        {
            print.Complete();
        }

        public void SetLeftOffset(int left)
        {
            print.SetLeftOffset(left);
        }

        public void SetTopOffset(int top)
        {
            print.SetTopOffset(top);
        }

        public int GetLeftOffset()
        {
            return print.GetLeftOffset();
        }

        public int GetTopOffset()
        {
            return print.GetTopOffset();
        }

        public int GetMessageLength()
        {
            return print.GetMessageLength();
        }

        public void SetMessageLength(int messageLength)
        {
            print.SetMessageLength(messageLength);
        }

        public void SetTipIcon(String fileName)
        {
            print.SetCreeseIcon(new LTexture(fileName));
        }

        public void SetTipIcon(LTexture icon)
        {
            print.SetCreeseIcon(icon);
        }

        public void SetNotTipIcon()
        {
            print.SetCreeseIcon(null);
        }

        public void SetEnglish(bool e)
        {
            print.SetEnglish(true);
        }

        public bool IsEnglish()
        {
            return print.IsEnglish();
        }

        public void SetDelay(long delay)
        {
            this.totalDuration = ((delay < 1) ? (long)(1) : (long)(delay));
        }

        public long GetDelay()
        {
            return totalDuration;
        }

        public bool IsComplete()
        {
            return print.IsComplete();
        }

        public void SetPauseIconAnimationLocation(int dx_0, int dy_1)
        {
            this.dx = dx_0;
            this.dy = dy_1;
        }

        public void SetMessage(String context, bool isComplete)
        {
            print.SetMessage(context, messageFont, isComplete);
        }

        public void SetMessage(String context)
        {
            print.SetMessage(context, messageFont);
        }

        public String GetMessage()
        {
            return print.GetMessage();
        }

        /// <summary>
        /// 处理点击事件（请重载实现）
        /// </summary>
        ///
        public virtual void DoClick()
        {
            if (Click != null)
            {
                Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
                Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        protected internal override void ProcessTouchClicked()
        {
            this.DoClick();
        }

        protected internal override void ProcessKeyPressed()
        {
            if (this.IsSelected() && this.input.GetKeyPressed() == Key.ENTER)
            {
                this.DoClick();
            }
        }

        public override void Update(long elapsedTime)
        {
            if (!visible)
            {
                return;
            }
            base.Update(elapsedTime);
            if (print.IsComplete())
            {
                animation.Update(elapsedTime);
            }
            printTime += elapsedTime;
            if (printTime >= totalDuration)
            {
                printTime = printTime % totalDuration;
                print.Next();
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        protected override void CreateCustomUI(GLEx g, int x, int y, int w,
                int h)
        {
            if (!visible)
            {
                return;
            }
            LFont oldFont = g.GetFont();
            g.SetFont(messageFont);
            print.Draw(g, fontColor);
            g.SetFont(oldFont);
            if (print.IsComplete() && animation != null)
            {
                if (animation.GetSpriteImage() != null)
                {
                    g.SetAlpha(1.0F);
                    UpdateIcon();
                    g.DrawTexture(animation.GetSpriteImage(), dx, dy);
                }
            }
            g.ResetColor();
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
                this.UpdateIcon();
            }
        }

        public void SetPauseIconAnimation(Animation a)
        {
            this.animation = a;
            if (a != null)
            {
                LTexture image = a.GetSpriteImage(0);
                if (image != null)
                {
                    this.dw = image.GetWidth();
                    this.dh = image.GetHeight();
                    this.UpdateIcon();
                }
            }
        }

        private void UpdateIcon()
        {
            this.SetPauseIconAnimationLocation(GetScreenX() + GetWidth() - dw / 2
                    - 20, GetScreenY() + GetHeight() - dh - 10);
        }

        public LColor GetFontColor()
        {
            return fontColor;
        }

        public void SetFontColor(LColor f)
        {
            this.fontColor = f;
        }

        public LFont GetMessageFont()
        {
            return messageFont;
        }

        public void SetMessageFont(LFont f)
        {
            this.messageFont = f;
        }

        public bool IsLocked()
        {
            return locked;
        }

        public void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        protected internal override void ValidateSize()
        {
            base.ValidateSize();
        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {

        }

        public override String GetUIName()
        {
            return "Message";
        }

        public override void Dispose()
        {
            base.Dispose();
            if (print != null)
            {
                print.Dispose();
                print = null;
            }
            if (animation != null)
            {
                animation.Dispose();
                animation = null;
            }
        }
    }
}
