using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.OpenGL;
using Loon.Utils;
using System.Runtime.CompilerServices;

namespace Loon.Core
{
    public class EmulatorButtons
    {

        private LTexture dpad, buttons;

        private EmulatorButton up, left, right, down;

        private EmulatorButton triangle, square, circle, cancel;

        private EmulatorListener emulatorListener;

        private int offsetX, offsetY, width, height;

        private const int offset = 10;

        private bool visible;

        public EmulatorButtons(EmulatorListener el)
            : this(el, LSystem.screenRect.width, LSystem.screenRect.height)
        {

        }

        public EmulatorButtons(EmulatorListener el, int w, int h)
            : this(el, w, h,
                LSystem.EMULATOR_BUTTIN_SCALE)
        {

        }

        public EmulatorButtons(EmulatorListener el, int w, int h, float scale)
        {

            this.emulatorListener = el;
            this.dpad = XNAConfig.LoadTexture("e1.png");
            this.buttons = XNAConfig.LoadTexture("e2.png");

            this.width = w;
            this.height = h;

            if (scale <= 1f)
            {
                this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true, 60, 60);
                this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true, 60, 60);
                this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true, 60, 60);
                this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true, 60, 60);

                this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
                        68, 68);
                this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true, 68,
                        68);
                this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true, 68,
                        68);
                this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true, 68,
                        68);
            }
            else
            {

                this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true,
                        (int)(60 * scale), (int)(60 * scale));
                this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true,
                        (int)(60 * scale), (int)(60 * scale));
                this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true,
                        (int)(60 * scale), (int)(60 * scale));
                this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true,
                        (int)(60 * scale), (int)(60 * scale));

                this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
                        (int)(68 * scale), (int)(68 * scale));
                this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true,
                        (int)(68 * scale), (int)(68 * scale));
                this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true,
                        (int)(68 * scale), (int)(68 * scale));
                this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true,
                        (int)(68 * scale), (int)(68 * scale));
            }

            if (dpad != null)
            {
                dpad.Dispose();
                dpad = null;
            }
            if (buttons != null)
            {
                buttons.Dispose();
                buttons = null;
            }
            this.visible = true;

            this.SetLocation(0, 0);
        }

        /// <summary>
        /// 移动模拟按钮集合位置(此为相对坐标，默认居于屏幕下方)
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        public void SetLocation(int x, int y)
        {
            if (!visible)
            {
                return;
            }
            this.offsetX = x;
            this.offsetY = y;
            up.SetLocation((offsetX + up.GetWidth()) + offset, offsetY
                    + (height - up.GetHeight() * 3) - offset);
            left.SetLocation((offsetX + 0) + offset,
                    offsetY + (height - left.GetHeight() * 2) - offset);
            right.SetLocation((offsetX + right.GetWidth() * 2) + offset, offsetY
                    + (height - right.GetHeight() * 2) - offset);
            down.SetLocation((offsetX + down.GetWidth()) + offset, offsetY
                    + (height - down.GetHeight()) - offset);

            if (LSystem.screenRect.height <= 320)
            {
                triangle.SetLocation(offsetX + (width - triangle.GetWidth() * 2)
                        - offset, offsetY + offset);
                square.SetLocation(offsetX + (width - square.GetWidth()) - offset,
                        offsetY + square.GetHeight() + offset);
                circle.SetLocation(offsetX + (width - circle.GetWidth() * 3)
                        - offset, offsetY + circle.GetHeight() + offset);
                cancel.SetLocation(offsetX + (width - cancel.GetWidth() * 2)
                        - offset, offsetY + offset + cancel.GetHeight() * 2);
            }
            else
            {
                triangle.SetLocation(offsetX + (width - triangle.GetWidth() * 2),
                        offsetY + offset + 80);
                square.SetLocation(offsetX + (width - square.GetWidth()), offsetY
                        + square.GetHeight() + offset + 80);
                circle.SetLocation(offsetX + (width - circle.GetWidth() * 3),
                        offsetY + circle.GetHeight() + offset + 80);
                cancel.SetLocation(offsetX + (width - cancel.GetWidth() * 2),
                        offsetY + offset + cancel.GetHeight() * 2 + 80);
            }
        }

        public bool IsClick()
        {
            if (up.IsClick())
            {
                return true;
            }
            if (left.IsClick())
            {
                return true;
            }
            if (down.IsClick())
            {
                return true;
            }
            if (right.IsClick())
            {
                return true;
            }
            if (triangle.IsClick())
            {
                return true;
            }
            if (square.IsClick())
            {
                return true;
            }
            if (circle.IsClick())
            {
                return true;
            }
            if (cancel.IsClick())
            {
                return true;
            }
            return false;
        }

        public void Hide()
        {
            HideLeft();
            HideRight();
        }

        public void Show()
        {
            ShowLeft();
            ShowRight();
        }

        public void HideLeft()
        {
            up.Disable(true);
            left.Disable(true);
            right.Disable(true);
            down.Disable(true);
        }

        public void ShowLeft()
        {
            up.Disable(false);
            left.Disable(false);
            right.Disable(false);
            down.Disable(false);
        }

        public void HideRight()
        {
            triangle.Disable(true);
            square.Disable(true);
            circle.Disable(true);
            cancel.Disable(true);
        }

        public void ShowRight()
        {
            triangle.Disable(false);
            square.Disable(false);
            circle.Disable(false);
            cancel.Disable(false);
        }

        /// <summary>
        /// 当触发模拟按钮时，自动分配事件
        /// </summary>
        /// <param name="touch"></param>
        /// <param name="touchX"></param>
        /// <param name="touchY"></param>
        public void OnEmulatorButtonEvent(Microsoft.Xna.Framework.Input.Touch.TouchLocation touch, float touchX, float touchY)
        {
            if (!visible)
            {
                return;
            }
            switch (touch.State)
            {
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Pressed:
                    Hit(touch.Id, touchX, touchY);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Released:
                    Unhit(touch.Id);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Moved:
                    Hit(touch.Id, touchX, touchY);
                    break;
                case Microsoft.Xna.Framework.Input.Touch.TouchLocationState.Invalid:
                    Unhit(touch.Id);
                    break;
            }
        }


        public int GetX()
        {
            return offsetX;
        }

        public int GetY()
        {
            return offsetY;
        }

        /// <summary>
        /// 获得模拟按钮的集合
        /// </summary>
        ///
        /// <returns></returns>
        public EmulatorButton[] GetEmulatorButtons()
        {
            return new EmulatorButton[] { up, left, right, down, triangle, square,
					circle, cancel };
        }

        /// <summary>
        /// 绘制模拟按钮(LGraphics模式)
        /// </summary>
        ///
        /// <param name="g"></param>
        public void Draw(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            g.BeginBatch();
            g.SetBatchAlpha(0.5f);
            up.Draw(g);
            left.Draw(g);
            right.Draw(g);
            down.Draw(g);

            triangle.Draw(g);
            square.Draw(g);
            circle.Draw(g);
            cancel.Draw(g);
            g.EndBatch();


        }

        /// <summary>
        /// 点击事件触发器
        /// </summary>
        ///
        private void CheckOn()
        {

            if (emulatorListener == null)
            {
                return;
            }
            if (up.IsClick())
            {
                emulatorListener.OnUpClick();
            }
            if (left.IsClick())
            {
                emulatorListener.OnLeftClick();
            }
            if (right.IsClick())
            {
                emulatorListener.OnRightClick();
            }
            if (down.IsClick())
            {
                emulatorListener.OnDownClick();
            }

            if (triangle.IsClick())
            {
                emulatorListener.OnTriangleClick();
            }
            if (square.IsClick())
            {
                emulatorListener.OnSquareClick();
            }
            if (circle.IsClick())
            {
                emulatorListener.OnCircleClick();
            }
            if (cancel.IsClick())
            {
                emulatorListener.OnCancelClick();
            }
        }

        public void Hit(int id, float x, float y)
        {
            Hit(id, MathUtils.Round(x), MathUtils.Round(y));
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Hit(int id, int x, int y)
        {

            if (!visible)
            {
                return;
            }

            up.Hit(id, x, y);
            left.Hit(id, x, y);
            right.Hit(id, x, y);
            down.Hit(id, x, y);

            triangle.Hit(id, x, y);
            square.Hit(id, x, y);
            circle.Hit(id, x, y);
            cancel.Hit(id, x, y);

            CheckOn();
        }

        /// <summary>
        /// 放开事件触发器
        /// </summary>
        ///
        /// <param name="id"></param>
        private void CheckUn(int id)
        {
            if (emulatorListener == null)
            {
                return;
            }
            if (up.IsClick() && up.GetPointerId() == id)
            {
                emulatorListener.UnUpClick();
            }
            if (left.IsClick() && left.GetPointerId() == id)
            {
                emulatorListener.UnLeftClick();
            }
            if (right.IsClick() && right.GetPointerId() == id)
            {
                emulatorListener.UnRightClick();
            }
            if (down.IsClick() && down.GetPointerId() == id)
            {
                emulatorListener.UnDownClick();
            }
            if (triangle.IsClick() && triangle.GetPointerId() == id)
            {
                emulatorListener.UnTriangleClick();
            }
            if (square.IsClick() && square.GetPointerId() == id)
            {
                emulatorListener.UnSquareClick();
            }
            if (circle.IsClick() && circle.GetPointerId() == id)
            {
                emulatorListener.UnCircleClick();
            }
            if (cancel.IsClick() && cancel.GetPointerId() == id)
            {
                emulatorListener.UnCancelClick();
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Unhit(int id)
        {

            if (!visible)
            {
                return;
            }

            CheckUn(id);

            up.Unhit(id);
            left.Unhit(id);
            right.Unhit(id);
            down.Unhit(id);

            triangle.Unhit(id);
            square.Unhit(id);
            circle.Unhit(id);
            cancel.Unhit(id);
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool visible_0)
        {
            if (!visible_0)
            {
                Release();
            }
            this.visible = visible_0;
        }

        public EmulatorListener GetEmulatorListener()
        {
            return emulatorListener;
        }

        public void SetEmulatorListener(EmulatorListener emulator)
        {
            this.emulatorListener = emulator;
        }

        public EmulatorButton GetUp()
        {
            return up;
        }

        public EmulatorButton GetLeft()
        {
            return left;
        }

        public EmulatorButton GetRight()
        {
            return right;
        }

        public EmulatorButton GetDown()
        {
            return down;
        }

        public EmulatorButton GetTriangle()
        {
            return triangle;
        }

        public EmulatorButton GetSquare()
        {
            return square;
        }

        public EmulatorButton GetCircle()
        {
            return circle;
        }

        public EmulatorButton GetCancel()
        {
            return cancel;
        }

        public void Release()
        {

            up.Unhit();
            left.Unhit();
            right.Unhit();
            down.Unhit();

            triangle.Unhit();
            square.Unhit();
            circle.Unhit();
            cancel.Unhit();

            if (emulatorListener != null)
            {
                emulatorListener.UnUpClick();
                emulatorListener.UnLeftClick();
                emulatorListener.UnRightClick();
                emulatorListener.UnDownClick();
                emulatorListener.UnTriangleClick();
                emulatorListener.UnSquareClick();
                emulatorListener.UnCircleClick();
                emulatorListener.UnCancelClick();
            }
        }
    }
}
