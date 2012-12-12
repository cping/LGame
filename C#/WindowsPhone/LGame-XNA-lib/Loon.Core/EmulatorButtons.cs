using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
using Loon.Action.Sprite;
using System.Runtime.CompilerServices;

namespace Loon.Core {
	
	public class EmulatorButtons : LRelease {
	
		private LTextureRegion dpad, buttons;
	
		private EmulatorButton up, left, right, down;
	
		private EmulatorButton triangle, square, circle, cancel;
	
		private EmulatorListener emulatorListener;
	
		private int offsetX, offsetY, width, height;
	
		private const int offset = 10;
	
		private bool visible;
	
		private LTexturePack pack;
	
		public EmulatorButtons(EmulatorListener el):this(el, LSystem.screenRect.width, LSystem.screenRect.height) {
			
		}
	
		public EmulatorButtons(EmulatorListener el, int w, int h):this(el, w, h, LSystem.EMULATOR_BUTTIN_SCALE) {
			
		}
	
		public EmulatorButtons(EmulatorListener el, int w, int h, float scale) {
			this.emulatorListener = el;
			if (pack == null) {
				pack = new LTexturePack();
                pack.PutImage(XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "e1.png"));
				pack.PutImage(XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "e2.png"));
				pack.Pack(Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
			}
			RectBox.Rect2i bounds = pack.GetEntry(0).getBounds();
			this.dpad = new LTextureRegion(pack.GetTexture(), bounds.left,
					bounds.top, bounds.right, bounds.bottom);
			bounds = pack.GetEntry(1).getBounds();
			this.buttons = new LTextureRegion(pack.GetTexture(), bounds.left,
					bounds.top, bounds.right, bounds.bottom);
			this.width = w;
			this.height = h;
	
