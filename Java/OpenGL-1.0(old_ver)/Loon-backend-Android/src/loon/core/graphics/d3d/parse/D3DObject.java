/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.core.graphics.d3d.parse;

import loon.core.geom.Matrix;

public class D3DObject {
	public D3DIRendererElement[] mRenderElement;
	public D3DMesh mMesh[];
	public D3DMaterial mMaterial[];
	public int mMeshCount;
	public float mScale;
	public float mRotation[] = new float[3];
	public float mPosition[];
	public float mTransformMatrix[];
	public D3DMaterial pickedMaterial;
	public D3DMaterial prepickedMaterial;
	public D3DMaterial unpickedMaterial;
	public boolean mIsPickable;
	public D3DObject mChild;

	public static Object[] merge(D3DObject objtomerge, float tx, float ty,
			float bx, float by) {
		D3DObject newObj = new D3DObject();
		newObj.mMesh = new D3DMesh[1];
		newObj.mMesh[0] = new D3DMesh();

		objtomerge.mMesh[0].mIndices.position(0);

		char a, b, c;
		float x, y, z;
		a = objtomerge.mMesh[0].mIndices.get();
		b = objtomerge.mMesh[0].mIndices.get();
		c = objtomerge.mMesh[0].mIndices.get();

		x = objtomerge.mMesh[0].mVertices.get(a * 3);
		y = objtomerge.mMesh[0].mVertices.get(a * 3 + 1);
		z = objtomerge.mMesh[0].mVertices.get(a * 3 + 2);

		return new Object[] { a, b, c, x, y, z };
	}

	public static void trackCleaner(D3DMesh trackmesh,
			D3DMaterial trackmaterial, D3DMesh mesh) {
		int triangleCount = trackmesh.mIndices.capacity() / 3;
		int meshTriangleCount = mesh.mIndices.capacity() / 3;
		char a, b, c;
		float red;
		float x1, y1, x2, y2, x3, y3;
		float tmpx;
		float tmpy;
		trackmesh.mIndices.position(0);
		mesh.mIndices.position(0);
		for (int i = 0; i < triangleCount; i++) {
			a = trackmesh.mIndices.get(i * 3);
			b = trackmesh.mIndices.get(i * 3 + 1);
			c = trackmesh.mIndices.get(i * 3 + 2);
			red = trackmaterial.mColors.get(a * 4);

			if (red == 1.f) {

				x1 = trackmesh.mVertices.get(a * 3);
				y1 = trackmesh.mVertices.get(a * 3 + 2);

				x2 = trackmesh.mVertices.get(b * 3);
				y2 = trackmesh.mVertices.get(b * 3 + 2);

				x3 = trackmesh.mVertices.get(c * 3);
				y3 = trackmesh.mVertices.get(c * 3 + 2);

				for (int j = 0; j < meshTriangleCount; j++) {
					a = mesh.mIndices.get(j * 3);
					b = mesh.mIndices.get(j * 3 + 1);
					c = mesh.mIndices.get(j * 3 + 2);
					boolean isOnTrack = false;

					tmpx = mesh.mVertices.get(a * 3);
					tmpy = mesh.mVertices.get(a * 3 + 2);

					isOnTrack = isOnTrack
							| Matrix.isOnTriange(x1, y1, x2, y2, x3, y3,
									tmpx, tmpy);

					tmpx = mesh.mVertices.get(b * 3);
					tmpy = mesh.mVertices.get(b * 3 + 2);

					isOnTrack = isOnTrack
							| Matrix.isOnTriange(x1, y1, x2, y2, x3, y3,
									tmpx, tmpy);

					tmpx = mesh.mVertices.get(c * 3);
					tmpy = mesh.mVertices.get(c * 3 + 2);

					isOnTrack = isOnTrack
							| Matrix.isOnTriange(x1, y1, x2, y2, x3, y3,
									tmpx, tmpy);

					if (isOnTrack) {
						mesh.mVertices.put(a * 3, 0.f);
						mesh.mVertices.put(a * 3 + 2, 0.f);

						mesh.mVertices.put(b * 3, 0.f);
						mesh.mVertices.put(b * 3 + 2, 0.f);

						mesh.mVertices.put(c * 3, 0.f);
						mesh.mVertices.put(c * 3 + 2, 0.f);

					}

				}
			}
			trackmesh.mIndices.position(0);
			mesh.mIndices.position(0);
		}

	}
}
