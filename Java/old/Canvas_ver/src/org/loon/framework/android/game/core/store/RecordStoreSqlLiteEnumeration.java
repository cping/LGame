package org.loon.framework.android.game.core.store;

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

public class RecordStoreSqlLiteEnumeration implements RecordEnumeration {

	private final RecordStore recordStore;

	private final RecordFilter filter;

	private final RecordComparator comparator;

	private boolean keepUpdated;

	private int lastRecordIndex;

	private boolean destroyed;

	private RecordStoreSqlLite sqlDao;

	private int[] recordIds;

	public RecordStoreSqlLiteEnumeration(RecordStore recordStore,
			RecordFilter filter, RecordComparator comparator,
			boolean keepUpdated) {
		this.recordStore = recordStore;
		this.filter = filter;
		this.comparator = comparator;
		this.keepUpdated = keepUpdated;
		this.sqlDao = RecordStoreSqlLite.getInstance();
		rebuild();
	}

	public void destroy() {
		this.destroyed = true;
	}

	public boolean hasNextElement() {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		boolean hasNext = this.lastRecordIndex < this.recordIds.length - 1;
		return hasNext;
	}

	public boolean hasPreviousElement() {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		boolean hasPrevious = this.lastRecordIndex > 0;
		return hasPrevious;
	}

	public boolean isKeptUpdated() {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		return this.keepUpdated;
	}

	public void keepUpdated(boolean keepUpdated2) {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		this.keepUpdated = keepUpdated2;
	}

	public byte[] nextRecord() throws InvalidRecordIDException,
			RecordStoreNotOpenException, RecordStoreException {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		if (this.recordStore.isClosed()) {
			throw new RecordStoreNotOpenException(
					"The record store which is enumerated is closed.");
		}
		int nextRecordIndex = this.lastRecordIndex + 1;
		if (nextRecordIndex >= this.recordIds.length) {
			throw new InvalidRecordIDException(
					"The end of the enumeration is reached.");
		}
		this.lastRecordIndex = nextRecordIndex;
		int recordId = this.recordIds[nextRecordIndex];
		byte[] result = this.sqlDao.getRecord(this.recordStore.getPk(),
				recordId);
		return result;
	}

	public int nextRecordId() throws InvalidRecordIDException {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		int nextRecordIndex = this.lastRecordIndex + 1;
		if (nextRecordIndex >= this.recordIds.length) {
			throw new InvalidRecordIDException(
					"No more records in this enumeration.");
		}
		this.lastRecordIndex = nextRecordIndex;
		int nextRecordId = this.recordIds[nextRecordIndex];
		return nextRecordId;
	}

	public int numRecords() {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		return this.recordIds.length;
	}

	public byte[] previousRecord() throws InvalidRecordIDException,
			RecordStoreNotOpenException, RecordStoreException {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		if (this.recordStore.isClosed()) {
			throw new RecordStoreNotOpenException(
					"The record store which is enumerated is closed.");
		}
		int previousRecordIndex = this.lastRecordIndex - 1;
		if (previousRecordIndex < 0) {
			throw new InvalidRecordIDException(
					"The start of the enumeration is reached.");
		}
		this.lastRecordIndex = previousRecordIndex;
		int recordId = this.recordIds[previousRecordIndex];
		byte[] result = this.sqlDao.getRecord(this.recordStore.getPk(),
				recordId);
		return result;
	}

	public int previousRecordId() throws InvalidRecordIDException {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		int previousIndex = this.lastRecordIndex - 1;
		if (this.lastRecordIndex < 0) {
			throw new InvalidRecordIDException(
					"The start of the enumeration is reached.");
		}
		this.lastRecordIndex = previousIndex;
		int nextRecordId = this.recordIds[previousIndex];
		return nextRecordId;
	}

	public void rebuild() {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		reset();
		this.recordIds = this.sqlDao
				.getRecordIdsForRecordStore(this.recordStore.getPk());
		filter();
		sort();
	}

	public void reset() {
		if (isDestroyed()) {
			throw new IllegalStateException(
					"RecordEnumeration instance is destroyed.");
		}
		this.lastRecordIndex = -1;

	}

	private boolean isDestroyed() {
		return this.destroyed;
	}

	private void filter() {
		if (this.filter == null) {
			return;
		}
		int deleteCount = 0;
		int numberOfRecords = this.recordIds.length;
		for (int currentIndex = 0; currentIndex < numberOfRecords; currentIndex++) {
			int currentRecordId = this.recordIds[currentIndex];
			byte[] currentData = this.sqlDao.getRecord(
					this.recordStore.getPk(), currentRecordId);
			boolean filterMatches = this.filter.matches(currentData);
			if (!filterMatches) {
				this.recordIds[currentIndex] = -1;
				deleteCount++;
			}
		}
		if (deleteCount > 0) {
			int newNumberOfRecords = numberOfRecords - deleteCount;
			int[] temp = new int[newNumberOfRecords];
			int tempIndex = 0;
			for (int currentIndex = 0; currentIndex < numberOfRecords; currentIndex++) {
				int recordId = this.recordIds[currentIndex];
				if (recordId != -1) {
					temp[tempIndex] = recordId;
					tempIndex++;
				}
			}
			this.recordIds = temp;
		}
	}

	private void sort() {
		if (this.comparator == null) {
			return;
		}

	}
}
