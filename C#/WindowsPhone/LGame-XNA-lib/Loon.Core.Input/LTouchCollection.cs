using Loon.Java.Generics;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Core.Geom;
using System;
namespace Loon.Core.Input {

	public class LTouchCollection : List<LTouchLocation> {
	
		private const long serialVersionUID = 1L;
	
		private bool isConnected;
	
		public bool AnyTouch() {
			foreach (LTouchLocation location  in  this) {
				if ((location.GetState() == LTouchLocationState.Pressed)
						|| (location.GetState() == LTouchLocationState.Dragged)) {
					return true;
				}
			}
			return false;
		}

        internal void SetConnected(bool s)
        {
            this.isConnected = s;
        }
	
		public bool GetIsConnected() {
			return this.isConnected;
		}
	
		public bool GetIsReadOnly() {
			return true;
		}
	
		public LTouchCollection() {
		}
	
		public LTouchCollection(ICollection<LTouchLocation> locations):base(locations) {
			
		}
	
		public void Update() {
			for (int i = this.Count - 1; i >= 0; --i) {
				LTouchLocation t = this[i];
				switch (t.GetState()) {
				case LTouchLocationState.Pressed:
					t.SetState(LTouchLocationState.Dragged);
					t.SetPrevPosition(t.GetPosition());
					this[i]=t.Clone();
					break;
				case LTouchLocationState.Dragged:
					t.SetPrevState(LTouchLocationState.Dragged);
					this[i]=t.Clone();
					break;
				case LTouchLocationState.Released:
				case LTouchLocationState.Invalid:
					CollectionUtils.RemoveAt(this,i);
					break;
				}
			}
		}
	
		public int FindIndexById(int id,
				RefObject<LTouchLocation> touchLocation) {
			for (int i = 0; i < this.Count; i++) {
				LTouchLocation location = this[i];
				if (location.GetId() == id) {
					touchLocation.argvalue = this[i];
					return i;
				}
			}
			touchLocation.argvalue = new LTouchLocation();
			return -1;
		}
	
		public void Add(int id, Vector2f position) {
			for (int i = 0; i < Count; i++) {
				if (this[i].id == id) {
                    CollectionUtils.Clear(this);
				}
			}
            CollectionUtils.Add(this, new LTouchLocation(id, LTouchLocationState.Pressed, position));
		}
	
		public void Add(int id, float x, float y) {
			for (int i = 0; i < Count; i++) {
				if (this[i].id == id) {
                    CollectionUtils.Clear(this);
				}
			}
            CollectionUtils.Add(this, new LTouchLocation(id, LTouchLocationState.Pressed, x, y));
		}
	
		public void Update(int id, LTouchLocationState state, float posX,
				float posY) {
			if (state == LTouchLocationState.Pressed) {
				throw new ArgumentException(
						"Argument 'state' cannot be TouchLocationState.Pressed.");
			}
	
			for (int i = 0; i < Count; i++) {
				if (this[i].id == id) {
					LTouchLocation touchLocation = this[i];
					touchLocation.SetPosition(posX, posY);
					touchLocation.SetState(state);
					this[i]=touchLocation;
					return;
				}
			}
            CollectionUtils.Clear(this);
		}
	
		public void Update(int id, LTouchLocationState state,
				Vector2f position) {
			if (state == LTouchLocationState.Pressed) {
				throw new ArgumentException(
						"Argument 'state' cannot be TouchLocationState.Pressed.");
			}
	
			for (int i = 0; i < Count; i++) {
				if (this[i].id == id) {
					LTouchLocation touchLocation = this[i];
					touchLocation.SetPosition(position);
					touchLocation.SetState(state);
					this[i]=touchLocation;
					return;
				}
			}
            CollectionUtils.Clear(this);
		}
	}
}
