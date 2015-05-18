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
namespace Loon.Action.Map
{
    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Core.Geom;
    using Loon.Action.Map;
    using Loon.Utils;
    using Loon.Java;
    using Loon.Java.Collections;
    using Loon.Action.Map.Heuristics;

    public class AStarFinder : Runnable, LRelease
    {

        public static readonly AStarFindHeuristic ASTAR_CLOSEST = new Closest();

        public static readonly AStarFindHeuristic ASTAR_CLOSEST_SQUARED = new ClosestSquared();

        public static readonly AStarFindHeuristic ASTAR_MANHATTAN = new Manhattan();

        public static readonly AStarFindHeuristic ASTAR_DIAGONAL = new Diagonal();

        public static readonly AStarFindHeuristic ASTAR_EUCLIDEAN = new Euclidean();

        public static readonly AStarFindHeuristic ASTAR_EUCLIDEAN_NOSQR = new EuclideanNoSQR();

        public static readonly AStarFindHeuristic ASTAR_MIXING = new Mixing();

        public static readonly AStarFindHeuristic ASTAR_DIAGONAL_SHORT = new DiagonalShort();

        private static readonly Dictionary<int, List<Vector2f>> finderLazy = new Dictionary<int, List<Vector2f>>(
                100);

        private static int MakeLazyKey(AStarFindHeuristic heuristic,
                int[][] map, int[] limits, int sx, int sy, int ex, int ey,
                bool flag)
        {
            int hashCode = 1;
            int w = map.Length;
            int h = map[0].Length;
            for (int i = 0; i < w; i++)
            {
                for (int j = 0; j < h; j++)
                {
                    hashCode = LSystem.Unite(hashCode, map[i][j]);
                }
            }
            if (limits != null)
            {
                for (int i = 0; i < limits.Length; i++)
                {
                    hashCode = LSystem.Unite(hashCode, limits[i]);
                }
            }
            hashCode = LSystem.Unite(hashCode, heuristic.GetType());
            hashCode = LSystem.Unite(hashCode, sx);
            hashCode = LSystem.Unite(hashCode, sy);
            hashCode = LSystem.Unite(hashCode, ex);
            hashCode = LSystem.Unite(hashCode, ey);
            hashCode = LSystem.Unite(hashCode, flag);
            return hashCode;
        }

        public static List<Vector2f> Find(AStarFindHeuristic heuristic,
                int[][] maps, int[] limits, int x1, int y1, int x2, int y2,
                bool flag)
        {
            heuristic = ((heuristic == null) ? ASTAR_MANHATTAN : heuristic);
            lock (finderLazy)
            {
                if (finderLazy.Count >= LSystem.DEFAULT_MAX_CACHE_SIZE * 10)
                {
                    finderLazy.Clear();
                }
                int key = MakeLazyKey(heuristic, maps, limits, x1, y1, x2, y2, flag);
                List<Vector2f> result = (List<Vector2f>)CollectionUtils.Get(finderLazy, key);
                if (result == null)
                {
                    AStarFinder astar = new AStarFinder(heuristic);
                    Field2D fieldMap = new Field2D(maps);
                    if (limits != null)
                    {
                        fieldMap.SetLimit(limits);
                    }
                    Vector2f start = new Vector2f(x1, y1);
                    Vector2f over = new Vector2f(x2, y2);
                    result = astar.Calc(fieldMap, start, over, flag);
                    CollectionUtils.Put(finderLazy, key, result);
                    astar.Dispose();
                }
                if (result != null)
                {
                    List<Vector2f> newResult = new List<Vector2f>();
                    CollectionUtils.AddAll(newResult, result);
                    result = newResult;
                }
                return result;
            }
        }

        public static List<Vector2f> Find(AStarFindHeuristic heuristic,
                int[][] maps, int x1, int y1, int x2, int y2, bool flag)
        {
            return Find(heuristic, maps, x1, y1, x2, y2, flag);
        }

        public static List<Vector2f> Find(AStarFindHeuristic heuristic,
                Field2D maps, int x1, int y1, int x2, int y2, bool flag)
        {
            return Find(heuristic, maps.GetMap(), maps.GetLimit(), x1, y1, x2, y2,
                    flag);
        }

        public static List<Vector2f> Find(AStarFindHeuristic heuristic,
                Field2D maps, Vector2f start, Vector2f goal, bool flag)
        {
            return Find(heuristic, maps.GetMap(), maps.GetLimit(), start.X(),
                    start.Y(), goal.X(), goal.Y(), flag);
        }

        public static List<Vector2f> Find(AStarFindHeuristic heuristic,
                int[][] maps, Vector2f start, Vector2f goal, bool flag)
        {
            return Find(heuristic, maps, start.X(), start.Y(), goal.X(), goal.Y(),
                    flag);
        }

        private Vector2f goal;

        private List<ScoredPath> pathes;

        private List<Vector2f> path;

        private HashedSet visitedCache;

        private AStarFinder.ScoredPath spath;

        private bool flying, flag;

        private Field2D field;

        private int startX, startY, endX, endY;

        private AStarFinderListener pathFoundListener;

        private AStarFindHeuristic findHeuristic;

        public AStarFinder(AStarFindHeuristic heuristic)
            : this(heuristic, false)
        {

        }

        public AStarFinder(AStarFindHeuristic heuristic, bool flying_0)
        {
            this.flying = flying_0;
            this.findHeuristic = heuristic;
        }

