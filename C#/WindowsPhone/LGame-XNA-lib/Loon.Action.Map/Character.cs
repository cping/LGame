namespace Loon.Action.Map {

    using System;
    using System.Collections.Generic;
    using Loon.Utils;
  
	public class Character {
	
		public Character() {
			this.attributes = new List<Attribute>();
			this.items = new List<Item>();
		}
	
		private string name;

        private List<Attribute> attributes;

        private List<Item> items;
	
		public string GetName() {
			return this.name;
		}
	
		public void SetName(string n) {
			this.name = n;
		}
	
		public void AddAttribute(Attribute attribute) {
			CollectionUtils.Add(this.attributes,attribute);
		}
	
		public Attribute GetAttribute(int index) {
			return (Attribute) this.attributes[index];
		}
	
		public Attribute GetAttribute(string n) {
			int index = FindAttribute(n);
			if (index == -1) {
				return null;
			}
			return GetAttribute(index);
		}
	
		public int FindAttribute(string n) {
			for (int i = 0; i < this.attributes.Count; i++) {
				if (GetAttribute(i).GetName().Equals(n,StringComparison.InvariantCultureIgnoreCase)) {
					return i;
				}
			}
			return -1;
		}
	
		public void RemoveAttribute(int index) {
            CollectionUtils.RemoveAt(this.attributes, index);
		}
	
		public int CountAttributes() {
			return this.attributes.Count;
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
	}
}
