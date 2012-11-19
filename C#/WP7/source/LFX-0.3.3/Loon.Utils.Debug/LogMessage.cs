namespace Loon.Utils.Debug
{
    public class LogMessage
    {
        static private System.DateTime date = System.DateTime.Now;

        public Level level;

        public string time;

        public string message;

        protected internal LogMessage(Level l, string m)
        {
            SetLogMessage(l, m);
        }

        protected internal void SetLogMessage(Level l, string m)
        {
            this.level = l;
            this.message = m;
            this.time = "[" + date.ToLocalTime().ToString() + "]";
        }

        public Level GetLevel()
        {
            return level;
        }

        public string GetMessage()
        {
            return message;
        }

        public string GetTime()
        {
            return time;
        }

        public override string ToString()
        {
            return (time + " [" + level + "] " + message);
        }
    }
}
