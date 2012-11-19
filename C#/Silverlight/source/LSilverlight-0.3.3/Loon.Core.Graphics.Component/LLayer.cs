#region LGame License
/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
using System;
using System.Collections.Generic;
using Loon.Core.Graphics.OpenGL;
using Loon.Utils.Collection;
using Loon.Utils;
using Loon.Core.Timer;
using Loon.Java.Collections;
using Loon.Core.Geom;
using Loon.Action.Map;

namespace Loon.Core.Graphics.Component
{
    public class LLayer : ActorLayer
    {

        private readonly ArrayMap textures = new ArrayMap(
                CollectionUtils.INITIAL_CAPACITY);

        private readonly LColor alphaColor = new LColor(1f, 1f, 1f, 1f);

        private float width;

        private float height;

        private float colorAlpha;

        private bool isBitmapFilter;

        private float actorX;

        private float actorY;

        private float actorWidth;

        private float actorHeight;

        protected internal bool actorDrag, pressed;

        private Actor dragActor;

        private LTimer timer = new LTimer(0);

        private bool isTouchClick;

        private bool isListener = false;

        private bool isVSync;
	
        private int paintSeq = 0;

        private Actor thing = null;

        private float angle;

        public bool Visible
        {
            get
            {
                return visible;
            }
            set
            {
                visible = value;
            }
        }

        public LLayer(int w, int h):this(0, 0, w, h)
        {
            
        }

        public LLayer(int w, int h, bool bounded): this(0, 0, w, h, bounded)
        {
           
        }

        public LLayer(int x, int y, int w, int h):  this(x, y, w, h, true)
        {
          
        }

        public LLayer(int x, int y, int w, int h, bool bounded): this(x, y, w, h, bounded, 1)
        {
           
        }

        public LLayer(int x, int y, int w, int h, bool bounded, int size): base(x, y, w, h, size, bounded)
        {
           
            this.SetLocation(x, y);
            this.actorDrag = true;
            this.visible = true;
            this.customRendering = true;
            this.isTouchClick = true;
            this.isLimitMove = true;
            this.isVSync = true;
            this.SetElastic(true);
            this.SetLocked(true);
            this.SetLayer(100);
        }

        public void SetVSync(bool vsync)
        {
            this.isVSync = vsync;
        }

        public bool IsVSync()
        {
            return isVSync;
        }

        public virtual void DownClick(int x, int y)
        {
            if (Click != null)
            {
                Click.DownClick(this, x, y);
            }
        }

        public virtual void UpClick(int x, int y)
        {
            if (Click != null)
            {
                Click.UpClick(this, x, y);
            }
        }

        public virtual void Drag(int x, int y)
        {
            if (Click != null)
            {
                Click.DragClick(this, x, y);
            }
        }

        public virtual void DownKey()
        {

        }

        public virtual void UpKey()
        {

        }

        /// <summary>
        /// 设定动作触发延迟时间
        /// </summary>
        ///
        /// <param name="delay"></param>
        public void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        /// <summary>
        /// 返回动作触发延迟时间
        /// </summary>
        ///
        /// <returns></returns>
        public long GetDelay()
        {
            return timer.GetDelay();
        }

        /// <summary>
        /// 动作处理
        /// </summary>
        ///
        /// <param name="elapsedTime"></param>
        public override void Action(long elapsedTime)
        {

        }

        public override void Update(long elapsedTime)
        {
            if (visible)
            {
                base.Update(elapsedTime);
                if (timer.Action(this.elapsedTime = elapsedTime))
                {
                    Action(elapsedTime);
                    if (!isVSync)
                    {
                        IIterator it = objects.NewIterator();
                        for (; it.HasNext(); )
                        {
                            thing = (Actor)it.Next();
                            if (!thing.visible)
                            {
                                continue;
                            }
                            thing.Update(elapsedTime);
                        }
                    }
                }
            }
        }

        protected override void CreateCustomUI(GLEx g, int x, int y, int w, int h)
        {
            if (!visible)
            {
                return;
            }
            PaintObjects(g, x, y, x + w, y + h);
            if (x == 0 && y == 0)
            {
                Paint(g);
            }
            else
            {
                g.Translate(x, y);
                Paint(g);
                g.Translate(-x, -y);
            }
        }

