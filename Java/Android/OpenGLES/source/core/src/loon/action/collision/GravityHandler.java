package loon.action.collision;

import java.util.ArrayList;

import loon.action.ActionBind;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.utils.MathUtils;

/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
public class GravityHandler implements LRelease {

	public static interface GravityUpdate {
		public void action(Gravity g, float x, float y);
	}

	private GravityUpdate listener;

	private int width, height;

	private int bindWidth;

	private int bindHeight;

	private float bindX;

	private float bindY;

	private float velocityX, velocityY;

	boolean isBounded;

	boolean isListener;

	boolean isEnabled;

	Gravity[] lazyObjects;

	ArrayList<Gravity> objects;

	ArrayList<Gravity> pendingAdd;

	ArrayList<Gravity> pendingRemove;

	public GravityHandler() {
		this(LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public GravityHandler(int w, int h) {
		this.setLimit(w, h);
		this.objects = new ArrayList<Gravity>(10);
		this.pendingAdd = new ArrayList<Gravity>(10);
		this.pendingRemove = new ArrayList<Gravity>(10);
		this.lazyObjects = new Gravity[] {};
		this.isEnabled = true;
	}

	public boolean isGravityRunning() {
		if (objects != null) {
			for (Gravity g : objects) {
				if (g != null && !g.enabled) {
					return true;
				}
			}
		}
		return false;
	}

	public void setLimit(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void update(long elapsedTime) {
		if (!isEnabled) {
			return;
		}
		commits();
		final float second = elapsedTime / 1000f;
		for (Gravity g : lazyObjects) {
			if (g.enabled && g.bind != null) {

				final float accelerationX = g.accelerationX;
				final float accelerationY = g.accelerationY;
				final float angularVelocity = g.angularVelocity;

				bindWidth = g.bind.getWidth();
				bindHeight = g.bind.getHeight();
				bindX = g.bind.getX();
				bindY = g.bind.getY();

				if (angularVelocity != 0) {

					final float rotate = g.bind.getRotation() + angularVelocity
							* second;
					int[] newObjectRect = MathUtils.getLimit(bindX, bindY,
							bindWidth, bindHeight, rotate);

					bindWidth = newObjectRect[2];
					bindHeight = newObjectRect[3];

					newObjectRect = null;

					g.bind.setRotation(rotate);
				}

				if (accelerationX != 0 || accelerationY != 0) {
					g.velocityX += accelerationX * second;
					g.velocityY += accelerationY * second;
				}

				velocityX = g.velocityX;
				velocityY = g.velocityY;
				if (velocityX != 0 || velocityY != 0) {
					velocityX = bindX + velocityX * second;
					velocityY = bindY + velocityY * second;
					if (isBounded) {
						if (g.g != 0) {
							velocityY += g.gadd;
							g.gadd += g.g;
						}
						if (g.bounce != 0) {
							final int limitWidth = width - bindWidth;
							final int limitHeight = height - bindHeight;
							final boolean chageWidth = bindX >= limitWidth;
							final boolean chageHeight = bindY >= limitHeight;
							if (chageWidth) {
								bindX -= g.bounce + g.g;
								if (g.bounce > 0) {
									g.bounce -= (g.bounce / MathUtils.random(
											1, 5)) + second;
								} else if (g.bounce < 0) {
									g.bounce = 0;
									bindX = limitWidth;
								}
							}
							if (chageHeight) {
								bindY -= g.bounce + g.g;
								if (g.bounce > 0) {
									g.bounce -= (g.bounce / MathUtils.random(
											1, 5)) + second;
								} else if (g.bounce < 0) {
									g.bounce = 0;
									bindY = limitHeight;
								}
							}
							if (chageWidth || chageHeight) {
								g.bind.setLocation(bindX, bindY);
								if (isListener) {
									listener.action(g, bindX, bindY);
								}
								return;
							}
						}
						velocityX = limitValue(velocityX, width - bindWidth);
						velocityY = limitValue(velocityY, height - bindHeight);
					}
					g.bind.setLocation(velocityX, velocityY);
					if (isListener) {
						listener.action(g, velocityX, velocityY);
					}
				}
			}
		}
	}

	private float limitValue(float value, float limit) {
		if (value < 0) {
			value = 0;
		}
		if (limit < value) {
			value = limit;
		}
		return value;
	}

	public void commits() {
		boolean changes = false;
		final int additionCount = pendingAdd.size();
		if (additionCount > 0) {
			final Object[] additionsArray = pendingAdd.toArray();
			for (int i = 0; i < additionCount; i++) {
				Gravity object = (Gravity) additionsArray[i];
				objects.add(object);
			}
			pendingAdd.clear();
			changes = true;
		}
		final int removalCount = pendingRemove.size();
		if (removalCount > 0) {
			final Object[] removalsArray = pendingRemove.toArray();
			for (int i = 0; i < removalCount; i++) {
				Gravity object = (Gravity) removalsArray[i];
				objects.remove(object);
			}
			pendingRemove.clear();
			changes = true;
		}
		if (changes) {
			lazyObjects = objects.toArray(new Gravity[] {});
		}
	}

	public Gravity[] getObjects() {
		return lazyObjects;
	}

	public int getCount() {
		return lazyObjects.length;
	}

	public int getConcreteCount() {
		return lazyObjects.length + pendingAdd.size() - pendingRemove.size();
	}

	public Gravity get(int index) {
		return lazyObjects[index];
	}

	public Gravity add(ActionBind o, float vx, float vy) {
		return add(o, vx, vy, 0);
	}

	public Gravity add(ActionBind o, float vx, float vy, float ave) {
		return add(o, vx, vy, 0, 0, ave);
	}

	public Gravity add(ActionBind o, float vx, float vy, float ax, float ay,
			float ave) {
		Gravity g = new Gravity(o);
		g.velocityX = vx;
		g.velocityY = vy;
		g.accelerationX = ax;
		g.accelerationY = ay;
		g.angularVelocity = ave;
		add(g);
		return g;
	}

	public void add(Gravity object) {
		pendingAdd.add(object);
	}

	public void remove(Gravity object) {
		pendingRemove.add(object);
	}

	public void removeAll() {
		final int count = objects.size();
		final Object[] objectArray = objects.toArray();
		for (int i = 0; i < count; i++) {
			pendingRemove.add((Gravity) objectArray[i]);
		}
		pendingAdd.clear();
	}

	public Gravity getObject(String name) {
		commits();
		for (Gravity object : lazyObjects) {
			if (object != null) {
				if (object.name != null) {
					if (object.name.equals(name)) {
						return object;
					}
				}
			}
		}
		return null;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isBounded() {
		return isBounded;
	}

	public void setBounded(boolean isBounded) {
		this.isBounded = isBounded;
	}

	public boolean isListener() {
		return isListener;
	}

	public void onUpdate(GravityUpdate listener) {
		this.listener = listener;
		if (listener != null) {
			isListener = true;
		} else {
			isListener = false;
		}
	}

	@Override
	public void dispose() {
		this.isEnabled = false;
		if (objects != null) {
			objects.clear();
			objects = null;
		}
		if (pendingAdd != null) {
			pendingAdd.clear();
			pendingAdd = null;
		}
		if (pendingAdd != null) {
			pendingAdd.clear();
			pendingAdd = null;
		}
		if (lazyObjects != null) {
			for (Gravity g : lazyObjects) {
				if (g != null) {
					g.dispose();
					g = null;
				}
			}
		}
	}

}
