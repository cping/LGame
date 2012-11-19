namespace Loon.Core
{

    using Loon.Core;
    using Loon.Utils;
    using Loon.Action.Sprite;
    using Loon.Core.Geom;

    public class LObjectCamera
    {

        private RectBox cameraRect;

        public float cameraX, cameraY;

        public float speedX, speedY;

        private float renderWidth;

        private float renderHeight;

        private Vector2f maxSpeed;

        private int horBorderPixel;

        private int vertBorderPixel;

        private RectBox visibleRect;

        private RectBox moveRect;

        private Bind follow;

        public LObjectCamera(object f, int width, int height)
            : this(LSystem.screenRect, f, width, height, -1, -1, null)
        {

        }

        public LObjectCamera(RectBox r, object f, int width, int height)
            : this(r, f, width, height, -1, -1, null)
        {

        }

        public LObjectCamera(RectBox r, object f, int width, int height,
                int horBorderPixel_0, int vertBorderPixel_1, Vector2f maxSpeed_2)
        {
            this.cameraX = 0;
            this.cameraY = 0;
            this.renderWidth = width;
            this.renderHeight = height;
            this.follow = new Bind(f);
            this.horBorderPixel = horBorderPixel_0;
            this.vertBorderPixel = vertBorderPixel_1;
            this.maxSpeed = maxSpeed_2;
            if (follow != null)
            {
                this.cameraX = follow.GetX() - (this.renderWidth / 2);
                this.cameraY = follow.GetY() - (this.renderHeight / 2);
            }
            this.cameraRect = r;
            this.visibleRect = new RectBox(cameraX - horBorderPixel_0, cameraY
                    - vertBorderPixel_1, renderWidth + horBorderPixel_0, renderHeight
                    + vertBorderPixel_1);
            this.moveRect = new RectBox(cameraX - horBorderPixel_0, cameraY
                    - vertBorderPixel_1, renderWidth + horBorderPixel_0, renderHeight
                    + vertBorderPixel_1);
            this.UpdateCamera();
        }

        public void UpdateCamera()
        {
            if (follow != null
                    && !moveRect.Contains(follow.GetX() + follow.GetWidth() / 2,
                            follow.GetY() + follow.GetHeight() / 2))
            {
                float targetCX = follow.GetX() - (this.renderWidth / 2);
                float targetCY = follow.GetY() - (this.renderHeight / 2);
                if (maxSpeed != null)
                {
                    if (MathUtils.Abs(targetCX - cameraX) > maxSpeed.x)
                    {
                        if (targetCX > cameraX)
                        {
                            cameraX += maxSpeed.x * 2;
                        }
                        else
                        {
                            cameraX -= maxSpeed.x * 2;
                        }
                    }
                    else
                    {
                        cameraX = targetCX;
                    }
                    if (MathUtils.Abs(targetCY - cameraY) > maxSpeed.y)
                    {
                        if (targetCY > cameraY)
                        {
                            cameraY += maxSpeed.y * 2;
                        }
                        else
                        {
                            cameraY -= maxSpeed.y * 2;
                        }
                    }
                    else
                    {
                        cameraY = targetCY;
                    }
                }
                else
                {
                    cameraX = targetCX;
                    cameraY = targetCY;
                }
            }
            if (cameraX < 0)
            {
                cameraX = 0;
            }
            if (cameraX + renderWidth > cameraRect.GetWidth())
            {
                cameraX = cameraRect.GetWidth() - renderWidth + 1;
            }
            if (cameraY < 0)
            {
                cameraY = 0;
            }
            if (cameraY + renderHeight > cameraRect.GetHeight())
            {
                cameraY = cameraRect.GetHeight() - renderHeight + 1;
            }
            visibleRect.SetBounds(cameraX - horBorderPixel, cameraY
                    - vertBorderPixel, renderWidth + horBorderPixel, renderHeight
                    + vertBorderPixel);
            moveRect.SetBounds(cameraX + horBorderPixel / 2 - speedX, cameraY
                    + vertBorderPixel / 2, renderWidth - horBorderPixel + speedY,
                    renderHeight - vertBorderPixel);
        }

        public float GetCameraX()
        {
            return cameraX;
        }

        public void SetCameraX(float cameraX_0)
        {
            this.cameraX = cameraX_0;
        }

        public float GetCameraY()
        {
            return cameraY;
        }

        public void SetCameraY(float cameraY_0)
        {
            this.cameraY = cameraY_0;
        }

        public float GetSpeedX()
        {
            return speedX;
        }

        public void SetSpeedX(float speedX_0)
        {
            this.speedX = speedX_0;
        }

        public float GetSpeedY()
        {
            return speedY;
        }

        public void SetSpeedY(float speedY_0)
        {
            this.speedY = speedY_0;
        }

        public RectBox GetMoveRect()
        {
            return moveRect;
        }

        public bool Contains(float x, float y, float w, float h)
        {
            return visibleRect.Contains(x, y, w, h);
        }

        public bool Intersects(float x, float y, float w, float h)
        {
            return visibleRect.Intersects(x, y, w, h);
        }

        public bool Includes(float x, float y)
        {
            return visibleRect.Includes(x, y);
        }
    }
}
