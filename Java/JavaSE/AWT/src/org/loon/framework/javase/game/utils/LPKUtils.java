package org.loon.framework.javase.game.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.loon.framework.javase.game.core.resource.LPKHeader;
import org.loon.framework.javase.game.core.resource.LPKResource;
import org.loon.framework.javase.game.core.resource.LPKTable;

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
public class LPKUtils extends LPKResource {

	/**
	 * 生成LPK目录表
	 * 
	 * @param srcFileName
	 * @param srcFileSize
	 * @param currentFileOffset
	 * @return
	 */
	public static LPKTable makeLPKTable(String srcFileName, long srcFileSize,
			long currentFileOffset) {
		LPKTable lpkTable = new LPKTable();
		lpkTable.setFileName(srcFileName.getBytes());
		lpkTable.setFileSize(srcFileSize);
		lpkTable.setOffSet(currentFileOffset);
		return lpkTable;
	}

	/**
	 * 打开指定的资源文件
	 * 
	 * @param fileName
	 * @param resName
	 * @param writeFile
	 * @param extractDir
	 * @return
	 * @throws Exception
	 */
	public static byte[] openResource(String fileName, String resName,
			boolean writeFile, String extractDir) throws Exception {
		File rFile = new File(fileName);
		FileInputStream fis = new FileInputStream(rFile);
		DataInputStream dis = new DataInputStream(fis);
		LPKHeader header = readHeader(dis);
		LPKTable[] fileTable = readLPKTable(dis, (int) header.getTables());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String innerName = new String(fileTable[i].getFileName()).trim();
			if (innerName.equals(resName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			throw new RuntimeException(("File not found. ( " + resName + " )"));
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			if (writeFile) {
				writeFileFromByteBuffer(buff, resName, extractDir);
			} else {
				dis.close();
				fis.close();
			}
			return buff;
		}
	}

	/**
	 * 写入资源到指定文件
	 * 
	 * @param fileBuff
	 * @param fileName
	 * @param extractDir
	 * @throws Exception
	 */
	public static void writeFileFromByteBuffer(byte[] fileBuff,
			String fileName, String extractDir) throws Exception {
		String extractFilePath = extractDir + fileName;
		File file = new File(extractFilePath);
		if (!file.exists()) {
			FileUtils.makedirs(extractFilePath);
		}
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.write(fileBuff);
		dos.close();
		fos.close();
	}

	/**
	 * 返回LPK文件信息
	 * 
	 * @param pakFilePath
	 * @return
	 * @throws Exception
	 */
	public static List<?> getLPKFileInfo(String pakFilePath) throws Exception {
		File rFile = new File(pakFilePath);
		FileInputStream fis = new FileInputStream(rFile);
		DataInputStream dis = new DataInputStream(fis);
		LPKHeader header = readHeader(dis);
		LPKTable[] fileTable = readLPKTable(dis, (int) header.getTables());
		Vector<Object> result = new Vector<Object>();
		result.add(header);
		result.add(fileTable);
		return result;
	}

	/**
	 * 创建LPK文件
	 * 
	 * @param srcFileName
	 * @param makeFilePath
	 */
	public static void makeLPKFile(String srcFileName, String makeFilePath) {
		makeLPKFile(srcFileName, makeFilePath, Long
				.parseLong(toNumberPassword(LPKHeader.LF_PASSWORD_LENGTH)));
	}

	public static String toNumberPassword(int length) {
		if (length < 1) {
			return null;
		}
		String strChars[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		StringBuffer strPassword = new StringBuffer();
		int nRand = (int) Math.round(Math.random() * 100D);
		for (int i = 0; i < length; i++) {
			nRand = (int) Math.round(Math.random() * 100D);
			strPassword.append(strChars[nRand % (strChars.length - 1)]);
		}
		return strPassword.toString();
	}

	/**
	 * 创建LPK文件
	 * 
	 * @param srcFileName
	 * @param makeFilePath
	 * @param password
	 */
	public static void makeLPKFile(String srcFileName, String makeFilePath,
			long password) {
		List<?> files = null;
		try {
			files = FileUtils.getAllFiles(srcFileName);
		} catch (IOException e) {
			throw new RuntimeException("File not found. ( " + srcFileName
					+ " )");
		}
		makeLPKFile(files, makeFilePath, password);
	}

	/**
	 * 创建LPK文件
	 * 
	 * @param files
	 * @param makeFilePath
	 * @param password
	 */
	public static void makeLPKFile(List<?> files, String makeFilePath,
			long password) {
		int size = files.size();
		LPKHeader header = new LPKHeader();
		header.setPAKIdentity(LPKHeader.LF_PAK_ID);
		header.setPassword(password);
		header.setTables(size);
		header.setVersion(1.0F);
		String[] filePaths = new String[size];
		for (int i = 0; i < size; i++) {
			filePaths[i] = (String) files.get(i);
		}
		LPKUtils.makeLPKFile(filePaths, makeFilePath, header);
	}

	/**
	 * 创建LPK文件
	 * 
	 * @param srcFilePath
	 * @param makeFilePath
	 * @param header
	 */
	public static void makeLPKFile(String[] srcFilePath, String makeFilePath,
			LPKHeader header) {
		FileOutputStream os = null;
		DataOutputStream dos = null;
		try {
			LPKTable[] fileTable = new LPKTable[srcFilePath.length];
			long fileOffset = outputOffset(header);
			for (int i = 0; i < srcFilePath.length; i++) {
				String sourceFileName = FileUtils.getFileName(srcFilePath[i]);
				long sourceFileSize = FileUtils.getFileSize(srcFilePath[i]);
				LPKTable ft = makeLPKTable(sourceFileName, sourceFileSize,
						fileOffset);
				fileOffset = outputNextOffset(sourceFileSize, fileOffset);
				fileTable[i] = ft;
			}
			File file = new File(makeFilePath);
			if (!file.exists()) {
				FileUtils.makedirs(file);
			}
			os = new FileOutputStream(file);
			dos = new DataOutputStream(os);
			dos.writeInt(header.getPAKIdentity());
			writeByteArray(header.getPassword(), dos);
			dos.writeFloat(header.getVersion());
			dos.writeLong(header.getTables());
			for (int i = 0; i < fileTable.length; i++) {
				writeByteArray(fileTable[i].getFileName(), dos);
				dos.writeLong(fileTable[i].getFileSize());
				dos.writeLong(fileTable[i].getOffSet());
			}
			for (int i = 0; i < fileTable.length; i++) {
				File ftFile = new File(srcFilePath[i]);
				FileInputStream ftFis = new FileInputStream(ftFile);
				DataInputStream ftDis = new DataInputStream(ftFis);
				byte[] buff = new byte[256];
				int readLength = 0;
				while ((readLength = ftDis.read(buff)) != -1) {
					makeBuffer(buff, readLength);
					dos.write(buff, 0, readLength);
				}
				ftDis.close();
				ftFis.close();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (dos != null) {
				try {
					dos.close();
					dos = null;
				} catch (IOException e) {
				}

			}
		}
	}

	/**
	 * 写入Byte[]
	 * 
	 * @param bytes
	 * @param dos
	 * @throws Exception
	 */
	public static void writeByteArray(byte[] bytes, DataOutputStream dos)
			throws Exception {
		for (int i = 0; i < bytes.length; dos.writeByte(bytes[i]), i++)
			;
	}

}
