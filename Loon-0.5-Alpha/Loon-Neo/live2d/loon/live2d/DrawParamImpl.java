package loon.live2d;

import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.canvas.LColor;
import loon.live2d.framework.L2DModelMatrix;
import loon.live2d.graphics.*;
import loon.opengl.BlendState;
import loon.opengl.GL20;
import loon.opengl.GLEx;
import loon.utils.ListMap;

public class DrawParamImpl extends DrawParam {

	private LColor color = new LColor(LColor.white);

	private ListMap<Integer, LTexture> textures = new ListMap<Integer, LTexture>(
			32);

	public DrawParamImpl() {

	}

	@Override
	public void drawTexture(final L2DModelMatrix matrix, final GLEx gl,
			final int textureNo, final int indexCount,
			final short[] indexArray, final float[] vertexArray,
			final float[] uvArray, final float opacity,
			final int colorCompositionType) {
		if (opacity < 0.01) {
			return;
		}
		LTexture texture = textures.get(textureNo);
		if (texture != null) {
			int sfactor = 0;
			int dfactor = 0;
			switch (colorCompositionType) {
			case 0:
			default:
				if (this._updateAlpha) {
					sfactor = 1;
				} else {
					sfactor = 770;
				}
				dfactor = 771;
				break;
			case 1:
				sfactor = 770;
				dfactor = 1;
				break;
			case 2:
				sfactor = 0;
				dfactor = 768;
			}
			if (list.size > textureNo) {
				float r = this.list.get(textureNo).r;
				float g = this.list.get(textureNo).g;
				float b = this.list.get(textureNo).b;
				float a = this.list.get(textureNo).a;
				if (this._updateAlpha) {
					color.setColor(this.red * opacity * r, this.green * opacity
							* g, this.blue * opacity * b, this.alpha * opacity
							* a);
				} else {
					color.setColor(this.red * r, this.green * g, this.blue * b,
							this.alpha * a * opacity);
				}
			}
			LSystem.base().graphics().gl.glBlendFunc(sfactor, dfactor);
			LTextureBatch batch = texture.getTextureBatch();
			batch.setBlendState(BlendState.Null);
			batch.begin();
			if (this._scale.x == 1f && this._scale.y == 1f) {
				batch.setBatchMatrix(matrix.getArray());
			}
			batch.setGLType(GL20.GL_TRIANGLES);
			batch.draw(indexArray, vertexArray, uvArray, _location.x,
					_location.y, _scale.x, _scale.y, color);
			batch.end();
		}
	}

	public void loadTexture(final int no, LTexture texture) {
		textures.put(no, texture);
	}

	public void deleteTextures() {
		for (int i = 0; i < textures.size; i++) {
			LTexture tex = textures.getValueAt(i);
			if (tex != null) {
				tex.close();
			}
		}
		textures.clear();
	}

}