        public AStarFinder(AStarFindHeuristic heuristic, Field2D field_0, int startX_1,
                int startY_2, int endX_3, int endY_4, bool flying_5, bool flag_6,
                AStarFinderListener callback)
        {
            this.field = field_0;
            this.startX = startX_1;
            this.startY = startY_2;
            this.endX = endX_3;
            this.endY = endY_4;
            this.flying = flying_5;
            this.flag = flag_6;
            this.pathFoundListener = callback;
            this.findHeuristic = heuristic;
        }

        public AStarFinder(AStarFindHeuristic heuristic, Field2D field_0, int startX_1,
                int startY_2, int endX_3, int endY_4, bool flying_5, bool flag_6)
            : this(heuristic, field_0, startX_1, startY_2, endX_3, endY_4, flying_5, flag_6, null)
        {

        }

        public void Update(AStarFinder Find)
        {
            this.field = Find.field;
            this.startX = Find.startX;
            this.startY = Find.startY;
            this.endX = Find.endX;
            this.endY = Find.endY;
            this.flying = Find.flying;
            this.flag = Find.flag;
            this.findHeuristic = Find.findHeuristic;
        }

        public override bool Equals(object o)
        {
            if (o is AStarFinder)
            {
                return this.pathFoundListener == ((AStarFinder)o).pathFoundListener;
            }
            return false;
        }

        public override int GetHashCode()
        {
            return JavaRuntime.IdentityHashCode(this);
        }

        public List<Vector2f> FindPath()
        {
            Vector2f start = new Vector2f(startX, startY);
            Vector2f over = new Vector2f(endX, endY);
            return Calc(field, start, over, flag);
        }

        private List<Vector2f> Calc(Field2D field_0, Vector2f start,
                Vector2f goal_1, bool flag_2)
        {
            if (start.Equals(goal_1))
            {
                List<Vector2f> v = new List<Vector2f>();
                CollectionUtils.Add(v, start);
                return v;
            }
            this.goal = goal_1;
            if (visitedCache == null)
            {
                visitedCache = new HashedSet();
            }
            else
            {
                CollectionUtils.Clear(visitedCache);
            }
            if (pathes == null)
            {
                pathes = new List<ScoredPath>();
            }
            else
            {
                CollectionUtils.Clear(pathes);
            }
            CollectionUtils.Add(visitedCache, start);
            if (path == null)
            {
                path = new List<Vector2f>();
            }
            else
            {
                CollectionUtils.Clear(path);
            }
            CollectionUtils.Add(path, start);
            if (spath == null)
            {
                spath = new AStarFinder.ScoredPath(0, path);
            }
            else
            {
                spath.score = 0;
                spath.path = path;
            }
            CollectionUtils.Add(pathes, spath);
            return Astar(field_0, flag_2);
        }

        private List<Vector2f> Astar(Field2D field_0, bool flag_1)
        {
            for (; pathes.Count > 0; )
            {
                AStarFinder.ScoredPath spath_2 = (AStarFinder.ScoredPath)CollectionUtils.RemoveAt(pathes, 0);
                Vector2f current = spath_2.path[spath_2.path.Count - 1];
                if (current.Equals(goal))
                {
                    return spath_2.path;
                }
                List<Vector2f> list = field_0.Neighbors(current, flag_1);
                int size = list.Count;
                for (int i = 0; i < size; i++)
                {
                    Vector2f next = list[i];
                    if (CollectionUtils.Contains(next, visitedCache))
                    {
                        continue;
                    }
                    CollectionUtils.Add(visitedCache, next);
                    if (!field_0.IsHit(next) && !flying)
                    {
                        continue;
                    }
                    List<Vector2f> path_3 = new List<Vector2f>(spath_2.path);
                    CollectionUtils.Add(path_3, next);
                    float score = spath_2.score
                            + findHeuristic
                                    .GetScore(goal.x, goal.y, next.x, next.y);
                    Insert(score, path_3);
                }
            }
            return null;
        }

        private void Insert(float score, List<Vector2f> path_0)
        {
            int size = pathes.Count;
            for (int i = 0; i < size; i += 1)
            {
                AStarFinder.ScoredPath spath_1 = pathes[i];
                if (spath_1.score >= score)
                {
                    pathes.Insert(i, new AStarFinder.ScoredPath(score, path_0));
                    return;
                }
            }
            CollectionUtils.Add(pathes, new AStarFinder.ScoredPath(score, path_0));
        }

        public int GetStartX()
        {
            return startX;
        }

        public int GetStartY()
        {
            return startY;
        }

        public int GetEndX()
        {
            return endX;
        }

        public int GetEndY()
        {
            return endY;
        }

        public bool IsFlying()
        {
            return flying;
        }

        public virtual void Run()
        {
            if (pathFoundListener != null)
            {
                pathFoundListener.PathFound(FindPath());
            }
        }

        private class ScoredPath
        {

            internal float score;

            internal List<Vector2f> path;

            internal ScoredPath(float score_0, List<Vector2f> path_1)
            {
                this.score = score_0;
                this.path = path_1;
            }

        }

        public virtual void Dispose()
        {
            if (path == null)
            {
                CollectionUtils.Clear(path);
                path = null;
            }
            if (pathes != null)
            {
                CollectionUtils.Clear(pathes);
                pathes = null;
            }
            if (visitedCache != null)
            {
                CollectionUtils.Clear(visitedCache);
                visitedCache = null;
            }
            spath = null;
            goal = null;
        }
    }
}
