using Loon.Utils;
namespace Loon.Core.Resource {
	
	public class LPKHeader {
	
		public const int LF_PAK_ID = (('L' << 24) + ('G' << 16) + ('P' << 8) + 'K');
	
		public const int LF_PASSWORD_LENGTH = 10;
	
		public const int LF_FILE_LENGTH = 30;
	
		private int identity;
	
		private byte[] password;
	
		private float version;
	
		private long tables;
	
		public LPKHeader() {
			this.version = 1.0F;
			this.tables = 0;
			this.password = new byte[LPKHeader.LF_PASSWORD_LENGTH];
		}
	
		public LPKHeader(byte[] p, float version_1, long tables_2) {
			this.version = 1.0F;
			this.tables = 0;
			this.password = p;
			this.version = version_1;
			this.tables = tables_2;
		}
	
		public long GetTables() {
			return tables;
		}
	
		public void SetTables(long tables_0) {
			this.tables = tables_0;
		}
	
		public float GetVersion() {
			return version;
		}
	
		public void SetVersion(float version_0) {
			this.version = version_0;
		}
	
		public int GetPAKIdentity() {
			return LPKHeader.LF_PAK_ID;
		}
	
		public void SetPAKIdentity(int id) {
			this.identity = id;
		}
	
		public bool ValidatePAK() {
			return identity == LPKHeader.LF_PAK_ID;
		}
	
		public byte[] GetPassword() {
			return password;
		}
	
		public void SetPassword(long pass) {
            this.password = StringUtils.GetBytes(MathUtils.AddZeros(pass, LPKHeader.LF_PASSWORD_LENGTH));
		}
	
		public void SetPassword(byte[] p) {
			this.password = p;
		}
	
		public static int Size() {
			return 4 + LPKHeader.LF_PASSWORD_LENGTH + 4 + 4 + 1;
		}
	
	}
}
