using System;
using System.Collections.Generic;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
using Loon.Core.Input;
using Loon.Utils;
using Loon.Core.Graphics.Component;
using Loon.Action.Map;
using Loon.Action;

namespace Loon.Core.Graphics
{

    public abstract class LComponent : LObject, ActionBind,
              LRelease
    {

        public interface CallListener
        {

            void Act(long elapsedTime);

        }

        //统一监听(其实用event更好，但对应的Java版写法无解……)
        public ClickListener Click
        {
            set;
            get;
        }

        public void SetClick(ClickListener c)
        {
            Click = c;
        }

        public ClickListener GetClick()
        {
            return Click;
        }

        public CallListener Call;

        public void SetCall(CallListener u)
        {
            Call = u;
        }

        public CallListener GetCall()
        {
            return Call;
        }

        private LContainer parent;

        private LTexture[] imageUI;

        protected internal bool elastic;

        protected internal bool autoDestroy;

        protected internal bool isClose;

        protected internal bool isFull;

        public bool customRendering;

        private int cam_x, cam_y, width, height;

        protected internal int screenX, screenY;

        protected internal string tooltip;

        protected internal bool visible = true;

        protected internal bool enabled = true;

        protected internal bool focusable = true;

        protected internal bool selected = false;

        protected internal Desktop desktop = Desktop.EMPTY_DESKTOP;

        private RectBox screenRect;

        protected internal LInput input;

        protected internal bool isLimitMove;

        protected internal LTexture background;

        public LComponent(int x, int y, int width, int height)
        {
            this.SetLocation(x, y);
            this.width = width;
            this.height = height;
            this.screenRect = LSystem.screenRect;
            if (this.width == 0)
            {
                this.width = 10;
            }
            if (this.height == 0)
            {
                this.height = 10;
            }
        }

        public int GetScreenWidth()
        {
            return screenRect.width;
        }

        public int GetScreenHeight()
        {
            return screenRect.height;
        }

        public void MoveCamera(int x, int y)
        {
            if (!this.isLimitMove)
            {
                SetLocation(x, y);
                return;
            }
            int tempX = x;
            int tempY = y;
            int tempWidth = GetWidth() - screenRect.width;
            int tempHeight = GetHeight() - screenRect.height;

            int limitX = tempX + tempWidth;
            int limitY = tempY + tempHeight;

            if (width >= screenRect.width)
            {
                if (limitX > tempWidth)
                {
                    tempX = screenRect.width - width;
                }
                else if (limitX < 1)
                {
                    tempX = X();
                }
            }
            else
            {
                return;
            }
            if (height >= screenRect.height)
            {
                if (limitY > tempHeight)
                {
                    tempY = screenRect.height - height;
                }
                else if (limitY < 1)
                {
                    tempY = Y();
                }
            }
            else
            {
                return;
            }
            this.cam_x = tempX;
            this.cam_y = tempY;
            this.SetLocation(cam_x, cam_y);
        }

        protected internal bool IsNotMoveInScreen(int x, int y)
        {
            if (!this.isLimitMove)
            {
                return false;
            }
            int width = GetWidth() - screenRect.width;
            int height = GetHeight() - screenRect.height;
            int limitX = x + width;
            int limitY = y + height;
            if (GetWidth() >= screenRect.width)
            {
                if (limitX >= width - 1)
                {
                    return true;
                }
                else if (limitX <= 1)
                {
                    return true;
                }
            }
            else
            {
                if (!screenRect.Contains(x, y, GetWidth(), GetHeight()))
                {
                    return true;
                }
            }
            if (GetHeight() >= screenRect.height)
            {
                if (limitY >= height - 1)
                {
                    return true;
                }
                else if (limitY <= 1)
                {
                    return true;
                }
            }
            else
            {
                if (!screenRect.Contains(x, y, GetWidth(), GetHeight()))
                {
                    return true;
                }
            }
            return false;
        }

        public virtual bool IsContainer()
        {
            return false;
        }

        public override void Update(long elapsedTime)
        {
            if (isClose)
            {
                return;
            }
            if (parent != null)
            {
                ValidatePosition();
            }
            if (Call != null)
            {
                Call.Act(elapsedTime);
            }
        }

        public abstract void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage);
        
