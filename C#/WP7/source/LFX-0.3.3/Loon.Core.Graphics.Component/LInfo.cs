using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using Loon.Core.Graphics.OpenGL;
using Loon.Core.Timer;
using Loon.Utils;
using Loon.Core.Input;

namespace Loon.Core.Graphics.Component
{
    public class LInfo : LContainer
    {

        private SpriteBatch batch;

        private float offsetX = 10, offsetY = 10;

        private static LTexture page; // 分页标记用图

        private static LTexture line; // 行末标记用图

        class FlagImage
        {

            internal bool visible = true; // 是否显示标记

            internal bool flag;

            internal LTimer timer = new LTimer(10);

            internal LTexture texture;// 背景图片

            internal float startTime = 0; // 开始时间

            internal float alpha; // 透明度

            internal int x = 0; // 显示的x坐标

            internal int y = 0; // 显示的y坐标

            internal int width = 24; // 宽

            internal int height = 24; // 高

            private LInfo info;

            public FlagImage(LInfo i)
            {
                this.info = i;
                // 读取基础标记
                if (page == null || (page != null && page.isClose))
                {
                    if (page != null)
                    {
                        page.Destroy();
                        page = null;
                    }
                    page = XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "page.png");
                }
                if (line == null || (line != null && line.isClose))
                {
                    if (line != null)
                    {
                        line.Destroy();
                        line = null;
                    }
                    line = XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "line.png");
                }
                texture = page;
            }

            // 绘制
            public void Draw(GLEx g)
            {
                if (!visible)
                {
                    return;
                }
                alpha = startTime / 255f;
                g.SetAlpha(alpha);
                g.DrawTexture(texture, info.offsetX + x, info.offsetY + y , width, height);
                g.SetAlpha(1f);
            }

            public void Update(long elapsedTime)
            {
                if (timer.Action(elapsedTime))
                {
                    if (flag)
                    {
                        startTime -= 2;
                        if (startTime <= 50)
                        {
                            flag = false;
                        }
                    }
                    else
                    {
                        startTime += 2;
                        if (startTime >= 250)
                        {
                            flag = true;
                        }
                    }
                }
            }

            public void SetType(int type)
            {
                if (type == 0)
                {
                    texture = line;
                }
                else
                {
                    texture = page;
                }
            }

            public void SetAlpha(int a)
            {
                this.alpha = a;
            }

            public void SetPos(int x, int y)
            {
                this.x = x;
                this.y = y;
            }

            public void SetX(int x)
            {
                SetPos(x, this.y);
            }

            public void SetY(int y)
            {
                SetPos(this.x, y);
            }

            public void SetSize(int width, int height)
            {
                this.width = width;
                this.height = height;
            }

