using System.Text;
using Loon.Core.Geom;
using Loon.Core.Graphics.Opengl;
using Loon.Utils;

namespace Loon.Core.Graphics.Component {
	
	public class Print : LRelease {
	
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
	
		private StringBuilder messageBuffer;
	
		private int width, height, leftOffset, topOffset, next, messageCount;
	
		private float alpha;
	
		private int size, wait, tmp_left, left, fontSize, fontHeight;
	
		private Vector2f vector;
	
		private LTexture creeseIcon;
	
		private LSTRFont strings;
	
		private bool isEnglish, isLeft, isWait;
	
		private float iconX, iconY;
	
		private int lazyHashCade = 1;
	
		public Print(Vector2f vector, LFont font, int width, int height):this("", font, vector, width, height) {
			
		}
	
		public Print(string context, LFont font, Vector2f vector, int width,
				int height) {
                    this.messageBuffer = new StringBuilder(messageLength);
			this.SetMessage(context, font);
			this.vector = vector;
			this.width = width;
			this.height = height;
			this.wait = 0;
			this.isWait = false;
		}
	
		public void SetMessage(string context, LFont font) {
			SetMessage(context, font, false);
		}
	
		public void SetMessage(string context, LFont font, bool isComplete) {
			if (strings != null) {
				strings.Dispose();
			}
			this.strings = new LSTRFont(font, context);
			this.lazyHashCade = 1;
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
			this.messageBuffer.Clear();
			if (isComplete) {
				this.Complete();
			}
			this.visible = true;
		}
	
		public string GetMessage() {
			return messages;
		}
	
		private LColor GetColor(char flagName) {
			if ('r' == flagName || 'R' == flagName) {
				return LColor.red;
			}
			if ('b' == flagName || 'B' == flagName) {
				return LColor.black;
			}
			if ('l' == flagName || 'L' == flagName) {
				return LColor.blue;
			}
			if ('g' == flagName || 'G' == flagName) {
				return LColor.green;
			}
			if ('o' == flagName || 'O' == flagName) {
				return LColor.orange;
			}
			if ('y' == flagName || 'Y' == flagName) {
				return LColor.yellow;
			}
			if ('m' == flagName || 'M' == flagName) {
				return LColor.magenta;
			}
			return null;
		}
	
		public void Draw(GLEx g) {
			Draw(g, LColor.white);
		}

        private void DrawMessage(GLEx gl, LColor old)
        {
            if (!visible)
            {
                return;
            }
            if (strings == null)
            {
                return;
            }
            lock (showMessages)
            {

                this.size = showMessages.Length;
                this.fontSize = (isEnglish) ? strings.GetSize() / 2 : gl.GetFont()
                        .GetSize();
                this.fontHeight = strings.GetHeight();
                this.tmp_left = isLeft ? 0 : (width - (fontSize * messageLength))
                    / 2 - (int)(fontSize * 1.5);
                this.left = tmp_left;
                this.index = offset = font = tmp_font = 0;
                this.fontSizeDouble = fontSize * 2;

                int hashCode = 1;
                hashCode = LSystem.Unite(hashCode, size);
                hashCode = LSystem.Unite(hashCode, left);
                hashCode = LSystem.Unite(hashCode, fontSize);
                hashCode = LSystem.Unite(hashCode, fontHeight);

                if (strings == null)
                {
                    return;
                }

                if (hashCode == lazyHashCade)
                {
                    strings.PostCharCache();
                    if (iconX != 0 && iconY != 0)
                    {
                        gl.DrawTexture(creeseIcon, iconX, iconY);
                    }
                    return;
                }

                strings.StartChar();
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
                   
                    if (i != size - 1)
                    {
                        strings.AddChar(text, vector.x + left + leftOffset,
                                (offset * fontHeight) + vector.y + fontSizeDouble
                                        + topOffset - font - 2, fontColor);
                    }
                    else if (!newLine && !onComplete)
                    {
                        iconX = vector.x + left + leftOffset + iconWidth ;
                        iconY = (offset * fontHeight) + vector.y + fontSize
                            + topOffset + strings.GetAscent();
                        if (iconX != 0 && iconY != 0)
                        {
                            gl.DrawTexture(creeseIcon, iconX, iconY);
                        }
                    }
                    index++;
                }

                strings.StopChar();
                strings.SaveCharCache();

                lazyHashCade = hashCode;

                if (messageCount == next)
                {
                    onComplete = true;
                }
            }
        }
	
		public void Draw(GLEx g, LColor old) {
			if (!visible) {
				return;
			}
			alpha = g.GetAlpha();
			if (alpha > 0 && alpha < 1) {
				g.SetAlpha(1.0f);
			}
			DrawMessage(g, old);
			if (alpha > 0 && alpha < 1) {
				g.SetAlpha(alpha);
			}
		}
	
		public void SetX(int x) {
			vector.SetX(x);
		}
	
		public void SetY(int y) {
			vector.SetY(y);
		}
	
		public int GetX() {
			return vector.X();
		}
	
		public int GetY() {
			return vector.Y();
		}
	
		public void Complete() {
			 lock (showMessages) {
						this.onComplete = true;
						this.messageCount = messages.Length;
						this.next = messageCount;
						this.showMessages = (messages + "_").ToCharArray();
						this.size = showMessages.Length;
					}
		}
	
		public bool IsComplete() {
			if (isWait) {
				if (onComplete) {
					wait++;
				}
				return onComplete && wait > 100;
			}
			return onComplete;
		}

        public bool Next()
        {
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
	
		public LTexture GetCreeseIcon() {
			return creeseIcon;
		}
	
		public void SetCreeseIcon(LTexture icon) {
			if (this.creeseIcon != null) {
				creeseIcon.Destroy();
				creeseIcon = null;
			}
			this.creeseIcon = icon;
			if (icon == null) {
				return;
			}
			this.iconWidth = icon.GetWidth();
		}
	
		public int GetMessageLength() {
			return messageLength;
		}
	
		public void SetMessageLength(int messageLength) {
			this.messageLength = (messageLength - 4);
		}
	
		public int GetHeight() {
			return height;
		}
	
		public void SetHeight(int height) {
			this.height = height;
		}
	
		public int GetWidth() {
			return width;
		}
	
		public void SetWidth(int width) {
			this.width = width;
		}
	
		public int GetLeftOffset() {
			return leftOffset;
		}
	
		public void SetLeftOffset(int leftOffset) {
			this.leftOffset = leftOffset;
		}
	
		public int GetTopOffset() {
			return topOffset;
		}
	
		public void SetTopOffset(int topOffset) {
			this.topOffset = topOffset;
		}
	
		public bool IsEnglish() {
			return isEnglish;
		}
	
		public void SetEnglish(bool isEnglish) {
			this.isEnglish = isEnglish;
		}
	
		public bool IsVisible() {
			return visible;
		}
	
		public void SetVisible(bool visible) {
			this.visible = visible;
		}
	
		public bool IsLeft() {
			return isLeft;
		}
	
		public void SetLeft(bool isLeft) {
			this.isLeft = isLeft;
		}
	
		public bool IsWait() {
			return isWait;
		}
	
		public void SetWait(bool isWait) {
			this.isWait = isWait;
		}
	
		public void Dispose() {
			if (strings != null) {
				strings.Dispose();
				strings = null;
			}
		}
	
	}
}
