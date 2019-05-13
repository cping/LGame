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

import loon.canvas.Canvas;

public interface Mesh {

	public void setVertices(float[] vers);
	
	public void setIndices(int[] inds);
	
	public void paint(Canvas canvas);

	public void renderNoIndexes(MeshData mesh);

	public void renderWithIndexes(MeshData mesh) ;

	public void renderDrawTriangle(MeshData mesh, int index0, int index1, int index2);
	
	public MeshData getMesh();
	
	public void setMesh(MeshData mesh) ;
}
