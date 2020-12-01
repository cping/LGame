namespace loon.events
{
    public class InputMapEvent
    {


        public const int NO_TYPE = -1;

        public const int CONFIRM = 0;

        public const int CANCEL = 1;

        public const int PAINT = 2;

        public const int NEXT = 3;

        public const int INFO = 4;

        public const int MOVE = 5;

        public const int BATTLE = 6;

        public static InputMapEvent SetID(int id)
        {
            return SetID(NO_TYPE, id);
        }

        public static InputMapEvent SetID(int e, int id)
        {
            return new InputMapEvent((e & unchecked((int)0xFFFF0000)) | id);
        }

        public static InputMapEvent GetID(int e)
        {
            return new InputMapEvent(e & unchecked(0x0000FFFF));
        }

        public static InputMapEvent SetType(int type)
        {
            return SetType(NO_TYPE, type);
        }

        public static InputMapEvent SetType(int e, int type)
        {
            return new InputMapEvent((e & unchecked(0x0000FFFF)) | (type << 16));
        }

        public static InputMapEvent GetType(int e)
        {
            return new InputMapEvent((e & unchecked((int)0xFFFF0000)) >> 16);
        }

        private int _code = NO_TYPE;

        public InputMapEvent() : this(NO_TYPE)
        {

        }

        public InputMapEvent(int c)
        {
            this._code = c;
        }

        public int GetCode()
        {
            return this._code;
        }

    }
}
