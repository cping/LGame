using System.Collections.Generic;

namespace loon.events
{
    public sealed class LTouchLocationState
    {

        public static readonly LTouchLocationState Invalid = new LTouchLocationState("Invalid", InnerEnum.Invalid);
        public static readonly LTouchLocationState Dragged = new LTouchLocationState("Dragged", InnerEnum.Dragged);
        public static readonly LTouchLocationState Pressed = new LTouchLocationState("Pressed", InnerEnum.Pressed);
        public static readonly LTouchLocationState Released = new LTouchLocationState("Released", InnerEnum.Released);

        private static readonly IList<LTouchLocationState> valueList = new List<LTouchLocationState>();

        static LTouchLocationState()
        {
            valueList.Add(Invalid);
            valueList.Add(Dragged);
            valueList.Add(Pressed);
            valueList.Add(Released);
        }

        public enum InnerEnum
        {
            Invalid,
            Dragged,
            Pressed,
            Released
        }

        public readonly InnerEnum innerEnumValue;
        private readonly string nameValue;
        private readonly int ordinalValue;
        private static int nextOrdinal = 0;

        private LTouchLocationState(string name, InnerEnum innerEnum)
        {
            nameValue = name;
            ordinalValue = nextOrdinal++;
            innerEnumValue = innerEnum;
        }

        public int Value
        {
            get
            {
                return this.Ordinal();
            }
        }

        public static LTouchLocationState ForValue(int value)
        {
            return Values()[value];
        }

        public static IList<LTouchLocationState> Values()
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

        public static LTouchLocationState ValueOf(string name)
        {
            foreach (LTouchLocationState enumInstance in valueList)
            {
                if (enumInstance.nameValue == name)
                {
                    return enumInstance;
                }
            }
            throw new System.ArgumentException(name);
        }
    }
}
