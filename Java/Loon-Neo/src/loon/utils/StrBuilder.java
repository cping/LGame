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
package loon.utils;

import loon.LSysException;
import loon.LSystem;

/**
 * @info:
 *
 * 		StrBuilder是一个简化的,基本等价于StringBuilder的字符串操作类,作用是当Loon处于不存在Java类库的环境时(比如typescript版之类)
 * 取代StringBuilder.总之是为了移植多平台的产物,如果能明确使用平台的话就没必要用……
 * 
 * @code:
 * 
*         <pre>
 * 		  StringBuilder sbr = new StringBuilder(); 
 *        sbr.append("ssssssssttts");
 *        StrBuilder str = new StrBuilder(); 
 *        str.append("ssssssssttts");
 *        System.out.println("A: " + sbr); 
 *        System.out.println("B: " + str);
 *        sbr.deleteCharAt(5); 
 *        str.deleteCharAt(5); 
 *        System.out.println("A: " + sbr); 
 *        System.out.println("B: " + str); 
 *        sbr.append(12345);
 *        sbr.append((String) null); 
 *        str.append(12345); 
 *        str.append((String)null); 
 *        System.out.println("A: " + sbr); 
 *        System.out.println("B: " + str); 
 *        System.out.println(str.equals(sbr)); 
 *        String a = sbr.substring(4,15); 
 *        String b = str.substring(4, 15); 
 *        System.out.println("A: " + a);
 *        System.out.println("B: " + b); 
 *        System.out.println(str.equals(sbr)); 
 *        a = sbr.toString();
 *        b = str.toString(); 
 *        str.delete(3, a.length());
 *        String nb = str.toString(); 
 *        System.out.println(a);
 *        System.out.println(b); 
 *        System.out.println("nb:" + nb);
 *        System.out.println(b);
 *        </pre>
 * 
 */
public class StrBuilder implements CharSequence, Appendable {

	private String _tempResult = null;

	private boolean _dirty = false;

	private char[] _values = null;

	private int _currentIndex = 0;

	private int _hash = 0;

	public static StrBuilder at() {
		return at(CollectionUtils.INITIAL_CAPACITY);
	}

	public static StrBuilder at(int cap) {
		return new StrBuilder(cap);
	}

	public static StrBuilder at(CharSequence... strs) {
		return new StrBuilder(strs);
	}

	public StrBuilder() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public StrBuilder(int cap) {
		this.reset(cap);
	}

	public StrBuilder(CharSequence... strs) {
		this(StringUtils.isEmpty(strs) ? CollectionUtils.INITIAL_CAPACITY
				: (totalSize(strs) + CollectionUtils.INITIAL_CAPACITY));
		for (CharSequence str : strs) {
			append(str);
		}
	}

	private void updateIndex(int index, int length) {
		ensureCapacity(MathUtils.max(this._currentIndex, index) + length);
		if (index < this._currentIndex) {
			System.arraycopy(this._values, index, this._values, index + length, this._currentIndex - index);
		} else if (index > this._currentIndex) {
			CollectionUtils.fill(this._values, this._currentIndex, index, ' ');
		}
		this._dirty = true;
	}

	private void ensureCapacity(int minimumCapacity) {
		if (minimumCapacity > _values.length) {
			expandCapacity(minimumCapacity);
		}
	}

	private void expandCapacity(int minimumCapacity) {
		int newCapacity = _values.length * 2 + 1;
		if (newCapacity < minimumCapacity) {
			newCapacity = minimumCapacity;
		}
		if (newCapacity < 0) {
			throw new LSysException("Capacity is too long and max Integer !");
		}
		this._values = CollectionUtils.copyOf(_values, newCapacity);
		this._dirty = true;
	}

	private static int totalSize(CharSequence... strs) {
		int totalLen = 0;
		for (CharSequence str : strs) {
			totalLen += (null == str ? 4 : str.length());
		}
		return totalLen;
	}

	public StrBuilder setLength(int newLength) {
		if (newLength < 0) {
			throw new LSysException("newLength : " + newLength + " < 0 ");
		}
		ensureCapacity(newLength);
		if (this._currentIndex < newLength) {
			CollectionUtils.fill(this._values, this._currentIndex, newLength, '\0');
		}
		this._currentIndex = newLength;
		this._dirty = true;
		return this;
	}

