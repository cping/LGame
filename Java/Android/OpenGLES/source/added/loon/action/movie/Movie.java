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

import java.util.ArrayList;

import loon.action.map.Story;
import loon.core.LRelease;
import loon.core.graphics.opengl.LTexture;


public class Movie implements LRelease {

	private String name;

	private Story story;

	private ArrayList<MovieSequence> sequences = new ArrayList<MovieSequence>();

	private int currentSequence = 0;

	private boolean playing = false;

	private LTexture frameImage = null;

	public Movie(String name, Story story) {
		this.name = name;
		this.story = story;
	}

	public void addMovieSequence(MovieSequence sequence) {
		this.sequences.add(sequence);
		sequence.setMovie(this);
	}

	public MovieSequence getMovieSequence(int index) {
		return (MovieSequence) this.sequences.get(index);
	}

	public MovieSequence getCurrentMovieSequence() {
		return (MovieSequence) this.sequences.get(this.currentSequence);
	}

	public void start() {
		this.playing = true;
		if (this.currentSequence == 0)
			getCurrentMovieSequence().start();
	}

	public void stop() {
		this.playing = false;
	}

	public void handle() {
		if (this.playing) {
			MovieSequence seq = getCurrentMovieSequence();
			checkForNextSequence();
			seq.nextFrame();
		}
	}

	public String getName() {
		return this.name;
	}

	public Story getStory() {
		return this.story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public boolean isFinished() {
		return (getCurrentMovieSequence().isFinished())
				&& (this.currentSequence == this.sequences.size() - 1);
	}

	public int countMovieSequences() {
		return this.sequences.size();
	}

	public int countFrames() {
		int frames = 0;
		for (int i = 0; i < countMovieSequences(); i++) {
			frames += getMovieSequence(i).countFrames();
		}
		return frames;
	}

	public LTexture getFrameImage() {
		return this.frameImage;
	}

	public void setFrameImage(LTexture frameImage) {
		this.frameImage = frameImage;
	}

	public void skipSequence() {
		getCurrentMovieSequence().finish();
		checkForNextSequence();
	}

	private void checkForNextSequence() {
		MovieSequence seq = getCurrentMovieSequence();
		if ((seq.isFinished())
				&& (this.currentSequence < this.sequences.size() - 1)) {
			this.currentSequence += 1;
			seq = getCurrentMovieSequence();
			seq.start();
		}
		if ((seq.isFinished())
				&& (this.currentSequence == this.sequences.size())) {
			stop();
			return;
		}
	}

	public void forward(int frames) {
		getCurrentMovieSequence().forward(frames);
	}

	public void dispose() {
		for (MovieSequence m : sequences) {
			if (m != null) {
				m.dispose();
				m = null;
			}
		}
		sequences.clear();
	}
}
