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
package loon.teavm.plugin;

import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldHolder;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReader;
import org.teavm.model.ReferenceCache;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;

import loon.teavm.builder.TeaBuilder;

public class OverlayTransformer implements ClassHolderTransformer {
	
	private ReferenceCache referenceCache = new ReferenceCache();

	@Override
	public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
		if (cls.getName().equals(java.lang.Runtime.class.getName())) {
			replaceClass(cls, context.getHierarchy().getClassSource().get(emu.java.lang.RuntimeEmu.class.getName()));
		}
	}

	private void replaceClass(final ClassHolder cls, final ClassReader emuCls) {
		ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache,
				preimage -> preimage.equals(emuCls.getName()) ? cls.getName() : preimage);
		for (FieldHolder field : cls.getFields().toArray(new FieldHolder[0])) {
			cls.removeField(field);
		}
		for (MethodHolder method : cls.getMethods().toArray(new MethodHolder[0])) {
			cls.removeMethod(method);
		}
		for (FieldReader field : emuCls.getFields()) {
			cls.addField(ModelUtils.copyField(field));
		}
		for (MethodReader method : emuCls.getMethods()) {
			cls.addMethod(renamer.rename(ModelUtils.copyMethod(method)));
		}
	}

}
