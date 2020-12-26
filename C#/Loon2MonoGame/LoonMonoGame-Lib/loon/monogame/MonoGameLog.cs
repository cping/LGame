using java.lang;
using MonoGame.Framework.Utilities;

namespace loon.monogame
{
    public class MonoGameLog : Log
    {

        private bool isInit = false;

        private LogImpl log = null;

        private readonly string logMes;

        public MonoGameLog(string log)
        {
            this.logMes = log;
        }

        public bool Init()
        {
            if (!isInit)
            {
                log = LogFactory.GetInstance(LSystem.GetSystemAppName());
                isInit = true;
            }
            return isInit;
        }

        protected internal void LogOut(object o, string msg)
        {
            JavaSystem.Out.Println(o + " : " + msg);
        }

        protected internal void LogOut(object o, string msg, System.Exception e)
        {
            JavaSystem.Out.Println(o + " : " + msg + " , " + e.Message);
        }

        protected internal override void CallNativeLog(Level level, string msg, System.Exception e)
        {
            if (Init())
            {
                if (PlatformInfo.MonoGamePlatform == MonoGamePlatform.DesktopGL || PlatformInfo.MonoGamePlatform == MonoGamePlatform.Windows || PlatformInfo.MonoGamePlatform == MonoGamePlatform.WindowsUniversal)
                {
                    log.AddLogMessage(msg, level, e);
                }
                else
                {
                    if (e == null)
                    {
                        if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id)
                        {
                            LogOut(logMes + "-" + level, msg);
                        }
                        else if (level.id == Level.ALL.id || level.id <= Level.WARN.id)
                        {
                            LogOut(logMes + "-" + level, msg);
                        }
                        else if (level.id == Level.ALL.id || level.id <= Level.ERROR.id)
                        {
                            LogOut(logMes + "-" + level, msg);
                        }
                        else
                        {
                            LogOut(logMes + "-" + level, msg);
                        }
                    }
                    else
                    {
                        if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id)
                        {
                            LogOut(logMes + "-" + level, msg, e);
                        }
                        else if (level.id == Level.ALL.id || level.id <= Level.WARN.id)
                        {
                            LogOut(logMes + "-" + level, msg, e);
                        }
                        else if (level.id == Level.ALL.id || level.id <= Level.ERROR.id)
                        {
                            LogOut(logMes + "-" + level, msg, e);
                        }
                        else
                        {
                            LogOut(logMes + "-" + level, msg, e);
                        }
                        if (e != null)
                        {
                            JavaSystem.Err.Println(e.StackTrace);
                        }
                    }
                }
            }
        }


        public override void OnError(System.Exception e)
        {

        }
    }

}
