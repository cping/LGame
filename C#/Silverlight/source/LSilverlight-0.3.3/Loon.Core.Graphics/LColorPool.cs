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
/// @email javachenpeng@yahoo.com

namespace Loon.Core.Graphics
{

    using System.Collections.Generic;
    using System;
    using Loon.Utils;

    public class LColorPool : LRelease
    {

        private Dictionary<Int32, LColor> colorMap = new Dictionary<Int32, LColor>();

        public LColor GetColor(float r, float g, float b, float a)
        {

            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, r);
            hashCode = LSystem.Unite(hashCode, g);
            hashCode = LSystem.Unite(hashCode, b);
            hashCode = LSystem.Unite(hashCode, a);
            LColor color = (LColor)CollectionUtils.Get(colorMap, hashCode);
            if (color == null)
            {
                color = new LColor(r, g, b, a);
                CollectionUtils.Put(colorMap, hashCode
                    , color);
            }
            return color;
        }

        public LColor GetColor(int c)
        {
            LColor color = (LColor)CollectionUtils.Get(colorMap, c);
            if (color == null)
            {
                color = new LColor((uint)c);
                CollectionUtils.Put(colorMap, c
                  , color);
            }
            return color;
        }

        public LColor GetColor(float r, float g, float b)
        {
            return GetColor(r, g, b, 1f);
        }

        public void Dispose()
        {
            if (colorMap != null)
            {
                colorMap.Clear();
            }
        }
    }
}
