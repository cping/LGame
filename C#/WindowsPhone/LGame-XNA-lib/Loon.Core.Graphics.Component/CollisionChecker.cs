using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using Loon.Java.Collections;

namespace Loon.Core.Graphics.Component
{
    public interface CollisionChecker : LRelease
    {

        void Initialize(int cellSize);

        void AddObject(Actor actor);

        void RemoveObject(Actor actor);

        void Clear();

        void UpdateObjectLocation(Actor actor, float x, float y);

        void UpdateObjectSize(Actor actor);

        IList GetObjectsAt(float x, float y, Type cls);

        IList GetIntersectingObjects(Actor actor, Type cls);

        IList GetObjectsInRange(float x, float y, float r, Type cls);

        IList GetNeighbours(Actor actor, float distance, bool d, Type cls);

        IList GetObjects(Type actor);

        IList GetObjectsList();

        Actor GetOnlyObjectAt(Actor actor, float x, float y, Type cls);

        Actor GetOnlyIntersectingObject(Actor actor, Type cls);

        IIterator GetActorsIterator();

        IList GetActorsList();

    }
}
