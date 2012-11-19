using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Input
{
    public interface LFlickerListener
    {
        void TouchSingleTap(float x, float y, float rawX, float rawY);

        void TouchScroll(float x, float y, float rawX, float rawY);

        void TouchFlick(float x, float y, float rawX, float rawY, int direction);

        void TouchDoubleTap(float x, float y, float rawX, float rawY);
    }
}
