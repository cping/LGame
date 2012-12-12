using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Graphics.Component
{
    public interface CollisionQuery
    {
        bool CheckCollision(Actor actor);
    }
}
