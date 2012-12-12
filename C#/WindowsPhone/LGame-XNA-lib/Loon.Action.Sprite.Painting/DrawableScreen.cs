using Loon.Core.Graphics;
using System.Collections.Generic;
using Loon.Core.Timer;
using Loon.Utils;
using Loon.Core.Graphics.Opengl;
using Loon.Core;
using Loon.Core.Geom;
using Loon.Core.Input;
namespace Loon.Action.Sprite.Painting
{

    public abstract class DrawableScreen : Screen
    {

        private List<Drawable> drawables;

        private List<Drawable> drawablesToUpdate;

        private List<Drawable> drawablesToDraw;

        private GameComponentCollection gameCollection;

        private bool isInit;

        private SpriteBatch batch;

        private readonly GameTime gameTime = new GameTime();

        public DrawableScreen()
        {
            this.drawables = new List<Drawable>();
            this.drawablesToUpdate = new List<Drawable>();
            this.drawablesToDraw = new List<Drawable>();
            this.gameCollection = new GameComponentCollection();
        }

        public virtual LFont GetFont()
        {
            if (batch != null)
            {
                return batch.GetFont();
            }
            return LFont.GetDefaultFont();
        }

        public virtual void AddDrawable(Drawable drawable)
        {
            drawable.drawableScreen = this;
            drawable.LoadContent();
            CollectionUtils.Add(drawables, drawable);
        }

        public virtual void AddDrawable(Drawable drawable, int index)
        {
            drawable.drawableScreen = this;
            drawable.LoadContent();
            CollectionUtils.Add(drawables, drawable);
            for (int i = 0; i < drawables.Count; i++)
            {
                if (i == index)
                {
                    drawables[i]._enabled = true;
                }
                else
                {
                    drawables[i]._enabled = false;
                }
            }
        }

        public override void Draw(GLEx g)
        {
            if (batch != null)
            {
                batch.Begin();

                gameCollection.Draw(batch, gameTime);
                if (drawablesToDraw.Count > 0)
                {
                    CollectionUtils.Clear(drawablesToDraw);
                }

                foreach (Drawable drawable in drawables)
                {
                    CollectionUtils.Add(drawablesToDraw, drawable);
                }

                foreach (Drawable drawable in drawablesToDraw)
                {
                    if (drawable._enabled)
                    {
                        if (drawable.GetDrawableState() == Painting.DrawableState.Hidden)
                        {
                            continue;
                        }
                        drawable.Draw(batch, gameTime);
                    }
                }
                Draw(batch);
                batch.End();
            }
        }

        public abstract void Draw(SpriteBatch batch);

        public virtual void FadeBackBufferToBlack(float a)
        {
            DrawRectangle(LSystem.screenRect, 0f, 0f, 0f, a);
        }

        public virtual void DrawRectangle(RectBox rect, LColor c)
        {
            DrawRectangle(rect, c.r, c.g, c.b, c.a);
        }

        public virtual void DrawRectangle(RectBox rect, float r, float g, float b, float a)
        {
            GLEx gl = GLEx.Self;
            if (gl != null)
            {
                gl.SetColor(r, g, b, a);
                gl.FillRect(rect.x, rect.y, rect.width, rect.height);
                gl.ResetColor();
            }
        }

        public virtual List<Drawable> GetDrawables()
        {
            return new List<Drawable>(drawables);
        }

        public override void OnLoad()
        {
            if (GLEx.Self != null)
            {
                if (batch == null)
                {
                    batch = new SpriteBatch();
                }
                foreach (Drawable drawable in drawables)
                {
                    drawable.LoadContent();
                }
                isInit = true;
                LoadContent();
                gameCollection.Load();
            }
        }

        public abstract void LoadContent();

        public virtual void RemoveDrawable(Drawable drawable)
        {
            drawable.UnloadContent();
            CollectionUtils.Remove(drawables, drawable);
            CollectionUtils.Remove(drawablesToUpdate, drawable);
        }

        public abstract void UnloadContent();

        public abstract void Pressed(LTouch e);

        public abstract void Released(LTouch e);

        public abstract void Move(LTouch e);

        public abstract void Drag(LTouch e);

        public abstract void Pressed(LKey e);

        public abstract void Released(LKey e);

