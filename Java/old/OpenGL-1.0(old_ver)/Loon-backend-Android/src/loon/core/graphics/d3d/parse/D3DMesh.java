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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import loon.core.geom.Matrix;
import loon.core.resource.Resources;
import loon.utils.MathUtils;
import loon.utils.collection.ArrayMap;

public class D3DMesh {
	
	public static D3DMesh createSphere(int nbSlices,int nbStairs, float radius) {
		D3DMesh mesh = new D3DMesh();
		
		int triangleCount = nbSlices * (nbStairs) * 2;
		
		mesh.mVertices = FloatBuffer.allocate(D3DMesh.nbFloatPerVertex * 3 * triangleCount);
		mesh.mIndices = CharBuffer.allocate(triangleCount * 3);
		
		int indice = 0;
        for (int i = 0; i < nbStairs; i++) {
            for (int j = 0; j < nbSlices; j++) {
                float alpha = MathUtils.PI / (nbStairs) * i;
                float beta = MathUtils.PI*2 / nbSlices * j;
                float alpha1 = MathUtils.PI / (nbStairs) * ((i + 1));

                float beta1 = MathUtils.PI*2 / nbSlices * ((j + 1));
                if (j == nbSlices-1){
                	beta1 = 0.f;          
                }

                D3DVector a = new D3DVector(radius * MathUtils.sin(alpha) * MathUtils.cos(beta), radius * MathUtils.cos(alpha), radius * MathUtils.sin(alpha) * MathUtils.sin(beta));
                D3DVector b = new D3DVector(radius * MathUtils.sin(alpha) * MathUtils.cos(beta1), radius * MathUtils.cos(alpha), radius * MathUtils.sin(alpha) * MathUtils.sin(beta1));
                D3DVector c = new D3DVector(radius * MathUtils.sin(alpha1) * MathUtils.cos(beta1), radius * MathUtils.cos(alpha1), radius * MathUtils.sin(alpha1) * MathUtils.sin(beta1));
                D3DVector d = new D3DVector(radius * MathUtils.sin(alpha1) * MathUtils.cos(beta), radius * MathUtils.cos(alpha1), radius * MathUtils.sin(alpha1) * MathUtils.sin(beta));
    
                
                mesh.mVertices.put(a.get(0));
                mesh.mVertices.put(a.get(1));
                mesh.mVertices.put(a.get(2));
                
                mesh.mVertices.put(a.get(0) / radius);
                mesh.mVertices.put(a.get(1) / radius);
                mesh.mVertices.put(a.get(2) / radius);                
                
                mesh.mVertices.put((float)(nbSlices - j + 0) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 0) / (float)nbStairs);                          
                
          
                mesh.mVertices.put(b.get(0));
                mesh.mVertices.put(b.get(1));
                mesh.mVertices.put(b.get(2));
        
                mesh.mVertices.put(b.get(0)/ radius);
                mesh.mVertices.put(b.get(1)/ radius);
                mesh.mVertices.put(b.get(2)/ radius);              
                
                mesh.mVertices.put((float)(nbSlices - (j + 1)) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 0) / (float)nbStairs);
                
                mesh.mVertices.put(c.get(0));
                mesh.mVertices.put(c.get(1));
                mesh.mVertices.put(c.get(2));
                   
                //normal not computed here
                mesh.mVertices.put(c.get(0) / radius);
                mesh.mVertices.put(c.get(1) / radius);
                mesh.mVertices.put(c.get(2) / radius);
               
                mesh.mVertices.put((float)(nbSlices -(j + 1)) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 1) / (float)nbStairs);

                mesh.mVertices.put(d.get(0));
                mesh.mVertices.put(d.get(1));
                mesh.mVertices.put(d.get(2));
                
                mesh.mVertices.put(d.get(0) / radius);
                mesh.mVertices.put(d.get(1) / radius);
                mesh.mVertices.put(d.get(2) / radius);
                
