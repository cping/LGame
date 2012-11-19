namespace Loon.Core.Graphics.Component
{
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Microsoft.Xna.Framework;
    using Loon.Core.Timer;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Input;
using Microsoft.Xna.Framework.Graphics;

    public class LSelect : LContainer
    {
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

        private SpriteBatch batch;

        public LSelect(int x, int y, int width, int height):this((LTexture)null, x, y, width, height)
        {
            
        }

        public LSelect(string fileName): this(fileName, 0, 0)
        {
           
        }

        public LSelect(string fileName, int x, int y):this(LTextures.LoadTexture(fileName), x, y)
        {
            
        }

        public LSelect(LTexture formImage): this(formImage, 0, 0)
        {
           
        }

        public LSelect(LTexture formImage, int x, int y): this(formImage, x, y, formImage.GetWidth(), formImage.GetHeight())
        {
           
        }

        public LSelect(LTexture formImage, int x, int y, int width, int height):base(x, y, width, height)
        {
            if (formImage == null)
            {
                this.SetBackground(new LTexture(width, height, true));
                this.SetAlpha(0.3F);
            }
            else
            {
                this.SetBackground(formImage);
            }
            this.customRendering = true;
            this.selectFlag = 1;
            this.tmpOffset = -(width / 10);
            this.delay = new LTimer(150);
            this.autoAlpha = 0.25F;
            this.isAutoAlpha = true;
            this.SetCursor(XNAConfig.LoadTexture("creese.png"));
            this.SetElastic(true);
            this.SetLocked(true);
            this.SetLayer(100);
        }

        public void SetLeftOffset(int left_0)
        {
            this.left = left_0;
        }

        public void SetTopOffset(int top_0)
        {
            this.top = top_0;
        }

        public int GetLeftOffset()
        {
            return left;
        }

        public int GetTopOffset()
        {
            return top;
        }

        public int GetResultIndex()
        {
            return selectFlag - 1;
        }

        public void SetDelay(long timer)
        {
            delay.SetDelay(timer);
        }

        public long GetDelay()
        {
            return delay.GetDelay();
        }

        public string GetResult()
        {
            return result;
        }

        private static string[] GetListToStrings(IList<string> list)
        {
            if (list == null || list.Count == 0)
                return null;
            string[] rs = new string[list.Count];
            for (int i = 0; i < rs.Length; i++)
            {
                rs[i] = (string)list[i];
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
            if (batch == null)
            {
                batch = new SpriteBatch(GLEx.Device);
            }
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
            if (!visible)
            {
                return;
            }
            base.Update(elapsedTime);
            if (isAutoAlpha && buoyage != null)
            {
                if (delay.Action(elapsedTime))
                {
                    if (autoAlpha < 0.95F)
                    {
                        autoAlpha += 0.05F;
                    }
                    else
                    {
                        autoAlpha = 0.25F;
                    }
                }
            }
        }

        protected override void CreateCustomUI(GLEx g, int x, int y, int w, int h)
        {
            if (!visible)
            {
                return;
            }
            if (batch == null)
            {
                return;
            }
            sizeFont = messageFont.GetSize();
            if (sizeFont > 25)
            {
                sizeFont = messageFont.GetSize();
                doubleSizeFont = sizeFont * 2;
                if (doubleSizeFont == 0)
                {
                    doubleSizeFont = 20;
                }
                messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + left
                        + doubleSizeFont;

                batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);

                if (message != null)
                {
                    messageTop = y + doubleSizeFont + top - 10;
                    batch.DrawString(messageFont.Font, message, new Vector2(messageLeft, messageTop), fontColor.Color);
                }
                else
                {
                    messageTop = y + top;
                }
                nTop = messageTop;
                if (selects != null)
                {
                    nLeft = messageLeft - sizeFont / 4;

                    for (int i = 0; i < selects.Length; i++)
                    {
                        nTop += messageFont.GetHeight();
                        type = i + 1;
                        isSelect = (type == ((selectFlag > 0) ? selectFlag : 1));
                        if ((buoyage != null) && isSelect)
                        {
                            g.SetAlpha(autoAlpha);
                            g.DrawTexture(buoyage, nLeft,
                                    nTop - (int)(buoyage.GetHeight() / 1.5f));
                            g.SetAlpha(1.0F);
                        }
                        batch.DrawString(messageFont.Font, selects[i], new Vector2(messageLeft, nTop), fontColor.Color);
                        if ((cursor != null) && isSelect)
                        {
                            batch.Draw(cursor.Texture, new Vector2(nLeft, nTop - cursor.GetHeight() / 4), Color.White);
                        }
                    }
                    batch.End();
                }
            }
            else
            {
                doubleSizeFont = (int)(sizeFont * 1.4f * 2);
                if (doubleSizeFont == 0)
                {
                    doubleSizeFont = 20;
                }
                messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + left
                        + doubleSizeFont;
                batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend);

                if (message != null)
                {
                    messageTop = y + doubleSizeFont + top + (int)(messageFont.GetAscent() * 2) + 10;
                    batch.DrawString(messageFont.Font, message, new Vector2(messageLeft, messageTop), fontColor.Color);
                }
                else
                {
                    messageTop = y + top + (int)(messageFont.GetAscent() * 2) + 10;
                }
                nTop = messageTop;
                if (selects != null)
                {
                    nLeft = messageLeft - sizeFont / 4;

                    for (int i = 0; i < selects.Length; i++)
                    {
                        nTop += (int)((messageFont.GetHeight() + messageFont.GetAscent()) * 1.2f);
                        type = i + 1;
                        isSelect = (type == ((selectFlag > 0) ? selectFlag : 1));
                        if ((buoyage != null) && isSelect)
                        {
                            g.SetAlpha(autoAlpha);
                            g.DrawTexture(buoyage, nLeft,
                                    nTop - (int)(buoyage.GetHeight() / 1.5f));
                            g.SetAlpha(1.0F);
                        }
                        batch.DrawString(messageFont.Font, selects[i], new Vector2(messageLeft, nTop), fontColor.Color);
                        if ((cursor != null) && isSelect)
                        {
                            batch.Draw(cursor.Texture, new Vector2(nLeft, (nTop - cursor.GetHeight() / 4)), Color.White);
                        }
                    }
                    batch.End();
                }
            }
        }

        private bool onClick;

        /// <summary>
        /// 处理点击事件（请重载实现）
        /// </summary>
        ///
        public virtual void DoClick()
        {
            if (Click != null)
            {
                Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
                Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        public bool IsClick()
        {
            return onClick;
        }

        protected internal override void ProcessTouchClicked()
        {
            if (!input.IsMoving())
            {
                if ((selects != null) && (selectFlag > 0))
                {
                    this.result = selects[selectFlag - 1];
                }
                this.DoClick();
                this.onClick = true;
            }
            else
            {
                this.onClick = false;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        protected internal override void ProcessTouchMoved()
        {
            if (messageFont.GetSize() < 25)
            {
                if (selects != null)
                {
                    float touchY = input.GetTouchY();
                    selectFlag = selectSize
                - (((nTop - ((int)(messageFont.GetHeight() + messageFont.GetAscent()) + 30)) - ((touchY == 0) ? 1 : (int)touchY)) / doubleSizeFont);
                    if (selectFlag < 1)
                    {
                        selectFlag = 0;
                    }
                    if (selectFlag > selectSize)
                    {
                        selectFlag = selectSize;
                    }
                }
            }
            else
            {
                if (selects != null)
                {
                    int touchY = input.GetTouchY();
                    selectFlag = selectSize
                            - (((nTop + 30) - ((touchY == 0) ? 1 : touchY)) / doubleSizeFont);
                    if (selectFlag < 1)
                    {
                        selectFlag = 0;
                    }
                    if (selectFlag > selectSize)
                    {
                        selectFlag = selectSize;
                    }
                }
            }
        }

        protected internal override void ProcessKeyPressed()
        {
            if (this.IsSelected() && this.input.GetKeyPressed() == Key.ENTER)
            {
                this.DoClick();
            }
        }

        protected internal override void ProcessTouchDragged()
        {
            ProcessTouchMoved();
            if (!locked)
            {
                if (GetContainer() != null)
                {
                    GetContainer().SendToFront(this);
                }
                this.Move(this.input.GetTouchDX(), this.input.GetTouchDY());
            }
        }

        public LColor GetFontColor()
        {
            return fontColor;
        }

        public void SetFontColor(LColor c)
        {
            this.fontColor = c;
        }

        public LFont GetMessageFont()
        {
            return messageFont;
        }

        public void SetMessageFont(LFont f)
        {
            this.messageFont = f;
        }

        public bool IsLocked()
        {
            return locked;
        }

        public void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        protected internal override void ValidateSize()
        {
            base.ValidateSize();
        }

        public LTexture GetCursor()
        {
            return cursor;
        }

        public void SetNotCursor()
        {
            this.cursor = null;
        }

        public void SetCursor(LTexture c)
        {
            this.cursor = c;
        }

        public void SetCursor(string fileName)
        {
            SetCursor(new LTexture(fileName));
        }

        public LTexture GetBuoyage()
        {
            return buoyage;
        }

        public void SetNotBuoyage()
        {
            this.cursor = null;
        }

        public void SetBuoyage(LTexture b)
        {
            this.buoyage = b;
        }

        public void SetBuoyage(string fileName)
        {
            SetBuoyage(new LTexture(fileName));
        }

        public bool IsFlashBuoyage()
        {
            return isAutoAlpha;
        }

        public void SetFlashBuoyage(bool flashBuoyage)
        {
            this.isAutoAlpha = flashBuoyage;
        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {

        }

        public override string GetUIName()
        {
            return "Select";
        }

        public override void Dispose()
        {
            base.Dispose();
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
            if (buoyage != null)
            {
                buoyage.Destroy();
                buoyage = null;
            }
        }

    }
}