        public override void Alter(LTimerContext timer)
        {
            if (!IsOnLoadComplete())
            {
                return;
            }

            gameTime.Update(timer);

            if (!isInit)
            {
                LoadContent();
            }

            gameCollection.Update(gameTime);
            if (drawablesToUpdate.Count > 0)
            {
                CollectionUtils.Clear(drawablesToUpdate);
            }

            foreach (Drawable drawable in drawables)
            {
                CollectionUtils.Add(drawablesToUpdate, drawable);
            }

            bool otherScreenHasFocus = false;
            bool coveredByOtherScreen = false;

            Drawable _drawable;
            int screenIndex;
            for (; drawablesToUpdate.Count > 0; )
            {

                screenIndex = drawablesToUpdate.Count - 1;
                _drawable = drawablesToUpdate[screenIndex];

                CollectionUtils.RemoveAt(drawablesToUpdate, screenIndex);

                if (_drawable._enabled)
                {
                    _drawable.Update(gameTime, otherScreenHasFocus,
                            coveredByOtherScreen);

                    if (_drawable.GetDrawableState() == Painting.DrawableState.TransitionOn
                            || _drawable.GetDrawableState() == Painting.DrawableState.Active)
                    {
                        if (!otherScreenHasFocus)
                        {
                            _drawable.HandleInput(this);
                            otherScreenHasFocus = true;
                        }
                        if (!_drawable.IsPopup)
                        {
                            coveredByOtherScreen = true;
                        }
                    }
                }
            }

            Update(gameTime);
        }

        public abstract void Update(GameTime gameTime);

        public override void OnKeyDown(LKey e)
        {
            foreach (Drawable drawable in drawablesToDraw)
            {
                if (drawable._enabled)
                {
                    if (drawable != null)
                    {
                        if (drawable.GetDrawableState() == Painting.DrawableState.Hidden)
                        {
                            continue;
                        }
                        drawable.Pressed(e);
                    }
                }
            }
            Pressed(e);
        }

        public override void OnKeyUp(LKey e)
        {
            foreach (Drawable drawable in drawablesToDraw)
            {
                if (drawable._enabled)
                {
                    if (drawable != null)
                    {
                        if (drawable.GetDrawableState() == Painting.DrawableState.Hidden)
                        {
                            continue;
                        }
                        drawable.Released(e);
                    }
                }
            }
            Released(e);
        }

        public override void TouchDown(LTouch e)
        {
            foreach (Drawable drawable in drawablesToDraw)
            {
                if (drawable._enabled)
                {
                    if (drawable != null)
                    {
                        if (drawable.GetDrawableState() == Painting.DrawableState.Hidden)
                        {
                            continue;
                        }
                        drawable.Pressed(e);
                    }
                }
            }
            Pressed(e);
        }

        public override void TouchUp(LTouch e)
        {
            foreach (Drawable drawable in drawablesToDraw)
            {
                if (drawable._enabled)
                {
                    if (drawable != null)
                    {
                        if (drawable.GetDrawableState() == Painting.DrawableState.Hidden)
                        {
                            continue;
                        }
                        drawable.Released(e);
                    }
                }
            }
            Released(e);
        }

        public override void TouchMove(LTouch e)
        {
            foreach (Drawable drawable in drawablesToDraw)
            {
                if (drawable._enabled)
                {
                    if (drawable != null)
                    {
                        if (drawable.GetDrawableState() == Painting.DrawableState.Hidden)
                        {
                            continue;
                        }
                        drawable.Move(e);
                    }
                }
            }
            Move(e);
        }

        public override void TouchDrag(LTouch e)
        {
            Drag(e);
        }

        public virtual SpriteBatch GetSpriteBatch()
        {
            return batch;
        }

        public virtual GameTime GetGameTime()
        {
            return gameTime;
        }

        public virtual GameComponentCollection Components()
        {
            return gameCollection;
        }

        public override void Dispose()
        {
            foreach (Drawable drawable in drawables)
            {
                if (drawable != null)
                {
                    drawable._enabled = false;
                    drawable.UnloadContent();
                    drawable.Dispose();
                }
            }
            CollectionUtils.Clear(drawables);
            CollectionUtils.Clear(drawablesToUpdate);
            CollectionUtils.Clear(drawablesToDraw);
            gameCollection.Clear();
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
            UnloadContent();
            isInit = false;
        }

    }
}
