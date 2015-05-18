namespace Loon{

	public class UnityContext{

		internal UnityGameContent _gameContent;
		
		public UnityGameContent GameRes
		{
			get{
				return _gameContent;
			}
		}

		internal int _width;
		
		internal  int _height;
		
		public UnityContext(int w, int h)
		{
			this._width = w;
			this._height = h;
		}
		
		public virtual int GetWidth()
		{
			return _width;
		}
		
		public virtual int GetHeight()
		{
			return _height;
		}
	
	}

}
