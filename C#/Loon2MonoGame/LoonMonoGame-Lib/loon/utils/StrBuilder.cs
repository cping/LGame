using java.lang;

namespace loon.utils
{
    public class StrBuilder : CharSequence
    {
        private string _tempResult = null;

        private bool _dirty = false;

        private char[] _values = null;

        private int _currentIndex = 0;

        private int _hash = 0;

        public static StrBuilder At()
        {
            return At(CollectionUtils.INITIAL_CAPACITY);
        }

        public static StrBuilder At(int cap)
        {
            return new StrBuilder(cap);
        }

        public static StrBuilder At(params CharSequence[] strs)
        {
            return new StrBuilder(strs);
        }

        public StrBuilder() : this(CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public StrBuilder(int cap)
        {
            this.Reset(cap);
        }
        public StrBuilder(params string[] strs) : this(StringUtils.IsEmpty(strs) ? CollectionUtils.INITIAL_CAPACITY
                : (TotalSize(strs) + CollectionUtils.INITIAL_CAPACITY))
        {
            foreach (string str in strs)
            {
                Append(str);
            }
        }

        public StrBuilder(params CharSequence[] strs) : this(StringUtils.IsEmpty(strs) ? CollectionUtils.INITIAL_CAPACITY
                    : (TotalSize(strs) + CollectionUtils.INITIAL_CAPACITY))
        {
            foreach (CharSequence str in strs)
            {
                Append(str);
            }
        }

        private void UpdateIndex(int index, int length)
        {
            EnsureCapacity(MathUtils.Max(this._currentIndex, index) + length);
            if (index < this._currentIndex)
            {
                JavaSystem.Arraycopy(this._values, index, this._values, index + length, this._currentIndex - index);
            }
            else if (index > this._currentIndex)
            {
                CollectionUtils.Fill(this._values, this._currentIndex, index, ' ');
            }
            this._dirty = true;
        }

        private void EnsureCapacity(int minimumCapacity)
        {
            if (minimumCapacity > _values.Length)
            {
                ExpandCapacity(minimumCapacity);
            }
        }

        private void ExpandCapacity(int minimumCapacity)
        {
            int newCapacity = _values.Length * 2 + 1;
            if (newCapacity < minimumCapacity)
            {
                newCapacity = minimumCapacity;
            }
            if (newCapacity < 0)
            {
                throw new LSysException("Capacity is too long and max Integer !");
            }
            this._values = CollectionUtils.CopyOf(_values, newCapacity);
            this._dirty = true;
        }

        private static int TotalSize(params CharSequence[] strs)
        {
            int totalLen = 0;
            foreach (CharSequence str in strs)
            {
                totalLen += (null == str ? 4 : str.Length());
            }
            return totalLen;
        }
        private static int TotalSize(params string[] strs)
        {
            int totalLen = 0;
            foreach (string str in strs)
            {
                totalLen += (null == str ? 4 : str.Length());
            }
            return totalLen;
        }

        public StrBuilder SetLength(int newLength)
        {
            if (newLength < 0)
            {
                throw new LSysException("newLength : " + newLength + " < 0 ");
            }
            EnsureCapacity(newLength);
            if (this._currentIndex < newLength)
            {
                CollectionUtils.Fill(this._values, this._currentIndex, newLength, '\0');
            }
            this._currentIndex = newLength;
            this._dirty = true;
            return this;
        }

        public StrBuilder Append(object o)
        {
            return Insert(this._currentIndex, o);
        }
        public StrBuilder Append(string c)
        {
            return Insert(this._currentIndex, c);
        }

        public StrBuilder Append(char c)
        {
            return Insert(this._currentIndex, c);
        }

        public StrBuilder Append(char[] src)
        {
            if (CollectionUtils.IsEmpty(src))
            {
                return this;
            }
            return Append(src, 0, src.Length);
        }

        public StrBuilder Append(char[] src, int srcPos, int length)
        {
            return Insert(this._currentIndex, src, srcPos, length);
        }


        public StrBuilder Append(CharSequence cs)
        {
            return Insert(this._currentIndex, cs);
        }

        public StrBuilder Append(CharSequence cs, int start, int end)
        {
            return Insert(this._currentIndex, cs, start, end);
        }

        public StrBuilder Insert(int index, object o)
        {
            if (o is CharSequence sequence)
            {
                return Insert(index, sequence);
            }
            if (o is string str)
            {
                return Insert(index, str);
            }
            return Insert(index, HelperUtils.ToStr(o));
        }

        public StrBuilder Insert(int index, char c)
        {
            UpdateIndex(index, 1);
            this._values[index] = c;
            this._currentIndex = MathUtils.Max(this._currentIndex, index) + 1;
            this._dirty = true;
            return this;
        }

        public StrBuilder Insert(int index, char[] src)
        {
            if (CollectionUtils.IsEmpty(src))
            {
                return this;
            }
            return Insert(index, src, 0, src.Length);
        }

        public StrBuilder Insert(int index, char[] src, int srcPos, int length)
        {
            if (CollectionUtils.IsEmpty(src) || srcPos > src.Length || length <= 0)
            {
                return this;
            }
            if (index < 0)
            {
                index = 0;
            }
            if (srcPos < 0)
            {
                srcPos = 0;
            }
            else if (srcPos + length > src.Length)
            {
                length = src.Length - srcPos;
            }
            UpdateIndex(index, length);
            JavaSystem.Arraycopy(src, srcPos, _values, index, length);
            this._currentIndex = MathUtils.Max(this._currentIndex, index) + length;
            this._dirty = true;
            return this;
        }

        public StrBuilder Insert(int index, CharSequence cs)
        {
            if (cs == null)
            {
                cs = LSystem.NULL.ToJavaString();
            }
            int len = cs.Length();
            UpdateIndex(index, cs.Length());
            if (cs is JavaString strs)
            {
                strs.GetChars(0, len, this._values, index);
            }
            else if (cs is StringBuilder sbr)
            {
                sbr.GetChars(0, len, this._values, index);
            }
            else if (cs is StringBuffer sb)
            {
                sb.GetChars(0, len, this._values, index);
            }
            else if (cs is StrBuilder sbrSelf)
            {
                sbrSelf.GetChars(0, len, this._values, index);
            }
            else
            {
                for (int i = 0, j = this._currentIndex; i < len; i++, j++)
                {
                    this._values[j] = cs.CharAt(i);
                }
            }
            this._currentIndex = MathUtils.Max(this._currentIndex, index) + len;
            this._dirty = true;
            return this;
        }

        public StrBuilder Insert(int index, string cs)
        {
            if (cs == null)
            {
                cs = LSystem.NULL;
            }
            int len = cs.Length();
            UpdateIndex(index, cs.Length());
            for (int i = 0, j = this._currentIndex; i < len; i++, j++)
            {
                this._values[j] = cs.CharAt(i);
            }
            this._currentIndex = MathUtils.Max(this._currentIndex, index) + len;
            this._dirty = true;
            return this;
        }

        public StrBuilder Insert(int index, string cs, int start, int end)
        {
            if (cs == null)
            {
                cs = LSystem.NULL;
            }
            int charLength = cs.Length();
            if (start > charLength)
            {
                return this;
            }
            if (start < 0)
            {
                start = 0;
            }
            if (end > charLength)
            {
                end = charLength;
            }
            if (start >= end)
            {
                return this;
            }
            if (index < 0)
            {
                index = 0;
            }
            int length = end - start;
            UpdateIndex(index, length);
            for (int i = start, j = this._currentIndex; i < end; i++, j++)
            {
                _values[j] = cs.CharAt(i);
            }
            this._currentIndex = MathUtils.Max(this._currentIndex, index) + length;
            this._dirty = true;
            return this;
        }

        public StrBuilder Insert(int index, CharSequence cs, int start, int end)
        {
            if (cs == null)
            {
                cs = LSystem.NULL.ToJavaString();
            }
            int charLength = cs.Length();
            if (start > charLength)
            {
                return this;
            }
            if (start < 0)
            {
                start = 0;
            }
            if (end > charLength)
            {
                end = charLength;
            }
            if (start >= end)
            {
                return this;
            }
            if (index < 0)
            {
                index = 0;
            }
            int length = end - start;
            UpdateIndex(index, length);
            for (int i = start, j = this._currentIndex; i < end; i++, j++)
            {
                _values[j] = cs.CharAt(i);
            }
            this._currentIndex = MathUtils.Max(this._currentIndex, index) + length;
            this._dirty = true;
            return this;
        }

        public StrBuilder GetChars(int begin, int end, char[] dst, int dstBegin)
        {
            if (begin < 0)
            {
                begin = 0;
            }
            if (end <= 0)
            {
                end = 0;
            }
            else if (end >= this._currentIndex)
            {
                end = this._currentIndex;
            }
            if (begin > end)
            {
                throw new LSysException("begin > end");
            }
            JavaSystem.Arraycopy(_values, begin, dst, dstBegin, end - begin);
            return this;
        }

        public bool HasContent()
        {
            return _currentIndex > 0;
        }

        public bool HasNext()
        {
            if (_values == null)
            {
                return false;
            }
            return _currentIndex < _values.Length;
        }

        public bool IsEmpty()
        {
            return _currentIndex == 0 || _values == null || _values.Length == 0;
        }

        public StrBuilder Clear()
        {
            return Reset();
        }

        public StrBuilder Reset()
        {
            return Reset(CollectionUtils.INITIAL_CAPACITY);
        }

        public StrBuilder Reset(int cap)
        {
            this._currentIndex = 0;
            this._hash = 0;
            this._values = new char[cap];
            this._dirty = true;
            this._tempResult = null;
            return this;
        }

        public StrBuilder DeleteTo(int newPosition)
        {
            if (newPosition < 0)
            {
                newPosition = 0;
            }
            return Delete(newPosition, this._currentIndex);
        }

        public StrBuilder DeleteCharAt(int index)
        {
            if (this._values == null)
            {
                return this;
            }
            if ((index < 0) || (index >= this._values.Length))
            {
                throw new LSysException("index :" + index + " out of range !");
            }
            JavaSystem.Arraycopy(this._values, index + 1, this._values, index, this._values.Length - index - 1);
            this._currentIndex--;
            this._dirty = true;
            return this;
        }

        public StrBuilder Delete(int start, int end)
        {
            if (this._values == null)
            {
                return this;
            }
            if (start < 0)
            {
                start = 0;
            }
            if (end >= this._currentIndex)
            {
                this._currentIndex = start;
                this._dirty = true;
                return this;
            }
            else if (end < 0)
            {
                end = 0;
            }
            int len = end - start;
            if (len > 0)
            {
                JavaSystem.Arraycopy(_values, start + len, _values, start, this._currentIndex - end);
                this._currentIndex -= len;
                this._dirty = true;
            }
            else if (len < 0)
            {
                throw new LSysException("index out of range: " + this._currentIndex);
            }
            return this;
        }


        public int Length()
        {
            return this._currentIndex;
        }

        public char CharAt(int index)
        {
            if ((index < 0) || (index > this._currentIndex))
            {
                throw new LSysException("index :" + index + " out of range !");
            }
            return this._values[index];
        }


        public CharSequence SubSequence(int start, int end)
        {
            return new JavaString(Substring(start, end));
        }

        public string Substring(int start)
        {
            return Substring(start, this._currentIndex);
        }

        public string Substring(int start, int end)
        {
            return new string(this._values, start, end - start);
        }

        public int Size()
        {
            return this._currentIndex;
        }

        public StrBuilder Reverse()
        {
            bool hasSurrogates = false;
            int n = _currentIndex - 1;
            for (int j = (n - 1) >> 1; j >= 0; j--)
            {
                int k = n - j;
                char cj = _values[j];
                char ck = _values[k];
                _values[j] = ck;
                _values[k] = cj;
                if (CharUtils.IsSurrogate(cj) ||
                        CharUtils.IsSurrogate(ck))
                {
                    hasSurrogates = true;
                }
            }
            if (hasSurrogates)
            {
                ReverseAllValidSurrogatePairs();
            }
            return this;
        }

        private void ReverseAllValidSurrogatePairs()
        {
            for (int i = 0; i < _currentIndex - 1; i++)
            {
                char c2 = _values[i];
                if (CharUtils.IsLowSurrogate(c2))
                {
                    char c1 = _values[i + 1];
                    if (CharUtils.IsHighSurrogate(c1))
                    {
                        _values[i++] = c1;
                        _values[i] = c2;
                    }
                }
            }
        }

        public override bool Equals(object o)
        {
            if (o == null)
            {
                return false;
            }
            if (o == this)
            {
                return true;
            }
            if (o is CharSequence)
            {
                return Equals((CharSequence)o);
            }
            return base.Equals(o);
        }

        public bool Equals(CharSequence cs)
        {
            if (cs == null)
            {
                return false;
            }
            if (cs.Length() == 0 && this._currentIndex == 0)
            {
                return true;
            }
            CharSequence another = cs;
            int len = _currentIndex;
            if (len == another.Length())
            {
                char[] selfChars = _values;
                int i = 0;
                while (len-- != 0)
                {
                    if (selfChars[i] != another.CharAt(i))
                    {
                        return false;
                    }
                    i++;
                }
                return true;
            }
            return false;
        }

        public bool IsDirty()
        {
            return this._dirty;
        }

        public override int GetHashCode()
        {
            if (!_dirty)
            {
                return _hash;
            }
            int h = _hash;
            if (h == 0 && _values.Length > 0)
            {
                char[] val = _values;
                for (int i = 0; i < _values.Length; i++)
                {
                    h = 31 * h + val[i];
                }
                _hash = h;
            }
            return h;
        }

        public string ToString(bool ret)
        {
            if (!ret && !_dirty && _tempResult != null)
            {
                return this._tempResult;
            }
            if (_currentIndex > 0)
            {
                string result = new string(this._values, 0, this._currentIndex);
                if (ret)
                {
                    Reset();
                }
                this._dirty = false;
                return (this._tempResult = result);
            }
            return LSystem.EMPTY;
        }

        public override string ToString()
        {
            return ToString(false);
        }

    }
}
