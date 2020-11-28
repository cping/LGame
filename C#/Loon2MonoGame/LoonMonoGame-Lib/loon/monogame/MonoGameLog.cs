namespace loon.monogame
{
    public class MonoGameLog : Log
    {

        private bool isInit = false;

        private LogImpl log = null;

        public bool Init()
        {
            if (!isInit)
            {
                log = LogFactory.GetInstance(LSystem.GetSystemAppName());
                isInit = true;
            }
            return isInit;
        }


        protected internal override void CallNativeLog(Level level, string msg, System.Exception e)
        {
            if (Init())
            {
                log.AddLogMessage(msg, level, e);
            }
        }


        public override void OnError(System.Exception e)
        {

        }
    }

}
