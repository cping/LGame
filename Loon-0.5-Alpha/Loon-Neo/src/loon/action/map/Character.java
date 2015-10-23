package loon.action.map;

import java.util.ArrayList;

public class Character {

	private String name;

	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();

	private ArrayList<Item> items = new ArrayList<Item>();

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public Attribute getAttribute(int index) {
		return this.attributes.get(index);
	}

	public Attribute getAttribute(String name) {
		int index = findAttribute(name);
		if (index == -1) {
			return null;
		}
		return getAttribute(index);
	}

	public int findAttribute(String name) {
		for (int i = 0; i < this.attributes.size(); i++) {
			if (getAttribute(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public void removeAttribute(int index) {
		this.attributes.remove(index);
	}

	public int countAttributes() {
		return this.attributes.size();
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public Item getItem(int index) {
		return this.items.get(index);
	}

	public Item getItem(String name) {
		int index = findItem(name);
		if (index == -1) {
			return null;
		}
		return getItem(index);
	}

	public int findItem(String name) {
		for (int i = 0; i < this.items.size(); i++) {
			if (getItem(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Item removeItem(int index) {
		return this.items.remove(index);
	}

	public int countItems() {
		return this.items.size();
	}
}
