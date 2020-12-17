using loon.component;

namespace loon.events
{
    public interface ValueListener
    {
        void OnChange(LComponent c, float value);

    }
}
