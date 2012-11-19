namespace Loon.Action.Sprite
{
    using Loon.Core;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Geom;
    using Loon.Utils.Debug;

    public class Picture : LObject, ISprite
    {
        private bool visible;

        private int width, height;

        private LTexture image;

        public Picture(string fileName)
            : this(fileName, 0, 0)
        {

        }

        public Picture(int x, int y)
            : this((LTexture)null, x, y)
        {

        }

        public Picture(string fileName, int x, int y)
            : this(new LTexture(fileName), x, y)
        {

        }

        public Picture(LTexture i)
            : this(i, 0, 0)
        {

        }

        public Picture(LTexture i, int x, int y)
        {
            this.alpha = 1f;
            if (i != null)
            {
                this.SetImage(i);
                this.width = i.GetWidth();
                this.height = i.GetHeight();
            }
            this.SetLocation(x, y);
            this.visible = true;
        }

        public virtual void CreateUI(GLEx g)
        {
            if (visible)
            {
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(alpha);
                }
                g.DrawTexture(image, X(), Y());
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(1.0f);
                }
            }
        }

        public bool Equals(Picture p)
        {

            if (this.width == p.width && this.height == p.height)
            {
             
                if (image.GetHashCode() == p.image.GetHashCode())
                {
                    return true;
                }
            }
            return false;
        }

        public override int GetHeight()
        {
            return height;
        }

        public override int GetWidth()
        {
            return width;
        }

        public override void Update(long timer)
        {
        }

        public virtual bool IsVisible()
        {
            return visible;
        }

        public virtual void SetVisible(bool v)
        {
            this.visible = v;
        }

        public virtual void Dispose()
        {
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
        }

        public void SetImage(string fileName)
        {
            this.image = new LTexture(fileName);
            this.width = image.GetWidth();
            this.height = image.GetHeight();
        }

        public void SetImage(LTexture image_0)
        {
            this.image = image_0;
            this.width = image_0.GetWidth();
            this.height = image_0.GetHeight();
        }

        public virtual RectBox GetCollisionBox()
        {
            return GetRect(X(), Y(), width, height);
        }

        public virtual LTexture GetBitmap()
        {
            return image;
        }

    }
}
