using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Media;
using Microsoft.Xna.Framework.Input.Touch;
using Loon.Core.Timer;
using Loon.Utils;
using Microsoft.Xna.Framework.Input;
using Loon.Utils.Collection;


namespace Loon.Core.Input
{

    public static class Touch
    {

        public const int UPPER_LEFT = 0;

        public const int UPPER_RIGHT = 1;

        public const int LOWER_LEFT = 2;

        public const int LOWER_RIGHT = 3;

        public const int TOUCH_DOWN = 0;

        public const int TOUCH_UP = 1;

        public const int TOUCH_MOVE = 2;

        public const int LEFT = 0;

        public const int RIGHT = 1;

        public const int MIDDLE = 2;

        public static int GetButton()
        {
            return LInputFactory.finalTouch.button;
        }

        public static int GetPointer()
        {
            return LInputFactory.finalTouch.pointer;
        }

        public static int GetCode()
        {
            return LInputFactory.finalTouch.type;
        }

        public static int X()
        {
            return LInputFactory.finalTouch.X();
        }

        public static int Y()
        {
            return LInputFactory.finalTouch.Y();
        }

        public static float GetX()
        {
            return LInputFactory.finalTouch.X();
        }

        public static float GetY()
        {
            return LInputFactory.finalTouch.Y();
        }

        public static bool IsDown()
        {
            return LInputFactory.finalTouch.IsDown();
        }

        public static bool IsUp()
        {
            return LInputFactory.finalTouch.IsUp();
        }

        public static bool IsMove()
        {
            return LInputFactory.finalTouch.IsMove();
        }

        public static bool IsDrag()
        {
            return LInputFactory.isDraging;
        }

        public static bool isLeft()
        {
            return LInputFactory.finalTouch.IsLeft();
        }

        public static bool isMiddle()
        {
            return LInputFactory.finalTouch.IsMiddle();
        }

        public static bool isRight()
        {
            return LInputFactory.finalTouch.IsRight();
        }
    }

    
	public static class Key {

		public const int KEY_DOWN = 0;

		public const int KEY_UP = 1;

		public const int KEY_TYPED = 2;

		public const int ANY_KEY = -1;

		public const int NUM_0 = 7;

		public const int NUM_1 = 8;

		public const int NUM_2 = 9;

		public const int NUM_3 = 10;

		public const int NUM_4 = 11;

		public const int NUM_5 = 12;

		public const int NUM_6 = 13;

		public const int NUM_7 = 14;

		public const int NUM_8 = 15;

		public const int NUM_9 = 16;

		public const int A = 29;

		public const int ALT_LEFT = 57;

		public const int ALT_RIGHT = 58;

		public const int APOSTROPHE = 75;

		public const int AT = 77;

		public const int B = 30;

		public const int BACK = 4;

		public const int BACKSLASH = 73;

		public const int C = 31;

		public const int CALL = 5;

		public const int CAMERA = 27;

		public const int CLEAR = 28;

		public const int COMMA = 55;

		public const int D = 32;

		public const int DEL = 67;

		public const int BACKSPACE = 67;

		public const int FORWARD_DEL = 112;

		public const int DPAD_CENTER = 23;

		public const int DPAD_DOWN = 20;

		public const int DPAD_LEFT = 21;

		public const int DPAD_RIGHT = 22;

		public const int DPAD_UP = 19;

		public const int CENTER = 23;

		public const int DOWN = 20;

		public const int LEFT = 21;

		public const int RIGHT = 22;

		public const int UP = 19;

		public const int E = 33;

		public const int ENDCALL = 6;

		public const int ENTER = 66;

		public const int ENVELOPE = 65;

		public const int EQUALS = 70;

		public const int EXPLORER = 64;

		public const int F = 34;

		public const int FOCUS = 80;

		public const int G = 35;

		public const int GRAVE = 68;

		public const int H = 36;

		public const int HEADSETHOOK = 79;

		public const int HOME = 3;

		public const int I = 37;

		public const int J = 38;

		public const int K = 39;

		public const int L = 40;

		public const int LEFT_BRACKET = 71;

		public const int M = 41;

		public const int MEDIA_FAST_FORWARD = 90;

		public const int MEDIA_NEXT = 87;

		public const int MEDIA_PLAY_PAUSE = 85;

		public const int MEDIA_PREVIOUS = 88;

		public const int MEDIA_REWIND = 89;

		public const int MEDIA_STOP = 86;

		public const int MENU = 82;

		public const int MINUS = 69;

		public const int MUTE = 91;

		public const int N = 42;

		public const int NOTIFICATION = 83;

		public const int NUM = 78;

		public const int O = 43;

		public const int P = 44;

		public const int PERIOD = 56;

		public const int PLUS = 81;

		public const int POUND = 18;

		public const int POWER = 26;

		public const int Q = 45;

		public const int R = 46;

		public const int RIGHT_BRACKET = 72;

		public const int S = 47;

		public const int SEARCH = 84;

		public const int SEMICOLON = 74;

		public const int SHIFT_LEFT = 59;

		public const int SHIFT_RIGHT = 60;

		public const int SLASH = 76;

		public const int SOFT_LEFT = 1;

		public const int SOFT_RIGHT = 2;

		public const int SPACE = 62;

		public const int STAR = 17;

		public const int SYM = 63;

