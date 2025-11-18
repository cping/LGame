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
package loon.teavm;

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.core.JSArray;

import loon.canvas.Path;

public class TeaPath implements Path {

	private static final int CMD_BEZIER = 3;
	private static final int CMD_CLOSE = 4;
	private static final int CMD_LINE = 1;
	private static final int CMD_MOVE = 0;
	private static final int CMD_QUAD = 2;

	private JSArray<Number> list = new JSArray<Number>(15);

	@Override
	public Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
		list.push(CMD_BEZIER);
		list.push(c1x);
		list.push(c1y);
		list.push(c2x);
		list.push(c2y);
		list.push(x);
		list.push(y);
		return this;
	}

	@Override
	public Path close() {
		list.push(CMD_CLOSE);
		return this;
	}

	@Override
	public Path lineTo(float x, float y) {
		list.push(CMD_LINE);
		list.push(x);
		list.push(y);
		return this;
	}

	@Override
	public Path moveTo(float x, float y) {
		list.push(CMD_MOVE);
		list.push(x);
		list.push(y);
		return this;
	}

	@Override
	public Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
		list.push(CMD_QUAD);
		list.push(cpx);
		list.push(cpy);
		list.push(x);
		list.push(y);
		return this;
	}

	@Override
	public Path reset() {
		list.setLength(0);
		return this;
	}

	void replay(CanvasRenderingContext2D ctx) {
		ctx.beginPath();

		int len = list.getLength(), i = 0;
		float x = 0, y = 0;
		while (i < len) {
			switch ((int) list.get(i++)) {
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
				float cpx = list.get(i++).floatValue();
				float cpy = list.get(i++).floatValue();
				x = list.get(i++).floatValue();
				y = list.get(i++).floatValue();
				ctx.quadraticCurveTo(cpx, cpy, x, y);
				break;
			}
			case CMD_BEZIER: {
				float c1x = list.get(i++).floatValue(), c1y = list.get(i++).floatValue();
				float c2x = list.get(i++).floatValue(), c2y = list.get(i++).floatValue();
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
		int len = list.getLength();
		assert len % 2 == 0;
		float[] vertices = new float[len];
		for (int v = 0; v < len;) {
			int cmd = (int) list.get(v);
			if (v == vertices.length - 2) {
				assert cmd == CMD_CLOSE;
			} else {
				assert cmd == CMD_MOVE;
			}
			vertices[v] = (float) list.get(v + 1);
			vertices[v + 1] = (float) list.get(v + 2);
		}
		return vertices;
	}

	void replay(Path path) {
		path.reset();

		int len = list.getLength(), i = 0;
		float x = 0, y = 0;
		while (i < len) {
			switch ((int) list.get(i++)) {
			case CMD_MOVE: {
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.moveTo(x, y);
				break;
			}
			case CMD_LINE: {
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.lineTo(x, y);
				break;
			}
			case CMD_QUAD: {
				float cpx = (float) list.get(i++);
				float cpy = (float) list.get(i++);
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.quadraticCurveTo(cpx, cpy, x, y);
				break;
			}
			case CMD_BEZIER: {
				float c1x = (float) list.get(i++), c1y = (float) list.get(i++);
				float c2x = (float) list.get(i++), c2y = (float) list.get(i++);
				x = (float) list.get(i++);
				y = (float) list.get(i++);
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
