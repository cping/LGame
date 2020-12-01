using loon.utils.reply;

namespace loon.monogame
{
    public class MonoGameAsyn<T> : Asyn.Default<T>
    {

        public MonoGameAsyn(Log log, Act<T> frame) : base(log, frame)
        {
        }

        public override bool IsAsyncSupported()
        {
            return false;
        }

    }
}
