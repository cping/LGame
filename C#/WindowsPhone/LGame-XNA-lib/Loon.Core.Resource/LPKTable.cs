using Loon.Utils;
namespace Loon.Core.Resource {
	
	public class LPKTable {
	
		private string fileName;
	
		private long fileSize;
	
		private long offSet;
	
		public LPKTable() {
			this.fileSize = 0x0L;
			this.offSet = 0x0L;
			this.fileName = null;
		}
	
		public LPKTable(byte[] path, long fileSize_1, long offSet_2) {
			this.fileSize = 0x0L;
			this.offSet = 0x0L;
            this.fileName = StringUtils.NewString(path).Trim();
			this.fileSize = fileSize_1;
			this.offSet = offSet_2;
		}
	
		public string GetFileName() {
			return fileName;
		}
	
		public void SetFileName(byte[] bytes) {
            this.fileName = StringUtils.NewString(bytes).Trim();
		}
	
		public long GetFileSize() {
			return fileSize;
		}
	
		public void SetFileSize(long fileSize_0) {
			this.fileSize = fileSize_0;
		}
	
		public long GetOffSet() {
			return offSet;
		}
	
		public void SetOffSet(long offSet_0) {
			this.offSet = offSet_0;
		}
	
		public static int Size() {
			return LPKHeader.LF_FILE_LENGTH + 4 + 4;
		}
	
	}
}
