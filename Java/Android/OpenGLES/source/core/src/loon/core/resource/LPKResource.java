package loon.core.resource;

import java.io.InputStream;
import java.util.HashMap;

import loon.core.graphics.LImage;
import loon.core.graphics.opengl.LTexture;
import loon.jni.NativeSupport;
import loon.utils.CollectionUtils;
import loon.utils.collection.ArrayByte;

/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
public abstract class LPKResource {

	//如果此项为true，已加载数据会自动缓存。
	public static boolean CACHE = false;

	private static HashMap<String, PAK> pakRes = new HashMap<String, PAK>(
			CollectionUtils.INITIAL_CAPACITY);

	private static HashMap<String, ArrayByte> cacheRes;

	static public class PAK {

		public LPKTable[] tables;

		public int head_size = 0;

		public int skip;

		public int length;

	}

	public static void FreeCache() {
		if (cacheRes != null) {
			cacheRes.clear();
		}
	}

	public static HashMap<String, PAK> MAP() {
		return pakRes;
	}

	/**
	 * 查找指定资源包中的指定资源文件并返回为Byte[]
	 * 
	 * @param fileName
	 * @param resName
	 * @return
	 */
	public static byte[] openResource(String fileName, String resName) {
		try {
			PAK pak = pakRes.get(fileName);
			InputStream ins = Resources.openResource(fileName);
			ArrayByte result = null;

			if (CACHE) {
				if (cacheRes == null) {
					cacheRes = new HashMap<String, ArrayByte>(
							CollectionUtils.INITIAL_CAPACITY);
				}
				result = cacheRes.get(fileName);
				if (result == null) {
					result = new ArrayByte(ins, ArrayByte.LITTLE_ENDIAN);
					cacheRes.put(fileName, result);
				} else {
					result.reset(ArrayByte.LITTLE_ENDIAN);
				}
			} else {
				result = new ArrayByte(ins, ArrayByte.LITTLE_ENDIAN);
			}

			if (pak == null) {
				pak = new PAK();
				LPKHeader header = readHeader(result);
				pak.tables = readLPKTable(result, (int) header.getTables());
				pak.head_size = (int) (LPKHeader.size() + header.getTables()
						* LPKTable.size());
				pak.skip = result.position();
				pak.length = result.length();
				pakRes.put(fileName, pak);
			} else {
				result.setPosition(pak.skip);
			}

			boolean find = false;
			int fileIndex = 0;
			String innerName = null;
			LPKTable[] tables = pak.tables;
			final int size = tables.length;

			for (int i = 0; i < size; i++) {
				innerName = tables[i].getFileName();
				if (resName.equalsIgnoreCase(innerName)) {
					find = true;
					fileIndex = i;
					break;
				}
			}

			if (!find) {
				throw new RuntimeException("File not found. ( " + fileName
						+ " )");
			} else {
				return readFileFromPak(result, pak.head_size, tables[fileIndex]);
			}
		} catch (Exception e) {
			throw new RuntimeException("File not found. ( " + fileName + " )");
		}
	}

	/**
	 * 返回指定资源文件中的指定资源为LImage
	 * 
	 * @param fileName
	 * @param resName
	 * @return
	 */
	public static LImage openImage(String fileName, String resName) {
		byte[] buffer = null;
		try {
			buffer = LPKResource.openResource(fileName, resName);
			return LImage.createImage(buffer);
		} catch (Exception e) {
			throw new RuntimeException("File not found. ( " + resName + " )");
		}
	}

	/**
	 * 返回LTexture
	 * 
	 * @param fileName
	 * @param resName
	 * @return
	 */
	public static LTexture openTexture(String fileName, String resName) {
		try {
			LImage image = openImage(fileName, resName);
			image.setAutoDispose(true);
			return image.getTexture();
		} catch (Exception e) {
			throw new RuntimeException("File not found. ( " + resName + " )");
		}
	}

	/**
	 * 读取头文件
	 * 
	 * @param dis
	 * @return
	 * @throws Exception
	 */
	public static LPKHeader readHeader(ArrayByte dis) throws Exception {
		LPKHeader header = new LPKHeader();
		header.setPAKIdentity(dis.readInt());
		byte[] pass = dis.readByteArray(LPKHeader.LF_PASSWORD_LENGTH);
		header.setPassword(pass);
		header.setVersion(dis.readFloat());
		header.setTables(dis.readLong());
		return header;
	}

	/**
	 * 读取文件列表
	 * 
	 * @param dis
	 * @param fileTableNumber
	 * @return
	 * @throws Exception
	 */
	public static LPKTable[] readLPKTable(ArrayByte dis, int fileTableNumber)
			throws Exception {
		LPKTable[] fileTable = new LPKTable[fileTableNumber];
		for (int i = 0; i < fileTableNumber; i++) {
			LPKTable ft = new LPKTable();
			ft.setFileName(dis.readByteArray(LPKHeader.LF_FILE_LENGTH));
			ft.setFileSize(dis.readLong());
			ft.setOffSet(dis.readLong());
			fileTable[i] = ft;
		}
		return fileTable;
	}

	/**
	 * 读取数据流
	 * 
	 * @param dis
	 * @param header
	 * @param fileTable
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFileFromPak(ArrayByte dis, int size,
			LPKTable fileTable) throws Exception {
		dis.skip(fileTable.getOffSet() - size);
		int fileLength = (int) fileTable.getFileSize();
		byte[] fileBuff = new byte[fileLength];
		int readLength = dis.read(fileBuff, 0, fileLength);
		if (readLength < fileLength) {
			return null;
		} else {
			NativeSupport.makeBuffer(fileBuff, readLength, 0xF7);
			return fileBuff;
		}
	}

}
