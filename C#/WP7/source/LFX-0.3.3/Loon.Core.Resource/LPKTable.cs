using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Resource
{
    public class LPKTable
    {

        private byte[] fileName;

        private long fileSize;

        private long offSet;

        public LPKTable()
        {
            this.fileSize = 0x0L;
            this.offSet = 0x0L;
            this.fileName = new byte[LPKHeader.LF_FILE_LENGTH];
        }

        public LPKTable(byte[] fileName_0, long s1, long s2)
        {
            this.fileSize = 0x0L;
            this.offSet = 0x0L;
            this.fileName = new byte[LPKHeader.LF_FILE_LENGTH];
            for (int i = 0; i < LPKHeader.LF_FILE_LENGTH; this.fileName[i] = fileName_0[i], i++)
                ;
            this.fileSize = s1;
            this.offSet = s2;
        }

        public byte[] GetFileName()
        {
            return fileName;
        }

        public void SetFileName(byte[] b)
        {
            for (int i = 0; i < b.Length; this.fileName[i] = b[i], i++)
                ;
        }

        public long GetFileSize()
        {
            return fileSize;
        }

        public void SetFileSize(long s)
        {
            this.fileSize = s;
        }

        public long GetOffSet()
        {
            return offSet;
        }

        public void SetOffSet(long s)
        {
            this.offSet = s;
        }

        public static int Size()
        {
            return LPKHeader.LF_FILE_LENGTH + 4 + 4;
        }
    }
}
