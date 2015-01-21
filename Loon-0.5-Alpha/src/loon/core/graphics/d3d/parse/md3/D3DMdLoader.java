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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class D3DMdLoader {
	public static String TAG_TORSO = "tag_torso";
	public static String TAG_HEAD = "tag_head";
	private float scale = 0.1f;
	private static String skinFiles[] = { "head_default.skin",
			"lower_default.skin", "upper_default.skin" };
	private TreeMap<Integer, Method> map = new TreeMap<Integer, Method>();
	private HashMap<String, String> skinMap = new HashMap<String, String>();

	private int unsignedByteToInt(byte b) {
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

	private int signedread16(byte buffer[], int offset) {
		int res = read16(buffer, offset);

		if (res > 0x7fff) {
			res = -(0xffff - res);
		}
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

	private String getString(byte[] input) {
		String s = "";
		try {
			s = new String(input, "ASCII");
			if (s.indexOf(0) != -1) {
				s = s.substring(0, s.indexOf(0));
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error while decoding array " + e.toString());
		}

		return s;
	}

	private int getInt(byte[] input) {
		int v = 0;
		v = read32(input, 0);
		return v;
	}

	private void readData(InputStream is, byte[] arg) {
		try {
			for (int i = 0; i < arg.length; i++) {
				arg[i] = (byte) is.read();
			}
			readCount += arg.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void skip(InputStream is, int count) {
		for (int i = 0; i < count; i++) {
			readData(is, read8);
		}
	}

	private void loadHeader(InputStream input, D3DIMdLoaderAdapter md3loader) {
		readCount = 0;

		// Read magic
		readData(input, read32);

		// Should be IDP3
		System.out.println("MAGIC:" + getString(read32));

		// Read version
		readData(input, read32);
		System.out.println("VERSION:" + getInt(read32));

		// Read name
		readData(input, readName);
		System.out.println("NAME:" + getString(readName));

		// Read flags
		readData(input, read32);

		// Read frames number
		readData(input, read32);
		System.out.println("FRAME NUMBER:" + getInt(read32));
		frameNumber = getInt(read32);
		md3loader.setFrameNumberHeader(frameNumber);

		// Read tags
		readData(input, read32);
		System.out.println("TAGS NUMBER:" + getInt(read32));
		tagNumber = getInt(read32);
		md3loader.setTagNumber(tagNumber);

		// Read surfaces
		readData(input, read32);
		System.out.println("SURFACES NUMBER:" + getInt(read32));
		surfaceNumber = getInt(read32);
		md3loader.setSurfaceNumber(surfaceNumber);

		// Read skins
		readData(input, read32);
		System.out.println("SKINS NUMBER:" + getInt(read32));

		// Read offset frames
		readData(input, read32);
		System.out.println("Frame Offset:" + getInt(read32));

		frameOffset = getInt(read32);

		// Read offset tags
		readData(input, read32);
		System.out.println("tags Offset:" + getInt(read32));
		tagOffset = getInt(read32);

		// Read offset surfaces
		readData(input, read32);
		System.out.println("Surface Offset:" + getInt(read32));

		surfaceOffset = getInt(read32);

		// Skipping following data to frame data
		this.skip(input, frameOffset - readCount);

		this.skip(input, tagOffset - readCount);
		loadTags(input, md3loader);

	}

	private void loadFrame(InputStream input) {
		this.skip(input, surfaceOffset - readCount);
	}

	private void loadTags(InputStream input, D3DIMdLoaderAdapter md3loader) {
		for (int k = 0; k < frameNumber; k++) {
			for (int i = 0; i < tagNumber; i++) {
				readData(input, readName);
				System.out.println("NAME:" + getString(readName));

				readData(input, read32);
				float x = Float.intBitsToFloat(getInt(read32));

				readData(input, read32);
				float y = Float.intBitsToFloat(getInt(read32));

				readData(input, read32);
				float z = Float.intBitsToFloat(getInt(read32));

				for (int j = 0; j < 9; j++) {
					readData(input, read32);
					rotMatrix[j] = Float.intBitsToFloat(getInt(read32));
				}

				md3loader.addTag(k, i, getString(readName), scale * x, scale
						* y, scale * z, rotMatrix);
			}
		}
	}

	private void loadSurface(InputStream input, D3DIMdLoaderAdapter md3loader) {

		for (int surfaceId = 0; surfaceId < this.surfaceNumber; surfaceId++) {
			System.out.println("Loading Surface " + surfaceId);

			int surfaceStart = readCount;

			readData(input, read32);
			System.out.println("MAGIC SURFACE:" + getString(read32));

			// Read name
			readData(input, readName);

			System.out.println("NAME:" + getString(readName));
			md3loader.setSurfaceName(surfaceId, getString(readName));

			// Read flags
			readData(input, read32);

			// Read frames number
			readData(input, read32);
			System.out.println("FRAME NUMBER:" + getInt(read32));
			frameNumber = getInt(read32);
			md3loader.setFrameNumber(surfaceId, frameNumber);

			// Read shaders number
			readData(input, read32);
			System.out.println("SHADER NUMBER:" + getInt(read32));
			shaderNumber = getInt(read32);
			md3loader.setShaderNumber(surfaceId, shaderNumber);

			// Read vertices number
			readData(input, read32);
			System.out.println("VERTICES NUMBER:" + getInt(read32));
			verticesNumber = getInt(read32);
			md3loader.setVerticeNumber(surfaceId, verticesNumber);

			// Read triangles number
			readData(input, read32);
			System.out.println("TRIANGLES NUMBER:" + getInt(read32));
			triangleNumber = getInt(read32);
			md3loader.setTriangleNumber(surfaceId, triangleNumber);

			// Read triangle offset
			readData(input, read32);
			System.out.println("TRIANGLES OFFSET:" + getInt(read32));
			triangleOffset = getInt(read32) + surfaceStart;

			// Read shader offset
			readData(input, read32);
			System.out.println("Shader OFFSET:" + getInt(read32));
			shaderOffset = getInt(read32) + surfaceStart;

			// Read st offset
			readData(input, read32);
			System.out.println("ST OFFSET:" + getInt(read32));
			stOffset = getInt(read32) + surfaceStart;

			// Read xyz normal offset
			readData(input, read32);
			System.out.println("XYZ NORMAL OFFSET:" + getInt(read32));
			xyzNormalOffset = getInt(read32) + surfaceStart;

			// Read end surface offset
			readData(input, read32);
			System.out.println("END OFFSET:" + getInt(read32));
			endSurfaceOffset = getInt(read32) + surfaceStart;

			this.map.clear();

			try {
				Method loadTriangleMethod = this.getClass().getMethod(
						"loadTriangles", InputStream.class,
						D3DIMdLoaderAdapter.class, int.class);
				Method loadShader = this.getClass()
						.getMethod("loadShader", InputStream.class,
								D3DIMdLoaderAdapter.class, int.class);
				Method loadSt = this.getClass()
						.getMethod("loadSt", InputStream.class,
								D3DIMdLoaderAdapter.class, int.class);

				this.map.put(triangleOffset, loadTriangleMethod);
				this.map.put(shaderOffset, loadShader);
				this.map.put(stOffset, loadSt);

				for (Entry<?, ?> e : this.map.entrySet()) {
					Method m = (Method) e.getValue();

					Integer i = (Integer) e.getKey();
					skip(input, i - readCount);
					try {
						m.invoke(this, input, md3loader, surfaceId);
					} catch (IllegalArgumentException e1) {

						e1.printStackTrace();
					} catch (IllegalAccessException e1) {

						e1.printStackTrace();
					} catch (InvocationTargetException e1) {

						e1.printStackTrace();
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}

			this.skip(input, xyzNormalOffset - readCount);

			for (int frameId = 0; frameId < frameNumber; frameId++)
				loadXYZNormal(input, md3loader, surfaceId, frameId);

			this.skip(input, endSurfaceOffset - readCount);
		}
	}

	public void loadShader(InputStream input, D3DIMdLoaderAdapter md3loader,
			int surfaceId) {
		for (int i = 0; i < shaderNumber; i++) {
			readData(input, readName);
			readData(input, read32);

			md3loader.setShader(surfaceId, getString(readName));
		}
	}

	public void loadTriangles(InputStream input, D3DIMdLoaderAdapter md3loader,
			int surfaceId) {
		for (int i = 0; i < triangleNumber; i++) {

			readData(input, read32);
			int a = getInt(read32);

			readData(input, read32);
			int b = getInt(read32);

			readData(input, read32);
			int c = getInt(read32);

			md3loader.addTriangleIndices(surfaceId, i, a, b, c);
		}
	}

	public void loadSt(InputStream input, D3DIMdLoaderAdapter md3loader,
			int surfaceId) {
		for (int i = 0; i < verticesNumber; i++) {
			readData(input, read32);
			float u = Float.intBitsToFloat(getInt(read32));
			readData(input, read32);
			float v = Float.intBitsToFloat(getInt(read32));

			md3loader.addTexCoords(surfaceId, i, u, v);
		}
	}

	private float extractFloat(byte[] data, float factor) {
		int val = signedread16(read16, 0);
		// System.out.println("VAL="+(int)((char)val));
		return factor * (float) val;
	}

	private float cos(float v) {
		return (float) java.lang.Math.cos((double) v);
	}

	private float sin(float v) {
		return (float) java.lang.Math.sin((double) v);
	}

	private void loadXYZNormal(InputStream input,
			D3DIMdLoaderAdapter md3loader, int surfaceId, int frameId) {
		float factor = 1.0f / 64.0f;
		float x;
		float y;
		float z;
		float xn;
		float yn;
		float zn;
		for (int i = 0; i < verticesNumber; i++) {
			readData(input, read16);
			x = extractFloat(read16, factor);
			readData(input, read16);
			y = extractFloat(read16, factor);
			readData(input, read16);
			z = extractFloat(read16, factor);

			readData(input, read8);
			int zenith = unsignedByteToInt(read8[0]);
			readData(input, read8);
			int azimuth = unsignedByteToInt(read8[0]);
			float lat = (float) zenith * (2 * (float) java.lang.Math.PI)
					/ 255.0f;
			float lng = (float) azimuth * (2 * (float) java.lang.Math.PI)
					/ 255.0f;

			xn = cos(lng) * sin(lat);
			yn = sin(lng) * sin(lat);
			zn = cos(lat);

			md3loader.addVertexCoords(surfaceId, frameId, i, x * scale, y
					* scale, z * scale);
			md3loader.addNormalCoords(surfaceId, frameId, i, xn, yn, zn);
		}
	}

	private byte readName[] = new byte[64];
	private byte read32[] = new byte[4];
	private byte read16[] = new byte[2];
	private byte read8[] = new byte[1];
	private float rotMatrix[] = new float[9];
	private int readCount = 0;

	private int frameOffset = 0;
	private int surfaceOffset = 0;
	private int surfaceNumber = 0;
	private int tagOffset = 0;
	private int tagNumber = 0;

	private int frameNumber = 0;
	private int verticesNumber = 0;
	private int triangleNumber = 0;
	private int triangleOffset = 0;
	private int stOffset = 0;
	private int xyzNormalOffset = 0;
	private int endSurfaceOffset = 0;
	private int shaderNumber = 0;
	private int shaderOffset = 0;

	public void loadMD3(String filePath, D3DIMdLoaderAdapter md3loader) {
		File file = new File(filePath);
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			loadHeader(input, md3loader);
			loadFrame(input);
			loadSurface(input, md3loader);

			input.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadMD3FromPK3(String filePath, D3DIMdLoaderAdapter md3loader) {
		File file = new File(filePath);
		InputStream input;
		try {

			input = new BufferedInputStream(new FileInputStream(file));
			loadMD3FromPK3(input, md3loader);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void loadMD3FromPK3(InputStream input, D3DIMdLoaderAdapter md3loader) {
		try {

			ZipInputStream zis = new ZipInputStream(input);
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null) {

				if (e.getName().toUpperCase().contains("UPPER.MD3")
						|| e.getName().toUpperCase().contains("LOWER.MD3")
						|| e.getName().toUpperCase().contains("HEAD.MD3"))
					if (e.getName().toUpperCase().endsWith("MD3")) {
						md3loader.setMd3FileName(e.getName());
						System.out.println("Loading: " + e.getName());
						loadHeader(zis, md3loader);
						loadFrame(zis);
						loadSurface(zis, md3loader);
						zis.closeEntry();
					}

			}

		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	public void loadSkinDataFromPk3(InputStream input) {

	}

	public HashMap<String, String> loadSkinData(InputStream input) {
		try {
			ZipInputStream zis = new ZipInputStream(input);
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null) {

				boolean read = false;
				for (int i = 0; i < skinFiles.length; i++) {
					if (e.getName().endsWith(skinFiles[i]))
						read = true;
				}

				if (read) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(zis));
					String strLine;
					while ((strLine = br.readLine()) != null) {
						String skindata[] = strLine.split(",");
						if (skindata.length == 2) {
							skinMap.put(skindata[0], skindata[1]);
						}
					}

					zis.closeEntry();
				}

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return this.skinMap;
	}

}