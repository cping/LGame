package loon.action.map;

import java.util.ArrayList;

/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public class Story {

	private long timer = System.currentTimeMillis();

	private String storyName = String.valueOf(timer);

	private ArrayList<Scene> scenes = new ArrayList<Scene>();

	public String getStoryName() {
		return this.storyName;
	}

	public void setStoryName(String storyName) {
		this.storyName = storyName;
	}

	public void addScene(Scene scene) {
		this.scenes.add(scene);
	}

	public Scene getScene(int index) {
		return this.scenes.get(index);
	}

	public Scene getScene(String name) {
		int index = findScene(name);
		if (index == -1) {
			return null;
		}
		return getScene(index);
	}

	public int findScene(String name) {
		for (int i = 0; i < this.scenes.size(); i++) {
			if (getScene(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Scene removeScene(int index) {
		return this.scenes.remove(index);
	}

	public int countScenes() {
		return this.scenes.size();
	}

	public ArrayList<Scene> getScenes() {
		return new ArrayList<Scene>(scenes);
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}

	public Character findCharacter(String name) {
		for (int i = 0; i < countScenes(); i++) {
			Scene scene = getScene(i);
			int index = scene.findCharacter(name);
			if (index != -1) {
				return scene.getCharacter(name);
			}
		}
		return null;
	}

	public Scene findSceneOfCharacter(String name) {
		for (int i = 0; i < countScenes(); i++) {
			Scene scene = getScene(i);
			int index = scene.findCharacter(name);
			if (index != -1) {
				return scene;
			}
		}
		return null;
	}

	public boolean moveCharacter(String Charactername, String Scenename) {
		Character character = findCharacter(Charactername);
		if (character != null) {
			Scene srcScene = findSceneOfCharacter(Charactername);
			Scene dstScene = getScene(Scenename);
			if ((srcScene != null) && (dstScene != null)) {
				srcScene.removeCharacter(srcScene.findCharacter(Charactername));
				dstScene.addCharacter(character);
				return true;
			}
		}
		return false;
	}

}
