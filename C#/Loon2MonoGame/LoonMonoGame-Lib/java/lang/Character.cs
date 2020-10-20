using System;

namespace java.lang 
{
    public class Character
    {
        private readonly char value;
        public readonly static int MIN_RADIX = 2;
        public readonly static int MAX_RADIX = 36;

        public readonly static byte UNASSIGNED = 0;
        public readonly static byte UPPERCASE_LETTER = 1;
        public readonly static byte LOWERCASE_LETTER = 2;
        public readonly static byte TITLECASE_LETTER = 3;
        public readonly static byte MODIFIER_LETTER = 4;
        public readonly static byte OTHER_LETTER = 5;
        public readonly static byte NON_SPACING_MARK = 6;
        public readonly static byte ENCLOSING_MARK = 7;
        public readonly static byte COMBINING_SPACING_MARK = 8;
        public readonly static byte DECIMAL_DIGIT_NUMBER = 9;
        public readonly static byte LETTER_NUMBER = 10;
        public readonly static byte OTHER_NUMBER = 11;
        public readonly static byte SPACE_SEPARATOR = 12;
        public readonly static byte LINE_SEPARATOR = 13;
        public readonly static byte PARAGRAPH_SEPARATOR = 14;
        public readonly static byte CONTROL = 15;
        public readonly static byte FORMAT = 16;
        public readonly static byte PRIVATE_USE = 17;
        public readonly static byte SURROGATE = 19;
        public readonly static byte DASH_PUNCTUATION = 20;
        public readonly static byte START_PUNCTUATION = 21;
        public readonly static byte END_PUNCTUATION = 22;
        public readonly static byte CONNECTOR_PUNCTUATION = 23;
        public readonly static byte OTHER_PUNCTUATION = 24;
        public readonly static byte MATH_SYMBOL = 25;
        public readonly static byte CURRENCY_SYMBOL = 26;
        public readonly static byte MODIFIER_SYMBOL = 27;
        public readonly static byte OTHER_SYMBOL = 28;
        public readonly static byte INITIAL_QUOTE_PUNCTUATION = 29;
        public readonly static byte FINAL_QUOTE_PUNCTUATION = 30;
        public readonly static sbyte DIRECTIONALITY_UNDEFINED = -1;
        public readonly static byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;
        public readonly static byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;
        public readonly static byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;
        public readonly static byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;
        public readonly static byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;
        public readonly static byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;
        public readonly static byte DIRECTIONALITY_ARABIC_NUMBER = 6;
        public readonly static byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;
        public readonly static byte DIRECTIONALITY_NONSPACING_MARK = 8;
        public readonly static byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;
        public readonly static byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;
        public readonly static byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;
        public readonly static byte DIRECTIONALITY_WHITESPACE = 12;
        public readonly static byte DIRECTIONALITY_OTHER_NEUTRALS = 13;
        public readonly static byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;
        public readonly static byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;
        public readonly static byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;
        public readonly static byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;
        public readonly static byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;
        public readonly static char MIN_HIGH_SURROGATE = '\uD800';
        public readonly static char MAX_HIGH_SURROGATE = '\uDBFF';
        public readonly static char MIN_LOW_SURROGATE = '\uDC00';
        public readonly static char MAX_LOW_SURROGATE = '\uDFFF';
        public readonly static char MIN_SURROGATE = MIN_HIGH_SURROGATE;
        public readonly static char MAX_SURROGATE = MAX_LOW_SURROGATE;
        public readonly static int MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;
        public readonly static int MIN_CODE_POINT = 0x000000;
        public readonly static int MAX_CODE_POINT = 0X10FFFF;
        public readonly static int SIZE = 16;

        private readonly static Character[] characterCache = new Character[128];

        private readonly static int SURROGATE_BIT_MASK = 0xFC00;
        private readonly static int SURROGATE_BIT_INV_MASK = 0x03FF;
        private readonly static int HIGH_SURROGATE_BITS = 0xD800;
        private readonly static int LOW_SURROGATE_BITS = 0xDC00;
        private readonly static int MEANINGFUL_SURROGATE_BITS = 10;

        public const char MIN_VALUE_JAVA = (char)0;
        public const char MAX_VALUE_JAVA = (char)0xffff;
        public Character(char v)
        {   
            value = v;
        }

        public char CharValue()
        {   
            return value;
        }
            
        public override bool Equals(object o)
        {   
            if (o == null || !(o is Character)) return false;
            return ((Character)o).value == value;
        }

        public override int GetHashCode()
        {   
            return (int) value;
        }

