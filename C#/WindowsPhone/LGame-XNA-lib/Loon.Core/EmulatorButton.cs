using Loon.Core.Graphics;
using Loon.Core.Geom;
using Loon.Core.Graphics.Opengl;
using Loon.Action.Sprite;
namespace Loon.Core {

	public class EmulatorButton {
	
		private readonly LColor color = new LColor(LColor.gray.r, LColor.gray.g,
				LColor.gray.b, 0.5f);
	
		private bool disabled;
	
		private bool onClick;
	
		private RectBox bounds;
	
		private LTextureRegion bitmap;
	
		private float scaleWidth, scaleHeight;
	
		private int id;
	
		public EmulatorButton(string fileName, int w, int h, int x, int y):this(new LTextureRegion(fileName), w, h, x, y, true) {
			
		}
	
		public EmulatorButton(LTextureRegion img, int w, int h, int x, int y):this(img, w, h, x, y, true) {
			
		}
	
		public EmulatorButton(string fileName, int x, int y):	this(new LTextureRegion(fileName), 0, 0, x, y, false) {
		
		}
	
		public EmulatorButton(LTextureRegion img, int x, int y):this(img, 0, 0, x, y, false) {
			
		}
	
		public EmulatorButton(LTextureRegion img, int w, int h, int x, int y,
				bool flag):this(img, w, h, x, y, flag, img.GetRegionWidth(), img.GetRegionHeight()) {
			
		}
	
		public EmulatorButton(LTextureRegion img, int w, int h, int x, int y,
				bool flag, int sizew, int sizeh) {
			if (flag) {
				this.bitmap = new LTextureRegion(img, x, y, w, h);
			} else {
				this.bitmap = new LTextureRegion(img);
			}
			this.scaleWidth = sizew;
			this.scaleHeight = sizeh;
			this.bounds = new RectBox(0, 0, scaleWidth, scaleHeight);
		}
	
		public bool IsClick() {
			return onClick;
		}
	
		public RectBox GetBounds() {
			return bounds;
		}
	
		public void Hit(int nid, float x, float y) {
			onClick = bounds.Contains(x, y);
			id = nid;
		}
	
		public void Hit(float x, float y) {
			onClick = bounds.Contains(x, y);
			id = 0;
		}
	
		public void Unhit(int nid) {
			if (id == nid) {
				onClick = false;
			}
		}
	
		public void Unhit() {
			onClick = false;
		}
	
		public void SetX(int x) {
			this.bounds.SetX(x);
		}
	
		public void SetY(int y) {
			this.bounds.SetY(y);
		}
	
		public int GetX() {
			return bounds.X();
		}
	
		public int GetY() {
			return bounds.Y();
		}
	
		public void SetLocation(int x, int y) {
			this.bounds.SetX(x);
			this.bounds.SetY(y);
		}
	
		public void SetPointerId(int id_0) {
			this.id = id_0;
		}
	
		public int GetPointerId() {
			return this.id;
		}
	
		public bool IsEnabled() {
			return disabled;
		}
	
		public void Disable(bool flag) {
			this.disabled = flag;
		}
	
		public int GetHeight() {
			return bounds.height;
		}
	
		public int GetWidth() {
			return bounds.width;
		}
	
		public void SetSize(int w, int h) {
			this.bounds.SetWidth(w);
			this.bounds.SetHeight(h);
		}
	
		public void SetBounds(int x, int y, int w, int h) {
			this.bounds.SetBounds(x, y, w, h);
		}
	
		public void SetClickImage(LTexture on) {
			if (on == null) {
				return;
			}
			if (bitmap != null) {
				bitmap.Dispose();
			}
			this.bitmap = new LTextureRegion(on);
			this.SetSize(on.GetWidth(), on.GetHeight());
		}
	
		public void Draw(SpriteBatch batch) {
			if (!disabled) {
				if (onClick) {
					uint old = batch.GetIntColor();
					batch.SetColor(color);
					batch.Draw(bitmap, bounds.x, bounds.y, scaleWidth, scaleHeight);
					batch.SetColor(old);
				} else {
					batch.Draw(bitmap, bounds.x, bounds.y, scaleWidth, scaleHeight);
				}
			}
		}
	
	}
}
