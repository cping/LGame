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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CMacroFix {

	static class MacroReplacement {
		String ifReplacement;
		String elseReplacement;

		MacroReplacement(String ifReplacement, String elseReplacement) {
			this.ifReplacement = ifReplacement;
			this.elseReplacement = elseReplacement;
		}
	}

	static class MacroInjection {
		String injectionText;
		boolean injectOnce;
		// FIRST, LAST, DEFAULT
		String insertionRule;

		MacroInjection(String injectionText, boolean injectOnce, String insertionRule) {
			this.injectionText = injectionText;
			this.injectOnce = injectOnce;
			this.insertionRule = insertionRule;
		}
	}

	static class MacroContext {
		String macroName;
		boolean skipping;
		boolean inElse;

		MacroContext(String macroName, boolean skipping) {
			this.macroName = macroName;
			this.skipping = skipping;
			this.inElse = false;
		}
	}

	public static void processFile(String filePath, Map<String, MacroReplacement> replacements,
			Map<String, MacroInjection> injections) throws IOException {
		
		if (filePath == null || replacements == null) {
			return;
		}
		if (injections == null) {
			injections = new HashMap<String, MacroInjection>();
		}
		
		BufferedReader reader = null;
		
		List<String> lines = new ArrayList<String>();
		String lineSeparator = System.getProperty("line.separator");
		
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		Map<String, List<Integer>> macroEndPositions = new HashMap<String, List<Integer>>();
		List<Integer> allEndPositions = new ArrayList<Integer>();
		Stack<String> macroStack = new Stack<String>();

		for (int i = 0; i < lines.size(); i++) {
			String trimLine = lines.get(i).trim();
			if (trimLine.startsWith("#if")) {
				String matchedMacro = null;
				for (String macro : replacements.keySet()) {
					if (trimLine.contains(macro)) {
						matchedMacro = macro;
						break;
					}
				}
				macroStack.push(matchedMacro);
			} else if (trimLine.startsWith("#endif")) {
				if (!macroStack.isEmpty()) {
					String endedMacro = macroStack.pop();
					allEndPositions.add(i);
					if (endedMacro != null) {
						if (!macroEndPositions.containsKey(endedMacro)) {
							macroEndPositions.put(endedMacro, new ArrayList<Integer>());
						}
						macroEndPositions.get(endedMacro).add(i);
					}
				}
			}
		}

		Map<Integer, List<String>> insertMap = new HashMap<Integer, List<String>>();
		for (String macro : injections.keySet()) {
			MacroInjection inj = injections.get(macro);
			List<Integer> positions = macroEndPositions.get(macro);
			int insertPos = -1;
			if ("FIRST".equals(inj.insertionRule)) {
				if (!allEndPositions.isEmpty()) {
					insertPos = allEndPositions.get(0);
				}
			} else if ("LAST".equals(inj.insertionRule)) {
				if (!allEndPositions.isEmpty()) {
					insertPos = allEndPositions.get(allEndPositions.size() - 1);
				}
			} else {
				if (positions == null || positions.isEmpty()) {
					if (!allEndPositions.isEmpty()) {
						insertPos = allEndPositions.get(0);
					}
				} else if (positions.size() == 1) {
					insertPos = positions.get(0);
				} else {
					insertPos = positions.get(positions.size() - 2);
				}
			}
			if (insertPos >= 0) {
				if (!insertMap.containsKey(insertPos)) {
					insertMap.put(insertPos, new ArrayList<String>());
				}
				insertMap.get(insertPos).add(macro);
			}
		}

		StringBuilder result = new StringBuilder();
		Set<String> injectedMacros = new HashSet<String>();

		for (int i = 0; i < lines.size(); i++) {

			result.append(lines.get(i)).append(lineSeparator);
			if (insertMap.containsKey(i)) {
				List<String> macrosToInject = insertMap.get(i);
				for (String macro : macrosToInject) {
					MacroInjection inj = injections.get(macro);
					if (inj != null) {
						if (inj.injectOnce) {
							if (!injectedMacros.contains(macro)) {
								result.append(inj.injectionText).append(lineSeparator);
								injectedMacros.add(macro);
							}
						} else {
							result.append(inj.injectionText).append(lineSeparator);
						}
					}
				}
			}
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(result.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
