package org.loon.framework.android.game.core.resource;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.graphics.Bitmap;


/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class LPKResource {

	/**
	 * 查找指定资源包中的指定资源文件并返回为Byte[]
	 * 
	 * @param fileName
	 * @param resName
	 * @return
	 */
	public static byte[] openResource(String fileName, String resName) {
		InputStream in = null;
		DataInputStream dis = null;
		try {
			in = Resources.getResourceAsStream(fileName);
			dis = new DataInputStream(in);
			LPKHeader header = readHeader(dis);
			LPKTable[] fileTable = readLPKTable(dis, (int) header.getTables());
			boolean find = false;
			int fileIndex = 0;
			for (int i = 0; i < fileTable.length; i++) {
				String innerName = new String(fileTable[i].getFileName())
						.trim();
				if (innerName.equals(resName)) {
					find = true;
					fileIndex = i;
					break;
				}
			}
			if (find == false) {
				throw new RuntimeException("File not found. ( " + fileName
						+ " )");
			} else {
				byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
				return buff;
			}
		} catch (Exception e) {
			throw new RuntimeException("File not found. ( " + fileName + " )");
		} finally {
			if (dis != null) {
				try {
					dis.close();
					dis = null;
				} catch (IOException e) {
				}

			}
		}
	}

	/**
	 * 返回LPK文件信息
	 * 
	 * @param pakFilePath
	 * @return
	 * @throws Exception
	 */
	public static List<Object> getLPKInfo(String resName) throws Exception {
		InputStream in = Resources.getResourceAsStream(resName);
		DataInputStream dis = new DataInputStream(in);
		LPKHeader header = readHeader(dis);
		LPKTable[] fileTable = readLPKTable(dis, (int) header.getTables());
		List<Object> result = new ArrayList<Object>();
		result.add(header);
		result.add(fileTable);
		return result;
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
	
	public static LImage open8888Image(String fileName, String resName) {
		byte[] buffer = null;
		try {
			buffer = LPKResource.openResource(fileName, resName);
			return GraphicsUtils.load8888Image(buffer);
		} catch (Exception e) {
			throw new RuntimeException("File not found. ( " + resName + " )");
		}
	}
	
	public static Bitmap openBitmap(String fileName, String resName) {
		byte[] buffer = null;
		try {
			buffer = LPKResource.openResource(fileName, resName);
			return GraphicsUtils.loadBitmap(buffer, true);
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
	public static LPKHeader readHeader(DataInputStream dis) throws Exception {
		LPKHeader header = new LPKHeader();
		header.setPAKIdentity(dis.readInt());
		byte[] pass = readByteArray(dis, LPKHeader.LF_PASSWORD_LENGTH);
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
	public static LPKTable[] readLPKTable(DataInputStream dis,
			int fileTableNumber) throws Exception {
		LPKTable[] fileTable = new LPKTable[fileTableNumber];
		for (int i = 0; i < fileTableNumber; i++) {
			LPKTable ft = new LPKTable();
			ft.setFileName(readByteArray(dis, LPKHeader.LF_FILE_LENGTH));
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
	public static byte[] readFileFromPak(DataInputStream dis, LPKHeader header,
			LPKTable fileTable) throws Exception {
		dis.skip(fileTable.getOffSet() - outputOffset(header));
		int fileLength = (int) fileTable.getFileSize();
		byte[] fileBuff = new byte[fileLength];
		int readLength = dis.read(fileBuff, 0, fileLength);
		if (readLength < fileLength) {
			return null;
		} else {
			makeBuffer(fileBuff, readLength);
			return fileBuff;
		}
	}

	/**
	 * 读取Byte[]
	 * 
	 * @param dis
	 * @param readLength
	 * @return
	 * @throws Exception
	 */
	public static byte[] readByteArray(DataInputStream dis, int readLength)
			throws Exception {
		byte[] readBytes = new byte[readLength];
		for (int i = 0; i < readLength; i++) {
			readBytes[i] = dis.readByte();
		}
		return readBytes;
	}

	/**
	 * 获得指定头文件的偏移长度
	 * 
	 * @param header
	 * @return
	 */
	public static long outputOffset(LPKHeader header) {
		return LPKHeader.size() + header.getTables() * LPKTable.size();
	}

	/**
	 * 移动偏移位置
	 * 
	 * @param sourceFileSize
	 * @param lastFileOffset
	 * @return
	 */
	public static long outputNextOffset(long sourceFileSize, long lastFileOffset) {
		return lastFileOffset + sourceFileSize;
	}

	/**
	 * 混淆数据
	 * 
	 * @param data
	 * @param size
	 */
	public static void makeBuffer(byte[] data, int size) {
		for (int i = 0; i < size; i++) {
			data[i] ^= 0xF7;
		}
	}
}
