#region LGame License
/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email£ºjavachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Loon.Java.Collections;
    using System.Collections;

    public abstract class AbstractMap<K, V> : IDictionary<K, V>, ICollection<KeyValuePair<K, V>>, IEnumerable<KeyValuePair<K, V>>, IEnumerable
    {

        protected Loon.Java.Generics.JavaListInterface.ISet<K> keySet;
        protected ICollection<V> valuesCollection;

        protected AbstractMap()
        {
        }

        public void Add(KeyValuePair<K, V> item)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual void Add(K key, V value)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual void Clear()
        {
            this.EntrySet().Clear();
        }

        protected object Clone()
        {
            throw new NotImplementedException("");
        }

        public bool Contains(KeyValuePair<K, V> item)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual bool ContainsKey(K key)
        {
            if (key != null)
            {
                foreach (KeyValuePair<K, V> pair in this.EntrySet())
                {
                    if (key.Equals(pair.Key))
                    {
                        return true;
                    }
                }
            }
            else
            {
                foreach (KeyValuePair<K, V> pair2 in this.EntrySet())
                {
                    if (pair2.Key == null)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public virtual bool ContainsValue(V value)
        {
            if (value != null)
            {
                foreach (KeyValuePair<K, V> pair in this.EntrySet())
                {
                    if (value.Equals(pair.Value))
                    {
                        return true;
                    }
                }
            }
            else
            {
                foreach (KeyValuePair<K, V> pair2 in this.EntrySet())
                {
                    if (pair2.Value == null)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public void CopyTo(KeyValuePair<K, V>[] array, int arrayIndex)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public abstract Loon.Java.Generics.JavaListInterface.ISet<KeyValuePair<K, V>> EntrySet();
        public override bool Equals(object obj)
        {
            if (this != obj)
            {
                if (!(obj is IDictionary<K, V>))
                {
                    return false;
                }
                IDictionary<K, V> dictionary = (IDictionary<K, V>)obj;
                if (this.Count != dictionary.Count)
                {
                    return false;
                }
                try
                {
                    foreach (KeyValuePair<K, V> pair in this.EntrySet())
                    {
                        K key = pair.Key;
                        V local2 = pair.Value;
                        V local3 = dictionary[key];
                        if (((local3 != null) && !local3.Equals(local2)) || (local3 == null))
                        {
                            return false;
                        }
                    }
                }
                catch (Exception)
                {
                    return false;
                }
            }
            return true;
        }

        public virtual V Get(object key)
        {
            this.EntrySet().GetEnumerator();
            if (key != null)
            {
                foreach (KeyValuePair<K, V> pair in this.EntrySet())
                {
                    if (key.Equals(pair.Key))
                    {
                        return pair.Value;
                    }
                }
            }
            else
            {
                foreach (KeyValuePair<K, V> pair2 in this.EntrySet())
                {
                    if (pair2.Key == null)
                    {
                        return pair2.Value;
                    }
                }
            }
            return default(V);
        }

        public IEnumerator<KeyValuePair<K, V>> GetEnumerator()
        {
            return this.EntrySet().GetEnumerator();
        }

        public override int GetHashCode()
        {
            int num = 0;
            foreach (KeyValuePair<K, V> pair in this.EntrySet())
            {
                num += pair.GetHashCode();
            }
            return num;
        }

        public virtual bool IsEmpty()
        {
            return (this.Count == 0);
        }

        public virtual Loon.Java.Generics.JavaListInterface.ISet<K> KeySet()
        {
            if (this.keySet == null)
            {
                this.keySet = new AnonymousSet((AbstractMap<K, V>)this);
            }
            return this.keySet;
        }

        public virtual V Put(K key, V value)
        {
            throw new NotSupportedException();
        }

        public virtual void PutAll(IDictionary<K, V> map)
        {
            foreach (KeyValuePair<K, V> pair in map)
            {
                this[pair.Key] = pair.Value;
            }
        }

        public virtual bool Remove(K key)
        {
            if (key != null)
            {
                foreach (KeyValuePair<K, V> pair in this.EntrySet())
                {
                    if (key.Equals(pair.Key))
                    {
                        throw new NotImplementedException("");
                    }
                }
            }
            else
            {
                foreach (KeyValuePair<K, V> pair2 in this.EntrySet())
                {
                    if (pair2.Key == null)
                    {
                        throw new NotImplementedException("");
                    }
                }
            }
            throw new NotImplementedException("");
        }

        public virtual bool Remove(KeyValuePair<K, V> item)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return this.EntrySet().GetEnumerator();
        }

        public override string ToString()
        {
            if (this.IsEmpty())
            {
                return "{}";
            }
            StringBuilder builder = new StringBuilder(this.Count * 0x1c);
            builder.Append('{');
            foreach (KeyValuePair<K, V> pair in this.EntrySet())
            {
                object key = pair.Key;
                if (key != this)
                {
                    builder.Append(key);
                }
                else
                {
                    builder.Append("(this Map)");
                }
                builder.Append('=');
                object obj3 = pair.Value;
                if (obj3 != this)
                {
                    builder.Append(obj3);
                }
                else
                {
                    builder.Append("(this Map)");
                }
            }
            builder.Append('}');
            return builder.ToString();
        }

        public virtual bool TryGetValue(K key, out V value)
        {
            try
            {
                value = this[key];
                return true;
            }
            catch (Exception)
            {
                value = default(V);
                return false;
            }
        }

        public virtual ICollection<V> Values()
        {
            ICollection<V> valuesCollection = this.valuesCollection;
            return this.valuesCollection;
        }

        // Properties
        public virtual int Count
        {
            get
            {
                return this.EntrySet().Count;
            }
        }

        public bool IsReadOnly
        {
            get
            {
                throw new Exception("The method or operation is not implemented.");
            }
        }

        public virtual V this[K key]
        {
            get
            {
                return this.Get(key);
            }
            set
            {
                this.Put(key, value);
            }
        }

        public virtual ICollection<K> Keys
        {
            get
            {
                throw new Exception("The method or operation is not implemented.");
            }
            set
            {
                throw new Exception("The method or operation is not implemented.");
            }
        }

        ICollection<V> IDictionary<K, V>.Values
        {
            get
            {
                throw new Exception("The method or operation is not implemented.");
            }
        }

        public class AnonymousSet : AbstractSet<K>
        {

            private AbstractMap<K, V> enclosing;

            public AnonymousSet(AbstractMap<K, V> enclosing)
            {
                this.enclosing = enclosing;
            }

            public bool Contains(object obj)
            {
                return this.enclosing.ContainsKey((K)obj);
            }

            public override Loon.Java.Generics.JavaListInterface.IIterator<K> Iterator()
            {
                return new AnonymousIterator(this.enclosing);
            }

            public override int Count
            {
                get
                {
                    return this.enclosing.Count;
                }
            }

            public class AnonymousIterator : Loon.Java.Generics.JavaListInterface.IIterator<K>, IIterator
            {

                private Loon.Java.Generics.JavaListInterface.IIterator<KeyValuePair<K, V>> setIterator;

                public AnonymousIterator(AbstractMap<K, V> enclosing)
                {
                    this.setIterator = new IteratorAdapter<KeyValuePair<K, V>>(enclosing.EntrySet().GetEnumerator());
                }

                public bool HasNext()
                {
                    return this.setIterator.HasNext();
                }

                bool IIterator.HasNext()
                {
                    throw new NotImplementedException();
                }

                object IIterator.Next()
                {
                    throw new NotImplementedException();
                }

                void IIterator.Remove()
                {
                    throw new NotImplementedException();
                }

                public K Next()
                {
                    return this.setIterator.Next().Key;
                }

                public void Remove()
                {
                    this.setIterator.Remove();
                }
            }
        }


    }


}
