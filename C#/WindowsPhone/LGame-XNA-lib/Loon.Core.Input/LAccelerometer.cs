using Microsoft.Xna.Framework;
using System;
using Loon.Java;
using Loon.Action.Map;
using Loon.Utils;
using Loon.Core.Geom;

namespace Loon.Core.Input
{
    public class LAccelerometer
    {

        public enum SensorDirection
        {
            EMPTY, LEFT, RIGHT, UP, DOWN
        }

        public interface Event
        {

            void OnDirection(SensorDirection direction, float x, float y,
                    float z);

            void OnShakeChanged(float force);
        }

        public static bool CheckAngle(float angle, float actual)
        {
            return actual > angle - 22.5f && actual < angle + 22.5f;
        }


        // 四方向手机朝向
        private SensorDirection _direction = SensorDirection.EMPTY;

        // 八方向手机朝向
        private int _all_direction = Config.EMPTY;

        private Event eve;

        private long lastUpdate;

        private float currentX, currentY, currentZ, currenForce;

        private float lastX, lastY, lastZ;

        private float orientation, magnitude;

        public LAccelerometer()
        {

        }


        private void OnOrientation(float x, float y, float z)
        {
            // 换算手机翻转角度
            orientation = 0;
            magnitude = x * x + y * y;
            if (magnitude * 4 >= z * z)
            {
                float angle = MathUtils.Atan2(-y, x) * MathUtils.RAD_TO_DEG;
                orientation = 90 - MathUtils.Round(angle);
                while (orientation >= 360)
                {
                    orientation -= 360;
                }
                while (orientation < 0)
                {
                    orientation += 360;
                }
            }
            // 将手机翻转角度转为手机朝向
            if (CheckAngle(0, orientation) || CheckAngle(360, orientation))
            {
                _all_direction = Config.TUP;
                _direction = SensorDirection.UP;
            }
            else if (CheckAngle(45, orientation))
            {
                _all_direction = Config.LEFT;
                _direction = SensorDirection.LEFT;
            }
            else if (CheckAngle(90, orientation))
            {
                _all_direction = Config.TLEFT;
                _direction = SensorDirection.LEFT;
            }
            else if (CheckAngle(135, orientation))
            {
                _all_direction = Config.DOWN;
                _direction = SensorDirection.LEFT;
            }
            else if (CheckAngle(180, orientation))
            {
                _all_direction = Config.TDOWN;
                _direction = SensorDirection.DOWN;
            }
            else if (CheckAngle(225, orientation))
            {
                _all_direction = Config.RIGHT;
                _direction = SensorDirection.RIGHT;
            }
            else if (CheckAngle(270, orientation))
            {
                _all_direction = Config.TRIGHT;
                _direction = SensorDirection.RIGHT;
            }
            else if (CheckAngle(315, orientation))
            {
                _all_direction = Config.UP;
                _direction = SensorDirection.RIGHT;
            }
            else
            {
                _all_direction = Config.EMPTY;
                _direction = SensorDirection.EMPTY;
            }
        }

        private void OnSensor(float[] values)
        {
            lock (this)
            {

                long curTime = JavaRuntime.CurrentTimeMillis();

                if (LSystem.SCREEN_LANDSCAPE)
                {
                    currentX = -values[0];
                    currentY = -values[1];
                    currentZ = -values[2];
                }
                else
                {
                    currentX = values[0];
                    currentY = values[1];
                    currentZ = values[2];
                }

                OnOrientation(currentX, currentY, currentZ);

                if ((curTime - lastUpdate) > 30)
                {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;
                    currenForce = MathUtils.Abs(currentX + currentY + currentZ
                            - lastX - lastY - lastZ)
                            / diffTime * 10000;

                    if (currenForce > 500 && eve != null)
                    {
                        eve.OnShakeChanged(currenForce);
                    }
                }

                lastX = currentX;
                lastY = currentY;
                lastZ = currentZ;

                if (eve != null)
                {
                    eve.OnDirection(_direction, currentX, currentY, currentZ);
                }
            }
        }


        public class AccelerometerState
        {

            internal bool _isConnected;

            internal Vector3f _acceleration = new Vector3f();

            public Vector3f GetAcceleration()
            {
                return _acceleration;
            }

            public bool IsConnected()
            {
                return _isConnected;
            }

        }

        private AccelerometerState _state = new AccelerometerState();

        class SensorThread : Thread
        {

            float[] accelerometerValues;

            LAccelerometer accelerometer;

            public SensorThread(LAccelerometer acc, float[] values)
            {
                this.accelerometer = acc;
                this.accelerometerValues = values;
            }

