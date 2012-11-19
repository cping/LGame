namespace Loon.Core.Geom
{
    using System;
    using Loon.Utils;

    public sealed class Easing
    {

        public sealed class _LINEAR : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * t / d + b;
            }
        }

        public sealed class _QUAD_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * (t /= d) * t + b;
            }
        }

        public sealed class _QUAD_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return -c * (t /= d) * (t - 2) + b;
            }
        }

        public sealed class _QUAD_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if ((t /= d / 2) < 1)
                    return c / 2 * t * t + b;
                return -c / 2 * ((--t) * (t - 2) - 1) + b;
            }
        }

        public sealed class _CUBIC_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * (t /= d) * t * t + b;
            }
        }

        public sealed class _CUBIC_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * ((t = t / d - 1) * t * t + 1) + b;
            }
        }

        public sealed class _CUBIC_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if ((t /= d / 2) < 1)
                    return c / 2 * t * t * t + b;
                return c / 2 * ((t -= 2) * t * t + 2) + b;
            }
        }

        public sealed class _QUARTIC_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * (t /= d) * t * t * t + b;
            }
        }

        public sealed class _QUARTIC_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return -c * ((t = t / d - 1) * t * t * t - 1) + b;
            }
        }

        public sealed class _QUARTIC_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if ((t /= d / 2) < 1)
                    return c / 2 * t * t * t * t + b;
                return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
            }
        }

        public sealed class _QUINTIC_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * (t /= d) * t * t * t * t + b;
            }
        }

        public sealed class _QUINTIC_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
            }
        }

        public sealed class _QUINTIC_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if ((t /= d / 2) < 1)
                    return c / 2 * t * t * t * t * t + b;
                return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
            }
        }

        public sealed class _SINE_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return -c * MathUtils.Cos(t / d * (MathUtils.PI / 2)) + c + b;
            }
        }

        public sealed class _SINE_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * MathUtils.Sin(t / d * (MathUtils.PI / 2)) + b;
            }
        }

        public sealed class _SINE_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return -c / 2 * (MathUtils.Cos(MathUtils.PI * t / d) - 1) + b;
            }
        }

        public sealed class _EXPO_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return (t == 0) ? b : c
                        * (float)MathUtils.Pow(2, 10 * (t / d - 1)) + b;
            }
        }

        public sealed class _EXPO_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return (t == d) ? b + c : c
                        * (-(float)MathUtils.Pow(2, -10 * t / d) + 1) + b;
            }
        }

        public sealed class _EXPO_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if (t == 0)
                    return b;
                if (t == d)
                    return b + c;
                if ((t /= d / 2) < 1)
                    return c / 2 * (float)MathUtils.Pow(2, 10 * (t - 1)) + b;
                return c / 2 * (-(float)MathUtils.Pow(2, -10 * --t) + 2) + b;
            }
        }

        public sealed class _CIRC_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return -c * ((float)MathUtils.Sqrt(1 - (t /= d) * t) - 1) + b;
            }
        }

        public sealed class _CIRC_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c * (float)MathUtils.Sqrt(1 - (t = t / d - 1) * t) + b;
            }
        }

        public sealed class _CIRC_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if ((t /= d / 2) < 1)
                    return -c / 2 * ((float)MathUtils.Sqrt(1 - t * t) - 1) + b;
                return c / 2 * ((float)MathUtils.Sqrt(1 - (t -= 2) * t) + 1) + b;
            }
        }

        public sealed class _BOUNCE_IN : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                return c - Easing.BOUNCE_OUT.Call(d - t, 0, c, d) + b;
            }
        }

        public sealed class _BOUNCE_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if ((t /= d) < (1 / 2.75f))
                {
                    return c * (7.5625f * t * t) + b;
                }
                else if (t < (2 / 2.75f))
                {
                    return c * (7.5625f * (t -= (1.5f / 2.75f)) * t + .75f) + b;
                }
                else if (t < (2.5f / 2.75f))
                {
                    return c * (7.5625f * (t -= (2.25f / 2.75f)) * t + .9375f) + b;
                }
                else
                {
                    return c * (7.5625f * (t -= (2.625f / 2.75f)) * t + .984375f)
                            + b;
                }
            }
        }

        public sealed class _BOUNCE_IN_OUT : Ease
        {
            public float Call(float t, float b, float c, float d)
            {
                if (t < d / 2)
                    return Easing.BOUNCE_IN.Call(t * 2, 0, c, d) * .5f + b;
                return Easing.BOUNCE_OUT.Call(t * 2 - d, 0, c, d) * .5f + c * .5f
                        + b;
            }
        }

        public interface Ease
        {

            float Call(float t, float b, float c, float d);

        }

        public static readonly Ease LINEAR = new _LINEAR();

        public static readonly Ease QUAD_IN = new _QUAD_IN();

        public static readonly Ease QUAD_OUT = new _QUAD_OUT();

        public static readonly Ease QUAD_IN_OUT = new _QUAD_IN_OUT();

        public static readonly Ease CUBIC_IN = new _CUBIC_IN();

        public static readonly Ease CUBIC_OUT = new _CUBIC_OUT();

        public static readonly Ease CUBIC_IN_OUT = new _CUBIC_IN_OUT();

        public static readonly Ease QUARTIC_IN = new _QUARTIC_IN();

        public static readonly Ease QUARTIC_OUT = new _QUARTIC_OUT();

        public static readonly Ease QUARTIC_IN_OUT = new _QUARTIC_IN_OUT();

        public static readonly Ease QUINTIC_IN = new _QUINTIC_IN();

        public static readonly Ease QUINTIC_OUT = new _QUINTIC_OUT();

        public static readonly Ease QUINTIC_IN_OUT = new _QUINTIC_IN_OUT();

        public static readonly Ease SINE_IN = new _SINE_IN();

        public static readonly Ease SINE_OUT = new _SINE_OUT();

        public static readonly Ease SINE_IN_OUT = new _SINE_IN_OUT();

        public static readonly Ease EXPO_IN = new _EXPO_IN();

        public static readonly Ease EXPO_OUT = new _EXPO_OUT();

        public static readonly Ease EXPO_IN_OUT = new _EXPO_IN_OUT();

        public static readonly Ease CIRC_IN = new _CIRC_IN();

        public static readonly Ease CIRC_OUT = new _CIRC_OUT();

        public static readonly Ease CIRC_IN_OUT = new _CIRC_IN_OUT();

        public abstract class Elastic : Ease
        {
            private float amplitude;
            private float period;

            public Elastic(float amplitude_0, float period_1)
            {
                this.amplitude = amplitude_0;
                this.period = period_1;
            }

            public Elastic()
                : this(-1f, 0f)
            {

            }

            public float GetPeriod()
            {
                return period;
            }

            public void SetPeriod(float period_0)
            {
                this.period = period_0;
            }

            public float GetAmplitude()
            {
                return amplitude;
            }

            public void SetAmplitude(float amplitude_0)
            {
                this.amplitude = amplitude_0;
            }

            public abstract float Call(float t, float b, float c, float d);
        }

        public static readonly Easing.Elastic ELASTIC_IN = new Easing.ElasticIn();

        public class ElasticIn : Elastic
        {
            public ElasticIn(float amplitude_0, float period_1)
                : base(amplitude_0, period_1)
            {

            }

            public ElasticIn()
                : base()
            {

            }

            public override float Call(float t, float b, float c, float d)
            {
                float a = GetAmplitude();
                float p = GetPeriod();
                if (t == 0)
                    return b;
                if ((t /= d) == 1)
                    return b + c;
                if (p == 0)
                    p = d * .3f;
                float s = 0;
                if (a < MathUtils.Abs(c))
                {
                    a = c;
                    s = p / 4;
                }
                else
                    s = p / (float)(2 * MathUtils.PI)
                            * (float)MathUtils.Asin(c / a);
                return -(a * (float)MathUtils.Pow(2, 10 * (t -= 1)) * (float)MathUtils
                        .Sin((t * d - s) * (2 * MathUtils.PI) / p)) + b;
            }
        }

        public static readonly Easing.Elastic ELASTIC_OUT = new Easing.ElasticOut();

        public class ElasticOut : Elastic
        {
            public ElasticOut(float amplitude_0, float period_1)
                : base(amplitude_0, period_1)
            {

            }

            public ElasticOut()
                : base()
            {

            }

            public override float Call(float t, float b, float c, float d)
            {
                float a = GetAmplitude();
                float p = GetPeriod();
                if (t == 0)
                    return b;
                if ((t /= d) == 1)
                    return b + c;
                if (p == 0)
                    p = d * .3f;
                float s = 0;
                if (a < MathUtils.Abs(c))
                {
                    a = c;
                    s = p / 4;
                }
                else
                    s = p / (float)(2 * MathUtils.PI)
                            * (float)MathUtils.Asin(c / a);
                return a * (float)MathUtils.Pow(2, -10 * t)
                        * MathUtils.Sin((t * d - s) * (2 * MathUtils.PI) / p) + c
                        + b;
            }
        }

        public static readonly Easing.Elastic ELASTIC_IN_OUT = new Easing.ElasticInOut();

        public class ElasticInOut : Elastic
        {
            public ElasticInOut(float amplitude_0, float period_1)
                : base(amplitude_0, period_1)
            {

            }

            public ElasticInOut()
                : base()
            {

            }

            public override float Call(float t, float b, float c, float d)
            {
                float a = GetAmplitude();
                float p = GetPeriod();
                if (t == 0)
                    return b;
                if ((t /= d / 2) == 2)
                    return b + c;
                if (p == 0)
                    p = d * (.3f * 1.5f);
                float s = 0;
                if (a < MathUtils.Abs(c))
                {
                    a = c;
                    s = p / 4f;
                }
                else
                    s = p / (float)(2 * MathUtils.PI)
                            * (float)MathUtils.Asin(c / a);
                if (t < 1)
                    return -.5f
                            * (a * (float)MathUtils.Pow(2, 10 * (t -= 1)) * MathUtils
                                    .Sin((t * d - s) * (2 * MathUtils.PI) / p)) + b;
                return a * (float)MathUtils.Pow(2, -10 * (t -= 1))
                        * MathUtils.Sin((t * d - s) * (2 * MathUtils.PI) / p) * .5f
                        + c + b;
            }
        }

        public abstract class Back : Ease
        {

            public const float DEFAULT_OVERSHOOT = 1.70158f;

            private float overshoot;

            public Back()
                : this(DEFAULT_OVERSHOOT)
            {

            }

            public Back(float o)
            {
                this.overshoot = o;
            }

            public void SetOvershoot(float o)
            {
                this.overshoot = o;
            }

            public float GetOvershoot()
            {
                return overshoot;
            }

            public abstract float Call(float t, float b, float c, float d);
        }

        public static readonly Easing.Back BACK_IN = new Easing.BackIn();

        public class BackIn : Back
        {
            public BackIn()
                : base()
            {

            }

            public BackIn(float o)
                : base(o)
            {

            }

            public override float Call(float t, float b, float c, float d)
            {
                float s = GetOvershoot();
                return c * (t /= d) * t * ((s + 1) * t - s) + b;
            }
        }

        public static readonly Easing.Back BACK_OUT = new Easing.BackOut();

        public class BackOut : Back
        {
            public BackOut()
                : base()
            {

            }

            public BackOut(float o)
                : base(o)
            {

            }

            public override float Call(float t, float b, float c, float d)
            {
                float s = GetOvershoot();
                return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
            }
        }

        public static readonly Easing.Back BACK_IN_OUT = new Easing.BackInOut();

        public class BackInOut : Back
        {
            public BackInOut()
                : base()
            {

            }

            public BackInOut(float o)
                : base(o)
            {

            }

            public override float Call(float t, float b, float c, float d)
            {
                float s = GetOvershoot();
                if ((t /= d / 2) < 1)
                    return c / 2 * (t * t * (((s *= (Single)(1.525d)) + 1) * t - s)) + b;
                return c / 2 * ((t -= 2) * t * (((s *= (Single)(1.525d)) + 1) * t + s) + 2)
                        + b;
            }
        }

        public static readonly Ease BOUNCE_IN = new _BOUNCE_IN();

        public static readonly Ease BOUNCE_OUT = new _BOUNCE_OUT();

        public static readonly Ease BOUNCE_IN_OUT = new _BOUNCE_IN_OUT();
    }
}
