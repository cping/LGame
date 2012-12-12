using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Utils;

namespace Loon.Core.Graphics.Component
{
    public class CollisionNeighbourQuery : CollisionQuery {
	
		private float x;
	
		private float y;
	
		private float distance;
	
		private bool diag;
	
		private Type cls;
	
		public void Init(float x_0, float y_1, float distance_2, bool diag_3,
				Type cls_4) {
			this.x = x_0;
			this.y = y_1;
			this.distance = distance_2;
			this.diag = diag_3;
			this.cls = cls_4;
		}
	
		public bool CheckCollision(Actor actor) {
			if (this.cls != null && !this.cls.IsInstanceOfType(actor)) {
				return false;
			} else {
				float actorX = actor.GetX();
				float actorY = actor.GetY();
				if (actorX == this.x && actorY == this.y) {
					return false;
				} else {
					float ax = actor.GetX();
					float ay = actor.GetY();
					float dx;
					float dy;
					if (!this.diag) {
						dx = MathUtils.Abs(ax - this.x);
						dy = MathUtils.Abs(ay - this.y);
						return dx + dy <= this.distance;
					} else {
						dx = this.x - this.distance;
						dy = this.y - this.distance;
						float x2 = this.x + this.distance;
						float y2 = this.y + this.distance;
						return ax >= dx && ay >= dy && ax <= x2 && ay <= y2;
					}
				}
			}
		}
	}
}
