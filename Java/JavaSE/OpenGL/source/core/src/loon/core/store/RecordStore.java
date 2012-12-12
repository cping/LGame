package loon.core.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import loon.utils.FileUtils;
import loon.utils.StringUtils;


/**
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class RecordStore {

	// 默认文件标识
	final static public String STORE_FILENAME_PREFIX = "lgame-record-";

	// 默认后缀
	final static public String STORE_FILENAME_SUFFIX = ".store";

	// 已查询数据缓冲
	private static HashMap<String, RecordStore> stores = new HashMap<String, RecordStore>(10);

	private String name;

	private int openCount = 0;

	private File storeFile;

	public static RecordStore openRecordStore(String recordStoreName,
			boolean createIfNecessary) throws RecordStoreException {
		synchronized (stores) {
			RecordStore store = (RecordStore) stores.get(recordStoreName);
			if (store == null) {
				store = new RecordStore(recordStoreName);
				stores.put(recordStoreName, store);
			}
			store.openRecordStore(createIfNecessary);
			return store;
		}
	}

	private RecordStore(String name) {
		this.name = name;
	}

	public static synchronized void deleteStores() {
		String[] stores = listRecordStores();
		for (int i = 0; i < stores.length; i++) {
			String store = stores[i];
			try {
				deleteRecordStore(store);
			} catch (RecordStoreException e) {
			}
		}
	}

	public static synchronized boolean deleteRecordStore(String recordStoreName)
			throws RecordStoreException {
		try {
			List<?> list = FileUtils.getFiles(".", STORE_FILENAME_SUFFIX
					.substring(1));
			if (list != null) {
				int size = list.size();
				String ret, name;
				for (int i = 0; i < size; i++) {
					name = (String) list.get(i);
					ret = FileUtils.getFileName(((String) list.get(i)));
					ret = StringUtils.replaceIgnoreCase(ret.substring(0, ret
							.length()
							- STORE_FILENAME_SUFFIX.length()),
							STORE_FILENAME_PREFIX, "");
					if (recordStoreName.equals(ret)) {
						stores.remove(ret);
						File file = new File(name);
						file.delete();
						return true;
					}
				}
			} else {
				return false;
			}
		} catch (IOException e) {
		} catch (Exception e) {
			throw new RuntimeException("Store " + recordStoreName
					+ "deleteRecordStore Exception!");
		}
		return false;
	}

	public static synchronized String[] listRecordStores() {
		String[] result = null;
		try {
			List<?> list = FileUtils.getFiles(".", STORE_FILENAME_SUFFIX
					.substring(1));
			if (list != null) {
				int size = list.size();
				result = new String[size];
				if (size == 0) {
					result = null;
				} else {
					String ret;
					for (int i = 0; i < size; i++) {
						ret = FileUtils.getFileName(((String) list.get(i)));
						result[i] = StringUtils.replaceIgnoreCase(ret
								.substring(0, ret.length()
										- STORE_FILENAME_SUFFIX.length()),
								STORE_FILENAME_PREFIX, "");
					}
				}

			}
		} catch (IOException e) {
		}
		return result;
	}

	public synchronized void openRecordStore(boolean createIfNecessary)
			throws RecordStoreException {
		if (openCount > 0) {
			openCount++;
			return;
		}

		storeFile = new File(STORE_FILENAME_PREFIX + name
				+ STORE_FILENAME_SUFFIX);

		boolean readOk = false;
		if (storeFile.exists()) {
			try {
				readFromDisk();
				readOk = true;
			} catch (Exception ex) {
				if (!createIfNecessary)
					throw new RecordStoreException("Store " + name
							+ " could not read/find backing file " + storeFile);
			}
		}
		if (!readOk) {
			clear();
			writeToDisk();
		}
		openCount = 1;
	}

	public static class RecordItem implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		int id;

		byte[] data;

		RecordItem() {
		}

		RecordItem(int id, byte[] data) {
			this.id = id;
			this.data = data;
		}
	}

	private int nextRecordId = 1;

	private Vector<RecordItem> records = new Vector<RecordItem>();

	public static final String HEADER = "RecordStore:1";

	private synchronized void clear() {
		nextRecordId = 1;
		records = new Vector<RecordItem>();
	}

	private synchronized void readFromDisk() throws RecordStoreException {
		try {
			FileInputStream fis = new FileInputStream(storeFile);
			ObjectInputStream os = new ObjectInputStream(fis);
			String header = os.readUTF();
			if (!header.equals(HEADER)) {
				extracted(header);
			}
			nextRecordId = os.readInt();
			int size = os.readInt();
			records = new Vector<RecordItem>();
			for (int i = 0; i < size; i++) {
				RecordItem ri = (RecordItem) os.readObject();
				records.addElement(ri);
			}
			os.close();

		} catch (Exception e) {
			throw new RecordStoreException("ERROR reading store from disk ("
					+ storeFile + "): " + e);
		}
	}

	private void extracted(String header) throws RecordStoreException {
		throw new RecordStoreException("Store file header mismatch: "
				+ header);
	}

	private synchronized void writeToDisk() throws RecordStoreException {
		try {
			FileOutputStream fos = new FileOutputStream(storeFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeUTF(HEADER);
			oos.writeInt(nextRecordId);
			oos.writeInt(records.size());
			for (int i = 0; i < records.size(); i++) {
				RecordItem ri = (RecordItem) records.elementAt(i);
				oos.writeObject(ri);
			}
			oos.close();
		} catch (Exception e) {
			throw new RecordStoreException("Error writing store to disk: " + e);
		}
	}

	public void checkOpen(String message) throws RecordStoreNotOpenException {
		if (openCount <= 0) {
			throw new RecordStoreNotOpenException(message);
		}
	}

	public synchronized int addRecord(byte[] data, int offset, int numBytes)
			throws RecordStoreException {
		checkOpen("addRecord");
		byte buf[] = new byte[numBytes];
		if (numBytes != 0) {
			System.arraycopy(data, offset, buf, 0, numBytes);
		}
		RecordItem ri = new RecordItem(nextRecordId++, buf);
		records.addElement(ri);
		writeToDisk();
		return ri.id;
	}

	public synchronized void closeRecordStore()
			throws RecordStoreNotOpenException, RecordStoreException {
		checkOpen("closeRecordStore");
		openCount--;
	}

	public synchronized void deleteRecord(int recordId)
			throws RecordStoreNotOpenException, RecordStoreException {
		checkOpen("deleteRecord");
		for (int i = 0; i < records.size(); i++) {
			RecordItem ri = (RecordItem) records.elementAt(i);
			if (ri.id == recordId) {
				records.removeElementAt(i);
				writeToDisk();
				return;
			}
		}
		throw new InvalidRecordIDException("deleteRecord " + recordId);
	}

	public synchronized RecordEnumeration enumerateRecords(RecordFilter filter,
			RecordComparator comparator, boolean keepUpdated)
			throws RecordStoreNotOpenException {
		checkOpen("enumerateRecords");
		if (filter != null)
			throw new RuntimeException(
					"enumerateRecords with RecordFilter Unimplemented");
		if (comparator != null)
			throw new RuntimeException(
					"enumerateRecords with RecordComparator Unimplemented");
		if (keepUpdated)
			throw new RuntimeException(
					"enumerateRecords with keepUpdated Unimplemented");

		return new RecordEnumerationImpl();
	}

	class RecordEnumerationImpl implements RecordEnumeration {

		RecordEnumerationImpl() {
			nextIndex = 0;
		}

		private int nextIndex;

		public boolean hasNextElement() {
			synchronized (RecordStore.this) {
				return nextIndex < records.size();
			}
		}

		public int nextRecordId() throws InvalidRecordIDException {
			synchronized (RecordStore.this) {
				if (nextIndex >= records.size()) {
					throw new InvalidRecordIDException("nextRecordId at index "
							+ nextIndex + "/" + records.size());
				}
				RecordItem ri = (RecordItem) records.elementAt(nextIndex);
				nextIndex++;
				return ri.id;
			}
		}

		public void destroy() {

		}

		public boolean hasPreviousElement() {
			return false;
		}

		public boolean isKeptUpdated() {
			return false;
		}

		public void keepUpdated(boolean keepUpdated) {

		}

		public byte[] nextRecord() throws InvalidRecordIDException,
				RecordStoreNotOpenException, RecordStoreException {
			return null;
		}

		public int numRecords() {
			return 0;
		}

		public byte[] previousRecord() throws InvalidRecordIDException,
				RecordStoreNotOpenException, RecordStoreException {
			return null;
		}

		public int previousRecordId() throws InvalidRecordIDException {
			return 0;
		}

		public void rebuild() {

		}

		public void reset() {

		}
	}

	public String getName() throws RecordStoreNotOpenException {
		checkOpen("getName");
		return name;
	}

	public synchronized int getNumRecords() throws RecordStoreNotOpenException {
		checkOpen("getNumRecords");
		return records.size();
	}

	private RecordItem getRecordItem(int id) {
		Enumeration<RecordItem> rs = records.elements();
		while (rs.hasMoreElements()) {
			RecordItem ri = (RecordItem) rs.nextElement();
			if (ri.id == id) {
				return ri;
			}
		}
		return null;
	}

	public synchronized byte[] getRecord(int recordId)
			throws RecordStoreNotOpenException, RecordStoreException {
		checkOpen("getRecord");
		RecordItem ri = getRecordItem(recordId);
		if (ri == null) {
			throw new InvalidRecordIDException("record " + recordId
					+ " not found");
		}
		return ri.data;
	}

	public synchronized int getRecord(int recordId, byte[] buffer, int offset)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		checkOpen("getRecord");
		RecordItem ri = getRecordItem(recordId);
		if (ri == null) {
			throw new InvalidRecordIDException("record " + recordId
					+ " not found");
		}
		byte[] data = ri.data;
		int recordSize = data.length;
		System.arraycopy(data, 0, buffer, offset, recordSize);
		return recordSize;
	}

	public synchronized int getRecordSize(int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		checkOpen("getRecordSize");
		RecordItem ri = getRecordItem(recordId);
		if (ri == null) {
			throw new InvalidRecordIDException("record " + recordId
					+ " not found");
		}
		byte[] data = (byte[]) ri.data;
		if (data == null) {
			throw new InvalidRecordIDException();
		}
		return data.length;
	}

	public synchronized int getNextRecordID()
			throws RecordStoreNotOpenException, RecordStoreException {
		return nextRecordId;
	}

	public synchronized int getSize() throws RecordStoreNotOpenException {
		try {
			return getRecordSize(nextRecordId);
		} catch (Exception e) {
			throw new RecordStoreNotOpenException();
		}
	}

	public synchronized void setRecord(int recordId, byte[] newData,
			int offset, int numBytes) throws RecordStoreNotOpenException,
			RecordStoreException {
		checkOpen("setRecord");
		RecordItem ri = getRecordItem(recordId);
		if (ri == null) {
			throw new InvalidRecordIDException("record " + recordId
					+ " not found");
		}
		byte buf[] = new byte[numBytes];
		if (numBytes != 0) {
			System.arraycopy(newData, offset, buf, 0, numBytes);
		}
		ri.data = buf;
		writeToDisk();
	}

}
