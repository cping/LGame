/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.IFont;
import loon.geom.Polygon;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.Duration;

/**
 * 漫画型气泡对话框构建用组件，用于在角色头顶显示指向性对话（漫画式小尾巴指向角色），或者特殊场合的对话效果
 */
public class LSpeechDialog extends LComponent implements FontSet<LSpeechDialog> {

	public static enum BubbleType {
		ROUND, ELLIPSE, CIRCLE
	}

	public static class Character {

		public String name;
		public LColor bubbleColor;
		public LColor textColor;
		public LTexture avatar;
		public String position;

		public Character(String name, LColor bubbleColor, LColor textColor, LTexture avatar, String position) {
			this.name = name;
			this.bubbleColor = bubbleColor;
			this.textColor = textColor;
			this.avatar = avatar;
			this.position = position;
		}
	}

	public static class TextSegment {

		public String content;
		public LColor color;
		public boolean shakeX, shakeY, flicker, gradient, scale;
		boolean completed;
		public int shakeOffsetX, shakeOffsetY;
		public float effectFrequency;

		public TextSegment(String content, LColor color) {
			this(content, color, false, false, false, false, false, 0, 0, 0);
		}

		/**
		 * 构建一个文字显示模组
		 * 
		 * @param content         文字内容
		 * @param color           颜色
		 * @param shakex          是否震荡x轴
		 * @param shakey          是否震荡y轴
		 * @param flicker         是否闪烁
		 * @param gradient        是否过度色
		 * @param scale           是否缩放
		 * @param sx              x轴震荡幅度
		 * @param sy              y轴震荡幅度
		 * @param effectFrequency 特效触发频率
		 */
		public TextSegment(String content, LColor color, boolean shakex, boolean shakey, boolean flicker,
				boolean gradient, boolean scale, int sx, int sy, float effectFrequency) {
			this.content = content;
			this.color = color;
			this.shakeX = shakex;
			this.shakeY = shakey;
			this.flicker = flicker;
			this.gradient = gradient;
			this.scale = scale;
			this.shakeOffsetX = sx;
			this.shakeOffsetY = sy;
			this.effectFrequency = effectFrequency;
		}
	}

	public static class Dialogue {

		public Character speaker;
		public TArray<TextSegment> segments;
		public float printSpeed;
		public String tailStyle;
		public BubbleType bubbleType;
		public Vector2f textOffset = new Vector2f();

		public Dialogue(Character speaker, TArray<TextSegment> segments, float printSpeed, BubbleType bubbleType,
				String tailStyle) {
			this(speaker, segments, printSpeed, bubbleType, tailStyle, 0f, 0f);
		}

		public Dialogue(Character speaker, TArray<TextSegment> segments, float printSpeed, BubbleType bubbleType,
				String tailStyle, float x, float y) {
			this.speaker = speaker;
			this.segments = segments;
			this.printSpeed = printSpeed;
			this.bubbleType = bubbleType;
			this.tailStyle = tailStyle;
			this.textOffset.set(x, y);
		}

		public String getFullText() {
			StrBuilder sbr = new StrBuilder();
			sbr.append(speaker.name).append(LSystem.COLON);
			for (TextSegment seg : segments) {
				sbr.append(seg.content);
			}
			return sbr.toString();
		}
	}

	private final Vector2f _bubbleTailOffset = new Vector2f();

	private final Vector2f _avatarOffset = new Vector2f(10f);

	private final Vector2f _position = new Vector2f();

	private final StrBuilder _message = new StrBuilder();

	private IntMap<Polygon> _tailShapes = new IntMap<Polygon>();

	private String _messageString;

	private boolean _initNativeDraw = false;

	private int _roundRadius = 20;

	private int _tailSize = 50;

	private boolean _isRunning = false;

	private IFont _font;

	private LColor _fontColor = LColor.white;

	private ObjectMap<String, Character> _characters = new ObjectMap<String, Character>();

	private TArray<Dialogue> _dialogues = new TArray<Dialogue>();

	private int _currentDialogueIndex = 0;

	private int _charIndex = 0;

	private long _elapsedTime = 0;

	private float _elapsedTimeCount = 0;

	private float _textSpace = 0f;

	private float _leftTextOffsetX = 0f;

	private float _leftTextOffsetY = 0f;

	public LSpeechDialog(int x, int y, int width, int height) {
		this(x, y, width, height, 20);
	}

	public LSpeechDialog(int x, int y, int width, int height, int round) {
		this(SkinManager.get().getMessageSkin().getFont(), x, y, width, height, round);
	}

