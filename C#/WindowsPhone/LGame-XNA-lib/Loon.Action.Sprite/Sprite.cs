using Loon.Core;
using Loon.Core.Graphics.Device;
using Loon.Core.Graphics.Opengl;
using Loon.Java;
using Loon.Utils;
using Loon.Core.Geom;
using System;
using Loon.Action.Collision;
using Loon.Core.Graphics;
using Loon.Action.Map;
namespace Loon.Action.Sprite
{

    public class Sprite : LObject, ActionBind, ISprite
    {

        private const long defaultTimer = 150;

        private bool visible = true;

        private string spriteName;

        private LTexture image;

        private Animation animation = new Animation();

        private int transform;

        private float scaleX = 1, scaleY = 1;

        public Sprite()
            : this(0, 0)
        {

        }

        public Sprite(float x, float y)
            : this("Sprite" + JavaRuntime.CurrentTimeMillis(), x, y)
        {

        }

        private Sprite(string spriteName, float x, float y)
        {
            this.SetLocation(x, y);
            this.spriteName = spriteName;
            this.visible = true;
            this.transform = LTrans.TRANS_NONE;
        }


        public Sprite(string fileName, int row, int col)
            : this(fileName, -1, 0, 0, row, col, defaultTimer)
        {

        }

        public Sprite(string fileName, int row, int col, long timer)
            : this(fileName, -1, 0, 0, row, col, timer)
        {

        }

        public Sprite(string fileName, float x, float y, int row, int col)
            : this(fileName, x, y, row, col, defaultTimer)
        {
            ;
        }

        private Sprite(string fileName, float x, float y, int row, int col,
                long timer)
            : this(fileName, -1, x, y, row, col, timer)
        {

        }

        public Sprite(string fileName, int maxFrame, float x, float y, int row,
                int col)
            : this(fileName, maxFrame, x, y, row, col, defaultTimer)
        {

        }

        public Sprite(string fileName, int maxFrame, float x, float y, int row,
                int col, long timer)
            : this("Sprite" + JavaRuntime.CurrentTimeMillis(), fileName, maxFrame, x, y,
                row, col, timer)
        {

        }

        public Sprite(string spriteName, string fileName, int maxFrame, float x,
                float y, int row, int col, long timer)
            : this(spriteName, TextureUtils.GetSplitTextures(fileName, row, col),
                maxFrame, x, y, timer)
        {

        }

        public Sprite(string fileName)
            : this(new LTexture(fileName))
        {

        }

        public Sprite(LTexture img)
            : this(new LTexture[] { img }, 0, 0)
        {

        }

        public Sprite(LTexture[] images)
            : this(images, 0, 0)
        {

        }

        public Sprite(LTexture[] images, float x, float y)
            : this(images, x, y, defaultTimer)
        {

        }

        public Sprite(LTexture[] images, long timer)
            : this(images, -1, 0, 0, defaultTimer)
        {

        }

        public Sprite(LTexture[] images, float x, float y, long timer)
            : this(images, -1, x, y, timer)
        {

        }

        public Sprite(LTexture[] images, int maxFrame, float x, float y, long timer)
            : this("Sprite" + JavaRuntime.CurrentTimeMillis(), images, maxFrame, x, y,
                timer)
        {

        }

        public Sprite(string spriteName, LTexture[] images, int maxFrame, float x,
                float y, long timer)
        {
            this.SetLocation(x, y);
            this.spriteName = spriteName;
            this.SetAnimation(animation, images, maxFrame, timer);
            this.visible = true;
            this.transform = LTrans.TRANS_NONE;
        }

        public void SetRunning(bool running)
        {
            animation.SetRunning(running);
        }

        public int GetTotalFrames()
        {
            return animation.GetTotalFrames();
        }

        public void SetCurrentFrameIndex(int index)
        {
            animation.SetCurrentFrameIndex(index);
        }

        public int GetCurrentFrameIndex()
        {
            return animation.GetCurrentFrameIndex();
        }

        public int CenterX(int x)
        {
            return CenterX(this, x);
        }

        public static int CenterX(Sprite sprite, int x)
        {
            int newX = x - (sprite.GetWidth() / 2);
            if (newX + sprite.GetWidth() >= LSystem.screenRect.width)
            {
                return (LSystem.screenRect.width - sprite.GetWidth() - 1);
            }
            if (newX < 0)
            {
                return x;
            }
            else
            {
                return newX;
            }
        }

        public int CenterY(int y)
        {
            return CenterY(this, y);
        }

        public static int CenterY(Sprite sprite, int y)
        {
            int newY = y - (sprite.GetHeight() / 2);
            if (newY + sprite.GetHeight() >= LSystem.screenRect.height)
            {
                return (LSystem.screenRect.height - sprite.GetHeight() - 1);
            }
            if (newY < 0)
            {
                return y;
            }
            else
            {
                return newY;
            }
        }

        private void SetAnimation(Animation myAnimation, LTexture[] images,
                int maxFrame, long timer)
        {
            if (maxFrame != -1)
            {
                for (int i = 0; i < maxFrame; i++)
                {
                    myAnimation.AddFrame(images[i], timer);
                }
            }
            else
            {
                for (int i = 0; i < images.Length; i++)
                {
                    myAnimation.AddFrame(images[i], timer);
                }
            }
        }