        public virtual void CreateUI(GLEx g)
        {
            if (isClose)
            {
                return;
            }
            if (!this.visible)
            {
                return;
            }
            int width = this.GetWidth();
            int height = this.GetHeight();

            if (rotation != 0)
            {
                float centerX = this.screenX + width / 2;
                float centerY = this.screenY + height / 2;
                g.Rotate(centerX, centerY, rotation);
            }
            else if (!(scaleX == 1f && scaleY == 1f))
            {
                g.Scale(scaleX, scaleY);
            }
            else if (this.elastic)
            {
                g.SetClip(this.screenX, this.screenY, width, height);
            }
            if (alpha > 0.1d && alpha < 1.0d)
            {
                g.SetAlpha(alpha);
                if (background != null)
                {
                    g.DrawTexture(background, this.screenX, this.screenY, width,
                            height);
                }
                if (this.customRendering)
                {
                    this.CreateCustomUI(g, this.screenX, this.screenY, width,
                            height);
                }
                else
                {
                    this.CreateUI(g, this.screenX, this.screenY, this, this.imageUI);
                }
                g.SetAlpha(1F);
            }
            else
            {
                if (background != null)
                {
                    g.DrawTexture(background, this.screenX, this.screenY, width,
                            height);
                }
                if (this.customRendering)
                {
                    this.CreateCustomUI(g, this.screenX, this.screenY, width,
                            height);
                }
                else
                {
                    this.CreateUI(g, this.screenX, this.screenY, this, this.imageUI);
                }
            }
            if (rotation != 0 || !(scaleX == 1f && scaleY == 1f))
            {
                g.Restore();
            }
            else if (this.elastic)
            {
                g.ClearClip();
            }
        }

        protected internal virtual void CreateCustomUI(GLEx g, int x, int y, int w, int h)
        {
        }

        public bool Contains(int x, int y)
        {
            return Contains(x, y, 0, 0);
        }

        public bool Contains(int x, int y, int width, int height)
        {
            return (this.visible)
                    && (x >= this.screenX
                            && y >= this.screenY
                            && ((x + width) <= (this.screenX + this.width * scaleX)) && ((y + height) <= (this.screenY + this.height
                            * scaleY)));
        }

        public bool Intersects(int x1, int y1)
        {
            return (this.visible)
                    && (x1 >= this.screenX
                            && x1 <= this.screenX + this.width * scaleX
                            && y1 >= this.screenY && y1 <= this.screenY
                            + this.height * scaleY);
        }

        public bool Intersects(LComponent comp)
        {
            return (this.visible)
                    && (comp.IsVisible())
                    && (this.screenX + this.width * scaleX >= comp.screenX
                            && this.screenX <= comp.screenX + comp.width
                            && this.screenY + this.height * scaleY >= comp.screenY && this.screenY <= comp.screenY
                            + comp.height);
        }

        public virtual void Dispose()
        {
            this.isClose = true;
            this.desktop.SetComponentStat(this, false);
            if (this.parent != null)
            {
                this.parent.Remove(this);
            }
            this.desktop = Desktop.EMPTY_DESKTOP;
            this.input = null;
            this.parent = null;
            if (imageUI != null)
            {
                for (int i = 0; i < imageUI.Length; i++)
                {
                    imageUI[i].Destroy();
                    imageUI[i] = null;
                }
                this.imageUI = null;
            }
            if (background != null)
            {
                this.background.Destroy();
                this.background = null;
            }
            this.selected = false;
            this.visible = false;
        }

        public bool IsVisible()
        {
            return this.visible;
        }

        public void SetVisible(bool visible)
        {
            if (this.visible == visible)
            {
                return;
            }
            this.visible = visible;
            this.desktop.SetComponentStat(this, this.visible);
        }

        public bool IsEnabled()
        {
            return (this.parent == null) ? this.enabled
                    : (this.enabled && this.parent.IsEnabled());
        }

        public void SetEnabled(bool b)
        {
            if (this.enabled == b)
            {
                return;
            }
            this.enabled = b;
            this.desktop.SetComponentStat(this, this.enabled);
        }

        public virtual bool IsSelected()
        {
            return this.selected;
        }

