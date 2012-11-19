using Loon.Core.Geom;
using Loon.Action.Map;
using Loon.Utils;

namespace Loon.Core
{
    public abstract class LObject
    {

        public object Tag;

        public float alpha = 1f;

        protected internal RectBox rect;

        protected internal string name;

        protected internal Vector2f location = new Vector2f(0, 0);

        protected internal int layer;

        protected internal float rotation;

        public void SetTransparency(int alpha)
        {
            SetAlpha(alpha / 255f);
        }

        public virtual int GetTransparency()
        {
            return (int)(alpha * 255);
        }

        public virtual void SetAlpha(float a)
        {
            this.alpha = a;
        }

        public virtual float GetAlpha()
        {
            return this.alpha;
        }

        public virtual void SetRotation(float r)
        {
            this.rotation = r;
            if (rect != null)
            {
                rect = NumberUtils.GetBounds(location.x, location.y, GetWidth(),
                        GetHeight(), r, rect);
            }
        }

        public virtual float GetRotation()
        {
            return rotation;
        }

        public abstract void Update(long elapsedTime);

        public float x
        {
            get
            {
                return location.x;
            }
        }

        public float y
        {
            get
            {
                return location.y;
            }
        }

        public void CenterOnScreen()
        {
            LObject.CenterOn(this, LSystem.screenRect.width,
                    LSystem.screenRect.height);
        }

        public void BottomOnScreen()
        {
            LObject.BottomOn(this, LSystem.screenRect.width,
                    LSystem.screenRect.height);
        }

        public void LeftOnScreen()
        {
            LObject.LeftOn(this, LSystem.screenRect.width,
                    LSystem.screenRect.height);
        }

        public void RightOnScreen()
        {
            LObject.RightOn(this, LSystem.screenRect.width,
                    LSystem.screenRect.height);
        }

        public void TopOnScreen()
        {
            LObject.TopOn(this, LSystem.screenRect.width, LSystem.screenRect.height);
        }

        public RectBox GetCollisionArea()
        {
            return GetRect(GetX(), GetY(), GetWidth(), GetHeight());
        }

        protected internal RectBox GetRect(float x, float y, float w, float h)
        {
            if (rect == null)
            {
                rect = new RectBox(x, y, w, h);
            }
            else
            {
                rect.SetBounds(x, y, w, h);
            }
            return rect;
        }

        public void SetName(string name)
        {
            this.name = name;
        }

        public virtual string GetName()
        {
            return name;
        }

        public virtual int GetLayer()
        {
            return layer;
        }

        public virtual void SetLayer(int layer)
        {
            this.layer = layer;
        }

        public virtual void Move_45D_up()
        {
            Move_45D_up(1);
        }

        public virtual void Move_45D_up(int multiples)
        {
            location.Move_multiples(Field2D.UP, multiples);
        }

        public virtual void Move_45D_left()
        {
            Move_45D_left(1);
        }

        public virtual void Move_45D_left(int multiples)
        {
            location.Move_multiples(Field2D.LEFT, multiples);
        }

        public virtual void Move_45D_right()
        {
            Move_45D_right(1);
        }

        public virtual void Move_45D_right(int multiples)
        {
            location.Move_multiples(Field2D.RIGHT, multiples);
        }

        public virtual void Move_45D_down()
        {
            Move_45D_down(1);
        }

        public virtual void Move_45D_down(int multiples)
        {
            location.Move_multiples(Field2D.DOWN, multiples);
        }

        public virtual void Move_up()
        {
            Move_up(1);
        }

        public virtual void Move_up(int multiples)
        {
            location.Move_multiples(Field2D.TUP, multiples);
        }

        public virtual void Move_left()
        {
            Move_left(1);
        }

        public virtual void Move_left(int multiples)
        {
            location.Move_multiples(Field2D.TLEFT, multiples);
        }

        public virtual void Move_right()
        {
            Move_right(1);
        }

        public virtual void Move_right(int multiples)
        {
            location.Move_multiples(Field2D.TRIGHT, multiples);
        }

        public virtual void Move_down()
        {
            Move_down(1);
        }

        public virtual void Move_down(int multiples)
        {
            location.Move_multiples(Field2D.TDOWN, multiples);
        }

        public virtual void Move(Vector2f vector2D)
        {
            location.Move(vector2D);
        }

        public virtual void Move(float x, float y)
        {
            location.Move(x, y);
        }

        public virtual void SetLocation(float x, float y)
        {
            location.SetLocation(x, y);
        }

        public virtual void SetLocation(int x, int y)
        {
            location.SetLocation(x, y);
        }

        public virtual int X()
        {
            return (int)location.GetX();
        }

        public virtual int Y()
        {
            return (int)location.GetY();
        }

        public virtual float GetX()
        {
            return location.GetX();
        }

        public virtual float GetY()
        {
            return location.GetY();
        }

        public virtual void SetX(int x)
        {
            location.SetX(x);
        }

        public virtual void SetX(float x)
        {
            location.SetX(x);
        }

        public virtual void SetY(int y)
        {
            location.SetY(y);
        }

        public virtual void SetY(float y)
        {
            location.SetY(y);
        }

        public virtual Vector2f GetLocation()
        {
            return location;
        }

        public virtual void SetLocation(Vector2f location)
        {
            this.location = location;
        }

        public static void CenterOn(LObject obj0, int w, int h)
        {
            obj0.SetLocation(w / 2 - obj0.GetWidth() / 2,
                    h / 2 - obj0.GetHeight() / 2);
        }

        public static void TopOn(LObject obj0, int w, int h)
        {
            obj0.SetLocation(w / 2 - h / 2, 0);
        }

        public static void LeftOn(LObject obj0, int w, int h)
        {
            obj0.SetLocation(0, h / 2 - obj0.GetHeight() / 2);
        }

        public static void RightOn(LObject obj0, int w, int h)
        {
            obj0.SetLocation(w - obj0.GetWidth(), h / 2 - obj0.GetHeight()
                    / 2);
        }

        public static void BottomOn(LObject obj0, int w, int h)
        {
            obj0.SetLocation(w / 2 - obj0.GetWidth() / 2,
                    h - obj0.GetHeight());
        }

        public void CenterOn(LObject obj0)
        {
            CenterOn(obj0, GetWidth(), GetHeight());
        }

        public void TopOn(LObject obj0)
        {
            TopOn(obj0, GetWidth(), GetHeight());
        }

        public void LeftOn(LObject obj0)
        {
            LeftOn(obj0, GetWidth(), GetHeight());
        }

        public void RightOn(LObject obj0)
        {
            RightOn(obj0, GetWidth(), GetHeight());
        }

        public void BottomOn(LObject obj0)
        {
            BottomOn(obj0, GetWidth(), GetHeight());
        }

        public abstract int GetWidth();

        public abstract int GetHeight();

    }
}
