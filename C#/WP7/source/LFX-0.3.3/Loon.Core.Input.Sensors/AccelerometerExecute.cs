namespace Loon.Core.Input.Sensors
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework;
    using Loon.Java;

    public class AccelerometerExecute
    {

#if WINDOWS_PHONE
        private static Microsoft.Devices.Sensors.Accelerometer accelerometer = new Microsoft.Devices.Sensors.Accelerometer();

        private static object threadLock = new object();

        private static Vector3 nextValue = new Vector3();
#endif

        private static bool isInitialized = false;

        private static bool isActive = false;

        public static void Initialize()
        {
            if (isInitialized)
            {
                throw new InvalidOperationException("Initialize can only be called once !");
            }
#if WINDOWS_PHONE
            if (!Microsoft.Devices.Sensors.Accelerometer.IsSupported)
            {
                throw new RuntimeException("Not support accelerometer !");
            }
            if (Microsoft.Devices.Environment.DeviceType == Microsoft.Devices.DeviceType.Device)
            {
#pragma warning disable
                try
                {
                    accelerometer.TimeBetweenUpdates = TimeSpan.FromMilliseconds(16);
                    accelerometer.ReadingChanged += new EventHandler<Microsoft.Devices.Sensors.AccelerometerReadingEventArgs>(Sensor_ReadingChanged);
                    accelerometer.Start();
                    isActive = true;
                }
                catch (Microsoft.Devices.Sensors.AccelerometerFailedException)
                {
                    isActive = false;
                }
#pragma warning restore
            }
            else
            {
                isActive = true;
            }
#endif
            isInitialized = true;
        }

#if WINDOWS_PHONE
        private static void Sensor_ReadingChanged(object sender, Microsoft.Devices.Sensors.AccelerometerReadingEventArgs e)
        {
            lock (threadLock)
            {
                nextValue = new Vector3((float)e.X, (float)e.Y, (float)e.Z);
            }
            GetState();
        }
#endif

        public static bool IsActive()
        {
            return isActive;
        }

        public static LProcess.AccelerometerState GetState()
        {
            if (!isInitialized)
            {
                throw new InvalidOperationException("You must Initialize before you can call GetState");
            }

            Vector3 stateValue = new Vector3();

#if WINDOWS_PHONE
            if (isActive)
            {
                if (Microsoft.Devices.Environment.DeviceType == Microsoft.Devices.DeviceType.Device)
                {
                    lock (threadLock)
                    {
                        stateValue = nextValue;
                    }
                }
                else
                {
                    //°´¼üÄ£Äâ
                    Microsoft.Xna.Framework.Input.KeyboardState keyboardState = Microsoft.Xna.Framework.Input.Keyboard.GetState();

                    stateValue.Z = -1;

                    if (keyboardState.IsKeyDown(Microsoft.Xna.Framework.Input.Keys.Left))
                    {
                        stateValue.X--;
                    }
                    if (keyboardState.IsKeyDown(Microsoft.Xna.Framework.Input.Keys.Right))
                    {
                        stateValue.X++;
                    }
                    if (keyboardState.IsKeyDown(Microsoft.Xna.Framework.Input.Keys.Up))
                    {
                        stateValue.Y++;
                    }
                    if (keyboardState.IsKeyDown(Microsoft.Xna.Framework.Input.Keys.Down))
                    {
                        stateValue.Y--;
                    }
                    stateValue.Normalize();
                }
            }
#endif
            LProcess.AccelerometerState state = new LProcess.AccelerometerState(stateValue, isActive);
            if (LSystem.screenProcess != null)
            {
                LSystem.screenProcess.OnSensorChanged(state);
            }
            return state;
        }
    }

}
