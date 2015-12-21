package loon.opengl;

import loon.particle.ParticleBuffer;

public class ParticleBatch extends TrilateralBatch {

	protected float _sx, _sy, _tx, _ty;
	
	public ParticleBatch(GL20 gl) {
		super(gl);
	}

	public ParticleBatch load(Painter p, int maxQuads) {
		setTexture(p.texture());
		_sx = p.sx();
		_sy = p.sy();
		_tx = p.tx();
		_ty = p.ty();
		beginPrimitive(maxQuads * 4, maxQuads * 6);
		return this;
	}

	public void addParticle(float l, float t, float r, float b, float[] data,
			int ppos) {
		int vertIdx = beginPrimitive(4, 6), pstart = ppos + ParticleBuffer.M00;
		float[] verts = vertices;
		int offset = vertPos;
		float sx = _sx, sy = _sy, tx = _tx, ty = _ty;
		offset = add(verts, add(verts, offset, data, pstart, 8), l, t, sx, sy);
		offset = add(verts, add(verts, offset, data, pstart, 8), r, t, tx, sy);
		offset = add(verts, add(verts, offset, data, pstart, 8), l, b, sx, ty);
		offset = add(verts, add(verts, offset, data, pstart, 8), r, b, tx, ty);
		vertPos = offset;
		addElems(vertIdx, QUAD_INDICES, 0, QUAD_INDICES.length, 0);
	}

}
