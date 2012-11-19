namespace Loon.Utils.Xml
{
    using System;
    using Loon.Java;
    using Loon.Core;
    using Loon.Utils.Debug;
   
    public class XMLAttribute
    {

        private string name;

        private string value_ren;

        internal XMLElement element;

        public XMLElement GetElement()
        {
            return element;
        }

        public string GetValue()
        {
            return this.value_ren;
        }

        internal XMLAttribute(string n, string v)
        {
            this.name = n;
            this.value_ren = v;
        }

        public int GetIntValue()
        {
            try
            {
                return Int32.Parse(this.value_ren);
            }
            catch (Exception ex)
            {
                Log.Exception(ex);
                throw new Exception("Attribute '" + this.name
                        + "' has value '" + this.value_ren
                        + "' which is not an integer !");
            }
        }

        public float GetFloatValue()
        {
            try
            {
                return Single.Parse(this.value_ren, JavaRuntime.NumberFormat);
            }
            catch (Exception ex)
            {
                Log.Exception(ex);
                throw new Exception("Attribute '" + this.name
                        + "' has value '" + this.value_ren
                        + "' which is not an float !");
            }
        }

        public double GetDoubleValue()
        {
            try
            {
                return ((Double)Double.Parse(this.value_ren, JavaRuntime.NumberFormat));
            }
            catch (Exception ex)
            {
                Log.Exception(ex);
                throw new Exception("Attribute '" + this.name
                        + "' has value '" + this.value_ren
                        + "' which is not an double !");
            }
        }

        public bool GetBoolValue()
        {
            if (value_ren == null)
            {
                return false;
            }
            return "true".Equals(value_ren, StringComparison.InvariantCultureIgnoreCase) || "yes".Equals(value_ren, StringComparison.InvariantCultureIgnoreCase);
        }

        public string GetName()
        {
            return this.name;
        }

    }
}
