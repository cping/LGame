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
 * @email£ºjavachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
namespace Loon.Action.Collision
{

    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Utils;

    public interface GravityUpdate
    {
        void Action(Gravity g, float x, float y);
    }

    public class GravityHandler : LRelease
    {


        private GravityUpdate listener;

        private int width, height;

        private int bindWidth;

        private int bindHeight;

        private float bindX;

        private float bindY;

        private float velocityX, velocityY;

        internal bool isBounded;

        internal bool isListener;

        internal bool isEnabled;

        internal Gravity[] lazyObjects;

        internal List<Gravity> objects;

        internal List<Gravity> pendingAdd;

        internal List<Gravity> pendingRemove;

        public GravityHandler()
            : this(LSystem.screenRect.width, LSystem.screenRect.height)
        {

        }

        public GravityHandler(int w, int h)
        {
            this.SetLimit(w, h);
            this.objects = new List<Gravity>(10);
            this.pendingAdd = new List<Gravity>(10);
            this.pendingRemove = new List<Gravity>(10);
            this.lazyObjects = new Gravity[] { };
            this.isEnabled = true;
        }

        public bool IsGravityRunning()
        {
            if (objects != null)
            {
                foreach (Gravity g in objects)
                {
                    if (g != null && !g.enabled)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public void SetLimit(int w, int h)
        {
            this.width = w;
            this.height = h;
        }

        public void Update(long elapsedTime)
        {
            if (!isEnabled)
            {
                return;
            }
            Commits();
            float second = elapsedTime / 1000f;
            foreach (Gravity g in lazyObjects)
            {
                if (g.enabled && g.bind != null)
                {

                    float accelerationX = g.accelerationX;
                    float accelerationY = g.accelerationY;
                    float angularVelocity = g.angularVelocity;

                    bindWidth = g.bind.GetWidth();
                    bindHeight = g.bind.GetHeight();
                    bindX = g.bind.GetX();
                    bindY = g.bind.GetY();

                    if (angularVelocity != 0)
                    {

                        float rotate = g.bind.GetRotation() + angularVelocity
                                * second;
                        int[] newObjectRect = Loon.Utils.NumberUtils.GetLimit(bindX, bindY,
                                bindWidth, bindHeight, rotate);

                        bindWidth = newObjectRect[2];
                        bindHeight = newObjectRect[3];

                        newObjectRect = null;

                        g.bind.CallRotation(rotate);
                    }

                    if (accelerationX != 0 || accelerationY != 0)
                    {
                        g.velocityX += accelerationX * second;
                        g.velocityY += accelerationY * second;
                    }

                    velocityX = g.velocityX;
                    velocityY = g.velocityY;
                    if (velocityX != 0 || velocityY != 0)
                    {
                        velocityX = bindX + velocityX * second;
                        velocityY = bindY + velocityY * second;
                        if (isBounded)
                        {
                            if (g.g != 0)
                            {
                                velocityY += g.gadd;
                                g.gadd += g.g;
                            }
                            if (g.bounce != 0)
                            {
                                int limitWidth = width - bindWidth;
                                int limitHeight = height - bindHeight;
                                bool chageWidth = bindX >= limitWidth;
                                bool chageHeight = bindY >= limitHeight;
                                if (chageWidth)
                                {
                                    bindX -= g.bounce + g.g;
                                    if (g.bounce > 0)
                                    {
                                        g.bounce -= (g.bounce / MathUtils.Random(
                                                1, 5)) + second;
                                    }
                                    else if (g.bounce < 0)
                                    {
                                        g.bounce = 0;
                                        bindX = limitWidth;
                                    }
                                }
                                if (chageHeight)
                                {
                                    bindY -= g.bounce + g.g;
                                    if (g.bounce > 0)
                                    {
                                        g.bounce -= (g.bounce / MathUtils.Random(
                                                1, 5)) + second;
                                    }
                                    else if (g.bounce < 0)
                                    {
                                        g.bounce = 0;
                                        bindY = limitHeight;
                                    }
                                }
                                if (chageWidth || chageHeight)
                                {
                                    g.bind.CallPos(bindX, bindY);
                                    if (isListener)
                                    {
                                        listener.Action(g, bindX, bindY);
                                    }
                                    return;
                                }
                            }
                            velocityX = LimitValue(velocityX, width - bindWidth);
                            velocityY = LimitValue(velocityY, height - bindHeight);
                        }
                        g.bind.CallPos(velocityX, velocityY);
                        if (isListener)
                        {
                            listener.Action(g, velocityX, velocityY);
                        }
                    }
                }
            }
        }

        private float LimitValue(float value_ren, float limit)
        {
            if (value_ren < 0)
            {
                value_ren = 0;
            }
            if (limit < value_ren)
            {
                value_ren = limit;
            }
            return value_ren;
        }

        public void Commits()
        {
            bool changes = false;
            int additionCount = pendingAdd.Count;
            if (additionCount > 0)
            {
                for (int i = 0; i < additionCount; i++)
                {
                    Gravity obj0 = (Gravity)pendingAdd[i];
                    CollectionUtils.Add(objects, obj0);
                }
                CollectionUtils.Clear(pendingAdd);
                changes = true;
            }
            int removalCount = pendingRemove.Count;
            if (removalCount > 0)
            {
                for (int i_0 = 0; i_0 < removalCount; i_0++)
                {
                    Gravity object_1 = (Gravity)pendingRemove[i_0];
                    CollectionUtils.Remove(objects, object_1);
                }
                CollectionUtils.Clear(pendingRemove);
                changes = true;
            }
            if (changes)
            {
                lazyObjects = objects.ToArray();
            }
        }

        public Gravity[] GetObjects()
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

        public Gravity Get(int index)
        {
            return lazyObjects[index];
        }

        public Gravity Add(object o, float vx, float vy)
        {
            return Add(o, vx, vy, 0);
        }

        public Gravity Add(object o, float vx, float vy, float ave)
        {
            return Add(o, vx, vy, 0, 0, ave);
        }

        public Gravity Add(object o, float vx, float vy, float ax, float ay,
                float ave)
        {
            Gravity g = new Gravity(o);
            g.velocityX = vx;
            g.velocityY = vy;
            g.accelerationX = ax;
            g.accelerationY = ay;
            g.angularVelocity = ave;
            Add(g);
            return g;
        }

        public void Add(Gravity obj0)
        {
            CollectionUtils.Add(pendingAdd, obj0);
        }

        public void Remove(Gravity obj0)
        {
            CollectionUtils.Add(pendingRemove, obj0);
        }

        public void RemoveAll()
        {
            int count = objects.Count;
            for (int i = 0; i < count; i++)
            {
                CollectionUtils.Add(pendingRemove, objects[i]);
            }
            CollectionUtils.Clear(pendingAdd);
        }

        public Gravity GetObject(string name)
        {
            Commits();
            foreach (Gravity obj0 in lazyObjects)
            {
                if (obj0 != null)
                {
                    if (obj0.name != null)
                    {
                        if (obj0.name.Equals(name))
                        {
                            return obj0;
                        }
                    }
                }
            }
            return null;
        }

        public bool IsEnabled()
        {
            return isEnabled;
        }

        public void SetEnabled(bool isEnabled_0)
        {
            this.isEnabled = isEnabled_0;
        }

        public bool IsBounded()
        {
            return isBounded;
        }

        public void SetBounded(bool isBounded_0)
        {
            this.isBounded = isBounded_0;
        }

        public bool IsListener()
        {
            return isListener;
        }

        public void OnUpdate(GravityUpdate listener_0)
        {
            this.listener = listener_0;
            if (listener_0 != null)
            {
                isListener = true;
            }
            else
            {
                isListener = false;
            }
        }

        public virtual void Dispose()
        {
            this.isEnabled = false;
            if (objects != null)
            {
                CollectionUtils.Clear(objects);
                objects = null;
            }
            if (pendingAdd != null)
            {
                CollectionUtils.Clear(pendingAdd);
                pendingAdd = null;
            }
            if (pendingAdd != null)
            {
                CollectionUtils.Clear(pendingAdd);
                pendingAdd = null;
            }
            if (lazyObjects != null)
            {
                foreach (Gravity g in lazyObjects)
                {
                    if (g != null)
                    {
                        g.Dispose();
                    }
                }
            }
        }

    }
}
