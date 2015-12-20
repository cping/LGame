package org.loon.framework.android.game.core.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

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
public class RecordStore {

	public static final int AUTHMODE_PRIVATE = 0;

	public static final int AUTHMODE_ANY = 1;

	private static RecordStoreSqlLite sqlLite;

	private static HashMap<String, RecordStore> openedRecordStores = new HashMap<String, RecordStore>();

	private String name;

	private int version;

	long recordStorePk;

	private int numRecords;

	private int size;

	private long sizeAvailable = 4 * 1024 * 1024;

	private long lastModified;

	private int nextRecordID = 1;

	private int openCount = 0;

	private int authMode;

	private List<RecordListener> listeners = new ArrayList<RecordListener>();

	public RecordStore(String name, long pk) {
		this.name = name;
		this.recordStorePk = pk;
	}

	public static void deleteRecordStore(String recordStoreName)
			throws RecordStoreException, RecordStoreNotFoundException {
		RecordStore recordStore = getOpenedRecordStoreFromCache(recordStoreName);
		if (recordStore != null) {
			throw new RecordStoreException("The record store '"
					+ recordStoreName + "' is not closed.");
		}
		sqlLite.deleteRecordStore(recordStoreName);

	}

	public static RecordStore openRecordStore(String recordStoreName,
			boolean createIfNecessary) throws RecordStoreException,
			RecordStoreFullException, RecordStoreNotFoundException {
		init();
		if (recordStoreName == null) {
			throw new IllegalArgumentException(
					"Parameter 'recordStoreName' must not be null or empty.");
		}

		if (recordStoreName.length() < 1 || recordStoreName.length() > 32) {
			throw new IllegalArgumentException(
					"Parameter 'recordStoreName' must have a length between 1 and 32.");
		}

		RecordStore recordStore = openRecordStoreFromCache(recordStoreName);
		if (recordStore != null) {
			return recordStore;
		}

		recordStore = sqlLite.getRecordStore(recordStoreName);
		if (recordStore != null) {
			cacheRecordStore(recordStoreName, recordStore);
			return recordStore;
		}
		if (!createIfNecessary) {
			throw new RecordStoreNotFoundException(
					"No record store with name '" + recordStoreName
							+ "' found.");
		}

		recordStore = sqlLite.createRecordStore(recordStoreName);
		if (recordStore == null) {
			throw new RecordStoreException(
					"Could not create record store with name '"
							+ recordStoreName
							+ "'. Reason: The method 'SqlDao.createRecordStore' returned null although it is not allowed to do so.");
		}
		cacheRecordStore(recordStoreName, recordStore);
		return recordStore;
	}

	public static RecordStore openRecordStore(String recordStoreName,
			boolean createIfNecessary, int authmode, boolean writable)
			throws RecordStoreException, RecordStoreFullException,
			RecordStoreNotFoundException {
		return openRecordStore(recordStoreName, createIfNecessary);
	}

	public static RecordStore openRecordStore(String recordStoreName,
			String vendorName, String suiteName) throws RecordStoreException,
			RecordStoreNotFoundException {
		return openRecordStore(recordStoreName, true);
	}

	public void setMode(int authmode, boolean writable) {

	}

	public void closeRecordStore() throws RecordStoreNotOpenException,
			RecordStoreException {
		if (isClosed()) {
			return;
		}
		boolean closed = closeChachedRecordStore();

		if (closed) {
			synchronized (this.listeners) {
				this.listeners.clear();
			}
		}
	}

	public static String[] listRecordStores() {
		init();
		String[] listRecordStores = sqlLite.listRecordStores();
		if (listRecordStores.length == 0) {
			return null;
		}
		return listRecordStores;
	}

	public String getName() throws RecordStoreNotOpenException {
		return this.name;
	}

	public int getVersion() throws RecordStoreNotOpenException {
		return this.version;
	}

	public int getNumRecords() throws RecordStoreNotOpenException {
		return this.numRecords;
	}

	public int getSize() throws RecordStoreNotOpenException {
		return this.size;
	}

	public int getSizeAvailable() throws RecordStoreNotOpenException {

		if (this.sizeAvailable > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) this.sizeAvailable;
	}

	public long getLastModified() throws RecordStoreNotOpenException {
		return this.lastModified;
	}

	public void addRecordListener(RecordListener listener) {
		synchronized (this.listeners) {
			if (!this.listeners.contains(listener)) {
				this.listeners.add(listener);
			}
		}
	}

	public void removeRecordListener(RecordListener listener) {
		synchronized (this.listeners) {
			if (!this.listeners.contains(listener)) {
				this.listeners.remove(listener);
			}
		}
	}

	public int getNextRecordID() throws RecordStoreNotOpenException,
			RecordStoreException {
		if (isClosed()) {
			throw new RecordStoreNotOpenException("");
		}
		return this.nextRecordID;
	}

	public int addRecord(byte[] data, int offset, int numBytes)
			throws RecordStoreNotOpenException, RecordStoreException,
			RecordStoreFullException {

		if (isClosed()) {
			throw new RecordStoreNotOpenException(
					"The record store is not open because it was closed. This RecordStore object is invalid and will stay so.");
		}
		if (data == null) {
			data = new byte[0];
		}
		if (data.length != 0 && offset >= data.length) {
			throw new RecordStoreException("The offset '" + offset
					+ "' is beyond the size of the data array of '"
					+ data.length + "'");
		}
		if (numBytes < 0) {
			throw new RecordStoreException("The number of bytes '" + numBytes
					+ "' must not be negative.");
		}
		if (offset < 0) {
			throw new RecordStoreException("The offset '" + offset
					+ "' must not be negative.");
		}
		if (offset + numBytes > data.length) {
			throw new RecordStoreException(
					"The Parameter numBytes with value '"
							+ numBytes
							+ "' exceeds the number of available bytes if counted from offset '"
							+ offset + "'");
		}
		byte[] actualData = new byte[numBytes];
		System.arraycopy(data, offset, actualData, 0, numBytes);
		int recordId = sqlLite.addRecord(getPk(), actualData);

		RecordStore recordStore = sqlLite.getRecordStore(getPk());
		updateRecordStoreInstance(recordStore);
		fireRecordAddedEvent(recordId);
		return recordId;
	}

