using Loon.Core.Timer;
using Loon.Core.Graphics.Opengl;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Loon.Core.Input;
namespace Loon.Core.Graphics.Component {
	
	public class LSelect : LContainer {
	
		private LFont messageFont = LFont.GetDefaultFont();
	
		private LColor fontColor = LColor.white;
	
		private int left, top, type, nTop;
	
		private int sizeFont, doubleSizeFont, tmpOffset, messageLeft, nLeft,
				messageTop, selectSize, selectFlag;
	
		private float autoAlpha;
	
		private LTimer delay;
	
		private string[] selects;
	
		private string message, result;
	
		private LTexture cursor, buoyage;
	
		private bool isAutoAlpha, isSelect;
	
		public LSelect(int x, int y, int width, int height):this((LTexture) null, x, y, width, height) {
			
		}
	
		public LSelect(string fileName):this(fileName, 0, 0) {
			
		}
	
		public LSelect(string fileName, int x, int y):this(LTextures.LoadTexture(fileName), x, y) {
			
		}
	
		public LSelect(LTexture formImage):this(formImage, 0, 0) {
			
		}
	
		public LSelect(LTexture formImage, int x, int y):this(formImage, x, y, formImage.GetWidth(), formImage.GetHeight()) {
			
		}
	
		public LSelect(LTexture formImage, int x, int y, int width, int height):base(x, y, width, height) {
			if (formImage == null) {
				this.SetBackground(new LTexture(width, height, true, Loon.Core.Graphics.Opengl.LTexture.Format.SPEED));
				this.SetAlpha(0.3F);
			} else {
				this.SetBackground(formImage);
			}
			this.customRendering = true;
			this.selectFlag = 1;
			this.tmpOffset = -(width / 10);
			this.delay = new LTimer(150);
			this.autoAlpha = 0.25F;
			this.isAutoAlpha = true;
            this.SetCursor(XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "creese.png"));
			this.SetElastic(true);
			this.SetLocked(true);
			this.SetLayer(100);
		}
	
		public void SetLeftOffset(int left) {
			this.left = left;
		}
	
		public void SetTopOffset(int top) {
			this.top = top;
		}
	
		public int GetLeftOffset() {
			return left;
		}
	
		public int GetTopOffset() {
			return top;
		}
	
		public int GetResultIndex() {
			return selectFlag - 1;
		}
	
		public void SetDelay(long timer) {
			delay.SetDelay(timer);
		}
	
		public long GetDelay() {
			return delay.GetDelay();
		}
	
		public string GetResult() {
			return result;
		}

        private static string[] GetListToStrings(IList<string> list)
        {
            if (list == null || list.Count == 0)
                return null;
            string[] rs = new string[list.Count];
            for (int i = 0; i < rs.Length; i++)
            {
                rs[i] = list[i];
            }
            return rs;
        }


        public void SetMessage(string mes, IList<string> list)
        {
            SetMessage(mes, GetListToStrings(list));
        }

        public void SetMessage(string[] sel)
        {
            SetMessage(null, sel);
        }

        public void SetMessage(string mes, string[] sel)
        {
            this.message = mes;
            this.selects = sel;
            this.selectSize = sel.Length;
            if (doubleSizeFont == 0)
            {
                doubleSizeFont = 20;
            }
        }

        public override void Update(long elapsedTime)
        {
			if (!visible) {
				return;
			}
			base.Update(elapsedTime);
			if (isAutoAlpha && buoyage != null) {
				if (delay.Action(elapsedTime)) {
					if (autoAlpha < 0.95F) {
						autoAlpha += 0.05F;
					} else {
						autoAlpha = 0.25F;
					}
				}
			}
		}

