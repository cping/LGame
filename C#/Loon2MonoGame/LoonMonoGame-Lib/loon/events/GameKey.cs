using loon.utils;

namespace loon.events
{
    public class GameKey
    {


        protected internal int type;

        protected internal int keyCode;

        protected internal int presses;

        protected internal bool down;

        protected internal char keyChar;

        protected internal double timer;

        protected internal GameKey()
        {
            Reset();
        }

        protected internal GameKey(GameKey key)
        {
            this.type = key.type;
            this.keyCode = key.keyCode;
            this.keyChar = key.keyChar;
            this.timer = key.timer;
            this.presses = key.presses;
            this.down = key.down;
        }

        public void Reset()
        {
            this.type = -1;
            this.keyCode = -1;
            this.keyChar = CharUtils.ToChar(-1);
            this.timer = 0;
            this.presses = 0;
            this.down = false;
        }

        public double GetTimer()
        {
            return timer;
        }

        public bool Toggle()
        {
            return Toggle(IsDown());
        }

        public bool Toggle(bool pressed)
        {
            if (pressed != this.down)
            {
                this.down = pressed;
            }
            if (pressed)
            {
                this.presses += 1;
            }
            return this.down;
        }

        public bool Equals(GameKey e)
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

        public int Type
        {
            get
            {
                return type;
            }
        }

        public bool IsShift()
        {
            return type == SysKey.SHIFT_LEFT || type == SysKey.SHIFT_RIGHT;
        }

        public bool IsCtrl()
        {
            return type == SysKey.CONTROL_LEFT || type == SysKey.CONTROL_RIGHT;
        }

        public bool IsAlt()
        {
            return type == SysKey.ALT_LEFT || type == SysKey.ALT_RIGHT;
        }

        public bool IsDown()
        {
            return type == SysKey.DOWN;
        }

        public bool IsUp()
        {
            return type == SysKey.UP;
        }

        public GameKey Cpy()
        {
            return new GameKey(this);
        }

        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("GameKey");
            builder.Kv("type", type).Comma().Kv("keyChar", keyChar).Comma().Kv("keyCode", keyCode).Comma().Kv("time",
                    timer);
            return builder.ToString();

        }


    }
}
