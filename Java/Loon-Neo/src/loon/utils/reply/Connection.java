/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.reply;

public abstract class Connection implements Closeable {

	public static Connection join(final Connection... conns) {
		return new Connection() {
			@Override
			public void close() {
				for (Connection c : conns) {
					c.close();
				}
			}

			@Override
			public Connection once() {
				for (Connection c : conns) {
					c.once();
				}
				return this;
			}

			@Override
			public Connection setPriority(int priority) {
				for (Connection c : conns) {
					c.setPriority(priority);
				}
				return this;
			}

			@Override
			public Connection holdWeakly() {
				for (Connection c : conns) {
					c.holdWeakly();
				}
				return this;
			}
		};
	}

	public abstract void close();

	public abstract Connection once();

	public abstract Connection setPriority(int priority);

	public abstract Connection holdWeakly();
}
