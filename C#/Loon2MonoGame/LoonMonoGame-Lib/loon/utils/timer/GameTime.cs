namespace loon.utils.timer
{

    public class GameTime
    {

        private static GameTime _instance = null;

        public static void FreeStatic()
        {
            _instance = null;
        }

        public static GameTime Get()
        {
            return GetInstance();
        }

        public static GameTime GetInstance()
        {
            if (_instance == null)
            {
                lock (typeof(GameTime))
                {
                    if (_instance == null)
                    {
                        _instance = new GameTime();
                    }
                }
            }
            return _instance;
        }

        float _elapsedTime;
        float _totalTime;

        bool _running;

        public static GameTime At()
        {
            return new GameTime();
        }

        public GameTime() : this(0f, 0f)
        {

        }

        public GameTime(float totalGameTime, float elapsedGameTime) : this(totalGameTime, elapsedGameTime, false)
        {

        }

        public GameTime(float totalRealTime, float elapsedRealTime, bool isRunningSlowly)
        {
            this._totalTime = totalRealTime;
            this._elapsedTime = elapsedRealTime;
            _running = isRunningSlowly;
        }

        public void Update(float elapsed)
        {
            this._elapsedTime = elapsed;
            this._totalTime += elapsed;
        }

        public void Update(LTimerContext context)
        {
            Update(context.GetMilliseconds());
        }

        public GameTime ResetElapsedTime()
        {
            _elapsedTime = 0f;
            return this;
        }

        public bool IsRunningSlowly()
        {
            return _running;
        }

        public float GetMilliseconds()
        {
            return MathUtils.Max(_elapsedTime * 1000, 10);
        }

        public float GetElapsedGameTime()
        {
            return _elapsedTime;
        }

        public float GetTotalGameTime()
        {
            return _totalTime;
        }


        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("GameTime");
            builder.Kv("elapsedTime", _elapsedTime).Comma().Kv("totalTime", _totalTime).Comma().Kv("running", _running);
            return builder.ToString();
        }
    }
}
