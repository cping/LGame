package org.test;

import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class CWaypoints {
	private int currentpoint;
	private int icount;
	private MainGame mainGame;
	private Vector2f offset;
	private float tilex;
	private float tiley;
	private Vector2f[] waypointArray;

	public final void addPoint(int x, int y) {
		if (this.currentpoint == this.icount) {
			Vector2f[] vectorArray = new Vector2f[this.icount + 1];
			for (int i = 0; i < this.icount; i++) {
				vectorArray[i] = new Vector2f(this.waypointArray[i].x,
						this.waypointArray[i].y);
			}
			this.icount++;
			this.waypointArray = vectorArray;
		}
		this.waypointArray[this.currentpoint++] = new Vector2f((x * this.tilex)
				+ this.offset.x, (y * this.tiley) + this.offset.y);
	}

	public final int count() {
		return this.currentpoint;
	}

	public final void fillArray(int[][] levelArray) {
		for (int i = 0; i < (this.currentpoint - 1); i++) {
			int startx = (int) MathUtils.clamp(
					(this.waypointArray[i].x / this.tilex) - this.offset.x, 0f,
					this.mainGame.MAXTILES.x - 1f);
			int starty = (int) MathUtils.clamp(
					(this.waypointArray[i].y / this.tilex) - this.offset.y, 0f,
					this.mainGame.MAXTILES.y - 1f);
			int endx = (int) MathUtils.clamp(
					(this.waypointArray[i + 1].x / this.tilex) - this.offset.x,
					0f, this.mainGame.MAXTILES.x - 1f);
			int endy = (int) MathUtils.clamp(
					(this.waypointArray[i + 1].y / this.tilex) - this.offset.y,
					0f, this.mainGame.MAXTILES.y - 1f);
			this.mainGame.fillArray(levelArray, startx, starty, endx, endy);
		}
	}

	public final CWaypoints generateWaypointsWithOffset(float offsetx,
			float offsety) {
		CWaypoints waypoints = new CWaypoints();
		waypoints.init(this.mainGame, this.icount, 1f, 1f);
		waypoints.setOffset(offsetx, offsety);
		for (int i = 0; i < this.icount; i++) {
			waypoints.addPoint((int) this.waypointArray[i].x,
					(int) this.waypointArray[i].y);
		}
		return waypoints;
	}

	public final Vector2f getWaypoint(int index) {
		if (index >= this.icount) {
			return Vector2f.ZERO();
		}
		return this.waypointArray[index];
	}

	public final void init(MainGame game, int count, float tilex, float tiley) {
		this.icount = count;
		this.tilex = tilex;
		this.tiley = tiley;
		this.waypointArray = new Vector2f[count];
		this.offset = new Vector2f(0f, 0f);
		this.mainGame = game;
	}

	public final void setOffset(float x, float y) {
		this.offset.x = x;
		this.offset.y = y;
	}
}