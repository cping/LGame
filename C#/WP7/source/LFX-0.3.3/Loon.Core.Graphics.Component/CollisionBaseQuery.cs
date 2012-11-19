using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Graphics.Component
{
    public class CollisionBaseQuery: CollisionQuery {
	
		private Type cls;
	
		private Actor compareObject;
	
		public void Init(Type cls, Actor actor) {
			this.cls = cls;
			this.compareObject = actor;
		}
	
		public bool CheckOnlyCollision(Actor other) {
			return ((this.compareObject == null) ? true : other
					.Intersects(this.compareObject));
		}
	
		public bool CheckCollision(Actor other) {
			return (this.cls != null && !this.cls.IsInstanceOfType(other)) ? false
					: ((this.compareObject == null) ? true : other
							.Intersects(this.compareObject));
		}
	}
}
