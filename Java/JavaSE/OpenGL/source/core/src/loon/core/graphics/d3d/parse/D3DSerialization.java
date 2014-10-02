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

import java.io.Serializable;

public class D3DSerialization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float vertices[];
	private char indices[];
	private float normals[];
	private float meshPostion[];

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public char[] getIndices() {
		return indices;
	}

	public void setIndices(char[] indices) {
		this.indices = indices;
	}

	public float[] getNormals() {
		return normals;
	}

	public void setNormals(float normals[]) {
		this.normals = normals;
	}

	public float[] getMeshPostion() {
		return meshPostion;
	}

	public void setMeshPostion(float meshPostion[]) {
		this.meshPostion = meshPostion;
	}

}