                mesh.mVertices.put((float)(nbSlices - j + 0) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 1) / (float)nbStairs);
                
                mesh.mIndices.put((char)(indice+0));
                mesh.mIndices.put((char)(indice+2));
                mesh.mIndices.put((char)(indice+1));		
      		
      		
                mesh.mIndices.put((char)(indice+2));
                mesh.mIndices.put((char)(indice+0));
                mesh.mIndices.put((char)(indice+3));	               
                
                
                indice = mesh.mVertices.position() /D3DMesh.nbFloatPerVertex;
            }
            
		}
		
        mesh.mIndices.position(0);
        mesh.mVertices.position(0);
        
        
		return mesh;
	}
	
	public static ArrayMap meshCache = new ArrayMap();
	public final static int nbFloatPerVertex = 3 + 3 + 2;
	public FloatBuffer mVertices;
	public FloatBuffer mTangentsBinormals;
	public CharBuffer mIndices;

	public boolean mHasNormals;
	public boolean mHasTexCoords;
	public String mMeshName;
	public float mMeshPosition[];
	public boolean mIsBillboard;
	public boolean mIsShadowVolume;
	public boolean mIsInScreenSpace;
	public D3DIRendererElement mRendererElementInterface;

	public void init(D3DMesh mesh) {
		mVertices = FloatBuffer.allocate(mesh.mVertices.capacity());
		mIndices = CharBuffer.allocate(mesh.mIndices.capacity());
	}

	public void init(int trianglecount) {
		mVertices = FloatBuffer.allocate(trianglecount
				* D3DMesh.nbFloatPerVertex * 3);
		mIndices = CharBuffer.allocate(trianglecount * 3);
	}

	public void init(float vertices[], float normals[], float texcoords[],
			char indices[]) {
		int nbVertex = vertices.length / 3;
		mVertices = FloatBuffer.allocate(nbVertex * D3DMesh.nbFloatPerVertex);

		for (int i = 0; i < nbVertex; i++) {
			float px = vertices[i * 3];
			float py = vertices[i * 3 + 1];
			float pz = vertices[i * 3 + 2];
			float nx = 0f;
			float ny = 0f;
			float nz = 0f;
			if (normals != null) {
				nx = normals[i * 3];
				ny = normals[i * 3 + 1];
				nz = normals[i * 3 + 2];
			}
			float tx = 0f;
			float ty = 0f;
			if (texcoords != null) {
				tx = texcoords[i * 2];
				ty = texcoords[i * 2 + 1];
			}

			this.addPoint(px, py, pz, nx, ny, nz, tx, ty);
		}

		mVertices.position(0);

		mIndices = CharBuffer.allocate(indices.length);
		mIndices.put(indices);
		mIndices.position(0);

	}

	public void writeToFile(String filename) {
		D3DSerialization bean = new D3DSerialization();
		this.mVertices.position(0);
		this.mIndices.position(0);
		float vertices[] = new float[this.mVertices.capacity()];
		this.mVertices.get(vertices);
		bean.setVertices(vertices);
		char indices[] = new char[this.mIndices.capacity()];
		this.mIndices.get(indices);
		bean.setIndices(indices);
		this.mVertices.position(0);
		this.mIndices.position(0);
		bean.setMeshPostion(this.mMeshPosition);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filename);
			try {
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(bean);
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean fileExists(String filename) {
		java.io.File file = new java.io.File(filename);
		return file.exists();
	}

	public void loadFromFile(String filename) {
		D3DSerialization bean;
		try {
			ObjectInputStream ois = new ObjectInputStream(
					Resources.openResource(filename));
			try {
				bean = (D3DSerialization) ois.readObject();
				ois.close();
				ByteBuffer vbb = ByteBuffer
						.allocateDirect(bean.getVertices().length * 4);
				vbb.order(ByteOrder.nativeOrder());
				FloatBuffer mFVertexBuffer = vbb.asFloatBuffer();
				mFVertexBuffer.put(bean.getVertices());
				this.mMeshPosition = bean.getMeshPostion();
				this.mVertices = mFVertexBuffer;
				this.mVertices.position(0);
				this.mIndices = CharBuffer.wrap(bean.getIndices());
				this.mIndices.position(0);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public D3DMesh buildShadowVolume(D3DVector lightVector) {
	
		int nbface = this.mIndices.capacity() / 3;

		int trianglecount = 0;
		D3DMesh shadowMesh = new D3DMesh();

		boolean faceToLight[] = new boolean[nbface];

		D3DVector v0 = new D3DVector();
		D3DVector v1 = new D3DVector();
		D3DVector v2 = new D3DVector();
		D3DVector vm1 = new D3DVector();
		D3DVector vm2 = new D3DVector();
		D3DVector res = new D3DVector();
		D3DVector vf = new D3DVector();
		for (int i = 0; i < this.mIndices.capacity(); i = i + 3) {
			int a = (int) (mIndices.get(i));
			int b = (int) (mIndices.get(i + 1));
			int c = (int) (mIndices.get(i + 2));

			v0.setFromVertice(mVertices, a);
			v1.setFromVertice(mVertices, b);
			v2.setFromVertice(mVertices, c);

			D3DVector.sub(vm1, v1, v0);
			D3DVector.sub(vm2, v2, v0);
			D3DVector.cross(res, vm1, vm2);
			res.normalize();

			D3DVector.add(vf, v0, v1);
			D3DVector.add(vf, vf, v2);

			float scalar = D3DVector.dot(res, lightVector);

			if (scalar > 0.0) {
				faceToLight[i / 3] = true;
			} else {

				trianglecount++;
			}
		}
	
		for (int i = 0; i < this.mIndices.capacity(); i = i + 3) {
			if (faceToLight[i / 3]) {
				for (int k = 0; k < 3; k++) {
					int a = mIndices.get(i + k);
					int b = mIndices.get(i + (k + 1) % 3);

					for (int l = 0; l < this.mIndices.capacity(); l = l + 3) {
						if (!faceToLight[l / 3]) {

							for (int z = 0; z < 3; z++) {
								int a2 = mIndices.get(l + z);
								int b2 = mIndices.get(l + (z + 1) % 3);

								if (((a == a2) && (b == b2))
										|| ((a == b2) && (b == a2))) {
						
									trianglecount++;
									trianglecount++;
								}

							}

						}
					}
				}

			}

		}

		shadowMesh.init(trianglecount);

		for (int i = 0; i < this.mIndices.capacity(); i = i + 3) {
			int a = (int) (mIndices.get(i));
			int b = (int) (mIndices.get(i + 1));
			int c = (int) (mIndices.get(i + 2));

			v0.setFromVertice(mVertices, a);
			v1.setFromVertice(mVertices, b);
			v2.setFromVertice(mVertices, c);

			D3DVector.sub(vm1, v1, v0);
			D3DVector.sub(vm2, v2, v0);
			D3DVector.cross(res, vm1, vm2);
			res.normalize();

			D3DVector.add(vf, v0, v1);
			D3DVector.add(vf, vf, v2);

			float scalar = D3DVector.dot(res, lightVector);

			if (scalar > 0.0) {
				faceToLight[i / 3] = true;
			} else {

				shadowMesh.putTriangle(mVertices.get(a * nbFloatPerVertex),
						mVertices.get(a * nbFloatPerVertex + 1),
						mVertices.get(a * nbFloatPerVertex + 2),
						mVertices.get(b * nbFloatPerVertex),
						mVertices.get(b * nbFloatPerVertex + 1),
						mVertices.get(b * nbFloatPerVertex + 2),
						mVertices.get(c * nbFloatPerVertex),
						mVertices.get(c * nbFloatPerVertex + 1),
						mVertices.get(c * nbFloatPerVertex + 2));

			}
		}

		for (int i = 0; i < this.mIndices.capacity(); i = i + 3) {
			if (faceToLight[i / 3]) {
				for (int k = 0; k < 3; k++) {
					int a = mIndices.get(i + k);
					int b = mIndices.get(i + (k + 1) % 3);

					for (int l = 0; l < this.mIndices.capacity(); l = l + 3) {
						if (!faceToLight[l / 3]) {

							for (int z = 0; z < 3; z++) {
								int a2 = mIndices.get(l + z);
								int b2 = mIndices.get(l + (z + 1) % 3);

								if (((a == a2) && (b == b2))
										|| ((a == b2) && (b == a2))) {
									D3DVector lightVectP = lightVector.clone();
									lightVectP.mul(10000.f);

									D3DVector vt1 = new D3DVector();
									vt1.setFromVertice(mVertices, a2);
									D3DVector vt1p = new D3DVector();
									D3DVector.sub(vt1p, vt1, lightVectP);

									D3DVector vt2 = new D3DVector();
									vt2.setFromVertice(mVertices, b2);
									D3DVector vt2p = new D3DVector();
									D3DVector.sub(vt2p, vt2, lightVectP);

									shadowMesh
											.putTriangle(
													mVertices.get(a
															* nbFloatPerVertex),
													mVertices.get(a
															* nbFloatPerVertex
															+ 1),
													mVertices.get(a
															* nbFloatPerVertex
															+ 2),
													mVertices.get(b
															* nbFloatPerVertex),
													mVertices.get(b
															* nbFloatPerVertex
															+ 1),
													mVertices.get(b
															* nbFloatPerVertex
															+ 2), vt2p.get(0),
													vt2p.get(1), vt2p.get(2));

									shadowMesh
											.putTriangle(
													vt2p.get(0),
													vt2p.get(1),
													vt2p.get(2),
													vt1p.get(0),
													vt1p.get(1),
													vt1p.get(2),
													mVertices.get(a
															* nbFloatPerVertex),
													mVertices.get(a
															* nbFloatPerVertex
															+ 1),
													mVertices.get(a
															* nbFloatPerVertex
															+ 2));

								}

							}

						}
					}
				}

			}

		}

		shadowMesh.mVertices.position(0);
		shadowMesh.mIndices.position(0);
		return shadowMesh;
	}

	public void setTexturedQuad(float x1, float y1, float z1, float x2,
			float y2, float z2, float x3, float y3, float z3, float x4,
			float y4, float z4, float x1t, float y1t, float x2t, float y2t,
			float x3t, float y3t, float x4t, float y4t) {
		int trianglecount = 2;
		int verticecount = 4;
		init(trianglecount, verticecount);
		putTexturedQuad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, x1t,
				y1t, x2t, y2t, x3t, y3t, x4t, y4t);

	}

	private void addPoint(float px, float py, float pz, float nx, float ny,
			float nz, float tx, float ty) {
		mVertices.put(px);
		mVertices.put(py);
		mVertices.put(pz);
		mVertices.put(nx);
		mVertices.put(ny);
		mVertices.put(nz);
		mVertices.put(tx);
		mVertices.put(ty);

	}

	public void putTexturedQuad(float x1, float y1, float z1, float x2,
			float y2, float z2, float x3, float y3, float z3, float x4,
			float y4, float z4, float x1t, float y1t, float x2t, float y2t,
			float x3t, float y3t, float x4t, float y4t) {
		int indice = mVertices.position() / D3DMesh.nbFloatPerVertex;

		addPoint(x1, y1, z1, 0f, 0f, 0f, x1t, y1t);
		addPoint(x2, y2, z2, 0f, 0f, 0f, x2t, y2t);
		addPoint(x3, y3, z3, 0f, 0f, 0f, x3t, y3t);
		addPoint(x4, y4, z4, 0f, 0f, 0f, x4t, y4t);

		mIndices.put((char) (indice + 0));
		mIndices.put((char) (indice + 1));
		mIndices.put((char) (indice + 2));

		mIndices.put((char) (indice + 2));
		mIndices.put((char) (indice + 3));
		mIndices.put((char) indice);

	}

	public void addTexCoords(int indice, float xt, float yt) {
		mVertices.put(indice * D3DMesh.nbFloatPerVertex + 6, xt);
		mVertices.put(indice * D3DMesh.nbFloatPerVertex + 7, yt);
	}

	public void addNormal(int indice, float nx, float ny, float nz) {
		mVertices.put(indice * D3DMesh.nbFloatPerVertex + 3, nx);
		mVertices.put(indice * D3DMesh.nbFloatPerVertex + 4, ny);
		mVertices.put(indice * D3DMesh.nbFloatPerVertex + 5, nz);
	}

	public void addTangent(int indice, float nx, float ny, float nz) {
		mTangentsBinormals.put(indice * 6, nx);
		mTangentsBinormals.put(indice * 6 + 1, ny);
		mTangentsBinormals.put(indice * 6 + 2, nz);
	}

	public void addBinormal(int indice, float nx, float ny, float nz) {
		mTangentsBinormals.put(indice * 6 + 3, nx);
		mTangentsBinormals.put(indice * 6 + 4, ny);
		mTangentsBinormals.put(indice * 6 + 5, nz);
	}

	public float getTangent(int indice, int element) {
		return mTangentsBinormals.get(indice * 6 + element);
	}

	public float getBinormal(int indice, int element) {
		return mTangentsBinormals.get(indice * 6 + 3 + element);
	}

	public float getNormal(int indice, int element) {
		return mVertices.get(indice * D3DMesh.nbFloatPerVertex + 3 + element);
	}

	public void putTriangle(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3) {
		// System.out.println("A= "+x1+" "+y1+" "+z1);
		// System.out.println("B= "+x2+" "+y2+" "+z2);
		// System.out.println("C= "+x3+" "+y3+" "+z3);

		int indice = mVertices.position() / D3DMesh.nbFloatPerVertex;
		// int indice = mVertices.position();
		mVertices.put(x1);
		mVertices.put(y1);
		mVertices.put(z1);

		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);

		mVertices.put(x2);
		mVertices.put(y2);
		mVertices.put(z2);

		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);

		mVertices.put(x3);
		mVertices.put(y3);
		mVertices.put(z3);

		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);

		mIndices.put((char) indice);
		mIndices.put((char) (indice + 1));
		mIndices.put((char) (indice + 2));
	}


	public void setMeshPosition(float x, float y, float z) {
		if (mMeshPosition == null) {
			mMeshPosition = new float[3];
		}

		mMeshPosition[0] = x;
		mMeshPosition[1] = y;
		mMeshPosition[2] = z;
	}

	public int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	private int read16(byte buffer[], int offset) {
		int res;
		int res2;
		res = unsignedByteToInt(buffer[offset]);
		res2 = unsignedByteToInt(buffer[offset + 1]) << 8;
		res = res | res2;
		return res;
	}

	private int read32(byte buffer[], int offset) {
		int res = 0;
		res = unsignedByteToInt(buffer[offset]);
		res = res | (unsignedByteToInt(buffer[offset + 1]) << 8);
		res = res | (unsignedByteToInt(buffer[offset + 2]) << 16);
		res = res | (unsignedByteToInt(buffer[offset + 3]) << 24);
		return res;
	}

	static final String HEXES = "0123456789ABCDEF";

	public String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	private int currentcount;

	private float buffVerts[] = null;
	private char indices[] = null;
	private float texCoords[] = null;
	private static float cosd[];
	private static float sind[];

	private static int init() {
		cosd = new float[361];
		sind = new float[361];
		for (int i = 0; i < 361; i++) {
			cosd[i] = MathUtils.cos(MathUtils.toRadians(i));
			sind[i] = MathUtils.sin(MathUtils.toRadians(i));

		}
		return 0;
	}

	public static float getCos(float a) {
		if (cosd == null) {
			init();
		}
		if (a >= 0.) {
			return cosd[((int) a) % 360];
		} else
			return cosd[-((int) a % 360)];
	}

	public static float getSin(float a) {
		if (sind == null) {
			init();
		}
		if (a >= 0.) {
			return sind[((int) a) % 360];
		} else
			return -sind[-((int) a) % 360];
	}

	public static Matrix getRotationMatrix(float ax, float ay, float az) {
		float cosax = getCos(ax);
		float sinax = getSin(ax);
		float cosay = getCos(ay);
		float sinay = getSin(ay);
		float cosaz = getCos(az);
		float sinaz = getSin(az);
		float tx[] = { 1, 0, 0, 0, cosax, -sinax, 0, sinax, cosax };
		float ty[] = { cosay, 0, sinay, 0, 1.f, 0.f, -sinay, 0, cosay };
		float tz[] = { cosaz, -sinaz, 0, sinaz, cosaz, 0, 0, 0, 1 };
		Matrix Rx = new Matrix(tx);
		Matrix Ry = new Matrix(ty);
		Matrix Rz = new Matrix(tz);
		Matrix result = new Matrix();
		Matrix tmpresult = new Matrix();
		Matrix.mul(tmpresult, Rx, Ry);
		Matrix.mul(result, tmpresult, Rz);
		return result;
	}

	private int eatChunk(byte buffer[], int offset, int count, float angle_y,
			float z_adjust, float scale) {
		int chunkid;
		int chunklength;
		int i = 0;
		int j;
		chunkid = read16(buffer, i + offset);
		byte buf[] = new byte[2];
		buf[0] = buffer[i + offset];
		buf[1] = buffer[i + offset + 1];
		chunklength = read32(buffer, i + 2 + offset);
		i = 6;

		float tmpV[] = new float[3];
		float tmpRes[] = new float[3];

		Matrix m = getRotationMatrix(-90, angle_y, 0);

		float v1[] = new float[4];

		int numVerts = 0;

		switch (chunkid) {
		case 0x4D4D: 
			while ((read16(buffer, i + offset) != 0x3D3D)
					&& (read16(buffer, i + offset) != 0xB000))
				i += 2;
			break;
		case 0x3D3D: 
			break;
		case 0x4000:
			while (buffer[i + offset] != 0){
				i++; 
			}
			i++;
			currentcount++;
			break;
		case 0x4100: 
			break;
		case 0x4110: 
			if ((numVerts == 0) && (currentcount == count)) {
				numVerts = read16(buffer, i + offset);
				i += 2;
				buffVerts = new float[numVerts * 3];
				for (j = 0; j < numVerts; j++) {
					v1[0] = Float.intBitsToFloat(read32(buffer, i + offset));
					i += 4;
					v1[1] = Float.intBitsToFloat(read32(buffer, i + offset));
					i += 4;
					v1[2] = Float.intBitsToFloat(read32(buffer, i + offset));
					i += 4;
					v1[3] = 0.f;

					tmpV[0] = v1[0];
					tmpV[1] = v1[1];
					tmpV[2] = v1[2];

					Matrix.mul(tmpRes, m, tmpV);

					buffVerts[j * 3] = tmpRes[0] * scale;
					buffVerts[j * 3 + 1] = tmpRes[1] * scale;
					buffVerts[j * 3 + 2] = tmpRes[2] * scale;

					this.mMeshPosition[0] = this.mMeshPosition[0]
							+ (buffVerts[j * 3] / (float) numVerts);
					this.mMeshPosition[1] = this.mMeshPosition[1]
							+ (buffVerts[j * 3 + 1] / (float) numVerts);
					this.mMeshPosition[2] = this.mMeshPosition[2]
							+ (buffVerts[j * 3 + 2] / (float) numVerts);

				}

				for (j = 0; j < numVerts; j++) {
					buffVerts[j * 3] = buffVerts[j * 3] - this.mMeshPosition[0];
					buffVerts[j * 3 + 1] = buffVerts[j * 3 + 1]
							- this.mMeshPosition[1] + z_adjust;
					buffVerts[j * 3 + 2] = buffVerts[j * 3 + 2]
							- this.mMeshPosition[2];
				}

			} else {
				i = chunklength;
			}
			break;

		case 0x4120:

			if ((indices == null) && (currentcount == count)) {
				int numpolys = read16(buffer, i + offset);
				i += 2;
				indices = new char[numpolys * 3];
				for (j = 0; j < numpolys; j++) {
					indices[j * 3 + 2] = (char) read16(buffer, i + offset);
					i += 2;
					indices[j * 3 + 1] = (char) read16(buffer, i + offset);
					i += 2;
					indices[j * 3] = (char) read16(buffer, i + offset);
					i += 2;
					i += 2; 
				}

				this.init(buffVerts, null, texCoords, indices);
			} else
				i = chunklength;
			break;

		case 0x4140:
			if (currentcount == count) {
				int numuvmaps = read16(buffer, i + offset);
				i += 2;

				texCoords = new float[numuvmaps * 2];
				for (j = 0; j < numuvmaps; j++) {
					texCoords[j * 2] = Float.intBitsToFloat(read32(buffer, i
							+ offset));

					i += 4;
					texCoords[j * 2 + 1] = 1.f - Float.intBitsToFloat(read32(
							buffer, i + offset));
			
					i += 4;
			
				}
		
			}

			i = chunklength;

			break;

		default:
			i = chunklength; 
			break;
		}

		while (i < chunklength) {
			i += eatChunk(buffer, i + offset, count, angle_y, z_adjust, scale);
		}
		return chunklength;
	}

	public static void storeMesh(String name, D3DMesh mesh) {
		D3DMesh.meshCache.put(name, mesh);
	}

	public static D3DMesh getMesh(String name) {
		return (D3DMesh) meshCache.get(name);
	}

	public void load3ds(String filename, int count, float angle_y,
			float z_adjust, float scale) throws IOException {
		if (meshCache.get(filename + ("" + angle_y) + count) == null) {
			InputStream is = Resources.openResource(filename);
			byte buffer[] = new byte[is.available()];
			is.read(buffer);
			this.mMeshPosition = new float[3];
			currentcount = 0;
			eatChunk(buffer, 0, count, angle_y, z_adjust, scale);
			is.close();
			this.mMeshName = filename;
			this.mMeshPosition = null;
			D3DMesh.storeMesh(filename + ("" + angle_y) + count, this);

		} else {
			this.copyReference(D3DMesh.getMesh(filename + ("" + angle_y)
					+ count));
		}

	}

	public void copyReference(D3DMesh mesh) {
		mIndices = mesh.mIndices;
		mVertices = mesh.mVertices;
		mMeshPosition = mesh.mMeshPosition;
	}

	public int mergeVertexIndices() {
		int countmerge = 0;
		for (int i = 0; i < mIndices.capacity(); i++) {
			int indice = mIndices.get(i);
			for (int j = 0; j < i; j++) {
				char indice2 = mIndices.get(j);

				float x = mVertices.get(indice * D3DMesh.nbFloatPerVertex);
				float y = mVertices.get(indice * D3DMesh.nbFloatPerVertex + 1);
				float z = mVertices.get(indice * D3DMesh.nbFloatPerVertex + 2);

				float x2 = mVertices.get(indice2 * D3DMesh.nbFloatPerVertex);
				float y2 = mVertices
						.get(indice2 * D3DMesh.nbFloatPerVertex + 1);
				float z2 = mVertices
						.get(indice2 * D3DMesh.nbFloatPerVertex + 2);

				if ((x == x2) && (y == y2) && (z == z2)) {
					mIndices.put(i, indice2);
					countmerge++;
				}
			}
		}
		return countmerge;
	}

	public void mergeNormals() {
		FloatBuffer normalAccumulationBuffer = FloatBuffer.allocate(mVertices
				.capacity());
		for (int i = 0; i < mIndices.capacity(); i++) {
			int indice = mIndices.get(i);
			int offset_indice = indice * D3DMesh.nbFloatPerVertex;

			for (int j = 0; j < mIndices.capacity(); j++) {
				char indice2 = mIndices.get(j);
				int offset_indice2 = indice2 * D3DMesh.nbFloatPerVertex;
				float x = mVertices.get(indice * D3DMesh.nbFloatPerVertex);
				float y = mVertices.get(indice * D3DMesh.nbFloatPerVertex + 1);
				float z = mVertices.get(indice * D3DMesh.nbFloatPerVertex + 2);

				float x2 = mVertices.get(indice2 * D3DMesh.nbFloatPerVertex);
				float y2 = mVertices
						.get(indice2 * D3DMesh.nbFloatPerVertex + 1);
				float z2 = mVertices
						.get(indice2 * D3DMesh.nbFloatPerVertex + 2);

				if ((x == x2) && (y == y2) && (z == z2)) {

					normalAccumulationBuffer.put(offset_indice2 + 3,
							normalAccumulationBuffer.get(offset_indice2 + 3)
									+ mVertices.get(offset_indice + 3));
					normalAccumulationBuffer.put(offset_indice2 + 4,
							normalAccumulationBuffer.get(offset_indice2 + 4)
									+ mVertices.get(offset_indice + 4));
					normalAccumulationBuffer.put(offset_indice2 + 5,
							normalAccumulationBuffer.get(offset_indice2 + 5)
									+ mVertices.get(offset_indice + 5));
				}
			}
		}

		D3DVector normal = new D3DVector();
		for (int i = 0; i < mIndices.capacity(); i++) {
			int indice = mIndices.get(i);
			int offset_indice = indice * D3DMesh.nbFloatPerVertex;
			normal.set(0, normalAccumulationBuffer.get(offset_indice + 3));
			normal.set(1, normalAccumulationBuffer.get(offset_indice + 4));
			normal.set(2, normalAccumulationBuffer.get(offset_indice + 5));
			normal.normalize();
			mVertices.put(offset_indice + 3, normal.get(0));
			mVertices.put(offset_indice + 4, normal.get(1));
			mVertices.put(offset_indice + 5, normal.get(2));
		}
	}

	public void mergeTangents() {

		FloatBuffer normalAccumulationBuffer = FloatBuffer
				.allocate(mTangentsBinormals.capacity());
		for (int i = 0; i < mIndices.capacity(); i++) {
			int indice = mIndices.get(i);
			int offset_indice = indice * 6;

			for (int j = 0; j < mIndices.capacity(); j++) {
				char indice2 = mIndices.get(j);
				int offset_indice2 = indice2 * 6;
				float x = mVertices.get(indice * D3DMesh.nbFloatPerVertex);
				float y = mVertices.get(indice * D3DMesh.nbFloatPerVertex + 1);
				float z = mVertices.get(indice * D3DMesh.nbFloatPerVertex + 2);

				float x2 = mVertices.get(indice2 * D3DMesh.nbFloatPerVertex);
				float y2 = mVertices
						.get(indice2 * D3DMesh.nbFloatPerVertex + 1);
				float z2 = mVertices
						.get(indice2 * D3DMesh.nbFloatPerVertex + 2);

				if ((x == x2) && (y == y2) && (z == z2)) {
					normalAccumulationBuffer
							.put(offset_indice2 + 0,
									normalAccumulationBuffer
											.get(offset_indice2 + 0)
											+ mTangentsBinormals
													.get(offset_indice + 0));
					normalAccumulationBuffer
							.put(offset_indice2 + 1,
									normalAccumulationBuffer
											.get(offset_indice2 + 1)
											+ mTangentsBinormals
													.get(offset_indice + 1));
					normalAccumulationBuffer
							.put(offset_indice2 + 2,
									normalAccumulationBuffer
											.get(offset_indice2 + 2)
											+ mTangentsBinormals
													.get(offset_indice + 2));

					normalAccumulationBuffer
							.put(offset_indice2 + 3,
									normalAccumulationBuffer
											.get(offset_indice2 + 3)
											+ mTangentsBinormals
													.get(offset_indice + 3));
					normalAccumulationBuffer
							.put(offset_indice2 + 4,
									normalAccumulationBuffer
											.get(offset_indice2 + 4)
											+ mTangentsBinormals
													.get(offset_indice + 4));
					normalAccumulationBuffer
							.put(offset_indice2 + 5,
									normalAccumulationBuffer
											.get(offset_indice2 + 5)
											+ mTangentsBinormals
													.get(offset_indice + 5));
				}
			}
		}

		D3DVector normal = new D3DVector();
		for (int i = 0; i < mIndices.capacity(); i++) {
			int indice = mIndices.get(i);
			int offset_indice = indice * 6;
			normal.set(0, normalAccumulationBuffer.get(offset_indice + 0));
			normal.set(1, normalAccumulationBuffer.get(offset_indice + 1));
			normal.set(2, normalAccumulationBuffer.get(offset_indice + 2));
			normal.normalize();
			mTangentsBinormals.put(offset_indice + 0, normal.get(0));
			mTangentsBinormals.put(offset_indice + 1, normal.get(1));
			mTangentsBinormals.put(offset_indice + 2, normal.get(2));

			normal.set(0, normalAccumulationBuffer.get(offset_indice + 3));
			normal.set(1, normalAccumulationBuffer.get(offset_indice + 4));
			normal.set(2, normalAccumulationBuffer.get(offset_indice + 5));
			normal.normalize();
			mTangentsBinormals.put(offset_indice + 3, normal.get(0));
			mTangentsBinormals.put(offset_indice + 4, normal.get(1));
			mTangentsBinormals.put(offset_indice + 5, normal.get(2));

		}
	}

	public void copy(D3DMesh mesh) {
		mIndices = CharBuffer.allocate(mesh.mIndices.position());
		mVertices = FloatBuffer.allocate(mesh.mVertices.position());

		for (int j = 0; j < mIndices.capacity(); j++)
			mIndices.put(mesh.mIndices.get(j));

		for (int j = 0; j < mVertices.capacity(); j++) {
			mVertices.put(mesh.mVertices.get(j));
		}

		mIndices.position(0);
		mVertices.position(0);
	}

	public void init(int triangleCount, int verticeCount) {
		mIndices = CharBuffer.allocate(triangleCount * 3);
		mVertices = FloatBuffer.allocate(verticeCount * 3);
	}

	public void generateNormalsPerVertex() {
		D3DVector res = new D3DVector();
		D3DVector vm1 = new D3DVector();
		D3DVector vm2 = new D3DVector();
		D3DVector v0 = new D3DVector();
		D3DVector v1 = new D3DVector();
		D3DVector v2 = new D3DVector();

		int trianglecount = this.mIndices.capacity() / 3;
		FloatBuffer normals = FloatBuffer.allocate(trianglecount * 3);

		this.mIndices.position(0);
		for (int i = 0; i < trianglecount; i++) {
			int a, b, c;
			a = this.mIndices.get();
			b = this.mIndices.get();
			c = this.mIndices.get();

			v0.setFromVertice(this.mVertices, a);
			v1.setFromVertice(this.mVertices, b);
			v2.setFromVertice(this.mVertices, c);

			D3DVector.sub(vm1, v1, v0);
			D3DVector.sub(vm2, v2, v0);
			D3DVector.cross(res, vm1, vm2);

			res.normalize();

			normals.put(res.get(0));
			normals.put(res.get(1));
			normals.put(res.get(2));

		}

		for (int i = 0; i < this.mVertices.capacity() / 3; i++) {
			float xv, yv, zv;
			int count = 0;
			xv = this.mVertices.get(i * 3);
			yv = this.mVertices.get(i * 3 + 1);
			zv = this.mVertices.get(i * 3 + 2);

			addNormal(i, 0f, 0f, 0f);
			for (int k = 0; k < trianglecount; k++) {
				int a, b, c;
				a = this.mIndices.get(k * 3);
				b = this.mIndices.get(k * 3 + 1);
				c = this.mIndices.get(k * 3 + 2);

				float x, y, z;
				x = this.mVertices.get(a * 3);
				y = this.mVertices.get(a * 3 + 1);
				z = this.mVertices.get(a * 3 + 2);

				float xnormal, ynormal, znormal;
				xnormal = normals.get(k * 3);
				ynormal = normals.get(k * 3 + 1);
				znormal = normals.get(k * 3 + 2);

				if ((x == xv) && (y == yv) && (z == zv)) {

					count++;

					this.addNormal(i, xnormal + getNormal(i, 0), ynormal
							+ getNormal(i, 1), znormal + getNormal(i, 2));

				}

				x = this.mVertices.get(b * 3);
				y = this.mVertices.get(b * 3 + 1);
				z = this.mVertices.get(b * 3 + 2);

				if ((x == xv) && (y == yv) && (z == zv)) {

					count++;
					this.addNormal(i, xnormal + getNormal(i, 0), ynormal
							+ getNormal(i, 1), znormal + getNormal(i, 2));
				}

				x = this.mVertices.get(c * 3);
				y = this.mVertices.get(c * 3 + 1);
				z = this.mVertices.get(c * 3 + 2);

				if ((x == xv) && (y == yv) && (z == zv)) {

					count++;

					this.addNormal(i, xnormal + getNormal(i, 0), ynormal
							+ getNormal(i, 1), znormal + getNormal(i, 2));
				}
			}

			if (count > 0) {
				this.addNormal(i, getNormal(i, 0) / count, getNormal(i, 1)
						/ count, getNormal(i, 2) / count);
			}

		}

		this.mIndices.position(0);
		this.mVertices.position(0);
	}

	public void generateNormals() {
		D3DVector res = new D3DVector();
		D3DVector vm1 = new D3DVector();
		D3DVector vm2 = new D3DVector();
		D3DVector v0 = new D3DVector();
		D3DVector v1 = new D3DVector();
		D3DVector v2 = new D3DVector();

		int trianglecount = this.mIndices.capacity() / 3;

		this.mIndices.position(0);
		for (int i = 0; i < trianglecount; i++) {
			int a, b, c;
			a = this.mIndices.get();
			b = this.mIndices.get();
			c = this.mIndices.get();

			v0.setFromVertice(this.mVertices, a);
			v1.setFromVertice(this.mVertices, b);
			v2.setFromVertice(this.mVertices, c);

			D3DVector.sub(vm1, v1, v0);
			D3DVector.sub(vm2, v2, v0);
			D3DVector.cross(res, vm1, vm2);

			res.normalize();

			this.addNormal(a, getNormal(a, 0) + res.get(0), getNormal(a, 1)
					+ res.get(1), getNormal(a, 2) + res.get(2));

			D3DVector.sub(vm1, v2, v1);
			D3DVector.sub(vm2, v0, v1);
			D3DVector.cross(res, vm1, vm2);
			res.normalize();

			this.addNormal(b, getNormal(b, 0) + res.get(0), getNormal(b, 1)
					+ res.get(1), getNormal(b, 2) + res.get(2));

			D3DVector.sub(vm1, v0, v2);
			D3DVector.sub(vm2, v1, v2);
			D3DVector.cross(res, vm1, vm2);
			res.normalize();

			this.addNormal(c, getNormal(c, 0) + res.get(0), getNormal(c, 1)
					+ res.get(1), getNormal(c, 2) + res.get(2));
		}

		D3DVector normal = new D3DVector();
		for (int i = 0; i < this.mVertices.capacity()
				/ D3DMesh.nbFloatPerVertex; i++) {
			normal.set(0, this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 3));
			normal.set(1, this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 4));
			normal.set(2, this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 5));
			normal.normalize();
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 3, normal.get(0));
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 4, normal.get(1));
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 5, normal.get(2));
		}
		this.mIndices.position(0);
		this.mVertices.position(0);
	}

	public void generateNormalsTangentsBinormals() {

		this.mTangentsBinormals = FloatBuffer.allocate(this.mVertices
				.capacity() / D3DMesh.nbFloatPerVertex * 6);

		int trianglecount = this.mIndices.capacity() / 3;
		this.mIndices.position(0);

		D3DVector normal = new D3DVector();
		D3DVector tangent = new D3DVector();
		D3DVector binormal = new D3DVector();
		D3DVector2f texCoords0 = new D3DVector2f();
		D3DVector2f texCoords1 = new D3DVector2f();
		D3DVector2f texCoords2 = new D3DVector2f();
		D3DVector vm1 = new D3DVector();
		D3DVector vm2 = new D3DVector();
		D3DVector2f t1 = new D3DVector2f();
		D3DVector2f t2 = new D3DVector2f();
		D3DVector v0 = new D3DVector();
		D3DVector v1 = new D3DVector();
		D3DVector v2 = new D3DVector();
		float coef;
		for (int i = 0; i < trianglecount; i++) {
			int a, b, c;
			a = this.mIndices.get();
			b = this.mIndices.get();
			c = this.mIndices.get();

			v0.setFromVertice(this.mVertices, a);
			v1.setFromVertice(this.mVertices, b);
			v2.setFromVertice(this.mVertices, c);

			texCoords0.setFromTexCoords(this.mVertices, a);
			texCoords1.setFromTexCoords(this.mVertices, b);
			texCoords2.setFromTexCoords(this.mVertices, c);

			D3DVector.sub(vm1, v1, v0);
			D3DVector.sub(vm2, v2, v0);

			D3DVector2f.sub(t1, texCoords1, texCoords0);
			D3DVector2f.sub(t2, texCoords2, texCoords0);

			vm1.normalize();
			vm2.normalize();
			t1.normalize();
			t2.normalize();

			coef = 1 / (t1.getX() * t2.getY() - t2.getX() * t1.getY());

			tangent.set(0,
					coef * (vm1.get(0) * t2.getY() + vm2.get(0) * -t1.getY()));
			tangent.set(1,
					coef * (vm1.get(1) * t2.getY() + vm2.get(1) * -t1.getY()));
			tangent.set(2,
					coef * (vm1.get(2) * t2.getY() + vm2.get(2) * -t1.getY()));

			tangent.normalize();

			normal.setFromNormal(this.mVertices, a);
			D3DVector.cross(binormal, normal, tangent);
			binormal.normalize();

			addTangent(a, getTangent(a, 0) + tangent.get(0), getTangent(a, 1)
					+ tangent.get(1), getTangent(a, 2) + tangent.get(2));
			addBinormal(a, getBinormal(a, 0) + binormal.get(0),
					getBinormal(a, 1) + binormal.get(1), getBinormal(a, 2)
							+ binormal.get(2));

			D3DVector.sub(vm1, v2, v1);
			D3DVector.sub(vm2, v0, v1);

			D3DVector2f.sub(t1, texCoords2, texCoords1);
			D3DVector2f.sub(t2, texCoords0, texCoords1);

			vm1.normalize();
			vm2.normalize();
			t1.normalize();
			t2.normalize();

			coef = 1 / (t1.getX() * t2.getY() - t2.getX() * t1.getY());

			tangent.set(0,
					coef * (vm1.get(0) * t2.getY() + vm2.get(0) * -t1.getY()));
			tangent.set(1,
					coef * (vm1.get(1) * t2.getY() + vm2.get(1) * -t1.getY()));
			tangent.set(2,
					coef * (vm1.get(2) * t2.getY() + vm2.get(2) * -t1.getY()));

			tangent.normalize();

			normal.setFromNormal(this.mVertices, b);
			D3DVector.cross(binormal, normal, tangent);
			binormal.normalize();

			addTangent(b, getTangent(b, 0) + tangent.get(0), getTangent(b, 1)
					+ tangent.get(1), getTangent(b, 2) + tangent.get(2));
			addBinormal(b, getBinormal(b, 0) + binormal.get(0),
					getBinormal(b, 1) + binormal.get(1), getBinormal(b, 2)
							+ binormal.get(2));

			D3DVector.sub(vm1, v0, v2);
			D3DVector.sub(vm2, v1, v2);

			D3DVector2f.sub(t1, texCoords0, texCoords2);
			D3DVector2f.sub(t2, texCoords1, texCoords2);

			vm1.normalize();
			vm2.normalize();
			t1.normalize();
			t2.normalize();

			coef = 1 / (t1.getX() * t2.getY() - t2.getX() * t1.getY());

			tangent.set(0,
					coef * (vm1.get(0) * t2.getY() + vm2.get(0) * -t1.getY()));
			tangent.set(1,
					coef * (vm1.get(1) * t2.getY() + vm2.get(1) * -t1.getY()));
			tangent.set(2,
					coef * (vm1.get(2) * t2.getY() + vm2.get(2) * -t1.getY()));

			tangent.normalize();

			normal.setFromNormal(this.mVertices, c);
			D3DVector.cross(binormal, normal, tangent);
			binormal.normalize();

			addTangent(c, getTangent(c, 0) + tangent.get(0), getTangent(c, 1)
					+ tangent.get(1), getTangent(c, 2) + tangent.get(2));
			addBinormal(c, getBinormal(c, 0) + binormal.get(0),
					getBinormal(c, 1) + binormal.get(1), getBinormal(c, 2)
							+ binormal.get(2));
		}

		D3DVector tmp = new D3DVector();
		for (int i = 0; i < this.mTangentsBinormals.capacity() / 6; i++) {
			tmp.set(0, this.mTangentsBinormals.get(i * 6));
			tmp.set(1, this.mTangentsBinormals.get(i * 6 + 1));
			tmp.set(2, this.mTangentsBinormals.get(i * 6 + 2));
			tmp.normalize();
			this.mTangentsBinormals.put(i * 6 + 0, tmp.get(0));
			this.mTangentsBinormals.put(i * 6 + 1, tmp.get(1));
			this.mTangentsBinormals.put(i * 6 + 2, tmp.get(2));

			tmp.set(0, this.mTangentsBinormals.get(i * 6 + 3));
			tmp.set(1, this.mTangentsBinormals.get(i * 6 + 4));
			tmp.set(2, this.mTangentsBinormals.get(i * 6 + 5));
			tmp.normalize();
			this.mTangentsBinormals.put(i * 6 + 3, tmp.get(0));
			this.mTangentsBinormals.put(i * 6 + 4, tmp.get(1));
			this.mTangentsBinormals.put(i * 6 + 5, tmp.get(2));
		}

		mergeTangents();

		this.mIndices.position(0);
		this.mTangentsBinormals.position(0);
	}

	public void applyMatrix(Matrix matrix) {
		float position[] = new float[3];
		float normal[] = new float[3];
		float result[] = new float[3];
		int nbVert = this.mVertices.capacity() / D3DMesh.nbFloatPerVertex;

		for (int i = 0; i < nbVert; i++) {
			position[0] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 0);
			position[1] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 1);
			position[2] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 2);

			normal[0] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 3);
			normal[1] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 4);
			normal[2] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 5);

			Matrix.mul(result, matrix, position);

			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 0, result[0]);
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 1, result[1]);
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 2, result[2]);

			Matrix.mul(result, matrix, normal);

			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 3, result[0]);
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 4, result[1]);
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 5, result[2]);
		}
	}

	public void applyVector(float v[]) {
		int nbVert = this.mVertices.capacity() / D3DMesh.nbFloatPerVertex;
		float position[] = new float[3];
		for (int i = 0; i < nbVert; i++) {
			position[0] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 0);
			position[1] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 1);
			position[2] = this.mVertices.get(i * D3DMesh.nbFloatPerVertex + 2);

			position[0] += v[0];
			position[1] += v[1];
			position[2] += v[2];

			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 0, position[0]);
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 1, position[1]);
			this.mVertices.put(i * D3DMesh.nbFloatPerVertex + 2, position[2]);
		}

	}

}