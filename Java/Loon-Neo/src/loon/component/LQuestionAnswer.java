/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.LSysException;
import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.canvas.LColor;
import loon.events.ActionKey;
import loon.events.SysTouch;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.PointF;
import loon.geom.RectF;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 问答选择用组件(仅限单选与多选,填空暂未实现,因为输入部分还需要修改下……)
 */
public class LQuestionAnswer extends LContainer {

	public static interface ClickEvent {

		public void onSelected(int questionIndex, int index);

	}

	public static class QAObject {

		public int state = -1;

		protected final String _question;

		protected final TArray<String> _answers;

		protected final IntArray _corrects;

		public QAObject(String question) {
			this(question, (IntArray) null, (String[]) null);
		}

		public QAObject(String question, String... as) {
			this(question, (IntArray) null, as);
		}

		public QAObject(String question, IntArray cts, String... as) {

			this._question = StringUtils.toString(question, LSystem.EMPTY);

			if (null != cts) {
				this._corrects = new IntArray(cts);
			} else {
				this._corrects = new IntArray();
			}

			if (null != as) {

				final int len = as.length;
				this._answers = new TArray<String>(len);

				for (int i = 0; i < len; i++) {
					String res = as[i];
					if (null != res && !_answers.contains(res)) {
						this._answers.add(res);
					}
				}

			} else {
				this._answers = new TArray<String>();
			}
		}

		public QAObject addAnswer(String answer) {
			String res = StringUtils.toString(answer, LSystem.EMPTY);
			if (!_answers.contains(res)) {
				_answers.add(res);
			}
			return this;
		}

		public boolean removeAnswer(String answer) {
			return _answers.remove(StringUtils.toString(answer, LSystem.EMPTY));
		}

		public String removeAnswerIndex(int idx) {
			return _answers.removeIndex(idx);
		}

		public QAObject addCorrect(int... corrects) {
			for (int i = 0; i < corrects.length; i++) {
				int v = corrects[i];
				if (!_corrects.contains(v)) {
					_corrects.add(v);
				}
			}
			return this;
		}

		public boolean removeCorrect(int correct) {
			return _corrects.removeValue(correct);
		}

		public String removeCorrectIndex(int idx) {
			return _answers.removeIndex(idx);
		}

		public IntArray getCorrects() {
			return _corrects.cpy();
		}

		public TArray<String> getAnswers() {
			return _answers.cpy();
		}

		public String getQuestion() {
			return _question;
		}

	}

	protected class QAData {

		final TArray<String> qlist;

		final TArray<TArray<String>> alist;

		protected final QAObject object;

		protected float qWidth;

		protected float qHeight;

		protected float aWidth;

		protected float aHeight;

		protected final IntArray selectAnswer;

		public QAData(QAObject o, IFont qfont, IFont afont, float w) {
			this.object = o;
			this.selectAnswer = new IntArray();
			PointF size = null;
			final String question = o._question;
			if (question != null) {
				this.qlist = FontUtils.splitLines(question, qfont, w);
				if (qfont instanceof LFont) {
					LSTRDictionary.get().bind((LFont) qfont, qlist);
				}
				size = FontUtils.getTextWidthAndHeight(qfont, qlist);
				this.qWidth = size.x;
				this.qHeight = size.y;
			} else {
				this.qlist = new TArray<String>();
			}
			this.alist = new TArray<TArray<String>>();
			final TArray<String> as = o._answers;
			if (as != null) {
				for (int i = 0; i < as.size; i++) {
					String mes = as.get(i);
					if (null != mes) {
						TArray<String> list = FontUtils.splitLines(mes, afont, w);
						if (qfont instanceof LFont) {
							LSTRDictionary.get().bind((LFont) afont, list);
						}
						alist.add(list);
						size = FontUtils.getTextWidthAndHeight(afont, list);
						this.aWidth = MathUtils.max(this.aWidth, size.x);
						this.aHeight += size.y;
					}
				}
			}
		}

	}

