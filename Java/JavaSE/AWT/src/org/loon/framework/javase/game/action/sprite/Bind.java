package org.loon.framework.javase.game.action.sprite;

import java.util.HashMap;

import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.graphics.LComponent;
import org.loon.framework.javase.game.core.graphics.component.Actor;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public final class Bind {

	private Object obj;

	private static HashMap<Class<?>, BindObject> methodList;

	private boolean isBindPos, isBindGetPos, isBindRotation, isBindGetRotation,
			isBindUpdate, isBindScale, isBindSize;

	private java.lang.reflect.Method[] methods;

	private int type;

	private Actor actorObject;

	private LComponent compObject;

	private LObject lObject;

	static class BindObject {

		boolean bindPos, bindGetPos, bindRotation, bindGetRotation, bindUpdate,
				bindScale, bindSize;

		java.lang.reflect.Method[] methods;

		public BindObject(java.lang.reflect.Method[] m) {
			this.methods = m;
		}
	}

	public Bind(Object o) {
		if (o instanceof Actor) {
			type = 1;
			actorObject = (Actor) o;
			this.isBindPos = true;
			this.isBindGetPos = true;
			this.isBindRotation = true;
			this.isBindGetRotation = true;
			this.isBindUpdate = true;
			this.isBindScale = true;
			this.isBindSize = true;
		} else if (o instanceof LComponent) {
			type = 3;
			compObject = (LComponent) o;
			this.isBindPos = true;
			this.isBindGetPos = true;
			this.isBindRotation = false;
			this.isBindGetRotation = false;
			this.isBindUpdate = true;
			this.isBindScale = false;
			this.isBindSize = true;
		} else if (o instanceof LObject) {
			type = 4;
			lObject = (LObject) o;
			this.isBindPos = true;
			this.isBindGetPos = true;
			this.isBindRotation = false;
			this.isBindGetRotation = false;
			this.isBindUpdate = true;
			this.isBindScale = false;
			this.isBindSize = true;
		} else {
			type = 0;
			BindObject object = bindClass(this.obj = o);
			this.methods = object.methods;
			this.isBindPos = object.bindPos;
			this.isBindGetPos = object.bindGetPos;
			this.isBindRotation = object.bindRotation;
			this.isBindGetRotation = object.bindGetRotation;
			this.isBindUpdate = object.bindUpdate;
			this.isBindScale = object.bindScale;
			this.isBindSize = object.bindSize;
		}
	}

	private synchronized static BindObject bindClass(Object o) {

		BindObject result;
		Class<?> clazz = o.getClass();

		if (methodList == null) {
			methodList = new HashMap<Class<?>, BindObject>(10);
		}
		result = methodList.get(clazz);

		if (result == null) {
			result = new BindObject(new java.lang.reflect.Method[11]);
			try {
				java.lang.reflect.Method setX = clazz.getMethod("setX",
						double.class);
				java.lang.reflect.Method setY = clazz.getMethod("setY",
						double.class);
				result.methods[0] = setX;
				result.methods[1] = setY;
				result.bindPos = true;

			} catch (Exception e) {
				result.bindPos = false;
			}
			if (result.bindPos) {
				try {
					java.lang.reflect.Method getX = clazz.getMethod("getX");
					java.lang.reflect.Method getY = clazz.getMethod("getY");
					result.methods[7] = getX;
					result.methods[8] = getY;
					result.bindGetPos = true;
				} catch (Exception e) {
					result.bindGetPos = false;
				}
			}
			if (result.bindPos && !result.bindGetPos) {
				try {
					java.lang.reflect.Method getX = clazz.getMethod("x");
					java.lang.reflect.Method getY = clazz.getMethod("y");
					result.methods[7] = getX;
					result.methods[8] = getY;
					result.bindGetPos = true;
				} catch (Exception e) {
					result.bindGetPos = false;
				}
			}
			if (!result.bindPos) {
				try {
					java.lang.reflect.Method setX = clazz.getMethod("setX",
							int.class);
					java.lang.reflect.Method setY = clazz.getMethod("setY",
							int.class);
					result.methods[0] = setX;
					result.methods[1] = setY;
					result.bindPos = true;
				} catch (Exception e) {
					result.bindPos = false;
				}
			}
			if (!result.bindPos) {
				try {
					java.lang.reflect.Method location = clazz.getMethod(
							"setLocation", double.class);
					result.methods[0] = location;
					result.bindPos = true;
				} catch (Exception e) {
					result.bindPos = false;
				}
			}
			if (!result.bindPos) {
				try {
					java.lang.reflect.Method location = clazz.getMethod(
							"setLocation", int.class);
					result.methods[0] = location;
					result.bindPos = true;
				} catch (Exception e) {
					result.bindPos = false;
				}
			}
			if (!result.bindPos) {
				try {
					java.lang.reflect.Method location = clazz.getMethod(
							"setPosition", double.class);
					result.methods[0] = location;
					result.bindPos = true;
				} catch (Exception e) {
					result.bindPos = false;
				}
			}
			if (!result.bindPos) {
				try {
					java.lang.reflect.Method location = clazz.getMethod(
							"setPosition", int.class);
					result.methods[0] = location;
					result.bindPos = true;
				} catch (Exception e) {
					result.bindPos = false;
				}
			}
			try {
				java.lang.reflect.Method rotation = clazz.getMethod(
						"setRotation", double.class);
				result.methods[2] = rotation;
				result.bindRotation = true;
			} catch (Exception e) {
				result.bindRotation = false;
			}
			if (result.bindRotation) {
				try {
					java.lang.reflect.Method getRotation = clazz
							.getMethod("getRotation");
					result.methods[6] = getRotation;
					result.bindGetRotation = true;
				} catch (Exception e) {
					result.bindGetRotation = false;
				}
			}
			if (!result.bindRotation) {
				try {
					java.lang.reflect.Method rotation = clazz.getMethod(
							"setRotation", int.class);
					result.methods[2] = rotation;
					result.bindRotation = true;
				} catch (Exception e) {
					result.bindRotation = false;
				}
			}
			if (!result.bindRotation) {
				try {
					java.lang.reflect.Method rotation = clazz.getMethod(
							"setAngle", double.class);
					result.methods[2] = rotation;
					result.bindRotation = true;
				} catch (Exception e) {
					result.bindRotation = false;
				}
			}
			if (result.bindRotation && !result.bindGetRotation) {
				try {
					java.lang.reflect.Method getRotation = clazz
							.getMethod("getAngle");
					result.methods[6] = getRotation;
					result.bindGetRotation = true;
				} catch (Exception e) {
					result.bindGetRotation = false;
				}
			}
			if (!result.bindRotation) {
				try {
					java.lang.reflect.Method rotation = clazz.getMethod(
							"setAngle", int.class);
					result.methods[2] = rotation;
					result.bindRotation = true;
				} catch (Exception e) {
					result.bindRotation = false;
				}
			}
			try {
				java.lang.reflect.Method update = clazz.getMethod("update",
						long.class);
				result.methods[3] = update;
				result.bindUpdate = true;

			} catch (Exception e) {
				result.bindUpdate = false;
			}
			try {
				java.lang.reflect.Method setScaleX = clazz.getMethod(
						"setScaleX", double.class);
				java.lang.reflect.Method setScaleY = clazz.getMethod(
						"setScaleY", double.class);
				result.methods[4] = setScaleX;
				result.methods[5] = setScaleY;
				result.bindScale = true;
			} catch (Exception e) {
				result.bindScale = false;
			}
			if (!result.bindScale) {
				try {
					java.lang.reflect.Method scale = clazz.getMethod(
							"setScale", double.class);
					result.methods[4] = scale;
					result.bindScale = true;
				} catch (Exception e) {
					result.bindScale = false;
				}
			}
			try {
				java.lang.reflect.Method width = clazz.getMethod("getWidth");
				java.lang.reflect.Method height = clazz.getMethod("getHeight");
				result.methods[9] = width;
				result.methods[10] = height;
				result.bindSize = true;
			} catch (Exception e) {
				result.bindSize = false;
			}
			methodList.put(clazz, result);
		}
		return result;
	}

	public double getX() {
		switch (type) {
		case 0:
			try {
				if (isBindGetPos) {
					if (methods != null) {
						if (methods[7] != null) {
							Object o = methods[7].invoke(obj);
							if (o instanceof Float) {
								return ((Float) o);
							} else if (o instanceof Integer) {
								return ((Integer) o);
							}
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				return actorObject.getX();
			}
			break;
		case 3:
			if (compObject != null) {
				return compObject.getX();
			}
			break;
		case 4:
			if (lObject != null) {
				return lObject.getX();
			}
			break;
		}
		return 0;
	}

	public double getY() {
		switch (type) {
		case 0:
			try {
				if (isBindGetPos) {
					if (methods != null) {
						if (methods[8] != null) {
							Object o = methods[8].invoke(obj);
							if (o instanceof Float) {
								return ((Float) o);
							} else if (o instanceof Integer) {
								return ((Integer) o);
							}
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				return actorObject.getY();
			}
			break;
		case 3:
			if (compObject != null) {
				return compObject.getY();
			}
			break;
		case 4:
			if (lObject != null) {
				return lObject.getY();
			}
			break;
		}

		return 0;
	}

	public void callScale(float scaleX, float scaleY) {
		switch (type) {
		case 0:
			try {
				if (isBindScale) {
					if (methods != null) {
						if (methods[5] != null) {
							methods[4].invoke(obj, scaleX);
							methods[5].invoke(obj, scaleY);
						} else {
							methods[4].invoke(obj, scaleX, scaleY);
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				actorObject.setScale(scaleX, scaleY);
			}
			break;
		}

	}

	public void callRotation(double r) {
		switch (type) {
		case 0:
			try {
				if (isBindRotation) {
					if (methods != null) {
						if (methods[2] != null) {
							methods[2].invoke(obj, r);
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				actorObject.setRotation((int) r);
			}
			break;
		}

	}

	public double getRotation() {
		switch (type) {
		case 0:
			try {
				if (isBindGetRotation) {
					if (methods != null) {
						if (methods[6] != null) {
							Object o = methods[6].invoke(obj);
							if (o instanceof Float) {
								return ((Float) o);
							} else if (o instanceof Integer) {
								return ((Integer) o);
							} else if (o instanceof Double) {
								return ((Double) o);
							}
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				return actorObject.getRotation();
			}
			break;
		}
		return 0;
	}

	public void callPos(double x, double y) {
		switch (type) {
		case 0:
			try {
				if (isBindPos) {
					if (methods != null) {
						if (methods[1] != null) {
							methods[0].invoke(obj, x);
							methods[1].invoke(obj, y);
						} else {
							methods[0].invoke(obj, x, y);
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				actorObject.setLocation((int) x, (int) y);
			}
			break;
		case 3:
			if (compObject != null) {
				compObject.setLocation(x, y);
			}
			break;
		case 4:
			if (lObject != null) {
				lObject.setLocation(x, y);
			}
			break;
		}
	}

	public void callUpdate(long elapsedTime) {
		switch (type) {
		case 0:
			try {
				if (isBindUpdate) {
					if (methods != null) {
						if (methods[3] != null) {
							methods[3].invoke(obj, elapsedTime);
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				actorObject.action(elapsedTime);
			}
			break;
		case 3:
			if (compObject != null) {
				compObject.update(elapsedTime);
			}
			break;
		case 4:
			if (lObject != null) {
				lObject.update(elapsedTime);
			}
			break;
		}
	}

	public int getWidth() {
		switch (type) {
		case 0:
			try {
				if (isBindSize) {
					if (methods != null) {
						if (methods[9] != null) {
							Object o = methods[9].invoke(obj);
							if (o instanceof Float) {
								return ((Float) o).intValue();
							} else if (o instanceof Integer) {
								return ((Integer) o);
							}
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				return actorObject.getWidth();
			}
			break;
		case 3:
			if (compObject != null) {
				return compObject.getWidth();
			}
			break;
		case 4:
			if (lObject != null) {
				return lObject.getWidth();
			}
			break;
		}
		return 0;
	}

	public int getHeight() {
		switch (type) {
		case 0:
			try {
				if (isBindSize) {
					if (methods != null) {
						if (methods[10] != null) {
							Object o = methods[10].invoke(obj);
							if (o instanceof Float) {
								return ((Float) o).intValue();
							} else if (o instanceof Integer) {
								return ((Integer) o);
							}
						}
					}
				}
			} catch (Exception e) {
			}
			break;
		case 1:
			if (actorObject != null) {
				return actorObject.getHeight();
			}
			break;
		case 3:
			if (compObject != null) {
				return compObject.getHeight();
			}
			break;
		case 4:
			if (lObject != null) {
				return lObject.getHeight();
			}
			break;
		}
		return 0;
	}

	public boolean isBindPos() {
		return isBindPos;
	}

	public boolean isBindRotation() {
		return isBindRotation;
	}

	public boolean isBindUpdate() {
		return isBindUpdate;
	}

	public Object ref() {
		return obj;
	}

}
