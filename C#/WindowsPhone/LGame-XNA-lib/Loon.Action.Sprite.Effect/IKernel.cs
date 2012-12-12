using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core;
using Loon.Core.Graphics.Opengl;

namespace Loon.Action.Sprite.Effect
{
    public interface IKernel : LRelease
    {
        int Id();

        void Draw(GLEx g);

        void Update();

        LTexture Get();

        float GetHeight();

        float GetWidth();

    }
}
