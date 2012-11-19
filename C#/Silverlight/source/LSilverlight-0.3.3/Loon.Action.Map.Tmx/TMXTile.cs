using System.Collections.Generic;
using Loon.Utils.Xml;

namespace Loon.Action.Map.Tmx
{
    public class TMXTile
    {

        public int index;

        public string name;

        public string type;

        public int x;

        public int y;

        public int width;

        public int height;

        internal string image;

        public TMXProperty props;

        public TMXTile(XMLElement element)
        {
            name = element.GetAttribute("name", "");
            type = element.GetAttribute("type", "");
            x = element.GetIntAttribute("x", 0);
            y = element.GetIntAttribute("y", 0);
            string w = element.GetAttribute("width", null);
            string h = element.GetAttribute("height", null);
            width = System.Int32.Parse((w == null || "".Equals(w)) ? "0" : w);
            height = System.Int32.Parse((h == null || "".Equals(h)) ? "0" : h);
            XMLElement imageElement = (XMLElement)element
                    .GetChildrenByName("image");
            if (imageElement != null)
            {
                image = imageElement.GetAttribute("source", null);
            }
            XMLElement propsElement = (XMLElement)element
                    .GetChildrenByName("properties");
            if (propsElement != null)
            {
                props = new TMXProperty();
                List<XMLElement> property = propsElement.List("property");
                for (int i = 0; i < property.Count; i++)
                {
                    XMLElement propElement = property[i];
                    string name_0 = propElement.GetAttribute("name", null);
                    string value_ren = propElement.GetAttribute("value", null);
                    props.SetProperty(name_0, value_ren);
                }

            }
        }
    }
}
