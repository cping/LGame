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
package loon.events;

import loon.utils.reply.ObjRef;

public class EventRef {

	public static final ObjRef<Runnable> doR(Runnable e) {
		return ObjRef.of(e);
	}

	public static final ObjRef<TaskRunnable> doRC(TaskRunnable e) {
		return ObjRef.of(e);
	}

	public static final ObjRef<EventActionCheck> doNC(EventActionCheck e) {
		return ObjRef.of(e);
	}

	public static final <T> ObjRef<EventActionT<T>> doT(EventActionT<T> e) {
		return ObjRef.of(e);
	}

	public static final ObjRef<EventActionN> doN(EventActionN e) {
		return ObjRef.of(e);
	}

	public static final <T, N> ObjRef<EventActionTN<T, N>> doTN(EventActionTN<T, N> e) {
		return ObjRef.of(e);
	}

	public static final <T> ObjRef<ActionUpdate> doAU(ActionUpdate u) {
		return ObjRef.of(u);
	}

	public static final <T> ObjRef<UpdateableT<T>> doUT(UpdateableT<T> u) {
		return ObjRef.of(u);
	}

	public static final <T> ObjRef<Updateable> doU(Updateable u) {
		return ObjRef.of(u);
	}

	public static final <T> ObjRef<ClickListener> doClick(ClickListener u) {
		return ObjRef.of(u);
	}

	public static final <T> ObjRef<Touched> doTouched(Touched u) {
		return ObjRef.of(u);
	}
}
