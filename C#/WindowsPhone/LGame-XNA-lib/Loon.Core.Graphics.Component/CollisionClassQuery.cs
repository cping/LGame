using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Graphics.Component
{
   public class CollisionClassQuery : CollisionQuery {
	
		private Type cls;
	
		private CollisionQuery subQuery;
	
		public CollisionClassQuery(Type c, CollisionQuery s) {
			this.cls = c;
			this.subQuery = s;
		}
	
		public bool CheckCollision(Actor actor) {
			return (this.cls.IsInstanceOfType(actor)) ? this.subQuery.CheckCollision(actor)
					: false;
		}
	}
}
