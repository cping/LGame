using Loon.Net;
using Loon.Utils;
using Loon.Core;
using System.IO;
using System.Text;
namespace Loon.Foundation {
	
	public class NSData : NSObject {
	
		private byte[] bytes;
	
		public NSData(byte[] b) {
			if (Base64Coder.IsArrayByteBase64(b)) {
				bytes = Base64Coder.Decode(b);
			} else {
				this.bytes = b;
			}
		}
	
		public NSData(string base64) {
			string data = "";
			if (Base64Coder.IsBase64(base64)) {
				foreach (string line  in  Loon.Utils.StringUtils.Split(base64, "\n")) {
					data += line.Trim();
				}
				char[] enc = data.ToCharArray();
				bytes = Base64Coder.DecodeBase64(enc);
			} else {
                this.bytes = Loon.Utils.StringUtils.GetBytes(base64);
			}
		}
	
		public byte[] Bytes() {
			return bytes;
		}
	
		public int Length() {
			return bytes.Length;
		}

        public void GetBytes(Loon.Java.ByteBuffer buf, int length)
        {
            sbyte[] buffer = new sbyte[bytes.Length];
            for (int i = 0; i < buffer.Length; i++)
            {
                buffer[i] = (sbyte)bytes[i];
            }
            buf.Put(buffer, 0, MathUtils.Min(bytes.Length, length));
        }

        public void GetBytes(Loon.Java.ByteBuffer buf, int rangeStart, int rangeEnd)
        {
            sbyte[] buffer = new sbyte[bytes.Length];
            for (int i = 0; i < buffer.Length; i++)
            {
                buffer[i] = (sbyte)bytes[i];
            }
            buf.Put(buffer, rangeStart, MathUtils.Min(bytes.Length, rangeEnd));
        }
	
		public string GetBase64() {
			byte[] buffer = Base64Coder.Encode(bytes);
			try {
				return StringUtils.GetString(buffer);
			} catch (IOException) {
                return StringUtils.GetString(buffer);
			}
		}
	
		public string GetString() {
            return GetString(System.Text.Encoding.UTF8);
		}
	
		public string GetString(System.Text.Encoding format) {
			byte[] buffer = this.bytes;
			try {
				return StringUtils.GetString(buffer,format);
			} catch (IOException) {
                return StringUtils.GetString(buffer);
			}
		}

        public override bool Equals(object obj)
        {
            return obj.GetType().Equals(GetType())
                    && CollectionUtils.Equals(((NSData)obj).bytes, bytes);
        }
	
		public override int GetHashCode() {
			int hash = 5;
			hash = 67 * hash + this.bytes.GetHashCode();
			return hash;
		}
	
		protected internal override void AddSequence(StringBuilder sbr, string indent) {
			sbr.Append(indent);
			sbr.Append("<data>");
			sbr.Append(LSystem.LS);
			string base64 = GetBase64();
			foreach (string line  in  StringUtils.Split(base64, LSystem.LS)) {
				sbr.Append(indent);
				sbr.Append("  ");
				sbr.Append(line);
				sbr.Append(LSystem.LS);
			}
			sbr.Append(indent);
			sbr.Append("</data>");
		}
	
	}
}