	private final ActionKey _touchEvent = new ActionKey(ActionKey.NORMAL);

	// 问题与答案字体间的上下显示间隔
	private float qaSpace = 12f;

	// 答案字体间的上下显示间隔
	private float answerFontSpace = 8f;

	// 问题字体间的上下显示间隔
	private float questionFontSpace = 1f;

	// 用户选中的答案颜色
	private final LColor _answerColor;

	private ClickEvent _clickEvent;

	private boolean _over, _pressed, _focused;

	private boolean _allowMultiChoice = false;

	private final TArray<QAData> _objects;

	private final IFont _qFont;

	private final IFont _aFont;

	private PointF _offsetFont;

	private int _questionIndex = 0;

	private boolean _dirty = false;

	private boolean questionAdded;

	private boolean questionSubed;

	private boolean autoNext;

	public LQuestionAnswer(int x, int y, int w, int h) {
		this(LColor.orange, x, y, w, h);
	}

	public LQuestionAnswer(LColor selected, int x, int y, int w, int h) {
		this(LFont.getDefaultFont(), selected, x, y, w, h);
	}

	public LQuestionAnswer(IFont font, LColor selected, int x, int y, int w, int h) {
		this(font, font, selected, x, y, w, h);
	}

	public LQuestionAnswer(IFont questionFont, IFont answerFont, LColor selected, int x, int y, int w, int h) {
		super(x, y, w, h);
		this._qFont = questionFont;
		this._aFont = answerFont;
		this._answerColor = selected;
		_objects = new TArray<QAData>();
		_offsetFont = new PointF();
	}

	protected TArray<QAObject> getUserList(boolean checkError) {
		TArray<QAObject> list = new TArray<QAObject>();
		for (int i = _objects.size - 1; i > -1; i--) {
			QAData data = _objects.get(i);
			if (data != null) {
				if (checkError) {
					if (!checkQA(i)) {
						list.add(data.object);
					}
				} else {
					if (checkQA(i)) {
						list.add(data.object);
					}
				}
			}
		}
		return list.reverse();

	}

	public TArray<QAObject> getUserCorrects() {
		return getUserList(false);
	}

	public TArray<QAObject> getUserErrors() {
		return getUserList(true);
	}

	public boolean checkChoice() {
		return checkChoice(this._questionIndex);
	}

	public boolean checkChoice(int idx) {
		if (idx < 0 || idx >= _objects.size) {
			throw new LSysException("QA Object is null !");
		}
		QAData data = _objects.get(idx);
		if (data == null) {
			return false;
		}
		return data.selectAnswer.size() > 0;
	}

	/**
	 * 用户已答题数
	 * 
	 * @return
	 */
	public float getUserAnswerAmount() {
		int amount = 0;
		for (int i = _objects.size - 1; i > -1; i--) {
			QAData data = _objects.get(i);
			if (data.selectAnswer.size() > 0) {
				amount++;
			}
		}
		return amount;
	}

	public float getQuestionAmount() {
		return _objects.size;
	}

	/**
	 * 已答题进度
	 * 
	 * @return
	 */
	public float getPercent() {
		return (getUserAnswerAmount() / _objects.size) * 100f;
	}

	public String getPercentString() {
		return (int) getPercent() + "%";
	}

	public boolean checkQA() {
		return checkQA(this._questionIndex);
	}

	public boolean checkQA(int idx) {
		if (idx < 0 || idx >= _objects.size) {
			throw new LSysException("QA Object is null !");
		}
		QAData data = _objects.get(idx);
		if (data == null) {
			return false;
		}
		final IntArray userAnswer = data.selectAnswer;
		final IntArray okAnswer = data.object._corrects;
		final int size = okAnswer.length;
		int amount = 0;
		for (int i = size - 1; i > -1; i--) {
			for (int j = userAnswer.size() - 1; j > -1; j--) {
				if (!okAnswer.contains(userAnswer.get(j))) {
					return false;
				} else {
					amount++;
				}
			}
		}
		return amount == okAnswer.size();
	}

