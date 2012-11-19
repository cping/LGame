package loon.physics;

import java.util.ArrayList;
import java.util.List;

import loon.core.geom.Vector2f;


import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * jbox
 */
public class PolygonDef extends FixtureDef {

	public List<Vector2f> vertices;

	public void set(final PolygonDef copyMe) {
		this.density = copyMe.density;
		this.friction = copyMe.friction;
		this.isSensor = copyMe.isSensor;
		this.restitution = copyMe.restitution;
		this.filter.categoryBits = copyMe.filter.categoryBits;
		this.filter.groupIndex = copyMe.filter.groupIndex;
		this.filter.maskBits = copyMe.filter.maskBits;
		this.friction = copyMe.friction;
		this.vertices = new ArrayList<Vector2f>();
		for (int i = 0; i < copyMe.vertices.size(); ++i) {
			this.addVertex((Vector2f)copyMe.vertices.get(i).cpy());
		}
	}

	public PolygonDef() {
		vertices = new ArrayList<Vector2f>();
	}

	public void addVertex(final Vector2f v) {
		vertices.add(v);
	}

	public void clearVertices() {
		vertices.clear();
	}

	public Vector2f[] getVertexArray() {
		return vertices.toArray(new Vector2f[0]);
	}

	public List<Vector2f> getVertexList() {
		return vertices;
	}

	public float[] getVertexs() {
		int vertice_size = vertices.size() * 2;
		float[] verts = new float[vertice_size];
		for (int i = 0, j = 0; i < vertice_size; i += 2, j++) {
			Vector2f v = vertices.get(j);
			verts[i] = v.x;
			verts[i + 1] = v.y;
		}
		return verts;
	}
	

	public void setAsBox(final float hx, final float hy) {
		vertices.clear();
		vertices.add(new Vector2f(-hx, -hy));
		vertices.add(new Vector2f(hx, -hy));
		vertices.add(new Vector2f(hx, hy));
		vertices.add(new Vector2f(-hx, hy));
	}

	public int getVertexCount() {
		return vertices.size();
	}

}