	public LSpeechDialog(IFont font, int x, int y, int width, int height, int round) {
		super(x, y, width, height);
		setFont(font);
		_roundRadius = round;
		_leftTextOffsetX = width / 7;
		_leftTextOffsetY = height / 6;
		_isRunning = true;
	}

	public LSpeechDialog putCharacter(String name, LColor bubbleColor, LColor textColor, LTexture avatar,
			String position) {
		return putCharacter(name, new Character(name, bubbleColor, textColor, avatar, position));
	}

	public LSpeechDialog putCharacter(String name, Character ch) {
		_characters.put(name, ch);
		_initNativeDraw = false;
		return this;
	}

	public Character removeCharacter(String name) {
		return _characters.remove(name);
	}

	public LSpeechDialog putDialogue(String name, TArray<TextSegment> segments, float printSpeed, BubbleType bubbleType,
			String tailStyle) {
		return putDialogue(name, segments, printSpeed, bubbleType, tailStyle, 0f, 0f);
	}

	public LSpeechDialog putDialogue(String name, TArray<TextSegment> segments, float printSpeed, BubbleType bubbleType,
			String tailStyle, float x, float y) {
		_dialogues.add(new Dialogue(_characters.get(name), segments, printSpeed, bubbleType, tailStyle, x, y));
		return this;
	}

	public LSpeechDialog putDialogue(Dialogue d) {
		_dialogues.add(d);
		_initNativeDraw = false;
		return this;
	}

	public boolean removeDialogue(Dialogue d) {
		return _dialogues.remove(d);
	}

