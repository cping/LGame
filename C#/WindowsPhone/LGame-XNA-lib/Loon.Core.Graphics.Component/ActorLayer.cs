namespace Loon.Core.Graphics.Component
{
    using System;
    using System.Collections;
    using Loon.Action.Map;
    using Loon.Core.Input;
    using Loon.Java.Collections;
    using Loon.Core.Geom;
    using Loon.Utils;
    using Loon.Action.Sprite.Effect;
    using Loon.Action;

    public abstract class ActorLayer : LContainer
    {

        private Field2D tmpField;

        private bool isBounded;

        protected internal int cellSize;

        internal CollisionChecker collisionChecker;

        internal ActorTreeSet objects;

        internal long elapsedTime;

        public ActorLayer(int x, int y, int layerWidth, int layerHeight,
                int s)
            : this(x, y, layerWidth, layerHeight, s, true)
        {

        }

        public ActorLayer(int x, int y, int layerWidth, int layerHeight,
                int s, bool bounded)
            : base(x, y, layerWidth, layerHeight)
        {

            this.collisionChecker = new CollisionManager();
            this.objects = new ActorTreeSet();
            this.cellSize = s;
            this.Initialize(layerWidth, layerHeight, s);
            this.isBounded = bounded;
        }

        private void Initialize(int width, int height, int s)
        {
            this.cellSize = s;
            this.collisionChecker.Initialize(s);
        }

        public LInput ScreenInput()
        {
            return input;
        }

        public int GetCellSize()
        {
            return this.cellSize;
        }

        /// <summary>
        /// 添加一个独立事件，并选择是否暂不启动
        /// </summary>
        ///
        /// <param name="action"></param>
        /// <param name="obj"></param>
        /// <param name="paused"></param>
        public void AddActionEvent(ActionEvent action, Loon.Action.ActionBind obj, bool paused)
        {
            ActionControl.GetInstance().AddAction(action, obj, paused);
        }

        /// <summary>
        /// 添加一个独立事件
        /// </summary>
        ///
        /// <param name="action"></param>
        /// <param name="obj"></param>
        public void AddActionEvent(ActionEvent action, Loon.Action.ActionBind obj)
        {
            ActionControl.GetInstance().AddAction(action, obj);
        }

        /// <summary>
        /// 删除所有和指定对象有关的独立事件
        /// </summary>
        ///
        /// <param name="actObject"></param>
        public void RemoveActionEvents(Loon.Action.ActionBind actObject)
        {
            ActionControl.GetInstance().RemoveAllActions(actObject);
        }

        /// <summary>
        /// 获得当前独立事件总数
        /// </summary>
        ///
        /// <returns></returns>
        public int GetActionEventCount()
        {
            return ActionControl.GetInstance().GetCount();
        }

        /// <summary>
        /// 删除指定的独立事件
        /// </summary>
        ///
        /// <param name="tag"></param>
        /// <param name="actObject"></param>
        public void RemoveActionEvent(object tag, Loon.Action.ActionBind actObject)
        {
            ActionControl.GetInstance().RemoveAction(tag, actObject);
        }

        /// <summary>
        /// 删除指定的独立事件
        /// </summary>
        ///
        /// <param name="action"></param>
        public void RemoveActionEvent(ActionEvent action)
        {
            ActionControl.GetInstance().RemoveAction(action);
        }

        /// <summary>
        /// 获得制定的独立事件
        /// </summary>
        ///
        /// <param name="tag"></param>
        /// <param name="actObject"></param>
        /// <returns></returns>
        public ActionEvent GetActionEvent(object tag, Loon.Action.ActionBind actObject)
        {
            return ActionControl.GetInstance().GetAction(tag, actObject);
        }

        /// <summary>
        /// 停止对象对应的自动事件
        /// </summary>
        ///
        /// <param name="actObject"></param>
        public void StopActionEvent(Loon.Action.ActionBind actObject)
        {
            ActionControl.GetInstance().Stop(actObject);
        }

        /// <summary>
        /// 设定指定角色暂停状态
        /// </summary>
        ///
        /// <param name="pause"></param>
        /// <param name="actObject"></param>
        public void PauseActionEvent(bool pause, Loon.Action.ActionBind actObject)
        {
            ActionControl.GetInstance().Paused(pause, actObject);
        }

        /// <summary>
        /// 设置是否暂停自动事件运行
        /// </summary>
        ///
        /// <param name="pause"></param>
        public void PauseActionEvent(bool pause)
        {
            ActionControl.GetInstance().SetPause(pause);
        }

        /// <summary>
        /// 获得是否暂停了独立事件运行
        /// </summary>
        ///
        /// <returns></returns>
        public bool IsPauseActionEvent()
        {
            return ActionControl.GetInstance().IsPause();
        }

        /// <summary>
        /// 启动指定对象对应的对立事件
        /// </summary>
        ///
        /// <param name="actObject"></param>
        public void StartActionEvent(Loon.Action.ActionBind actObject)
        {
            ActionControl.GetInstance().Start(actObject);
        }

        /// <summary>
        /// 停止独立事件运行用线程
        /// </summary>
        ///
        public void StopActionEvent()
        {
            ActionControl.GetInstance().Stop();
        }

        /// <summary>
        /// 让指定对象执行MoveTo事件
        /// </summary>
        ///
        /// <param name="field"></param>
        /// <param name="o"></param>
        /// <param name="flag"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public MoveTo CallMoveTo(Field2D field, Loon.Action.ActionBind o, bool flag, int x, int y)
        {
            if (isClose)
            {
                return null;
            }
            MoveTo move = new MoveTo(field, x, y, flag);
            AddActionEvent(move, o);
            return move;
        }

        /// <summary>
        /// 让指定对象执行MoveTo事件
        /// </summary>
        ///
        /// <param name="field"></param>
        /// <param name="o"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public MoveTo CallMoveTo(Field2D field, Loon.Action.ActionBind o, int x, int y)
        {
            return CallMoveTo(field, o, true, x, y);
        }

        /// <summary>
        /// 让指定对象执行MoveTo事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="flag"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="w"></param>
        /// <param name="h"></param>
        /// <returns></returns>
        public MoveTo CallMoveTo(Loon.Action.ActionBind o, bool flag, int x, int y, int w, int h)
        {
            if (isClose)
            {
                return null;
            }
            if (tmpField == null)
            {
                tmpField = CreateArrayMap(w, h);
            }
            MoveTo move = new MoveTo(tmpField, x, y, flag);
            AddActionEvent(move, o);
            return move;
        }

        /// <summary>
        /// 让指定对象执行MoveTo事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="w"></param>
        /// <param name="h"></param>
        /// <returns></returns>
        public MoveTo CallMoveTo(Loon.Action.ActionBind o, int x, int y, int w, int h)
        {
            return CallMoveTo(o, true, x, y, w, h);
        }

        /// <summary>
        /// 让指定对象执行MoveTo事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public MoveTo CallMoveTo(Loon.Action.ActionBind o, int x, int y)
        {
            return CallMoveTo(o, x, y, 32, 32);
        }

        /// <summary>
        /// 让指定对象执行MoveTo事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="flag"></param>
        /// <returns></returns>
        public MoveTo CallMoveTo(Loon.Action.ActionBind o, int x, int y, bool flag)
        {
            return CallMoveTo(o, flag, x, y, 32, 32);
        }

        /// <summary>
        /// 让指定对象执行FadeTo事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="type"></param>
        /// <param name="speed"></param>
        /// <returns></returns>
        public FadeTo CallFadeTo(Loon.Action.ActionBind o, int type, int speed)
        {
            if (isClose)
            {
                return null;
            }
            FadeTo fade = new FadeTo(type, speed);
            AddActionEvent(fade, o);
            return fade;
        }

        /// <summary>
        /// 让指定对象执行FadeTo淡入事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="speed"></param>
        /// <returns></returns>
        public FadeTo CallFadeInTo(Loon.Action.ActionBind o, int speed)
        {
            return CallFadeTo(o, Loon.Action.Sprite.ISprite_Constants.TYPE_FADE_IN, speed);
        }

        /// <summary>
        /// 让指定对象执行FadeTo淡出事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="speed"></param>
        /// <returns></returns>
        public FadeTo CallFadeOutTo(Loon.Action.ActionBind o, int speed)
        {
            return CallFadeTo(o, Loon.Action.Sprite.ISprite_Constants.TYPE_FADE_OUT, speed);
        }

        /// <summary>
        /// 让指定对象执行RotateTo旋转事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="angle"></param>
        /// <param name="speed"></param>
        /// <returns></returns>
        public RotateTo CallRotateTo(Loon.Action.ActionBind o, float angle, float speed)
        {
            if (isClose)
            {
                return null;
            }
            RotateTo rotate = new RotateTo(angle, speed);
            AddActionEvent(rotate, o);
            return rotate;
        }

        /// <summary>
        /// 让指定对象执行JumpTo跳跃事件
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="j"></param>
        /// <param name="g"></param>
        /// <returns></returns>
        public JumpTo CallJumpTo(Loon.Action.ActionBind o, int j, float g)
        {
            if (isClose)
            {
                return null;
            }
            JumpTo jump = new JumpTo(j, g);
            AddActionEvent(jump, o);
            return jump;
        }

        /// <summary>
        /// 让指定角色根据指定半径以指定速度循环转动
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="radius"></param>
        /// <param name="velocity"></param>
        /// <returns></returns>
        public CircleTo CallCircleTo(Loon.Action.ActionBind o, int radius, int velocity)
        {
            if (isClose)
            {
                return null;
            }
            CircleTo circle = new CircleTo(radius, velocity);
            AddActionEvent(circle, o);
            return circle;
        }

        /// <summary>
        /// 向指定坐标以指定速度让指定角色做为子弹发射
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="speed"></param>
        /// <returns></returns>
        public FireTo CallFireTo(Loon.Action.ActionBind o, int x, int y, double speed)
        {
            if (isClose)
            {
                return null;
            }
            FireTo fire = new FireTo(x, y, speed);
            AddActionEvent(fire, o);
            return fire;
        }

        /// <summary>
        /// 让角色缩放指定大小
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="sx"></param>
        /// <param name="sy"></param>
        /// <returns></returns>
        public ScaleTo CallScaleTo(Loon.Action.ActionBind o, float sx, float sy)
        {
            if (isClose)
            {
                return null;
            }
            ScaleTo scale = new ScaleTo(sx, sy);
            AddActionEvent(scale, o);
            return scale;
        }

        /// <summary>
        /// 让角色缩放指定大小
        /// </summary>
        ///
        /// <param name="o"></param>
        /// <param name="s"></param>
        /// <returns></returns>
        public ScaleTo CallScaleTo(Loon.Action.ActionBind o, float s)
        {
            return CallScaleTo(o, s, s);
        }

        /// <summary>
        /// 让指定角色做箭状发射(抛物线)
        /// </summary>
        /// <param name="o"></param>
        /// <param name="tx"></param>
        /// <param name="ty"></param>
        /// <returns></returns>
        public ArrowTo CallArrowTo(Loon.Action.ActionBind o, float tx, float ty)
        {
            if (isClose)
            {
                return null;
            }
            ArrowTo arrow = new ArrowTo(tx, ty);
            AddActionEvent(arrow, o);
            return arrow;
        }

        /// <summary>
        /// 以指定瓦片大小创建数组地图
        /// </summary>
        ///
        /// <param name="tileWidth"></param>
        /// <param name="tileHeight"></param>
        /// <returns></returns>
        public Field2D CreateArrayMap(int tileWidth, int tileHeight)
        {
            if (isClose)
            {
                return null;
            }
            tmpField = new Field2D((int[][])CollectionUtils.XNA_CreateJaggedArray(typeof(int), GetHeight() / tileHeight, GetWidth()
                            / tileWidth), tileWidth, tileHeight);
            return tmpField;
        }

        /// <summary>
        /// 设定Layer对应的二维数组地图
        /// </summary>
        ///
        /// <param name="map"></param>
        public void SetField2D(Field2D field)
        {
            if (isClose)
            {
                return;
            }
            if (field == null)
            {
                return;
            }
            if (tmpField != null)
            {
                if ((field.GetMap().Length == tmpField.GetMap().Length)
                        && (field.GetTileWidth() == tmpField.GetTileWidth())
                        && (field.GetTileHeight() == tmpField.GetTileHeight()))
                {
                    tmpField.Set(field.GetMap(), field.GetTileWidth(),
                            field.GetTileHeight());
                }
            }
            else
            {
                tmpField = field;
            }
        }

        /// <summary>
        /// 返回Layer对应的二维数据地图
        /// </summary>
        ///
        /// <param name="map"></param>
        public override Field2D GetField2D()
        {
            return tmpField;
        }

        /// <summary>
        /// 添加角色到Layer(在Layer中添加的角色将自动赋予碰撞检查)
        /// </summary>
        ///
        /// <param name="object"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        public void AddObject(Actor o, float x, float y)
        {
            if (isClose)
            {
                return;
            }
            lock (objects)
            {
                if (this.objects.Add(o))
                {
                    o.AddLayer(x, y, this);
                    this.collisionChecker.AddObject(o);
                    o.AddLayer(this);
                }
            }
        }

        /// <summary>
        /// 添加角色到Layer
        /// </summary>
        ///
        /// <param name="object"></param>
        public void AddObject(Actor o)
        {
            if (isClose)
            {
                return;
            }
            AddObject(o, o.X(), o.Y());
        }

        /// <summary>
        /// 将指定角色于Layer前置
        /// </summary>
        ///
        /// <param name="actor"></param>
        internal void SendToFront(Actor actor)
        {
            if (isClose)
            {
                return;
            }
            if (objects != null)
            {
                lock (objects)
                {
                    if (objects != null)
                    {
                        objects.SendToFront(actor);
                    }
                }
            }
        }

        /// <summary>
        /// 将指定角色于Layer后置
        /// </summary>
        ///
        /// <param name="actor"></param>
        internal void SendToBack(Actor actor)
        {
            if (isClose)
            {
                return;
            }
            if (objects != null)
            {
                lock (objects)
                {
                    if (objects != null)
                    {
                        objects.SendToBack(actor);
                    }
                }
            }
        }

        /// <summary>
        /// 参考指定大小根据Layer范围生成一组不重复的随机坐标
        /// </summary>
        ///
        /// <param name="act"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public RectBox[] GetRandomLayerLocation(int nx, int ny, int nw, int nh,
                int count)
        {
            if (isClose)
            {
                return null;
            }
            if (count <= 0)
            {
                throw new Exception("count <= 0 !");
            }
            int layerWidth = GetWidth();
            int layerHeight = GetHeight();
            int actorWidth = (nw > 48) ? nw : 48;
            int actorHeight = (nh > 48) ? nh : 48;
            int x = nx / actorWidth;
            int y = ny / actorHeight;
            int row = layerWidth / actorWidth;
            int col = layerHeight / actorHeight;
            RectBox[] randoms = new RectBox[count];
            int oldRx = 0, oldRy = 0;
            int index = 0;
            for (int i = 0; i < count * 100; i++)
            {
                if (index >= count)
                {
                    return randoms;
                }
                int rx = LSystem.random.Next(row);
                int ry = LSystem.random.Next(col);
                if (oldRx != rx && oldRy != ry && rx != x && ry != y
                        && rx * actorWidth != nx && ry * actorHeight != ny)
                {
                    bool stop = false;
                    for (int j = 0; j < index; j++)
                    {
                        if (randoms[j].x == rx && randoms[j].y == ry && oldRx != x
                                && oldRy != y && rx * actorWidth != nx
                                && ry * actorHeight != ny)
                        {
                            stop = true;
                            break;
                        }
                    }
                    if (stop)
                    {
                        continue;
                    }
                    randoms[index] = new RectBox(rx * actorWidth, ry * actorHeight,
                            actorWidth, actorHeight);
                    oldRx = rx;
                    oldRy = ry;
                    index++;
                }
            }
            return null;
        }

        /// <summary>
        /// 参考指定大小根据Layer范围生成一组不重复的随机坐标
        /// </summary>
        ///
        /// <param name="actorWidth"></param>
        /// <param name="actorHeight"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public RectBox[] GetRandomLayerLocation(int actorWidth, int actorHeight,
                int count)
        {
            if (isClose)
            {
                return null;
            }
            return GetRandomLayerLocation(0, 0, actorWidth, actorHeight, count);
        }

        /// <summary>
        /// 参考指定角色根据Layer范围生成一组不重复的随机坐标
        /// </summary>
        ///
        /// <param name="actor"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public RectBox[] GetRandomLayerLocation(Actor actor, int count)
        {
            if (isClose)
            {
                return null;
            }
            RectBox rect = actor.GetRectBox();
            return GetRandomLayerLocation((int)rect.x, (int)rect.y, rect.width,
                    rect.height, count);
        }

        /// <summary>
        /// 参考指定Actor大小根据Layer生成一个不重复的随机坐标
        /// </summary>
        ///
        /// <param name="actor"></param>
        /// <returns></returns>
        public RectBox GetRandomLayerLocation(Actor actor)
        {
            if (isClose)
            {
                return null;
            }
            RectBox[] rects = GetRandomLayerLocation(actor, 1);
            if (rects != null)
            {
                return rects[0];
            }
            return null;
        }

        /// <summary>
        /// 删除指定的角色
        /// </summary>
        ///
        /// <param name="object"></param>
        public void RemoveObject(Actor o)
        {
            if (isClose)
            {
                return;
            }
            if (o == null)
            {
                return;
            }
            lock (objects)
            {
                if (this.objects.Remove(o))
                {
                    this.collisionChecker.RemoveObject(o);
                }
                RemoveActionEvents(o);
                o.SetLayer((ActorLayer)null);
            }
        }

        /// <summary>
        /// 删除所有指定的游戏类
        /// </summary>
        ///
        /// <param name="clazz"></param>
        public void RemoveObject(Type clazz)
        {
            if (isClose)
            {
                return;
            }
            lock (objects)
            {
                IIterator it = objects.Iterator();
                while (it.HasNext())
                {
                    Actor actor = (Actor)it.Next();
                    if (actor == null)
                    {
                        continue;
                    }
                    Type cls = actor.GetType();
                    if (clazz == null || clazz == cls || clazz.IsInstanceOfType(actor)
                            || clazz.Equals(cls))
                    {
                        if (this.objects.Remove(actor))
                        {
                            this.collisionChecker.RemoveObject(actor);
                        }
                        RemoveActionEvents(actor);
                        actor.SetLayer((ActorLayer)null);
                    }
                }
            }
        }

        /// <summary>
        /// 删除指定集合中的所有角色
        /// </summary>
        ///
        /// <param name="obj"></param>
        public void RemoveObjects(IList obj)
        {
            if (isClose)
            {
                return;
            }
            lock (obj)
            {
                IIterator iter = new IteratorAdapter(obj.GetEnumerator());
                while (iter.HasNext())
                {
                    Actor actor = (Actor)iter.Next();
                    this.RemoveObject(actor);
                }
            }
        }

        /// <summary>
        /// 获得含有指定角色碰撞的List集合
        /// </summary>
        ///
        /// <param name="actor"></param>
        /// <returns></returns>
        public IList GetCollisionObjects(Actor actor)
        {
            if (isClose)
            {
                return null;
            }
            return GetCollisionObjects(actor.GetType());
        }

        /// <summary>
        /// 刷新缓存数据，重置世界
        /// </summary>
        ///
        public void Reset()
        {
            if (isClose)
            {
                return;
            }
            if (objects != null)
            {
                lock (objects)
                {
                    if (objects != null)
                    {
                        objects.Clear();
                        objects = null;
                    }
                    if (collisionChecker != null)
                    {
                        collisionChecker.Clear();
                        collisionChecker = null;
                    }
                    this.collisionChecker = new CollisionManager();
                    this.objects = new ActorTreeSet();
                }
            }
        }

        /// <summary>
        /// 获得指定类所产生角色碰撞的List集合
        /// </summary>
        ///
        /// <param name="cls"></param>
        /// <returns></returns>
        public IList GetCollisionObjects(Type cls)
        {
            if (isClose)
            {
                return null;
            }
            return this.collisionChecker.GetObjects(cls);
        }

        /// <summary>
        /// 返回指定角色类在指定位置的List集合
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="cls"></param>
        /// <returns></returns>
        public IList GetCollisionObjectsAt(float x, float y, Type cls)
        {
            if (isClose)
            {
                return null;
            }
            return this.collisionChecker.GetObjectsAt(x, y, cls);
        }

        /// <summary>
        /// 返回指定区域对应的单一Actor
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public Actor GetOnlyCollisionObjectsAt(float x, float y)
        {
            if (isClose)
            {
                return null;
            }
            return objects.GetOnlyCollisionObjectsAt(x, y);
        }

        /// <summary>
        /// 返回指定区域和标记对应的单一Actor
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public Actor GetOnlyCollisionObjectsAt(float x, float y, object tag)
        {
            if (isClose)
            {
                return null;
            }
            return objects.GetOnlyCollisionObjectsAt(x, y, tag);
        }

        /// <summary>
        /// 角色对象总数
        /// </summary>
        ///
        /// <returns></returns>
        public int Size()
        {
            if (isClose)
            {
                return 0;
            }
            return this.objects.Size();
        }

        public abstract void Action(long elapsedTime);

        public override bool IsBounded()
        {
            return this.isBounded;
        }

        internal Actor GetSynchronizedObject(float x, float y)
        {
            if (isClose)
            {
                return null;
            }
            return objects.GetSynchronizedObject(x, y);
        }

        internal IList GetIntersectingObjects(Actor actor, Type cls)
        {
            if (isClose)
            {
                return null;
            }
            return this.collisionChecker.GetIntersectingObjects(actor, cls);
        }

        internal Actor GetOnlyIntersectingObject(Actor o, Type cls)
        {
            if (isClose)
            {
                return null;
            }
            return this.collisionChecker.GetOnlyIntersectingObject(o, cls);
        }

        internal IList GetObjectsInRange(float x, float y, float r, Type cls)
        {
            if (isClose)
            {
                return null;
            }
            return this.collisionChecker.GetObjectsInRange(x, y, r, cls);
        }

        internal IList GetNeighbours(Actor actor, float distance, bool d, Type cls)
        {
            if (isClose)
            {
                return null;
            }
            if (distance < 0)
            {
                throw new Exception("distance < 0");
            }
            else
            {
                return this.collisionChecker.GetNeighbours(actor, distance, d, cls);
            }
        }

        internal int GetHeightInPixels()
        {
            return this.GetHeight() * this.cellSize;
        }

        internal int GetWidthInPixels()
        {
            return this.GetWidth() * this.cellSize;
        }

        internal int ToCellCeil(float pixel)
        {
            return MathUtils.Ceil(pixel / this.cellSize);
        }

        internal int ToCellFloor(float pixel)
        {
            return MathUtils.Floor(pixel / this.cellSize);
        }

        internal float GetCellCenter(float c)
        {
            float cellCenter = (c * this.cellSize) + this.cellSize / 2.0f;
            return cellCenter;
        }

        internal IList GetCollisionObjects(float x, float y)
        {
            if (isClose)
            {
                return null;
            }
            return collisionChecker.GetObjectsAt(x, y, null);
        }

        internal void UpdateObjectLocation(Actor o, float oldX, float oldY)
        {
            if (isClose)
            {
                return;
            }
            this.collisionChecker.UpdateObjectLocation(o, oldX, oldY);
        }

        internal void UpdateObjectSize(Actor o)
        {
            if (isClose)
            {
                return;
            }
            this.collisionChecker.UpdateObjectSize(o);
        }

        internal Actor GetOnlyObjectAt(Actor o, float dx, float dy, Type cls)
        {
            if (isClose)
            {
                return null;
            }
            return this.collisionChecker.GetOnlyObjectAt(o, dx, dy, cls);
        }

        internal ActorTreeSet getObjectsListInPaintO()
        {
            if (isClose)
            {
                return null;
            }
            return this.objects;
        }

    }
}
