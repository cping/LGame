/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.fx;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import loon.canvas.Path;

public class JavaFXPath implements Path {

	private static final int CMD_BEZIER = 3;
	private static final int CMD_CLOSE = 4;
	private static final int CMD_LINE = 1;
	private static final int CMD_MOVE = 0;
	private static final int CMD_QUAD = 2;

	private ArrayList<Number> list = new ArrayList<Number>();

	@Override
	public Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
		list.add(CMD_BEZIER);
		list.add(c1x);
		list.add(c1y);
		list.add(c2x);
		list.add(c2y);
		list.add(x);
		list.add(y);
		return this;
	}

	@Override
	public Path close() {
		list.add(CMD_CLOSE);
		return this;
	}

	@Override
	public Path lineTo(float x, float y) {
		list.add(CMD_LINE);
		list.add(x);
		list.add(y);
		return this;
	}

	@Override
	public Path moveTo(float x, float y) {
		list.add(CMD_MOVE);
		list.add(x);
		list.add(y);
		return this;
	}

	@Override
	public Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
		list.add(CMD_QUAD);
		list.add(cpx);
		list.add(cpy);
		list.add(x);
		list.add(y);
		return this;
	}

	@Override
	public Path reset() {
		list.clear();
		return this;
	}

	void replay(GraphicsContext ctx) {
		ctx.beginPath();

		int len = list.size(), i = 0;
		float x = 0, y = 0;
		while (i < len) {
			switch (list.get(i++).intValue()) {
			case CMD_MOVE: {
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				ctx.moveTo(x, y);
				break;
			}
			case CMD_LINE: {
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				ctx.lineTo(x, y);
				break;
			}
			case CMD_QUAD: {
				double cpx = list.get(i++).floatValue();
				double cpy = list.get(i++).floatValue();
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				ctx.quadraticCurveTo(cpx, cpy, x, y);
				break;
			}
			case CMD_BEZIER: {
				double c1x = list.get(i++).floatValue(), c1y = list.get(i++).floatValue();
				double c2x = list.get(i++).floatValue(), c2y = list.get(i++).floatValue();
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				ctx.bezierCurveTo(c1x, c1y, c2x, c2y, x, y);
				break;
			}
			case CMD_CLOSE: {
				ctx.closePath();
				break;
			}

			default:
				throw new AssertionError("Corrupt command list");
			}
		}
	}

	float[] getVertices() {
		int len = list.size();
		assert len % 2 == 0;
		float[] vertices = new float[len];
		for (int v = 0; v < len;) {
			int cmd = list.get(v).intValue();
			if (v == vertices.length - 2) {
				assert cmd == CMD_CLOSE;
			} else {
				assert cmd == CMD_MOVE;
			}
			vertices[v] = list.get(v + 1).floatValue();
			vertices[v + 1] = list.get(v + 2).floatValue();
		}
		return vertices;
	}

	void replay(Path path) {
		path.reset();

		int len = list.size(), i = 0;
		float x = 0, y = 0;
		while (i < len) {
			switch (list.get(i++).intValue()) {
			case CMD_MOVE: {
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				path.moveTo(x, y);
				break;
			}
			case CMD_LINE: {
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				path.lineTo(x, y);
				break;
			}
			case CMD_QUAD: {
				float cpx = list.get(i++).floatValue();
				float cpy = list.get(i++).floatValue();
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				path.quadraticCurveTo(cpx, cpy, x, y);
				break;
			}
			case CMD_BEZIER: {
				float c1x = list.get(i++).floatValue(), c1y = list.get(i++).floatValue();
				float c2x = list.get(i++).floatValue(), c2y = list.get(i++).floatValue();
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				path.bezierTo(c1x, c1y, c2x, c2y, x, y);
				break;
			}
			case CMD_CLOSE: {
				path.close();
				break;
			}

			default:
				throw new AssertionError("Corrupt command list");
			}
		}

	}

}