		public const int T = 48;

		public const int TAB = 61;

		public const int U = 49;

		public const int UNKNOWN = 0;

		public const int V = 50;

		public const int VOLUME_DOWN = 25;

		public const int VOLUME_UP = 24;

		public const int W = 51;

		public const int X = 52;

		public const int Y = 53;

		public const int Z = 54;

		public const int META_ALT_LEFT_ON = 16;

		public const int META_ALT_ON = 2;

		public const int META_ALT_RIGHT_ON = 32;

		public const int META_SHIFT_LEFT_ON = 64;

		public const int META_SHIFT_ON = 1;

		public const int META_SHIFT_RIGHT_ON = 128;

		public const int META_SYM_ON = 4;

		public const int CONTROL_LEFT = 129;

		public const int CONTROL_RIGHT = 130;

		public const int ESCAPE = 131;

		public const int END = 132;

		public const int INSERT = 133;

		public const int PAGE_UP = 92;

		public const int PAGE_DOWN = 93;

		public const int PICTSYMBOLS = 94;

		public const int SWITCH_CHARSET = 95;

		public const int BUTTON_A = 96;

		public const int BUTTON_B = 97;

		public const int BUTTON_C = 98;

		public const int BUTTON_X = 99;

		public const int BUTTON_Y = 100;

		public const int BUTTON_Z = 101;

		public const int BUTTON_L1 = 102;

		public const int BUTTON_R1 = 103;

		public const int BUTTON_L2 = 104;

		public const int BUTTON_R2 = 105;

		public const int BUTTON_THUMBL = 106;

		public const int BUTTON_THUMBR = 107;

		public const int BUTTON_START = 108;

		public const int BUTTON_SELECT = 109;

		public const int BUTTON_MODE = 110;

		public static char GetKeyChar() {
            return LInputFactory.finalKey.keyChar;
		}

		public static int GetKeyCode() {
            return LInputFactory.finalKey.keyCode;
		}

		public static int GetCode() {
            return LInputFactory.finalKey.keyCode;
		}

		public static bool IsDown() {
            return LInputFactory.finalKey.IsDown();
		}

		public static bool IsUp() {
            return LInputFactory.finalKey.IsUp();
		}

        public static void Clear()
        {
            LInputFactory.keys.Clear();
        }

        public static void AddKey(int key)
        {
            LInputFactory.keys.Add(key);
        }

        public static void RemoveKey(int key)
        {
            LInputFactory.keys.RemoveValue(key);
        }

        public static bool IsKeyPressed(int key)
        {
            if (key == Key.ANY_KEY)
            {
                return LInputFactory.keys.size > 0;
            }
            else
            {
                return LInputFactory.keys.Contains(key);
            }
        }
	}

    public class LInputFactory
    {
        
	internal static IntArray keys = new IntArray();

        private LFlickerListener flickListener;

        public bool MoveForward { get; protected set; }

        public bool MoveBackward { get; protected set; }

        public bool TurnLeft { get; protected set; }

        public bool TurnRight { get; protected set; }

        public LFlickerListener Flicker
        {
            get
            {
                return flickListener;
            }
            set
            {
                flickListener = value;
            }
        }

        public const int FLICK_UP = 0;

        public const int FLICK_RIGHT = 1;

        public const int FLICK_LEFT = 2;

        public const int FLICK_DOWN = 3;

        private static Keys cKey;

        private static GamePadButtons cButton;

        public Keys KeyValue
        {
            get
            {
                return cKey;
            }
        }

        public GamePadButtons ButtonValue
        {
            get
            {
                return cButton;
            }
        }

        public const int maxInputs = 4;

        private const int click_count = 5;

        private static bool mousePressed;

        private static bool isClick;

        private static Point startLocation;

        private LTimer locked = new LTimer(0);

        private float offsetTouchX, offsetMoveX, offsetTouchY, offsetMoveY;

        public TouchCollection TouchState;

        private LProcess handler;

        internal readonly static LTouch finalTouch = new LTouch();

        internal readonly static LKey finalKey = new LKey();

        public readonly bool[] gamePadWasConnected;

        private char lastKeyCharPressed;

        internal static bool isDraging;

        private int halfWidth, halfHeight;

        private MouseState currentMouseboardState;

        private MouseState lastMouseboardState;

        private readonly KeyboardState[] lastKeyboardStates;

        private readonly GamePadState[] lastGamePadStates;

        private readonly KeyboardState[] currentKeyboardStates;

        private readonly GamePadState[] currentGamePadStates;

        //游戏手柄链接状态集合
        public bool[] GamePadWasConnecteds
        {
            get
            {
                return gamePadWasConnected;
            }
        }

        public KeyboardState[] CurrentKeyboardStates
        {
            get
            {
                return currentKeyboardStates;
            }
        }

        public GamePadState[] CurrentGamePadStates
        {
            get
            {
                return currentGamePadStates;
            }
        }

        public KeyboardState[] LastKeyboardStates
        {
            get
            {
                return lastKeyboardStates;
            }
        }

        public GamePadState[] LastGamePadStates
        {
            get
            {
                return lastGamePadStates;
            }
        }

        private static bool limit_back = true;

        public bool IsGamePadBackExit
        {
            get
            {
                return limit_back;
            }

            set
            {
                limit_back = value;
            }

        }

