/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core;

import java.util.ArrayList;
import java.util.List;

public class CallbackList<T> implements Callback<T> {

	public static <T> List<Callback<T>> createAdd(
			List<Callback<T>> list, Callback<T> callback) {
		if (list == null)
			list = new ArrayList<Callback<T>>();
		list.add(callback);
		return list;
	}

	public static <T> List<Callback<T>> dispatchSuccessClear(
			List<Callback<T>> list, T result) {
		if (list != null) {
			for (int ii = 0, ll = list.size(); ii < ll; ii++)
				list.get(ii).onSuccess(result);
		}
		return null;
	}

	public static <T> List<Callback<T>> dispatchFailureClear(
			List<Callback<T>> list, Throwable cause) {
		if (list != null) {
			for (int ii = 0, ll = list.size(); ii < ll; ii++)
				list.get(ii).onFailure(cause);
		}
		return null;
	}

	private ArrayList<Callback<T>> callbacks = new ArrayList<Callback<T>>();

	protected void checkState() {
		if (callbacks == null) {
			throw new IllegalStateException("CallbackList has already fired !");
		}
	}

	public static <T> CallbackList<T> create(Callback<T> callback) {
		CallbackList<T> list = new CallbackList<T>();
		list.add(callback);
		return list;
	}

	public CallbackList<T> add(Callback<T> callback) {
		checkState();
		callbacks.add(callback);
		return this;
	}

	public void remove(Callback<T> callback) {
		checkState();
		callbacks.remove(callback);
	}

	@Override
	public void onSuccess(T result) {
		checkState();
		for (Callback<T> cb : callbacks) {
			cb.onSuccess(result);
		}
		callbacks = null;
	}

	@Override
	public void onFailure(Throwable t) {
		checkState();
		for (Callback<T> cb : callbacks) {
			cb.onFailure(t);
		}
		callbacks = null;
	}

}
