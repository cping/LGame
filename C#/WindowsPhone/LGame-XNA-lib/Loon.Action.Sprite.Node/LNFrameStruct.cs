using Loon.Core.Geom;
using Loon.Core.Graphics.Opengl;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {

	public class LNFrameStruct {
	
		public Vector2f _anchor;
	
		public Vector2f _place;
	
		public int _orig_width;
	
		public int _orig_height;
	
		public int _size_width;
	
		public int _size_height;
	
		public Vector2f _textCoords;
	
		public LTexture _texture;
	
		public BlendState _state;
	
		public static LNFrameStruct InitWithImage(DefImage img) {
			LNFrameStruct struct2 = new LNFrameStruct();
			if (img.maskColor == null) {
				struct2._texture = LTextures.LoadTexture(img.fileName,
						Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
			} else {
				struct2._texture = TextureUtils.FilterColor(img.fileName,
						img.maskColor, Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
			}
			struct2._state = img.blend;
			struct2._textCoords = img.pos;
			struct2._orig_width = img.orig.X();
			struct2._orig_height = img.orig.Y();
			struct2._size_width = img.size.X();
			struct2._size_height = img.size.Y();
			struct2._anchor = img.anchor;
			struct2._place = img.place;
			return struct2;
		}
	
		public static LNFrameStruct InitWithImageName(string imgName) {
			DefImage image = LNDataCache.ImageByKey(imgName);
			LNFrameStruct struct2 = new LNFrameStruct();
			if (image.maskColor == null) {
				struct2._texture = LTextures.LoadTexture(image.fileName,
						Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
			} else {
				struct2._texture = TextureUtils.FilterColor(image.fileName,
						image.maskColor, Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
			}
			struct2._state = image.blend;
			struct2._textCoords = image.pos;
			struct2._orig_width = image.orig.X();
			struct2._orig_height = image.orig.Y();
			struct2._size_width = image.size.X();
			struct2._size_height = image.size.Y();
			struct2._anchor = image.anchor;
			struct2._place = image.place;
			return struct2;
		}
	}
}
