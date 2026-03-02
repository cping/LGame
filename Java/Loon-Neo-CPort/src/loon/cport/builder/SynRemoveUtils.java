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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.teavm.asm.ClassReader;
import org.teavm.asm.ClassVisitor;
import org.teavm.asm.ClassWriter;
import org.teavm.asm.Label;
import org.teavm.asm.MethodVisitor;
import org.teavm.asm.Opcodes;
import org.teavm.asm.util.CheckClassAdapter;

import loon.utils.PathUtils;

/**
 * The synchronized flag in teavm C version blocks the normal loop and must be
 * cleared completely...
 */
public class SynRemoveUtils {

	private static class SyncResult {
		String updatedContent;
		int removedCount;

		SyncResult(String updatedContent, int removedCount) {
			this.updatedContent = updatedContent;
			this.removedCount = removedCount;
		}
	}

	public static void processJar(File inputJar, File outputJar, String packageName) throws Exception {
		try (JarFile jarFile = new JarFile(inputJar);
				JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar))) {
			for (JarEntry entry : jarFile.stream().toList()) {
				InputStream is = jarFile.getInputStream(entry);
				byte[] data = is.readAllBytes();
				if (entry.getName().endsWith(".class") && entry.getName().replace('/', '.').startsWith(packageName)) {
					data = removeAllSynchronized(data);
				}
				JarEntry newEntry = new JarEntry(entry.getName());
				jos.putNextEntry(newEntry);
				jos.write(data);
				jos.closeEntry();
			}
		}
	}

	public static byte[] removeAllSynchronized(byte[] source) throws Exception {
		return removeAllSynchronized(source);
	}

	public static byte[] removeAllSynchronized(Class<?> clazz) throws Exception {
		return removeAllSynchronized(new ClassReader(clazz.getName()));
	}

	public static byte[] removeAllSynchronized(ClassReader reader) throws Exception {
		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

		ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature,
					String[] exceptions) {
				access = access & ~Opcodes.ACC_SYNCHRONIZED;
				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				return new MethodVisitor(Opcodes.ASM9, mv) {
					@Override
					public void visitInsn(int opcode) {
						if (opcode == Opcodes.MONITORENTER || opcode == Opcodes.MONITOREXIT) {
							return;
						}
						super.visitInsn(opcode);
					}

					@Override
					public void visitVarInsn(int opcode, int var) {
						super.visitVarInsn(opcode, var);
					}

					@Override
					public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
						return;
					}
				};
			}
		};

		reader.accept(visitor, ClassReader.EXPAND_FRAMES);

		byte[] modifiedBytes = writer.toByteArray();
		ClassReader checkReader = new ClassReader(modifiedBytes);
		CheckClassAdapter.verify(checkReader, false, new java.io.PrintWriter(System.out));

		return modifiedBytes;
	}

	protected static SyncResult removeSynchronized(String src, String mode) {
		final StringBuilder outBuffer = new StringBuilder(4096);
		final StringBuilder token = new StringBuilder(4096);
		boolean inSingleLineComment = false;
		boolean inMultiLineComment = false;
		boolean inString = false;
		boolean inChar = false;
		int removedCount = 0;
		final int len = src.length();
		for (int i = 0; i < len; i++) {
			char c = src.charAt(i);
			char next = (i + 1 < len) ? src.charAt(i + 1) : '\0';
			if (!inString && !inChar && !inMultiLineComment && c == '/' && next == '/') {
				inSingleLineComment = true;
				outBuffer.append(c);
				continue;
			}
			if (inSingleLineComment) {
				outBuffer.append(c);
				if (c == '\n')
					inSingleLineComment = false;
				continue;
			}
			if (!inString && !inChar && !inSingleLineComment && c == '/' && next == '*') {
				inMultiLineComment = true;
				outBuffer.append(c);
				continue;
			}
			if (inMultiLineComment) {
				outBuffer.append(c);
				if (c == '*' && next == '/') {
					outBuffer.append(next);
					i++;
					inMultiLineComment = false;
				}
				continue;
			}
			if (!inChar && c == '"' && !inSingleLineComment && !inMultiLineComment) {
				inString = !inString;
				outBuffer.append(c);
				continue;
			}
			if (inString) {
				outBuffer.append(c);
				if (c == '\\' && next != '\0') {
					outBuffer.append(next);
					i++;
				}
				continue;
			}
			if (!inString && c == '\'' && !inSingleLineComment && !inMultiLineComment) {
				inChar = !inChar;
				outBuffer.append(c);
				continue;
			}
			if (inChar) {
				outBuffer.append(c);
				if (c == '\\' && next != '\0') {
					outBuffer.append(next);
					i++;
				}
				continue;
			}
			if (Character.isJavaIdentifierPart(c)) {
				token.append(c);
			} else {
				if (token.length() > 0) {
					String word = token.toString();
					if (word.equals("synchronized")) {
						boolean isCodeBlock = false;
						int j = i;
						while (j < len && Character.isWhitespace(src.charAt(j)))
							j++;
						if (j < len && src.charAt(j) == '(')
							isCodeBlock = true;
						if ((mode.equals("all")) || (mode.equals("modifier") && !isCodeBlock)
								|| (mode.equals("block") && isCodeBlock)) {
							removedCount++;
							if (isCodeBlock) {
								j++;
								int depth = 1;
								while (j < len && depth > 0) {
									char cc = src.charAt(j);
									if (cc == '(')
										depth++;
									else if (cc == ')')
										depth--;
									j++;
								}
								while (j < len && Character.isWhitespace(src.charAt(j))) {
									j++;
								}
								i = j - 1;
							}
						} else {
							outBuffer.append(word);
						}
					} else {
						outBuffer.append(word);
					}
					token.setLength(0);
				}
				outBuffer.append(c);
			}
		}
		if (token.length() > 0) {
			String word = token.toString();
			if (!word.equals("synchronized")) {
				outBuffer.append(word);
			} else {
				removedCount++;
			}
		}
		return new SyncResult(outBuffer.toString(), removedCount);
	}

	public static void removeSynchronizedFromFiles(File rootDir) {
		removeSynchronizedFromFiles(rootDir, "all", false, null);
	}

	public static void removeSynchronizedFromFiles(File rootDir, String mode, boolean backFile, File outputDir) {
		int totalRemoved = 0;
		try (Stream<Path> paths = Files.walk(rootDir.toPath())) {
			List<Path> javaFiles = paths.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList());
			for (Path javaFile : javaFiles) {
				String dirName = PathUtils.getLastDirName(javaFile.toAbsolutePath().toString());
				if (!"reply".equals(dirName) && !"cport".equals(dirName)) {
					String content = Files.readString(javaFile, StandardCharsets.UTF_8);
					SyncResult result = removeSynchronized(content, mode);
					if (result.removedCount > 0) {
						totalRemoved += result.removedCount;
						if (backFile && outputDir == null) {
							Files.writeString(Path.of(javaFile.toString() + ".bak"), content, StandardCharsets.UTF_8);
						}
						Path targetPath;
						if (outputDir != null) {
							Path relativePath = rootDir.toPath().relativize(javaFile);
							targetPath = outputDir.toPath().resolve(relativePath);
							Files.createDirectories(targetPath.getParent());
						} else {
							targetPath = javaFile;
						}
						Files.writeString(targetPath, result.updatedContent, StandardCharsets.UTF_8);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		CBuilder.println("Removed synchronized count : " + totalRemoved);
	}

	public static void restoreFiles(File rootDir) {
		int restoredCount = 0;
		try (Stream<Path> paths = Files.walk(rootDir.toPath())) {
			List<Path> bakFiles = paths.filter(p -> p.toString().endsWith(".java.bak")).collect(Collectors.toList());
			for (Path bakFile : bakFiles) {
				Path originalFile = Path.of(bakFile.toString().replace(".java.bak", ".java"));
				if (Files.exists(bakFile)) {
					String bakContent = Files.readString(bakFile, StandardCharsets.UTF_8);
					Files.writeString(originalFile, bakContent, StandardCharsets.UTF_8);
					Files.delete(bakFile);
					restoredCount++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		CBuilder.println("Restored synchronized count : " + restoredCount);
	}
}
