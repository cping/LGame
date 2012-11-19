namespace Loon.Action.Avg.Drama
{
    using System;
    using System.Text;
    using System.Collections;
    using System.Collections.Generic;
    using Loon.Java.Collections;
    using Loon.Utils;
    using Loon.Core;
    using Loon.Java;

    public abstract class Conversion : Expression
    {

        private const int MAX_LENGTH = 128;

        public const int STACK_VARIABLE = 11;

        public const int STACK_RAND = -1;

        public const int STACK_NUM = 0;

        public const int PLUS = 1;

        public const int MINUS = 2;

        public const int MULTIPLE = 3;

        public const int DIVISION = 4;

        public const int MODULO = 5;

       internal Exps exp = new Exps();

        internal static bool IsCondition(string s)
        {
            if (s.Equals("==", StringComparison.InvariantCultureIgnoreCase))
            {
                return true;
            }
            else if (s.Equals("!=", StringComparison.InvariantCultureIgnoreCase))
            {
                return true;
            }
            else if (s.Equals(">=", StringComparison.InvariantCultureIgnoreCase))
            {
                return true;
            }
            else if (s.Equals("<=", StringComparison.InvariantCultureIgnoreCase))
            {
                return true;
            }
            else if (s.Equals(">", StringComparison.InvariantCultureIgnoreCase))
            {
                return true;
            }
            else if (s.Equals("<", StringComparison.InvariantCultureIgnoreCase))
            {
                return true;
            }
            return false;
        }

        internal static bool IsOperator(char c)
        {
            switch (c)
            {
                case '+':
                    return true;
                case '-':
                    return true;
                case '*':
                    return true;
                case '/':
                    return true;
                case '<':
                    return true;
                case '>':
                    return true;
                case '=':
                    return true;
                case '%':
                    return true;
                case '!':
                    return true;
            }
            return false;
        }

        public static string UpdateOperator(string context)
        {
            if (context != null
                    && (context.StartsWith("\"") || context.StartsWith("'")))
            {
                return context;
            }
            int size = context.Length;
            StringBuilder sbr = new StringBuilder(size * 2);
            char[] chars = context.ToCharArray();
            bool notFlag = false;
            bool operators;
            for (int i = 0; i < size; i++)
            {
                if (chars[i] == '"' || chars[i] == '\'')
                {
                    notFlag = !notFlag;
                }
                if (notFlag)
                {
                    sbr.Append(chars[i]);
                    continue;
                }
                if (chars[i] == ' ')
                {
                    if (i > 0 && chars[i - 1] != ' ')
                    {
                        sbr.Append(FLAG);
                    }
                }
                else
                {
                    operators = IsOperator(chars[i]);
                    if (i > 0)
                    {
                        if (operators && !IsOperator(chars[i - 1]))
                        {
                            if (chars[i - 1] != ' ')
                            {
                                sbr.Append(FLAG);
                            }
                        }
                    }
                    sbr.Append(chars[i]);
                    if (i < size - 1)
                    {
                        if (operators && !IsOperator(chars[i + 1]))
                        {
                            if (chars[i + 1] != ' ')
                            {
                                sbr.Append(FLAG);
                            }
                        }
                    }
                }
            }
            return sbr.ToString().Trim();
        }

        public static IList SplitToList(string strings, string tag)
        {
            return Arrays.AsList(StringUtils.Split(strings, tag));
        }

        public class Exps : LRelease
        {

            private Dictionary<string, Compute> computes = new Dictionary<string, Compute>();

            private char[] expChr;

            private bool Exp(string exp)
            {
                return exp.IndexOf("+") != -1 || exp.IndexOf("-") != -1
                        || exp.IndexOf("*") != -1 || exp.IndexOf("/") != -1
                        || exp.IndexOf("%") != -1;
            }

            public float Parse(object v)
            {
                return Parse((string)v);
            }

            public float Parse(string v)
            {
                if (!Exp(v))
                {
                    if (NumberUtils.IsNan(v))
                    {
                        return float.Parse(v);
                    }
                    else
                    {
                        throw new RuntimeException(v + " not parse !");
                    }
                }
                return Eval(v);
            }

            private void EvalFloatValue(Compute compute, int stIdx, int lgt,
                    float sign)
            {
                if (expChr[stIdx] == '$')
                {
                    string label = new string(expChr, stIdx + 1, lgt - 1);
                    if (label.Equals("rand", StringComparison.InvariantCultureIgnoreCase))
                    {
                        compute.Push(0, STACK_RAND);
                    }
                    else
                    {
                        int idx;
                        try
                        {
                            idx = int.Parse(label) - 1;
                        }
                        catch
                        {
                            compute.Push(0, STACK_NUM);
                            return;
                        }
                        compute.Push(0, STACK_VARIABLE + idx);
                    }
                }
                else
                {
                    try
                    {
                        float idx = float.Parse(new string(expChr, stIdx, lgt));
                        compute.Push(idx * sign, STACK_NUM);
                    }
                    catch
                    {
                        compute.Push(0, STACK_NUM);
                    }
                }
            }

            private void EvalExp(Compute compute, int stIdx, int edIdx)
            {
                int[] op = new int[] { -1, -1 };
                while (expChr[stIdx] == '(' && expChr[edIdx - 1] == ')')
                {
                    stIdx++;
                    edIdx--;
                }
                for (int i = edIdx - 1; i >= stIdx; i--)
                {
                    char c = expChr[i];
                    if (c == ')')
                    {
                        do
                        {
                            i--;
                        } while (expChr[i] != '(');
                    }
                    else if (op[0] < 0 && (c == '*' || c == '/' || c == '%'))
                    {
                        op[0] = i;
                    }
                    else if (c == '+' || c == '-')
                    {
                        op[1] = i;
                        break;
                    }
                }
                if (op[1] < 0)
                {
                    if (op[0] < 0)
                    {
                        EvalFloatValue(compute, stIdx, edIdx - stIdx, 1);
                    }
                    else
                    {
                        switch (expChr[op[0]])
                        {
                            case '*':
                                EvalExp(compute, stIdx, op[0]);
                                EvalExp(compute, op[0] + 1, edIdx);
                                compute.SetOperator(MULTIPLE);
                                break;
                            case '/':
                                EvalExp(compute, stIdx, op[0]);
                                EvalExp(compute, op[0] + 1, edIdx);
                                compute.SetOperator(DIVISION);
                                break;
                            case '%':
                                EvalExp(compute, stIdx, op[0]);
                                EvalExp(compute, op[0] + 1, edIdx);
                                compute.SetOperator(MODULO);
                                break;
                        }
                    }
                }
                else
                {
                    if (op[1] == stIdx)
                    {
                        switch (expChr[op[1]])
                        {
                            case '-':
                                EvalFloatValue(compute, stIdx + 1, edIdx - stIdx - 1,
                                        -1);
                                break;
                            case '+':
                                EvalFloatValue(compute, stIdx + 1, edIdx - stIdx - 1, 1);
                                break;
                        }
                    }
                    else
                    {
                        switch (expChr[op[1]])
                        {
                            case '+':
                                EvalExp(compute, stIdx, op[1]);
                                EvalExp(compute, op[1] + 1, edIdx);
                                compute.SetOperator(PLUS);
                                break;
                            case '-':
                                EvalExp(compute, stIdx, op[1]);
                                EvalExp(compute, op[1] + 1, edIdx);
                                compute.SetOperator(MINUS);
                                break;
                        }
                    }
                }
            }

            public float Eval(string exp)
            {
                Compute compute = (Compute)CollectionUtils.Get(computes, exp);
                if (compute == null)
                {
                    expChr = new char[exp.Length];
                    int ecIdx = 0;
                    bool skip = false;
                    StringBuilder buf = new StringBuilder(exp);
                    int depth = 0;
                    bool balance = true;
                    char ch;
                    for (int i = 0; i < buf.Length; i++)
                    {
                        ch = buf[i];
                        switch (ch)
                        {
                            case ' ':
                            case '\n':
                                skip = true;
                                break;
                            case ')':
                                depth--;
                                if (depth < 0)
                                    balance = false;
                                break;
                            case '(':
                                depth++;
                                break;
                        }
                        if (skip)
                        {
                            skip = false;
                        }
                        else
                        {
                            expChr[ecIdx] = ch;
                            ecIdx++;
                        }
                    }
                    if (depth != 0 || !balance)
                    {
                        return 0;
                    }
                    compute = new Compute();
                    EvalExp(compute, 0, ecIdx);
                    CollectionUtils.Put(computes, exp, compute);
                }
                return compute.Calc();
            }

            private class Compute
            {

                private float[] num = new float[MAX_LENGTH];

                private int[] opr = new int[MAX_LENGTH];

                private int idx;

                private float[] stack = new float[MAX_LENGTH];

                public Compute()
                {
                    idx = 0;
                }

                private float CalcOp(int op, float n1, float n2)
                {
                    switch (op)
                    {
                        case PLUS:
                            return n1 + n2;
                        case MINUS:
                            return n1 - n2;
                        case MULTIPLE:
                            return n1 * n2;
                        case DIVISION:
                            return n1 / n2;
                        case MODULO:
                            return n1 % n2;
                    }
                    return 0;
                }

                public void SetOperator(int op)
                {
                    if (idx >= MAX_LENGTH)
                    {
                        return;
                    }
                    if (opr[idx - 1] == STACK_NUM && opr[idx - 2] == STACK_NUM)
                    {
                        num[idx - 2] = CalcOp(op, num[idx - 2], num[idx - 1]);
                        idx--;
                    }
                    else
                    {
                        opr[idx] = op;
                        idx++;
                    }
                }

                public void Push(float nm, int vr)
                {
                    if (idx >= MAX_LENGTH)
                    {
                        return;
                    }
                    num[idx] = nm;
                    opr[idx] = vr;
                    idx++;
                }

                public float Calc()
                {
                    int stkIdx = 0;
                    for (int i = 0; i < idx; i++)
                    {
                        switch (opr[i])
                        {
                            case STACK_NUM:
                                stack[stkIdx] = num[i];
                                stkIdx++;
                                break;
                            case STACK_RAND:
                                stack[stkIdx] = (float)LSystem.random.NextDouble();
                                stkIdx++;
                                break;
                            default:
                                if (opr[i] >= STACK_VARIABLE)
                                {
                                    stkIdx++;
                                }
                                else
                                {
                                    stack[stkIdx - 2] = CalcOp(opr[i],
                                            stack[stkIdx - 2], stack[stkIdx - 1]);
                                    stkIdx--;
                                }
                                break;
                        }
                    }
                    return stack[0];
                }
            }

            public void Dispose()
            {
                if (computes != null)
                {
                    computes.Clear();
                }
            }
        }

    }
}
