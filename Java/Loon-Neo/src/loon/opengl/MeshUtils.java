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
package loon.opengl;

import loon.utils.FloatArray;
import loon.utils.IntArray;

public class MeshUtils {

	public static FloatArray createVerticesGrid(FloatArray vertices, int columns, int rows, float width, float height,
			float staggerX, float staggerY, int attrLength, FloatArray attrValues) {

		int vertexCount = (columns + 1) * (rows + 1);
		if (vertices == null) {
			vertices = new FloatArray();
		}
		if (vertices.length > vertexCount * 2) {
			vertices.ensureCapacity(vertexCount * 2);
		}

		float columnWidth = width / columns;
		float rowHeight = height / rows;

		int index = 0;
		int attrIndex = 0;
		if (attrLength > 0 && attrValues == null) {
			if (staggerX == 0 && staggerY == 0) {
				for (int y = 0; y < (rows + 1); y++) {
					for (int x = 0; x < (columns + 1); x++) {

						float xPos = x * columnWidth;
						float yPos = y * rowHeight;

						vertices.set(index++, xPos);
						vertices.set(index++, yPos);

						for (int i = 0; i < attrLength; i++) {
							vertices.set(index++, 0);
						}
					}
				}
			} else {
				for (int y = 0; y < (rows + 1); y++) {
					int modY = (y % 2);
					for (int x = 0; x < (columns + 1); x++) {

						float xPos = x * columnWidth;
						float yPos = y * rowHeight;

						vertices.set(index++, xPos + staggerX * modY);
						vertices.set(index++, yPos + staggerY * (x % 2));

						for (int i = 0; i < attrLength; i++) {
							vertices.set(index++, 0);
						}
					}
				}
			}
		} else {
			if (staggerX == 0 && staggerY == 0) {
				for (int y = 0; y < (rows + 1); y++) {
					for (int x = 0; x < (columns + 1); x++) {

						float xPos = x * columnWidth;
						float yPos = y * rowHeight;

						vertices.set(index++, xPos);
						vertices.set(index++, yPos);

						for (int i = 0; i < attrLength; i++) {
							vertices.set(index++, attrValues.get(attrIndex++));
						}
					}
				}
			} else {
				for (int y = 0; y < (rows + 1); y++) {
					int modY = (y % 2);
					for (int x = 0; x < (columns + 1); x++) {

						float xPos = x * columnWidth;
						float yPos = y * rowHeight;

						vertices.set(index++, xPos + staggerX * modY);
						vertices.set(index++, yPos + staggerY * (x % 2));

						for (int i = 0; i < attrLength; i++) {
							vertices.set(index++, attrValues.get(attrIndex++));
						}
					}
				}
			}
		}
		return vertices;
	}

	public static IntArray createIndicesGrid(IntArray indices, int columns, int rows, boolean mirrorX, boolean mirrorY,
			boolean mirrorFlip) {

		int triangleCount = columns * rows * 2;
		if (indices == null) {
			indices = new IntArray();
		}
		if (indices.length > triangleCount * 3) {
			indices.ensureCapacity(triangleCount * 3);
		}
		int index = 0;
		int baseFlip = mirrorFlip ? -1 : 1;
		for (int y = 0; y < rows; y++) {
			int yAlt = (y % 2 == 1) ? -1 : 1;
			for (int x = 0; x < columns; x++) {
				int topLeft = y * (columns + 1) + x;
				int topRight = topLeft + 1;
				int bottomLeft = topLeft + (columns + 1);
				int bottomRight = bottomLeft + 1;
				int flip = baseFlip;
				if (mirrorX && x % 2 == 1) {
					flip *= -1;
				}
				flip *= yAlt;
				if (flip == 1) {
					indices.set(index++, topLeft);
					indices.set(index++, topRight);
					indices.set(index++, bottomLeft);
					indices.set(index++, topRight);
					indices.set(index++, bottomRight);
					indices.set(index++, bottomLeft);
				} else {
					indices.set(index++, topLeft);
					indices.set(index++, bottomRight);
					indices.set(index++, bottomLeft);
					indices.set(index++, topLeft);
					indices.set(index++, topRight);
					indices.set(index++, bottomRight);
				}
			}
		}
		return indices;
	}

	public static FloatArray createUVsGrid(FloatArray uvs, int columns, int rows, float offsetX, float offsetY) {

		int vertexCount = (columns + 1) * (rows + 1);
		if (uvs == null) {
			uvs = new FloatArray();
		}
		if (uvs.length > vertexCount * 2) {
			uvs.ensureCapacity(vertexCount * 2);
		}

		int index = 0;
		for (int y = 0; y < (rows + 1); y++) {
			for (int x = 0; x < (columns + 1); x++) {

				float uvX = (x + offsetX) / columns;
				float uvY = (y + offsetY) / rows;

				uvs.set(index++, uvX);
				uvs.set(index++, uvY);
			}
		}

		return uvs;

	}
}