        //游戏手柄监听用缓存
        private static LinkedList<GamePadListener> gamePadListeners;

        private static LinkedList<GamePadListener> gamePadListenersToAdd = new LinkedList<GamePadListener>();

        private static LinkedList<GamePadListener> gamePadListenersToRemove = new LinkedList<GamePadListener>();

        public LInputFactory(LProcess p)
        {

            handler = p;

            this.halfWidth = handler.GetWidth() / 2;
            this.halfHeight = handler.GetHeight() / 2;

            gamePadListeners = new LinkedList<GamePadListener>();

            gamePadWasConnected = new bool[maxInputs];
            currentKeyboardStates = new KeyboardState[maxInputs];
            currentGamePadStates = new GamePadState[maxInputs];

            lastKeyboardStates = new KeyboardState[maxInputs];
            lastGamePadStates = new GamePadState[maxInputs];

            mousePressed = false;

        }

        public static void AddGamePadListener(GamePadListener g)
        {
            gamePadListenersToAdd.AddLast(g);
        }

        public static void RemoveGamePadListener(GamePadListener g)
        {
            gamePadListenersToRemove.AddLast(g);
        }

        private static void UpdateGamePadListeners()
        {
            UpdateListeners<GamePadListener>(gamePadListeners, gamePadListenersToAdd, gamePadListenersToRemove);
        }

        private static void UpdateListeners<T>(LinkedList<T> listenerList, LinkedList<T> addList, LinkedList<T> removeList)
        {
            if (addList.Count != 0 || removeList.Count != 0)
            {
                foreach (T ml in addList)
                {
                    listenerList.AddFirst(ml);
                }
                foreach (T ml in removeList)
                {
                    listenerList.Remove(ml);
                }

                addList.Clear();
                removeList.Clear();
            }
        }

        public void SetDelay(long delay)
        {
            locked.SetDelay(delay);
        }

        public long GetDelay()
        {
            return locked.GetDelay();
        }

        public void Update(long elapsedTime)
        {
            if (handler == null)
            {
                return;
            }
            if (locked.Action(elapsedTime))
            {

                #if WINDOWS_PHONE
                //触发触屏
                                ClickTouch();
                #else
                                //触发鼠标
                                ClickMouse();
                #endif

                                if (!isDraging)
                                {
                                    //触发其它游戏设备操作
                                    ClickGamePad();
                                }

            }
        }

        /// <summary>
        /// 鼠标事件处理
        /// </summary>
        private void ClickMouse()
        {

            if (lastMouseboardState == null)
            {
                lastMouseboardState = Mouse.GetState();
            }
            else
            {
                lastMouseboardState = currentMouseboardState;
            }

            currentMouseboardState = Mouse.GetState();

            if (currentMouseboardState == null)
            {
                return;
            }

            int modifiers = 0;

            KeyboardState keyState = Keyboard.GetState();

            if (keyState.IsKeyDown(Keys.LeftShift) || keyState.IsKeyDown(Keys.RightShift))
            {
                modifiers |= LTouch.SHIFT_DOWN;
            }
            if (keyState.IsKeyDown(Keys.LeftControl) || keyState.IsKeyDown(Keys.RightControl))
            {
                modifiers |= LTouch.CTRL_DOWN;
            }
            if (keyState.IsKeyDown(Keys.LeftAlt) || keyState.IsKeyDown(Keys.RightAlt))
            {
                modifiers |= LTouch.ALT_DOWN;
            }

            float lastMouseX = (lastMouseboardState.X - handler.GetX()) / LSystem.scaleWidth;
            float lastMouseY = (lastMouseboardState.Y - handler.GetY()) / LSystem.scaleHeight;

            float currentMouseX = (currentMouseboardState.X - handler.GetX()) / LSystem.scaleWidth;
            float currentMouseY = (currentMouseboardState.Y - handler.GetY()) / LSystem.scaleHeight;

            Point previousLocation = new Point((int)lastMouseX, (int)lastMouseY);
            Point currentLocation = new Point((int)currentMouseX, (int)currentMouseY);

            if ((previousLocation.X != currentLocation.X) || (previousLocation.Y != currentLocation.Y))
            {
                finalTouch.button = Touch.TOUCH_MOVE;
                finalTouch.Set(previousLocation, currentLocation, modifiers);
                handler.MouseMoved(finalTouch);
                if (mousePressed && currentMouseboardState.LeftButton == ButtonState.Pressed)
                {
                    if (isClick && MathUtils.Distance(startLocation, currentLocation) > click_count)
                    {
                        isClick = false;
                    }
                    handler.MouseDragged(finalTouch);
                }
                isDraging = true;
            }
            if (lastMouseboardState.LeftButton == ButtonState.Pressed && currentMouseboardState.LeftButton == ButtonState.Released)
            {
                finalTouch.button = Touch.TOUCH_UP;
                finalTouch.Set(currentLocation, modifiers);
                handler.MouseReleased(finalTouch);
                if (isClick)
                {
                    handler.MouseClicked(finalTouch);
                }
                isDraging = false;
            }
            else if (lastMouseboardState.LeftButton == ButtonState.Released && currentMouseboardState.LeftButton == ButtonState.Pressed)
            {
                mousePressed = true;
                isClick = true;
                startLocation = new Point(currentMouseboardState.X, currentMouseboardState.Y);
                finalTouch.button = Touch.TOUCH_DOWN;
                finalTouch.Set(currentLocation, modifiers);
                handler.MousePressed(finalTouch);
                isDraging = false;
            }
            else if (lastMouseboardState.RightButton == ButtonState.Pressed && currentMouseboardState.RightButton == ButtonState.Released)
            {
                finalTouch.button = Touch.TOUCH_UP;
                finalTouch.Set(currentLocation, Touch.RIGHT, modifiers);
                handler.MouseReleased(finalTouch);
                isDraging = false;
            }
            else if (lastMouseboardState.RightButton == ButtonState.Released && currentMouseboardState.RightButton == ButtonState.Pressed)
            {
                finalTouch.button = Touch.TOUCH_DOWN;
                finalTouch.Set(currentLocation, Touch.LEFT, modifiers);
                handler.MousePressed(finalTouch);
                isDraging = false;
            }
            else if (lastMouseboardState.MiddleButton == ButtonState.Pressed && currentMouseboardState.MiddleButton == ButtonState.Released)
            {
                finalTouch.button = Touch.TOUCH_UP;
                finalTouch.Set(currentLocation, Touch.MIDDLE, modifiers);
                handler.MousePressed(finalTouch);
                isDraging = false;
            }
        }

