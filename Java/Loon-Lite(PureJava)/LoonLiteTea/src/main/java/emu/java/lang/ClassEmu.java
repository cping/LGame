package emu.java.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import loon.tea.make.Emulate;

@Emulate(value = Class.class, updateCode = true)
public class ClassEmu {

    @Emulate
    public boolean isAnonymousClass() {
        return false;
    }

    @Emulate
    public Method getEnclosingMethod() {
        return null;
    }

    @Emulate
    public Constructor<?> getEnclosingConstructor() {
        return null;
    }

    @Emulate
    public URL getResource(String name) {
        return null;
    }
}