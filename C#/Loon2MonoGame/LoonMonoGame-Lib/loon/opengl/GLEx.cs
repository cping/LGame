using loon.canvas;
using loon.font;

namespace loon.opengl
{
   public class GLEx : BlendMethod
{
		private class BrushSave
		{
			internal uint baseColor = LColor.DEF_COLOR;
			internal uint fillColor = LColor.DEF_COLOR;
			//internal int pixSkip = def_skip;
			internal float lineWidth = 1f;
			internal float baseAlpha = 1f;
			internal int blend = BlendMethod.MODE_NORMAL;

			internal bool alltextures = false;
			internal IFont font = null;
			internal LTexture patternTex = null;

			internal virtual BrushSave Cpy()
			{
				BrushSave save = new BrushSave();
				save.baseColor = this.baseColor;
				save.fillColor = this.fillColor;
				//save.pixSkip = this.pixSkip;
				save.lineWidth = this.lineWidth;
				save.alltextures = this.alltextures;
				save.font = this.font;
				save.patternTex = this.patternTex;
				save.blend = this.blend;
				return save;
			}
		}

		public enum Direction
		{
			TRANS_NONE,
			TRANS_MIRROR,
			TRANS_FLIP,
			TRANS_MF
		}

		public GLEx(Graphics gfx, RenderTarget target, BaseBatch def, bool alltex, bool saveFrameBuffer) 
		{
			/*this.gfx_Conflict = gfx;
			this.target = target;
			this.batch_Conflict = def;
			this.affineStack.add(lastTrans = new Affine2f());
			this.colorTex = gfx.finalColorTex();
			this.scale(scaleX = target.xscale(), scaleY = target.yscale());
			this.lastBrush = new BrushSave();
			this.lastBrush.font = LSystem.SystemGameFont;
			this.lastBrush.alltextures = alltex;
			this.lastBrush.pixSkip = LSystem.HTML5 ? def_skip_html5 : def_skip;
			this.lastBrush.blend = BlendMethod.MODE_NORMAL;
			this.brushStack.add(lastBrush);
			this.saveToFrameBufferTexture = saveFrameBuffer;
			this.update();*/
		}
		public GLEx(Graphics gfx, RenderTarget target, GL20 gl, bool alltex, bool saveFrameBuffer) : this(gfx, target, CreateDefaultBatch(gl), alltex, saveFrameBuffer)
		{
		}

		public GLEx(Graphics gfx, RenderTarget target, GL20 gl) : this(gfx, target, CreateDefaultBatch(gl), false, false)
		{
		}

		public static BaseBatch CreateDefaultBatch(GL20 gl)
		{
			return null;
		}

		public virtual void Update()
        {

        }
	}
}
