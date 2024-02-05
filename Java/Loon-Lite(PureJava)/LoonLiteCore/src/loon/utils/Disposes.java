/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import java.util.Iterator;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;

/**
 * LRelease资源管理器
 */
public class Disposes implements LRelease {

	public static void closeAll(LRelease... ds) {
		new Disposes(ds).close();
	}

	public static void closeAll(Iterable<LRelease> rs) {
		new Disposes(rs).close();
	}

	private final Object _lock = new Object();

	private final SortedList<LRelease> _disposeSelf;

	private TArray<Exception> _exceptions;

	public Disposes() {
		this._disposeSelf = new SortedList<LRelease>();
	}

	public Disposes(LRelease... rs) {
		this();
		put(rs);
	}

	public Disposes(Iterable<LRelease> rs) {
		this();
		put(rs);
	}

	public Disposes put(LRelease... rs) {
		if (rs == null) {
			return this;
		}
		final int size = rs.length;
		synchronized (_lock) {
			for (int i = 0; i < size; i++) {
				LRelease r = rs[i];
				if (r != null && r != this) {
					_disposeSelf.add(r);
				}
			}
		}
		return this;
	}

	public Disposes put(Iterable<LRelease> rs) {
		if (rs == null) {
			return this;
		}
		synchronized (_lock) {
			for (Iterator<LRelease> it = rs.iterator(); it.hasNext();) {
				LRelease r = it.next();
				if (r != null && r != this) {
					_disposeSelf.add(r);
				}
			}
		}
		return this;
	}

	public Disposes put(LRelease release) {
		if (release == null) {
			return this;
		}
		if (release == this) {
			return this;
		}
		synchronized (_lock) {
			_disposeSelf.add(release);
		}
		return this;
	}

	public boolean contains(LRelease release) {
		if (release == null) {
			return false;
		}
		synchronized (_lock) {
			return _disposeSelf.contains(release);
		}
	}

	public Disposes remove(LRelease release) {
		if (release == null) {
			return this;
		}
		synchronized (_lock) {
			_disposeSelf.remove(release);
		}
		return this;
	}

	protected Disposes putException(Exception e) {
		if (_exceptions == null) {
			_exceptions = new TArray<Exception>();
		}
		_exceptions.add(e);
		return this;
	}

	@Override
	public void close() {
		if (_disposeSelf.size == 0) {
			return;
		}
		synchronized (_lock) {
			for (LIterator<LRelease> it = _disposeSelf.listIterator(); it.hasNext();) {
				LRelease release = it.next();
				if (release != null && release != this) {
					try {
						release.close();
					} catch (Exception e) {
						putException(e);
					}
				}
			}
			_disposeSelf.clear();
			if (_exceptions != null) {
				if (_exceptions.size == 1) {
					Exception ex = _exceptions.get(0);
					if (ex instanceof RuntimeException) {
						throw (RuntimeException) ex;
					} else {
						throw new LSysException("Dispose Exceptions:", ex);
					}
				} else {
					final StringKeyValue skv = new StringKeyValue("Dispose Exceptions:").newLine();
					final int size = _exceptions.size;
					for (int i = 0; i < size; i++) {
						Exception ex = _exceptions.get(i);
						if (ex != null) {
							if (i < size - 1) {
								skv.addValue(ex.toString()).comma().newLine();
							} else {
								skv.addValue(ex.toString());
							}
						}
					}
					LSystem.error(skv.toData());
				}
			}
		}
	}

}
