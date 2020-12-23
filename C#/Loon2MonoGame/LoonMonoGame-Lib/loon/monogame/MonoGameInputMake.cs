using java.lang;
using loon.events;
using loon.geom;
using loon.utils;
using Microsoft.Xna.Framework.Input;
using static loon.events.TouchMake.Event;

namespace loon.monogame
{
    public class MonoGameInputMake : InputMake
    {

        private MouseState _previousMouseState;
        private Keys[] _previousPressedKeys;
        private MouseState _currentMouseState;
        private Keys[] _currentPressedKeys;
        private Vector2f _currentPos = new Vector2f();

        public MonoGameInputMake()
        {
            _currentMouseState = _previousMouseState = Mouse.GetState();
            _currentPressedKeys = _previousPressedKeys = new Keys[0];
        }

        public void Update()
        {
            this._previousMouseState = _currentMouseState;
            this._previousPressedKeys = _currentPressedKeys;
            this._currentMouseState = Mouse.GetState();
            this._currentPressedKeys = Keyboard.GetState().GetPressedKeys();
            int flags = ModifierFlags(IsKeyDown(Keys.LeftAlt) || IsKeyDown(Keys.RightAlt),
                        IsKeyDown(Keys.LeftControl) || IsKeyDown(Keys.RightControl),
                        IsKeyDown(Keys.LeftWindows) || IsKeyDown(Keys.RightWindows),
                        IsKeyDown(Keys.LeftShift) || IsKeyDown(Keys.RightShift));
            UpdateMouseInput(flags);
            UpdateKeyboardInput(flags);
        }


        protected void UpdateMouseInput(int flags)
        {
            _currentPos.Set(_currentMouseState.X, _currentMouseState.Y);

            if (_previousMouseState.X != _currentMouseState.X || _previousMouseState.Y != _currentMouseState.Y)
            {
                if (!IsAnyMouseButtonPressed(_currentMouseState))
                {
                    EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, -1, false, flags);
                }
            }
            else
            {

                if (_previousMouseState.LeftButton != _currentMouseState.LeftButton)
                {
                    if (_currentMouseState.LeftButton == ButtonState.Pressed)
                    {
                        EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, SysTouch.LEFT, true, flags);
                    }
                    else
                    {
                        EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, SysTouch.LEFT, false, flags);
                    }
                }

