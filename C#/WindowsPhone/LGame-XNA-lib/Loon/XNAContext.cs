using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon
{
    public class XNAContext
    {
        internal Microsoft.Xna.Framework.Content.ContentManager GameContent;

        public Microsoft.Xna.Framework.Content.ContentManager GameRes
        {
            get{
                return GameContent;
            }
        }

        internal int _width;

        internal  int _height;

        public XNAContext(int w, int h)
        {
            this._width = w;
            this._height = h;
        }

        public virtual int GetWidth()
        {
            return _width;
        }

        public virtual int GetHeight()
        {
            return _height;
        }

    }
}