        public void Paint(GLEx g)
        {

        }

        public void PaintObjects(GLEx g, int minX, int minY, int maxX, int maxY)
        {
            lock (objects)
            {
                g.BeginBatch();
                IIterator it = objects.Iterator();
                for (; it.HasNext(); )
                {
                    thing = (Actor)it.Next();
                    if (!thing.visible)
                    {
                        continue;
                    }
                    isListener = (thing.actorListener != null);

                    if (isVSync)
                    {
                        if (isListener)
                        {
                            thing.actorListener.Update(elapsedTime);
                        }
                        thing.Update(elapsedTime);
                    }

                    RectBox rect = thing.GetRectBox();
                    actorX = minX + thing.GetX();
                    actorY = minY + thing.GetY();
                    actorWidth = rect.width;
                    actorHeight = rect.height;
                    if (actorX + actorWidth < minX || actorX > maxX
                            || actorY + actorHeight < minY || actorY > maxY)
                    {
                        continue;
                    }
                    LTexture actorImage = thing.GetImage();
                    if (actorImage != null)
                    {
                        width = (actorImage.GetWidth() * thing.scaleX);
                        height = (actorImage.GetHeight() * thing.scaleY);
                        isBitmapFilter = (thing.filterColor != null);
                        thing.SetLastPaintSeqNum(paintSeq++);
                        angle = thing.GetRotation();
                        colorAlpha = thing.alpha;

                        if (isBitmapFilter)
                        {
                            g.DrawBatch(actorImage, actorX, actorY, width,
                                    height, angle, thing.filterColor.Color);
                        }
                        else
                        {
                            if (colorAlpha != 1f)
                            {
                                g.SetAlpha(colorAlpha);
                            }
                            g.DrawBatch(actorImage, actorX, actorY, width,
                                    height, angle);
                            if (colorAlpha != 1f)
                            {
                                g.SetAlpha(1f);
                            }
                        }
                    }
                    if (actorX == 0 && actorY == 0)
                    {
                        thing.Draw(g);
                        if (isListener)
                        {
                            thing.actorListener.Draw(g);
                        }
                    }
                    else
                    {
                        g.Translate(actorX, actorY);
                        thing.Draw(g);
                        if (isListener)
                        {
                            thing.actorListener.Draw(g);
                        }
                        g.Translate(-actorX, -actorY);
                    }
                }
                g.EndBatch();
            }
        }

        public void MoveCamera(Actor actor)
        {
            MoveCamera(actor.X(), actor.Y());
        }

        public void CenterOn(Actor o)
        {
            o.SetLocation(GetWidth() / 2 - o.GetWidth() / 2, GetHeight()
                    / 2 - o.GetHeight() / 2);
        }

        public void TopOn(Actor o)
        {
            o.SetLocation(GetWidth() / 2 - o.GetWidth() / 2, 0);
        }

        public void LeftOn(Actor o)
        {
            o.SetLocation(0, GetHeight() / 2 - o.GetHeight() / 2);
        }

        public void RightOn(Actor o)
        {
            o.SetLocation(GetWidth() - o.GetWidth(), GetHeight() / 2
                    - o.GetHeight() / 2);
        }

        public void BottomOn(Actor o)
        {
            o.SetLocation(GetWidth() / 2 - o.GetWidth() / 2, GetHeight()
                    - o.GetHeight());
        }

        public void SetField2DBackground(Field2D field, Dictionary<object, object> pathMap)
        {
            SetField2DBackground(field, pathMap, null);
        }

        public void SetField2DBackground(Field2D field, Dictionary<object, object> pathMap,
                String fileName)
        {
            SetField2D(field);
            LPixmap background = null;

            if (fileName != null)
            {
                LPixmap tmp = new LPixmap(fileName);
                background = SetTileBackground(tmp, true);
                if (tmp != null)
                {
                    tmp.Dispose();
                    tmp = null;
                }
            }
            else
            {
                background = new LPixmap(GetWidth(), GetHeight(), false);
            }

            for (int i = 0; i < field.GetWidth(); i++)
            {
                for (int j = 0; j < field.GetHeight(); j++)
                {
                    int index = field.GetType(j, i);
                    Object o = CollectionUtils.Get(pathMap, index);
                    if (o != null)
                    {
                        if (o is LPixmap)
                        {
                            background.DrawPixmap(((LPixmap)o), field.TilesToWidthPixels(i),
                                    field.TilesToHeightPixels(j));
                        }
                        else if (o is Actor)
                        {
                            AddObject(((Actor)o), field.TilesToWidthPixels(i),
                                    field.TilesToHeightPixels(j));
                        }
                    }
                }
            }
            SetBackground(background.Texture);
            if (background != null)
            {
                background.Dispose();
                background = null;
            }
        }
        
