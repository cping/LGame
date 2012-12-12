using Loon.Utils.Collection;
using Loon.Utils;
using Loon.Core.Timer;
using Loon.Java.Collections;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
using Loon.Action.Map;
using System.Collections.Generic;
using Loon.Core.Graphics.Device;
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

        private Actor thing = null;

        private bool isListener = false;

        private bool isVSync;

        private int paintSeq = 0;

        private float angle;

        public LLayer(int w, int h)
            : this(0, 0, w, h)
        {

        }

        public LLayer(int w, int h, int size)
            : this(0, 0, w, h, true, size)
        {

        }

        public LLayer(int w, int h, bool bounded)
            : this(0, 0, w, h, bounded)
        {

        }

        public LLayer(int x, int y, int w, int h)
            : this(x, y, w, h, true)
        {

        }

        public LLayer(int x, int y, int w, int h, bool bounded)
            : this(x, y, w, h, bounded, 1)
        {

        }

        public LLayer(int x, int y, int w, int h, bool bounded, int size)
            : base(x, y, w, h, size, bounded)
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

        public virtual void SetVSync(bool vsync)
        {
            this.isVSync = vsync;
        }

        public virtual bool IsVSync()
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


        public virtual void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        public virtual long GetDelay()
        {
            return timer.GetDelay();
        }

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

        protected internal override void CreateCustomUI(GLEx g, int x, int y, int w, int h)
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

        public virtual void Paint(GLEx g)
        {

        }

        public virtual void PaintObjects(GLEx g, int minX, int minY, int maxX, int maxY)
        {
            lock (objects)
            {
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

                        if (thing.isAnimation)
                        {

                            if (isBitmapFilter)
                            {
                                g.DrawTexture(actorImage, actorX, actorY, width,
                                        height, angle, thing.filterColor);
                            }
                            else
                            {
                                if (colorAlpha != 1f)
                                {
                                    g.SetAlpha(colorAlpha);
                                }
                                g.DrawTexture(actorImage, actorX, actorY, width,
                                        height, angle);
                                if (colorAlpha != 1f)
                                {
                                    g.SetAlpha(1f);
                                }
                            }

                        }
                        else
                        {

                            int texId = actorImage.GetTextureID();

                            LTextureBatch batch = (LTextureBatch)textures
                                    .GetValue(texId);

                            if (batch == null)
                            {
                                LTextureBatch pBatch = LTextureBatch
                                        .BindBatchCache(this, texId,
                                                actorImage);
                                batch = pBatch;
                                batch.GLBegin();

                                textures.Put(texId, batch);

                            }

                            batch.SetTexture(actorImage);

                            if (isBitmapFilter)
                            {
                                batch.Draw(actorX, actorY, width, height, angle,
                                        thing.filterColor);
                            }
                            else
                            {
                                if (colorAlpha != 1f)
                                {
                                    alphaColor.a = colorAlpha;
                                }
                                batch.Draw(actorX, actorY, width, height, angle,
                                        alphaColor);
                                if (colorAlpha != 1f)
                                {
                                    alphaColor.a = 1;
                                }
                            }

                        }
                    }
                    if (thing.isConsumerDrawing)
                    {
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
                }

                int size = textures.Size();
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        LTextureBatch batch = (LTextureBatch)textures.Get(i);
                        batch.GLEnd();
                    }
                    textures.Clear();
                }
            }
        }

        public void MoveCamera(Actor actor)
        {
            MoveCamera(actor.X(), actor.Y());
        }

        public void CenterOn(Actor obj0)
        {
            obj0.SetLocation(GetWidth() / 2 - obj0.GetWidth() / 2, GetHeight()
                    / 2 - obj0.GetHeight() / 2);
        }

        public void TopOn(Actor obj0)
        {
            obj0.SetLocation(GetWidth() / 2 - obj0.GetWidth() / 2, 0);
        }

        public void LeftOn(Actor obj0)
        {
            obj0.SetLocation(0, GetHeight() / 2 - obj0.GetHeight() / 2);
        }

        public void RightOn(Actor obj0)
        {
            obj0.SetLocation(GetWidth() - obj0.GetWidth(), GetHeight() / 2
                    - obj0.GetHeight() / 2);
        }

        public void BottomOn(Actor obj0)
        {
            obj0.SetLocation(GetWidth() / 2 - obj0.GetWidth() / 2, GetHeight()
                    - obj0.GetHeight());
        }

        public virtual void SetField2DBackground(Field2D field, Dictionary<object, object> pathMap)
        {
            SetField2DBackground(field, pathMap, null);
        }

        public virtual void SetField2DBackground(Field2D field, Dictionary<object, object> pathMap,
                string fileName)
        {
            SetField2D(field);
            LImage background = null;

            if (fileName != null)
            {
                LImage tmp = LImage.CreateImage(fileName);
                background = SetTileBackground(tmp, true);
                if (tmp != null)
                {
                    tmp.Dispose();
                    tmp = null;
                }
            }
            else
            {
                background = LImage.CreateImage(GetWidth(), GetHeight(), false);
            }
            int srcWidth = GetWidth();
            int srcHeight = GetHeight();
            //在C#环境下LGraphics采取像素渲染，得不到硬件加速，此处直接将像素操作方法粘过来了(虽然也快不了几毫秒……)
            int[] dstColors = background.GetIntPixels();
            int[] srcColors = null;
            for (int i = 0; i < field.GetWidth(); i++)
            {
                for (int j = 0; j < field.GetHeight(); j++)
                {
                    int index = field.GetType(j, i);
                    object o = CollectionUtils.Get(pathMap, index);
                    if (o != null)
                    {
                        if (o is LImage)
                        {
                            LImage img = (LImage)o;
                            srcColors = img.GetIntPixels();
                            int w = img.Width;
                            int h = img.Height;
                            int y = field.TilesToHeightPixels(j);
                            int x = field.TilesToWidthPixels(i);
                            if (x < 0)
                            {
                                w += x;
                                x = 0;
                            }
                            if (y < 0)
                            {
                                h += y;
                                y = 0;
                            }
                            if (x + w > srcWidth)
                            {
                                w = srcWidth - x;
                            }
                            if (y + h > srcHeight)
                            {
                                h = srcHeight - y;
                            }
                            if (img.hasAlpha)
                            {
                                int findIndex = y * srcWidth + x;
                                int drawIndex = 0;
                                int moveFind = srcWidth - w;
                                for (int col = 0; col < h; col++)
                                {
                                    for (int row = 0; row < w; )
                                    {
                                        if (srcColors[drawIndex] != 0)
                                        {
                                            dstColors[findIndex] = srcColors[drawIndex];
                                        }
                                        row++;
                                        findIndex++;
                                        drawIndex++;
                                    }
                                    findIndex += moveFind;
                                }
                            }
                            else
                            {
                                for (int size = 0; size < h; size++)
                                {
                                    System.Array.Copy(srcColors, size * w, dstColors,
                                                     (y + size) * srcWidth + x, w);
                                }
                            }
                        }
                        else if (o is Actor)
                        {
                            AddObject(((Actor)o), field.TilesToWidthPixels(i),
                                    field.TilesToHeightPixels(j));
                        }
                    }
                }
            }
            background.SetIntPixels(dstColors);
            background.SetFormat(Loon.Core.Graphics.Opengl.LTexture.Format.SPEED);
            SetBackground(background.GetTexture());
            srcColors = null;
            dstColors = null;
            if (background != null)
            {
                background.Dispose();
                background = null;
            }
        }

        public virtual void SetTileBackground(string fileName)
        {
            SetTileBackground(LImage.CreateImage(fileName));
        }

        public virtual void SetTileBackground(LImage image)
        {
            SetTileBackground(image, false);
        }

        public virtual LImage SetTileBackground(LImage image, bool isReturn)
        {
            if (image == null)
            {
                return null;
            }
            int layerWidth = GetWidth();
            int layerHeight = GetHeight();
            int tileWidth = image.GetWidth();
            int tileHeight = image.GetHeight();

            LImage tempImage = LImage.CreateImage(layerWidth, layerHeight, false);
            LGraphics g = tempImage.GetLGraphics();
            for (int x = 0; x < layerWidth; x += tileWidth)
            {
                for (int y = 0; y < layerHeight; y += tileHeight)
                {
                    g.DrawImage(image, x, y);
                }
            }
            g.Dispose();
            if (isReturn)
            {
                return tempImage;
            }
            tempImage.SetFormat(Loon.Core.Graphics.Opengl.LTexture.Format.SPEED);
            SetBackground(tempImage.GetTexture());
            if (tempImage != null)
            {
                tempImage.Dispose();
                tempImage = null;
            }
            return null;
        }

        public virtual int GetScroll(RectBox visibleRect, int orientation, int direction)
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
            int increment = System.Math.Abs((int)System.Math.IEEERemainder(scrollPos, cellSize));
            if (increment == 0)
            {
                increment = cellSize;
            }
            return increment;
        }

        public virtual Actor GetClickActor()
        {
            return dragActor;
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
                                int dx = dropX - (rect.width / 2);
                                int dy = dropY - (rect.height / 2);
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
                            int dx = dropX - (rect.width / 2);
                            int dy = dropY - (rect.height / 2);
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

        public virtual bool IsTouchPressed()
        {
            return this.pressed;
        }

        public virtual bool IsActorDrag()
        {
            return actorDrag;
        }

        public virtual void SetActorDrag(bool actorDrag)
        {
            this.actorDrag = actorDrag;
        }

        public virtual bool IsLimitMove()
        {
            return isLimitMove;
        }

        public virtual void SetLimitMove(bool isLimitMove)
        {
            this.isLimitMove = isLimitMove;
        }

        public virtual bool IsLocked()
        {
            return locked;
        }

        public virtual void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        public virtual bool IsTouchClick()
        {
            return isTouchClick;
        }

        public virtual void SetTouchClick(bool isTouchClick)
        {
            this.isTouchClick = isTouchClick;
        }

        public virtual int GetLayerTouchX()
        {
            return this.input.GetTouchX() - this.GetScreenX();
        }

        public virtual int GetLayerTouchY()
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
                object[] o = objects.ToActors();
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

        public override string GetUIName()
        {
            return "Layer";
        }
    }
}
