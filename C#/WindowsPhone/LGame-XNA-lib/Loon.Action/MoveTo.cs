namespace Loon.Action
{
    using System;
    using System.Collections.Generic;
    using Loon.Action.Map;
    using Loon.Core.Geom;
    using Loon.Utils;
    using Loon.Core.Graphics.Component;
    using Loon.Utils.Debug;

    public class MoveTo : ActionEvent
    {

        private static readonly Dictionary<Int32, List<Vector2f>> pathCache = new Dictionary<Int32, List<Vector2f>>(
                Loon.Core.LSystem.DEFAULT_MAX_CACHE_SIZE);

        private Vector2f startLocation, endLocation;

        private Field2D layerMap;

        private bool flag, useCache, synchroLayerField;

        private List<Vector2f> pActorPath;

        private int startX, startY, endX, endY, moveX, moveY;

        private int direction, speed;

        private AStarFindHeuristic heuristic;

        private Vector2f pLocation;

        public MoveTo(Field2D map, int x, int y, bool flag_0)
        {
            this.pLocation = new Vector2f();
            this.startLocation = new Vector2f();
            this.endLocation = new Vector2f(x, y);
            this.layerMap = map;
            this.flag = flag_0;
            this.speed = 4;
            this.useCache = true;
            this.synchroLayerField = false;
        }

        public MoveTo(Field2D map, Vector2f pos, bool flag_0)
            : this(map, pos.X(), pos.Y(), flag_0)
        {

        }

        public void RandomPathFinder()
        {
            lock (typeof(MoveTo))
            {
                AStarFindHeuristic afh = null;
                int index = Loon.Utils.MathUtils.Random(AStarFindHeuristic.MANHATTAN,
                        AStarFindHeuristic.CLOSEST_SQUARED);
                switch (index)
                {
                    case AStarFindHeuristic.MANHATTAN:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_EUCLIDEAN;
                        break;
                    case AStarFindHeuristic.MIXING:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_MIXING;
                        break;
                    case AStarFindHeuristic.DIAGONAL:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_DIAGONAL;
                        break;
                    case AStarFindHeuristic.DIAGONAL_SHORT:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_DIAGONAL_SHORT;
                        break;
                    case AStarFindHeuristic.EUCLIDEAN:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_EUCLIDEAN;
                        break;
                    case AStarFindHeuristic.EUCLIDEAN_NOSQR:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_EUCLIDEAN_NOSQR;
                        break;
                    case AStarFindHeuristic.CLOSEST:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_CLOSEST;
                        break;
                    case AStarFindHeuristic.CLOSEST_SQUARED:
                        afh = Loon.Action.Map.AStarFinder.ASTAR_CLOSEST_SQUARED;
                        break;
                }
                SetHeuristic(afh);
            }
        }

        public float[] GetBeginPath()
        {
            return new float[] { startX, startY };
        }

        public float[] GetEndPath()
        {
            return new float[] { endX, endY };
        }

        public override void OnLoad()
        {
            if (layerMap == null || original == null)
            {
                return;
            }
            if (!original.GetRectBox().Contains(endLocation.X(), endLocation.Y()))
            {
                if (useCache)
                {
                    lock (pathCache)
                    {
                        if (pathCache.Count > Loon.Core.LSystem.DEFAULT_MAX_CACHE_SIZE * 10)
                        {
                            pathCache.Clear();
                        }
                        Int32 key = GetHashCode();
                        List<Vector2f> final_path = (List<Vector2f>)CollectionUtils.Get(pathCache, key);
                        if (final_path == null)
                        {
                            final_path = AStarFinder.Find(heuristic,
                                            layerMap,
                                            layerMap.PixelsToTilesWidth(startLocation
                                                    .X()),
                                            layerMap.PixelsToTilesHeight(startLocation
                                                    .Y()),
                                            layerMap.PixelsToTilesWidth(endLocation
                                                    .X()),
                                            layerMap.PixelsToTilesHeight(endLocation
                                                    .Y()), flag);
                            CollectionUtils.Put(pathCache, key, final_path);
                        }
                        pActorPath = new List<Vector2f>();
                        CollectionUtils.AddAll(final_path, pActorPath);
                    }
                }
                else
                {
                    pActorPath = Loon.Action.Map.AStarFinder.Find(heuristic, layerMap,
                            layerMap.PixelsToTilesWidth(startLocation.X()),
                            layerMap.PixelsToTilesHeight(startLocation.Y()),
                            layerMap.PixelsToTilesWidth(endLocation.X()),
                            layerMap.PixelsToTilesHeight(endLocation.Y()), flag);

                }
            }
        }

        public void ClearPath()
        {
            if (pActorPath != null)
            {
                lock (pActorPath)
                {
                    CollectionUtils.Clear(pActorPath);
                    pActorPath = null;
                }
            }
        }

        public static void ClearPathCache()
        {
            if (pathCache != null)
            {
                lock (pathCache)
                {
                    pathCache.Clear();
                }
            }
        }

        public override int GetHashCode()
        {
            if (layerMap == null || original == null)
            {
                return base.GetHashCode();
            }
            int hashCode = 1;
            hashCode = Loon.Core.LSystem.Unite(hashCode, flag);
            hashCode = Loon.Core.LSystem.Unite(hashCode,
                    layerMap.PixelsToTilesWidth(original.X()));
            hashCode = Loon.Core.LSystem.Unite(hashCode,
                    layerMap.PixelsToTilesHeight(original.Y()));
            hashCode = Loon.Core.LSystem.Unite(hashCode,
                    layerMap.PixelsToTilesWidth(endLocation.X()));
            hashCode = Loon.Core.LSystem.Unite(hashCode,
                    layerMap.PixelsToTilesHeight(endLocation.Y()));
            hashCode = Loon.Core.LSystem.Unite(hashCode, layerMap.GetWidth());
            hashCode = Loon.Core.LSystem.Unite(hashCode, layerMap.GetHeight());
            hashCode = Loon.Core.LSystem.Unite(hashCode, layerMap.GetTileWidth());
            hashCode = Loon.Core.LSystem.Unite(hashCode, layerMap.GetTileHeight());
            hashCode = Loon.Core.LSystem.Unite(hashCode,
                    Loon.Utils.CollectionUtils.HashCode(layerMap.GetMap()));
            return hashCode;
        }

        public override void Start(ActionBind target)
        {
            base.Start(target);
            startLocation.Set(target.GetX(), target.GetY());
        }

        public List<Vector2f> GetPath()
        {
            return pActorPath;
        }

        public int GetDirection()
        {
            return direction;
        }

        public void SetField2D(Field2D field)
        {
            if (field != null)
            {
                this.layerMap = field;
            }
        }

        public Field2D GetField2D()
        {
            return layerMap;
        }

        public override void Update(long elapsedTime)
        {
            if (layerMap == null || original == null || pActorPath == null)
            {
                return;
            }
            lock (pActorPath)
            {
                if (synchroLayerField)
                {
                    if (original != null)
                    {
                        Field2D field = original.GetField2D();
                        if (field != null && layerMap != field)
                        {
                            this.layerMap = field;
                        }
                    }
                }
                if (endX == startX && endY == startY)
                {
                    if (pActorPath.Count > 1)
                    {
                        Vector2f moveStart = (Vector2f)pActorPath[0];
                        Vector2f moveEnd = (Vector2f)pActorPath[1];
                        startX = layerMap.TilesToWidthPixels(moveStart.X());
                        startY = layerMap.TilesToHeightPixels(moveStart.Y());
                        endX = moveEnd.X() * layerMap.GetTileWidth();
                        endY = moveEnd.Y() * layerMap.GetTileHeight();
                        moveX = moveEnd.X() - moveStart.X();
                        moveY = moveEnd.Y() - moveStart.Y();
                        if (moveX > -2 && moveY > -2 && moveX < 2 && moveY < 2)
                        {
                            direction = Loon.Action.Map.Field2D.GetDirection(moveX, moveY,
                                    direction);
                        }
                    }
                    CollectionUtils.RemoveAt(pActorPath, 0);
                }
                switch (direction)
                {
                    case Config.TUP:
                        startY -= speed;
                        if (startY < endY)
                        {
                            startY = endY;
                        }
                        break;
                    case Config.TDOWN:
                        startY += speed;
                        if (startY > endY)
                        {
                            startY = endY;
                        }
                        break;
                    case Config.TLEFT:
                        startX -= speed;
                        if (startX < endX)
                        {
                            startX = endX;
                        }
                        break;
                    case Config.TRIGHT:
                        startX += speed;
                        if (startX > endX)
                        {
                            startX = endX;
                        }
                        break;
                    case Config.UP:
                        startX += speed;
                        startY -= speed;
                        if (startX > endX)
                        {
                            startX = endX;
                        }
                        if (startY < endY)
                        {
                            startY = endY;
                        }
                        break;
                    case Config.DOWN:
                        startX -= speed;
                        startY += speed;
                        if (startX < endX)
                        {
                            startX = endX;
                        }
                        if (startY > endY)
                        {
                            startY = endY;
                        }
                        break;
                    case Config.LEFT:
                        startX -= speed;
                        startY -= speed;
                        if (startX < endX)
                        {
                            startX = endX;
                        }
                        if (startY < endY)
                        {
                            startY = endY;
                        }
                        break;
                    case Config.RIGHT:
                        startX += speed;
                        startY += speed;
                        if (startX > endX)
                        {
                            startX = endX;
                        }
                        if (startY > endY)
                        {
                            startY = endY;
                        }
                        break;
                }
                lock (original)
                {
                    original.SetLocation(startX + offsetX, startY + offsetY);
                }
            }
        }

        public Vector2f NextPos()
        {
            if (pActorPath != null)
            {
                lock (pActorPath)
                {
                    int size = pActorPath.Count;
                    if (size > 0)
                    {
                        pLocation.Set(endX, endY);
                    }
                    else
                    {
                        pLocation.Set(original.GetX(), original.GetY());
                    }
                    return pLocation;
                }
            }
            else
            {
                pLocation.Set(original.GetX(), original.GetY());
                return pLocation;
            }
        }

        public int GetSpeed()
        {
            return speed;
        }

        public void SetSpeed(int speed_0)
        {
            this.speed = speed_0;
        }

        public override bool IsComplete()
        {
            return pActorPath == null || pActorPath.Count == 0 || isComplete
                    || original == null;
        }

        public bool IsUseCache()
        {
            return useCache;
        }

        public void SetUseCache(bool useCache_0)
        {
            this.useCache = useCache_0;
        }

        public bool IsSynchroLayerField()
        {
            return synchroLayerField;
        }

        public void SetSynchroLayerField(bool syn)
        {
            this.synchroLayerField = syn;
        }

        public AStarFindHeuristic GetHeuristic()
        {
            return heuristic;
        }

        public void SetHeuristic(AStarFindHeuristic h)
        {
            this.heuristic = h;
        }

    }
}
