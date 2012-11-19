using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Graphics.Component
{
    public class CollisionPointQuery : CollisionQuery
    {

        private float x;

        private float y;

        private Type cls;

        public void Init(float x_0, float y_1, Type cls_2)
        {
            this.x = x_0;
            this.y = y_1;
            this.cls = cls_2;
        }

        public bool CheckCollision(Actor actor)
        {
            return (this.cls != null && !this.cls.IsInstanceOfType(actor)) ? false : actor
                    .ContainsPoint(this.x, this.y);
        }
    }
}