        public void SetAnimation(string fileName, int maxFrame, int row, int col,
                long timer)
        {
            SetAnimation(new Animation(),
                    TextureUtils.GetSplitTextures(fileName, row, col), maxFrame,
                    timer);
        }

        public void SetAnimation(string fileName, int row, int col, long timer)
        {
            SetAnimation(fileName, -1, row, col, timer);
        }

        public void SetAnimation(LTexture[] images, int maxFrame, long timer)
        {
            SetAnimation(new Animation(), images, maxFrame, timer);
        }

        public void SetAnimation(LTexture[] images, long timer)
        {
            SetAnimation(new Animation(), images, -1, timer);
        }

        public void SetAnimation(Animation animation)
        {
            this.animation = animation;
        }

        public Animation GetAnimation()
        {
            return animation;
        }

        public override void Update(long timer)
        {
            if (visible)
            {
                animation.Update(timer);
            }
        }

        public void UpdateLocation(Vector2f vector)
        {
            this.SetX(MathUtils.Round(vector.GetX()));
            this.SetY(MathUtils.Round(vector.GetY()));
        }

        public LTexture GetImage()
        {
            return animation.GetSpriteImage();
        }

        public override int GetWidth()
        {
            LTexture si = animation.GetSpriteImage();
            if (si == null)
            {
                return -1;
            }
            return (int)(si.GetWidth() * scaleX);
        }

        public override int GetHeight()
        {
            LTexture si = animation.GetSpriteImage();
            if (si == null)
            {
                return -1;
            }
            return (int)(si.GetHeight() * scaleY);
        }

        public Point GetMiddlePoint()
        {
            return new Point(GetLocation().X() + GetWidth() / 2, GetLocation().Y()
                    + GetHeight() / 2);
        }

        public float GetDistance(Sprite second)
        {
            return (float)this.GetMiddlePoint()
                    .DistanceTo(second.GetMiddlePoint());
        }

        public RectBox GetCollisionBox()
        {
            return GetRect(GetLocation().X(), GetLocation().Y(), GetWidth(),
                    GetHeight());
        }

        public bool IsRectToRect(Sprite sprite)
        {
            return CollisionHelper.IsRectToRect(this.GetCollisionBox(),
                    sprite.GetCollisionBox());
        }

        public bool IsCircToCirc(Sprite sprite)
        {
            return CollisionHelper.IsCircToCirc(this.GetCollisionBox(),
                    sprite.GetCollisionBox());
        }

        public bool IsRectToCirc(Sprite sprite)
        {
            return CollisionHelper.IsRectToCirc(this.GetCollisionBox(),
                    sprite.GetCollisionBox());
        }

        private LColor filterColor;

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            image = animation.GetSpriteImage();
            if (image == null)
            {
                return;
            }
            float width = (image.GetWidth() * scaleX);
            float height = (image.GetHeight() * scaleY);
            if (filterColor == null)
            {
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(alpha);
                }
                if (LTrans.TRANS_NONE == transform)
                {
                    g.DrawTexture(image, X(), Y(), width, height, rotation);
                }
                else
                {
                    g.DrawRegion(image, 0, 0, GetWidth(), GetHeight(), transform,
                            X(), Y(), LGraphics.TOP | LGraphics.LEFT);
                }
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(1);
                }
                return;
            }
            else
            {
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(alpha);
                }
                if (LTrans.TRANS_NONE == transform)
                {
                    g.DrawTexture(image, X(), Y(), width, height, rotation,
                            filterColor);
                }
                else
                {
                    g.DrawRegion(image, 0, 0, GetWidth(), GetHeight(), transform,
                            X(), Y(), LGraphics.TOP | LGraphics.LEFT, filterColor);
                }
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(1);
                }
                return;
            }
        }

        public virtual bool IsVisible()
        {
            return visible;
        }

        public virtual void SetVisible(bool visible)
        {
            this.visible = visible;
        }

        public string GetSpriteName()
        {
            return spriteName;
        }

        public void SetSpriteName(string spriteName)
        {
            this.spriteName = spriteName;
        }

        public int GetTransform()
        {
            return transform;
        }

        public void SetTransform(int transform)
        {
            this.transform = transform;
        }

        public LColor GetFilterColor()
        {
            return filterColor;
        }

        public void SetFilterColor(LColor filterColor)
        {
            this.filterColor = filterColor;
        }

        public LTexture GetBitmap()
        {
            return this.image;
        }

        public float GetScaleX()
        {
            return scaleX;
        }

        public void SetScaleX(float scaleX)
        {
            this.scaleX = scaleX;
        }

        public float GetScaleY()
        {
            return scaleY;
        }

        public void SetScaleY(float scaleY)
        {
            this.scaleY = scaleY;
        }

        public void Dispose()
        {
            this.visible = false;
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
            if (animation != null)
            {
                animation.Dispose();
                animation = null;
            }
        }

        public Field2D GetField2D()
        {
            return null;
        }

        public bool IsBounded()
        {
            return false;
        }

        public bool IsContainer()
        {
            return false;
        }

        public bool InContains(int x, int y, int w, int h)
        {
            return false;
        }

        public RectBox GetRectBox()
        {
            return GetCollisionBox();
        }

        public int GetContainerWidth()
        {
            return 0;
        }

        public int GetContainerHeight()
        {
            return 0;
        }

        public void SetScale(float sx, float sy)
        {
            this.scaleX = sx;
            this.scaleY = sy;
        }
    }
}