        internal void SetSelected(bool b)
        {
            this.selected = b;
        }

        public bool RequestFocus()
        {
            return this.desktop.SelectComponent(this);
        }

        public void TransferFocus()
        {
            if (this.IsSelected() && this.parent != null)
            {
                this.parent.TransferFocus(this);
            }
        }

        public void TransferFocusBackward()
        {
            if (this.IsSelected() && this.parent != null)
            {
                this.parent.TransferFocusBackward(this);
            }
        }

        public bool IsFocusable()
        {
            return this.focusable;
        }

        public void SetFocusable(bool b)
        {
            this.focusable = b;
        }

        public LContainer GetContainer()
        {
            return this.parent;
        }

        internal void SetContainer(LContainer container)
        {
            this.parent = container;

            this.ValidatePosition();
        }

        internal void SetDesktop(Desktop desktop)
        {
            if (this.desktop == desktop)
            {
                return;
            }

            this.desktop = desktop;
            this.input = desktop.input;
        }

        public void SetBounds(float dx, float dy, int width, int height)
        {
            SetLocation(dx, dy);
            if (this.width != width || this.height != height)
            {
                this.width = width;
                this.height = height;
                if (width == 0)
                {
                    width = 1;
                }
                if (height == 0)
                {
                    height = 1;
                }
                this.ValidateSize();
            }
        }

        public override void SetX(Int32 x)
        {
            if (this.GetX() != x || x == 0)
            {
                base.SetX(x);
                this.ValidatePosition();
            }
        }

        public override void SetX(float x)
        {
            if (this.GetX() != x || x == 0)
            {
                base.SetX(x);
                this.ValidatePosition();
            }
        }

        public override void SetY(Int32 y)
        {
            if (this.GetY() != y || y == 0)
            {
                base.SetY(y);
                this.ValidatePosition();
            }
        }

        public override void SetY(float y)
        {
            if (this.GetY() != y || y == 0)
            {
                base.SetY(y);
                this.ValidatePosition();
            }
        }

        public override void SetLocation(Vector2f location)
        {
            SetLocation(location.x, location.y);
        }

        public override void SetLocation(float dx, float dy)
        {
            if (this.GetX() != dx || this.GetY() != dy || dx == 0 || dy == 0)
            {
                base.SetLocation(dx, dy);
                this.ValidatePosition();
            }
        }

        public override void Move(float dx, float dy)
        {
            if (dx != 0 || dy != 0)
            {
                if (dx > -100 && dx < 100 && dy > -100 && dy < 100)
                {
                    base.Move(dx, dy);
                    this.ValidatePosition();
                }
            }
        }

        public void SetSize(int w, int h)
        {
            if (this.width != w || this.height != h)
            {
                this.width = w;
                this.height = h;
                if (this.width == 0)
                {
                    this.width = 1;
                }
                if (this.height == 0)
                {
                    this.height = 1;
                }
                this.ValidateSize();
            }
        }

        protected internal virtual void ValidateSize()
        {
        }

        public virtual void ValidatePosition()
        {
            if (parent != null)
            {
                this.screenX = location.X() + this.parent.GetScreenX();
                this.screenY = location.Y() + this.parent.GetScreenY();
            }
            else
            {
                this.screenX = location.X();
                this.screenY = location.Y();
            }
        }

        public int GetScreenX()
        {
            return this.screenX;
        }

        public int GetScreenY()
        {
            return this.screenY;
        }

        protected internal void SetHeight(int height)
        {
            this.height = height;
        }

        protected internal void SetWidth(int width)
        {
            this.width = width;
        }

        public override int GetWidth()
        {
            return (int)(this.width * scaleX);
        }

        public override int GetHeight()
        {
            return (int)(this.height * scaleY);
        }

        public RectBox GetCollisionBox()
        {
            if (rect == null)
            {
                rect = new RectBox(screenX, screenY, width * scaleX, height
                        * scaleY);
            }
            else
            {
                rect.SetBounds(screenX, screenY, width * scaleX, height * scaleY);
            }
            return rect;
        }

        public string GetToolTipText()
        {
            return this.tooltip;
        }

        public void SetToolTipText(string text)
        {
            this.tooltip = text;
        }

        protected internal virtual void ProcessTouchPressed()
        {

        }