	private void updateRecordStoreInstance(RecordStore recordStore)
			throws RecordStoreException {
		this.name = recordStore.name;
		this.nextRecordID = recordStore.nextRecordID;
		this.numRecords = recordStore.numRecords;
		this.size = recordStore.size;
		this.version = recordStore.version;
		this.recordStorePk = recordStore.recordStorePk;
		this.authMode = recordStore.authMode;
	}

	protected long getPk() {
		return this.recordStorePk;
	}

	public void deleteRecord(int recordId) throws RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		if (isClosed()) {
			throw new RecordStoreNotOpenException();
		}
		if (recordId < 0) {
			throw new InvalidRecordIDException();
		}
		sqlLite.removeRecord(getPk(), recordId);
		RecordStore recordStore = sqlLite.getRecordStore(getPk());
		updateRecordStoreInstance(recordStore);
		fireRecordDeletedEvent(recordId);
	}

	public int getRecordSize(int recordId) throws RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		byte[] bytes = getRecord(recordId);
		if (bytes == null) {
			return 0;
		}
		return bytes.length;
	}

	public int getRecord(int recordId, byte[] buffer, int offset)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		byte[] data = getRecord(recordId);

		System.arraycopy(data, 0, buffer, offset, data.length);

		return data.length - offset;
	}

	public byte[] getRecord(int recordId) throws RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		if (isClosed()) {
			throw new RecordStoreNotOpenException();
		}
		if (recordId < 0) {
			throw new InvalidRecordIDException();
		}
		byte[] record = sqlLite.getRecord(getPk(), recordId);
		return record;
	}

	public void setRecord(int recordId, byte[] newData, int offset, int numBytes)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException, RecordStoreFullException {
		if (isClosed()) {
			throw new RecordStoreNotOpenException();
		}
		if (recordId < 0) {
			throw new InvalidRecordIDException(
					"The parameter 'recordId' must not be negative.");
		}
		if (newData == null) {
			newData = new byte[0];
		}

		byte[] data = new byte[numBytes];
		System.arraycopy(newData, offset, data, 0, numBytes);

		sqlLite.setRecord(getPk(), recordId, data);
		RecordStore recordStore = sqlLite.getRecordStore(getPk());
		updateRecordStoreInstance(recordStore);
		fireRecordChangedEvent(recordId);
	}

	public RecordEnumeration enumerateRecords(RecordFilter filter,
			RecordComparator comparator, boolean keepUpdated)
			throws RecordStoreNotOpenException {
		RecordStoreSqlLiteEnumeration sqlRecordEnumeration = new RecordStoreSqlLiteEnumeration(
				this, filter, comparator, keepUpdated);
		return sqlRecordEnumeration;
	}

	private static void init() {
		if (sqlLite == null) {
			sqlLite = RecordStoreSqlLite.getInstance();
		}
	}

	protected synchronized boolean isClosed() {
		return this.openCount <= 0;
	}

	private void fireRecordAddedEvent(int recordId) {
		synchronized (this.listeners) {
			for (Iterator<RecordListener> iterator = this.listeners.iterator(); iterator
					.hasNext();) {
				RecordListener recordListener = iterator.next();
				recordListener.recordAdded(this, recordId);
			}
		}
	}

	private void fireRecordChangedEvent(int recordId) {
		synchronized (this.listeners) {
			for (Iterator<RecordListener> iterator = this.listeners.iterator(); iterator
					.hasNext();) {
				RecordListener recordListener = iterator.next();
				recordListener.recordChanged(this, recordId);
			}
		}
	}

	private void fireRecordDeletedEvent(int recordId) {
		synchronized (this.listeners) {
			for (Iterator<RecordListener> iterator = this.listeners.iterator(); iterator
					.hasNext();) {
				RecordListener recordListener = iterator.next();
				recordListener.recordDeleted(this, recordId);
			}
		}
	}

	void setVersion(int version) {
		this.version = version;
	}

	void setNextId(int nextRecordId) {
		this.nextRecordID = nextRecordId;
	}

	void setNumberOfRecords(int numberOfRecords) {
		this.numRecords = numberOfRecords;
	}

	void setSize(int size) {
		this.size = size;
	}

	private static RecordStore getOpenedRecordStoreFromCache(
			String recordStoreName) {
		return openedRecordStores.get(recordStoreName);
	}

	private static void cacheRecordStore(String recordStoreName,
			RecordStore recordStore) {
		openedRecordStores.put(recordStoreName, recordStore);
		recordStore.openCount++;
	}

	private static RecordStore openRecordStoreFromCache(String recordStoreName) {
		RecordStore recordStore = openedRecordStores.get(recordStoreName);
		if (recordStore != null) {
			recordStore.openCount++;
		}
		return recordStore;
	}

	private boolean closeChachedRecordStore() {
		this.openCount--;
		if (this.openCount > 0) {
			return false;
		}
		openedRecordStores.remove(this.name);
		return true;
	}

}
