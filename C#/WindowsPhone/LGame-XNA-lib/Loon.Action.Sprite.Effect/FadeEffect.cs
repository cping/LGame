using Loon.Core;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;

namespace Loon.Action.Sprite.Effect {
	
	public class FadeEffect : LObject, ISprite {

		public LColor color;
	
		public float time;
	
		public float currentFrame;
	
		public int type;
	
		public bool stop;
	
		private float opacity;
	
		private int offsetX, offsetY;
	
		private int width;
	
		private int height;
	
		private bool visible;
	
		public static FadeEffect GetInstance(int type, LColor c) {
			return new FadeEffect(c, 120, type, LSystem.screenRect.width,
					LSystem.screenRect.height);
	
		}
	
		public FadeEffect(LColor c, int delay, int type, int w, int h) {
			this.visible = true;
			this.type = type;
			this.SetDelay(delay);
			this.SetColor(c);
			this.width = w;
			this.height = h;
		}
	
		public float GetDelay() {
			return time;
		}
	
		public void SetDelay(int delay) {
			this.time = delay;
			if (type == ISprite_Constants.TYPE_FADE_IN) {
				this.currentFrame = this.time;
			} else {
				this.currentFrame = 0;
			}
		}
	
		public LColor GetColor() {
			return color;
		}
	
		public void SetColor(LColor color) {
			this.color = color;
		}
	
		public float GetCurrentFrame() {
			return currentFrame;
		}
	
		public void SetCurrentFrame(float currentFrame) {
			this.currentFrame = currentFrame;
		}
	
		public bool IsStop() {
			return stop;
		}
	
		public void SetStop(bool stop) {
			this.stop = stop;
		}
	
		public int GetIType() {
			return type;
		}
	
		public void SetIType(int type) {
			this.type = type;
		}
	
		public void SetVisible(bool visible) {
			this.opacity = (visible) ? 255 : 0;
			this.visible = visible;
		}
	
		public bool IsVisible() {
			return visible;
		}
	
		public void SetOpacity(float opacity) {
			this.opacity = opacity;
		}
	
		public float GetOpacity() {
			return opacity;
		}

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (stop)
            {
                return;
            }
            float alpha = (currentFrame / time);
            SetOpacity(alpha);
            if (alpha > 0f)
            {
                g.SetColor(color.R, color.G, color.B, (byte)(color.A * alpha));
                g.FillRect(offsetX + this.X(), offsetY + this.Y(), width, height);
                g.ResetColor();
            }
        }

        public override void Update(long timer)
        {
            if (type == ISprite_Constants.TYPE_FADE_IN)
            {
				currentFrame--;
				if (currentFrame == 0) {
					SetOpacity(0);
					stop = true;
				}
			} else {
				currentFrame++;
				if (currentFrame == time) {
					SetOpacity(0);
					stop = true;
				}
			}
		}
	
		public RectBox GetCollisionBox() {
			return GetRect(X(), Y(), GetWidth(), GetHeight());
		}
	
		public override int GetHeight() {
			return height;
		}

        public override int GetWidth()
        {
			return width;
		}
	
		public int GetOffsetX() {
			return offsetX;
		}
	
		public void SetOffsetX(int offsetX) {
			this.offsetX = offsetX;
		}
	
		public int GetOffsetY() {
			return offsetY;
		}
	
		public void SetOffsetY(int offsetY) {
			this.offsetY = offsetY;
		}
	
		public LTexture GetBitmap() {
			return null;
		}
	
		public void Dispose() {
	
		}
	
	}
}
