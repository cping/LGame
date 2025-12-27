/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.cport.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

public class TeaReflectionSupplier implements ReflectionSupplier {

    private static ArrayList<String> clazzList = new ArrayList<String>();

    public static void addReflectionClass(Class<?> type) {
        addReflectionClass(type.getName());
    }

    public static List<String> getReflectionClasses() {
        return clazzList;
    }

    public static boolean containsReflection(String className) {
        for(int i = 0; i < clazzList.size(); i++) {
            String reflectionClass = clazzList.get(i);
            if(className.contains(reflectionClass))
                return true;
        }
        return false;
    }

    public static void addReflectionClass(String className) {
        if(!clazzList.contains(className)) {
            clazzList.add(className);
        }
    }

    public static void printReflectionClasses() {
		CBuilder.begin("REFLECTION CLASSES: " + clazzList.size());
        for(String reflectionClass : clazzList) {
        	CBuilder.println(reflectionClass);
        }
        CBuilder.end();
    }

    public TeaReflectionSupplier() {
    }

    @Override
    public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
        ClassReader cls = context.getClassSource().get(className);
        if(cls == null) {
            return Collections.emptyList();
        }
        Set<String> fields = new HashSet<>();

        if(cls != null) {
            if(canHaveReflection(className)) {
                for(FieldReader field : cls.getFields()) {
                    String name = field.getName();
                    fields.add(name);
                }
            }
        }
        return fields;
    }

    @Override
    public Collection<MethodDescriptor> getAccessibleMethods(ReflectionContext context, String className) {
        ClassReader cls = context.getClassSource().get(className);
        if(cls == null) {
            return Collections.emptyList();
        }
        Set<MethodDescriptor> methods = new HashSet<>();
        if(canHaveReflection(className)) {
            Collection<? extends MethodReader> methods2 = cls.getMethods();
            for(MethodReader method : methods2) {
                MethodDescriptor descriptor = method.getDescriptor();
                methods.add(descriptor);
            }
        }
        return methods;
    }

    @Override
    public boolean isClassFoundByName(ReflectionContext context, String name) {
        return canHaveReflection(name);
    }

    private boolean canHaveReflection(String className) {
        boolean flag = false;
        for(int i = 0; i < clazzList.size(); i++) {
            String name = clazzList.get(i);
            if(className.contains(name)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}