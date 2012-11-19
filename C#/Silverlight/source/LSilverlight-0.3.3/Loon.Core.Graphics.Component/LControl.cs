namespace Loon.Core.Graphics.Component
{
    using Loon;
    using Loon.Core.Graphics;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Utils;
    using Loon.Core.Input;
    using Loon.Action.Map;

    public class LControl : LComponent
    {

        public interface DigitalListener
        {

            void Up();

            void Down();

            void Left();

            void Right();

            void Up45();

            void Down45();

            void Left45();

            void Right45();

        }

        public DigitalListener control;

        private const float SIDE = 0.5f;

        private const float DIAGONAL = 0.354f;

        private const float DELTA = 22.5f;

        private LTexture controlBase;

        private LTexture controlDot;

        private int baseWidth, baseHeight;

        private int dotWidth, dotHeight;

        private float centerX, centerY;

        private bool allowDiagonal;

        public LControl(int x, int y):this(x,y,128, 128, 64, 64)
        {
           
        }

        public LControl(int x, int y, string basename, string dot, int bw, int bh,
                int dw, int dh)
            : this(x, y, LTextures.LoadTexture(basename), LTextures
                .LoadTexture(dot), bw, bh, dw, dh)
        {

        }

        public LControl(int x, int y, int bw, int bh, int dw, int dh):  this(x, y,XNAConfig.LoadTexture(
                    "control_base.png"), XNAConfig.LoadTexture("control_dot.png"),
                    bw, bh, dw, dh)
        {
          
        }

        public LControl(int x, int y, LTexture bs, LTexture dot, int bw, int bh,
                int dw, int dh):  base(x, y, bw, bh)
        {
            this.controlBase = bs;
            this.controlDot = dot;
            this.baseWidth = bw;
            this.baseHeight = bh;
            this.dotWidth = dw;
            this.dotHeight = dh;
            this.allowDiagonal = true;
            this.CenterOffset();
        }

        public bool IsAllowDiagonal()
        {
            return this.allowDiagonal;
        }

        public void SetAllowDiagonal(bool a)
        {
            this.allowDiagonal = a;
        }

        private void CenterOffset()
        {
            this.centerX = (baseWidth - dotWidth) / 2 + 1f;
            this.centerY = (baseHeight - dotHeight) / 2 + 1f;
        }

        private bool CheckAngle(float angle, float actual)
        {
            return actual > angle - DELTA && actual < angle + DELTA;
        }

        protected internal override void ProcessTouchPressed()
        {
            float relativeX = MathUtils.BringToBounds(0, baseWidth,
                    Touch.GetX() - GetScreenX())
                    / baseWidth - 0.5f;
            float relativeY = MathUtils.BringToBounds(0, baseHeight,
                    Touch.GetY() - GetScreenY())
                    / baseHeight - 0.5f;
            OnUpdateControlDot(relativeX, relativeY);
        }

        protected internal override void ProcessTouchReleased()
        {
            CenterOffset();
        }

        private void Position(float x, float y, int direction)
        {
            this.centerX = dotWidth * 0.5f + x * baseWidth;
            this.centerY = dotHeight * 0.5f + y * baseHeight;
            if (control != null)
            {
                switch (direction)
                {
                    case Config.TUP:
                        control.Up();
                        break;
                    case Config.UP:
                        control.Up45();
                        break;
                    case Config.TRIGHT:
                        control.Right();
                        break;
                    case Config.RIGHT:
                        control.Right45();
                        break;
                    case Config.TDOWN:
                        control.Down();
                        break;
                    case Config.DOWN:
                        control.Down45();
                        break;
                    case Config.TLEFT:
                        control.Left();
                        break;
                    case Config.LEFT:
                        control.Left45();
                        break;
                    default:
                        break;
                }
            }
        }

        private void OnUpdateControlDot(float x, float y)
        {
            if (x == 0 && y == 0)
            {
                Position(0, 0, Config.EMPTY);
                return;
            }
            if (this.allowDiagonal)
            {
                float angle = MathUtils.RadToDeg(MathUtils.Atan2(x, y)) + 180;
                if (this.CheckAngle(0, angle) || this.CheckAngle(360, angle))
                {
                    Position(0, -SIDE, Config.TUP);
                }
                else if (this.CheckAngle(45, angle))
                {
                    Position(-DIAGONAL, -DIAGONAL, Config.LEFT);
                }
                else if (this.CheckAngle(90, angle))
                {
                    Position(-SIDE, 0, Config.TLEFT);
                }
                else if (this.CheckAngle(135, angle))
                {
                    Position(-DIAGONAL, DIAGONAL, Config.DOWN);
                }
                else if (this.CheckAngle(180, angle))
                {
                    Position(0, SIDE, Config.TDOWN);
                }
                else if (this.CheckAngle(225, angle))
                {
                    Position(DIAGONAL, DIAGONAL, Config.RIGHT);
                }
                else if (this.CheckAngle(270, angle))
                {
                    Position(SIDE, 0, Config.TRIGHT);
                }
                else if (this.CheckAngle(315, angle))
                {
                    Position(DIAGONAL, -DIAGONAL, Config.UP);
                }
                else
                {
                    Position(0, 0, Config.EMPTY);
                }
            }
            else
            {
                if (MathUtils.Abs(x) > MathUtils.Abs(y))
                {
                    if (x > 0)
                    {
                        Position(SIDE, 0, Config.RIGHT);
                    }
                    else if (x < 0)
                    {
                        Position(-SIDE, 0, Config.LEFT);
                    }
                    else if (x == 0)
                    {
                        Position(0, 0, Config.EMPTY);
                    }
                }
                else
                {
                    if (y > 0)
                    {
                        Position(0, SIDE, Config.DOWN);
                    }
                    else if (y < 0)
                    {
                        Position(0, -SIDE, Config.UP);
                    }
                    else if (y == 0)
                    {
                        Position(0, 0, Config.EMPTY);
                    }
                }
            }

        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {
            if (this.visible)
            {
                g.SetAlpha(0.5f);
                g.DrawTexture(controlBase, x, y, baseWidth, baseHeight);
                g.DrawTexture(controlDot, x + centerX, y + centerY, dotWidth, dotHeight);
                g.SetAlpha(1f);
            }
        }

        public override void Dispose()
        {
            if (controlBase != null)
            {
                controlBase.Destroy();
                controlBase = null;
            }
            if (controlDot != null)
            {
                controlDot.Destroy();
                controlDot = null;
            }
        }

        public override string GetUIName()
        {
            return "Control";
        }

        public DigitalListener GetDigitalListener()
        {
            return control;
        }

        public void SetControl(DigitalListener c)
        {
            this.control = c;
        }

    }
}
