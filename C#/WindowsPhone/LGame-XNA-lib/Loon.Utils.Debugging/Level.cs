namespace Loon.Utils.Debugging
{
    public class Level
    {

        public static readonly Level DEBUG = new Level("Debug", 1);

        public static readonly Level INFO = new Level("Info", 2);

        public static readonly Level WARN = new Level("Warn", 3);

        public static readonly Level ERROR = new Level("Error", 4);

        public static readonly Level IGNORE = new Level("Ignore", 5);

        public static readonly Level ALL = new Level("Ignore", 0);

        internal string levelString;

        internal readonly int level;

        private Level(string s, int levelInt)
        {
            this.levelString = s;
            this.level = levelInt;
        }

        public override string ToString()
        {
            return levelString;
        }

        public int ToType()
        {
            return level;
        }
    }
}
