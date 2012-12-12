namespace Loon.Java
{
    public class IntBuffer
    {

        private ByteBuffer data;
        private int pos;

        public IntBuffer(ByteBuffer src)
        {
            this.data = src;
            this.pos = 0;
        }

        public int Position()
        {
            return this.pos;
        }

        public void Position(int pos)
        {
            this.pos = pos;
        }

        public void Put(int value)
        {
            this.data.PutInt(this.pos++ * 4, value);
        }

        public void Put(uint[] value)
        {
            for (int i = 0; i < value.Length; i++)
            {
                this.data.PutInt(this.pos++ * 4, (int)value[i]);
            }
        }

    }
}