	public IntArray getCorrects() {
		return getCorrects(this._questionIndex);
	}

	/**
	 * 获得指定索引的正确答案
	 * 
	 * @param idx
	 * @return
	 */
	public IntArray getCorrects(int idx) {
		if (idx < 0 || idx >= _objects.size) {
			throw new LSysException("QA Object is null !");
		}
		return _objects.get(idx).object._corrects.cpy();
	}

	public IntArray getUserSelectCorrects() {
		return getUserSelectCorrects(this._questionIndex);
	}

	/**
	 * 获得指定索引的用户答案
	 * 
	 * @param idx
	 * @return
	 */
	public IntArray getUserSelectCorrects(int idx) {
		if (idx < 0 || idx >= _objects.size) {
			throw new LSysException("QA Object is null !");
		}
		return _objects.get(idx).selectAnswer.cpy();
	}

	public QAObject getQA(int idx) {
		if (idx < 0 || idx >= _objects.size) {
			throw new LSysException("QA Object is null !");
		}
		return _objects.get(idx).object;
	}

	public LQuestionAnswer addQA(QAObject o) {
		if (o == null) {
			return this;
		}
		_dirty = true;
		for (int i = _objects.size - 1; i > -1; i--) {
			QAData data = _objects.get(i);
			if (o.equals(data.object)) {
				return this;
			}
		}
		_objects.add(new QAData(o, _qFont, _aFont, getWidth()));
		return this;
	}

	public LQuestionAnswer removeQA(QAObject o) {
		if (o == null) {
			return this;
		}
		_dirty = true;
		for (int i = _objects.size - 1; i > -1; i--) {
			QAData data = _objects.get(i);
			if (o.equals(data.object)) {
				_objects.removeIndex(i);
			}
		}
		return this;
	}

	public LQuestionAnswer addUserAnswer(int... aidx) {
		if (_questionIndex != -1 && _objects.size > 0) {
			if (!_allowMultiChoice) {
				clearUserAnswer();
			}
			final IntArray userAnswer = _objects.get(_questionIndex).selectAnswer;
			for (int i = 0; i < aidx.length; i++) {
				int v = aidx[i];
				if (!userAnswer.contains(v)) {
					userAnswer.add(v);
				}
			}
		}
		return this;
	}

	public LQuestionAnswer clearUserAnswer() {
		if (_questionIndex != -1 && _objects.size > 0) {
			_objects.get(_questionIndex).selectAnswer.clear();
		}
		return this;
	}

	public LQuestionAnswer updateUserAnswer(int... aidx) {
		clearUserAnswer();
		return addUserAnswer(aidx);
	}

	public int getQuestionIndex() {
		return _questionIndex;
	}

	public LQuestionAnswer setQuestionIndex(int idx) {
		if (idx > -1 && idx < _objects.size - 1) {
			_questionIndex = idx;
		}
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}

		super.update(elapsedTime);

