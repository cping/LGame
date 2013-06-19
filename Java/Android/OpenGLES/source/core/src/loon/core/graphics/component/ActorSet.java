package loon.core.graphics.component;
import java.util.AbstractSet;
import java.util.Iterator;

/**
 * 
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

@SuppressWarnings({"rawtypes"})
public class ActorSet extends AbstractSet {

	private ActorSet.ListNode listHeadTail = new ActorSet.ListNode();

	private ActorSet.ListNode[] hashMap = new ActorSet.ListNode[0];

	private int numActors = 0;

	private int code = 0;

	@Override
	public int hashCode() {
		return this.code;
	}

	public boolean add(Actor actor) {
		if (this.contains(actor)) {
			return false;
		} else {
			++this.numActors;
			ActorSet.ListNode newNode = new ActorSet.ListNode(actor,
					this.listHeadTail.prev);
			int seq = actor.getSequenceNumber();
			if (this.numActors >= 2 * this.hashMap.length) {
				this.resize();
			} else {
				int hash = seq % this.hashMap.length;
				ActorSet.ListNode hashHead = this.hashMap[hash];
				this.hashMap[hash] = newNode;
				newNode.setHashListHead(hashHead);
			}
			this.code += seq;
			return true;
		}
	}

	public void addAll(Object[] o) {
		int size = o.length;
		this.numActors = size;
		this.resize();
		for (int i = 0; i < size; i++) {
			Actor actor = (Actor) o[i];
			ActorSet.ListNode newNode = new ActorSet.ListNode(actor,
					this.listHeadTail.prev);
			int seq = actor.getSequenceNumber();
			int hash = seq % this.hashMap.length;
			ActorSet.ListNode hashHead = this.hashMap[hash];
			this.hashMap[hash] = newNode;
			newNode.setHashListHead(hashHead);
			this.code += seq;
		}
	}

	private void resize(int size) {
		this.hashMap = new ActorSet.ListNode[size];
		for (ActorSet.ListNode currentActor = this.listHeadTail.next; currentActor != this.listHeadTail; currentActor = currentActor.next) {
			int seq = currentActor.actor.getSequenceNumber();
			int hash = seq % size;
			ActorSet.ListNode hashHead = this.hashMap[hash];
			this.hashMap[hash] = currentActor;
			currentActor.setHashListHead(hashHead);
		}
	}

	private void resize() {
		resize(this.numActors);
	}

	public boolean contains(Actor actor) {
		return this.getActorNode(actor) != null;
	}

	private ActorSet.ListNode getActorNode(Actor actor) {
		if (this.hashMap.length == 0) {
			return null;
		} else {
			int seq = actor.getSequenceNumber();
			int hash = seq % this.hashMap.length;
			ActorSet.ListNode hashHead = this.hashMap[hash];
			if (hashHead == null) {
				return null;
			} else if (hashHead.actor == actor) {
				return hashHead;
			} else {
				for (ActorSet.ListNode curNode = hashHead.nextHash; curNode != hashHead; curNode = curNode.nextHash) {
					if (curNode.actor == actor) {
						return curNode;
					}
				}

				return null;
			}
		}
	}

	public boolean remove(Actor actor) {
		ActorSet.ListNode actorNode = this.getActorNode(actor);
		if (actorNode != null) {
			this.remove(actorNode);
			this.code -= actor.getSequenceNumber();
			return true;
		} else {
			return false;
		}
	}

	private void remove(ActorSet.ListNode actorNode) {
		int seq = actorNode.actor.getSequenceNumber();
		int hash = seq % this.hashMap.length;
		if (this.hashMap[hash] == actorNode) {
			this.hashMap[hash] = actorNode.nextHash;
			if (this.hashMap[hash] == actorNode) {
				this.hashMap[hash] = null;
			}
		}

		actorNode.remove();
		--this.numActors;
		if (this.numActors <= this.hashMap.length / 2) {
			this.resize();
		}

	}

	@Override
	public int size() {
		return this.numActors;
	}

	@Override
	public Iterator iterator() {
		return new ActorSet.ActorSetIterator();
	}

	private class ListNode {

		Actor actor;

		ActorSet.ListNode next;

		ActorSet.ListNode prev;

		ActorSet.ListNode nextHash;

		ActorSet.ListNode prevHash;

		public ListNode() {
			this.next = this;
			this.prev = this;
		}

		public ListNode(Actor actor, ActorSet.ListNode listTail) {
			this.actor = actor;
			this.next = listTail.next;
			this.prev = listTail;
			listTail.next = this;
			this.next.prev = this;
		}

		public void setHashListHead(ActorSet.ListNode oldHead) {
			if (oldHead == null) {
				this.nextHash = this;
				this.prevHash = this;
			} else {
				this.nextHash = oldHead;
				this.prevHash = oldHead.prevHash;
				oldHead.prevHash = this;
				this.prevHash.nextHash = this;
			}

		}

		public void remove() {
			this.next.prev = this.prev;
			this.prev.next = this.next;
			this.nextHash.prevHash = this.prevHash;
			this.prevHash.nextHash = this.nextHash;
		}
	}

	private class ActorSetIterator implements Iterator {

		ActorSet.ListNode currentNode;

		public ActorSetIterator() {
			this.currentNode = ActorSet.this.listHeadTail;
		}

		@Override
		public boolean hasNext() {
			return this.currentNode.next != ActorSet.this.listHeadTail;
		}

		@Override
		public Object next() {
			this.currentNode = this.currentNode.next;
			return this.currentNode.actor;
		}

		@Override
		public void remove() {
			ActorSet.this.remove(this.currentNode);
		}
	}
}
