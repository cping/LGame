/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.utils;

import loon.LRelease;

/**
 * 分页用列表工具类
 * 
 * example: PageList<String> strings = new PageList<>(2, 3); for (int i = 0; i <
 * 9; i++) { strings.add(String.valueOf(i)); }
 * System.out.println(strings.getData());
 */
public class PageList<T> implements LRelease {

	private boolean _dirty = false;

	private TArray<T> _tempData = new TArray<T>();

	private TArray<T> _data;

	private int _lastPage = -1;

	private int _totalItems;

	private int _currentPage;

	private int _pageSize;

	private int _totalPages;

	public PageList() {
		this(0, -1);
	}

	public PageList(TArray<T> items) {
		this(items, 0, -1);
	}

	public PageList(int pageNumber, int pageSize) {
		this(new TArray<T>(), pageNumber, pageSize);
	}

	public PageList(TArray<T> items, int pageNumber, int pageSize) {
		this._totalItems = items.size;
		this._data = items;
		this.set(pageNumber, pageSize);
	}

	public PageList(Array<T> items, int pageNumber, int pageSize) {
		this._data = new TArray<T>(items);
		this._totalItems = items.size();
		this.set(pageNumber, pageSize);
	}

	public PageList(SortedList<T> items, int pageNumber, int pageSize) {
		this._data = new TArray<T>(items);
		this._totalItems = items.size();
		this.set(pageNumber, pageSize);
	}

	public PageList<T> set(int pageNumber, int pageSize) {
		this._currentPage = pageNumber;
		this._pageSize = pageSize;
		this._totalPages = MathUtils.ceil(_totalItems / (float) _pageSize);
		this._dirty = true;
		return this;
	}

	public PageList<T> clear() {
		if (_data != null) {
			_data.clear();
		}
		this._tempData.clear();
		this._totalItems = _data.size;
		this._currentPage = 0;
		this._totalPages = 0;
		this._dirty = true;
		return this;
	}

	public T get(int idx) {
		return _data.get(idx);
	}

	public TArray<T> all() {
		return _data;
	}

	public PageList<T> dirty() {
		this._lastPage = -1;
		this._dirty = true;
		return this;
	}

	public PageList<T> updateDirty() {
		this._dirty = !_dirty;
		return this;
	}

	public PageList<T> setDirty(boolean d) {
		this._dirty = d;
		return this;
	}

	public int getPageNumber() {
		return this._currentPage + 1;
	}

	public boolean add(T item) {
		boolean added = _data.add(item);
		if (added) {
			_totalItems = _data.size;
			set(_currentPage, _pageSize);
		}
		return added;
	}

	public boolean remove(T item) {
		boolean removed = _data.remove(item);
		if (removed) {
			_totalItems = _data.size;
			set(_currentPage, _pageSize);
		}
		return removed;
	}

	public T removeIndex(int idx) {
		T removed = _data.removeIndex(idx);
		if (removed != null) {
			_totalItems = _data.size;
			set(_currentPage, _pageSize);
		}
		return removed;
	}

	public PageList<T> setValue(int idx, T item) {
		this._data.set(_currentPage, item);
		this._lastPage = -1;
		this._dirty = true;
		return this;
	}

	protected TArray<T> getData(int page) {
		if (_lastPage != page || _tempData.size != _totalItems) {
			_tempData.clear();
			if (_pageSize != -1) {
				int start = (page * _pageSize);
				for (int i = start; (i < start + _pageSize && i < _data.size); i++) {
					_tempData.add(_data.get(i));
				}
			} else {
				_tempData.addAll(_data);
			}
			this._lastPage = page;
			this._totalItems = _data.size;
			this._totalPages = MathUtils.ceil(_totalItems / (float) _pageSize);
		}
		return _tempData;
	}

	public PageList<T> back() {
		if (hasBack()) {
			this._currentPage--;
			this._dirty = true;
		}
		return this;
	}

	public PageList<T> next() {
		if (hasNext()) {
			this._currentPage++;
			this._dirty = true;
		}
		return this;
	}

	public PageList<T> setPage(int page) {
		if (page >= 0 && page < _totalItems) {
			this._currentPage = page;
			this._lastPage = -1;
			this._dirty = true;
		}
		return this;
	}

	public TArray<T> getData() {
		return getData(_currentPage);
	}

	public boolean hasBack() {
		return _currentPage > 0;
	}

	public boolean hasNext() {
		return _currentPage < _totalPages - 1;
	}

	public boolean isFirst() {
		return _currentPage <= 0;
	}

	public boolean isLast() {
		return _currentPage >= _totalPages - 1;
	}

	public int size() {
		return _data.size;
	}

	public int getTotalItems() {
		return _totalItems;
	}

	public int getCurrentPage() {
		return _currentPage;
	}

	public int getPageSize() {
		return _pageSize;
	}

	public int getTotalPages() {
		return _totalPages;
	}

	public boolean isDirty() {
		return _dirty;
	}

	@Override
	public void close() {
		clear();
	}

}
