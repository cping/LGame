package loon.physics;

import loon.core.geom.FloatValue;

public class PWorldBox {

	private PBody northBody, southBody, eastBody, westBody;

	private PPhysManager manager;

	private boolean build;

	public final FloatValue density = new FloatValue(0);

	private float mx, my, mw, mh;

	private float thick;

	public PWorldBox(PPhysManager world, float x, float y, float w, float h) {
		this.build = false;
		this.manager = world;
		this.set(x, y, w, h);
		this.thick = 1f;
		this.density.set(1f);
	}

	public void removeWorld() {
		if (build) {
			manager.world.removeBody(northBody);
			manager.world.removeBody(southBody);
			manager.world.removeBody(eastBody);
			manager.world.removeBody(westBody);
		}
		build = false;
	}

	public boolean isBuild() {
		return build;
	}
	
	public void set(float x, float y, float w, float h) {
		this.mx = x;
		this.my = y;
		this.mw = w;
		this.mh = h;
	}
	
	public void build() {
		if (build) {
			throw new RuntimeException("Build Error !");
		}
		this.manager.addBox(true, 0f, 0f, mw, thick, 0, density.get());
		this.manager.addBox(true, 0f, mh, mw, thick, 0, density.get());
		this.manager.addBox(true, 0f, 0f, thick, mh, 0, density.get());
		this.manager.addBox(true, mw, 0f, thick, mh, 0, density.get());
		this.build = true;
	}

	public float getDensity() {
		return density.get();
	}

	public void setDensity(float d) {
		this.density.set(d);
	}

	public PBody getEastBody() {
		return eastBody;
	}

	public void setEastBody(PBody eastBody) {
		this.eastBody = eastBody;
	}

	public PBody getNorthBody() {
		return northBody;
	}

	public void setNorthBody(PBody northBody) {
		this.northBody = northBody;
	}

	public PBody getSouthBody() {
		return southBody;
	}

	public void setSouthBody(PBody southBody) {
		this.southBody = southBody;
	}

	public PBody getWestBody() {
		return westBody;
	}

	public void setWestBody(PBody westBody) {
		this.westBody = westBody;
	}

	public float x() {
		return mx;
	}

	public void setX(float mx) {
		this.mx = mx;
	}

	public float y() {
		return my;
	}

	public void setY(float my) {
		this.my = my;
	}

	public float getWidth() {
		return mw;
	}

	public void setWidth(float mw) {
		this.mw = mw;
	}

	public float getHeight() {
		return mh;
	}

	public void setHeight(float mh) {
		this.mh = mh;
	}

	public float getThick() {
		return thick;
	}

	public void setThick(float thick) {
		this.thick = thick;
	}
}
