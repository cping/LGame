using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.OpenGL;

namespace Loon.Core.Graphics.Component
{
    public interface ActorListener
    {
         void Draw(GLEx g);

         void Update(long elapsedTime);

         void DownClick(int x, int y);

         void UpClick(int x, int y);

         void Drag(int x, int y);
    }
}