        protected internal override void CreateCustomUI(GLEx g, int x, int y, int w, int h)
        {
            if (!visible)
            {
                return;
            }
            LColor oldColor = g.GetColor();
            LFont oldFont = g.GetFont();
            g.SetColor(fontColor);
            g.SetFont(messageFont);
            sizeFont = messageFont.GetSize();
            doubleSizeFont = sizeFont * 2;
            if (doubleSizeFont == 0)
            {
                doubleSizeFont = 20;
            }
            messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + left
                    + doubleSizeFont;
            // g.setAntiAlias(true);
            if (message != null)
            {
                messageTop = y + doubleSizeFont + top + 2;
                g.DrawString(message, messageLeft, messageTop);
            }
            else
            {
                messageTop = y + top + 2;
            }

            nTop = messageTop;
            if (selects != null)
            {
                nLeft = messageLeft - sizeFont / 4;
                for (int i = 0; i < selects.Length; i++)
                {
                    nTop += 30;
                    type = i + 1;
                    isSelect = (type == ((selectFlag > 0) ? selectFlag : 1));
                    if ((buoyage != null) && isSelect)
                    {
                        g.SetAlpha(autoAlpha);
                        g.DrawTexture(buoyage, nLeft,
                                nTop - (int)(buoyage.GetHeight() / 1.5f));
                        g.SetAlpha(1.0F);
                    }
                    g.DrawString(selects[i], messageLeft, nTop);
                    if ((cursor != null) && isSelect)
                    {
                        g.DrawTexture(cursor, nLeft, nTop - cursor.GetHeight() / 2, LColor.white);
                    }

                }
            }
            // g.setAntiAlias(false);
            g.SetColor(oldColor);
            g.SetFont(oldFont);

        }
	
		private bool onClick;
	
		public void DoClick() {
			if (Click != null) {
                Click.DoClick(this);
			}
		}
	
		public bool IsClick() {
			return onClick;
		}

        protected internal override void ProcessTouchClicked()
        {
			if (!input.IsMoving()) {
				if ((selects != null) && (selectFlag > 0)) {
					this.result = selects[selectFlag - 1];
				}
				this.DoClick();
				this.onClick = true;
			} else {
				this.onClick = false;
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        protected internal override void ProcessTouchMoved()
        {
			if (selects != null) {
				int touchY = input.GetTouchY();
				selectFlag = selectSize
						- (((nTop + 30) - ((touchY == 0) ? 1 : touchY)) / doubleSizeFont);
				if (selectFlag < 1) {
					selectFlag = 0;
				}
				if (selectFlag > selectSize) {
					selectFlag = selectSize;
				}
			}
	
		}
	
		protected internal override void ProcessKeyPressed() {
			if (this.IsSelected() && this.input.GetKeyPressed() == Key.ENTER) {
				this.DoClick();
			}
		}

        protected internal override void ProcessTouchDragged()
        {
			ProcessTouchMoved();
			if (!locked) {
				if (GetContainer() != null) {
					GetContainer().SendToFront(this);
				}
				this.Move(this.input.GetTouchDX(), this.input.GetTouchDY());
			}
		}
	
		public LColor GetFontColor() {
			return fontColor;
		}
	
		public void SetFontColor(LColor fontColor) {
			this.fontColor = fontColor;
		}
	
		public LFont GetMessageFont() {
			return messageFont;
		}
	
		public void SetMessageFont(LFont messageFont) {
			this.messageFont = messageFont;
		}
	
		public bool IsLocked() {
			return locked;
		}
	
		public void SetLocked(bool locked) {
			this.locked = locked;
		}
	
		public LTexture GetCursor() {
			return cursor;
		}
	
		public void SetNotCursor() {
			this.cursor = null;
		}
	
		public void SetCursor(LTexture cursor) {
			this.cursor = cursor;
		}
	
		public void SetCursor(string fileName) {
			SetCursor(new LTexture(fileName));
		}
	
		public LTexture GetBuoyage() {
			return buoyage;
		}
	
		public void SetNotBuoyage() {
			this.cursor = null;
		}
	
		public void SetBuoyage(LTexture buoyage) {
			this.buoyage = buoyage;
		}
	
		public void SetBuoyage(string fileName) {
			SetBuoyage(new LTexture(fileName));
		}
	
		public bool IsFlashBuoyage() {
			return isAutoAlpha;
		}
	
		public void SetFlashBuoyage(bool flashBuoyage) {
			this.isAutoAlpha = flashBuoyage;
		}

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
				LTexture[] buttonImage) {
	
		}
	
		public override string GetUIName() {
			return "Select";
		}
	
	}
}