			if (scale <= 0f) {
				this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true, 60, 60);
				this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true, 60, 60);
				this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true, 60, 60);
				this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true, 60, 60);
	
				this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
						68, 68);
				this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true, 68,
						68);
				this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true, 68,
						68);
				this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true, 68,
						68);
			} else {
	
				this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true,
						(int) (60 * scale), (int) (60 * scale));
				this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true,
						(int) (60 * scale), (int) (60 * scale));
				this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true,
						(int) (60 * scale), (int) (60 * scale));
				this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true,
						(int) (60 * scale), (int) (60 * scale));
	
				this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
						(int) (68 * scale), (int) (68 * scale));
				this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true,
						(int) (68 * scale), (int) (68 * scale));
				this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true,
						(int) (68 * scale), (int) (68 * scale));
				this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true,
						(int) (68 * scale), (int) (68 * scale));
			}
	
			if (dpad != null) {
				dpad.Dispose();
				dpad = null;
			}
			if (buttons != null) {
				buttons.Dispose();
				buttons = null;
			}
			this.visible = true;
	
			this.SetLocation(0, 0);
		}
	

		public void SetLocation(int x, int y) {
			if (!visible) {
				return;
			}
			this.offsetX = x;
			this.offsetY = y;
			up.SetLocation((offsetX + up.GetWidth()) + offset, offsetY
					+ (height - up.GetHeight() * 3) - offset);
			left.SetLocation((offsetX + 0) + offset,
					offsetY + (height - left.GetHeight() * 2) - offset);
			right.SetLocation((offsetX + right.GetWidth() * 2) + offset, offsetY
					+ (height - right.GetHeight() * 2) - offset);
			down.SetLocation((offsetX + down.GetWidth()) + offset, offsetY
					+ (height - down.GetHeight()) - offset);
	
			if (LSystem.screenRect.height >= LSystem.screenRect.width) {
				triangle.SetLocation(offsetX + (width - triangle.GetWidth() * 2)
						- offset, height - (triangle.GetHeight() * 4)
						- (offset * 2));
				square.SetLocation(offsetX + (width - square.GetWidth()) - offset,
						height - (square.GetHeight() * 3) - (offset * 2));
				circle.SetLocation(offsetX + (width - circle.GetWidth() * 3)
						- offset, height - (circle.GetHeight() * 3) - (offset * 2));
				cancel.SetLocation(offsetX + (width - cancel.GetWidth() * 2)
						- offset, offsetY + height - (circle.GetHeight() * 2)
						- (offset * 2));
			} else {
				triangle.SetLocation(offsetX + (width - triangle.GetWidth() * 2)
						- offset, height - (triangle.GetHeight() * 3) - offset);
				square.SetLocation(offsetX + (width - square.GetWidth()) - offset,
						height - (square.GetHeight() * 2) - offset);
				circle.SetLocation(offsetX + (width - circle.GetWidth() * 3)
						- offset, height - (circle.GetHeight() * 2) - offset);
				cancel.SetLocation(offsetX + (width - cancel.GetWidth() * 2)
						- offset, offsetY + height - (circle.GetHeight()) - offset);
			}
		}
	
		public bool IsClick() {
			if (up.IsClick()) {
				return true;
			}
			if (left.IsClick()) {
				return true;
			}
			if (down.IsClick()) {
				return true;
			}
			if (right.IsClick()) {
				return true;
			}
			if (triangle.IsClick()) {
				return true;
			}
			if (square.IsClick()) {
				return true;
			}
			if (circle.IsClick()) {
				return true;
			}
			if (cancel.IsClick()) {
				return true;
			}
			return false;
		}
	
		public void Hide() {
			HideLeft();
			HideRight();
		}
	
		public void Show() {
			ShowLeft();
			ShowRight();
		}
	
		public void HideLeft() {
			up.Disable(true);
			left.Disable(true);
			right.Disable(true);
			down.Disable(true);
		}
	
		public void ShowLeft() {
			up.Disable(false);
			left.Disable(false);
			right.Disable(false);
			down.Disable(false);
		}
	
		public void HideRight() {
			triangle.Disable(true);
			square.Disable(true);
			circle.Disable(true);
			cancel.Disable(true);
		}
	
		public void ShowRight() {
			triangle.Disable(false);
			square.Disable(false);
			circle.Disable(false);
			cancel.Disable(false);
		}
	
	
		public int GetX() {
			return offsetX;
		}
	
		public int GetY() {
			return offsetY;
		}
	
		public EmulatorButton[] GetEmulatorButtons() {
			return new EmulatorButton[] { up, left, right, down, triangle, square,
					circle, cancel };
		}

        public void OnEmulatorButtonEvent(Microsoft.Xna.Framework.Input.Touch.TouchLocation touch, float touchX, float touchY)
        {
            if (!visible)
            {
                return;
            }
            switch (touch.State)
            {
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Pressed:
                    Hit(touch.Id, touchX, touchY);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Released:
                    Unhit(touch.Id);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Moved:
                    Hit(touch.Id, touchX, touchY);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Invalid:
                    Unhit(touch.Id);
                    break;
            }
        }

        private SpriteBatch batch = new SpriteBatch(128);
	
		public void Draw(GLEx g) {
			if (!visible) {
				return;
			}
            batch.Begin();
			batch.HalfAlpha();
			up.Draw(batch);
			left.Draw(batch);
			right.Draw(batch);
			down.Draw(batch);
			triangle.Draw(batch);
			square.Draw(batch);
			circle.Draw(batch);
			cancel.Draw(batch);
			batch.ResetColor();
            batch.End();
		}
	
		private void CheckOn() {
	
			if (emulatorListener == null) {
				return;
			}
			if (up.IsClick()) {
				emulatorListener.OnUpClick();
			}
			if (left.IsClick()) {
				emulatorListener.OnLeftClick();
			}
			if (right.IsClick()) {
				emulatorListener.OnRightClick();
			}
			if (down.IsClick()) {
				emulatorListener.OnDownClick();
			}
	
			if (triangle.IsClick()) {
				emulatorListener.OnTriangleClick();
			}
			if (square.IsClick()) {
				emulatorListener.OnSquareClick();
			}
			if (circle.IsClick()) {
				emulatorListener.OnCircleClick();
			}
			if (cancel.IsClick()) {
				emulatorListener.OnCancelClick();
			}
		}
	
		public void Hit(int id, float x, float y) {
	
			if (!visible) {
				return;
			}
	
			up.Hit(id, x, y);
			left.Hit(id, x, y);
			right.Hit(id, x, y);
			down.Hit(id, x, y);
	
			triangle.Hit(id, x, y);
			square.Hit(id, x, y);
			circle.Hit(id, x, y);
			cancel.Hit(id, x, y);
	
			CheckOn();
		}
	
		private void CheckUn(int id) {
			if (emulatorListener == null) {
				return;
			}
			if (up.IsClick() && up.GetPointerId() == id) {
				emulatorListener.UnUpClick();
			}
			if (left.IsClick() && left.GetPointerId() == id) {
				emulatorListener.UnLeftClick();
			}
			if (right.IsClick() && right.GetPointerId() == id) {
				emulatorListener.UnRightClick();
			}
			if (down.IsClick() && down.GetPointerId() == id) {
				emulatorListener.UnDownClick();
			}
			if (triangle.IsClick() && triangle.GetPointerId() == id) {
				emulatorListener.UnTriangleClick();
			}
			if (square.IsClick() && square.GetPointerId() == id) {
				emulatorListener.UnSquareClick();
			}
			if (circle.IsClick() && circle.GetPointerId() == id) {
				emulatorListener.UnCircleClick();
			}
			if (cancel.IsClick() && cancel.GetPointerId() == id) {
				emulatorListener.UnCancelClick();
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Unhit(int id) {
	
			if (!visible) {
				return;
			}
	
			CheckUn(id);
	
			up.Unhit(id);
			left.Unhit(id);
			right.Unhit(id);
			down.Unhit(id);
	
			triangle.Unhit(id);
			square.Unhit(id);
			circle.Unhit(id);
			cancel.Unhit(id);
		}
	
		public bool IsVisible() {
			return visible;
		}
	
		public void SetVisible(bool visible_0) {
			if (!visible_0) {
				Release();
			}
			this.visible = visible_0;
		}
	
		public EmulatorListener GetEmulatorListener() {
			return emulatorListener;
		}
	
		public void SetEmulatorListener(EmulatorListener emulator) {
			this.emulatorListener = emulator;
		}
	
		public EmulatorButton GetUp() {
			return up;
		}
	
		public EmulatorButton GetLeft() {
			return left;
		}
	
		public EmulatorButton GetRight() {
			return right;
		}
	
		public EmulatorButton GetDown() {
			return down;
		}
	
		public EmulatorButton GetTriangle() {
			return triangle;
		}
	
		public EmulatorButton GetSquare() {
			return square;
		}
	
		public EmulatorButton GetCircle() {
			return circle;
		}
	
		public EmulatorButton GetCancel() {
			return cancel;
		}
	
		public void Release() {
	
			up.Unhit();
			left.Unhit();
			right.Unhit();
			down.Unhit();
	
			triangle.Unhit();
			square.Unhit();
			circle.Unhit();
			cancel.Unhit();
	
			if (emulatorListener != null) {
				emulatorListener.UnUpClick();
				emulatorListener.UnLeftClick();
				emulatorListener.UnRightClick();
				emulatorListener.UnDownClick();
				emulatorListener.UnTriangleClick();
				emulatorListener.UnSquareClick();
				emulatorListener.UnCircleClick();
				emulatorListener.UnCancelClick();
			}
		}
	
		public void Dispose() {
			if (pack != null) {
				pack.Dispose();
				pack = null;
			}
			if (batch != null) {
				batch.Dispose();
				batch = null;
			}
		}
	
	}
}