        private float touchX,touchY;

        private float TouchX(TouchLocation touch)
        {
            return (touch.Position.X - handler.GetX())
                             / LSystem.scaleWidth;
        }

        private float TouchY(TouchLocation touch)
        {
            return (touch.Position.Y - handler.GetY())
                             / LSystem.scaleHeight;
        }

        private void ResetMove()
        {
            TurnLeft = false;
            TurnRight = false;
            MoveForward = false;
            MoveBackward = false;
        }

        private void GestureClick()
        {
            if (flickListener != null)
            {
                TouchPanel.EnabledGestures = GestureType.Flick | GestureType.DoubleTap | GestureType.Tap | GestureType.FreeDrag | GestureType.HorizontalDrag | GestureType.VerticalDrag;

                if (TouchPanel.EnabledGestures != GestureType.None)
                {
                    GestureSample sample = TouchPanel.ReadGesture();

                    Vector2 touch1 = sample.Position;
                    Vector2 touch2 = sample.Position2;
                    float x = touch1.X;
                    float y = touch1.Y;
                    float rawX = sample.Delta.X;
                    float rawY = sample.Delta.Y;

                    switch (sample.GestureType)
                    {
                        case GestureType.Tap:
                            ResetMove();
                            flickListener.TouchSingleTap(x, y, rawX, rawY);
                            break;
                        case GestureType.DoubleTap:
                            ResetMove();
                            flickListener.TouchDoubleTap(x, y, rawX, rawY);
                            break;
                        case GestureType.FreeDrag:
                            ResetMove();
                            x = touch2.X;
                            y = touch2.Y;
                            rawX = sample.Delta2.X;
                            rawY = sample.Delta2.Y;
                            flickListener.TouchScroll(x, y, rawX, rawY);
                            break;
                        case GestureType.Flick:
                            ResetMove();
                            x = touch1.X;
                            y = touch1.Y;
                            rawX = sample.Delta.X;
                            rawY = sample.Delta.Y;
                            float distanceX = touch2.X - x;
                            float distanceY = touch2.Y - y;
                            bool a = (distanceY > distanceX);
                            bool b = (distanceY > -distanceX);
                            int direction = (a ? FLICK_LEFT : FLICK_UP)
                                    | (b ? FLICK_RIGHT : FLICK_UP);
                            flickListener.TouchFlick(x, y, rawX, rawY, direction);
                            break;
                        case GestureType.HorizontalDrag:
                            TurnLeft = sample.Delta.X < 0;
                            TurnRight = sample.Delta.X > 0;
                            break;
                        case GestureType.VerticalDrag:
                            MoveForward = sample.Delta.Y < 0;
                            MoveBackward = sample.Delta.Y > 0;
                            break;
                        default:
                            ResetMove();
                            break;

                    }
                }
            }
        }

        private bool isEmulator;

