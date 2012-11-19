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
package loon.physics;

import java.util.ArrayList;

import loon.core.geom.Vector2f;


import com.badlogic.gdx.physics.box2d.Fixture;

public class PhysicsContacts {

	ArrayList<Contact> contacts = new ArrayList<Contact>();
	
	int activeContacts = 0;
	
	public static class Contact {

		Fixture myFixture;
		Fixture otherFixture;

		boolean inContact = false;

		Vector2f normal = new Vector2f();

		public void setContact(Fixture myFixture, Fixture otherFixture,
				Vector2f normal) {
			this.myFixture = myFixture;
			this.otherFixture = otherFixture;
			this.normal.set(normal);
			this.inContact = true;
		}

		public void unsetContact() {
			this.inContact = false;
			myFixture = null;
			otherFixture = null;
			this.normal.set(0f, 0f);
		}

		public Fixture getMyFixture() {
			return myFixture;
		}

		public Fixture getOtherFixture() {
			return otherFixture;
		}

		public boolean isInContact() {
			return inContact;
		}

		public Vector2f getNormal() {
			return normal;
		}

	}


	public void addContact(com.badlogic.gdx.physics.box2d.Contact contact,
			boolean AB) {
		Vector2f normal = contact.getWorldManifold().getNormal();
		Fixture myFixture;
		Fixture otherFixture;
		if (AB) {
			myFixture = contact.getFixtureA();
			otherFixture = contact.getFixtureB();
		} else {
			myFixture = contact.getFixtureB();
			otherFixture = contact.getFixtureA();
			normal.mul(-1);
		}

		addContact(myFixture, otherFixture, normal);
	}

	void addContact(Fixture myFixture, Fixture otherFixture, Vector2f normal) {
		Contact contact = null;
		if (activeContacts == contacts.size()) {
			contact = new Contact();
			contacts.add(contact);
		} else {
			contact = contacts.get(activeContacts);
		}

		contact.setContact(myFixture, otherFixture, normal);
		activeContacts++;
	}

	public void removeContact(com.badlogic.gdx.physics.box2d.Contact contact,
			boolean AB) {
		Fixture myFixture;
		Fixture otherFixture;
		if (AB) {
			myFixture = contact.getFixtureA();
			otherFixture = contact.getFixtureB();
		} else {
			myFixture = contact.getFixtureB();
			otherFixture = contact.getFixtureA();
		}

		removeContact(myFixture, otherFixture);
	}

	public void removeContact(Fixture myFixture, Fixture otherFixture) {
		for (int i = 0; i < activeContacts; i++) {
			Contact contact = contacts.get(i);
			if (contact.myFixture != myFixture
					|| contact.otherFixture != otherFixture) {
				continue;
			}
			contact.unsetContact();
			contacts.set(i, contacts.get(activeContacts - 1));
			contacts.set(activeContacts - 1, contact);
			activeContacts--;
			return;
		}
	}

	public int getContactCount() {
		return activeContacts;
	}

	public Contact getContact(int i) {
		return contacts.get(i);
	}

	public boolean isInContact() {
		return activeContacts != 0;
	}
}
