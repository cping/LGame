namespace Loon.Action.Sprite
{
    using System;
    using System.Collections;
    using System.ComponentModel;
    using System.IO;
    using System.Reflection;
    using System.Runtime.CompilerServices;
    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Core.Graphics.Component;
    using Loon.Utils;
    using Loon.Java;
    using Loon.Utils.Debug;

    public sealed class Bind
    {

        private Object obj;

        private static Dictionary<Type, BindObject> methodList;

        private bool isBindPos, isBindGetPos, isBindRotation, isBindGetRotation,
                isBindUpdate, isBindScale, isBindSize;

        private MethodInfo[] methods;

        private int type;

        private Actor actorObject;

        private LComponent compObject;

        private Shape shapeObject;

        private LObject lObject;

        internal class BindObject
        {

            internal bool bindPos, bindGetPos, bindRotation, bindGetRotation, bindUpdate,
                    bindScale, bindSize;

            internal MethodInfo[] methods;

            public BindObject(MethodInfo[] m)
            {
                this.methods = m;
            }
        }

        public Bind(Object o)
        {
            if (o is Actor)
            {
                type = 1;
                actorObject = (Actor)o;
                this.isBindPos = true;
                this.isBindGetPos = true;
                this.isBindRotation = true;
                this.isBindGetRotation = true;
                this.isBindUpdate = true;
                this.isBindScale = true;
                this.isBindSize = true;
            }
            else if (o is Shape)
            {
                type = 2;
                shapeObject = (Shape)o;
                this.isBindPos = true;
                this.isBindGetPos = true;
                this.isBindRotation = true;
                this.isBindGetRotation = true;
                this.isBindUpdate = false;
                this.isBindScale = true;
                this.isBindSize = true;
            }
            else if (o is LComponent)
            {
                type = 3;
                compObject = (LComponent)o;
                this.isBindPos = true;
                this.isBindGetPos = true;
                this.isBindRotation = false;
                this.isBindGetRotation = false;
                this.isBindUpdate = true;
                this.isBindScale = false;
                this.isBindSize = true;
            }
            else if (o is LObject)
            {
                type = 4;
                lObject = (LObject)o;
                this.isBindPos = true;
                this.isBindGetPos = true;
                this.isBindRotation = true;
                this.isBindGetRotation = true;
                this.isBindUpdate = true;
                this.isBindScale = false;
                this.isBindSize = true;
            }
            else
            {
                type = 0;
                Bind.BindObject obj0 = BindClass(this.obj = o);
                this.methods = obj0.methods;
                this.isBindPos = obj0.bindPos;
                this.isBindGetPos = obj0.bindGetPos;
                this.isBindRotation = obj0.bindRotation;
                this.isBindGetRotation = obj0.bindGetRotation;
                this.isBindUpdate = obj0.bindUpdate;
                this.isBindScale = obj0.bindScale;
                this.isBindSize = obj0.bindSize;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private static Bind.BindObject BindClass(Object o)
        {

            Bind.BindObject result;
            Type clazz = o.GetType();

            if (methodList == null)
            {
                methodList = new Dictionary<Type, BindObject>(10);
            }
            result = (BindObject)CollectionUtils.Get(methodList, clazz);

            if (result == null)
            {
                result = new Bind.BindObject(new MethodInfo[11]);
                try
                {
                    MethodInfo setX = JavaRuntime.GetMethod(clazz, "SetX", typeof(float));
                    MethodInfo setY = JavaRuntime.GetMethod(clazz, "SetY", typeof(float));
                    result.methods[0] = setX;
                    result.methods[1] = setY;
                    result.bindPos = true;

                }
                catch (Exception e)
                {
                    Log.Exception(e);
                    result.bindPos = false;
                }
                if (result.bindPos)
                {
                    try
                    {
                        MethodInfo getX = JavaRuntime.GetMethod(clazz, "GetX");
                        MethodInfo getY = JavaRuntime.GetMethod(clazz, "GetY");
                        result.methods[7] = getX;
                        result.methods[8] = getY;
                        result.bindGetPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindGetPos = false;
                    }
                }
                if (result.bindPos && !result.bindGetPos)
                {
                    try
                    {
                        MethodInfo getX_1 = JavaRuntime.GetMethod(clazz, "X");
                        MethodInfo getY_2 = JavaRuntime.GetMethod(clazz, "Y");
                        result.methods[7] = getX_1;
                        result.methods[8] = getY_2;
                        result.bindGetPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindGetPos = false;
                    }
                }
                if (!result.bindPos)
                {
                    try
                    {
                        MethodInfo setX_4 = JavaRuntime.GetMethod(clazz, "SetX", typeof(int));
                        MethodInfo setY_5 = JavaRuntime.GetMethod(clazz, "SetY", typeof(int));
                        result.methods[0] = setX_4;
                        result.methods[1] = setY_5;
                        result.bindPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindPos = false;
                    }
                }
                if (!result.bindPos)
                {
                    try
                    {
                        MethodInfo location = JavaRuntime.GetMethod(clazz, "SetLocation", typeof(float));
                        result.methods[0] = location;
                        result.bindPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindPos = false;
                    }
                }
                if (!result.bindPos)
                {
                    try
                    {
                        MethodInfo location_8 = JavaRuntime.GetMethod(clazz, "SetLocation", typeof(int));
                        result.methods[0] = location_8;
                        result.bindPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindPos = false;
                    }
                }
                if (!result.bindPos)
                {
                    try
                    {
                        MethodInfo location_10 = JavaRuntime.GetMethod(clazz, "SetPosition", typeof(float));
                        result.methods[0] = location_10;
                        result.bindPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindPos = false;
                    }
                }
                if (!result.bindPos)
                {
                    try
                    {
                        MethodInfo location_12 = JavaRuntime.GetMethod(clazz, "SetPosition", typeof(int));
                        result.methods[0] = location_12;
                        result.bindPos = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindPos = false;
                    }
                }
                try
                {
                    MethodInfo rotation = JavaRuntime.GetMethod(clazz, "SetRotation", typeof(float));
                    result.methods[2] = rotation;
                    result.bindRotation = true;
                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                    result.bindRotation = false;
                }
                if (result.bindRotation)
                {
                    try
                    {
                        MethodInfo getRotation = JavaRuntime.GetMethod(clazz, "GetRotation");
                        result.methods[6] = getRotation;
                        result.bindGetRotation = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindGetRotation = false;
                    }
                }
                if (!result.bindRotation)
                {
                    try
                    {
                        MethodInfo rotation_16 = JavaRuntime.GetMethod(clazz, "SetRotation", typeof(int));
                        result.methods[2] = rotation_16;
                        result.bindRotation = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindRotation = false;
                    }
                }
                if (!result.bindRotation)
                {
                    try
                    {
                        MethodInfo rotation_18 = JavaRuntime.GetMethod(clazz, "SetAngle", typeof(float));
                        result.methods[2] = rotation_18;
                        result.bindRotation = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindRotation = false;
                    }
                }
                if (result.bindRotation && !result.bindGetRotation)
                {
                    try
                    {
                        MethodInfo getRotation_20 = JavaRuntime.GetMethod(clazz, "GetAngle");
                        result.methods[6] = getRotation_20;
                        result.bindGetRotation = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindGetRotation = false;
                    }
                }
                if (!result.bindRotation)
                {
                    try
                    {
                        MethodInfo rotation_22 = JavaRuntime.GetMethod(clazz, "SetAngle", typeof(int));
                        result.methods[2] = rotation_22;
                        result.bindRotation = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindRotation = false;
                    }
                }
                try
                {
                    MethodInfo update = JavaRuntime.GetMethod(clazz, "Update", typeof(long));
                    result.methods[3] = update;
                    result.bindUpdate = true;

                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                    result.bindUpdate = false;
                }
                try
                {
                    MethodInfo setScaleX = JavaRuntime.GetMethod(clazz, "SetScaleX", typeof(float));
                    MethodInfo setScaleY = JavaRuntime.GetMethod(clazz, "SetScaleY", typeof(float));
                    result.methods[4] = setScaleX;
                    result.methods[5] = setScaleY;
                    result.bindScale = true;
                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                    result.bindScale = false;
                }
                if (!result.bindScale)
                {
                    try
                    {
                        MethodInfo scale = JavaRuntime.GetMethod(clazz, "SetScale", typeof(float));
                        result.methods[4] = scale;
                        result.bindScale = true;
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                        result.bindScale = false;
                    }
                }
                try
                {
                    MethodInfo width = JavaRuntime.GetMethod(clazz, "GetWidth");
                    MethodInfo height = JavaRuntime.GetMethod(clazz, "GetHeight");
                    result.methods[9] = width;
                    result.methods[10] = height;
                    result.bindSize = true;
                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                    result.bindSize = false;
                }
                CollectionUtils.Put(methodList, clazz, result);
            }
            return result;
        }

        public float GetX()
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindGetPos)
                        {
                            if (methods != null)
                            {
                                if (methods[7] != null)
                                {
                                    Object o = JavaRuntime.Invoke(methods[7], obj);
                                    if (o is Single)
                                    {
                                        return ((Single)o);
                                    }
                                    else if (o is Int32)
                                    {
                                        return ((Int32)o);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        return actorObject.GetX();
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        return shapeObject.GetX();
                    }
                    break;
                case 3:
                    if (compObject != null)
                    {
                        return compObject.GetX();
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        return lObject.GetX();
                    }
                    break;
            }
            return 0;
        }

        public float GetY()
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindGetPos)
                        {
                            if (methods != null)
                            {
                                if (methods[8] != null)
                                {
                                    Object o = JavaRuntime.Invoke(methods[8], obj);
                                    if (o is Single)
                                    {
                                        return ((Single)o);
                                    }
                                    else if (o is Int32)
                                    {
                                        return ((Int32)o);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        return actorObject.GetY();
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        return shapeObject.GetY();
                    }
                    break;
                case 3:
                    if (compObject != null)
                    {
                        return compObject.GetY();
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        return lObject.GetY();
                    }
                    break;
            }

            return 0;
        }

        public void CallScale(float scaleX, float scaleY)
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindScale)
                        {
                            if (methods != null)
                            {
                                if (methods[5] != null)
                                {
                                    JavaRuntime.Invoke(methods[4], obj, scaleX);
                                    JavaRuntime.Invoke(methods[5], obj, scaleY);
                                }
                                else
                                {
                                    JavaRuntime.Invoke(methods[4], obj, scaleX, scaleY);
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        actorObject.SetScale(scaleX, scaleY);
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        shapeObject.SetScale(scaleX, scaleY);
                    }
                    break;
            }

        }

        public void CallRotation(float r)
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindRotation)
                        {
                            if (methods != null)
                            {
                                if (methods[2] != null)
                                {
                                    JavaRuntime.Invoke(methods[2], obj, r);
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        actorObject.SetRotation(r);
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        shapeObject.SetRotation(r);
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        lObject.SetRotation(r);
                    }
                    break;
            }

        }

        public float GetRotation()
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindGetRotation)
                        {
                            if (methods != null)
                            {
                                if (methods[6] != null)
                                {
                                    Object o = JavaRuntime.Invoke(methods[6], obj);
                                    if (o is Single)
                                    {
                                        return ((Single)o);
                                    }
                                    else if (o is Int32)
                                    {
                                        return ((Int32)o);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        return actorObject.GetRotation();
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        return shapeObject.GetRotation();
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        return lObject.GetRotation();
                    }
                    break;
            }
            return 0;
        }

        public void CallPos(float x, float y)
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindPos)
                        {
                            if (methods != null)
                            {
                                if (methods[1] != null)
                                {
                                    JavaRuntime.Invoke(methods[0], obj, x);
                                    JavaRuntime.Invoke(methods[1], obj, y);
                                }
                                else
                                {
                                    JavaRuntime.Invoke(methods[0], obj, x, y);
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        actorObject.SetLocation((int)x, (int)y);
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        shapeObject.SetLocation(x, y);
                    }
                    break;
                case 3:
                    if (compObject != null)
                    {
                        compObject.SetLocation(x, y);
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        lObject.SetLocation(x, y);
                    }
                    break;
            }
        }

        public void CallUpdate(long elapsedTime)
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindUpdate)
                        {
                            if (methods != null)
                            {
                                if (methods[3] != null)
                                {
                                    JavaRuntime.Invoke(methods[3], obj, elapsedTime);
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        actorObject.Action(elapsedTime);
                    }
                    break;
                case 3:
                    if (compObject != null)
                    {
                        compObject.Update(elapsedTime);
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        lObject.Update(elapsedTime);
                    }
                    break;
            }
        }

        public int GetWidth()
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindSize)
                        {
                            if (methods != null)
                            {
                                if (methods[9] != null)
                                {
                                    Object o = JavaRuntime.Invoke(methods[9], obj);
                                    if (o is Single)
                                    {
                                        return System.Convert.ToInt32(((Single)o));
                                    }
                                    else if (o is Int32)
                                    {
                                        return ((Int32)o);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        return actorObject.GetWidth();
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        return (int)shapeObject.GetWidth();
                    }
                    break;
                case 3:
                    if (compObject != null)
                    {
                        return compObject.GetWidth();
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        return lObject.GetWidth();
                    }
                    break;
            }
            return 0;
        }

        public int GetHeight()
        {
            switch (type)
            {
                case 0:
                    try
                    {
                        if (isBindSize)
                        {
                            if (methods != null)
                            {
                                if (methods[10] != null)
                                {
                                    Object o = JavaRuntime.Invoke(methods[10], obj);
                                    if (o is Single)
                                    {
                                        return System.Convert.ToInt32(((Single)o));
                                    }
                                    else if (o is Int32)
                                    {
                                        return ((Int32)o);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    break;
                case 1:
                    if (actorObject != null)
                    {
                        return actorObject.GetHeight();
                    }
                    break;
                case 2:
                    if (shapeObject != null)
                    {
                        return (int)shapeObject.GetHeight();
                    }
                    break;
                case 3:
                    if (compObject != null)
                    {
                        return compObject.GetHeight();
                    }
                    break;
                case 4:
                    if (lObject != null)
                    {
                        return lObject.GetHeight();
                    }
                    break;
            }
            return 0;
        }

        public bool IsBindPos()
        {
            return isBindPos;
        }

        public bool IsBindRotation()
        {
            return isBindRotation;
        }

        public bool IsBindUpdate()
        {
            return isBindUpdate;
        }

        public Object Ref()
        {
            return obj;
        }

    }
}
