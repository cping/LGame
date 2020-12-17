using loon.geom;
using loon.utils;

namespace loon.events
{
    public abstract class Event
    {

        public static readonly int F_PREVENT_DEFAULT = 1 << 0;

        protected static readonly int F_ALT_DOWN = 1 << 1;
        protected static readonly int F_CTRL_DOWN = 1 << 2;
        protected static readonly int F_SHIFT_DOWN = 1 << 3;
        protected static readonly int F_META_DOWN = 1 << 4;

        public class InputEvent : Event
        {

            public int flags;

            public readonly double time;

            public bool IsAltDown()
            {
                return IsSet(F_ALT_DOWN);
            }

            public bool IsCtrlDown()
            {
                return IsSet(F_CTRL_DOWN);
            }

            public bool IsShiftDown()
            {
                return IsSet(F_SHIFT_DOWN);
            }

            public bool IsMetaDown()
            {
                return IsSet(F_META_DOWN);
            }

            public bool IsSet(int flag)
            {
                return (flags & flag) != 0;
            }

            public void SetFlag(int flag)
            {
                flags |= flag;
            }

            public void ClearFlag(int flag)
            {
                flags &= ~flag;
            }

            public void UpdateFlag(int flag, bool on)
            {
                if (on)
                {
                    SetFlag(flag);
                }
                else
                {
                    ClearFlag(flag);
                }
            }


            public override string ToString()
            {
                StrBuilder builder = new StrBuilder(Name()).Append('[');
                AddFields(builder);
                return builder.Append(']').ToString();
            }

            public static int ModifierFlags(bool altP, bool ctrlP,
                    bool metaP, bool shiftP)
            {
                int flags = 0;
                if (altP)
                    flags |= F_ALT_DOWN;
                if (ctrlP)
                    flags |= F_CTRL_DOWN;
                if (metaP)
                    flags |= F_META_DOWN;
                if (shiftP)
                    flags |= F_SHIFT_DOWN;
                return flags;
            }

            protected internal InputEvent(int flags, double time)
            {
                this.flags = flags;
                this.time = time;
            }

            protected internal virtual string Name()
            {
                return "Input";
            }

            protected internal virtual void AddFields(StrBuilder builder)
            {
                builder.Append("time=").Append(time).Append(", flags=")
                        .Append(flags);
            }
        }

        public class XYEvent : InputEvent, XY
        {

            public readonly float x;

            public readonly float y;

            public int X()
            {
                return (int)x;
            }

            public int Y()
            {
                return (int)y;
            }

            public float GetX()
            {
                return x;
            }


            public float GetY()
            {
                return y;
            }

            protected XYEvent(int flags, double time, float x, float y) : base(flags, time)
            {

                this.x = x;
                this.y = y;
            }


            protected internal override string Name()
            {
                return "XY";
            }


            protected internal override void AddFields(StrBuilder builder)
            {
                base.AddFields(builder);
                builder.Append(", x=").Append(x).Append(", y=").Append(y);
            }
        }
    }
}
