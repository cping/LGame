using System;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core.Graphics.OpenGL;
using Loon.Utils;
using Microsoft.Xna.Framework.Graphics;
using Loon.Utils.Debug;

namespace Loon.Core.Graphics.Component
{
    public class Print : LRelease
    {

        private Microsoft.Xna.Framework.Vector2 pos = new Microsoft.Xna.Framework.Vector2();

        private int index, offset, font, tmp_font;

        private int fontSizeDouble;

        private char text;

        private char[] showMessages;

        private int iconWidth;

        private LColor fontColor = LColor.white;

        private int interceptMaxString;

        private int interceptCount;

        private int messageLength = 10;

        private string messages;

        private bool onComplete, newLine, visible;

        private System.Text.StringBuilder messageBuffer = new System.Text.StringBuilder();

        private int width, height, leftOffset, topOffset, next, messageCount;

        private float alpha;

        private int size, wait, tmp_left, left, fontSize, fontHeight;

        private Vector2f vector;

        private LTexture creeseIcon;

        private bool isEnglish, isLeft, isWait;

        private LFont strings;

        private SpriteBatch batch;

        public Print(Vector2f v, LFont f, int w, int h)
            : this("", f, v, w, h)
        {

        }

        public Print(string context, LFont f, Vector2f v, int w,
                int h)
        {
            this.SetMessage(context, f);
            this.vector = v;
            this.width = w;
            this.height = h;
            this.wait = 0;
            this.isWait = false;
        }

        public void SetMessage(string context, LFont f)
        {
            SetMessage(context, f, false);
        }

        public void SetMessage(string context, LFont f, bool isComplete)
        {
            if (batch == null)
            {
                batch = new SpriteBatch(GLEx.Device);
            }
            this.strings = f;
            this.wait = 0;
            this.visible = false;
            this.showMessages = new char[] { '\0' };
            this.interceptMaxString = 0;
            this.next = 0;
            this.messageCount = 0;
            this.interceptCount = 0;
            this.size = 0;
            this.tmp_left = 0;
            this.left = 0;
            this.fontSize = 0;
            this.fontHeight = 0;
            this.messages = context;
            this.next = context.Length;
            this.onComplete = false;
            this.newLine = false;
            this.messageCount = 0;
            this.messageBuffer.Remove(0, messageBuffer.Length - (0));
            if (isComplete)
            {
                this.Complete();
            }
            this.visible = true;
        }

        public string GetMessage()
        {
            return messages;
        }

        private LColor GetColor(char flagName)
        {
            if ('r' == flagName || 'R' == flagName)
            {
                return LColor.red;
            }
            if ('b' == flagName || 'B' == flagName)
            {
                return LColor.black;
            }
            if ('l' == flagName || 'L' == flagName)
            {
                return LColor.blue;
            }
            if ('g' == flagName || 'G' == flagName)
            {
                return LColor.green;
            }
            if ('o' == flagName || 'O' == flagName)
            {
                return LColor.orange;
            }
            if ('y' == flagName || 'Y' == flagName)
            {
                return LColor.yellow;
            }
            if ('m' == flagName || 'M' == flagName)
            {
                return LColor.magenta;
            }
            return null;
        }

        public void Draw(GLEx g)
        {
            Draw(g, LColor.white);
        }

