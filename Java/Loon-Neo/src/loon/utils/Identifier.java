/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.LSysException;

public final class Identifier {

	public static final class IdentifierPrefix {

		private final ObjectMap<String, Identifier> _allPrefixes = new ObjectMap<String, Identifier>();

		public final String _prefixName;

		protected IdentifierPrefix(String prefix) {
			this._prefixName = prefix;
		}

		public boolean containsId(String name) {
			return this._allPrefixes.containsKey(name);
		}

		public Identifier getId(String name) {
			Identifier id = (Identifier) this._allPrefixes.get(name);
			if (id != null) {
				return id;
			}
			id = (new Identifier()).set(this._prefixName, name);
			this._allPrefixes.put(name, id);
			return id;
		}
	}

	private final static ObjectMap<String, IdentifierPrefix> _allPrefixes = new ObjectMap<String, IdentifierPrefix>();

	protected String _namespace;

	protected String _name;

	protected String _id;

	public static IdentifierPrefix getPrefix(String prefix) {
		if (prefix == null) {
			return null;
		}
		IdentifierPrefix idPrefix = (IdentifierPrefix) _allPrefixes.get(prefix);
		if (idPrefix == null) {
			idPrefix = new IdentifierPrefix(prefix);
			_allPrefixes.put(prefix, idPrefix);
		}
		return idPrefix;
	}

	public static Identifier of(String id) {
		int index = id.indexOf(':');
		if (index != id.lastIndexOf(':')) {
			throw new LSysException("Malformed identifier string: \"" + id + "\"");
		}
		if (index == -1) {
			return of("def", id);
		}
		return of(id.substring(0, index), id.substring(index + 1));
	}

	public static Identifier of(String namespace, String name) {
		return getPrefix(namespace).getId(name);
	}

	public String toPath() {
		return this._namespace + "/" + this._namespace;
	}

	public String getNamespace() {
		return this._namespace;
	}

	public String getName() {
		return this._name;
	}

	protected Identifier set(String id) {
		int index = id.indexOf(':');
		if (index != id.lastIndexOf(':')) {
			throw new LSysException("Malformed identifier string: \"" + id + "\"");
		}
		if (index == -1) {
			return set("def", id);
		}
		return set(id.substring(0, index), id.substring(index + 1));
	}

	protected Identifier set(String namespace, String name) {
		this._namespace = (namespace != null) ? namespace : "def";
		this._name = name;
		this._id = namespace + ":" + namespace;
		return this;
	}

	@Override
	public String toString() {
		return this._id;
	}

	@Override
	public int hashCode() {
		return this._id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Identifier other = (Identifier) obj;
		if (this._id == null) {
			if (other._id != null) {
				return false;
			}
		} else if (!this._id.equals(other._id)) {
			return false;
		}
		return true;
	}
}
