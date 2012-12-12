using Loon.Core;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
namespace Loon.Action.Sprite {
	
	public class Label : LObject, ISprite {
	
		private const long serialVersionUID = 1L;
	
		private LFont font;
	
		private bool visible;
	
		private int width, height;
	
		private LColor color;
	
		private string label;
	
		public Label(string label, int x, int y):this(LFont.GetDefaultFont(), label, x, y) {
			
		}
	
		public Label(string label, string font, int type, int size, int x, int y):this(LFont.GetFont(font, type, size), label, x, y) {
			
		}
	
		public Label(LFont font, string label, int x, int y) {
			this.font = font;
			this.label = label;
			this.color = LColor.black;
			this.visible = true;
			this.SetLocation(x, y);
		}
	
		public void SetFont(string fontName, int type, int size) {
			SetFont(LFont.GetFont(fontName, type, size));
		}
	
		public void SetFont(LFont font) {
			this.font = font;
		}
	
		public void CreateUI(GLEx g) {
			if (visible) {
				LFont oldFont = g.GetFont();
				uint oldColor = g.GetColorRGB();
				g.SetFont(font);
				g.SetColor(color);
				this.width = font.StringWidth(label);
				this.height = font.GetSize();
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(alpha);
					g.DrawString(label, X(), Y());
					g.SetAlpha(1.0F);
				} else {
					g.DrawString(label, X(), Y());
				}
				g.SetFont(oldFont);
				g.SetColor(oldColor);
			}
		}
	
		public override int GetWidth() {
			return width;
		}

        public override int GetHeight()
        {
			return height;
		}

        public override void Update(long timer)
        {
	
		}
	
		public RectBox GetCollisionBox() {
			return GetRect(X(), Y(), width, height);
		}
	
		public virtual bool IsVisible() {
			return visible;
		}
	
		public virtual void SetVisible(bool visible) {
			this.visible = visible;
		}
	
		public string GetLabel() {
			return label;
		}
	
		public void SetLabel(int label) {
			SetLabel(label.ToString());
		}
	
		public void SetLabel(string label) {
			this.label = label;
		}
	
		public LColor GetColor() {
			return color;
		}
	
		public void SetColor(LColor color) {
			this.color = color;
		}
	
		public LTexture GetBitmap() {
			return null;
		}
	
		public void Dispose() {
	
		}
	
	}
}