            public void SetVisible(bool visible)
            {
                this.visible = visible;
            }

        }

        public LInfo(int width, int height)
            : this(0, 0, width, height)
        {

        }

        public static int messageCountMax = 1000; // 允许显示的最大文字数量

        private LFont deffont = LFont.GetDefaultFont();

        private LColor fontColor = LColor.white;

        private bool isHch = false;

        private int indentPoint = 0; // 此变量用以记录换行点位置

        private float fontSize; // 默认的字体大小

        private int linespacing = 15; // 默认的行间距

        private int linesize = 15; // 默认的列间距

        private int pitch = 0; // 默认的文字间距离

        private bool autoreturn = true; // 是否开启自动换行

        private int margin_left = 10; // 文字显示时距离左侧位置修正 10

        private int margin_right = 10; // 文字显示时距离右侧位置修正 10

        private int margin_top = 10; // 文字显示时距离上方位置修正 10

        private int margin_bottom = 10; // 文字显示时距离下方位置修正 10

        private string message = ""; // 完整的文字信息

        private string tmpMessage = null;

        private int message_char_count; // 转化为字符串的信息数量

        private char align = 'l'; // 文字对齐方式（l为左对齐）

        private int[] message_x = null; // 文字显示位置的x坐标集合

        private int[] message_y = null; // 文字显示位置的y坐标集合

        // 已经出现过的字符坐标集合(每打印过一个字符，就在这里多保存一个)
        private List<LocatePoint> locatePoint = new List<LocatePoint>();

        private FlagImage flag = null; // 文字显示进度标记

        class LocatePoint
        {

            internal int point;

            internal Int32 x = 0;

            internal Int32 y = 0;
        }

        public LInfo(int x, int y, int width, int height)
            : this((LTexture)null, x, y, width, height)
        {

        }

        public LInfo(string fileName, int x, int y)
            : this(LTextures.LoadTexture(fileName), x, y)
        {

        }

        public LInfo(LTexture formImage, int x, int y)
            : this(formImage, x, y, formImage.GetWidth(), formImage.GetHeight())
        {

        }

        public LInfo(LTexture formImage, int x, int y, int width, int height)
            : base(x, y, width, height)
        {
            fontSize = deffont.GetSize();
            if (formImage == null)
            {
                this.SetBackground(new LTexture(width, height, true));
                this.SetAlpha(0.3F);
            }
            else
            {
                this.SetBackground(formImage);
                if (width == -1)
                {
                    width = formImage.GetWidth();
                }
                if (height == -1)
                {
                    height = formImage.GetHeight();
                }
            }
            this.message_char_count = 0;
            this.message_x = new int[messageCountMax];
            this.message_y = new int[messageCountMax];
            this.locatePoint = new List<LocatePoint>();
            this.flag = new FlagImage(this);
            this.customRendering = true;
            this.SetElastic(true);
            this.SetLayer(100);
        }

        public void Locate(Int32 x, Int32 y)
        {
            LocatePoint l = new LocatePoint();
            l.point = message_char_count;
            l.x = x;
            l.y = y;
            locatePoint.Add(l);
        }

        public void FlagOn(int type)
        {
            flag.SetVisible(true);
            flag.SetType(type);
        }

        public void FlagOff()
        {
            flag.SetVisible(false);
        }

        public void SetIndent()
        {
            indentPoint = message_char_count;
        }

        public void EndIndent()
        {
            indentPoint = -1;
        }

        public void PutMessage(string text)
        {
            if (text == null)
            {
                return;
            }
            this.tmpMessage = message;
            this.message += text;
            this.message_char_count +=text.Replace("\n", "").Length;
            this.ResetMessagePos();
        }

        public void ResetMessagePos()
        {
            int x = 0, y = 0;
            int len = message.Length;
            float mesWidth;
            int rightLimit = (int)(this.X() + GetWidth() - margin_right - fontSize);
            string[] line;
            char[] ch = new char[1];
            int count = 0;
            int baseX = 0, baseY = 0;
            LocatePoint lp;
            int locateCount = locatePoint.Count;
            int widthBuff;
            baseY = (int)(this.Y() + margin_top + fontSize);
            baseX = (int)(this.X() + margin_left);
            switch (align)
            {
                case 'c': // 居中
                    line = StringUtils.Split(message, "\n");
                    y = baseY;
                    for (int j = 0; j < line.Length; j++)
                    {
                        len = line[j].Length;
                        mesWidth = (fontSize + pitch) * (len);
                        widthBuff = (int)((GetWidth() - margin_left - margin_right) / 2 - mesWidth / 2);
                        x = baseX + widthBuff;
                        for (int i = 0; i < len; i++)
                        {
                            ch[0] = line[j][i];
                            for (int k = 0; k < locateCount; k++)
                            {
                                lp = locatePoint[k];
                                if (count == lp.point)
                                {
                                    if (lp.x != 0)
                                        x = baseX + lp.x + widthBuff;
                                    if (lp.y != 0)
                                        y = baseY + lp.y;
                                }
                            }
                            message_x[count] = x;
                            message_y[count] = y;
                            count++;
                            x += (int)(fontSize + pitch);
                        }
                        if (isHch)
                        {
                            flag.SetPos((int)(y - flag.height), x);
                        }
                        else
                        {
                            flag.SetPos(x, (int)(y - flag.height));
                        }
                        y += linesize + linespacing;
                    }
                    break;

                case 'r': // 右对齐
                    line = StringUtils.Split(message, "\n");
                    y = baseY;
                    for (int j = 0; j < line.Length; j++)
                    {
                        len = line[j].Length;
                        mesWidth = (fontSize + pitch) * len;
                        widthBuff = (int)((GetWidth() - margin_left - margin_right) - mesWidth);
                        x = baseX + widthBuff;
                        for (int i = 0; i < len; i++)
                        {
                            ch[0] = line[j][i];
                            for (int k = 0; k < locateCount; k++)
                            {
                                lp = locatePoint[k];
                                if (count == lp.point)
                                {
                                    if (lp.x != 0)
                                        x = baseX + lp.x + widthBuff;
                                    if (lp.y != 0)
                                        y = baseY + lp.y;
                                }
                            }
                            message_x[count] = x;
                            message_y[count] = y;
                            count++;
                            x += (int)(fontSize + pitch);
                        }
                        if (isHch)
                        {
                            flag.SetPos((int)(y - flag.height), x);
                        }
                        else
                        {
                            flag.SetPos(x, (int)(y - flag.height));
                        }
                        y += linesize + linespacing;
                    }
                    break;
                default: // 左对齐
                    line = StringUtils.Split(message, "\n");
                    y = baseY;
                    for (int j = 0; j < line.Length; j++)
                    {
                        len = line[j].Length;
                        x = baseX;
                        for (int i = 0; i < len; i++)
                        {
                            ch[0] = line[j][i];
                            if (autoreturn && x >= rightLimit)
                            {
                                y += linesize + linespacing;
                                x = baseX;
                            }
                            for (int k = 0; k < locateCount; k++)
                            {
                                lp = locatePoint[k];
                                if (count == lp.point)
                                {
                                    if (lp.x != 0)
                                        x = baseX + lp.x;
                                    if (lp.y != 0)
                                        y = baseY + lp.y;
                                }
                            }
                            message_x[count] = x;
                            message_y[count] = y;
                            if (count == indentPoint)
                            {
                                baseX = x;
                            }
                            count++;
                            x += (int)(fontSize + pitch);
                        }
                        y += linesize + linespacing;
                    }
                    if (autoreturn && x >= rightLimit)
                    {
                        y += linesize + linespacing;
                        x = baseX;
                    }
                    y -= linesize + linespacing;
                    if (isHch)
                    {
                        flag.SetPos((int)(y - flag.height), x);
                    }
                    else
                    {
                        flag.SetPos(x, (int)(y - flag.height));
                    }
                    break;
            }
            if (batch == null)
            {
                batch = new SpriteBatch(GLEx.Device);
            }
        }

        public string GetMessage()
        {
            return message;
        }

        public void SetFlagType(int type)
        {
            flag.SetType(type);
        }

        public void SetAlign(char align)
        {
            this.align = align;
        }

        public void SetMargin(int margin)
        {
            SetMargin(margin, margin, margin, margin);
        }

        public void SetMargin(int top, int right, int bottom, int left)
        {
            margin_top = top;
            margin_right = right;
            margin_bottom = bottom;
            margin_left = left;
        }

        public void SetMarginTop(int top)
        {
            margin_top = top;
        }

        public void SetMarginRight(int right)
        {
            margin_right = right;
        }

        public void SetMarginBottom(int bottom)
        {
            margin_bottom = bottom;
        }

        public void SetMarginLeft(int left)
        {
            margin_left = left;
        }

        public int GetMarginBottom()
        {
            return margin_bottom;
        }

        public virtual void DoClick()
        {
            if (Click != null)
            {
                Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
                Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        protected internal override void ProcessTouchClicked()
        {
            this.DoClick();
        }

        protected internal override void ProcessKeyPressed()
        {
            if (this.IsSelected() && this.input.GetKeyPressed() == Key.ENTER)
            {
                this.DoClick();
            }
        }

        public override void Update(long elapsedTime)
        {
            if (!visible)
            {
                return;
            }
            base.Update(elapsedTime);
            flag.Update(elapsedTime);
        }

        private void DrawMessage(GLEx g)
        {
            if (batch == null)
            {
                return;
            }
            lock (batch)
            {
                char[] chars = message.ToCharArray();
                int len = message.Length;
                int i, j = 0;
                char ch;

                batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);

                if (isHch)
                {
                    for (i = 0; i < len; i++)
                    {
                        ch = chars[i];
                        if (ch != '\n')
                        {
                            batch.DrawString(deffont.Font, "" + ch, new Vector2(offsetY + message_y[j], offsetX
                                    + message_x[j]), fontColor.Color);
                            j++;
                        }
                    }
                }
                else
                {
                    for (i = 0; i < len; i++)
                    {
                        ch = chars[i];
                        if (ch != '\n')
                        {
                            batch.DrawString(deffont.Font, "" + ch, new Vector2(offsetX + message_x[j], offsetY
                            + message_y[j]), fontColor.Color);
                            j++;
                        }
                    }
                }
                batch.End();
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        protected override void CreateCustomUI(GLEx g, int x, int y, int w,
                int h)
        {
            if (!visible)
            {
                return;
            }
            if (message.Length != 0)
            {
                DrawMessage(g);
            }
            flag.Draw(g);
        }

        protected internal override void ProcessTouchDragged()
        {
            if (!locked)
            {
                if (GetContainer() != null)
                {
                    GetContainer().SendToFront(this);
                }
                this.Move(this.input.GetTouchDX(), this.input.GetTouchDY());
            }
        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {

        }

        public LFont GetFont()
        {
            return deffont;
        }

        public void SetFont(LFont deffont)
        {
            if (deffont == null)
            {
                return;
            }
            this.deffont = deffont;
            this.fontSize = deffont.GetSize();
        }

        public LColor GetFontColor()
        {
            return fontColor;
        }

        public void SetFontColor(LColor fontColor)
        {
            this.fontColor = fontColor;
        }

        public void SetPitch(int pitch)
        {
            this.pitch = pitch;
        }

        public void SetLineSpacing(int lineSpacing)
        {
            this.linespacing = lineSpacing;
        }

        public void SetLineSize(int linesize)
        {
            this.linesize = linesize;
        }

        public void SetAutoReturn(bool autoreturn)
        {
            this.autoreturn = autoreturn;
        }

        public float GetOffsetX()
        {
            return offsetX;
        }

        public void SetOffsetX(float offsetX)
        {
            this.offsetX = offsetX;
        }

        public float GetOffsetY()
        {
            return offsetY;
        }

        public void SetOffsetY(float offsetY)
        {
            this.offsetY = offsetY;
        }

        public bool IsHch()
        {
            return isHch;
        }

        public void SetHch(bool isHch)
        {
            this.isHch = isHch;
        }

        public bool IsLocked()
        {
            return locked;
        }

        public void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        public override void Dispose()
        {
            base.Dispose();
            if (page != null)
            {
                page.Destroy();
                page = null;
            }
            if (line != null)
            {
                line.Destroy();
                line = null;
            }
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
        }

        public override string GetUIName()
        {
            return "Info";
        }

    }
}