            public override void Run()
            {
                while (accelerometer._state._isConnected)
                {
                    accelerometerValues[2] = -1f;
                    if (Key.IsDown() && Key.GetKeyCode() == Key.LEFT)
                    {
                        accelerometerValues[0]--;
                    }
                    if (Key.IsDown() && Key.GetKeyCode() == Key.RIGHT)
                    {
                        accelerometerValues[0]++;
                    }
                    if (Key.IsDown() && Key.GetKeyCode() == Key.UP)
                    {
                        accelerometerValues[1]++;
                    }
                    if (Key.IsDown() && Key.GetKeyCode() == Key.DOWN)
                    {
                        accelerometerValues[1]--;
                    }
                    accelerometer.OnSensor(accelerometerValues);
                    accelerometer._state._acceleration.Set(accelerometer.currentX, accelerometer.currentY, accelerometer.currentZ);
                    try
                    {
                        Thread.Sleep(accelerometer._sleep);
                    }
                    catch (Exception)
                    {
                    }
                }
            }
        }

        private int _sleep = 20;

        private float[] accelerometerValues = new float[3];

        private float[] magneticFieldValues = new float[3];

#if !WINDOWS&&!XBOX
    private Microsoft.Devices.Sensors.Accelerometer manager;
        
    private void accelerometer_CurrentValueChanged(object sender, Microsoft.Devices.Sensors.SensorReadingEventArgs<Microsoft.Devices.Sensors.AccelerometerReading> e)
    {
        lock (this)
        {
            Vector3 vector = e.SensorReading.Acceleration;
            if (!LSystem.SCREEN_LANDSCAPE)
            {
                accelerometerValues[0] = vector.X;
                accelerometerValues[1] = vector.Y;
                accelerometerValues[2] = vector.Z;
            }
            else
            {
                accelerometerValues[0] = vector.Y;
                accelerometerValues[1] = -vector.X;
                accelerometerValues[2] = vector.Z;
            }
        }
        OnSensor(accelerometerValues);
        _state._acceleration.Set(currentX, currentY, currentZ);
    }
#endif

        public void Start()
        {
            // 模拟器下启动时键盘模拟重力
            if (LSystem.IsEmulator())
            {
                _state._isConnected = true;
                LSystem.CallScreenRunnable(new SensorThread(this, accelerometerValues));
                return;
            }
#if !WINDOWS&&!XBOX
            if (!_state._isConnected && manager == null)
            {
                if (Microsoft.Devices.Sensors.Accelerometer.IsSupported)
                {
                    if (this.manager == null)
                    {
                        this.manager = new Microsoft.Devices.Sensors.Accelerometer();
                        this.manager.TimeBetweenUpdates = (TimeSpan.FromMilliseconds(_sleep));
                        this.manager.CurrentValueChanged += (new EventHandler<Microsoft.Devices.Sensors.SensorReadingEventArgs<Microsoft.Devices.Sensors.AccelerometerReading>>(this.accelerometer_CurrentValueChanged));
                    }
                    _state._isConnected = true;
                }
                else
                {
                    _state._isConnected = false;
                }
                if (_state._isConnected)
                {
                    if (this.manager != null)
                    {
                        try
                        {
                            this.manager.Start();
                        }
                        catch (InvalidOperationException ex)
                        {
                            Loon.Utils.Debug.Log.Exception(ex);
                        }
                        return;
                    }
                }
#endif
                // 如果无法正常启动，则开启伪重力感应
                if (!_state._isConnected)
                {
                    _state._isConnected = true;
                    LSystem.CallScreenRunnable(new SensorThread(this, accelerometerValues));
                }
            }
        }


        public void Stop()
        {
#if !WINDOWS&&!XBOX
            if (manager != null)
            {
                try
                {
                    this.manager.Stop();
                }
                catch (InvalidOperationException ex)
                {
                    Loon.Utils.Debug.Log.Exception(ex);
                }
                manager = null;
                _state._isConnected = false;
            }
            else
            {
                _state._isConnected = false;
            }
#endif
        }

        public float GetLastX()
        {
            return lastX;
        }

        public float GetLastY()
        {
            return lastY;
        }

        public float GetLastZ()
        {
            return lastZ;
        }

        public float GetX()
        {
            return currentX;
        }

        public float GetY()
        {
            return currentY;
        }

        public float GetZ()
        {
            return currentZ;
        }

        public AccelerometerState GetState()
        {
            return _state;
        }

        public int GetSleep()
        {
            return _sleep;
        }

        public void Sleep(int sleep)
        {
            this._sleep = sleep;
        }

        public int GetAllDirection()
        {
            return _all_direction;
        }

        public Event GetEvent()
        {
            return eve;
        }

        public void SetEvent(Event e)
        {
            this.eve = e;
        }

        public float GetOrientation()
        {
            return orientation;
        }

        public SensorDirection GetDirection()
        {
            return _direction;
        }
    }
}
