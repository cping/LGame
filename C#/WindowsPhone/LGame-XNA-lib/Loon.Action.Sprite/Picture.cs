using Loon.Core;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
namespace Loon.Action.Sprite {
	
	public class Picture : LObject, ISprite {
	
		private bool visible;
	
		private int width, height;
	
		private LTexture image;
	
		public Picture(string fileName) :this(fileName, 0, 0){
			
		}
	
		public Picture(int x, int y):this((LTexture) null, x, y) {
			
		}
	
		public Picture(string fileName, int x, int y):	this(new LTexture(fileName), x, y) {
		
		}
	
		public Picture(LTexture image):this(image, 0, 0) {
			
		}
	
		public Picture(LTexture image, int x, int y) {
			if (image != null) {
				this.SetImage(image);
				this.width = image.GetWidth();
				this.height = image.GetHeight();
			}
			this.SetLocation(x, y);
			this.visible = true;
		}
	
		public virtual void CreateUI(GLEx g) {
			if (visible) {
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(alpha);
				}
				g.DrawTexture(image, X(), Y());
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(1.0f);
				}
			}
		}

        public virtual bool Equals(Picture p)
        {
			if (image.Equals(p.image)) {
				return true;
			}
			if (this.width == p.width && this.height == p.height) {
				if (image.GetHashCode() == p.image.GetHashCode()) {
					return true;
				}
			}
			return false;
		}
	
		public override int GetHeight() {
			return height;
		}

        public override int GetWidth()
        {
			return width;
		}

        public override void Update(long timer)
        {
		}
	
		public virtual bool IsVisible() {
			return visible;
		}
	
		public virtual void SetVisible(bool visible) {
			this.visible = visible;
		}

        public virtual void Dispose()
        {
			if (image != null) {
				image.Dispose();
				image = null;
			}
		}

        public virtual void SetImage(string fileName)
        {
			this.image = new LTexture(fileName);
			this.width = image.GetWidth();
			this.height = image.GetHeight();
		}

        public virtual void SetImage(LTexture image)
        {
			this.image = image;
			this.width = image.GetWidth();
			this.height = image.GetHeight();
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
