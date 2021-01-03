namespace loon.utils.parse
{
    public class ParserReader
	{
		private char[] _context = null;
		private int _currentIndex = 0;
		private int _currentChar = 0;
		private readonly StrBuilder _tempBuffer;
		private int _tempIndex = 0;
		private int _level = 0;
		private int _eofIndex = 0;
		private readonly ObjectMap<int, int> _markMap;

		public ParserReader(string context)
		{
			if (!string.ReferenceEquals(context, null))
			{
				this._context = context.ToCharArray();
			}
			else
			{
				this._context = new char[0];
			}
			this._tempBuffer = new StrBuilder();
			this._currentIndex = 0;
			this._tempIndex = 0;
			this._level = 0;
			this._eofIndex = -1;
			this._markMap = new ObjectMap<int, int>();
		}

		public virtual string GetString()
		{
				int begin = _markMap.Get(_level - 1);
				int end = _tempIndex;
				if (begin > end)
				{
					throw new LSysException(StringUtils.Format("begin:{0} > end:{1}", begin, end));
				}
				else
				{
					return _tempBuffer.Substring(begin, end - begin);
				}
			
		}

		public virtual ParserReader PositionChar(int idx)
		{
			this._currentIndex = idx;
			return this;
		}

		public virtual int NextChar()
		{
			if (_currentIndex < 0)
			{
				this._currentIndex = 0;
			}
			int len = this._context.Length - 1;
			if (_currentIndex >= len)
			{
				return -1;
			}
			return _context[_currentIndex++];
		}

		public virtual int Read()
		{
			if (_tempIndex == _eofIndex)
			{
				_tempIndex++;
				return -1;
			}
			if (_tempIndex < _tempBuffer.Length())
			{
				_currentChar = (int)_tempBuffer.CharAt(_tempIndex);
			}
			else
			{
				if (_eofIndex != -1)
				{
					return -1;
				}
				_currentChar = NextChar();
				if (_currentChar == -1)
				{
					_eofIndex = _tempIndex;
				}
				_tempBuffer.Append((char)_currentChar);
			}
			_tempIndex++;
			return _currentChar;
		}

		public virtual int CurrentChar()
		{
			Read();
			Unread();
			return _currentChar;
		}

		public virtual int Previous()
		{
			return _tempIndex <= 1 || _tempIndex >= _tempBuffer.Length() ? -1 : _tempBuffer.CharAt(_tempIndex - 2);
		}

		public virtual ParserReader Mark()
		{
			_markMap.Put(_level, _tempIndex);
			_level++;
			return this;
		}

		public virtual ParserReader Unmark()
		{
			_level--;
			if (_level < 0)
			{
				throw new LSysException("no more mark() is to unmark()");
			}
			return this;
		}

		public virtual ParserReader Reset()
		{
			Unmark();
			this._tempIndex = _markMap.Get(_level);
			return this;
		}

		public virtual ParserReader Unread()
		{
			_tempIndex--;
			if (_tempIndex < 0)
			{
				_tempIndex = 0;
			}
			return this;
		}

		public override string ToString()
		{
			return GetString();
		}
	}
}
