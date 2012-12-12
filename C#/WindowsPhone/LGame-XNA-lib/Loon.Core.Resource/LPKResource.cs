using Loon.Utils;
using Loon.Utils.Collection;
using System.IO;
using System;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Jni;
namespace Loon.Core.Resource {

	public abstract class LPKResource {
	
		public static bool CACHE = false;
	
		private static System.Collections.Generic.Dictionary<string, PAK> pakRes = new System.Collections.Generic.Dictionary<string, PAK>(
				CollectionUtils.INITIAL_CAPACITY);
	
		private static System.Collections.Generic.Dictionary<string, ArrayByte> cacheRes;
	
		public class PAK {
	
			public LPKTable[] tables;
	
			public int head_size = 0;
	
			public int skip;
	
			public int length;
	
		}
	
		public static void FreeCache() {
			if (cacheRes != null) {
				cacheRes.Clear();
			}
		}
	
		public static System.Collections.Generic.Dictionary<string, PAK> MAP() {
			return pakRes;
		}
	
		public static byte[] OpenResource(string fileName, string resName) {
			try {
				PAK pak = (PAK)CollectionUtils.Get(pakRes,fileName);
				Stream ins = Resources.OpenStream(fileName);
             
				ArrayByte result = null;
	
				if (CACHE) {
					if (cacheRes == null) {
						cacheRes = new System.Collections.Generic.Dictionary<string, ArrayByte>(
								CollectionUtils.INITIAL_CAPACITY);
					}
					result = (ArrayByte)CollectionUtils.Get(cacheRes,fileName);
					if (result == null) {
						result = new ArrayByte(ins, ArrayByte.LITTLE_ENDIAN);
                        CollectionUtils.Put(cacheRes,fileName, result);
					} else {
						result.Reset(ArrayByte.LITTLE_ENDIAN);
					}
				} else {
					result = new ArrayByte(ins, ArrayByte.LITTLE_ENDIAN);
				}
    
				if (pak == null) {
					pak = new PAK();
					LPKHeader header = ReadHeader(result);
					pak.tables = ReadLPKTable(result, (int) header.GetTables());
					pak.head_size = (int) (LPKHeader.Size() + header.GetTables()
							* LPKTable.Size());
					pak.skip = result.Position();
					pak.length = result.Length();
					CollectionUtils.Put(pakRes,fileName, pak);
				} else {
					result.SetPosition(pak.skip);
				}
	
				bool find = false;
				int fileIndex = 0;
				string innerName = null;
				LPKTable[] tables_0 = pak.tables;
				int size = tables_0.Length;
	
				for (int i = 0; i < size; i++) {
					innerName = tables_0[i].GetFileName();
					if (resName.Equals(innerName,System.StringComparison.InvariantCultureIgnoreCase)) {
						find = true;
						fileIndex = i;
						break;
					}
				}
	
				if (!find) {
					throw new Exception("File not found. ( " + fileName
							+ " )");
				} else {
					return ReadFileFromPak(result, pak.head_size, tables_0[fileIndex]);
				}
			} catch (Exception) {
				throw new Exception("File not found. ( " + fileName + " )");
			}
		}

        public static Stream OpenStream(string fileName, string resName)
        {
            byte[] bytes = OpenResource(fileName, resName);
            MemoryStream byteArrayOutputStream = new MemoryStream(bytes);
            return byteArrayOutputStream;
        }
	
		public static LImage OpenImage(string fileName, string resName) {
			byte[] buffer = null;
			try {
				buffer = LPKResource.OpenResource(fileName, resName);
          
				return LImage.CreateImage(buffer);
			} catch (Exception) {
				throw new Exception("File not found. ( " + resName + " )");
			}
		}
	
		public static LTexture OpenTexture(string fileName, string resName) {
			try {
				LImage image = OpenImage(fileName, resName);
				image.SetAutoDispose(true);
				return image.GetTexture();
			} catch (Exception) {
				throw new Exception("File not found. ( " + resName + " )");
			}
		}
	
		public static LPKHeader ReadHeader(ArrayByte dis) {
			LPKHeader header = new LPKHeader();
			header.SetPAKIdentity(dis.ReadInt());
			byte[] pass = dis.ReadByteArray(LPKHeader.LF_PASSWORD_LENGTH);
			header.SetPassword(pass);
			header.SetVersion(dis.ReadFloat());
			header.SetTables(dis.ReadLong());
			return header;
		}
	
		public static LPKTable[] ReadLPKTable(ArrayByte dis, int fileTableNumber) {
			LPKTable[] fileTable = new LPKTable[fileTableNumber];
			for (int i = 0; i < fileTableNumber; i++) {
				LPKTable ft = new LPKTable();
				ft.SetFileName(dis.ReadByteArray(LPKHeader.LF_FILE_LENGTH));
				ft.SetFileSize(dis.ReadLong());
				ft.SetOffSet(dis.ReadLong());
				fileTable[i] = ft;
			}
			return fileTable;
		}
	

		public static byte[] ReadFileFromPak(ArrayByte dis, int size,
				LPKTable fileTable) {
			dis.Skip(fileTable.GetOffSet() - size);
			int fileLength = (int) fileTable.GetFileSize();
			byte[] fileBuff = new byte[fileLength];
			int readLength = dis.Read(fileBuff, 0, fileLength);
			if (readLength < fileLength) {
				return null;
			} else {
				NativeSupport.MakeBuffer(fileBuff, readLength, 0xF7);
				return fileBuff;
			}
		}
	
	}
}
