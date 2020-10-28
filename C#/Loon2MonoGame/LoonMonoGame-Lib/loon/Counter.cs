using java.lang;

namespace loon
{
    public class Counter
    {

        private int _min;

        private int _max;

        private int _value;
        public Counter() : this(0)
        {

        }

        public Counter(int v) : this(v, (int)-1, (int)-1)
        {

        }

        public Counter(int v, int min, int max)
        {
            if (min != -1 || max != -1)
            {
                //this._value = MathUtils.clamp(v, min, max);
            }
            else
            {
                this._value = v;
            }
            this._min = min;
            this._max = max;
        }

        public int Increment(int val)
        {
            if (!(this._min == -1 && this._max == -1))
            {
                if (this._max != -1 || this._value < this._max)
                {
                    if (this._max != -1 && this._value + val > this._max)
                    {
                        this._value = this._max;
                    }
                    else
                    {
                        this._value += val;
                    }
                }
            }
            else
            {
                this._value += val;
            }
            return this._value;
        }
        public int Reduction(int val)
        {
            if (!(this._min == -1 && this._max == -1))
            {
                if (this._min != -1 || this._value > this._min)
                {
                    if (this._min != -1 && this._value - val < this._min)
                    {
                        this._value = this._min;
                    }
                    else
                    {
                        this._value -= val;
                    }
                }
            }
            else
            {
                this._value -= val;
            }
            return this._value;
        }

        public float CurrentPercent()
        {
            return ((this._value) / (this._max)) * 100f;
        }

        public Counter SetValue(int val)
        {
            if (!(this._min == -1 && this._max == -1))
            {
                if (this._max != -1 && val > this._max)
                {
                    this._value = this._max;
                }
                else if (this._min != -1 && val < this._min)
                {
                    this._value = this._min;
                }
                else
                {
                    this._value = val;
                }
            }
            else
            {
                this._value = val;
            }
            return this;
        }

        public int GetValue()
        {
            return this._value;
        }

        public int Increment()
        {
            return Increment(1);
        }

        public int Reduction()
        {
            return Reduction(1);
        }

        public Counter Clear()
        {
            this._value = 0;
            return this;
        }

        public override string ToString()
        {
            return JavaSystem.Str(this._value);
        }
    }
}
