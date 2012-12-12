using Loon.Core.Graphics.Opengl;
using Loon.Utils;
using Loon.Core.Input;
using Microsoft.Xna.Framework.Graphics;

namespace Loon.Core.Graphics.Component
{
    public class LPad : LComponent
    {

        private bool isLeft, isRight, isUp, isDown, isClick;

        public  interface ClickListener
        {

             void Up();

             void Down();

             void Left();

             void Right();

             void Other();

        }

        public ClickListener listener;

        private float centerX, centerY;

        private float offsetX, offsetY;

        private int dotWidth, dotHeight;

        private int angle;

        private int baseWidth, baseHeight;

        private int backWidth, backHeight;

        private LTexture back;

        private LTexture fore;

        private LTexture dot;

        private float scale_pad;

        private LTexturePack pack;

        public LPad(int x, int y)
            : this(x, y, 1.2f)
        {

        }

        public LPad(int x, int y, float scale)
            : this(x, y, XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "pad_ui_back.png"), XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "pad_ui_fore.png"),
                XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "pad_ui_dot.png"), scale)
        {

        }

        public LPad(int x, int y, LTexture b, LTexture f, LTexture d, float scale)
            : base(x, y, (int)(f.GetWidth() * scale), (int)(f.GetHeight() * scale))
        {
            this.offsetX = 6 * scale;
            this.offsetY = 6 * scale;
            this.fore = f;
            this.back = b;
            this.dot = d;
            this.dotWidth = (int)(d.GetWidth() * scale);
            this.dotHeight = (int)(d.GetHeight() * scale);
            this.baseWidth = (int)(f.GetWidth() * scale);
            this.baseHeight = (int)(f.GetHeight() * scale);
            this.backWidth = (int)(b.GetWidth() * scale);
            this.backHeight = (int)(b.GetHeight() * scale);
            this.centerX = (baseWidth - dotWidth) / 2 + offsetX;
            this.centerY = (baseHeight - dotHeight) / 2 + offsetY;
            this.scale_pad = scale;
        }

        public LPad(int x, int y, LTexturePack p, float scale): base(x, y, (int)(p.GetEntry("fore").Width() * scale), (int)(p
                    .GetEntry("fore").Height() * scale))
        {
           
            this.offsetX = 6 * scale;
            this.offsetY = 6 * scale;
            this.pack = p;
            this.dotWidth = (int)(p.GetEntry("dot").Width() * scale);
            this.dotHeight = (int)(p.GetEntry("dot").Height() * scale);
            this.baseWidth = (int)(p.GetEntry("fore").Width() * scale);
            this.baseHeight = (int)(p.GetEntry("fore").Height() * scale);
            this.backWidth = (int)(p.GetEntry("back").Width() * scale);
            this.backHeight = (int)(p.GetEntry("back").Height() * scale);
            this.centerX = (baseWidth - dotWidth) / 2 + offsetX;
            this.centerY = (baseHeight - dotHeight) / 2 + offsetY;
            this.scale_pad = scale;
            p.SetFormat(Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
        }

        public float GetScale()
        {
            return scale_pad;
        }

        void FreeClick()
        {
            this.isLeft = false;
            this.isRight = false;
            this.isDown = false;
            this.isUp = false;
            this.isClick = false;
            if (listener != null)
            {
                listener.Other();
            }
        }

        protected internal override void ProcessTouchReleased()
        {
            FreeClick();
        }

        protected internal override void ProcessTouchPressed()
        {
            float x = MathUtils.BringToBounds(0, baseWidth, Touch.GetX()
                   - GetScreenX())
                   / baseWidth - 0.5f;
            float y = MathUtils.BringToBounds(0, baseHeight, Touch.GetY()
                   - GetScreenY())
                   / baseHeight - 0.5f;
            if (x == 0 && y == 0)
            {
                return;
            }
            if (MathUtils.Abs(x) > MathUtils.Abs(y))
            {
                if (x > 0)
                {
                    this.isRight = true;
                    this.isClick = true;
                    this.centerX = offsetX + x + (baseWidth - dotWidth) / 2
                            + dotWidth * 0.75f;
                    this.centerY = offsetY + y + (baseHeight - dotHeight) / 2;
                    if (listener != null)
                    {
                        listener.Right();
                    }
                }
                else if (x < 0)
                {
                    this.isLeft = true;
                    this.isClick = true;
                    this.centerX = offsetX + x + (baseWidth - dotWidth) / 2
                            - dotWidth * 0.75f;
                    this.centerY = offsetY + y + (baseHeight - dotHeight) / 2 ;
                    if (listener != null)
                    {
                        listener.Left();
                    }
                }
                else if (x == 0)
                {
                    FreeClick();
                }
            }
            else
            {
                if (y > 0)
                {
                    this.isDown = true;
                    this.isClick = true;
                    this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - 1;
                    this.centerY = offsetY + y + (baseHeight - dotHeight) / 2
                            + dotHeight * 0.75f;
                    if (listener != null)
                    {
                        listener.Down();
                    }
                }
                else if (y < 0)
                {
                    this.isUp = true;
                    this.isClick = true;
                    this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - 1;
                    this.centerY = offsetY + y + (baseHeight - dotHeight) / 2
                            - dotHeight * 0.75f;
                    if (listener != null)
                    {
                        listener.Up();
                    }
                }
                else if (y == 0)
                {
                    FreeClick();
                }
            }
        }

        Loon.Action.Sprite.SpriteBatch batch = new Loon.Action.Sprite.SpriteBatch(128);

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {
            if (Touch.IsUp())
            {
                FreeClick();
            }
            if (pack != null)
            {
                pack.GLBegin();
                pack.Draw(0, x, y, backWidth, backHeight);
                if (isClick)
                {
                    if (angle < 360)
                    {
                        angle += 1;
                    }
                    else
                    {
                        angle = 0;
                    }
                    pack.Draw(2, x + centerX, y + centerY, dotWidth, dotHeight,
                            angle, null);
                }
                pack.Draw(1, x + (backWidth - baseWidth) * 0.5f, y
                        + (backHeight - baseHeight) * 0.5f, baseWidth, baseHeight);
                pack.GLEnd();
            }
            else
            {
                batch.Begin();
                batch.Draw(back, x, y, backWidth, backHeight);
                if (isClick)
                {
                    if (angle < 360)
                    {
                        angle += 1;
                    }
                    else
                    {
                        angle = 1;
                    }
                    batch.Draw(dot, x + centerX, y + centerY, dotWidth, dotHeight,
                            angle);
                }
                batch.Draw(fore, (x + (backWidth - baseWidth) * 0.5f),
                        (y + (backHeight - baseHeight) * 0.5f), baseWidth,
                        baseHeight);
                batch.End();
            }
        }

        public bool IsLeft()
        {
            return isLeft;
        }

        public bool IsRight()
        {
            return isRight;
        }

        public bool IsUp()
        {
            return isUp;
        }

        public bool IsDown()
        {
            return isDown;
        }

        public bool IsClick()
        {
            return isClick;
        }

        public ClickListener GetListener()
        {
            return listener;
        }

        public void SetListener(ClickListener listener)
        {
            this.listener = listener;
        }

        public float GetOffsetX()
        {
            return offsetX;
        }

        public void SetOffsetX(float offsetX)
        {
            this.offsetX = offsetX;
        }

        public float GetOffsetY()
        {
            return offsetY;
        }

        public void SetOffsetY(float offsetY)
        {
            this.offsetY = offsetY;
        }

        public override string GetUIName()
        {
            return "Pad";
        }

        public override void Dispose()
        {
            if (pack != null)
            {
                pack.Dispose();
            }
            if (back != null)
            {
                back.Destroy();
                back = null;
            }
            if (fore != null)
            {
                fore.Destroy();
                fore = null;
            }
            if (dot != null)
            {
                dot.Destroy();
                dot = null;
            }
        }
    }
}
