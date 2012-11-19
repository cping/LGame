/// <summary>
/// Copyright 2008 - 2012
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
/// @project loon
/// @email£ºjavachenpeng@yahoo.com
namespace Loon.Action.Sprite
{

    using System;
    using System.Collections;
    using System.ComponentModel;
    using System.IO;
    using System.Runtime.CompilerServices;
    using Loon.Action.Map;
    using Loon.Core.Graphics;
    using Loon.Utils.Collection;
    using Loon.Action.Sprite;
    using System.Collections.Generic;
    using Loon.Utils;
    using Loon.Core.Geom;
    using Loon.Core;
    using Loon.Core.Event;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Timer;
    using Loon.Core.Input;

    public abstract class SpriteBatchScreen : Screen
    {

        private ArrayMap keyActions = new ArrayMap(CollectionUtils.INITIAL_CAPACITY);

        private SpriteBatch batch;

        private List<SpriteBatchObject> objects;

        private List<SpriteBatchObject> pendingAdd;

        private List<SpriteBatchObject> pendingRemove;

        private List<TileMap> tiles = new List<TileMap>(10);

        private Vector2f offset = new Vector2f();

        private LObject follow;

        private TileMap indexTile;

        private SpriteBatchObject[] lazyObjects;

        public SpriteBatchScreen()
        {
            this.objects = new List<SpriteBatchObject>(10);
            this.pendingAdd = new List<SpriteBatchObject>(10);
            this.pendingRemove = new List<SpriteBatchObject>(10);
            this.lazyObjects = new SpriteBatchObject[] { };
        }

        public void Commits() {
			bool changes = false;
			int additionCount = pendingAdd.Count;
			if (additionCount > 0) {
				object[] additionsArray = pendingAdd.ToArray();
				for (int i = 0; i < additionCount; i++) {
					SpriteBatchObject o = (SpriteBatchObject) additionsArray[i];
					objects.Add(o);
				}
				pendingAdd.Clear();
				changes = true;
			}
			int removalCount = pendingRemove.Count;
			if (removalCount > 0) {
				object[] removalsArray = pendingRemove.ToArray();
				for (int i_0 = 0; i_0 < removalCount; i_0++) {
					SpriteBatchObject object_1 = (SpriteBatchObject) removalsArray[i_0];
					objects.Remove(object_1);
				}
				pendingRemove.Clear();
				changes = true;
			}
			if (changes) {
				lazyObjects = objects.ToArray();
			}
		}

        public SpriteBatchObject[] GetObjects()
        {
            return lazyObjects;
        }

        public int GetCount()
        {
            return lazyObjects.Length;
        }

        public int GetConcreteCount()
        {
            return lazyObjects.Length + pendingAdd.Count - pendingRemove.Count;
        }

        public void Add(SpriteBatchObject obj0)
        {
            pendingAdd.Add(obj0);
        }

        public void Remove(SpriteBatchObject obj0)
        {
            pendingRemove.Add(obj0);
        }

        public void RemoveTileObjects()
        {
            int count = objects.Count;
            object[] objectArray = objects.ToArray();
            for (int i = 0; i < count; i++)
            {
                pendingRemove.Add((SpriteBatchObject)objectArray[i]);
            }
            pendingAdd.Clear();
        }

        public SpriteBatchObject FindObject(float x, float y)
        {
            foreach (SpriteBatchObject o in objects)
            {
                if (o.GetX() == x && o.GetY() == y)
                {
                    return o;
                }
            }
            return null;
        }

        public TileMap GetIndexTile()
        {
            return indexTile;
        }

        public void SetIndexTile(TileMap indexTile)
        {
            this.indexTile = indexTile;
        }

        public void Follow(LObject o)
        {
            this.follow = o;
        }

        public override void OnLoad()
        {
            if (batch == null)
            {
                batch = new SpriteBatch(2000);
            }
        }

        public override void OnLoaded()
        {
            Create();
        }

        public abstract void Create();

        public void AddActionKey(Int32 keyCode, ActionKey e)
        {
            keyActions.Put(keyCode, e);
        }

        public void RemoveActionKey(Int32 keyCode)
        {
            keyActions.Remove(keyCode);
        }

        public void PressActionKey(Int32 keyCode)
        {
            ActionKey key = (ActionKey)keyActions.GetValue(keyCode);
            if (key != null)
            {
                key.Press();
            }
        }

        public void ReleaseActionKey(Int32 keyCode)
        {
            ActionKey key = (ActionKey)keyActions.GetValue(keyCode);
            if (key != null)
            {
                key.Release();
            }
        }

        public void ClearActionKey()
        {
            keyActions.Clear();
        }

