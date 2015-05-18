using System;
namespace Loon.Core.Timer
{
    public class NanoTimer : SystemTimer
    {

        private const int NUM_TIMERS = 8;

        private const long ONE_SEC = 1000000000L;

        private const long MAX_DIFF = ONE_SEC;

        private const long NEVER_USED = -1;

        private const long DEFAULT_FAIL_RESET_TIME = ONE_SEC;

        private long[] lastTimeStamps = new long[NUM_TIMERS];

        private long[] timeSinceLastUsed = new long[NUM_TIMERS];

        private long virtualNanoTime;

        private int timesInARowNewTimerChosen;

        private long lastDiff;

        private long failTime;

        private long failResetTime;

        public NanoTimer()
        {
            virtualNanoTime = 0;
            failResetTime = DEFAULT_FAIL_RESET_TIME;
            Reset();
        }

        private void Reset()
        {
            failTime = 0;
            lastDiff = 0;
            timesInARowNewTimerChosen = 0;
            for (int i = 0; i < NUM_TIMERS; i++)
            {
                timeSinceLastUsed[i] = NEVER_USED;
            }
        }

        private long NanoTime()
        {
            long diff;

            if (timesInARowNewTimerChosen >= NUM_TIMERS)
            {
                long nanoTime = Environment.TickCount * 1000000;
                diff = nanoTime - lastTimeStamps[0];
                failTime += diff;
                if (failTime >= failResetTime)
                {
                    Reset();
                    failResetTime *= 2;
                }
            }
            else
            {
                long nanoTime = Environment.TickCount;
                int bestTimer = -1;
                long bestDiff = 0;
                for (int i = 0; i < NUM_TIMERS; i++)
                {
                    if (timeSinceLastUsed[i] != NEVER_USED)
                    {
                        long t = lastTimeStamps[i] + timeSinceLastUsed[i];
                        long timerDiff = nanoTime - t;
                        if (timerDiff > 0 && timerDiff < MAX_DIFF)
                        {
                            if (bestTimer == -1 || timerDiff < bestDiff)
                            {
                                bestTimer = i;
                                bestDiff = timerDiff;
                            }
                        }
                    }
                }

                if (bestTimer == -1)
                {
                    diff = lastDiff;
                    bestTimer = 0;
                    for (int i = 0; i < NUM_TIMERS; i++)
                    {
                        if (timeSinceLastUsed[i] == NEVER_USED)
                        {
                            bestTimer = i;
                            break;
                        }
                        else if (timeSinceLastUsed[i] > timeSinceLastUsed[bestTimer])
                        {
                            bestTimer = i;
                        }
                    }
                    timesInARowNewTimerChosen++;
                }
                else
                {
                    timesInARowNewTimerChosen = 0;
                    failResetTime = DEFAULT_FAIL_RESET_TIME;
                    diff = nanoTime - lastTimeStamps[bestTimer]
                            - timeSinceLastUsed[bestTimer];

                    if (timeSinceLastUsed[bestTimer] == 0)
                    {
                        lastDiff = diff;
                    }
                }

                lastTimeStamps[bestTimer] = nanoTime;
                timeSinceLastUsed[bestTimer] = 0;

                for (int i = 0; i < NUM_TIMERS; i++)
                {
                    if (i != bestTimer && timeSinceLastUsed[i] != NEVER_USED)
                    {
                        timeSinceLastUsed[i] += diff;
                    }
                }

                if (timesInARowNewTimerChosen >= NUM_TIMERS)
                {
                    lastTimeStamps[0] = Environment.TickCount * 1000000;
                }
            }

            virtualNanoTime += diff;

            return virtualNanoTime;
        }

        public override long GetTimeMillis()
        {
            return NanoTime() / 1000000;
        }

        public override long GetTimeMicros()
        {
            return NanoTime() / 1000;
        }
    }
}
