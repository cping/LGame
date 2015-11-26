package loon.action.collision.c2d;

import loon.action.collision.Collision2D;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.TArray;

public class Polygon2D {
	private Vector2f position;
	private Vector2f center;
	private TArray<Vector2f> vertices;
	private float rotation;

	private float minX;
	private float minY;
	private float maxX;
	private float maxY;

	private Rectangle2D bounds;

	public Polygon2D() {
		this.vertices = new TArray<Vector2f>();
		this.position = new Vector2f();
		this.center = new Vector2f();

		clearVertices();
	}

	public void clearVertices() {
		vertices.clear();

		minX = minY = Float.POSITIVE_INFINITY;
		maxX = maxY = Float.NEGATIVE_INFINITY;

		rotation = 0;
	}

	public void addVertex(float x, float y) {
		addVertex(new Vector2f(x, y));
	}

	public void addVertex(Vector2f v) {
		vertices.add(v);

		minX = MathUtils.min(v.x, minX);
		minY = MathUtils.min(v.y, minY);

		maxX = MathUtils.max(v.x, maxX);
		maxY = MathUtils.max(v.y, maxY);
	}

	public void scale(float s) {
		scale(s, s);
	}

	public void scale(float sx, float sy) {
		for (Vector2f v : vertices) {
			v.scaleSelf(sx, sy);
		}
	}

	public boolean intersects(Polygon2D other) {
		return Collision2D.checkPolygonCollision(this, other, null);
	}

	public boolean contains(Vector2f p) {
		int i, j = getVertices().size - 1;
		boolean oddNodes = false;

		Vector2f vi = Vector2f.TMP();
		Vector2f vj = Vector2f.TMP();

		for (i = 0; i < getVertices().size; j = i++) {
			vi.set(getVertex(i)).addSelf(position);
			vj.set(getVertex(j)).addSelf(position);

			if ((((vi.getY() <= p.getY()) && (p.getY() < vj.getY())) || ((vj
					.getY() <= p.getY()) && (p.getY() < vi.getY())))
					&& (p.getX() < (vj.getX() - vi.getX())
							* (p.getY() - vi.getY()) / (vj.getY() - vi.getY())
							+ vi.getX()))
				oddNodes = !oddNodes;
		}

		return oddNodes;
	}

	public TArray<Vector2f> getVertices() {
		return vertices;
	}

	public Vector2f getVertex(int index) {
		return vertices.get(index);
	}

	public Polygon2D copy() {
		Polygon2D p = new Polygon2D();
		vertices.addAll(p.vertices);
		return p;
	}

	public int vertexCount() {
		return vertices.size;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f v) {
		this.position.set(v);

		if (bounds != null){
			bounds.setPosition(position);
		}
	}

	public Vector2f getCenter() {
		if (vertexCount() == 0){
			center.set(position);
		}
		else{
			center.set(position).addSelf((maxX - minX) / 2, (maxY - minY) / 2);
		}

		return center;
	}

	public void setCenter(Vector2f center) {
		this.center.set(center);

		position.set(center);

		if (vertexCount() != 0){
			position.subtractSelf((maxX - minX) / 2, (maxY - minY) / 2);
		}
	}

	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;

		center.set(position).addSelf((maxX - minX) / 2, (maxY - minY) / 2);

		if (bounds != null)
			bounds.setPosition(position);
	}

	public Rectangle2D getBounds() {
		updateBounds();
		return bounds;
	}

	private void updateBounds() {
		if (bounds == null){
			bounds = new Rectangle2D();
		}

		float minX, minY, maxX, maxY;

		minX = minY = Float.POSITIVE_INFINITY;
		maxX = maxY = Float.NEGATIVE_INFINITY;

		for (Vector2f vertex : vertices) {
			minX = MathUtils.min(minX, vertex.x);
			minY = MathUtils.min(minY, vertex.y);
			maxX = MathUtils.max(maxX, vertex.x);
			maxY = MathUtils.max(maxY, vertex.y);
		}

		bounds.set(position.getX() + minX, position.getY() + minY, maxX - minX,
				maxY - minY);
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		if (this.rotation == rotation){
			return;
		}
		rotate(rotation - this.rotation);
	}

	public void rotate(float angle) {
		rotate(angle, (maxX - minX) / 2, (maxY - minY) / 2);
	}

	public void rotate(float angle, float originX, float originY) {
		this.rotation += angle;
		if (angle == 0 || this instanceof Circle2D){
			return;
		}
		for (Vector2f vertex : vertices){
			vertex.subtractSelf(originX, originY).rotateSelf(angle)
					.addSelf(originX, originY);
		}
	}

	public void translate(float x, float y) {
		for (Vector2f v : vertices) {
			v.addSelf(x, y);
		}
	}

	@Override
	public int hashCode() {
		int result = position.hashCode();
		result = 31 * result + center.hashCode();
		result = 31 * result + vertices.hashCode();
		result = 31
				* result
				+ (rotation != +0.0f ? NumberUtils.floatToIntBits(rotation) : 0);
		result = 31 * result
				+ (minX != +0.0f ? NumberUtils.floatToIntBits(minX) : 0);
		result = 31 * result
				+ (minY != +0.0f ? NumberUtils.floatToIntBits(minY) : 0);
		result = 31 * result
				+ (maxX != +0.0f ? NumberUtils.floatToIntBits(maxX) : 0);
		result = 31 * result
				+ (maxY != +0.0f ? NumberUtils.floatToIntBits(maxY) : 0);
		result = 31 * result + bounds.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        Polygon2D polygon = (Polygon2D) o;

        return NumberUtils.compare(polygon.maxX, maxX) == 0 &&
               NumberUtils.compare(polygon.maxY, maxY) == 0 &&
               NumberUtils.compare(polygon.minX, minX) == 0 &&
               NumberUtils.compare(polygon.minY, minY) == 0 &&
               NumberUtils.compare(polygon.rotation, rotation) == 0 &&
               bounds.equals(polygon.bounds) &&
               center.equals(polygon.center) &&
               position.equals(polygon.position) &&
               vertices.equals(polygon.vertices);
    }

	@Override
	public String toString() {
		return "Polygon{" + "position=" + position + ", center=" + center
				+ ", vertices=" + vertices + ", rotation=" + rotation
				+ ", minX=" + minX + ", minY=" + minY + ", maxX=" + maxX
				+ ", maxY=" + maxY + ", bounds=" + bounds + '}';
	}

	public float getMinX() {
		return minX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMaxY() {
		return maxY;
	}
}