	/**
	 * 顺序注入一个对象到字符序列中去
	 * 
	 * @param o
	 * @return
	 */
	public StrBuilder append(Object o) {
		return insert(this._currentIndex, o);
	}

	@Override
	public StrBuilder append(char c) {
		return insert(this._currentIndex, c);
	}

	public StrBuilder append(char[] src) {
		if (CollectionUtils.isEmpty(src)) {
			return this;
		}
		return append(src, 0, src.length);
	}

	public StrBuilder append(char[] src, int srcPos, int length) {
		return insert(this._currentIndex, src, srcPos, length);
	}

	@Override
	public StrBuilder append(CharSequence cs) {
		return insert(this._currentIndex, cs);
	}

	@Override
	public StrBuilder append(CharSequence cs, int start, int end) {
		return insert(this._currentIndex, cs, start, end);
	}

	public StrBuilder insert(int index, Object o) {
		if (o instanceof CharSequence) {
			return insert(index, (CharSequence) o);
		}
		return insert(index, HelperUtils.toStr(o));
	}

	public StrBuilder insert(int index, char c) {
		updateIndex(index, 1);
		this._values[index] = c;
		this._currentIndex = MathUtils.max(this._currentIndex, index) + 1;
		this._dirty = true;
		return this;
	}

	public StrBuilder insert(int index, char[] src) {
		if (CollectionUtils.isEmpty(src)) {
			return this;
		}
		return insert(index, src, 0, src.length);
	}

	public StrBuilder insert(int index, char[] src, int srcPos, int length) {
		if (CollectionUtils.isEmpty(src) || srcPos > src.length || length <= 0) {
			return this;
		}
		if (index < 0) {
			index = 0;
		}
		if (srcPos < 0) {
			srcPos = 0;
		} else if (srcPos + length > src.length) {
			length = src.length - srcPos;
		}
		updateIndex(index, length);
		System.arraycopy(src, srcPos, _values, index, length);
		this._currentIndex = MathUtils.max(this._currentIndex, index) + length;
		this._dirty = true;
		return this;
	}

	public StrBuilder insert(int index, CharSequence cs) {
		if (cs == null) {
			cs = LSystem.NULL;
		}
		int len = cs.length();
		updateIndex(index, cs.length());
		if (cs instanceof String) {
			((String) cs).getChars(0, len, this._values, index);
		} else if (cs instanceof StringBuilder) {
			((StringBuilder) cs).getChars(0, len, this._values, index);
		} else if (cs instanceof StringBuffer) {
			((StringBuffer) cs).getChars(0, len, this._values, index);
		} else if (cs instanceof StrBuilder) {
			((StrBuilder) cs).getChars(0, len, this._values, index);
		} else {
			for (int i = 0, j = this._currentIndex; i < len; i++, j++) {
				this._values[j] = cs.charAt(i);
			}
		}
		this._currentIndex = MathUtils.max(this._currentIndex, index) + len;
		this._dirty = true;
		return this;
	}

	public StrBuilder insert(int index, CharSequence cs, int start, int end) {
		if (cs == null) {
			cs = LSystem.NULL;
		}
		final int charLength = cs.length();
		if (start > charLength) {
			return this;
		}
		if (start < 0) {
			start = 0;
		}
		if (end > charLength) {
			end = charLength;
		}
		if (start >= end) {
			return this;
		}
		if (index < 0) {
			index = 0;
		}
		final int length = end - start;
		updateIndex(index, length);
		for (int i = start, j = this._currentIndex; i < end; i++, j++) {
			_values[j] = cs.charAt(i);
		}
		this._currentIndex = MathUtils.max(this._currentIndex, index) + length;
		this._dirty = true;
		return this;
	}

	public StrBuilder getChars(int begin, int end, char[] dst, int dstBegin) {
		if (begin < 0) {
			begin = 0;
		}
		if (end <= 0) {
			end = 0;
		} else if (end >= this._currentIndex) {
			end = this._currentIndex;
		}
		if (begin > end) {
			throw new LSysException("begin > end");
		}
		System.arraycopy(_values, begin, dst, dstBegin, end - begin);
		return this;
	}

