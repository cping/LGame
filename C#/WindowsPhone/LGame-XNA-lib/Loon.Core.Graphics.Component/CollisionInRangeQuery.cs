using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Geom;
using Loon.Utils;

namespace Loon.Core.Graphics.Component
{
    public class CollisionInRangeQuery : CollisionQuery
    {

        private float dx;

        private float dy;

        private float dist;

        private float x;

        private float y;

        private float r;

        private RectBox obj0;

        public void Init(float x_0, float y_1, float r_2)
        {
            this.x = x_0;
            this.y = y_1;
            this.r = r_2;
        }

        public bool CheckCollision(Actor actor)
        {

            obj0 = actor.GetRectBox();

            dx = MathUtils.Abs(obj0.GetCenterX() - x);
            dy = MathUtils.Abs(obj0.GetCenterY() - y);

            dist =  MathUtils.Sqrt(dx * dx + dy * dy);

            return dist <= this.r;
        }
    }
}
