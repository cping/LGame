namespace loon.utils.json
{

    internal class JsonStringTypedArray : TArray<string>, Json_TypedArray<string>
    {

        public JsonStringTypedArray(params string[] contents) : base(contents)
        {
        }

        public JsonStringTypedArray(loon.utils.ObjectMap<string, object>.Keys<string> keys) : base(keys)
        {
        }

        public virtual int Length()
        {
            return size;
        }

        public virtual string Get(int index, string dflt)
        {
            string s = Get(index);
            if (s == null)
            {
                return dflt;
            }

            return s;
        }
    }
}
