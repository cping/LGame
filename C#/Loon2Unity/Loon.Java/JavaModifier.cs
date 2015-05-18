namespace Loon.Java
{
    using System;
    using System.Reflection;
    
    public class JavaModifier
    {

        public const int ABSTRACT = 0x20;
        public const int BRIDGE = 0x200000;
        public const int CLASS = 0x200;
        public const int CONST = 0x800000;
        public const int EXTERN = 0x100000;
        public const int FINAL = 0x10;
        public const int INTERFACE = 0x100;
        public const int INTERNAL = 0x80000;
        private int modifiers = 0;
        private string name = "";
        public const int NATIVE = 0x400;
        public const int NEW = 0x40000;
        public const int OVERRIDE = 0x8000;
        public const int PACKAGE = 0x80;
        public const int PRIVATE = 2;
        public const int PROTECTED = 4;
        public const int PUBLIC = 8;
        public const int READONLY = 0x20000;
        public const int SEALED = 0x10000;
        public const int STATIC = 1;
        public const int STRICT = 0x800;
        public const int SYNCHRONIZED = 0x40;
        public const int SYNTHETIC = 0x400000;
        public const int TRANSIENT = 0x1000;
        public const int VARARGS = 0x1000000;
        public const int VIRTUAL = 0x4000;
        public const int VOLATILE = 0x2000;

        public JavaModifier(ConstructorInfo info)
        {
            MethodAttributes attr = info.Attributes;
            this.name = info.Name;
            bool varArgs = this.IsVarargs(info);
            this.InitModifiers(varArgs, attr);
        }

        public JavaModifier(FieldInfo info)
        {
            FieldAttributes attr = info.Attributes;
            this.name = info.Name;
            this.InitModifiers(attr);
        }

        public JavaModifier(MethodInfo info)
        {
            MethodAttributes attr = info.Attributes;
            this.name = info.Name;
            bool varArgs = this.IsVarargs(info);
            this.InitModifiers(varArgs, attr);
        }

        public JavaModifier(Type info)
        {
            TypeAttributes attr = info.Attributes;
            this.name = info.Name;
            this.InitModifiers(attr);
            if (info.IsInterface)
            {
                this.modifiers |= 0x100;
            }
        }

        public int GetModifiers()
        {
            return this.modifiers;
        }

        private void InitModifiers(FieldAttributes attr)
        {
            if ((attr & FieldAttributes.Public) == FieldAttributes.Public)
            {
                this.modifiers |= 8;
            }
            if ((attr & FieldAttributes.Static) == FieldAttributes.Static)
            {
                this.modifiers |= 1;
            }
            if ((attr & FieldAttributes.Private) == FieldAttributes.Private)
            {
                this.modifiers |= 2;
            }
            if ((attr & FieldAttributes.Assembly) == FieldAttributes.Assembly)
            {
                this.modifiers |= 0x80;
            }
            if ((attr & FieldAttributes.Literal) == FieldAttributes.Literal)
            {
                this.modifiers |= 0x800000;
            }
        }

        private void InitModifiers(TypeAttributes attr)
        {
            if ((attr & TypeAttributes.Public) == TypeAttributes.Public)
            {
                this.modifiers |= 8;
            }
            if ((attr & TypeAttributes.Interface) == TypeAttributes.Interface)
            {
                this.modifiers |= 0x100;
            }
            if ((attr & TypeAttributes.Abstract) == TypeAttributes.Abstract)
            {
                this.modifiers |= 0x20;
            }
        }

        private void InitModifiers(bool varArgs, MethodAttributes attr)
        {
            if ((attr & MethodAttributes.Abstract) == MethodAttributes.Abstract)
            {
                this.modifiers |= 0x20;
            }
            if ((attr & MethodAttributes.Static) == MethodAttributes.Static)
            {
                this.modifiers |= 1;
            }
            if (varArgs)
            {
                this.modifiers |= 0x1000000;
            }
            if ((attr & MethodAttributes.Public) == MethodAttributes.Public)
            {
                this.modifiers |= 8;
            }
            else if ((attr & MethodAttributes.Assembly) == MethodAttributes.Assembly)
            {
                this.modifiers |= 0x80;
            }
            else if ((attr & MethodAttributes.Private) == MethodAttributes.Private)
            {
                this.modifiers |= 2;
            }
        }

		public string GetName(){
			return this.name;
		}

        public static bool IsAbstract(int mod)
        {
            return ((mod & 0x20) == 0x20);
        }

        public static bool IsConst(int mod)
        {
            return ((mod & 0x800000) == 0x800000);
        }

        public static bool IsFinal(int mod)
        {
            return ((mod & 0x10) == 0x10);
        }

        public static bool IsInterface(int mod)
        {
            return ((mod & 0x100) == 0x100);
        }

        public static bool IsPrivate(int mod)
        {
            return ((mod & 2) == 2);
        }

        public static bool IsProtected(int mod)
        {
            return ((mod & 4) == 4);
        }

        public static bool IsPublic(int mod)
        {
            return ((mod & 8) == 8);
        }

        public static bool IsStatic(int mod)
        {
            return ((mod & 1) == 1);
        }

        public static bool IsSynchronized(int mod)
        {
            return ((mod & 0x40) == 0x40);
        }

        public static bool IsTransient(int mod)
        {
            return ((mod & 0x1000) == 0x1000);
        }

        public static bool IsVarargs(int mod)
        {
            return ((mod & 0x1000000) == 0x1000000);
        }

        private bool IsVarargs(ConstructorInfo info)
        {
            int length = info.GetParameters().Length;
            if (length >= 1)
            {
                ParameterInfo info2 = info.GetParameters()[length - 1];
                ParameterAttributes attributes = info2.Attributes;
                object[] customAttributes = info2.GetCustomAttributes(true);
                if ((customAttributes != null) && (customAttributes.Length > 0))
                {
                    foreach (object obj2 in customAttributes)
                    {
                        if (obj2 is ParamArrayAttribute)
                        {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private bool IsVarargs(MethodInfo info)
        {
            int length = info.GetParameters().Length;
            if (length >= 1)
            {
                ParameterInfo info2 = info.GetParameters()[length - 1];
                ParameterAttributes attributes = info2.Attributes;
                object[] customAttributes = info2.GetCustomAttributes(true);
                if ((customAttributes != null) && (customAttributes.Length > 0))
                {
                    foreach (object obj2 in customAttributes)
                    {
                        if (obj2 is ParamArrayAttribute)
                        {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public static bool IsVolatile(int mod)
        {
            return ((mod & 0x2000) == 0x2000);
        }

    }
}
