using java.lang;

namespace loon.utils
{
    public class Calculator
    {

        public const int ADD = 1;

        public const int SUBTRACT = 2;

        public const int MULTIPLY = 3;

        public const int DIVIDE = 4;

        public const int MODULO = 5;

        public const int EQUAL = 6;

        private float currentTotal;

        public Calculator() : this(0)
        {

        }

        public Calculator(object num) : this(ConvertObjectToFloat(num))
        {

        }

        public Calculator(float num)
        {
            this.currentTotal = num;
        }

        protected static float ConvertObjectToFloat(object num)
        {
            if (num == null)
            {
                return -1f;
            }
            float value;
            if (num is Number number)
            {
                value = number.FloatValue();
            }
            else
            {
                string mes = num.ToString();
                if (MathUtils.IsNan(mes))
                {
                    value = Float.ParseFloat(mes);
                }
                else
                {
                    value = -1f;
                }
            }
            return value;
        }

        public Calculator Add(object num)
        {
            return Add(ConvertObjectToFloat(num));
        }

        public Calculator Sub(object num)
        {
            return Sub(ConvertObjectToFloat(num));
        }

        public Calculator Mul(object num)
        {
            return Mul(ConvertObjectToFloat(num));
        }

        public Calculator Div(object num)
        {
            return Div(ConvertObjectToFloat(num));
        }

        public Calculator Mod(object num)
        {
            return Mod(ConvertObjectToFloat(num));
        }

        public Calculator Equal(object num)
        {
            return Div(ConvertObjectToFloat(num));
        }

        public Calculator Add(string num)
        {
            return ConvertToFloat(num, ADD);
        }

        public Calculator Sub(string num)
        {
            return ConvertToFloat(num, SUBTRACT);
        }

        public Calculator Mul(string num)
        {
            return ConvertToFloat(num, MULTIPLY);
        }

        public Calculator Div(string num)
        {
            return ConvertToFloat(num, DIVIDE);
        }

        public Calculator Mod(string num)
        {
            return ConvertToFloat(num, MODULO);
        }

        private Calculator ConvertToFloat(string num, int oper)
        {
            if (MathUtils.IsNan(num))
            {
                float dblNumber = Float.ParseFloat(num);
                switch (oper)
                {
                    case ADD:
                        return Add(dblNumber);
                    case SUBTRACT:
                        return Sub(dblNumber);
                    case MULTIPLY:
                        return Mul(dblNumber);
                    case DIVIDE:
                        return Div(dblNumber);
                    case MODULO:
                        return Mod(dblNumber);
                    case EQUAL:
                        return Equal(dblNumber);
                    default:
                        break;
                }
            }
            return this;
        }

        public Calculator Add(float num)
        {
            currentTotal += num % 1f == 0 ? (int)num : num;
            return this;
        }

        public Calculator Sub(float num)
        {
            currentTotal -= num % 1f == 0 ? (int)num : num;
            return this;
        }

        public Calculator Mul(float num)
        {
            currentTotal *= num % 1f == 0 ? (int)num : num;
            return this;
        }

        public Calculator Div(float num)
        {
            currentTotal /= num % 1f == 0 ? (int)num : num;
            return this;
        }

        public Calculator Mod(float num)
        {
            currentTotal %= num % 1f == 0 ? (int)num : num;
            return this;
        }

        public Calculator Equal(float num)
        {
            currentTotal = num;
            return this;
        }

        public Calculator Equal(string num)
        {
            if (MathUtils.IsNan(num))
            {
                currentTotal = Float.ParseFloat(num);
            }
            return this;
        }

        public int GetInt()
        {
            return (int)currentTotal;
        }

        public float GetFloat()
        {
            return currentTotal;
        }

        public override string ToString()
        {
            return currentTotal % 1f == 0 ? Integer.ToString(GetInt()) : StringExtensions.ValueOf(currentTotal);
        }
    }
}
