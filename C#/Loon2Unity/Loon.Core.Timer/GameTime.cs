namespace Loon.Core.Timer
{
    public class GameTime
    {

        float _elapsedTime;
        float _totalTime;

        bool _running;

        public GameTime()
        {
            _elapsedTime = _totalTime = 0f;
        }

        public GameTime(float totalGameTime, float elapsedGameTime)
        {
            _totalTime = totalGameTime;
            _elapsedTime = elapsedGameTime;
        }

        public GameTime(float totalRealTime, float elapsedRealTime,
                bool isRunningSlowly)
        {
            _totalTime = totalRealTime;
            _elapsedTime = elapsedRealTime;
            _running = isRunningSlowly;
        }

        public void Update(float elapsed)
        {
            _elapsedTime = elapsed;
            _totalTime += elapsed;
        }

        public void Update(LTimerContext context)
        {
            Update(context.GetMilliseconds());
        }

        public void ResetElapsedTime()
        {
            _elapsedTime = 0f;
        }

        public bool IsRunningSlowly()
        {
            return _running;
        }

        public float GetMilliseconds()
        {
            return _elapsedTime * 1000;
        }

        public float GetElapsedGameTime()
        {
            return _elapsedTime;
        }

        public float GetTotalGameTime()
        {
            return _totalTime;
        }

    }
}
