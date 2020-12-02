using loon.geom;
using loon.utils;

namespace loon.events
{
    public class LTouchCollection : SortedList<LTouchLocation>
    {

        private bool _connected;

        public bool AnyTouch()
        {
            for (LIterator<LTouchLocation> it = ListIterator(); it.HasNext();)
            {
                LTouchLocation location = it.Next();
                if ((location.GetState() == LTouchLocationState.Pressed)
                        || (location.GetState() == LTouchLocationState.Dragged))
                {
                    return true;
                }
            }
            return false;
        }

        public LTouchCollection SetConnected(bool c)
        {
            this._connected = c;
            return this;
        }

        public bool IsConnected()
        {
            return this._connected;
        }

        public bool IsReadOnly()
        {
            return true;
        }

        public LTouchCollection()
        {
        }

        public LTouchCollection(SortedList<LTouchLocation> locations) : base(locations)
        {

        }

        public void Update()
        {
            for (int i = this.Size() - 1; i >= 0; --i)
            {
                LTouchLocation t = this.Get(i);
                int v = t.GetState().Value;
                if (v == LTouchLocationState.Pressed.Value)
                {
                    t.SetState(LTouchLocationState.Dragged);
                    t.SetPrevPosition(t.GetPosition());
                    this.Set(i, t.Cpy());
                }
                else if (v == LTouchLocationState.Dragged.Value)
                {
                    t.SetPrevState(LTouchLocationState.Dragged);
                    this.Set(i, t.Cpy());
                }
                else
                {
                    Remove(i);
                }
            }
        }

        public int FindIndexById(int id,
                RefObject<LTouchLocation> touchLocation)
        {
            for (int i = 0; i < this.Size(); i++)
            {
                LTouchLocation location = this.Get(i);
                if (location.GetId() == id)
                {
                    touchLocation.argvalue = this.Get(i);
                    return i;
                }
            }
            touchLocation.argvalue = new LTouchLocation();
            return -1;
        }

        public void Add(int id, Vector2f position)
        {
            for (int i = 0; i < Size(); i++)
            {
                if (this.Get(i).GetId() == id)
                {
                    Clear();
                }
            }
            Add(new LTouchLocation(id, LTouchLocationState.Pressed, position));
        }

        public void Add(int id, float x, float y)
        {
            for (int i = 0; i < Size(); i++)
            {
                if (this.Get(i).GetId() == id)
                {
                    Clear();
                }
            }
            Add(new LTouchLocation(id, LTouchLocationState.Pressed, x, y));
        }

        public void Update(int id, LTouchLocationState state, float posX,
                float posY)
        {
            if (state == LTouchLocationState.Pressed)
            {
                throw new LSysException(
                        "Argument 'state' cannot be TouchLocationState.Pressed.");
            }

            for (int i = 0; i < Size(); i++)
            {
                if (this.Get(i).GetId() == id)
                {
                    LTouchLocation touchLocation = this.Get(i);
                    touchLocation.SetPosition(posX, posY);
                    touchLocation.SetState(state);
                    this.Set(i, touchLocation);
                    return;
                }
            }
            Clear();
        }

        public void Update(int id, LTouchLocationState state,
                Vector2f position)
        {
            if (state == LTouchLocationState.Pressed)
            {
                throw new LSysException(
                        "Argument 'state' cannot be TouchLocationState.Pressed.");
            }

            for (int i = 0; i < Size(); i++)
            {
                if (this.Get(i).GetId() == id)
                {
                    LTouchLocation touchLocation = this.Get(i);
                    touchLocation.SetPosition(position);
                    touchLocation.SetState(state);
                    this.Set(i, touchLocation);
                    return;
                }
            }
            Clear();
        }
    }
}
