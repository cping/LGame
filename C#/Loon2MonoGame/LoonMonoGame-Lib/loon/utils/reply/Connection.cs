namespace loon.utils.reply
{
    public abstract class Connection : Closeable
    {

        public abstract Connection Once();

        public abstract Connection SetPriority(int priority);

    }
}
