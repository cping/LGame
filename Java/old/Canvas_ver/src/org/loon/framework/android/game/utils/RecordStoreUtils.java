package org.loon.framework.android.game.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.loon.framework.android.game.core.store.RecordStore;
import org.loon.framework.android.game.core.store.RecordStoreException;
import org.loon.framework.android.game.core.store.RecordStoreFullException;
import org.loon.framework.android.game.core.store.RecordStoreNotFoundException;
import org.loon.framework.android.game.core.store.RecordStoreNotOpenException;

/**
 * 
 * Copyright 2008 - 2010
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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.0
 */
public class RecordStoreUtils {

	private static final int DEFAULT_ID = 1;

	private static final int COULD_NOT_SAVE = -1;

	private static final int COULD_NOT_OPEN = -2;

	private RecordStoreUtils() {
	}

	/**
	 * 获得指定资源的Byte[]
	 * 
	 * @param resName
	 * @return
	 */
	public static byte[] getBytes(final String resName) {
		return getBytes(resName, DEFAULT_ID);
	}

	/**
	 * 获得指定资源的String
	 * 
	 * @param resName
	 * @return
	 */
	public static String getString(final String resName) {
		return bytesToString(getBytes(resName));
	}

	/**
	 * 获得指定资源的Byte[]
	 * 
	 * @param resName
	 * @param recordId
	 * @return
	 */
	public static byte[] getBytes(final String resName, final int recordId) {
		byte[] result = new byte[0];
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(resName, true);
			result = rs.getRecord(recordId);
		} catch (RecordStoreException e) {
		} finally {
			closeRecordStore(rs);
		}
		return result;
	}
	/**
	 * 将Byte[]转化为String
	 * 
	 * @param bytes
	 * @return
	 */
	private static String bytesToString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		String res = "";
		try {
			res = dis.readUTF();
		} catch (IOException ex) {
		} finally {
			try {
				dis.close();
				bais.close();
			} catch (IOException ex1) {
			}
			dis = null;
			bais = null;
		}
		return res;
	}

	/**
	 * 将String转化为Byte[]
	 * 
	 * @param string
	 * @return
	 */
	private static byte[] stringToBytes(String string) {
		if (string == null) {
			return null;
		}
		byte[] buffer = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeUTF(string);
			buffer = baos.toByteArray();
		} catch (IOException ex) {
		} finally {
			try {
				baos.close();
				dos.close();
			} catch (IOException ex1) {
			}
			baos = null;
			dos = null;
		}
		return buffer;
	}
	/**
	 * 获得指定资源的String
	 * 
	 * @param resName
	 * @param recordId
	 * @return
	 */
	public static String getString(final String resName, final int recordId) {
		return bytesToString(getBytes(resName,recordId));
	}

	/**
	 * 写入指定的Byte[]资源
	 * 
	 * @param resName
	 * @param data
	 */
	public static void setBytes(final String resName, final String data) {
		setBytes(resName, stringToBytes(data));
	}

	/**
	 * 写入指定的Byte[]资源
	 * 
	 * @param resName
	 * @param data
	 */
	public static void setBytes(final String resName, final byte[] data) {
		setBytes(resName,DEFAULT_ID, data);
	}

	/**
	 * 写入指定的Byte[]资源
	 * 
	 * @param resName
	 * @param recordId
	 * @param data
	 */
	public static void setBytes(final String resName, final int recordId,
			final byte[] data) {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(resName, true);
			if (rs.getNumRecords() == 0) {
				rs.addRecord(data, 0, data.length);
			} else {
				rs.setRecord(recordId, data, 0, data.length);
			}
		} catch (RecordStoreException e) {
		} finally {
			closeRecordStore(rs);
		}
	}

	/**
	 * 追加指定的Byte[]资源（与setBytes函数区别在于一定会增加一条数据）
	 * 
	 * @param resName
	 * @param data
	 * @return
	 */
	public static int addBytes(final String resName, final byte[] data) {
		RecordStore rs = null;
		boolean opened = false;
		try {
			rs = RecordStore.openRecordStore(resName, true);
			opened = true;
			return rs.addRecord(data, 0, data.length);
		} catch (RecordStoreException e) {

		} finally {
			closeRecordStore(rs);
		}
		return opened ? COULD_NOT_SAVE : COULD_NOT_OPEN;
	}

	/**
	 * 删除指定的存储资源
	 * 
	 * @param resName
	 * @param recordId
	 */
	public static void removeRecord(final String resName, final int recordId) {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(resName, false);
			rs.deleteRecord(recordId);
		} catch (RecordStoreException e) {

		} finally {
			closeRecordStore(rs);
		}
	}

	/**
	 * 删除指定的存储资源
	 * 
	 * @param resName
	 */
	public static void removeRecord(final String resName) {
		removeRecord(resName, DEFAULT_ID);
	}

	/**
	 * 判定指定的存储资源是否存在
	 * 
	 * @param resName
	 * @return
	 */
	public static boolean existRecordStore(String resName) {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(resName, false);
			return (rs != null);
		} catch (RecordStoreFullException e) {
			return false;
		} catch (RecordStoreNotFoundException e) {
			return false;
		} catch (RecordStoreException e) {
			return false;
		} finally {
			if (rs != null)
				try {
					rs.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
				} catch (RecordStoreException e) {
				}
		}
	}

	/**
	 * 判定指定的存储资源是否存在(全部数据遍历判定)
	 * 
	 * @param resName
	 * @return
	 */
	public static boolean existRecordStoreAll(final String resName) {
		final String[] recordStores = RecordStore.listRecordStores();
		if (recordStores == null) {
			return false;
		}
		for (int i = 0; i < recordStores.length; i++) {
			if (recordStores[i].equals(resName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除指定的存储资源
	 * 
	 * @param resName
	 */
	public static void deleteRecordStore(String resName) {
		if (existRecordStore(resName)) {
			try {
				RecordStore.deleteRecordStore(resName);
			} catch (RecordStoreNotFoundException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除拥有指定后缀的存储资源
	 * 
	 * @param prefix
	 */
	public static void deleteRecordStoresWithPrefix(final String prefix) {
		final String[] recordStores = RecordStore.listRecordStores();
		if (recordStores == null) {
			return;
		}
		for (int i = 0; i < recordStores.length; i++) {
			if (recordStores[i].startsWith(prefix)) {
				try {
					RecordStore.deleteRecordStore(recordStores[i]);
				} catch (RecordStoreException e) {
				}
			}
		}
	}

	/**
	 * 关闭指定的资源存储器
	 * 
	 * @param rs
	 */
	public static void closeRecordStore(final RecordStore rs) {
		if (rs != null) {
			try {
				rs.closeRecordStore();
			} catch (RecordStoreException e) {
			}
		}
	}

}