		final boolean freeTouched = SysTouch.isUp();
		if (questionAdded && questionSubed) {
			questionAdded = questionSubed = false;
		}
		if (questionAdded && freeTouched) {
			_questionIndex++;
			questionAdded = false;
		}
		if (questionSubed && freeTouched) {
			_questionIndex--;
			questionSubed = false;
		}
		if (_focused) {
			_pressed = true;
			return;
		}
	}

	public PointF getOffsetFont() {
		return _offsetFont;
	}

	public LQuestionAnswer setOffsetFont(PointF offset) {
		this._offsetFont = offset;
		return this;
	}

	public LQuestionAnswer nextQuestion() {
		if (_questionIndex < _objects.size - 1) {
			questionAdded = true;
		}
		return this;
	}

	public LQuestionAnswer backQuestion() {
		if (_questionIndex > 0) {
			questionSubed = true;
		}
		return this;
	}

	public boolean isAllowMultiChoice() {
		return _allowMultiChoice;
	}

	public LQuestionAnswer setAllowMultiChoice(boolean allowMultiChoice) {
		this._allowMultiChoice = allowMultiChoice;
		return this;
	}

	public boolean isTouchOver() {
		return this._over;
	}

	public boolean isTouchPressed() {
		return this._pressed;
	}

	@Override
	protected void processTouchDragged() {
		this._over = this._pressed = this.intersects(getUITouchX(), getUITouchY());
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		if (!isVisible()) {
			return;
		}
		if (!_touchEvent.isPressed()) {
			this._pressed = true;
			super.processTouchPressed();
			this._touchEvent.press();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!isVisible()) {
			return;
		}
		if (_touchEvent.isPressed()) {
			this._pressed = false;
			int idx = getQASelected(getUITouchX(), getUITouchY());
			if (idx != -1) {
				addUserAnswer(idx);
				if (_clickEvent != null) {
					_clickEvent.onSelected(this._questionIndex, idx);
				}
				if (this.autoNext && !this._allowMultiChoice) {
					nextQuestion();
				}
			} else if (_clickEvent != null) {
				_clickEvent.onSelected(this._questionIndex, idx);
			}
			super.processTouchReleased();
			_touchEvent.release();
		}
	}

	public LQuestionAnswer setQAListener(ClickEvent event) {
		_clickEvent = event;
		return this;
	}

	public ClickEvent getQAListener() {
		return this._clickEvent;
	}

	@Override
	protected void processTouchEntered() {
		this._over = true;
	}

	@Override
	protected void processTouchExited() {
		this._over = this._pressed = false;
	}

	public RectF getQASizeSelected(int index) {

		RectF.Range rect = new RectF.Range();
		final int idx = this._questionIndex;

		if (idx != -1 && _objects.size > 0) {

			QAData o = _objects.get(idx);
			if (o != null && o.object != null) {
				int fontHeight = _qFont.getHeight();
				float qHeight = MathUtils.max(((o.qlist.size - 1) * (fontHeight + questionFontSpace)), o.qHeight);
				fontHeight = _aFont.getHeight();
				final float spaceHeight = qHeight + qaSpace;
				final TArray<TArray<String>> as = o.alist;

				for (int i = 0, count = 0; i < as.size; i++) {
					TArray<String> a = as.get(i);
					int lastIdx = 0;
					for (int j = 0; j < a.size; j++) {
						String mes = a.get(j);
						qHeight = ((count) * (fontHeight + answerFontSpace));
						PointF size = FontUtils.getTextWidthAndHeight(_aFont, mes);
						if (lastIdx != i) {
							rect.set(_offsetFont.x, spaceHeight + qHeight + _offsetFont.y, size.x, size.y);
						} else {
							rect.union(_offsetFont.x, spaceHeight + qHeight + _offsetFont.y, size.x, size.y);
						}
						lastIdx = i;
						count++;
					}
					if (index == i) {
						return rect.getRect();
					}
				}
			}

		}

		return rect.getRect();
	}

	public int getQASelected(float x, float y) {

		final int idx = this._questionIndex;

		if (idx != -1 && _objects.size > 0) {

			QAData o = _objects.get(idx);

			if (o != null && o.object != null) {
				int fontHeight = _qFont.getHeight();
				float qHeight = MathUtils.max(((o.qlist.size - 1) * (fontHeight + questionFontSpace)), o.qHeight);
				fontHeight = _aFont.getHeight();
				final float spaceHeight = qHeight + qaSpace;
				final TArray<TArray<String>> as = o.alist;
				for (int i = 0, count = 0; i < as.size; i++) {
					TArray<String> a = as.get(i);
					for (int j = 0; j < a.size; j++) {
						String mes = a.get(j);
						qHeight = ((count) * (fontHeight + answerFontSpace));
						PointF size = FontUtils.getTextWidthAndHeight(_aFont, mes);
						if (CollisionHelper.contains(_offsetFont.x, spaceHeight + qHeight + _offsetFont.y, size.x + 2,
								size.y + 2, x, y, 2f, 2f)) {
							return i;
						}
						count++;
					}
				}
			}
		}
		return -1;
	}

	public TArray<String> getMessageData() {
		final TArray<String> list = new TArray<String>();
		for (int j = 0; j < _objects.size; j++) {
			QAData o = _objects.get(j);
			if (o != null && o.object != null) {
				final TArray<TArray<String>> as = o.alist;
				for (int i = 0; i < as.size; i++) {
					TArray<String> a = as.get(i);
					for (int n = 0; n < a.size; n++) {
						String mes = a.get(n);
						list.add(mes);
					}
				}

			}
		}
		return list;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (_dirty) {
			if (_qFont instanceof LFont) {
				LSTRDictionary.get().bind((LFont) _aFont, getMessageData());
			}
			_dirty = false;
		}

		final int idx = this._questionIndex;
		if (idx != -1 && _objects.size > 0) {
			QAData o = _objects.get(idx);
			if (o != null && o.object != null) {
				g.setClip(x, y, getWidth(), getHeight());
				IFont tmpFont = g.getFont();
				paint(g, o, x, y);
				g.clearClip();
				g.setFont(tmpFont);
			}
		}
	}

	protected void paint(GLEx g, QAData o, float x, float y) {

		final IntArray selected = o.selectAnswer;

		int fontHeight = _qFont.getHeight();
		g.setFont(_qFont);

		float qHeight = 0;

		TArray<String> qs = o.qlist;
		for (int i = 0; i < qs.size; i++) {
			String q = qs.get(i);
			qHeight = (i * (fontHeight + questionFontSpace));
			g.drawString(q, x + _offsetFont.x, y + qHeight + _offsetFont.y);
		}

		qHeight = MathUtils.max(qHeight, o.qHeight);
		fontHeight = _aFont.getHeight();

		g.setFont(_qFont);

		final float spaceHeight = qHeight + qaSpace;
		final TArray<TArray<String>> as = o.alist;

		for (int i = 0, count = 0; i < as.size; i++) {
			TArray<String> a = as.get(i);
			for (int j = 0; j < a.size; j++) {
				String mes = a.get(j);
				qHeight = ((count) * (fontHeight + answerFontSpace));
				if (selected.contains(i)) {
					g.drawString(mes, x + _offsetFont.x, y + spaceHeight + qHeight + _offsetFont.y, _answerColor);
				} else {
					g.drawString(mes, x + _offsetFont.x, y + spaceHeight + qHeight + _offsetFont.y);
				}
				count++;
			}
		}
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public LQuestionAnswer setDirty(boolean d) {
		this._dirty = d;
		return this;
	}

	public float getQuestionFontSpace() {
		return questionFontSpace;
	}

	public LQuestionAnswer setQuestionFontSpace(float questionSpace) {
		this.questionFontSpace = questionSpace;
		return this;
	}

	public float getAnswerFontSpace() {
		return answerFontSpace;
	}

	public LQuestionAnswer setAnswerFontSpace(float answerSpace) {
		this.answerFontSpace = answerSpace;
		return this;
	}

	public float getQASpace() {
		return qaSpace;
	}

	public LQuestionAnswer setQASpace(float qaSpace) {
		this.qaSpace = qaSpace;
		return this;
	}

	public boolean isAutoNext() {
		return autoNext;
	}

	public LQuestionAnswer setAutoNext(boolean a) {
		this.autoNext = a;
		return this;
	}

	@Override
	public String getUIName() {
		return "QuestionAnswer";
	}

	@Override
	public void destory() {

	}

}
