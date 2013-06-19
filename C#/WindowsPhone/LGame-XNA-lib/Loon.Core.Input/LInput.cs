
using Loon.Core.Geom;
using Loon.Core.Graphics;
namespace Loon.Core.Input
{
    public interface LInput
    {

         void SetKeyDown(int code);

         void SetKeyUp(int code);

         bool IsMoving();

         int GetRepaintMode();

         void SetRepaintMode(int mode);

         Loon.Core.Geom.Point.Point2i GetTouch();

         int GetWidth();

         int GetHeight();

         void Refresh();

         int GetTouchX();

         int GetTouchY();

         int GetTouchDX();

         int GetTouchDY();

         int GetTouchReleased();

         bool IsTouchReleased(int i);

         int GetTouchPressed();

         bool IsTouchPressed(int i);

         bool IsTouchType(int i);

         int GetKeyReleased();

         bool IsKeyReleased(int i);

         int GetKeyPressed();

         bool IsKeyPressed(int i);

         bool IsKeyType(int i);
    }
}
