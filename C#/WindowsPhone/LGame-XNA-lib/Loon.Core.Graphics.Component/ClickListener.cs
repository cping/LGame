namespace Loon.Core.Graphics.Component
{
    public interface ClickListener
    {
        void DoClick(LComponent comp);

        void DownClick(LComponent comp, float x, float y);

        void UpClick(LComponent comp, float x, float y);

        void DragClick(LComponent comp, float x, float y);
    }
}