        public override string ToString()
        {   
            return Character.ToString(value);
        }

        public static string ToString(char c)
        {   
            return c.ToString();
        }
        public static Character ValueOf(char value)
        {
            if (value < characterCache.Length)
            {
                Character result = characterCache[value];
                if (result == null)
                {
                    result = new Character(value);
                    characterCache[value] = result;
                }
                return result;
            }
            return new Character(value);
        }

        public static bool IsValidCodePoint(int codePoint)
        {
            return codePoint >= 0 && codePoint <= MAX_CODE_POINT;
        }

        public static bool IsBmpCodePoint(int codePoint)
        {
            return codePoint > 0 && codePoint <= MAX_VALUE_JAVA;
        }

        public static bool IsSupplementaryCodePoint(int codePoint)
        {
            return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT && codePoint <= MAX_CODE_POINT;
        }

        public static bool IsHighSurrogate(char ch)
        {
            return (ch & SURROGATE_BIT_MASK) == HIGH_SURROGATE_BITS;
        }

        public static bool IsLowSurrogate(char ch)
        {
            return (ch & SURROGATE_BIT_MASK) == LOW_SURROGATE_BITS;
        }

        public static bool IsSurrogate(char ch)
        {
            return IsHighSurrogate(ch) || IsLowSurrogate(ch);
        }

        public static bool IsSurrogatePair(char high, char low)
        {
            return IsHighSurrogate(high) && IsLowSurrogate(low);
        }

        public static int CharCount(int codePoint)
        {
            return IsSupplementaryCodePoint(codePoint) ? 2 : 1;
        }

        public static int ToCodePoint(char high, char low)
        {
            return (((high & SURROGATE_BIT_INV_MASK) << MEANINGFUL_SURROGATE_BITS) | (low & SURROGATE_BIT_INV_MASK))
                    + MIN_SUPPLEMENTARY_CODE_POINT;
        }

        public static int CodePointAt(CharSequence seq, int index)
        {
            if (index >= seq.Length() - 1 || !IsHighSurrogate(seq.CharAt(index))
                    || !IsLowSurrogate(seq.CharAt(index + 1)))
            {
                return seq.CharAt(index);
            }
            else
            {
                return ToCodePoint(seq.CharAt(index), seq.CharAt(index + 1));
            }
        }

        public static int CodePointAt(char[] a, int index)
        {
            return CodePointAt(a, index, a.Length);
        }

        public static int CodePointAt(char[] a, int index, int limit)
        {
            if (index >= limit - 1 || !IsHighSurrogate(a[index]) || !IsLowSurrogate(a[index + 1]))
            {
                return a[index];
            }
            else
            {
                return ToCodePoint(a[index], a[index + 1]);
            }
        }

        public static int CodePointBefore(CharSequence seq, int index)
        {
            if (index == 1 || !IsLowSurrogate(seq.CharAt(index - 1)) || !IsHighSurrogate(seq.CharAt(index - 2)))
            {
                return seq.CharAt(index - 1);
            }
            return ToCodePoint(seq.CharAt(index - 2), seq.CharAt(index - 1));
        }

        public static int CodePointBefore(char[] a, int index)
        {
            return CodePointBefore(a, index, 0);
        }

        public static int CodePointBefore(char[] a, int index, int start)
        {
            if (index <= start + 1 || !IsLowSurrogate(a[index - 1]) || !IsHighSurrogate(a[index - 2]))
            {
                return a[index];
            }
            else
            {
                return ToCodePoint(a[index - 2], a[index - 1]);
            }
        }

        public static char HighSurrogate(int codePoint)
        {
            codePoint -= MIN_SUPPLEMENTARY_CODE_POINT;
            return (char)(HIGH_SURROGATE_BITS | (codePoint >> MEANINGFUL_SURROGATE_BITS) & SURROGATE_BIT_INV_MASK);
        }

        public static char LowSurrogate(int codePoint)
        {
            return (char)(LOW_SURROGATE_BITS | codePoint & SURROGATE_BIT_INV_MASK);
        }

        public static int ToLowerCase(int ch)
        {
            return (int)JavaSystem.Str(ch).ToLower().CharAt(0);
        }

        public static char ToLowerCase(char ch)
        {
            return JavaSystem.Str(ch).ToLower().CharAt(0);
        }
        public static char ToUpperCase(char ch)
        {
            return JavaSystem.Str(ch).ToUpper().CharAt(0);
        }
        public static int ToUpperCase(int ch)
        {
            return (int)JavaSystem.Str(ch).ToUpper().CharAt(0);
        }
    }
}