        public void SetTileBackground(String fileName)
        {
            SetTileBackground(new LPixmap(fileName));
        }

        public void SetTileBackground(LPixmap image)
        {
            SetTileBackground(image, false);
        }

        public LPixmap SetTileBackground(LPixmap image, bool isReturn)
        {
            if (image == null)
            {
                return null;
            }
            int layerWidth = GetWidth();
            int layerHeight = GetHeight();
            int tileWidth = image.GetWidth();
            int tileHeight = image.GetHeight();
            LPixmap tempImage = new LPixmap(layerWidth, layerHeight, false);
            for (int x = 0; x < layerWidth; x += tileWidth)
            {
                for (int y = 0; y < layerHeight; y += tileHeight)
                {
                    tempImage.DrawPixmap(image, x, y);
                }
            }
            if (isReturn)
            {
                return tempImage;
            }
            SetBackground(tempImage.Texture);
            return null;
        }

        public int GetScroll(RectBox visibleRect, int orientation, int direction)
        {
            int cellSize = this.GetCellSize();
            double scrollPos = 0.0D;
            if (orientation == 0)
            {
                if (direction < 0)
                {
                    scrollPos = visibleRect.GetMinX();
                }
                else if (direction > 0)
                {
                    scrollPos = visibleRect.GetMaxX();
                }
            }
            else if (direction < 0)
            {
                scrollPos = visibleRect.GetMinY();
            }
            else if (direction > 0)
            {
                scrollPos = visibleRect.GetMaxY();
            }
            int increment = MathUtils.Abs((int)Math.IEEERemainder(scrollPos, cellSize));
            if (increment == 0)
            {
                increment = cellSize;
            }
            return increment;
        }

        public Actor GetClickActor()
        {
            return dragActor;
        }

        protected internal override void ProcessTouchPressed()
        {
            if (!isTouchClick)
            {
                return;
            }
            if (!input.IsMoving())
            {
                int dx = this.input.GetTouchX() - this.GetScreenX();
                int dy = this.input.GetTouchY() - this.GetScreenY();
                dragActor = GetSynchronizedObject(dx, dy);
                if (dragActor != null)
                {
                    if (dragActor.IsClick())
                    {
                        dragActor.DownClick(dx, dy);
                        if (dragActor.actorListener != null)
                        {
                            dragActor.actorListener.DownClick(dx, dy);
                        }
                    }
                }
                this.DownClick(dx, dy);
            }
        }

        protected internal override void ProcessTouchReleased()
        {
            if (!isTouchClick)
            {
                return;
            }
            if (!input.IsMoving())
            {
                int dx = this.input.GetTouchX() - this.GetScreenX();
                int dy = this.input.GetTouchY() - this.GetScreenY();
                dragActor = GetSynchronizedObject(dx, dy);
                if (dragActor != null)
                {
                    if (dragActor.IsClick())
                    {
                        dragActor.UpClick(dx, dy);
                        if (dragActor.actorListener != null)
                        {
                            dragActor.actorListener.UpClick(dx, dy);
                        }
                    }
                }
                this.UpClick(dx, dy);
                this.dragActor = null;
            }
        }

        protected internal override void ProcessTouchEntered()
        {
            this.pressed = true;
        }

        protected internal override void ProcessTouchExited()
        {
            this.pressed = false;
        }

        protected internal override void ProcessKeyPressed()
        {
            if (this.IsSelected())
            {
                this.DownKey();
            }
        }

        protected internal override void ProcessKeyReleased()
        {
            if (this.IsSelected())
            {
                this.UpKey();
            }
        }

