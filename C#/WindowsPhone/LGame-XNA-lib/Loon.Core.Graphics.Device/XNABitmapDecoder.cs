namespace Loon.Core.Graphics.Device
{
    using System;
    using System.IO;
    using Loon.Java;
    using Loon.Core.Resource;

    public class XNABitmapDecoder
    {

        private int width, height;

        private string format;

        public XNABitmapDecoder(string file)
            : this(Resources.OpenSource(file), file)
        {
            
        }

        public XNABitmapDecoder(InputStream ins)
            : this(ins, null)
        {
        }

        public XNABitmapDecoder(InputStream ins, string file)
        {
            this.width = 0;
            this.height = 0;
            this.format = null;
            try
            {
                int c1 = ins.Read();
                int c2 = ins.Read();
                int c3 = ins.Read();

                // GIF
                if (c1 == 'G' && c2 == 'I' && c3 == 'F')
                {
                    ins.Skip(3);
                    this.width = this.ReadInt(ins, 2, false);
                    this.height = this.ReadInt(ins, 2, false);
                    this.format = "gif";
                    // JPG
                }
                else if (c1 == 0xFF && c2 == 0xD8)
                {
                    while (c3 == 255)
                    {
                        int marker = ins.Read();
                        int len = this.ReadInt(ins, 2, true);
                        if (marker == 192 || marker == 193 || marker == 194)
                        {
                            ins.Skip(1);
                            this.height = this.ReadInt(ins, 2, true);
                            this.width = this.ReadInt(ins, 2, true);
                            this.format = "jpeg";
                            break;
                        }
                        ins.Skip(len - 2);
                        c3 = ins.Read();
                    }
                    // PNG
                }
                else if (c1 == 137 && c2 == 80 && c3 == 78)
                {
                    ins.Skip(15);
                    this.width = this.ReadInt(ins, 2, true);
                    ins.Skip(2);
                    this.height = this.ReadInt(ins, 2, true);
                    this.format = "png";
                    // BMP
                }
                else if (c1 == 66 && c2 == 77)
                {
                    ins.Skip(15);
                    this.width = this.ReadInt(ins, 2, false);
                    ins.Skip(2);
                    this.height = this.ReadInt(ins, 2, false);
                    this.format = "bmp";
                }
                else
                {
                    int c4 = ins.Read();
                    //TIFF
                    if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42) || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0))
                    {
                        bool bigEndian = c1 == 'M';
                        int ifd;
                        int entries;
                        int w = -1, h = -1;
                        ifd = this.ReadInt(ins, 4, bigEndian);
                        ins.Skip(ifd - 8);
                        entries = this.ReadInt(ins, 2, bigEndian);
                        for (int i = 1; i <= entries; i++)
                        {
                            int tag = this.ReadInt(ins, 2, bigEndian);
                            int fieldType = this.ReadInt(ins, 2, bigEndian);
                            long count = this.ReadInt(ins, 4, bigEndian);
                            int valOffset;
                            if ((fieldType == 3 || fieldType == 8))
                            {
                                valOffset = this.ReadInt(ins, 2, bigEndian);
                                ins.Skip(2);
                            }
                            else
                            {
                                valOffset = this.ReadInt(ins, 4, bigEndian);
                            }
                            if (tag == 256)
                            {
                                w = valOffset;
                            }
                            else if (tag == 257)
                            {
                                h = valOffset;
                            }
                            if (w != -1 && h != -1)
                            {
                                this.width = w;
                                this.height = h;
                                this.format = "tiff";
                                break;
                            }
                        }
                    }
                    else
                    {
                        this.format = null;
                        this.width = 0;
                        this.height = 0;
                    }
                }
                ins.Close();
            }
            catch (FileNotFoundException)
            {
                Loon.Utils.Debug.Log.DebugWrite("File not found:" + file);
            }
            catch (IOException)
            {
                Loon.Utils.Debug.Log.DebugWrite("Error on reading:" + file);
            }
            if (this.format == null)
            {
                Loon.Utils.Debug.Log.DebugWrite("Unsuported format !");
            }
        }

        private int ReadInt(InputStream ins, int noOfBytes, bool bigEndian)
        {
            int ret = 0;
            int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
            int cnt = bigEndian ? -8 : 8;
            for (int i = 0; i < noOfBytes; i++)
            {
                ret |= ins.Read() << sv;
                sv += cnt;
            }
            return ret;
        }

        public int GetWidth()
        {
            return this.width;
        }

        public int GetHeight()
        {
            return this.height;
        }

        public string GetFormat()
        {
            return this.format;
        }
    }
}
