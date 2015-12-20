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

import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import loon.core.graphics.d3d.parse.D3DMaterial;
import loon.core.graphics.d3d.parse.D3DMesh;
import loon.core.graphics.d3d.parse.D3DObject;
import loon.utils.collection.ArrayMap;

public class D3DMd3LoaderAdapter implements D3DIMdLoaderAdapter{
	
	
	public ArrayMap md3Map = new ArrayMap();
	public HashMap<String,float[][]> tagMap = new HashMap<String,float[][]>();
	public HashMap<String, D3DMaterial> materialMap = new HashMap<String, D3DMaterial>();
	private ArrayList<ArrayList<D3DObject>> surfaceList;
	private String currentSurfaceName = null;
	public HashMap<String,D3DMaterial> shaderMap = new HashMap<String,D3DMaterial>();
	private String fileName = "";
	private int currentFrameNumber;
	private int currentVerticeNumber = 0;
	private int currentTriangleNumber = 0;
	
	@Override
	public void setSurfaceNumber(int surfaceNumber) {
		for (int i = 0;i < surfaceNumber;i++)
		{
			surfaceList.add(new ArrayList<D3DObject>());
		}
	}

	@Override
	public void setFrameNumberHeader(int frameNumber) {
		currentFrameNumber = frameNumber;		
	}
	
	@Override
	public void setFrameNumber(int surfaceId, int frameNumber) {

		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		 
		for (int i = 0;i < frameNumber;i++)
		{
			D3DObject obj = new D3DObject();
			obj.mRotation = new float[3];
			obj.mMesh = new D3DMesh[1];
			obj.mMesh[0] = new D3DMesh();
			frameList.add(obj);
		}
	}

	@Override
	public void setVerticeNumber(int surfaceId, int verticeNumber) {
		currentVerticeNumber = verticeNumber;
		
		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			D3DObject obj = frameList.get(i);
			obj.mMesh[0].mVertices = FloatBuffer.allocate(D3DMesh.nbFloatPerVertex * verticeNumber);
			obj.mMesh[0].mVertices.position(0);
		}
	}

	@Override
	public void setTriangleNumber(int surfaceId, int triangleNumber) {
		currentTriangleNumber = triangleNumber;
	
		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			D3DObject obj = frameList.get(i);
			obj.mMesh[0].mIndices = CharBuffer.allocate(triangleNumber * 3);
			obj.mMesh[0].mIndices.position(0);
		}		
		
	}

	@Override
	public void addVertexCoords(int surfaceId, int frameId, int vertexId, float x, float y,
			float z) {
		
		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		

		D3DObject obj = frameList.get(frameId);
		
		obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex    , x);
		obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 1, y);
		obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 2, z);

	}

	@Override
	public void addNormalCoords(int surfaceId, int frameId, int vertexId, float xn, float yn,
			float zn) {
		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		

		D3DObject obj = frameList.get(frameId);
		
		obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 3, xn);
		obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 4, yn);
		obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 5, zn);
		
	}

	@Override
	public void addTexCoords(int surfaceId, int vertexId, float u, float v) {
		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			D3DObject obj = frameList.get(i);
			obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 6, u);
			obj.mMesh[0].mVertices.put(vertexId*D3DMesh.nbFloatPerVertex + 7, v);			
		}
	}

	@Override
	public void addTriangleIndices(int surfaceId, int triangleId, int a, int b, int c) {
		ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			D3DObject obj = frameList.get(i);
			obj.mMesh[0].mIndices.put(triangleId * 3, (char)a);
			obj.mMesh[0].mIndices.put(triangleId * 3 + 1, (char)b);
			obj.mMesh[0].mIndices.put(triangleId * 3 + 2, (char)c);
		}		
	}

	@Override
	public void setMd3FileName(String filename) {
		surfaceList = new ArrayList<ArrayList<D3DObject>>();
		md3Map.put(filename, surfaceList);
		fileName = filename;
	}

	@Override
	public void setTagNumber(int tagNumber) {
		
		
	}

	@Override
	public void addTag(int frameId, int tagId, String tagName, float x, float y, float z, float rotMatrix[]) {
		
		float f[] = new float[12];
		f[0] = x;
		f[1] = y;
		f[2] = z;
		
		
		for (int i = 0; i < 9;i++)
		{
			f[i+3] = rotMatrix[i];
		}
		
		
		
		
		if (((fileName.toUpperCase().contains("LOWER"))
			&&tagName.equals(D3DMdLoader.TAG_TORSO))
			||
			((fileName.toUpperCase().contains("UPPER"))
					&&tagName.equals(D3DMdLoader.TAG_HEAD)))
		{
			float v[][];
			if (tagMap.get(tagName) == null)
			{
				v = new float[currentFrameNumber][];
				tagMap.put(tagName, v);
			}
			else
			{
				v = tagMap.get(tagName);
				v[frameId] = f;
			}
			
		}
		
	}

	@Override
	public void setShader(int surfaceId, String shader) {
		if (shader.length() > 0)
		{
			if (materialMap.get(shader) == null)
			{
				D3DMaterial material = new D3DMaterial();
				materialMap.put(shader, material);
			}
			ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);		
			
			for (D3DObject o : frameList)
			{
				o.mMaterial = new D3DMaterial[1];
				o.mMaterial[0] = materialMap.get(shader);				
			}
		}
		else
		{
			if (shaderMap.get(currentSurfaceName) == null)
			{
				D3DMaterial material = new D3DMaterial();
				shaderMap.put(currentSurfaceName, material);
			}
			ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);		
			
			for (D3DObject o : frameList)
			{
				
				o.mMaterial = new D3DMaterial[1];
				o.mMaterial[0] = shaderMap.get(currentSurfaceName);
			}			
		}
	}

	@Override
	public void setSurfaceName(int surfaceId, String name) {
		currentSurfaceName = name;
		
	}

	@Override
	public void setShaderNumber(int surfaceId, int shaderNumber) {
		if (shaderNumber == 0)
		{
			if (shaderMap.get(currentSurfaceName) == null)
			{
				D3DMaterial material = new D3DMaterial();
				shaderMap.put(currentSurfaceName, material);
			}
			ArrayList<D3DObject> frameList = surfaceList.get(surfaceId);		
			
			for (D3DObject o : frameList)
			{
				
				o.mMaterial = new D3DMaterial[1];
				o.mMaterial[0] = shaderMap.get(currentSurfaceName);
			}				
		}
		
	}

	public String getCurrentSurfaceName() {
		return currentSurfaceName;
	}

	public int getCurrentFrameNumber() {
		return currentFrameNumber;
	}

	public int getCurrentVerticeNumber() {
		return currentVerticeNumber;
	}

	public int getCurrentTriangleNumber() {
		return currentTriangleNumber;
	}

}