        protected internal override void ProcessTouchDragged()
        {
            int dropX = 0;
            int dropY = 0;
            if (!locked)
            {
                bool moveActor = false;
                if (actorDrag)
                {
                    lock (objects)
                    {
                        dropX = this.input.GetTouchX() - this.GetScreenX();
                        dropY = this.input.GetTouchY() - this.GetScreenY();
                        if (dragActor == null)
                        {
                            dragActor = GetSynchronizedObject(dropX, dropY);
                        }
                        if (dragActor != null && dragActor.IsDrag())
                        {
                            lock (dragActor)
                            {
                                objects.SendToFront(dragActor);
                                RectBox rect = dragActor.GetBoundingRect();
                                int dx = (dropX - (rect.width / 2));
                                int dy = (dropY - (rect.height / 2));
                                if (dragActor.GetLLayer() != null)
                                {
                                    dragActor.SetLocation(dx, dy);
                                    dragActor.Drag(dropX, dropY);
                                    if (dragActor.actorListener != null)
                                    {
                                        dragActor.actorListener.Drag(dropX, dropY);
                                    }
                                }
                                moveActor = true;
                            }
                        }
                    }
                }
                if (!moveActor)
                {
                    lock (input)
                    {
                        dropX = this.input.GetTouchDX();
                        dropY = this.input.GetTouchDY();
                        if (IsNotMoveInScreen(dropX + this.X(), dropY + this.Y()))
                        {
                            return;
                        }
                        if (GetContainer() != null)
                        {
                            GetContainer().SendToFront(this);
                        }
                        this.Move(dropX, dropY);
                        this.Drag(dropX, dropY);
                    }
                }
            }
            else
            {
                if (!actorDrag)
                {
                    return;
                }
                lock (objects)
                {
                    dropX = this.input.GetTouchX() - this.GetScreenX();
                    dropY = this.input.GetTouchY() - this.GetScreenY();
                    if (dragActor == null)
                    {
                        dragActor = GetSynchronizedObject(dropX, dropY);
                    }
                    if (dragActor != null && dragActor.IsDrag())
                    {
                        lock (dragActor)
                        {
                            objects.SendToFront(dragActor);
                            RectBox rect = dragActor.GetBoundingRect();
                            int dx = (dropX - (rect.width / 2));
                            int dy = (dropY - (rect.height / 2));
                            if (dragActor.GetLLayer() != null)
                            {
                                dragActor.SetLocation(dx, dy);
                                dragActor.Drag(dropX, dropY);
                                if (dragActor.actorListener != null)
                                {
                                    dragActor.actorListener.Drag(dropX, dropY);
                                }
                            }
                        }
                    }
                }
            }
        }

        public bool IsTouchPressed()
        {
            return this.pressed;
        }

        public bool IsActorDrag()
        {
            return actorDrag;
        }

        public void SetActorDrag(bool actorDrag)
        {
            this.actorDrag = actorDrag;
        }

        public bool IsLimitMove()
        {
            return isLimitMove;
        }

        public void SetLimitMove(bool isLimitMove)
        {
            this.isLimitMove = isLimitMove;
        }

        public bool IsLocked()
        {
            return locked;
        }

        public void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        public bool IsTouchClick()
        {
            return isTouchClick;
        }

        public void SetTouchClick(bool isTouchClick)
        {
            this.isTouchClick = isTouchClick;
        }

        public int GetLayerTouchX()
        {
            return this.input.GetTouchX() - this.GetScreenX();
        }

        public int GetLayerTouchY()
        {
            return this.input.GetTouchY() - this.GetScreenY();
        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {

        }

        public override void Dispose()
        {
            base.Dispose();
            if (textures != null)
            {
                textures.Clear();
            }
            if (collisionChecker != null)
            {
                collisionChecker.Dispose();
                collisionChecker = null;
            }
            if (objects != null)
            {
                Object[] o = objects.ToActors();
                for (int i = 0; i < o.Length; i++)
                {
                    Actor actor = (Actor)o[i];
                    if (actor != null)
                    {
                        actor.Dispose();
                        actor = null;
                    }
                }
            }
        }

        protected internal override void ValidateSize()
        {
            base.ValidateSize();
        }

        public override String GetUIName()
        {
            return "Layer";
        }
    }
}
