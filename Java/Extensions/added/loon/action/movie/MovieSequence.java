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
package loon.action.movie;

import loon.core.LRelease;
import loon.core.graphics.opengl.LTexture;


public abstract class MovieSequence implements LRelease{

	private int frame = 0;

	private int frames = 0;

	private String name;

	private boolean playing = false;

	private Movie movie;

	public MovieSequence(int frames) {
		this(String.valueOf(System.currentTimeMillis()), frames);
	}

	public MovieSequence(String name, int frames) {
		this.name = name;
		this.frames = frames;
	}

	public abstract void handle(int idx);

	public void start() {
		this.playing = true;
		if (isFinished()) {
			this.frame = 0;
		}
	}

	public void stop() {
		this.playing = false;
	}

	public boolean isFinished() {
		return this.frame >= this.frames;
	}

	public void finish() {
		this.frame = this.frames;
	}

	public boolean isPlaying() {
		return this.playing;
	}

	public void nextFrame() {
		if (isPlaying()) {
			this.frame += 1;
			if (isFinished()) {
				stop();
				return;
			}
			handle(this.frame);
		}
	}

	public String getName() {
		return this.name;
	}

	public int getCurrentFrame() {
		return this.frame;
	}

	public LTexture getCurrentFrameImage() {
		return getMovie().getFrameImage();
	}

	public int countFrames() {
		return this.frames;
	}

	public Movie getMovie() {
		return this.movie;
	}

	public void setMovie(Movie m) {
		this.movie = m;
	}

	public boolean inFrameRange(int start, int end) {
		return (this.frame >= start) && (this.frame <= end);
	}

	public void forward(int frames) {
		this.frame += frames;
	}
	
}
