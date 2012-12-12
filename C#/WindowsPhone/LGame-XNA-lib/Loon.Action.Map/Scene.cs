namespace Loon.Action.Map {

    using System;
    using System.Collections.Generic;
    using Loon.Utils;
   
	public class Scene {
	
		public Scene() {
			this.items = new List<Item>();
			this.characters = new List<Character>();
		}
	
		private string name;
	
		private List<Item> items;
	
		private List<Character> characters;
	
		public string GetName() {
			return this.name;
		}

        public void SetName(string n)
        {
			this.name = n;
		}
	
		public void AddItem(Item item) {
            CollectionUtils.Add(this.items, item);
		}
	
		public Item GetItem(int index) {
			return (Item) this.items[index];
		}
	
		public Item GetItem(string n) {
			int index = FindItem(n);
			if (index == -1) {
				return null;
			}
			return GetItem(index);
		}
	
		public int FindItem(string n) {
			for (int i = 0; i < this.items.Count; i++) {
				if (GetItem(i).GetName().Equals(n,StringComparison.InvariantCultureIgnoreCase)) {
					return i;
				}
			}
			return -1;
		}
	
		public Item RemoveItem(int index) {
            return (Item)CollectionUtils.RemoveAt(this.items, index);
		}
	
		public int CountItems() {
			return this.items.Count;
		}
	
		public void AddCharacter(Character character) {
            CollectionUtils.Add(this.characters, character);
		}
	
		public Character GetCharacter(int index) {
			return (Character) this.characters[index];
		}
	
		public Character GetCharacter(string n) {
			int index = FindCharacter(n);
			if (index == -1) {
				return null;
			}
			return GetCharacter(index);
		}
	
		public int FindCharacter(string n) {
			for (int i = 0; i < this.characters.Count; i++) {
				if (GetCharacter(i).GetName().Equals(n,StringComparison.InvariantCultureIgnoreCase)) {
					return i;
				}
			}
			return -1;
		}
	
		public Character RemoveCharacter(int index) {
            return (Character)CollectionUtils.RemoveAt(this.characters, index);
		}
	
		public int CountCharacters() {
			return this.characters.Count;
		}
	}
}
