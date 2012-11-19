namespace Loon.Core.Event
{
    using Loon.Core.Input;

    public interface TouchListener
    {

        void pressed(LTouch e);

        void released(LTouch e);

        void move(LTouch e);

        void drag(LTouch e);

    }
}