                if (_previousMouseState.MiddleButton != _currentMouseState.MiddleButton)
                {
                    if (_currentMouseState.MiddleButton == ButtonState.Pressed)
                    {
                        EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, SysTouch.MIDDLE, true, SysTouch.MIDDLE);
                    }
                    else
                    {
                        EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, SysTouch.MIDDLE, false, SysTouch.MIDDLE);
                    }
                }

                if (_previousMouseState.RightButton != _currentMouseState.RightButton)
                {
                    if (_currentMouseState.RightButton == ButtonState.Pressed)
                    {
                        EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, SysTouch.RIGHT, true, flags);
                    }
                    else
                    {

                        EmitMouseButton(TimeUtils.Millis(), _currentPos.x, _currentPos.y, SysTouch.RIGHT, false, flags);
                    }
                }
            }
        }

        protected void UpdateKeyboardInput(int flags)
        {

            for (int i = 0; i < _currentPressedKeys.Length; i++)
            {
                Keys key = _currentPressedKeys[i];
                if (!Contains(_previousPressedKeys, key))
                {
                    var monoGameKey = ToLoonKey(key);
                    if (monoGameKey != 0)
                    {
                        var keyCharacter = GetMonoGameKeyToChar(key);
                        EmitKeyPress(TimeUtils.Millis(), monoGameKey, keyCharacter, true, flags);
                    }
                }
            }

            for (int i = 0; i < _previousPressedKeys.Length; i++)
            {
                Keys key = _previousPressedKeys[i];
                if (!Contains(_currentPressedKeys, key))
                {
                    var monoGameKey = ToLoonKey(key);
                    if (monoGameKey != 0)
                    {
                        var keyCharacter = GetMonoGameKeyToChar(key);
                        EmitKeyPress(TimeUtils.Millis(), monoGameKey, keyCharacter, false, flags);
                    }
                }
            }
        }

        protected static bool Contains(Keys[] keys, Keys key)
        {
            for (int i = 0; i < keys.Length; i++)
            {
                if (keys[i] == key)
                {
                    return true;
                }
            }
            return false;
        }

        protected char GetMonoGameKeyToChar(Keys pressedKey)
        {
            if (pressedKey >= Keys.A && pressedKey <= Keys.Z)
            {
                return (char)(pressedKey - Keys.A + 'a');
            }
            if (pressedKey >= Keys.D0 && pressedKey <= Keys.D9)
            {
                return (char)(pressedKey - Keys.D0 + '0');
            }

            switch (pressedKey)
            {
                case Keys.Tab:
                    return '\t';
                case Keys.Space:
                    return ' ';
                case Keys.NumPad0:
                    return '0';
                case Keys.NumPad1:
                    return '1';
                case Keys.NumPad2:
                    return '2';
                case Keys.NumPad3:
                    return '3';
                case Keys.NumPad4:
                    return '4';
                case Keys.NumPad5:
                    return '5';
                case Keys.NumPad6:
                    return '6';
                case Keys.NumPad7:
                    return '7';
                case Keys.NumPad8:
                    return '8';
                case Keys.NumPad9:
                    return '9';
                case Keys.Multiply:
                    return '*';
                case Keys.Add:
                    return '+';
                case Keys.Separator:
                    return '.';
                case Keys.Subtract:
                    return '-';
                case Keys.Decimal:
                    return '.';
                case Keys.Divide:
                    return '/';
                case Keys.OemSemicolon:
                    return ';';
                case Keys.OemPlus:
                    return '+';
                case Keys.OemComma:
                    return ',';
                case Keys.OemMinus:
                    return '-';
                case Keys.OemPeriod:
                    return '.';
                case Keys.OemQuestion:
                    return '?';
                case Keys.OemTilde:
                    return '~';
                case Keys.OemOpenBrackets:
                    return '(';
                case Keys.OemPipe:
                    return '|';
                case Keys.OemCloseBrackets:
                    return ')';
                case Keys.OemQuotes:
                    return '"';
                case Keys.OemBackslash:
                    return '\\';
                default:
                    return '\0';
            }
        }

        public int GetX()
        {
            return _previousMouseState.X;
        }

        public int GetY()
        {
            return _previousMouseState.Y;
        }

        protected Keys ToMonoGameKey(int key)
        {
            if (key >= SysKey.A && key <= SysKey.Z)
            {
                return key - SysKey.A + Keys.A;
            }
            if (key >= SysKey.NUM_0 && key <= SysKey.NUM_9)
            {
                return key - SysKey.NUM_0 + Keys.NumPad0;
            }
            if (key >= SysKey.NUM_0 && key <= SysKey.NUM_9)
            {
                return key - SysKey.NUM_0 + Keys.D0;
            }

            switch (key)
            {
                case SysKey.BACK:
                    return Keys.Back;
                case SysKey.TAB:
                    return Keys.Tab;
                case SysKey.ENTER:
                    return Keys.Enter;
                case SysKey.ESCAPE:
                    return Keys.Escape;
                case SysKey.SPACE:
                    return Keys.Space;
                case SysKey.PAGE_UP:
                    return Keys.PageUp;
                case SysKey.PAGE_DOWN:
                    return Keys.PageDown;
                case SysKey.END:
                    return Keys.End;
                case SysKey.HOME:
                    return Keys.Home;
                case SysKey.LEFT:
                    return Keys.Left;
                case SysKey.UP:
                    return Keys.Up;
                case SysKey.RIGHT:
                    return Keys.Right;
                case SysKey.DOWN:
                    return Keys.Down;
                case SysKey.BUTTON_SELECT:
                    return Keys.Select;
                case SysKey.INSERT:
                    return Keys.Insert;
                case SysKey.DEL:
                    return Keys.Delete;
                case SysKey.NUM:
                    return Keys.Multiply;
                case SysKey.PLUS:
                    return Keys.Add;
                case SysKey.MINUS:
                    return Keys.Subtract;
                case SysKey.SLASH:
                    return Keys.Divide;
                case SysKey.SHIFT_LEFT:
                    return Keys.LeftShift;
                case SysKey.SHIFT_RIGHT:
                    return Keys.RightShift;
                case SysKey.CONTROL_LEFT:
                    return Keys.LeftControl;
                case SysKey.CONTROL_RIGHT:
                    return Keys.RightControl;
                case SysKey.ALT_LEFT:
                    return Keys.LeftAlt;
                case SysKey.ALT_RIGHT:
                    return Keys.RightAlt;
                case SysKey.MUTE:
                    return Keys.VolumeMute;
                case SysKey.VOLUME_UP:
                    return Keys.VolumeUp;
                case SysKey.VOLUME_DOWN:
                    return Keys.VolumeDown;
                case SysKey.MEDIA_NEXT:
                    return Keys.MediaNextTrack;
                case SysKey.MEDIA_PREVIOUS:
                    return Keys.MediaPreviousTrack;
                case SysKey.MEDIA_STOP:
                    return Keys.MediaStop;
                case SysKey.MEDIA_PLAY_PAUSE:
                    return Keys.MediaPlayPause;
                case SysKey.SEMICOLON:
                    return Keys.OemSemicolon;
                case SysKey.COMMA:
                    return Keys.OemComma;
                case SysKey.PERIOD:
                    return Keys.OemPeriod;
                case SysKey.BACKSLASH:
                    return Keys.OemBackslash;
                default:
                    return 0;
            }
        }

        protected int ToLoonKey(Keys key)
        {
            if (key >= Keys.A && key <= Keys.Z)
            {
                return key - Keys.A + SysKey.A;
            }

            if (key >= Keys.NumPad0 && key <= Keys.NumPad9)
            {
                return key - Keys.NumPad0 + SysKey.NUM_0;
            }
            if (key >= Keys.D0 && key <= Keys.D9)
            {
                return key - Keys.D0 + SysKey.NUM_0;
            }

            switch (key)
            {
                case Keys.Back:
                    return SysKey.BACK;
                case Keys.Tab:
                    return SysKey.TAB;
                case Keys.Enter:
                    return SysKey.ENTER;
                case Keys.Escape:
                    return SysKey.ESCAPE;
                case Keys.Space:
                    return SysKey.SPACE;
                case Keys.PageUp:
                    return SysKey.PAGE_UP;
                case Keys.PageDown:
                    return SysKey.PAGE_DOWN;
                case Keys.End:
                    return SysKey.END;
                case Keys.Home:
                    return SysKey.HOME;
                case Keys.Left:
                    return SysKey.LEFT;
                case Keys.Up:
                    return SysKey.UP;
                case Keys.Right:
                    return SysKey.RIGHT;
                case Keys.Down:
                    return SysKey.DOWN;
                case Keys.Select:
                    return SysKey.BUTTON_SELECT;
                case Keys.Insert:
                    return SysKey.INSERT;
                case Keys.Delete:
                    return SysKey.DEL;
                case Keys.Multiply:
                    return SysKey.NUM;
                case Keys.Add:
                    return SysKey.PLUS;
                case Keys.Subtract:
                    return SysKey.MINUS;
                case Keys.Divide:
                    return SysKey.SLASH;
                case Keys.LeftShift:
                    return SysKey.SHIFT_LEFT;
                case Keys.RightShift:
                    return SysKey.SHIFT_RIGHT;
                case Keys.LeftControl:
                    return SysKey.CONTROL_LEFT;
                case Keys.RightControl:
                    return SysKey.CONTROL_RIGHT;
                case Keys.LeftAlt:
                    return SysKey.ALT_LEFT;
                case Keys.RightAlt:
                    return SysKey.ALT_RIGHT;
                case Keys.BrowserBack:
                    return SysKey.BACK;
                case Keys.VolumeMute:
                    return SysKey.MUTE;
                case Keys.VolumeDown:
                    return SysKey.VOLUME_DOWN;
                case Keys.VolumeUp:
                    return SysKey.VOLUME_UP;
                case Keys.MediaNextTrack:
                    return SysKey.MEDIA_NEXT;
                case Keys.MediaPreviousTrack:
                    return SysKey.MEDIA_PREVIOUS;
                case Keys.MediaStop:
                    return SysKey.MEDIA_STOP;
                case Keys.MediaPlayPause:
                    return SysKey.MEDIA_PLAY_PAUSE;
                case Keys.OemSemicolon:
                    return SysKey.SEMICOLON;
                case Keys.OemPlus:
                    return SysKey.PLUS;
                case Keys.OemComma:
                    return SysKey.COMMA;
                case Keys.OemMinus:
                    return SysKey.MINUS;
                case Keys.OemPeriod:
                    return SysKey.PERIOD;
                case Keys.OemBackslash:
                    return SysKey.BACKSLASH;
                default:
                    return 0;
            }
        }

        public bool IsKeyJustPressed(int i)
        {
            var monogameKey = ToMonoGameKey(i);
            return Contains(_currentPressedKeys, monogameKey) && !Contains(_previousPressedKeys, monogameKey);
        }

        public bool IsKeyDown(int i)
        {
            return Contains(_currentPressedKeys, ToMonoGameKey(i));
        }

        public bool IsKeyDown(Keys k)
        {
            return Contains(_currentPressedKeys, k);
        }

        public bool IsKeyUp(int i)
        {
            return !IsKeyDown(i);
        }

        public bool IsKeyUp(Keys k)
        {
            return !IsKeyDown(k);
        }

        public bool JustTouched()
        {
            return _previousMouseState.LeftButton == ButtonState.Released && _currentMouseState.LeftButton == ButtonState.Pressed ||
                   _previousMouseState.MiddleButton == ButtonState.Released && _currentMouseState.MiddleButton == ButtonState.Pressed ||
                   _previousMouseState.RightButton == ButtonState.Released && _currentMouseState.RightButton == ButtonState.Pressed;
        }
        protected static bool IsAnyMouseButtonPressed(MouseState state)
        {
            return state.LeftButton == ButtonState.Pressed ||
                   state.MiddleButton == ButtonState.Pressed ||
                   state.RightButton == ButtonState.Pressed || 
                   state.XButton1 == ButtonState.Pressed || 
                   state.XButton2 == ButtonState.Pressed;
        }

        public override void Callback<T1>(LObject<T1> o)
        {
        }
    }
}
