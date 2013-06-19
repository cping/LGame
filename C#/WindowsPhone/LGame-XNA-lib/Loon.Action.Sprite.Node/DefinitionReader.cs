using System;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Utils.Xml;
using Loon.Java;

namespace Loon.Action.Sprite.Node
{

    public class DefinitionReader
    {

        private Type curClass;

        public const string flag_source = "src";

        public const string flag_type = "type";

        protected internal string _classType;

        protected internal string _source;

        public DefinitionObject currentDefinitionObject = null;

        private bool isCurrentElementDefined = false;

        private static DefinitionReader instance;

        private static readonly Dictionary<string, Type> change = new Dictionary<string, Type>(10);

        static DefinitionReader()
        {
            CollectionUtils.Put(change, "image", typeof(DefImage));
            CollectionUtils.Put(change, "animation", typeof(DefAnimation));
        }

        public static DefinitionReader Get()
        {
            lock (typeof(DefinitionReader))
            {
                if (instance == null)
                {
                    instance = new DefinitionReader();
                }
                return instance;
            }
        }

        private readonly DefListener listener;

        private DefinitionReader()
        {
            listener = new DefListener(this);
        }

        protected internal string _path;

        public virtual string GetCurrentPath()
        {
            return this._path;
        }

        private void StopElement(string name)
        {
            Type clazz = (Type)CollectionUtils.Get(change, name.ToLower());
            if (clazz != null && clazz.Equals(this.curClass))
            {
                this.currentDefinitionObject.DefinitionObjectDidFinishParsing();
                if (this.currentDefinitionObject.parentDefinitionObject != null)
                {
                    this.currentDefinitionObject.parentDefinitionObject.ChildDefinitionObjectDidFinishParsing(this.currentDefinitionObject);
                }
                this.currentDefinitionObject = this.currentDefinitionObject.parentDefinitionObject;
                if (this.currentDefinitionObject != null)
                {
                    this.curClass = this.currentDefinitionObject.GetType();
                    this.isCurrentElementDefined = true;
                }
                else
                {
                    this.curClass = null;
                    this.isCurrentElementDefined = false;
                }
            }
            else if (this.currentDefinitionObject != null)
            {
                this.currentDefinitionObject.UndefinedElementDidFinish(name);
            }
        }

        private void ParseContent(string str)
        {
            if (this.isCurrentElementDefined)
            {
                this.currentDefinitionObject.DefinitionObjectDidReceiveString(str);
            }
            else
            {
                this.currentDefinitionObject.UndefinedElementDidReceiveString(str);
            }
        }

        private class DefListener : XMLListener
        {

            private DefinitionReader _read;
            public DefListener(DefinitionReader read)
            {
                _read = read;
            }

            public virtual void AddHeader(int line, XMLProcessing xp)
            {

            }

            public virtual void AddData(int line, XMLData data)
            {
                if (data != null)
                {
                    string content = data.ToString().Trim();
                    if (!"".Equals(content))
                    {
                        _read.ParseContent(content);
                    }
                }
            }

            public virtual void AddComment(int line, XMLComment c)
            {

            }

            public virtual void AddAttribute(int line, XMLAttribute a)
            {
                if (a != null)
                {
                    XMLElement ele = a.GetElement();
                    if (flag_source.ToUpper() == a.GetName().ToUpper())
                    {
                        _read._source = a.GetValue();
                    }
                    else if (flag_type.ToUpper() == a.GetName().ToUpper())
                    {
                        _read._classType = a.GetValue();
                    }
                    else if (ele != null)
                    {
                        _read._classType = ele.GetName();
                    }
                }
            }

            public virtual void AddElement(int line, XMLElement e)
            {
                _read.StartElement(e != null ? e.GetName() : _read._classType);
            }

            public virtual void EndElement(int line, XMLElement e)
            {
                _read.StopElement(e != null ? e.GetName() : _read._classType);
            }
        }


        public virtual void Load(string resName)
        {
            this._path = resName;
            this._classType = null;
            this._source = null;
            this.currentDefinitionObject = null;
            this.isCurrentElementDefined = false;
            XMLParser.Parse(resName, listener);
        }

        public virtual void Load(InputStream res)
        {
            this._path = null;
            this._classType = null;
            this._source = null;
            this.currentDefinitionObject = null;
            this.isCurrentElementDefined = false;
            XMLParser.Parse(res, listener);
        }

        private void StartElement(string name)
        {
            
            Type clazz = (Type)CollectionUtils.Get(change,name.ToLower());
            if (clazz != null)
            {
                DefinitionObject childObject = null;
                try
                {
                    childObject = (DefinitionObject)JavaRuntime.NewInstance(clazz);
                    if (_source != null)
                    {
                        childObject.fileName = _source;
                    }
                }
                catch (Exception e)
                {
                    Loon.Utils.Debugging.Log.Exception(e);
                }
                if (this.isCurrentElementDefined)
                {
                    childObject.InitWithParentObject(this.currentDefinitionObject);
                }
                childObject.DefinitionObjectDidInit();
                if (childObject.parentDefinitionObject != null)
                {
                    childObject.parentDefinitionObject.ChildDefinitionObjectDidInit(childObject);
                }
                this.curClass = clazz;
                this.currentDefinitionObject = childObject;
                this.isCurrentElementDefined = true;
            }
            else
            {
                this.isCurrentElementDefined = false;
                if (this.currentDefinitionObject != null)
                {
                    this.currentDefinitionObject.UndefinedElementDidStart(name);
                }
            }
        }

    }

}