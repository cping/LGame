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
package loon.core.graphics.d3d.parse.md3;

public interface D3DIMdLoaderAdapter {

	public void setMd3FileName(String filename);
	
	public void setSurfaceNumber(int surfaceNumber);
	public void setFrameNumberHeader(int frameNumber);
	public void setFrameNumber(int surfaceId, int frameNumber);
	public void setTagNumber(int tagNumber);
	public void setSurfaceName(int surfaceId, String name);
	public void setShader(int surfaceId, String shader);
	public void setShaderNumber(int surfaceId, int shaderNumber);
	public void setVerticeNumber(int surfaceId, int verticeNumber);
	public void setTriangleNumber(int surfaceId, int triangleNumber);
	public void addTag(int frameId, int tagId, String tagName, float x, float y, float z, float rotMatrix[]);
	public void addVertexCoords(int surfaceId, int frameId, int vertexId, float x ,float y, float z);
	public void addNormalCoords(int surfaceId, int frameId, int vertexId, float xn ,float yn, float zn);
	public void addTexCoords(int surfaceId, int vertexId, float u, float v);
	public void addTriangleIndices(int surfaceId, int triangleId,int a, int b, int c);
}
