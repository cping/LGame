namespace Loon.Core.Input
{
    using Loon.Utils.Collection;
    using Microsoft.Xna.Framework.Input;

    public class LKey
    {

        internal int type;

        internal int keyCode;

        internal char keyChar;

        public LKey(byte[] o)
        {
            In(o);
        }

        internal LKey()
        {

        }

        LKey(LKey key)
        {
            this.type = key.type;
            this.keyCode = key.keyCode;
            this.keyChar = key.keyChar;
        }

        public bool Equals(LKey e)
        {
            if (e == null)
            {
                return false;
            }
            if (e == this)
            {
                return true;
            }
            if (e.type == type && e.keyCode == keyCode && e.keyChar == keyChar)
            {
                return true;
            }
            return false;
        }

        public char GetKeyChar()
        {
            return keyChar;
        }

        public int GetKeyCode()
        {
            return keyCode;
        }

        public int GetCode()
        {
            return type;
        }

        public bool IsDown()
        {
            return type == Key.KEY_DOWN;
        }

        public bool IsUp()
        {
            return type == Key.KEY_UP;
        }

        public byte[] Out()
        {
            ArrayByte touchByte = new ArrayByte();
            touchByte.WriteInt(type);
            touchByte.WriteInt(keyCode);
            touchByte.WriteInt(keyChar);
            return touchByte.GetData();
        }

        public void In(byte[] o)
        {
            ArrayByte touchByte = new ArrayByte(o);
            type = touchByte.ReadInt();
            keyCode = touchByte.ReadInt();
            keyChar = (char)touchByte.ReadInt();
            type = touchByte.ReadInt();
        }
    }
}
