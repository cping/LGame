namespace Loon.Action.Map {

    using System;
    using System.Collections.Generic;
    using Loon.Java;
    using Loon.Utils;
   
	public class Story {
	
		public Story() {
			this.timer = JavaRuntime.CurrentTimeMillis();
			this.storyName = timer.ToString();
			this.scenes = new List<Scene>();
		}
	
		private long timer;
	
		private string storyName;
	
		private List<Scene> scenes;
	
		public string GetStoryName() {
			return this.storyName;
		}
	
		public void SetStoryName(string s) {
			this.storyName = s;
		}
	
		public void AddScene(Scene scene) {
            CollectionUtils.Add(this.scenes, scene);
		}
	
		public Scene GetScene(int index) {
			return (Scene) this.scenes[index];
		}
	
		public Scene GetScene(string name) {
			int index = FindScene(name);
			if (index == -1) {
				return null;
			}
			return GetScene(index);
		}
	
		public int FindScene(string name) {
			for (int i = 0; i < this.scenes.Count; i++) {
				if (GetScene(i).GetName().Equals(name,StringComparison.InvariantCultureIgnoreCase)) {
					return i;
				}
			}
			return -1;
		}
	
		public Scene RemoveScene(int index) {
            return (Scene)CollectionUtils.RemoveAt(this.scenes, index);
		}
	
		public int CountScenes() {
			return this.scenes.Count;
		}
	
		public List<Scene> GetScenes() {
			return new List<Scene>(scenes);
		}
	
		public long GetTimer() {
			return timer;
		}
	
		public void SetTimer(long t) {
			this.timer = t;
		}
	
		public Character FindCharacter(string name) {
			for (int i = 0; i < CountScenes(); i++) {
				Scene scene = GetScene(i);
				int index = scene.FindCharacter(name);
				if (index != -1) {
					return scene.GetCharacter(name);
				}
			}
			return null;
		}
	
		public Scene FindSceneOfCharacter(string name) {
			for (int i = 0; i < CountScenes(); i++) {
				Scene scene = GetScene(i);
				int index = scene.FindCharacter(name);
				if (index != -1) {
					return scene;
				}
			}
			return null;
		}
	
		public bool MoveCharacter(string Charactername, string Scenename) {
			Character character = FindCharacter(Charactername);
			if (character != null) {
				Scene srcScene = FindSceneOfCharacter(Charactername);
				Scene dstScene = GetScene(Scenename);
				if ((srcScene != null) && (dstScene != null)) {
					srcScene.RemoveCharacter(srcScene.FindCharacter(Charactername));
					dstScene.AddCharacter(character);
					return true;
				}
			}
			return false;
		}
	
	}
}