	public boolean hasContent() {
		return _currentIndex > 0;
	}

	public boolean hasNext() {
		if (_values == null) {
			return false;
		}
		return _currentIndex < _values.length;
	}

	public boolean isEmpty() {
		return _currentIndex == 0 || _values == null || _values.length == 0;
	}

	public StrBuilder clear() {
		return reset();
	}

	public StrBuilder reset() {
		return reset(CollectionUtils.INITIAL_CAPACITY);
	}

	public StrBuilder reset(int cap) {
		this._currentIndex = 0;
		this._hash = 0;
		this._values = new char[cap];
		this._dirty = true;
		this._tempResult = null;
		return this;
	}

	/**
	 * 删除从指定位置开始到结束的字符串
	 * 
	 * @param newPosition
	 * @return
	 */
	public StrBuilder deleteTo(int newPosition) {
		if (newPosition < 0) {
			newPosition = 0;
		}
		return delete(newPosition, this._currentIndex);
	}

	/**
	 * 删除从字符串顶部开始到指定位置的字符串
	 * 
	 * @param index
	 * @return
	 */
	public StrBuilder deleteCharAt(int index) {
		if (this._values == null) {
			return this;
		}
		if ((index < 0) || (index >= this._values.length)) {
			throw new LSysException("index :" + index + " out of range !");
		}
		System.arraycopy(this._values, index + 1, this._values, index, this._values.length - index - 1);
		this._currentIndex--;
		this._dirty = true;
		return this;
	}

	public StrBuilder delete(int start, int end) {
		if (this._values == null) {
			return this;
		}
		if (start < 0) {
			start = 0;
		}
		if (end >= this._currentIndex) {
			this._currentIndex = start;
			this._dirty = true;
			return this;
		} else if (end < 0) {
			end = 0;
		}
		int len = end - start;
		if (len > 0) {
			System.arraycopy(_values, start + len, _values, start, this._currentIndex - end);
			this._currentIndex -= len;
			this._dirty = true;
		} else if (len < 0) {
			throw new LSysException("index out of range: " + this._currentIndex);
		}
		return this;
	}

	@Override
	public int length() {
		return this._currentIndex;
	}

	@Override
	public char charAt(int index) {
		if ((index < 0) || (index > this._currentIndex)) {
			throw new LSysException("index :" + index + " out of range !");
		}
		return this._values[index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return substring(start, end);
	}

	public String substring(int start) {
		return substring(start, this._currentIndex);
	}

	public String substring(int start, int end) {
		return new String(this._values, start, end - start);
	}

	public int size() {
		return this._currentIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof CharSequence) {
			return equals((CharSequence) o);
		}
		return super.equals(o);
	}

	public boolean equals(final CharSequence cs) {
		if (cs == null) {
			return false;
		}
		if (cs.length() == 0 && this._currentIndex == 0) {
			return true;
		}
		final CharSequence another = cs;
		int len = _currentIndex;
		if (len == another.length()) {
			char[] selfChars = _values;
			int i = 0;
			while (len-- != 0) {
				if (selfChars[i] != another.charAt(i)) {
					return false;
				}
				i++;
			}
			return true;
		}
		return false;
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public int getHashCode() {
		if (!_dirty) {
			return _hash;
		}
		int h = _hash;
		if (h == 0 && _values.length > 0) {
			char val[] = _values;
			for (int i = 0; i < _values.length; i++) {
				h = 31 * h + val[i];
			}
			_hash = h;
		}
		return h;
	}

	public String toString(boolean ret) {
		if (!ret && !_dirty && _tempResult != null) {
			return this._tempResult;
		}
		if (_currentIndex > 0) {
			final String result = new String(this._values, 0, this._currentIndex);
			if (ret) {
				reset();
			}
			this._dirty = false;
			return (this._tempResult = result);
		}
		return LSystem.EMPTY;
	}

	@Override
	public String toString() {
		return toString(false);
	}

}