	@Override
	public LSpeechDialog setFont(IFont font) {
		_font = font;
		return this;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public LSpeechDialog setFontColor(LColor color) {
		_fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor;
	}

	protected int getTotalChars(Dialogue dialogue) {
		int count = 0;
		for (TextSegment seg : dialogue.segments) {
			count += seg.content.length();
		}
		return count;
	}

	protected void replayDialogue(int index) {
		_currentDialogueIndex = index;
	}

	private void drawSpeechBubble(GLEx g, long timer, int x, int y, int w, int h, Dialogue dialogue, BubbleType type,
			String tailStyle, LColor backgroundColor, LColor textColor, LTexture avatar, int charIndex) {
		String side;
		switch (dialogue.speaker.position) {
		case "left":
			side = "left";
			break;
		case "right":
			side = "right";
			break;
		default:
			side = "bottom";
			break;
		}
		Vector2f target = getBubbleTailTarget(dialogue.speaker, x, y, w, h);
		Shape tailShape = createTailShape(x, y, w, h, target.x(), target.y(), tailStyle, side);
		paintBubble(g, tailShape, type, x, y, w, h, backgroundColor);
		paintAvatar(g, avatar, x, y);
		Vector2f offset = dialogue.textOffset;
		drawTextWithEffects(g, timer, x + _leftTextOffsetX + offset.x, y + _leftTextOffsetY + offset.y, dialogue,
				charIndex);
	}

	private void paintBubble(GLEx g, Shape tailShape, BubbleType type, float x, float y, float w, float h,
			LColor backgroundColor) {
		g.setColor(backgroundColor);
		if (type == BubbleType.ROUND) {
			g.fillRoundRect(x, y, w, h, _roundRadius);
		} else if (type == BubbleType.ELLIPSE) {
			g.fillOval(x - 1, y - 1, w + 2, h + 2);
		} else if (type == BubbleType.CIRCLE) {
			g.fillCircle(x - 1, y - 1, MathUtils.min(w, h) + 2);
		}
		g.fill(tailShape);
	}

	protected Shape createTailShape(int x, int y, int w, int h, int targetX, int targetY, String tailStyle,
			String side) {
		int result = 1;
		result = LSystem.unite(result, x);
		result = LSystem.unite(result, y);
		result = LSystem.unite(result, w);
		result = LSystem.unite(result, h);
		result = LSystem.unite(result, targetX);
		result = LSystem.unite(result, targetY);
		result = LSystem.unite(result, tailStyle);
		result = LSystem.unite(result, side);
		Polygon tailShape = _tailShapes.get(result);
		if (tailShape == null) {
			tailShape = new Polygon();
			if ("left".equals(side)) {
				int anchorY = y + h / 2;
				if ("zigzag".equals(tailStyle)) {
					tailShape.addPoint(x - _roundRadius, anchorY);
					tailShape.addPoint(x, anchorY - _roundRadius / 2);
					tailShape.addPoint(x - _roundRadius / 2, anchorY);
					tailShape.addPoint(x, anchorY + _roundRadius / 2);
				} else {
					tailShape.addPoint(x - _roundRadius, anchorY);
					tailShape.addPoint(x, anchorY - _roundRadius / 2);
					tailShape.addPoint(x, anchorY + _roundRadius / 2);
				}
			} else if ("right".equals(side)) {
				int anchorY = y + h / 2;
				if ("zigzag".equals(tailStyle)) {
					tailShape.addPoint(x + w + _roundRadius, anchorY);
					tailShape.addPoint(x + w, anchorY - _roundRadius / 2);
					tailShape.addPoint(x + w + _roundRadius / 2, anchorY);
					tailShape.addPoint(x + w, anchorY + _roundRadius / 2);
				} else {
					tailShape.addPoint(x + w + _roundRadius, anchorY);
					tailShape.addPoint(x + w, anchorY - _roundRadius / 2);
					tailShape.addPoint(x + w, anchorY + _roundRadius / 2);
				}
			} else {
				int anchorX = MathUtils.max(x, MathUtils.min(targetX, x + w));
				int anchorY = y + h;
				tailShape.addPoint(targetX, targetY);
				tailShape.addPoint(anchorX - _roundRadius / 2, anchorY);
				tailShape.addPoint(anchorX + _roundRadius / 2, anchorY);
			}
			_tailShapes.put(result, tailShape);
		}
		return tailShape;
	}

	private Vector2f getBubbleTailTarget(Character speaker, int bubbleX, int bubbleY, int bubbleW, int bubbleH) {
		switch (speaker.position) {
		case "left":
			return _position.set(bubbleX + _tailSize + _bubbleTailOffset.x,
					bubbleY + bubbleH + (_tailSize / 2 + 5) + _bubbleTailOffset.y);
		case "center":
			return _position.set(bubbleX + bubbleW / 2 + _bubbleTailOffset.x,
					bubbleY + bubbleH + (_tailSize / 2 + 5) + _bubbleTailOffset.y);
		case "right":
			return _position.set(bubbleX + bubbleW - _tailSize + _bubbleTailOffset.x,
					bubbleY + bubbleH + (_tailSize / 2 + 5) + _bubbleTailOffset.y);
		default:
			return _position.set(bubbleX + bubbleW / 2 + _bubbleTailOffset.x,
					bubbleY + bubbleH + (_tailSize / 2 + 5) + _bubbleTailOffset.y);
		}
	}

	private void paintAvatar(GLEx g, LTexture avatar, int x, int y) {
		if (avatar != null) {
			g.draw(avatar, x + _avatarOffset.x, y + _avatarOffset.y);
		}
	}

	private void drawTextWithEffects(GLEx g, long time, float x, float y, Dialogue dialogue, int charIndex) {
		float cursorX = x;
		float cursorY = y;
		int printedChars = 0;
		TArray<TextSegment> segments = dialogue.segments;
		for (int n = 0; n < segments.size; n++) {
			TextSegment seg = segments.get(n);
			LColor baseColor = seg.color != null ? seg.color : dialogue.speaker.textColor;
			final String text = seg.content;
			final int len = text.length();
			char[] messages = text.toCharArray();
			for (int i = 0; i < len; i++) {
				if (printedChars >= charIndex) {
					return;
				}
				final char c = messages[i];
				if (c == LSystem.LF) {
					cursorX = x;
					cursorY += _font.getHeight() + 1;
					continue;
				}
				String mes = String.valueOf(c);
				final int width = (StringUtils.isCJK(c) || StringUtils.isFullChar(c))
						? (_font.stringWidth(mes) + _font.getSize()) / 2
						: (_font.charWidth(c) + _font.getSize() - 4) / 2;
				float offsetX = 0, offsetY = 0;
				if (seg.shakeX) {
					offsetX = (MathUtils.sin(time * seg.effectFrequency) * seg.shakeOffsetX) / 2;
				}
				if (seg.shakeY) {
					offsetY = (MathUtils.cos(time * seg.effectFrequency) * seg.shakeOffsetY) / 2;
				}
				if (seg.flicker && time % 2 == 0) {
					printedChars++;
					cursorX += width / 2;
					continue;
				}
				if (seg.gradient) {
					float hue = MathUtils.abs(MathUtils.max(1f, (float) (_elapsedTimeCount * 10)));
					baseColor = new LColor(baseColor.r * hue, baseColor.g * hue, baseColor.b * hue, 1f);
				}
				if (seg.scale) {
					float scaleFactor = 1f + 0.2f * MathUtils.sin(time * seg.effectFrequency);
					g.drawString(mes, cursorX + offsetX, cursorY + offsetY, scaleFactor, scaleFactor, 0f, 0f, 0f,
							baseColor);
				} else {
					g.drawString(mes, cursorX + offsetX, cursorY + offsetY, baseColor);
				}
				cursorX += (width + _textSpace);
				printedChars++;
			}
		}
	}

	@Override
	public void process(final long elapsedTime) {
		if (!_isRunning || _currentDialogueIndex >= _dialogues.size()) {
			return;
		}
		Dialogue dialogue = _dialogues.get(_currentDialogueIndex);
		float delta = MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED);
		_elapsedTime = elapsedTime;
		_elapsedTimeCount += delta;
		int totalChars = getTotalChars(dialogue);
		if (_charIndex >= totalChars && _currentDialogueIndex < _dialogues.size - 1) {
			_currentDialogueIndex++;
			_charIndex = 0;
		}
		if (_charIndex < totalChars) {
			float interval = dialogue.printSpeed;
			if (_elapsedTimeCount >= interval) {
				_charIndex++;
				_elapsedTimeCount = 0;
			}
		}
	}

