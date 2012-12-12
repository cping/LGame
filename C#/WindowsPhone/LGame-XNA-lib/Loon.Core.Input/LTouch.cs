using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Utils.Collection;
using Microsoft.Xna.Framework;

namespace Loon.Core.Input
{
    public class LTouch
    {

        private int modifiers;
        private Point startLocation;
        private Point currentLocation;

        private bool consumed = false;

        public bool Consumed
        {
            get { return consumed; }
            set { consumed = value; }
        }

        public const int SHIFT_DOWN = 1 << 0;

        public const int CTRL_DOWN = 1 << 1;

        public const int ALT_DOWN = 1 << 2;

        internal protected int type;

        internal protected float x0, y0;

        internal protected int button;

        internal protected int pointer;

        internal protected int id;

        protected LTouch(LTouch touch)
        {
            this.type = touch.type;
            this.x0 = touch.x0;
            this.y0 = touch.y0;
            this.button = touch.button;
            this.pointer = touch.pointer;
            this.id = touch.id;
        }

        public LTouch(byte[] o)
        {
            In(o);
        }

        internal LTouch()
        {
        }

        public void Set(Point currentLocation, int modifiers)
        {
            Set(currentLocation, Touch.LEFT, modifiers);
        }

        public void Set(Point currentLocation, int button, int modifiers)
        {
            Set(currentLocation, currentLocation, button, modifiers);
        }

        public void Set(Point startLocation, Point currentLocation, int modifiers)
        {
            Set(startLocation, currentLocation, -1, modifiers);
        }

        public void Set(Point startLocation, Point currentLocation, int mouseButton, int modifiers)
        {
            this.startLocation = startLocation;
            this.currentLocation = currentLocation;
            this.modifiers = modifiers;
            this.button = mouseButton;
            this.x0 = currentLocation.X;
            this.y0 = currentLocation.Y;
        }

        public Point StartLocation
        {
            get { return startLocation; }
        }

        public Point CurrentLocation
        {
            get { return currentLocation; }
        }

        public bool IsCtrlDown
        {
            get { return (modifiers & CTRL_DOWN) > 0; }
        }

        public bool IsShiftDown
        {
            get { return (modifiers & SHIFT_DOWN) > 0; }
        }

        public bool IsAltDown
        {
            get { return (modifiers & ALT_DOWN) > 0; }
        }

        public void Offset(float x, float y)
        {
            this.x0 = x;
            this.y0 = y;
        }

        public void OffsetX(float x)
        {
            this.x0 += x;
        }

        public void OffsetY(float y)
        {
            this.y0 += y;
        }

        public bool Equals(LTouch e)
        {
            if (e == null)
            {
                return false;
            }
            if (e == this)
            {
                return true;
            }
            if (e.type == type && e.x0 == x0 && e.y0 == y0 && e.button == button
                    && e.pointer == pointer && e.id == id)
            {
                return true;
            }
            return false;
        }

        public int GetButton()
        {
            return button;
        }

        public int GetPointer()
        {
            return pointer;
        }

        public int GetCode()
        {
            return type;
        }

        public int GetID()
        {
            return id;
        }

        public int X()
        {
            return (int)x0;
        }

        public int Y()
        {
            return (int)y0;
        }

        public float GetX()
        {
            return x0;
        }

        public float GetY()
        {
            return y0;
        }

        public bool IsDown()
        {
            return button == Touch.TOUCH_DOWN;
        }

        public bool IsUp()
        {
            return button == Touch.TOUCH_UP;
        }

        public bool IsMove()
        {
            return button == Touch.TOUCH_MOVE;
        }

        public bool IsDrag()
        {
            return LInputFactory.isDraging;
        }

        public bool IsLeft()
        {
            return type == Touch.LEFT;
        }

        public bool IsMiddle()
        {
            return type == Touch.MIDDLE;
        }

        public bool IsRight()
        {
            return type == Touch.RIGHT;
        }

        public byte[] Out()
        {
            ArrayByte touchByte = new ArrayByte();
            touchByte.WriteInt(X());
            touchByte.WriteInt(Y());
            touchByte.WriteInt(GetButton());
            touchByte.WriteInt(GetPointer());
            touchByte.WriteInt(GetCode());
            return touchByte.GetData();
        }

        public void In(byte[] o)
        {
            ArrayByte touchByte = new ArrayByte(o);
            x0 = touchByte.ReadInt();
            y0 = touchByte.ReadInt();
            button = touchByte.ReadInt();
            pointer = touchByte.ReadInt();
            type = touchByte.ReadInt();
        }
    }
}
