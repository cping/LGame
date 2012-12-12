using Loon.Core.Graphics.Opengl;
using Loon.Utils;
using Loon.Core.Input;

namespace Loon.Core.Graphics.Component {
	
	public class LButton : LComponent {
	
		private string text = null;
	
		private bool over, pressed, exception;
	
		private int pressedTime, offsetLeft, offsetTop, type;
	
		private LFont font = LFont.GetDefaultFont();
	
		private LColor fontColor = LColor.white;
	
		public LButton(string fileName):this(fileName, null, 0, 0) {
			
		}
	
		public LButton(string fileName, string text, int x, int y):this(new LTexture(fileName), text, x, y) {
			
		}
	
		public LButton(LTexture img, string text, int x, int y):this(img, text, img.GetWidth(), img.GetHeight(), x, y) {
			
		}
	
		public LButton(string fileName, int row, int col):this(fileName, null, row, col, 0, 0) {
			
		}
	
		public LButton(string fileName, string text, int row, int col, int x, int y):this(LTextures.LoadTexture(fileName), text, row, col, x, y) {
		
		}
	
		public LButton(LTexture img, string text, int row, int col, int x, int y):this(TextureUtils.GetSplitTextures(img, row, col), text, row, col, x, y) {
			
		}
	
		public LButton(LTexture[] img, string text, int row, int col, int x, int y):base(x, y, row, col) {
			this.SetImages(img);
			this.text = text;
		}
	
		public LButton(string text, int x, int y, int w, int h):base(x, y, w, h) {
			this.text = text;
		}

        public virtual void SetImages(params LTexture[] images)
        {
			LTexture[] buttons = new LTexture[4];
			if (images != null) {
				int size = images.Length;
				this.type = size;
				switch (size) {
				case 1:
					buttons[0] = images[0];
					buttons[1] = images[0];
					buttons[2] = images[0];
					buttons[3] = images[0];
					break;
				case 2:
					buttons[0] = images[0];
					buttons[1] = images[1];
					buttons[2] = images[0];
					buttons[3] = images[0];
					break;
				case 3:
					buttons[0] = images[0];
					buttons[1] = images[1];
					buttons[2] = images[2];
					buttons[3] = images[0];
					break;
				case 4:
					buttons = images;
					break;
				default:
					exception = true;
					break;
				}
			}
			if (!exception) {
				this.SetImageUI(buttons, true);
			}
	
		}
	
		public override void CreateUI(GLEx g, int x, int y, LComponent component,
				LTexture[] buttonImage) {
			LButton button = (LButton) component;
			if (buttonImage != null) {
				if (!button.IsEnabled()) {
					g.DrawTexture(buttonImage[3], x, y);
				} else if (button.IsTouchPressed()) {
					g.DrawTexture(buttonImage[2], x, y);
				} else if (button.IsTouchOver()) {
					g.DrawTexture(buttonImage[1], x, y);
				} else {
					if (type == 1) {
						g.DrawTexture(buttonImage[0], x, y, LColor.gray);
					} else {
						g.DrawTexture(buttonImage[0], x, y);
					}
				}
			}
			if (text != null) {
				LFont old = g.GetFont();
				g.SetFont(font);
				g.SetColor(fontColor);
				g.DrawString(
						text,
						x + button.GetOffsetLeft()
								+ (button.GetWidth() - font.StringWidth(text)) / 2,
						y + button.GetOffsetTop()
								+ (button.GetHeight() - font.GetLineHeight()) / 2
								+ font.GetLineHeight());
				g.SetFont(old);
				g.ResetColor();
			}
		}

        public override void Update(long timer)
        {
			if (this.pressedTime > 0 && --this.pressedTime <= 0) {
				this.pressed = false;
			}
		}
	
		public bool IsTouchOver() {
			return this.over;
		}
	
		public bool IsTouchPressed() {
			return this.pressed;
		}
	
		public string GetText() {
			return this.text;
		}
	
		public void SetText(string st) {
			this.text = st;
		}
	
		protected internal override void ProcessTouchDragged() {
			if (this.input.GetTouchPressed() == Touch.TOUCH_MOVE) {
				this.over = this.pressed = this.Intersects(this.input.GetTouchX(),
						this.input.GetTouchY());
			}
		}
	
		public void DoClick() {
			if (Click != null) {
                Click.DoClick(this);
			}
		}
	
		public void DownClick() {
			if (Click != null) {
				Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
			}
		}
	
		public void UpClick() {
			if (Click != null) {
				Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
			}
		}

        protected internal override void ProcessTouchClicked()
        {
			if (this.input.GetTouchReleased() == Touch.TOUCH_UP) {
				this.DoClick();
			}
		}

        protected internal override void ProcessTouchPressed()
        {
			if (this.input.GetTouchPressed() == Touch.TOUCH_DOWN) {
				this.DownClick();
				this.pressed = true;
			}
		}

        protected internal override void ProcessTouchReleased()
        {
			if (this.input.GetTouchReleased() == Touch.TOUCH_UP) {
				this.UpClick();
				this.pressed = false;
			}
		}

        protected internal override void ProcessTouchEntered()
        {
			this.over = true;
		}

        protected internal override void ProcessTouchExited()
        {
			this.over = this.pressed = false;
		}

        protected internal override void ProcessKeyPressed()
        {
			if (this.IsSelected() && this.input.GetKeyPressed() == Key.ENTER) {
				this.pressedTime = 5;
				this.pressed = true;
				this.DoClick();
			}
		}

        protected internal override void ProcessKeyReleased()
        {
			if (this.IsSelected() && this.input.GetKeyReleased() == Key.ENTER) {
				this.pressed = false;
			}
		}

        public virtual bool IsException()
        {
			return exception;
		}

        public override string GetUIName()
        {
			return "Button";
		}

        public virtual LFont GetFont()
        {
			return font;
		}

        public virtual void SetFont(LFont font)
        {
			this.font = font;
		}

        public virtual LColor GetFontColor()
        {
			return fontColor;
		}

        public virtual void SetFontColor(LColor fontColor)
        {
			this.fontColor = fontColor;
		}

        public virtual int GetOffsetLeft()
        {
			return offsetLeft;
		}

        public virtual void SetOffsetLeft(int offsetLeft)
        {
			this.offsetLeft = offsetLeft;
		}

        public virtual int GetOffsetTop()
        {
			return offsetTop;
		}

        public virtual void SetOffsetTop(int offsetTop)
        {
			this.offsetTop = offsetTop;
		}
	
	}
}
