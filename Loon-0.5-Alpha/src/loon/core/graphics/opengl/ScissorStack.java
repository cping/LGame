package loon.core.graphics.opengl;

import loon.core.geom.Matrix4;
import loon.core.geom.RectBox;
import loon.core.geom.Vector3f;
import loon.core.graphics.Camera;
import loon.utils.collection.Array;

public class ScissorStack {

	private static Array<RectBox> scissors = new Array<RectBox>();
	static Vector3f tmp = new Vector3f();
	static final RectBox viewport = new RectBox();

	public static boolean pushScissors(RectBox scissor) {
		fix(scissor);
		if (scissors.size() == 0) {
			if (scissor.width < 1 || scissor.height < 1) {
				return false;
			}
			GLUtils.enablecissorTest(GLEx.gl);
		} else {
			RectBox parent = scissors.get(scissors.size() - 1);
			float minX = Math.max(parent.x, scissor.x);
			float maxX = Math.min(parent.x + parent.width, scissor.x
					+ scissor.width);
			if (maxX - minX < 1) {
				return false;
			}
			float minY = Math.max(parent.y, scissor.y);
			float maxY = Math.min(parent.y + parent.height, scissor.y
					+ scissor.height);
			if (maxY - minY < 1) {
				return false;
			}
			scissor.x = minX;
			scissor.y = minY;
			scissor.width = (int) (maxX - minX);
			scissor.height = (int) Math.max(1, maxY - minY);
		}
		scissors.add(scissor);
		GLEx.gl.glScissor((int) scissor.x, (int) scissor.y,
				(int) scissor.width, (int) scissor.height);
		return true;
	}

	public static RectBox popScissors() {
		RectBox old = scissors.pop();
		if (scissors.size() == 0) {
			GLUtils.disablecissorTest(GLEx.gl);
		} else {
			RectBox scissor = scissors.last();
			GLEx.gl.glScissor((int) scissor.x, (int) scissor.y,
					(int) scissor.width, (int) scissor.height);
		}
		return old;
	}

	public static RectBox peekScissors() {
		return scissors.last();
	}

	private static void fix(RectBox rect) {
		rect.x = Math.round(rect.x);
		rect.y = Math.round(rect.y);
		rect.width = Math.round(rect.width);
		rect.height = Math.round(rect.height);
		if (rect.width < 0) {
			rect.width = -rect.width;
			rect.x -= rect.width;
		}
		if (rect.height < 0) {
			rect.height = -rect.height;
			rect.y -= rect.height;
		}
	}

	public static void calculateScissors(Camera camera, Matrix4 batchTransform,
			RectBox area, RectBox scissor) {
		calculateScissors(camera, 0, 0, GLEx.width(), GLEx.height(),
				batchTransform, area, scissor);
	}

	public static void calculateScissors(Camera camera, float viewportX,
			float viewportY, float viewportWidth, float viewportHeight,
			Matrix4 batchTransform, RectBox area, RectBox scissor) {
		tmp.set(area.x, area.y, 0);
		tmp.mul(batchTransform);
		camera.project(tmp, viewportX, viewportY, viewportWidth, viewportHeight);
		scissor.x = tmp.x;
		scissor.y = tmp.y;

		tmp.set(area.x + area.width, area.y + area.height, 0);
		tmp.mul(batchTransform);
		camera.project(tmp, viewportX, viewportY, viewportWidth, viewportHeight);
		scissor.width = (int) (tmp.x - scissor.x);
		scissor.height = (int) (tmp.y - scissor.y);
	}

	public static RectBox getViewport() {
		if (scissors.size() == 0) {
			viewport.setBounds(0, 0, GLEx.width(), GLEx.height());
			return viewport;
		} else {
			RectBox scissor = scissors.last();
			viewport.setBounds(scissor);
			return viewport;
		}
	}
}
