package loon.physics;

import loon.core.geom.Circle;
import loon.core.geom.Polygon;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.utils.CollectionUtils;

public class PPhysManager {
	public float scale;

	public float offX;
	public float offY;

	public PPhysWorld world;
	public boolean start;
	public boolean enableGravity;
	public Vector2f gravity;

	private boolean isCenterPos = false;

	public PPhysManager() {
		this(50F);
	}

	public PPhysManager(float s) {
		this(s, 0.0F, 9.80665F);
	}

	public PPhysManager(float s, float gx, float gy) {
		this.world = new PPhysWorld();
		this.gravity = new Vector2f(gx, gy);
		this.start = false;
		this.enableGravity = true;
		this.scale = s;
	}

	public PBody addBody(PBody body) {
		world.addBody(body);
		return body;
	}

	public PBody box(boolean fix, float x, float y, float w, float h,
			float angle, float density) {
		PBody body = null;
		if (!isCenterPos) {
			body = new PBody(angle, fix, new PShape[] { new PBoxShape(
					(x + w / 2) / scale, (y + h / 2) / scale, w / scale, h
							/ scale, angle, density) });
		} else {
			body = new PBody(angle, fix,
					new PShape[] { new PBoxShape(x / scale, y / scale, w
							/ scale, h / scale, angle, density) });
		}
		return body;
	}

	public PBody box(boolean fix, RectBox rect, float angle, float density) {
		return box(fix, rect.x, rect.y, rect.width,
				rect.height, angle, density);
	}

	public PBody addBox(boolean fix, float x, float y, float w, float h,
			float angle, float density) {
		PBody body = box(fix, x, y, w, h, angle, density);
		world.addBody(body);
		return body;
	}

	public PBody addBox(boolean fix, float x, float y, float w, float h) {
		return addBox(fix, x, y, w, h, 0F, 1F);
	}

	public PBody addBox(boolean fix, RectBox rect, float angle, float density) {
		PBody body = box(fix, rect, angle, density);
		world.addBody(body);
		return body;
	}

	public PBody addBox(boolean fix, RectBox rect) {
		return addBox(fix, rect, 0F, 1F);
	}

	public PBody circle(boolean fix, float x, float y, float r, float angle,
			float density) {
		PBody body = null;
		if (!isCenterPos) {
			body = new PBody(angle, fix, new PShape[] { new PCircleShape(x
					/ scale, y / scale, r / scale, angle, density) });
		} else {
			body = new PBody(angle, fix, new PShape[] { new PCircleShape(x
					/ scale, y / scale, r / scale, angle, density) });
		}
		return body;
	}

	public PBody circle(boolean fix, Circle c, float angle, float density) {
		return circle(fix, c.x, c.y, c.radius, angle, density);
	}

	public PBody addCircle(boolean fix, float x, float y, float r, float angle,
			float density) {
		PBody body = circle(fix, x, y, r, angle, density);
		world.addBody(body);
		return body;
	}

	public PBody addCircle(boolean fix, float x, float y, float r, float angle) {
		return addCircle(fix, x, y, r, angle, 1F);
	}

	public PBody addCircle(boolean fix, float x, float y, float r) {
		return addCircle(fix, x, y, r, 0F, 1F);
	}

	public PBody polygon(boolean fix, float[] xs, float[] ys, int num,
			float angle, float density) {
		if (num < 3) {
			return null;
		}
		if (xs.length != num) {
			xs = CollectionUtils.copyOf(xs, num);
		}
		if (ys.length != num) {
			ys = CollectionUtils.copyOf(ys, num);
		}
		for (int i = 0; i < num; i++) {
			xs[i] /= scale;
			ys[i] /= scale;
		}
		PConcavePolygonShape shape = new PConcavePolygonShape(xs, ys, density);
		PBody body = new PBody(angle, fix, new PShape[] { shape });
		return body;
	}

	public PBody polygon(boolean fix, Polygon p, float angle, float density) {
		PPolygon tmp = p.getPPolygon(this.scale);
		PConcavePolygonShape shape = new PConcavePolygonShape(tmp.xs, tmp.ys,
				density);
		PBody body = new PBody(angle, fix, new PShape[] { shape });
		return body;
	}

	public PBody addPolygon(boolean fix, float[] xs, float[] ys, int num) {
		return addPolygon(fix, xs, ys, num, 0F, 1F);
	}

	public PBody addPolygon(boolean fix, float[] xs, float[] ys, int num,
			float angle, float density) {
		PBody body = polygon(fix, xs, ys, num, angle, density);
		world.addBody(body);
		return body;
	}

	public PBody addPolygon(boolean fix, Polygon p) {
		return addPolygon(fix, p, 0F, 1F);
	}

	public PBody addPolygon(boolean fix, Polygon p, float angle, float density) {
		PBody body = polygon(fix, p, angle, density);
		world.addBody(body);
		return body;
	}

	public PBody shape(boolean fix, PShape shape, float angle, float density) {
		PBody body = new PBody(angle, fix, new PShape[] { shape });
		return body;
	}

	public PBody shape(boolean fix, PShape shape) {
		return shape(fix, shape, 0F, 1F);
	}

	public PBody addShape(boolean fix, PShape shape, float angle, float density) {
		PBody body = shape(fix, shape, angle, density);
		world.addBody(body);
		return body;
	}

	public PBody addShape(boolean fix, PShape shape) {
		return addShape(fix, shape, 0F, 1F);
	}

	public PBody addShape(boolean fix, LTexture shape) {
		return addPolygon(fix, ((Polygon) shape.getShape()));
	}

	public PBody addShape(boolean fix, LTexture shape, float angle,
			float density) {
		return addPolygon(fix, ((Polygon) shape.getShape()), angle, density);
	}

	public float getWorldX(float sx) {
		return (sx - this.offX) / this.scale;
	}

	public float getWorldY(float sy) {
		return (sy - this.offY) / this.scale;
	}

	public float getScreenX(float wx) {
		return wx * this.scale + this.offX;
	}

	public float getScreenY(float wy) {
		return wy * this.scale + this.offY;
	}

	public void panScreen(float px, float py) {
		this.offX += px;
		this.offY += py;
	}

	public void offset(float px, float py) {
		this.offX = px;
		this.offY = py;
	}

	public void zoomScreen(float zoom, float cx, float cy) {
		float px = -getWorldX(cx);
		float py = -getWorldY(cy);
		this.scale *= zoom;
		px += getWorldX(cx);
		py += getWorldY(cy);
		panScreen(px * this.scale, py * this.scale);
	}

	public void step(float dt) {
		if (enableGravity) {
			world.setGravity(gravity.x, gravity.y);
		} else {
			world.setGravity(0.0F, 0.0F);
		}
		if (start) {
			world.step(dt);
		} else {
			world.update();
		}
	}

	public PPhysWorld getWorld() {
		return world;
	}

	public void setWorld(PPhysWorld world) {
		this.world = world;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public boolean isEnableGravity() {
		return enableGravity;
	}

	public void setEnableGravity(boolean enableGravity) {
		this.enableGravity = enableGravity;
	}

	public Vector2f getGravity() {
		return gravity;
	}

	public void setGravity(Vector2f gravity) {
		this.gravity = gravity;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getOffX() {
		return offX;
	}

	public void setOffX(float offX) {
		this.offX = offX;
	}

	public float getOffY() {
		return offY;
	}

	public void setOffY(float offY) {
		this.offY = offY;
	}

	public boolean isCenterPos() {
		return isCenterPos;
	}

	public void setCenterPos(boolean isCenterPos) {
		this.isCenterPos = isCenterPos;
	}

}
