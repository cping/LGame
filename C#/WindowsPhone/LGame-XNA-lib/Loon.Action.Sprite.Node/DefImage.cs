namespace Loon.Action.Sprite.Node
{
    using Loon.Core.Graphics;
    using Loon.Core.Geom;
    using System.Collections.Generic;
    using Loon.Utils;
    using System;

    public class DefImage : DefinitionObject
    {

        public LColor maskColor;

        public BlendState blend = BlendState.NonPremultiplied;

        public Vector2f anchor;

        public Vector2f place;

        public Vector2f pos;

        public Vector2f orig;

        public Vector2f size;

        public string uniqueID;

        public DefImage()
        {
        }

        public static DefImage Put(string uid, string name, Vector2f p, Vector2f s, Vector2f a, Vector2f pl)
        {
            return new DefImage(uid, name, p, s, a, pl);
        }

        internal DefImage(string uid, string name, Vector2f p, Vector2f o, Vector2f a, Vector2f pl)
        {
            this.uniqueID = uid;
            this.pos = p;
            this.orig = o;
            this.anchor = a;
            this.place = pl;
            this.fileName = name;
            LNDataCache.SetImage(this, this.uniqueID);
        }

        public override void DefinitionObjectDidFinishParsing()
        {
            base.DefinitionObjectDidFinishParsing();
            LNDataCache.SetImage(this, this.uniqueID);
        }

        public override void DefinitionObjectDidReceiveString(string v)
        {
            base.DefinitionObjectDidReceiveString(v);
            List<string> result = GetResult(v);
            foreach (string list in result)
            {
                if (list.Length > 2)
                {
                    string[] values = StringUtils.Split(list, "=");
                    string name = values[0];
                    string value = values[1];
                    if ("imageid".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.uniqueID = value;
                    }
                    else if ("pos".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.pos = DefinitionObject.StrToVector2(value);
                    }
                    else if ("orig".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.orig = DefinitionObject.StrToVector2(value);
                    }
                    else if ("size".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.size = DefinitionObject.StrToVector2(value);
                    }
                    else if ("anchor".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.anchor = DefinitionObject.StrToVector2(value);
                    }
                    else if ("place".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.place = DefinitionObject.StrToVector2(value);
                    }
                    else if ("file".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.fileName = value;
                    }
                    else if ("name".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.uniqueID = value;
                    }
                    else if ("id".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.uniqueID = value;
                    }
                    else if ("mask".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        string[] colors = StringUtils.Split(value, ",");
                        if (colors.Length == 3)
                        {
                            this.maskColor = new LColor(Convert.ToInt32(colors[0]), Convert.ToInt32(colors[1]), Convert.ToInt32(colors[2]));
                        }
                        else if (colors.Length == 4)
                        {
                            this.maskColor = new LColor(Convert.ToInt32(colors[0]), Convert.ToInt32(colors[1]), Convert.ToInt32(colors[2]), Convert.ToInt32(colors[4]));
                        }
                    }
                    else if ("blend".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        if ("non".Equals(value, System.StringComparison.InvariantCultureIgnoreCase) || "NonPremultiplied".Equals(value, System.StringComparison.InvariantCultureIgnoreCase))
                        {
                            blend = BlendState.NonPremultiplied;
                        }
                        else if ("add".Equals(value, System.StringComparison.InvariantCultureIgnoreCase) || "Additive".Equals(value, System.StringComparison.InvariantCultureIgnoreCase))
                        {
                            blend = BlendState.Additive;
                        }
                        else if ("alpha".Equals(value, System.StringComparison.InvariantCultureIgnoreCase) || "AlphaBlend".Equals(value, System.StringComparison.InvariantCultureIgnoreCase))
                        {
                            blend = BlendState.AlphaBlend;
                        }
                        else if ("op".Equals(value, System.StringComparison.InvariantCultureIgnoreCase) || "Opaque".Equals(value, System.StringComparison.InvariantCultureIgnoreCase))
                        {
                            blend = BlendState.Opaque;
                        }
                    }
                }
            }
            if (size == null && orig != null)
            {
                size = orig;
            }
            else if (orig == null && size != null)
            {
                orig = size;
            }
            if (anchor == null && size != null)
            {
                anchor = new Vector2f(size.x / 2, size.y / 2);
            }
            if (place == null)
            {
                place = new Vector2f();
            }
            result.Clear();
        }
    }

}