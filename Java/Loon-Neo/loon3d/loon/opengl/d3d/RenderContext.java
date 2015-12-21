package loon.opengl.d3d;

import loon.LSystem;
import loon.opengl.GL20;

public class RenderContext {

	public final TextureBinder textureBinder;
	private boolean blending;
	private int blendSFactor;
	private int blendDFactor;
	private boolean depthTest;
	private int depthFunc;
	private int cullFace;
	
	public RenderContext(TextureBinder textures) {
		this.textureBinder = textures;
	}
	
	public final void begin() {
		GL20 gl =  LSystem.base().graphics().gl;
		gl.glDisable(GL20.GL_DEPTH_TEST);
		depthTest = false;
		gl.glDisable(GL20.GL_BLEND);
		blending = false;
		gl.glDisable(GL20.GL_CULL_FACE);
		cullFace = blendSFactor = blendDFactor = depthFunc = 0;
		textureBinder.begin();
	}
	
	public final void end() {
		GL20 gl =  LSystem.base().graphics().gl;
		if(depthTest) gl.glDisable(GL20.GL_DEPTH_TEST);
		if(blending) gl.glDisable(GL20.GL_BLEND);
		if(cullFace>0) gl.glDisable(GL20.GL_CULL_FACE);
		textureBinder.end();
	}
	
	public final void setDepthTest(final boolean enabled, final int depthFunction) {
		GL20 gl =  LSystem.base().graphics().gl;
		if (enabled != depthTest) {
			depthTest = enabled;
			if (enabled)
				gl.glEnable(GL20.GL_DEPTH_TEST);
			else
				gl.glDisable(GL20.GL_DEPTH_TEST);
		}
		if (enabled && depthFunc != depthFunction) {
			gl.glDepthFunc(depthFunction);
			depthFunc = depthFunction;
		}
	}
	
	public final void setBlending(final boolean enabled, final int sFactor, final int dFactor) {
		GL20 gl =  LSystem.base().graphics().gl;
		if (enabled != blending) {
			blending = enabled;
			if (enabled)
				gl.glEnable(GL20.GL_BLEND);
			else
				gl.glDisable(GL20.GL_BLEND);
		}
		if (enabled && (blendSFactor != sFactor || blendDFactor != dFactor)) {
			gl.glBlendFunc(sFactor, dFactor);
			blendSFactor = sFactor;
			blendDFactor = dFactor;
		}
	}
	
	public final void setCullFace(final int face) {
		GL20 gl =  LSystem.base().graphics().gl;
		if (face != cullFace) {
			cullFace = face;
			if ((face == GL20.GL_FRONT) || (face == GL20.GL_BACK) || (face == GL20.GL_FRONT_AND_BACK)) {
				gl.glEnable(GL20.GL_CULL_FACE);
				gl.glCullFace(face);
			}
			else
				gl.glDisable(GL20.GL_CULL_FACE);
		}
	}
}
