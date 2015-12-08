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
package loon.opengl.parse;

import loon.BaseIO;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteReader;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class D3DObjLoader {

	D3DIObjLoaderAdapter mObjLoaderAdapter = new D3DObjLoaderAdapter();
	TArray<float[]> vertices = new TArray<float[]>();
	TArray<float[]> normals = new TArray<float[]>();
	TArray<float[]> texcoords = new TArray<float[]>();
	char vertCounter = 0;

	public D3DObjLoader(D3DIObjLoaderAdapter objloader) {
		mObjLoaderAdapter = objloader;
	}

	private void addVertexData(String vertexDesc) {
		String tokens[] = vertexDesc.split("/");
		int vIndice = Integer.parseInt(tokens[0]) - 1;
		int tIndice = Integer.parseInt(tokens[1]) - 1;
		int nIndice = Integer.parseInt(tokens[2]) - 1;

		float vert[] = vertices.get(vIndice);
		mObjLoaderAdapter.addVertex(vert[0], vert[1], vert[2]);
		float norm[] = normals.get(nIndice);
		mObjLoaderAdapter.addNormal(norm[0], norm[1], norm[2]);
		float text[] = texcoords.get(tIndice);
		mObjLoaderAdapter.addTexCoords(text[0], 1.0f - text[1]);
		vertCounter++;
	}

	private void addfaceData(int vertCount) {
		if (vertCount == 4) {
			mObjLoaderAdapter.addFace((char) (vertCounter - 2),
					(char) (vertCounter - 3), (char) (vertCounter - 4));
			mObjLoaderAdapter.addFace((char) (vertCounter - 1),
					(char) (vertCounter - 2), (char) (vertCounter - 4));
		} else if (vertCount == 3) {
			mObjLoaderAdapter.addFace((char) (vertCounter - 1),
					(char) (vertCounter - 2), (char) (vertCounter - 3));
		}
	}

	public void forwardCountFromStream(ArrayByte is) {
		int faceCounter = 0;
		int vertexCounter = 0;
		try {
			ArrayByteReader reader = new ArrayByteReader(is);
			String line = null;
			while ((line = reader.readLine()) != null) {
				String tokens[] = line.split(" ");
				if (tokens.length > 0) {
					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							vertexCounter += 4;
							faceCounter += 2;
						} else if (tokens.length == 4) {
							vertexCounter += 3;
							faceCounter += 1;
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mObjLoaderAdapter.setVertexNumber(vertexCounter);
		mObjLoaderAdapter.setFaceNumber(faceCounter);
	}

	public void loadFromFile(ArrayByte is) {
		forwardCountFromStream(is);
		is.setPosition(0);
		try {
			ArrayByteReader reader = new ArrayByteReader(is);
			String line = null;
			while ((line = reader.readLine()) != null) {
				String tokens[] = StringUtils.split(line, " ");
				if (tokens.length > 0) {
					if ((tokens[0].length() > 0) && tokens[0].charAt(0) == 'v') {
						float v[] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						vertices.add(v);
					}

					if (tokens[0].equals("vn")) {
						float v[] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						normals.add(v);
					}

					if (tokens[0].equals("vt")) {
						float v[] = new float[2];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						texcoords.add(v);
					}

					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);
							addVertexData(tokens[4]);
							addfaceData(4);
						} else if (tokens.length == 4) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);
							addfaceData(3);
						}
					}
				}
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void forwardCount(String file) {

		int faceCounter = 0;
		int vertexCounter = 0;
		try {
			String[] lines = StringUtils.split(BaseIO.loadText(file), '\n');
			for (String line : lines) {
				String tokens[] = line.split(" ");
				if (tokens.length > 0) {
					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							vertexCounter += 4;
							faceCounter += 2;
						} else if (tokens.length == 4) {
							vertexCounter += 3;
							faceCounter += 1;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mObjLoaderAdapter.setVertexNumber(vertexCounter);
		mObjLoaderAdapter.setFaceNumber(faceCounter);

	}

	public void loadFromFile(String file) {
		forwardCount(file);
		try {
			String[] lines = StringUtils.split(BaseIO.loadText(file), '\n');
			for (String line : lines) {
				String tokens[] = StringUtils.split(line, " ");
				if (tokens.length > 0) {
					if (tokens[0].charAt(0) == 'v') {
						float v[] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						vertices.add(v);
					}

					if (tokens[0].equals("vn")) {
						float v[] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						vertices.add(v);
					}

					if (tokens[0].equals("vt")) {
						float v[] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						texcoords.add(v);
					}

					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);
							addVertexData(tokens[4]);
							addfaceData(4);
						} else if (tokens.length == 4) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);
							addfaceData(3);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
