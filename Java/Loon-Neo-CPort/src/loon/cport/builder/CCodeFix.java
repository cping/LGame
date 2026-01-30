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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import loon.cport.builder.CMacroFix.MacroInjection;
import loon.cport.builder.CMacroFix.MacroReplacement;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.TArray;

/**
 * 修正C代码用类，用于动态修正一些teavm的c版生成的c代码，有些是teavm的生成错误，有些是多平台适配需要，反正都是必改的……
 */
public class CCodeFix {

	private static final LinkedHashMap<String, String> ALL_FILE_REPLACEMENT_RULES = new LinkedHashMap<String, String>();

	static {
		ALL_FILE_REPLACEMENT_RULES.put("TeaVM_Class*, TeaVM_Class* cls", "TeaVM_Class* fixedname, TeaVM_Class* cls");
	}

	public static void fixMacro(Path fixFile) {
		if (fixFile == null) {
			return;
		}
		String fixFileName = fixFile.toAbsolutePath().toString();
		String fixBaseName = PathUtils.getBaseFileName(fixFileName);
		String fixExtName = PathUtils.getExtension(fixFileName);
		if ("c".equals(fixExtName)) {
			String fixMacroName1 = "time";
			if (fixBaseName.equalsIgnoreCase(fixMacroName1)) {
				Map<String, MacroReplacement> replacements = new HashMap<String, MacroReplacement>();
				replacements.put("TEAVM_WINDOWS", new MacroReplacement(null, null));
				Map<String, MacroInjection> append = new HashMap<String, MacroInjection>();
				append.put("TEAVM_WINDOWS",
						new MacroInjection("\r\n#if !defined(TEAVM_WINDOWS) && !defined(TEAVM_UNIX)\r\n"
								+ "    int64_t teavm_currentTimeMillis() {\r\n"
								+ "        return (int64_t)SDL_GetTicks();\r\n" + "    }\r\n" + "\r\n"
								+ "    int64_t teavm_currentTimeNano() {\r\n"
								+ "        return (int64_t)(SDL_GetTicks64() * 1000000LL);\r\n" + "    }\r\n"
								+ "#endif", true, "DEFAULT"));
				try {
					CMacroFix.processFile(fixFileName, replacements, append);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String fixMacroName2 = "fiber";
			if (fixBaseName.equalsIgnoreCase(fixMacroName2)) {
				Map<String, MacroReplacement> replacements = new HashMap<String, MacroReplacement>();
				replacements.put("TEAVM_WINDOWS", new MacroReplacement(null, null));
				Map<String, MacroInjection> append = new HashMap<String, MacroInjection>();
				append.put("TEAVM_WINDOWS",
						new MacroInjection("\r\n#if !defined(TEAVM_WINDOWS) && !defined(TEAVM_UNIX)\r\n"
								+ "    void teavm_waitFor(int64_t timeout) {\r\n"
								+ "        SDL_Delay((Uint32)timeout);\r\n" + "    }\r\n" + "\r\n"
								+ "    void teavm_interrupt() {\r\n" + "    }\r\n" + "#endif", true, "LAST"));
				try {
					CMacroFix.processFile(fixFileName, replacements, append);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String fixMacroName3 = "memory";
			if (fixBaseName.equalsIgnoreCase(fixMacroName3)) {
				Map<String, MacroReplacement> replacements = new HashMap<String, MacroReplacement>();
				replacements.put("TEAVM_MEMORY_TRACE", new MacroReplacement(null, null));
				Map<String, MacroInjection> append = new HashMap<String, MacroInjection>();
				append.put("TEAVM_MEMORY_TRACE",
						new MacroInjection("\r\n#if !defined(TEAVM_WINDOWS) && !defined(TEAVM_UNIX)\r\n"
								+ "    static void* teavm_virtualAlloc(size_t size) {\r\n" + "    #if defined(XBOX)\r\n"
								+ "            return XMemVirtualAlloc(size, XMEM_COMMIT, XMEM_READWRITE);\r\n"
								+ "    #elif defined(__linux__) || defined(__APPLE__) || defined(__ANDROID__) || defined(__IOS__) \\\r\n"
								+ "           || defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__) || defined(__HAIKU__)\r\n"
								+ "            void* p = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);\r\n"
								+ "            return (p == MAP_FAILED) ? NULL : p;\r\n" + "    #else\r\n"
								+ "            return gc_mmap((size_t)size, GC_PROT_NONE);\r\n" + "    #endif\r\n"
								+ "    }\r\n" + "\r\n" + "    static int64_t teavm_pageSize() {\r\n"
								+ "    #if defined(XBOX)\r\n" + "            return (int64_t)XMemGetPageSize();\r\n"
								+ "    #elif defined(__linux__) || defined(__APPLE__) || defined(__ANDROID__) || defined(__IOS__) \\\r\n"
								+ "       || defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__) || defined(__HAIKU__)\r\n"
								+ "            long pageSize = sysconf(_SC_PAGESIZE);\r\n"
								+ "            return (pageSize > 0) ? (int64_t)pageSize : 4096;\r\n" + "    #else\r\n"
								+ "            return 4096;\r\n" + "    #endif\r\n" + "    }\r\n" + "\r\n"
								+ "    static void teavm_virtualCommit(void* address, int64_t size) {\r\n"
								+ "    #if defined(XBOX)\r\n"
								+ "            XMemProtect(address, (SIZE_T)size, XMEM_READWRITE);\r\n"
								+ "    #elif defined(__linux__) || defined(__APPLE__) || defined(__ANDROID__) || defined(__IOS__) \\\r\n"
								+ "       || defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__) || defined(__HAIKU__)\r\n"
								+ "            mprotect(address, (size_t)size, PROT_READ | PROT_WRITE);\r\n"
								+ "    #else\r\n"
								+ "            gc_mprotect(address, (size_t)size, GC_PROT_READ | GC_PROT_WRITE);\r\n"
								+ "    #endif\r\n" + "    }\r\n" + "\r\n"
								+ "    static void teavm_virtualUncommit(void* address, int64_t size) {\r\n"
								+ "    #if defined(XBOX)\r\n"
								+ "            XMemProtect(address, (SIZE_T)size, XMEM_NOACCESS);\r\n"
								+ "    #elif defined(__linux__) || defined(__APPLE__) || defined(__ANDROID__) || defined(__IOS__) \\\r\n"
								+ "       || defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__) || defined(__HAIKU__)\r\n"
								+ "            mprotect(address, (size_t)size, PROT_NONE);\r\n" + "    #else\r\n"
								+ "            gc_mprotect(address, (size_t)size, GC_PROT_NONE);\r\n" + "    #endif\r\n"
								+ "    }\r\n" + "#endif\r\n", true, "DEFAULT"));
				try {
					CMacroFix.processFile(fixFileName, replacements, append);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if ("h".equals(fixExtName)) {
			String fixMacroName1 = "exceptions";
			if (fixBaseName.equalsIgnoreCase(fixMacroName1)) {
				Map<String, MacroReplacement> replacements = new HashMap<String, MacroReplacement>();
				replacements.put("TEAVM_WINDOWS", new MacroReplacement(null, null));
				Map<String, MacroInjection> append = new HashMap<String, MacroInjection>();
				append.put("TEAVM_WINDOWS",
						new MacroInjection("    #if !defined(TEAVM_WINDOWS) && !defined(TEAVM_UNIX)\r\n"
								+ "        #if defined(_MSC_VER)\r\n"
								+ "        #define TEAVM_UNREACHABLE __assume(0);\r\n"
								+ "        #elif defined(__GNUC__) || defined(__clang__)\r\n"
								+ "        #define TEAVM_UNREACHABLE __builtin_unreachable();\r\n" + "        #else\r\n"
								+ "        #define TEAVM_UNREACHABLE abort();\r\n" + "        #endif\r\n"
								+ "    #endif", true, "DEFAULT"));
				try {
					CMacroFix.processFile(fixFileName, replacements, append);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void processFile(Path filePath) {
		ArrayList<String> newLines = new ArrayList<String>();
		boolean modified = false;
		try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String updatedLine = line;
				for (Map.Entry<String, String> entry : ALL_FILE_REPLACEMENT_RULES.entrySet()) {
					String target = entry.getKey();
					String replacement = entry.getValue();
					if (updatedLine.contains(target)) {
						updatedLine = updatedLine.replace(target, replacement);
						modified = true;
					}
				}
				newLines.add(updatedLine);
			}
		} catch (IOException e) {
			CBuilder.println("Error reading file: " + filePath + " - " + e.getMessage());
			return;
		}
		if (modified) {
			try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
					StandardOpenOption.TRUNCATE_EXISTING)) {
				for (String l : newLines) {
					writer.write(l);
					writer.newLine();
				}
			} catch (IOException e) {
				CBuilder.println("Error writing file: " + filePath + " - " + e.getMessage());
			}
		}
	}

	public static void fixAllFiles(String folderPath) throws IOException {
		Path rootPath = Paths.get(folderPath);
		Stream<Path> paths = Files.walk(rootPath);
		try {
			for (Path path : (Iterable<Path>) paths::iterator) {
				String pathEx = path.toString();
				if (Files.isRegularFile(path) && (pathEx.endsWith(".c") || pathEx.endsWith(".h"))) {
					processFile(path);
					fixMacro(path);
				}
			}
		} finally {
			paths.close();
		}
	}

	final static class FileFix {

		public String fileName;

		public final ObjectMap<String, String> fixContexts = new ObjectMap<String, String>();

		public FileFix(String name, String src, String dst) {
			fileName = name;
			putFixReplace(src, dst);
		}

		public void putFixReplace(String src, String dst) {
			fixContexts.put(src, dst);
		}
	}

	public final TArray<FileFix> fixContexts = new TArray<FileFix>();

	public CCodeFix() {
		FileFix fix1 = new FileFix("file.c", "file, size, 0, where)", "file, size, 0, FILE_BEGIN)");
		FileFix fix2 = new FileFix("definitions.h", "#define TEAVM_UNIX 1",
				"   #if defined(__linux__) || defined(__APPLE__) || defined(__ANDROID__) || defined(__IOS__) || defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__) || defined(__HAIKU__)\r\n"
						+ "       #define TEAVM_UNIX 1\r\n" + "       #endif");
		FileFix fix3 = new FileFix("core.h", "((char*) teavm_gc_cardTable)[offset] = 0;",
				"char* result = ((char*)teavm_gc_cardTable);\r\n" + "    int off = (int)offset;\r\n"
						+ "    if (result && off >=0) {\r\n" + "           result[off] = 0;\r\n" + "    }");
		FileFix fix4 = new FileFix("config.h", "#pragma once", "#pragma once\r\n" + "#include \"SDLSupport.c\"\r\n"
				+ "#include \"STBSupport.c\"\r\n" + "#include \"SocketSupport.c\"\r\n" + "#include \"gles2.c\"");
		fixContexts.add(fix1);
		fixContexts.add(fix2);
		fixContexts.add(fix3);
		fixContexts.add(fix4);
	}

	public TArray<FileFix> getFixList() {
		return fixContexts;
	}

}
