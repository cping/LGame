using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
using Loon.Action.Sprite;
using System.Runtime.CompilerServices;
using Loon.Utils;

namespace Loon.Core {
	
	public class EmulatorButtons : LRelease {

	 class up_monitor : EmulatorButton.Monitor {

         private EmulatorListener _l;

         public up_monitor(EmulatorListener l)
         {
             this._l = l;
         }

			public void Free() {
                if (_l != null)
                {
                    _l.UnUpClick();
				}
			}

			
			public void Call() {
                if (_l != null)
                {
                    _l.OnUpClick();
				}
			}
		};

     class down_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public down_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnDownClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnDownClick();
             }
         }
     };

     class left_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public left_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnLeftClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnLeftClick();
             }
         }
     };
     class right_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public right_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnRightClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnRightClick();
             }
         }
     };

     class triangle_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public triangle_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnTriangleClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnTriangleClick();
             }
         }
     };

     class square_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public square_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnSquareClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnSquareClick();
             }
         }
     };

     class circle_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public circle_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnCircleClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnCircleClick();
             }
         }
     };

     class cancel_monitor : EmulatorButton.Monitor
     {

         private EmulatorListener _l;

         public cancel_monitor(EmulatorListener l)
         {
             this._l = l;
         }

         public void Free()
         {
             if (_l != null)
             {
                 _l.UnCancelClick();
             }
         }


         public void Call()
         {
             if (_l != null)
             {
                 _l.OnCancelClick();
             }
         }
     };

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

        public EmulatorButtons(EmulatorListener el, int w, int h, float scale)
        {
            this.emulatorListener = el;
            if (pack == null)
            {
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

            if (scale <= 0f)
            {
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
            }
            else
            {

                this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true,
                        (int)(60 * scale), (int)(60 * scale));
                this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true,
                        (int)(60 * scale), (int)(60 * scale));
                this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true,
                        (int)(60 * scale), (int)(60 * scale));
                this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true,
                        (int)(60 * scale), (int)(60 * scale));

                this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
                        (int)(68 * scale), (int)(68 * scale));
                this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true,
                        (int)(68 * scale), (int)(68 * scale));
                this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true,
                        (int)(68 * scale), (int)(68 * scale));
                this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true,
                        (int)(68 * scale), (int)(68 * scale));
            }

            this.up._monitor = new up_monitor(el);
            this.left._monitor = new left_monitor(el);
            this.right._monitor = new right_monitor(el);
            this.down._monitor = new down_monitor(el);
            this.triangle._monitor = new triangle_monitor(el);
            this.square._monitor = new square_monitor(el);
            this.circle._monitor = new circle_monitor(el);
            this.cancel._monitor = new cancel_monitor(el);
            if (dpad != null)
            {
                dpad.Dispose();
                dpad = null;
            }
            if (buttons != null)
            {
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
        
        private float offsetTouchX,offsetTouchY,offsetMoveX,offsetMoveY;

        public void OnEmulatorButtonEvent(Microsoft.Xna.Framework.Input.Touch.TouchLocation touch, float touchX, float touchY)
        {
            if (!visible)
            {
                return;
            }
            switch (touch.State)
            {
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Pressed:
                    offsetTouchX = touchX;
                    offsetTouchY = touchY;
                    Hit(touch.Id, touchX, touchY, false);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Released:
                    Unhit(touch.Id, touchX, touchY);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Moved:
                    offsetMoveX = touchX;
                    offsetMoveY = touchY;
                    if (MathUtils.Abs(offsetTouchX - offsetMoveX) > 5
                        || MathUtils.Abs(offsetTouchY - offsetMoveY) > 5)
                    {
                        Hit(touch.Id, touchX, touchY, true);
                    }
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Invalid:
                    Unhit(touch.Id, touchX, touchY);
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



        public void Hit(int id, float x, float y, bool flag)
        {

            if (!visible)
            {
                return;
            }

            up.Hit(id, x, y, flag);
            left.Hit(id, x, y, flag);
            right.Hit(id, x, y, flag);
            down.Hit(id, x, y, flag);

            triangle.Hit(id, x, y, flag);
            square.Hit(id, x, y, flag);
            circle.Hit(id, x, y, flag);
            cancel.Hit(id, x, y, flag);

        }

        public void Unhit(int id, float x, float y)
        {

            if (!visible)
            {
                return;
            }

            up.Unhit(id, x, y);
            left.Unhit(id, x, y);
            right.Unhit(id, x, y);
            down.Unhit(id, x, y);

            triangle.Unhit(id, x, y);
            square.Unhit(id, x, y);
            circle.Unhit(id, x, y);
            cancel.Unhit(id, x, y);
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
