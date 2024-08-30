package loon.tea.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.reflect.ReflectField;
import org.teavm.metaprogramming.reflect.ReflectMethod;

public class GenericTypeProvider {
    private ClassLoader classLoader;
    private Map<ReflectMethod, Method> methodCache = new HashMap<>();
    private Map<ReflectMethod, Constructor<?>> constructorCache = new HashMap<>();
    private Map<ReflectField, Field> fieldCache = new HashMap<>();
    private Map<String, Class<?>> classCache = new HashMap<>();

    public GenericTypeProvider(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Method findMethod(ReflectMethod methodCache) {
        return this.methodCache.computeIfAbsent(methodCache, r -> {
            Class<?> owner = findClass(methodCache.getDeclaringClass().getName());
            Class<?>[] params = Arrays.stream(methodCache.getParameterTypes())
                    .map(this::convertType)
                    .toArray((IntFunction<Class<?>[]>)Class[]::new);
            while(owner != null) {
                try {
                    return owner.getDeclaredMethod(methodCache.getName(), params);
                }
                catch(NoSuchMethodException e) {
                    owner = owner.getSuperclass();
                }
            }
            return null;
        });
    }

    public Constructor<?> findConstructor(ReflectMethod method) {
        return constructorCache.computeIfAbsent(method, c -> {
            Class<?> owner = findClass(method.getDeclaringClass().getName());
            Class<?>[] params = Arrays.stream(method.getParameterTypes())
                    .map(this::convertType)
                    .toArray((IntFunction<Class<?>[]>)Class[]::new);
            while(owner != null) {
                try {
                    return owner.getDeclaredConstructor(params);
                }
                catch(NoSuchMethodException e) {
                    owner = owner.getSuperclass();
                }
            }
            return null;
        });
    }

    public Field findField(ReflectField field) {
        return fieldCache.computeIfAbsent(field, f -> {
            Class<?> owner = findClass(field.getDeclaringClass().getName());
            while(owner != null) {
                try {
                    return owner.getDeclaredField(field.getName());
                }
                catch(NoSuchFieldException e) {
                    owner = owner.getSuperclass();
                }
            }
            return null;
        });
    }

    private Class<?> convertType(ReflectClass<?> type) {
        if(type.isPrimitive()) {
            switch(type.getName()) {
                case "boolean":
                    return boolean.class;
                case "byte":
                    return byte.class;
                case "short":
                    return short.class;
                case "char":
                    return char.class;
                case "int":
                    return int.class;
                case "long":
                    return long.class;
                case "float":
                    return float.class;
                case "double":
                    return double.class;
                case "void":
                    return void.class;
            }
        }
        else if(type.isArray()) {
            Class<?> itemCls = convertType(type.getComponentType());
            return Array.newInstance(itemCls, 0).getClass();
        }
        return findClass(type.getName());
    }

    public Class<?> findClass(String name) {
        return classCache.computeIfAbsent(name, n -> {
            try {
                return Class.forName(name, false, classLoader);
            }
            catch(ClassNotFoundException e) {
                throw new RuntimeException("Can't find class " + name, e);
            }
        });
    }

    static Class<?> rawType(Type type) {
        if(type instanceof Class<?>) {
            return (Class<?>)type;
        }
        else if(type instanceof ParameterizedType) {
            return rawType(((ParameterizedType)type).getRawType());
        }
        else if(type instanceof GenericArrayType) {
            return Array.newInstance(rawType(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
        }
        else if(type instanceof TypeVariable<?>) {
            return rawType(((TypeVariable<?>)type).getBounds()[0]);
        }
        else if(type instanceof WildcardType) {
            return rawType(((WildcardType)type).getUpperBounds()[0]);
        }
        else {
            throw new IllegalArgumentException("Don't know how to convert generic type: " + type);
        }
    }
}