        public void ReleaseActionKeys()
        {
            int size = keyActions.Size();
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    ActionKey act = (ActionKey)keyActions.Get(i);
                    act.Release();
                }
            }
        }

        public void SetOffset(float sx, float sy)
        {
            offset.Set(sx, sy);
            
            foreach (TileMap tile in tiles)
            {
                tile.SetOffset(offset);
            }
        }

        public void PutTileMap(TileMap t)
        {
            tiles.Add(t);
        }

        public void RemoveTileMap(TileMap t)
        {
            tiles.Remove(t);
        }

        public void AddTileObject(SpriteBatchObject o)
        {
            Add(o);
        }

        public JumpObject AddJumpObject(float x, float y, float w, float h,
                Animation a)
        {
            JumpObject o = null;
            if (indexTile != null)
            {
                o = new JumpObject(x, y, w, h, a, indexTile);
            }
            else if (tiles.Count > 0)
            {
                o = new JumpObject(x, y, w, h, a, tiles[0]);
            }
            else
            {
                return null;
            }
            Add(o);
            return o;
        }

        public MoveObject AddMoveObject(float x, float y, float w, float h,
                Animation a)
        {
            MoveObject o = null;
            if (indexTile != null)
            {
                o = new MoveObject(x, y, w, h, a, indexTile);
            }
            else if (tiles.Count > 0)
            {
                o = new MoveObject(x, y, w, h, a, tiles[0]);
            }
            else
            {
                return null;
            }
            Add(o);
            return o;
        }

        public void RemoveTileObject(SpriteBatchObject o)
        {
            Remove(o);
        }
        
	private float objX, objY;

    public override void Draw(GLEx g)
    {
        if (IsOnLoadComplete())
        {
            batch.Begin();
            Before(batch);
            foreach (TileMap tile in tiles)
            {
                tile.Draw(g, batch, offset.x, offset.y);
            }
            foreach (SpriteBatchObject o in objects)
            {
                objX = o.GetX() + offset.x;
                objY = o.GetY() + offset.y;
                if (Contains(objX, objY))
                {
                    o.Draw(batch, offset.x, offset.y);
                }
            }
            After(batch);
            batch.End();
        }
    }

        public abstract void After(SpriteBatch batch);

        public abstract void Before(SpriteBatch batch);

        public override void Alter(LTimerContext timer)
        {
            int size = keyActions.Size();
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    ActionKey act = (ActionKey)keyActions.Get(i);
                    if (act.IsPressed())
                    {
                        act.Act(elapsedTime);
                        if (act.isReturn)
                        {
                            return;
                        }
                    }
                }
            }
            if (follow != null)
            {
                foreach (TileMap tile in tiles)
                {
                    float offsetX = GetHalfWidth() - follow.GetX();
                    offsetX = MathUtils.Min(offsetX, 0);
                    offsetX = MathUtils.Max(offsetX, GetWidth() - tile.GetWidth());

                    float offsetY = GetHalfHeight() - follow.GetY();
                    offsetY = MathUtils.Min(offsetY, 0);
                    offsetY = MathUtils
                            .Max(offsetY, GetHeight() - tile.GetHeight());

                    SetOffset(offsetX, offsetY);
                    tile.Update(elapsedTime);
                }
            }

            foreach (SpriteBatchObject o in objects)
            {
                o.Update(elapsedTime);
                if (updateListener != null)
                {
                    updateListener.Act(o, elapsedTime);
                }
            }
            Update(elapsedTime);
            Commits();
        }

        protected internal UpdateListener updateListener;

        public interface UpdateListener
        {

            void Act(SpriteBatchObject obj, long elapsedTime);

        }

        public override void OnKeyDown(LKey e)
        {
            int size = keyActions.Size();
            if (size > 0)
            {
                int keyCode = e.GetKeyCode();
                for (int i = 0; i < size; i++)
                {
                    Int32 code = (Int32)keyActions.GetKey(i);
                    if (code == keyCode)
                    {
                        ActionKey act = (ActionKey)keyActions.GetValue(code);
                        act.Press();
                    }
                }
            }
            Press(e);
        }

        public abstract void Press(LKey e);

        public override void OnKeyUp(LKey e)
        {
            int size = keyActions.Size();
            if (size > 0)
            {
                int keyCode = e.GetKeyCode();
                for (int i = 0; i < size; i++)
                {
                    Int32 code = (Int32)keyActions.GetKey(i);
                    if (code == keyCode)
                    {
                        ActionKey act = (ActionKey)keyActions.GetValue(code);
                        act.Release();
                    }
                }
            }
            Release(e);
        }

        public abstract void Release(LKey e);

        public abstract void Update(long elapsedTime);

        public override void Dispose()
        {
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
            if (indexTile != null)
            {
                indexTile.Dispose();
                indexTile = null;
            }
            tiles.Clear();
            Close();
        }

        public abstract void Close();

    }
}
