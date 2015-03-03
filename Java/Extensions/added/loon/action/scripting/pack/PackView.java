package loon.action.scripting.pack;

import loon.action.ActionBind;
import loon.core.timer.LTimer;


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
public abstract class PackView {

	public static class CameraView extends PackView {

		private static CameraView instance;

		public static CameraView getInstance(ActionBind o, int w, int h) {
			if (instance == null) {
				instance = new CameraView(o, w, h);
			} else if (instance.obj != o || instance.width != w
					|| instance.height != h) {
				instance = new CameraView(o, w, h);
			}
			return instance;
		}

		private LTimer timer = new LTimer(150);

		private float cameraX, cameraY;

		private float width, height;

		private ActionBind bind;

		private ActionBind obj;

		public CameraView(ActionBind o, int w, int h) {
			this.width = w;
			this.height = h;
			this.bind(o);
		}

		public void setDelay(long d) {
			timer.setDelay(d);
		}

		public long getDelay() {
			return timer.getDelay();
		}

		@Override
		public float worldToRealX(float x) {
			return x - cameraX + width / 2;
		}

		@Override
		public float worldToRealY(float y) {
			return y - cameraY + height / 2;
		}

		@Override
		public float realToWorldX(float x) {
			return x + cameraX - width / 2;
		}

		@Override
		public float realToWorldY(float y) {
			return y + cameraY - height / 2;
		}

		public void bind(ActionBind o) {
			this.obj = o;
			if (o == null) {
				bind = null;
			} else {
				bind = o;
			}
		}

		@Override
		public void update(long elapsedTime) {
			if (bind == null) {
				return;
			}
			if (timer.action(elapsedTime)) {
				float dx = bind.getX() - cameraX;
				float cx = width / 3;
				if (dx > cx) {
					cameraX += dx - cx;
				}
				if (dx < -cx) {
					cameraX += dx + cx;
				}
				float dy = bind.getY() - cameraY;
				float cy = height / 3;
				if (dy > cy) {
					cameraY += dy - cy;
				}
				if (dy < -cy) {
					cameraY += dy + cy;
				}
			}
		}

		public void reset() {
			cameraX = width / 2;
			cameraY = height / 2;
			update(0);
		}
	}

	public static class EmptyView extends PackView {

		private static EmptyView instance;

		public static EmptyView getInstance() {
			if (instance == null) {
				instance = new EmptyView();
			}
			return instance;
		}

		private EmptyView() {
		}

		@Override
		public float worldToRealX(float x) {
			return x;
		}

		@Override
		public float worldToRealY(float y) {
			return y;
		}

		@Override
		public float realToWorldX(float x) {
			return x;
		}

		@Override
		public float realToWorldY(float y) {
			return y;
		}

		@Override
		public void update(long elapsedTime) {

		}
	}

	public abstract float worldToRealX(float x);

	public abstract float worldToRealY(float y);

	public abstract float realToWorldX(float x);

	public abstract float realToWorldY(float y);

	public abstract void update(long elapsedTime);
}
