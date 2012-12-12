using Loon.Core.Graphics.Opengl;
using System;
namespace Loon.Action.Sprite.Node {
	
	public class LNAtlasNode : LNNode {
	
		protected internal int _itemHeight;
	
		protected internal int _itemsPerColumn;
	
		protected internal int _itemsPerRow;
	
		protected internal int _itemWidth;
	
		protected internal LNTextureAtlas _textureAtlas;
	
		public LNAtlasNode() {
			this._itemsPerRow = 0;
			this._itemsPerColumn = 0;
			this._itemWidth = 0;
			this._itemHeight = 0;
		}
	
		public LNAtlasNode(string fsName, int tileWidth, int tileHeight) {
			try {
				this._itemWidth = tileWidth;
				this._itemHeight = tileHeight;
				LTexture texture = LNDataCache.GetFrameStruct(fsName)._texture;
				this._itemsPerRow = texture.GetWidth() / tileWidth;
				this._itemsPerColumn = texture.GetHeight() / tileHeight;
				this._textureAtlas = new LNTextureAtlas(texture, this._itemsPerRow
						* this._itemsPerColumn);
			} catch (Exception) {
				throw new Exception(
						"LNAtlasNode Exception in the data load : " + fsName);
			}
		}
	}
}
