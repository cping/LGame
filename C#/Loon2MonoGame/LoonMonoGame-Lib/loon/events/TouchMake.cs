using loon.utils;
using System.Collections.Generic;

namespace loon.events
{
    public class TouchMake
    {
        public class Event : loon.events.Event.XYEvent
        {

            public sealed class Kind
            {
                public static readonly Kind START = new Kind("START", InnerEnum.START, true, false);
                public static readonly Kind MOVE = new Kind("MOVE", InnerEnum.MOVE, false, false);
                public static readonly Kind END = new Kind("END", InnerEnum.END, false, true);
                public static readonly Kind CANCEL = new Kind("CANCEL", InnerEnum.CANCEL, false, true);

                private static readonly IList<Kind> valueList = new List<Kind>();

                static Kind()
                {
                    valueList.Add(START);
                    valueList.Add(MOVE);
                    valueList.Add(END);
                    valueList.Add(CANCEL);
                }

                public enum InnerEnum
                {
                    START,
                    MOVE,
                    END,
                    CANCEL
                }

                public readonly InnerEnum innerEnumValue;
                private readonly string nameValue;
                private readonly int ordinalValue;
                private static int nextOrdinal = 0;
                public readonly bool isStart, isEnd;

                internal Kind(string name, InnerEnum innerEnum, bool isStart, bool isEnd)
                {
                    this.isStart = isStart;
                    this.isEnd = isEnd;

                    nameValue = name;
                    ordinalValue = nextOrdinal++;
                    innerEnumValue = innerEnum;
                }

                public static IList<Kind> Values()
                {
                    return valueList;
                }

                public int Ordinal()
                {
                    return ordinalValue;
                }

                public override string ToString()
                {
                    return nameValue;
                }

                public static Kind valueOf(string name)
                {
                    foreach (Kind enumInstance in Kind.valueList)
                    {
                        if (enumInstance.nameValue == name)
                        {
                            return enumInstance;
                        }
                    }
                    throw new System.ArgumentException(name);
                }
            }

            public readonly Kind kind;

            public readonly int id;

            public readonly float pressure;

            public readonly float size;

            public Event(int flags, double time, float x, float y, Kind kind, int id) : this(flags, time, x, y, kind, id, -1, -1)
            {
            }

            public Event(int flags, double time, float x, float y, Kind kind, int id, float pressure, float size) : base(flags, time, x, y)
            {
                this.kind = kind;
                this.id = id;
                this.pressure = pressure;
                this.size = size;
            }

            protected internal override string Name()
            {
                return "Touch";
            }

            protected internal override void AddFields(StrBuilder builder)
            {
                base.AddFields(builder);
                builder.Append(", kind=").Append(kind).Append(", id=").Append(id).Append(", pressure=").Append(pressure).Append(", size=").Append(size);
            }
        }

    }
}