	/**
	 * 直接进入下一个对话模组
	 */
	public boolean nextDialogue() {
		if (_currentDialogueIndex >= _dialogues.size()) {
			return false;
		}
		Dialogue dialogue = _dialogues.get(_currentDialogueIndex);
		int totalChars = getTotalChars(dialogue);
		if (_isRunning) {
			_charIndex = totalChars;
		} else {
			_charIndex++;
		}
		if (_currentDialogueIndex < _dialogues.size - 1) {
			_currentDialogueIndex++;
			_charIndex = 0;
			_elapsedTimeCount = 0;
		}
		return true;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (!_initNativeDraw) {
			_message.clear();
			for (Dialogue dialog : _dialogues) {
				for (TextSegment seg : dialog.segments) {
					_message.append(seg.content);
				}
			}
			if (_message.size() > 0) {
				this._messageString = StringUtils.unificationChars(_message.toString().toCharArray());
				_initNativeDraw = true;
			}
		}
		IFont oldFont = g.getFont();
		g.setFont(_font);
		int oldColor = g.color();
		Dialogue dialogue = _dialogues.get(_currentDialogueIndex);
		Character speaker = dialogue.speaker;
		drawSpeechBubble(g, _elapsedTime, x, y, width(), height(), dialogue, dialogue.bubbleType, dialogue.tailStyle,
				speaker.bubbleColor, speaker.textColor, speaker.avatar, _charIndex);
		g.setFont(oldFont);
		g.setColor(oldColor);
	}

	public String getMessageString() {
		return _messageString;
	}

	public Vector2f getBubbleTailOffset() {
		return _bubbleTailOffset;
	}

	public LSpeechDialog setBubbleTailOffset(float x, float y) {
		_bubbleTailOffset.set(x, y);
		return this;
	}

	public Vector2f getAvatarOffset() {
		return _avatarOffset;
	}

	public LSpeechDialog setAvatarOffset(float x, float y) {
		_avatarOffset.set(x, y);
		return this;
	}

	public float getTextSpace() {
		return _textSpace;
	}

	public LSpeechDialog setTextSpace(float space) {
		this._textSpace = space;
		return this;
	}

	public float getLeftTextOffsetX() {
		return _leftTextOffsetX;
	}

	public void setLeftTextOffsetX(float x) {
		this._leftTextOffsetX = x;
	}

	public float getLeftTextOffsetY() {
		return _leftTextOffsetY;
	}

	public void setLeftTextOffsetY(float y) {
		this._leftTextOffsetY = y;
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public void setRunning(boolean r) {
		this._isRunning = r;
	}

	public int getCurrentDialogueIndex() {
		return _currentDialogueIndex;
	}

	public void setCurrentDialogueIndex(int d) {
		this._currentDialogueIndex = d;
	}

	public int getCharIndex() {
		return _charIndex;
	}

	public void setCharIndex(int i) {
		this._charIndex = i;
	}

	public int getTailSize() {
		return _tailSize;
	}

	public void setTailSize(int t) {
		this._tailSize = t;
	}

	@Override
	public String getUIName() {
		return "SpeechBubble";
	}

	@Override
	public void destory() {
		_isRunning = false;
		_initNativeDraw = false;
		_tailShapes.clear();
	}

}
