using System;
using System.Collections.Generic;
using Loon.Utils.Xml;
using Loon.Utils;

namespace Loon.Action.Map.Tmx
{
    public class TMXTileGroup
    {

        public int index;

        public string name;

        public List<TMXTile> objects;

        public int width;

        public int height;

        public TMXProperty props;

        public TMXTileGroup(XMLElement element)
        {
            name = element.GetAttribute("name", null);
            width = element.GetIntAttribute("width", 0);
            height = element.GetIntAttribute("height", 0);
            objects = new List<TMXTile>();

            XMLElement propsElement = element.GetChildrenByName("properties");
            if (propsElement != null)
            {
                List<XMLElement> properties = propsElement.List("property");
                if (properties != null)
                {
                    props = new TMXProperty();
                    for (int p = 0; p < properties.Count; p++)
                    {
                        XMLElement propElement = properties[p];
                        string name_0 = propElement.GetAttribute("name", null);
                        string value_ren = propElement.GetAttribute("value", null);
                        props.SetProperty(name_0, value_ren);
                    }
                }
            }
            List<XMLElement> objectNodes = element.List("object");
            for (int i = 0; i < objectNodes.Count; i++)
            {
                XMLElement objElement = objectNodes[i];
                TMXTile obj0 = new TMXTile(objElement);
                obj0.index = i;
                CollectionUtils.Add(objects, obj0);
            }
        }
    }
}