        /// <summary>
        /// 触屏事件处理
        /// </summary>
        public void ClickTouch()
        {
            //手势监听
            GestureClick();

            isEmulator = false;

            EmulatorButtons ebs = handler.emulatorButtons;
            if (ebs != null && ebs.IsVisible())
            {
                isEmulator = true;
            }

            TouchState = TouchPanel.GetState();

            lock (TouchState.GetType()){

                foreach (TouchLocation touch in TouchState)
                {

                    touchX = TouchX(touch);

                    touchY = TouchY(touch);

                    if (isEmulator)
                    {
                        ebs.OnEmulatorButtonEvent(touch, touchX, touchY);
                    }

                    finalTouch.id = touch.Id;

                    switch (touch.State)
                    {
                        case TouchLocationState.Pressed:
                            offsetTouchX = touchX;
                            offsetTouchY = touchY;
                            if ((touchX < halfWidth) && (touchY < halfHeight))
                            {
                                finalTouch.type = Touch.UPPER_LEFT;
                            }
                            else if ((touchX >= halfWidth)
                                  && (touchY < halfHeight))
                            {
                                finalTouch.type = Touch.UPPER_RIGHT;
                            }
                            else if ((touchX < halfWidth)
                                  && (touchY >= halfHeight))
                            {
                                finalTouch.type = Touch.LOWER_LEFT;
                            }
                            else
                            {
                                finalTouch.type = Touch.LOWER_RIGHT;
                            }
                            finalTouch.button = Touch.TOUCH_DOWN;
                            finalTouch.x0 = touchX;
                            finalTouch.y0 = touchY;
                            finalTouch.pointer = touch.Id;
                            handler.MousePressed(finalTouch);
                            isDraging = false;
                            break;
                        case TouchLocationState.Released:
                            finalTouch.button = Touch.TOUCH_UP;
                            finalTouch.x0 = touchX;
                            finalTouch.y0 = touchY;
                            finalTouch.pointer = touch.Id;
                            handler.MouseReleased(finalTouch);
                            isDraging = false;
                            break;
                        case TouchLocationState.Moved:
                            offsetMoveX = touchX;
                            offsetMoveY = touchY;
                            if (MathUtils.Abs(offsetTouchX - offsetMoveX) > 5
                                    || MathUtils.Abs(offsetTouchY - offsetMoveY) > 5)
                            {
                                finalTouch.x0 = touchX;
                                finalTouch.y0 = touchY;
                                finalTouch.pointer = touch.Id;
                                finalTouch.button = Touch.TOUCH_MOVE;
                                handler.MouseMoved(finalTouch);
                            }
                            isDraging = true;
                            break;
                        case TouchLocationState.Invalid:
                            finalTouch.button = Touch.TOUCH_UP;
                            break;
                        default:
                            finalTouch.button = Touch.TOUCH_UP;
                            break;
                    }
                }

            }
        }


        private readonly static GamePadButtons EmptyPad = new GamePadButtons();

        private static int CALL_GAMEPAD_INDEX = 0;

        /// <summary>
        /// 获得当前执行的游戏设备索引
        /// </summary>
        public static int GamePadIndex{
            get
            {
                return CALL_GAMEPAD_INDEX;
            }
            set
            {
                CALL_GAMEPAD_INDEX = value;
            }
        }

        public void ClickGamePad()
        {
            //遍历允许的最大游戏设备值，尽可能满足不同外设需要
            for (int idx = 0; idx < maxInputs; idx++)
            {
                lastKeyboardStates[idx] = currentKeyboardStates[idx];
                lastGamePadStates[idx] = currentGamePadStates[idx];

                currentKeyboardStates[idx] = Keyboard.GetState((PlayerIndex)idx);
                currentGamePadStates[idx] = GamePad.GetState((PlayerIndex)idx);

                if (currentGamePadStates[idx].IsConnected)
                {
                    gamePadWasConnected[idx] = true;
                }

                //默认情况下，LGame仅处理第一设备的操作，其余扩展设备请自行转化(比如XBOX多手柄的操作转化)
                if (idx == CALL_GAMEPAD_INDEX)
                {
                    CallGamePad(idx);
                }
            }
        }

        /// <summary>
        /// 呼叫并处理指定索引的游戏设备操作
        /// </summary>
        /// 
        /// <param name="idx"></param>
        public void CallGamePad(int idx)
        {
            if (idx >= maxInputs)
            {
                return;
            }
            KeyboardState keyEvent = currentKeyboardStates[idx];
          
            if (keyEvent != null)
            {
                if (lastKeyCharPressed != 0)
                {
                    finalKey.keyCode = 0;
                    finalKey.keyChar = lastKeyCharPressed;
                    finalKey.type = Key.KEY_TYPED;
                }

                foreach (Keys e in keyEvent.GetPressedKeys())
                {

                    LInputFactory.cKey = e;

                    int keyCode = ToKeyCode(e);
                    char keyChar = (char)keyCode;
                    if (IsKeyPressed(e, idx))
                    {
                        finalKey.keyCode = keyCode;
                        finalKey.keyChar = keyChar;
					    finalKey.type = Key.KEY_DOWN;
					    lastKeyCharPressed = keyChar;
                    }
                    else if (IsKeyReleased(e, idx))
                    {
                        finalKey.keyCode = keyCode;
                        finalKey.keyChar = keyChar;
                        finalKey.type = Key.KEY_UP;
                        lastKeyCharPressed = keyChar;
                    }
                    switch (finalKey.type)
                    {
                        case Key.KEY_DOWN:
                            handler.KeyDown(finalKey);
                            keys.Add(finalKey.keyCode);
                            break;
                        case Key.KEY_UP:
                            handler.KeyUp(finalKey);
                            keys.RemoveValue(finalKey.keyCode);
                            break;
                        case Key.KEY_TYPED:
                            handler.KeyTyped(finalKey);
                            break;
                        default:
                            keys.Clear();
                            break;
                    }
                }

            }

            //如果是XBOX360，手柄不连接绝对废了，也不用下面的操作……
            #if XBOX
            if (!gamePadWasConnected[idx])
            {
                return;
            }
            #endif

            GamePadState padState = currentGamePadStates[idx];

            if (padState != null)
            {
                GamePadButtons button = padState.Buttons;

                if (!EmptyPad.Equals(button))
                {
                    LInputFactory.cButton = button;

                    //处理具体的按键监听
                    RunGamePad(idx);

                    //如果限制了Back事件
                    if (limit_back)
                    {
                        if (IsPadPressed(Buttons.Back, idx))
                        {
                            if (LSystem.screenActivity != null)
                            {
                                LSystem.screenActivity.Destory();
                            }
                        }
                    }
                }
            }

        }