        protected internal virtual void ProcessTouchReleased()
        {
        }

        protected internal virtual void ProcessTouchClicked()
        {
        }

        protected internal virtual void ProcessTouchMoved()
        {
        }

        protected internal virtual void ProcessTouchDragged()
        {
        }

        protected internal virtual void ProcessTouchEntered()
        {
        }

        protected internal virtual void ProcessTouchExited()
        {
        }

        protected internal virtual void ProcessKeyPressed()
        {

        }

        protected internal virtual void ProcessKeyReleased()
        {
        }

        internal void KeyPressed()
        {
            this.CheckFocusKey();
            this.ProcessKeyPressed();
        }

        protected internal void CheckFocusKey()
        {
            if (this.input.GetKeyPressed() == Key.ENTER)
            {

                this.TransferFocus();

            }
            else
            {
                this.TransferFocusBackward();
            }
        }

        public LTexture[] GetImageUI()
        {
            return this.imageUI;
        }

        public void SetImageUI(LTexture[] imageUI, bool processUI)
        {
            if (imageUI != null)
            {
                this.width = imageUI[0].GetWidth();
                this.height = imageUI[0].GetHeight();
            }

            this.imageUI = imageUI;
        }

        public void SetImageUI(int index, LTexture imageUI)
        {
            if (imageUI != null)
            {
                this.width = imageUI.GetWidth();
                this.height = imageUI.GetHeight();
            }
            this.imageUI[index] = imageUI;
        }

        public abstract string GetUIName();

        public LTexture GetBackground()
        {
            return background;
        }

        public void ClearBackground()
        {
            this.SetBackground(new LTexture(1, 1, true, Loon.Core.Graphics.Opengl.LTexture.Format.SPEED));
        }

        public void SetBackground(string fileName)
        {
            this.SetBackground(new LTexture(fileName, Loon.Core.Graphics.Opengl.LTexture.Format.SPEED));
        }

        public void SetBackground(LColor color)
        {
            SetBackground(TextureUtils
                    .CreateTexture(GetWidth(), GetHeight(), color));
        }

        public void SetBackground(LTexture background)
        {
            if (background == null)
            {
                return;
            }
            LTexture oldImage = this.background;
            if (oldImage != background && oldImage != null)
            {
                oldImage.Destroy();
                oldImage = null;
            }
            this.background = background;
            this.SetAlpha(1.0F);
            this.width = background.GetWidth();
            this.height = background.GetHeight();
            if (this.width <= 0)
            {
                this.width = 1;
            }
            if (this.height <= 0)
            {
                this.height = 1;
            }
        }

        public int GetCamX()
        {
            return (cam_x == 0) ? X() : cam_x;
        }

        public int GetCamY()
        {
            return (cam_y == 0) ? Y() : cam_y;
        }

        protected internal virtual void CreateCustomUI(int w, int h)
        {
        }

        public bool IsClose()
        {
            return isClose;
        }

        public virtual bool IsAutoDestroy()
        {
            return autoDestroy;
        }

        public virtual void SetAutoDestroy(bool autoDestroy)
        {
            this.autoDestroy = autoDestroy;
        }

        public virtual Field2D GetField2D()
        {
            return null;
        }

        internal float scaleX = 1, scaleY = 1;

        public virtual void SetScale(float s)
        {
            this.SetScale(s, s);
        }

        public virtual void SetScale(float sx, float sy)
        {
            if (this.scaleX == sx && this.scaleY == sy)
            {
                return;
            }
            this.scaleX = sx;
            this.scaleY = sy;
        }

        public float GetScaleX()
        {
            return this.scaleX;
        }

        public float GetScaleY()
        {
            return this.scaleY;
        }

        public virtual bool IsBounded()
        {
            return true;
        }

        public virtual bool InContains(int x, int y, int w, int h)
        {
            if (parent != null)
            {
                return parent.Contains(x, y, w, h);
            }
            return false;
        }

        public virtual RectBox GetRectBox()
        {
            return GetCollisionBox();
        }

        public virtual int GetContainerWidth()
        {
            return parent.GetWidth();
        }

        public virtual int GetContainerHeight()
        {
            return parent.GetHeight();
        }

    }
}