        private void DrawMessage(GLEx gl, LColor old)
        {

            if (!visible)
            {
                return;
            }
            if (!running)
            {
                return;
            }
            if (batch == null)
            {
                return;
            }
            if (strings.GetSize() > 25)
            {

                lock (showMessages)
                {
                    this.size = showMessages.Length;

                    this.fontSize = (isEnglish) ? strings.GetSize() / 2 : gl.GetFont()
                            .GetSize();
                    this.fontHeight = strings.GetHeight();
                    this.tmp_left = (isLeft) ? 0 : (width - (fontSize * messageLength))
                            / 2 - (int)(fontSize * 1.5f);
                    this.left = tmp_left;
                    this.index = offset = font = tmp_font = 0;
                    this.fontSizeDouble = fontSize * 2;

                    batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, null, null, gl.View);

                    fontColor = old;

                    for (int i = 0; i < size; i++)
                    {
                        text = showMessages[i];
                        if (text == '\0')
                        {
                            continue;
                        }

                        if (interceptCount < interceptMaxString)
                        {
                            interceptCount++;
                            continue;
                        }
                        else
                        {
                            interceptMaxString = 0;
                            interceptCount = 0;
                        }
                        if (showMessages[i] == 'n'
                                && showMessages[(i > 0) ? i - 1 : 0] == '\\')
                        {
                            index = 0;
                            left = tmp_left;
                            offset++;
                            continue;
                        }
                        else if (text == '\n')
                        {
                            index = 0;
                            left = tmp_left;
                            offset++;
                            continue;
                        }
                        else if (text == '<')
                        {
                            LColor color = GetColor(showMessages[(i < size - 1) ? i + 1
                                    : i]);
                            if (color != null)
                            {
                                interceptMaxString = 1;
                                fontColor = color;
                            }
                            continue;
                        }
                        else if (showMessages[(i > 0) ? i - 1 : i] == '<'
                              && GetColor(text) != null)
                        {
                            continue;
                        }
                        else if (text == '/')
                        {
                            if (showMessages[(i < size - 1) ? i + 1 : i] == '>')
                            {
                                interceptMaxString = 1;
                                fontColor = old;
                            }
                            continue;
                        }
                        else if (index > messageLength)
                        {
                            index = 0;
                            left = tmp_left;
                            offset++;
                            newLine = false;
                        }
                        else if (text == '\\')
                        {
                            continue;
                        }
                        tmp_font = strings.CharWidth(text);
                        if (System.Char.IsLetter(text))
                        {
                            if (tmp_font < fontSize)
                            {
                                font = fontSize;
                            }
                            else
                            {
                                font = tmp_font;
                            }
                        }
                        else
                        {
                            font = fontSize;
                        }
                        left += font;
                        if (font <= 10 && StringUtils.IsSingle(text))
                        {
                            left += 12;
                        }
                        if (i != size - 1)
                        {
                            pos.X = vector.x + left + leftOffset;
                            pos.Y = (offset * fontHeight) + vector.y + fontSizeDouble
                                            + topOffset;
                            batch.DrawString(strings.Font, Convert.ToString(text), pos, fontColor.Color);
                        }
                        else if (!newLine)
                        {
                            pos.X = vector.x + left + leftOffset + iconWidth;
                            pos.Y = (offset * fontHeight) + vector.y + fontSize
                                    + topOffset + strings.GetAscent();
                            batch.Draw(creeseIcon.Texture, pos, Microsoft.Xna.Framework.Color.White);
                        }
                        index++;
                    }

                    batch.End();

                    if (messageCount == next)
                    {
                        onComplete = true;
                    }
                }
            }
            else
            {
                lock (showMessages)
                {
                    this.size = showMessages.Length;

                    this.fontSize = (isEnglish) ? ((int)(strings.GetSize() * 1.4f)) / 2 : (int)(gl.GetFont()
                            .GetSize() * 1.4f);
                    this.fontHeight = strings.GetHeight() + 10;
                    this.tmp_left = isLeft ? 0 : (int)(((width - (fontSize * messageLength))
                        / 2 + (int)(fontSize * 4)));
                    this.left = tmp_left;
                    this.index = offset = font = tmp_font = 0;
                    this.fontSizeDouble = (int)(fontSize * 2 * 1.2f);

                    batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend);

                    fontColor = old;

                    for (int i = 0; i < size; i++)
                    {
                        text = showMessages[i];

                        if (text == '\0')
                        {
                            continue;
                        }

                        if (interceptCount < interceptMaxString)
                        {
                            interceptCount++;
                            continue;
                        }
                        else
                        {
                            interceptMaxString = 0;
                            interceptCount = 0;
                        }
                        if (showMessages[i] == 'n'
                                && showMessages[(i > 0) ? i - 1 : 0] == '\\')
                        {
                            index = 0;
                            left = tmp_left;
                            offset++;
                            continue;
                        }
                        else if (text == '\n')
                        {
                            index = 0;
                            left = tmp_left;
                            offset++;
                            continue;
                        }
                        else if (text == '<')
                        {
                            LColor color = GetColor(showMessages[(i < size - 1) ? i + 1
                                    : i]);
                            if (color != null)
                            {
                                interceptMaxString = 1;
                                fontColor = color;
                            }
                            continue;
                        }
                        else if (showMessages[(i > 0) ? i - 1 : i] == '<'
                              && GetColor(text) != null)
                        {
                            continue;
                        }
                        else if (text == '/')
                        {
                            if (showMessages[(i < size - 1) ? i + 1 : i] == '>')
                            {
                                interceptMaxString = 1;
                                fontColor = old;
                            }
                            continue;
                        }
                        else if (index > messageLength)
                        {
                            index = 0;
                            left = tmp_left;
                            offset++;
                            newLine = false;
                        }
                        else if (text == '\\')
                        {
                            continue;
                        }
                        tmp_font = strings.CharWidth(text);
                        font = tmp_font;
                        if (System.Char.IsLetter(text))
                        {
                            if (tmp_font < fontSize)
                            {
                                font = fontSize;
                            }
                            else
                            {
                                font = tmp_font;
                            }
                        }
                        else
                        {
                            font = fontSize;
                        }
                        left += font;
                        if (font <= 10 && StringUtils.IsSingle(text))
                        {
                            left += 12;
                        }
                        if (i != size - 1)
                        {
                            pos.X = vector.x + left + leftOffset;
                            pos.Y = ((offset * fontHeight) + vector.y + fontSizeDouble
                                            + topOffset + strings.GetAscent() * 2.2f);
                            batch.DrawString(strings.Font, Convert.ToString(text), pos, fontColor.Color);
                        }
                        else if (!newLine)
                        {
                            pos.X = vector.x + left + leftOffset + iconWidth;
                            pos.Y = (offset * fontHeight) + vector.y + fontSize
                                    + topOffset + (strings.GetAscent() * 2.2f) + iconWidth;
                            batch.Draw(creeseIcon.Texture, pos, Microsoft.Xna.Framework.Color.White);
                        }
                        index++;
                    }

                    batch.End();

                    if (messageCount == next)
                    {
                        onComplete = true;
                    }
                }
            }

        }

        public void Draw(GLEx g, LColor old)
        {
            if (!visible)
            {
                return;
            }
            alpha = g.GetAlpha();
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(1.0f);
            }
            DrawMessage(g, old);
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(alpha);
            }
        }

        public void SetX(int x)
        {
            vector.SetX(x);
        }

        public void SetY(int y)
        {
            vector.SetY(y);
        }

        public int GetX()
        {
            return vector.X();
        }

        public int GetY()
        {
            return vector.Y();
        }

        private bool running;

        public void Complete()
        {
            lock (showMessages)
            {
                this.onComplete = true;
                this.messageCount = messages.Length;
                this.next = messageCount;
                this.showMessages = (messages + "_").ToCharArray();
                this.size = showMessages.Length;
            }
        }

        public bool IsComplete()
        {
            if (isWait)
            {
                if (onComplete)
                {
                    wait++;
                }
                return onComplete && wait > 100;
            }
            return onComplete;
        }

        public bool Next()
        {
            this.running = true;
            lock (messageBuffer)
            {
                if (!onComplete)
                {
                    if (messageCount == next)
                    {
                        onComplete = true;
                        return false;
                    }
                    if (messageBuffer.Length > 0)
                    {
                        messageBuffer.Remove(messageBuffer.Length - 1, messageBuffer.Length - (messageBuffer.Length - 1));
                    }
                    this.messageBuffer.Append(messages[messageCount]);
                    this.messageBuffer.Append("_");
                    this.showMessages = messageBuffer.ToString().ToCharArray();
                    this.size = showMessages.Length;
                    this.messageCount++;
                }
                else
                {
                    return false;
                }
                return true;
            }
        }

        public LTexture GetCreeseIcon()
        {
            return creeseIcon;
        }

        public void SetCreeseIcon(LTexture icon)
        {
            if (this.creeseIcon != null)
            {
                creeseIcon.Destroy();
                creeseIcon = null;
            }
            this.creeseIcon = icon;
            if (icon == null)
            {
                return;
            }
            this.iconWidth = icon.GetWidth()*2;
        }

        public int GetMessageLength()
        {
            return messageLength;
        }

        public void SetMessageLength(int m)
        {
            this.messageLength = m;
        }

        public int GetHeight()
        {
            return height;
        }

        public void SetHeight(int h)
        {
            this.height = h;
        }

        public int GetWidth()
        {
            return width;
        }

        public void SetWidth(int w)
        {
            this.width = w;
        }

        public int GetLeftOffset()
        {
            return leftOffset;
        }

        public void SetLeftOffset(int l)
        {
            this.leftOffset = l;
        }

        public int GetTopOffset()
        {
            return topOffset;
        }

        public void SetTopOffset(int t)
        {
            this.topOffset = t;
        }

        public bool IsEnglish()
        {
            return isEnglish;
        }

        public void SetEnglish(bool i)
        {
            this.isEnglish = i;
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool v)
        {
            this.visible = v;
        }

        public bool IsLeft()
        {
            return isLeft;
        }

        public void SetLeft(bool i)
        {
            this.isLeft = i;
        }

        public bool IsWait()
        {
            return isWait;
        }

        public void SetWait(bool i)
        {
            this.isWait = i;
        }

        public virtual void Dispose()
        {
            if (strings != null)
            {
                strings = null;
            }
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
        }
    }
}
