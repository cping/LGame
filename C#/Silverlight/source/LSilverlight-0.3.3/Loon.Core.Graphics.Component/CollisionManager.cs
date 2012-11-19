using System;
using System.Collections;
using Loon.Utils;
using Loon.Utils.Collection;
using Loon.Java.Collections;

namespace Loon.Core.Graphics.Component
{
    public class CollisionManager : CollisionChecker
    {
        
		private ArrayMap freeObjects = new ArrayMap();
	
		private HashedSet collisionClasses = new HashedSet();
	
		private CollisionChecker collisionChecker = new BSPCollisionChecker();
	
		public void Initialize(int cellSize) {
			this.collisionChecker.Initialize(cellSize);
		}
	
		public void Clear() {
			 lock (typeof(CollisionManager)) {
						if (collisionChecker != null) {
							collisionChecker.Dispose();
							collisionChecker.Clear();
						}
						if (freeObjects != null) {
							freeObjects.Clear();
						}
						if (collisionClasses != null) {
							CollectionUtils.Clear(collisionClasses);
						}
					}
		}
	
		private void MakeCollisionObjects(Type cls, bool includeSubclasses) {
			ArrayMap.Entry[] entries;
			ArrayMap.Entry entry;
			if (cls == null) {
				entries = this.freeObjects.ToEntrys();
				for (int i = 0; i < entries.Length; i++) {
					entry = entries[i];
                    IEnumerator itr = ((LinkedList)entry.GetValue()).GetEnumerator();
					for (; itr.MoveNext();) {
						Actor actor = (Actor) itr.Current;
						this.collisionChecker.AddObject(actor);
					}
					CollectionUtils.Add(this.collisionClasses,entry.GetKey());
				}
				this.freeObjects.Clear();
			} else if (!CollectionUtils.Contains(cls,this.collisionClasses)) {
				LinkedList entries2 = (LinkedList) this.freeObjects.Remove(cls);
				if (entries2 != null) {
					CollectionUtils.Add(this.collisionClasses,cls);
                    for (IEnumerator it = entries2.GetEnumerator(); it.MoveNext(); )
                    {
						Actor entry1 = (Actor) it.Current;
						this.collisionChecker.AddObject(entry1);
					}
				}
			}
			if (includeSubclasses) {
				entries = this.freeObjects.ToEntrys();
				for (int i_0 = 0; i_0 < entries.Length; i_0++) {
					entry = entries[i_0];
					if (cls.IsAssignableFrom((Type) entry.GetKey())) {
						this.MakeCollisionObjects((Type) entry.GetKey(), false);
					}
				}
			}
		}
	
		private void PrepareForCollision(Actor actor, Type cls) {
			this.MakeCollisionObjects(actor.GetType(), false);
			this.MakeCollisionObjects(cls, true);
		}
	
		public void AddObject(Actor actor) {
			Type cls = actor.GetType();
			if (CollectionUtils.Contains(cls,this.collisionClasses)) {
				this.collisionChecker.AddObject(actor);
			} else {
				LinkedList classSet = (LinkedList) this.freeObjects.Get(cls);
				if (classSet == null) {
					classSet = new LinkedList();
					this.freeObjects.Put(cls, classSet);
				}
				CollectionUtils.Add(classSet,actor);
			}
		}
	
		public IList GetIntersectingObjects(Actor actor, Type cls) {
			 lock (typeof(CollisionManager)) {
						this.PrepareForCollision(actor, cls);
						return this.collisionChecker.GetIntersectingObjects(actor, cls);
					}
		}
	
		public IList GetNeighbours(Actor actor, float distance, bool diag,
				Type cls) {
			 lock (typeof(CollisionManager)) {
						this.PrepareForCollision(actor, cls);
						return this.collisionChecker.GetNeighbours(actor, distance, diag,
								cls);
					}
		}
	
		public IList GetObjects(Type cls) {
			 lock (typeof(CollisionManager)) {
						IList result = this.collisionChecker.GetObjects(cls);
						ArrayMap.Entry[] entries = this.freeObjects.ToEntrys();
						for (int i = 0; i < entries.Length; i++) {
							ArrayMap.Entry entry = entries[i];
							if (cls == null || cls.IsAssignableFrom((Type) entry.GetKey())) {
								CollectionUtils.AddAll((ICollection) entry.GetValue(),result);
							}
						}
						return result;
					}
		}
	
		public IList GetObjectsAt(float x, float y, Type cls) {
			 lock (typeof(CollisionManager)) {
						this.MakeCollisionObjects(cls, true);
						return this.collisionChecker.GetObjectsAt(x, y, cls);
					}
		}
	
		public IList GetObjectsInRange(float x, float y, float r, Type cls) {
			 lock (typeof(CollisionManager)) {
						this.MakeCollisionObjects(cls, true);
						return this.collisionChecker.GetObjectsInRange(x, y, r, cls);
					}
	
		}
	
		public IList GetObjectsList() {
			 lock (typeof(CollisionManager)) {
						return this.GetObjects((Type) null);
			
					}
		}
	
		public Actor GetOnlyIntersectingObject(Actor o, Type cls) {
			 lock (typeof(CollisionManager)) {
						this.PrepareForCollision(o, cls);
						return this.collisionChecker.GetOnlyIntersectingObject(o, cls);
					}
		}
	
		public Actor GetOnlyObjectAt(Actor o, float dx, float dy, Type cls) {
			 lock (typeof(CollisionManager)) {
						this.PrepareForCollision(o, cls);
						return this.collisionChecker.GetOnlyObjectAt(o, dx, dy, cls);
					}
		}
	
		public void RemoveObject(Actor o) {
			 lock (typeof(CollisionManager)) {
						LinkedList classSet = (LinkedList) this.freeObjects.Get(o
								.GetType());
						if (classSet != null) {
                            classSet.Remove(o);
						} else {
							this.collisionChecker.RemoveObject(o);
						}
					}
		}
	
		public void RemoveObject(Type cls) {
			 lock (typeof(CollisionManager)) {
						LinkedList classSet = (LinkedList) this.freeObjects.Get(cls);
						if (collisionClasses != null) {
							CollectionUtils.Remove(collisionClasses,cls);
						}
						if (classSet != null) {
                            classSet.Remove(cls);
						}
					}
		}
	
		public void UpdateObjectLocation(Actor o, float oldX, float oldY) {
			 lock (typeof(CollisionManager)) {
						if (!this.freeObjects.ContainsKey(o.GetType())) {
							this.collisionChecker.UpdateObjectLocation(o, oldX, oldY);
						}
					}
		}
	
		public void UpdateObjectSize(Actor o) {
			 lock (typeof(CollisionManager)) {
						if (!this.freeObjects.ContainsKey(o.GetType())) {
							this.collisionChecker.UpdateObjectSize(o);
						}
					}
		}
	
		public void Dispose() {
			 lock (typeof(CollisionManager)) {
						if (freeObjects != null) {
							freeObjects.Clear();
							freeObjects = null;
						}
						if (collisionClasses != null) {
							CollectionUtils.Clear(collisionClasses);
							collisionClasses = null;
						}
						if (collisionChecker != null) {
							collisionChecker.Dispose();
							collisionChecker.Clear();
							collisionChecker = null;
						}
					}
		}
	
		public IIterator GetActorsIterator() {
			 lock (typeof(CollisionManager)) {
						return collisionChecker.GetActorsIterator();
					}
		}
	
		public IList GetActorsList() {
			 lock (typeof(CollisionManager)) {
						return collisionChecker.GetActorsList();
					}
		}
    }
}