        /// <summary>
        /// 自动匹配具体手柄按钮到对应的监听
        /// </summary>
        /// 
        /// <param name="player"></param>
        private void RunGamePad(int player)
        {
            UpdateGamePadListeners();
            if (gamePadListeners.Count > 0)
            {
                foreach (GamePadListener e in gamePadListeners)
                {
                    if (IsPadPressed(Buttons.DPadDown, player))
                    {
                        e.DPadDown_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.DPadLeft, player))
                    {
                        e.DPadLeft_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.DPadUp, player))
                    {
                        e.DPadUp_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.DPadRight, player))
                    {
                        e.DPadRight_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftThumbstickDown, player))
                    {
                        e.LeftThumbstickDown_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftThumbstickUp, player))
                    {
                        e.LeftThumbstickUp_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftThumbstickLeft, player))
                    {
                        e.LeftThumbstickLeft_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftThumbstickRight, player))
                    {
                        e.LeftThumbstickRight_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightThumbstickDown, player))
                    {
                        e.RightThumbstickDown_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightThumbstickUp, player))
                    {
                        e.RightThumbstickUp_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightThumbstickLeft, player))
                    {
                        e.RightThumbstickLeft_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightThumbstickRight, player))
                    {
                        e.RightThumbstickRight_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftTrigger, player))
                    {
                        e.LeftTrigger_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightTrigger, player))
                    {
                        e.RightTrigger_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.A, player))
                    {
                        e.A_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.B, player))
                    {
                        e.B_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.Back, player))
                    {
                        e.Back_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.BigButton, player))
                    {
                        e.BigButton_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftShoulder, player))
                    {
                        e.LeftShoulder_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.LeftStick, player))
                    {
                        e.LeftStick_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightShoulder, player))
                    {
                        e.RightShoulder_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.RightStick, player))
                    {
                        e.RightStick_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.Start, player))
                    {
                        e.Start_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.X, player))
                    {
                        e.X_Pressed(player);
                    }
                    if (IsPadPressed(Buttons.Y, player))
                    {
                        e.Y_Pressed(player);
                    }
                    //释放
                    if (IsPadReleased(Buttons.DPadDown, player))
                    {
                        e.DPadDown_Released(player);
                    }
                    if (IsPadReleased(Buttons.DPadLeft, player))
                    {
                        e.DPadLeft_Released(player);
                    }
                    if (IsPadReleased(Buttons.DPadUp, player))
                    {
                        e.DPadUp_Released(player);
                    }
                    if (IsPadReleased(Buttons.DPadRight, player))
                    {
                        e.DPadRight_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftThumbstickDown, player))
                    {
                        e.LeftThumbstickDown_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftThumbstickUp, player))
                    {
                        e.LeftThumbstickUp_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftThumbstickLeft, player))
                    {
                        e.LeftThumbstickLeft_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftThumbstickRight, player))
                    {
                        e.LeftThumbstickRight_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightThumbstickDown, player))
                    {
                        e.RightThumbstickDown_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightThumbstickUp, player))
                    {
                        e.RightThumbstickUp_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightThumbstickLeft, player))
                    {
                        e.RightThumbstickLeft_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightThumbstickRight, player))
                    {
                        e.RightThumbstickRight_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftTrigger, player))
                    {
                        e.LeftTrigger_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightTrigger, player))
                    {
                        e.RightTrigger_Released(player);
                    }
                    if (IsPadReleased(Buttons.A, player))
                    {
                        e.A_Released(player);
                    }
                    if (IsPadReleased(Buttons.B, player))
                    {
                        e.B_Released(player);
                    }
                    if (IsPadReleased(Buttons.Back, player))
                    {
                        e.Back_Released(player);
                    }
                    if (IsPadReleased(Buttons.BigButton, player))
                    {
                        e.BigButton_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftShoulder, player))
                    {
                        e.LeftShoulder_Released(player);
                    }
                    if (IsPadReleased(Buttons.LeftStick, player))
                    {
                        e.LeftStick_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightShoulder, player))
                    {
                        e.RightShoulder_Released(player);
                    }
                    if (IsPadReleased(Buttons.RightStick, player))
                    {
                        e.RightStick_Released(player);
                    }
                    if (IsPadReleased(Buttons.Start, player))
                    {
                        e.Start_Released(player);
                    }
                    if (IsPadReleased(Buttons.X, player))
                    {
                        e.X_Released(player);
                    }
                    if (IsPadReleased(Buttons.Y, player))
                    {
                        e.Y_Released(player);
                    }
                }
            }
        }

        /// <summary>
        /// 将XNA按键转化为统一的LGame按键
        /// </summary>
        /// 
        /// <param name="key"></param>
        /// <returns></returns>
        public static int ToKeyCode(Keys key)
        {
            switch (key)
            {
                case Keys.D0:
                    return Key.NUM_0;
                case Keys.D1:
                    return Key.NUM_1;
                case Keys.D2:
                    return Key.NUM_2;
                case Keys.D3:
                    return Key.NUM_3;
                case Keys.D4:
                    return Key.NUM_4;
                case Keys.D5:
                    return Key.NUM_5;
                case Keys.D6:
                    return Key.NUM_6;
                case Keys.D7:
                    return Key.NUM_7;
                case Keys.D8:
                    return Key.NUM_8;
                case Keys.D9:
                    return Key.NUM_9;
                case Keys.A:
                    return Key.A;
                case Keys.B:
                    return Key.B;
                case Keys.C:
                    return Key.C;
                case Keys.D:
                    return Key.D;
                case Keys.E:
                    return Key.E;
                case Keys.F:
                    return Key.F;
                case Keys.G:
                    return Key.G;
                case Keys.H:
                    return Key.H;
                case Keys.I:
                    return Key.I;
                case Keys.J:
                    return Key.J;
                case Keys.K:
                    return Key.K;
                case Keys.L:
                    return Key.L;
                case Keys.M:
                    return Key.M;
                case Keys.N:
                    return Key.N;
                case Keys.O:
                    return Key.O;
                case Keys.P:
                    return Key.P;
                case Keys.Q:
                    return Key.Q;
                case Keys.R:
                    return Key.R;
                case Keys.S:
                    return Key.S;
                case Keys.T:
                    return Key.T;
                case Keys.U:
                    return Key.U;
                case Keys.V:
                    return Key.V;
                case Keys.W:
                    return Key.W;
                case Keys.X:
                    return Key.X;
                case Keys.Y:
                    return Key.Y;
                case Keys.Z:
                    return Key.Z;
                case Keys.LeftAlt:
                    return Key.ALT_LEFT;
                case Keys.RightAlt:
                    return Key.ALT_RIGHT;
                case Keys.OemBackslash:
                    return Key.BACKSLASH;
                case Keys.OemComma:
                    return Key.COMMA;
                case Keys.Delete:
                    return Key.FORWARD_DEL;
                case Keys.Left:
                    return Key.DPAD_LEFT;
                case Keys.Right:
                    return Key.DPAD_RIGHT;
                case Keys.Up:
                    return Key.DPAD_UP;
                case Keys.Down:
                    return Key.DPAD_DOWN;
                case Keys.Enter:
                    return Key.ENTER;
                case Keys.Home:
                    return Key.HOME;
                case Keys.OemMinus:
                    return Key.MINUS;
                case Keys.OemPeriod:
                    return Key.PERIOD;
                case Keys.Add:
                    return Key.PLUS;
                case Keys.OemSemicolon:
                    return Key.SEMICOLON;
                case Keys.LeftShift:
                    return Key.SHIFT_LEFT;
                case Keys.RightShift:
                    return Key.SHIFT_RIGHT;
                case Keys.Space:
                    return Key.SPACE;
                case Keys.Tab:
                    return Key.TAB;
                case Keys.LeftControl:
                    return Key.CONTROL_LEFT;
                case Keys.RightControl:
                    return Key.CONTROL_RIGHT;
                case Keys.Escape:
                    return Key.ESCAPE;
                case Keys.End:
                    return Key.END;
                case Keys.Insert:
                    return Key.INSERT;
                case Keys.Back:
                    return Key.BACK;
                default:
                    return Key.UNKNOWN;
            }
        }

        /// <summary>
        /// 判定指定的手柄按钮是否按下
        /// </summary>
        /// 
        /// <param name="button"></param>
        /// <param name="playIndex"></param>
        /// <returns></returns>
        public bool IsPadPressed(Buttons button,int playIndex)
        {
            return currentGamePadStates[playIndex].IsButtonDown(button);
        }

        /// <summary>
        /// 判定指定的手柄按钮是否松开
        /// </summary>
        /// 
        /// <param name="button"></param>
        /// <param name="playIndex"></param>
        /// <returns></returns>
        public bool IsPadReleased(Buttons button, int playIndex)
        {
            return lastGamePadStates[playIndex].IsButtonUp(button);
        }

        /// <summary>
        /// 判定指定设备上的按键是否按下
        /// </summary>
        /// 
        /// <param name="key"></param>
        /// <param name="player"></param>
        /// <returns></returns>
        public bool IsKeyDown(Keys key, int player)
        {
            return currentKeyboardStates[player].IsKeyDown(key);
        }

        /// <summary>
        /// 判定指定设备上的按键是否放开
        /// </summary>
        /// 
        /// <param name="key"></param>
        /// <param name="player"></param>
        /// <returns></returns>
        public bool IsKeyUp(Keys key, int player)
        {
            return currentKeyboardStates[player].IsKeyUp(key);
        }

        ///PS:默认不要求键盘支持连键，所以Pressed与Released检查上和GamePad有所区别
        /// <summary>
        /// 判定指定设备上的按键是否完整执行了一次按下
        /// </summary>
        /// 
        /// <param name="key"></param>
        /// <param name="player"></param>
        /// <returns></returns>
        public bool IsKeyPressed(Keys key,int player)
        {
            return (lastKeyboardStates[player].IsKeyUp(key) && currentKeyboardStates[player].IsKeyDown(key));
        }

        /// <summary>
        /// 判定指定设备上的按键是否完整执行了一次放开
        /// </summary>
        /// 
        /// <param name="key"></param>
        /// <param name="player"></param>
        /// <returns></returns>
        public bool IsKeyReleased(Keys key, int player)
        {
            return (lastKeyboardStates[player].IsKeyDown(key) && currentKeyboardStates[player].IsKeyUp(key));
        }

        /// <summary>
        /// 判断指定按键是否获得执行
        /// </summary>
        /// 
        /// <param name="key"></param>
        /// <param name="controllingPlayer"></param>
        /// <param name="playerIndex"></param>
        /// <returns></returns>
        public bool IsNewKeyPress(Keys key, PlayerIndex? controllingPlayer,
                                           out PlayerIndex playerIndex)
        {
            if (controllingPlayer.HasValue)
            {
                playerIndex = controllingPlayer.Value;

                int i = (int)playerIndex;

                return (currentKeyboardStates[i].IsKeyDown(key) &&
                        lastKeyboardStates[i].IsKeyUp(key));
            }
            else
            {
                return (IsNewKeyPress(key, PlayerIndex.One, out playerIndex) ||
                        IsNewKeyPress(key, PlayerIndex.Two, out playerIndex) ||
                        IsNewKeyPress(key, PlayerIndex.Three, out playerIndex) ||
                        IsNewKeyPress(key, PlayerIndex.Four, out playerIndex));
            }
        }

        /// <summary>
        /// 判断指定按钮是否获得执行
        /// </summary>
        /// 
        /// <param name="button"></param>
        /// <param name="controllingPlayer"></param>
        /// <param name="playerIndex"></param>
        /// <returns></returns>
        public bool IsNewButtonPress(Buttons button, PlayerIndex? controllingPlayer,
                                                     out PlayerIndex playerIndex)
        {
            if (controllingPlayer.HasValue)
            {
                playerIndex = controllingPlayer.Value;

                int i = (int)playerIndex;

                return (currentGamePadStates[i].IsButtonDown(button) &&
                        lastGamePadStates[i].IsButtonUp(button));
            }
            else
            {
                return (IsNewButtonPress(button, PlayerIndex.One, out playerIndex) ||
                        IsNewButtonPress(button, PlayerIndex.Two, out playerIndex) ||
                        IsNewButtonPress(button, PlayerIndex.Three, out playerIndex) ||
                        IsNewButtonPress(button, PlayerIndex.Four, out playerIndex));
            }
        }

        /// <summary>
        /// 判断是否执行了选择菜单
        /// </summary>
        /// 
        /// <param name="controllingPlayer"></param>
        /// <param name="playerIndex"></param>
        /// <returns></returns>
        public bool IsMenuSelect(PlayerIndex? controllingPlayer,
                                 out PlayerIndex playerIndex)
        {
            return IsNewKeyPress(Keys.Space, controllingPlayer, out playerIndex) ||
                   IsNewKeyPress(Keys.Enter, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.A, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.Start, controllingPlayer, out playerIndex);
        }

        public bool IsMenuSelect(out PlayerIndex playerIndex)
        {
            return IsMenuSelect(PlayerIndex.One, out playerIndex);
        }

        /// <summary>
        /// 判断是否取消了选择菜单
        /// </summary>
        /// 
        /// <param name="controllingPlayer"></param>
        /// <param name="playerIndex"></param>
        /// <returns></returns>
        public bool IsMenuCancel(PlayerIndex? controllingPlayer,
                                 out PlayerIndex playerIndex)
        {
            return IsNewKeyPress(Keys.Escape, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.B, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.Back, controllingPlayer, out playerIndex);
        }

        public bool IsMenuCancel(out PlayerIndex playerIndex)
        {
            return IsMenuCancel(PlayerIndex.One, out playerIndex);
        }

        /// <summary>
        /// 判断菜单上移
        /// </summary>
        /// 
        /// <param name="controllingPlayer"></param>
        /// <returns></returns>
        public bool IsMenuUp(PlayerIndex? controllingPlayer)
        {
            PlayerIndex playerIndex;

            return IsNewKeyPress(Keys.Up, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.DPadUp, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.LeftThumbstickUp, controllingPlayer, out playerIndex);
        }

        public bool IsMenuUp()
        {
            return IsMenuUp(PlayerIndex.One);
        }

        /// <summary>
        /// 判断菜单下移
        /// </summary>
        /// 
        /// <param name="controllingPlayer"></param>
        /// <returns></returns>
        public bool IsMenuDown(PlayerIndex? controllingPlayer)
        {
            PlayerIndex playerIndex;

            return IsNewKeyPress(Keys.Down, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.DPadDown, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.LeftThumbstickDown, controllingPlayer, out playerIndex);
        }

        public bool IsMenuDown()
        {
            return IsMenuDown(PlayerIndex.One);
        }

        /// <summary>
        /// 判断游戏是否暂停
        /// </summary>
        /// 
        /// <param name="controllingPlayer"></param>
        /// <returns></returns>
        public bool IsPauseGame(PlayerIndex? controllingPlayer)
        {
            PlayerIndex playerIndex;

            return IsNewKeyPress(Keys.Escape, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.Back, controllingPlayer, out playerIndex) ||
                   IsNewButtonPress(Buttons.Start, controllingPlayer, out playerIndex);
        }

        public bool IsPauseGame()
        {
            return IsPauseGame(PlayerIndex.One);
        }
    }

}
