using System;
using Loon.Utils;
using Loon.Java;
using System.Text;
namespace Loon.Foundation {
	
	public class NSNumber : NSObject {
	
		public const int INTEGER = 0;
	
		public const int REAL = 1;
	
		public const int BOOLEAN = 2;
	
		private int type;
	
		private long longValue;
	
		private double doubleValue;
	
		private bool boolValue;
	
		protected static internal long ParseUnsignedInt(byte[] bytes) {
			long l = 0;
			foreach (byte b  in  bytes) {
				l <<= 8;
                l |= (uint)b & 0xFF;
			}
			l &= 0xFFFFFFFFL;
			return l;
		}
	
		protected static internal long ParseLong(byte[] bytes) {
			long l = 0;
			foreach (byte b  in  bytes) {
				l <<= 8;
                l |= (uint)b & 0xFF;
			}
			return l;
		}
	
		protected static internal double ParseDouble(byte[] bytes) {
			if (bytes.Length != 8) {
				throw new ArgumentException("bad byte array length "
						+ bytes.Length);
			}
			return BitConverter.Int64BitsToDouble(ParseLong(bytes));
		}
	
		public NSNumber(byte[] bytes, int type_0) {
			switch (type_0) {
			case INTEGER: {
				doubleValue = longValue = ParseLong(bytes);
				break;
			}
			case REAL: {
				doubleValue = ParseDouble(bytes);
				longValue = (long) doubleValue;
				break;
			}
			default: {
				throw new ArgumentException("Type argument is not valid.");
			}
			}
			this.type = type_0;
		}
	
		public NSNumber(String text) {
			if (text.Equals("yes",StringComparison.InvariantCultureIgnoreCase) || text.Equals("true",StringComparison.InvariantCultureIgnoreCase)) {
				boolValue = true;
				doubleValue = longValue = 1;
				type = BOOLEAN;
				return;
			} else if (text.Equals("no",StringComparison.InvariantCultureIgnoreCase)
					|| text.Equals("false",StringComparison.InvariantCultureIgnoreCase)) {
				boolValue = false;
				doubleValue = longValue = 0;
				type = BOOLEAN;
				return;
			}
			if (!MathUtils.IsNan(text)) {
				throw new ArgumentException("[" + text
						+ "] value must be a boolean or numeric !");
			}
			if (StringUtils.IsAlphabetNumeric(text) && text.IndexOf('.') == -1) {
				long l = ((Int64 )Int64.Parse(text,System.Globalization.NumberStyles.Integer));
				doubleValue = longValue = l;
				type = INTEGER;
			} else if (StringUtils.IsAlphabetNumeric(text)
					&& text.IndexOf('.') != -1) {
				double d = ((Double )Double.Parse(text,JavaRuntime.NumberFormat));
				longValue = (long) (doubleValue = d);
				type = REAL;
			} else {
				try {
					long l_0 = ((Int64 )Int64.Parse(text,System.Globalization.NumberStyles.Integer));
					doubleValue = longValue = l_0;
					type = INTEGER;
				} catch (Exception) {
					try {
                        double d_1 = ((Double)Double.Parse(text, JavaRuntime.NumberFormat));
						longValue = (long) (doubleValue = d_1);
						type = REAL;
					} catch (Exception) {
						try {
							boolValue = Boolean.Parse(text);
							doubleValue = longValue = (boolValue) ? 1 : 0;
						} catch (Exception) {
							throw new ArgumentException(
									"Given text neither represents a double, int nor boolean value.");
						}
					}
				}
			}
		}
	
		public NSNumber(int i) {
			type = INTEGER;
			doubleValue = longValue = i;
		}
	
		public NSNumber(double d) {
			longValue = (long) (doubleValue = d);
			type = REAL;
		}
	
		public NSNumber(bool b) {
			boolValue = b;
			doubleValue = longValue = (b) ? 1 : 0;
			type = BOOLEAN;
		}
	
		public int Type() {
			return type;
		}
	
		public bool BooleanValue() {
			if (type == BOOLEAN) {
				return boolValue;
			} else {
				return longValue != 0;
			}
		}
	
		public long LongValue() {
			return longValue;
		}
	
		public int IntValue() {
			return (int) longValue;
		}
	
		public double DoubleValue() {
			return doubleValue;
		}
	
		public override bool Equals(Object obj) {
			return obj.GetType().Equals(typeof(NSNumber))
					&& obj.GetHashCode() == GetHashCode();
		}
	
		public override int GetHashCode() {
			int hash = 3;
			hash = 37 * hash + (int) (this.longValue ^ ((long) (((ulong) this.longValue) >> 32)));
			hash = 37
					* hash
                    + (int)(BitConverter.DoubleToInt64Bits(this.doubleValue) ^ ((long)(((ulong)BitConverter.DoubleToInt64Bits(this.doubleValue)) >> 32)));
			hash = 37 * hash + ((BooleanValue()) ? 1 : 0);
			return hash;
		}
	
		public override String ToString() {
			switch (type) {
			case INTEGER: {
				return LongValue().ToString();
			}
			case REAL: {
				return DoubleValue().ToString();
			}
			case BOOLEAN: {
				return BooleanValue().ToString();
			}
			default: {
				return base.ToString();
			}
			}
		}

        protected internal override void AddSequence(StringBuilder sbr, string indent)
        {
			sbr.Append(indent);
			switch (type) {
			case INTEGER: 
				sbr.Append("<integer>");
				sbr.Append(longValue.ToString());
				sbr.Append("</integer>");
				return;
			case REAL: 
				sbr.Append("<real>");
				sbr.Append(doubleValue.ToString());
				sbr.Append("</real>");
				return;
			case BOOLEAN: 
				if (boolValue) {
					sbr.Append("<true/>");
					return;
				} else {
					sbr.Append("<false/>");
					return;
				}
			default: 
				return;
			}
		}
	}
}
