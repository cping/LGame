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
package loon.media;

import loon.core.Callback;

public interface Sound {

	public static class Silence implements Sound {
		@Override
		public boolean prepare() {
			return false;
		}

		@Override
		public boolean play() {
			return false;
		}

		@Override
		public void stop() {
		}

		@Override
		public void setLooping(boolean looping) {
		}

		@Override
		public float volume() {
			return 0;
		}

		@Override
		public void setVolume(float volume) {
		}

		@Override
		public boolean isPlaying() {
			return false;
		}

		@Override
		public void release() {
		}

		@Override
		public void addCallback(Callback<Sound> callback) {
			callback.onSuccess(this);
		}
	}

	public static class Error extends Silence {
		private final Exception error;

		public Error(Exception error) {
			this.error = error;
		}

		@Override
		public void addCallback(Callback<Sound> callback) {
			callback.onFailure(error);
		}
	}

	boolean prepare();

	boolean play();

	void stop();

	void setLooping(boolean looping);

	float volume();

	void setVolume(float volume);

	boolean isPlaying();

	void release();

	void addCallback(Callback<Sound> callback);
}
