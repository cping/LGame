using loon.utils.reply;

namespace loon.monogame
{
    public class MonoGameAsyn : Asyn.Default
    {

        public MonoGameAsyn(Log log, Act<LGame> frame) : base(log, frame)
        {
        }

        public override bool IsAsyncSupported()
        {
            return false;
        }

    }
}
