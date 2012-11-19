package loon.action.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import loon.action.scripting.pack.PackAnimation;
import loon.action.scripting.pack.PackFrame;
import loon.action.scripting.pack.PackTileFactory;
import loon.action.scripting.pack.PackTileMap;
import loon.core.LRelease;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.graphics.opengl.LTexturePack.PackEntry;
import loon.core.resource.Resources;
import loon.utils.StringUtils;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class ScriptFactory implements LRelease {

	private PackTileFactory tileFactory;

	private HashMap<String, Script> scripts;

	private HashMap<String, LTexturePack> packs;

	private HashMap<String, PackTileMap> maps;

	private HashMap<String, PackAnimation> animations;

	private ArrayList<Stack<StackFrame>> executionStacks;

	private Callback callback;

	String packName;

	String mapName;

	int packCount;

	private InputStream inputStream;

	private Screen screen;

	public ScriptFactory(InputStream in) {
		this(null, in);
	}

	public ScriptFactory(String res) {
		this(null, res);
	}

	public ScriptFactory(Screen screen, String res) {
		this.screen = screen;
		try {
			this.inputStream = Resources.openResource(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.init();
	}

	public ScriptFactory(Screen screen, InputStream in) {
		this.screen = screen;
		this.inputStream = in;
		this.init();
	}

	private void init() {
		this.scripts = new HashMap<String, Script>(10);
		this.packs = new HashMap<String, LTexturePack>(10);
		this.maps = new HashMap<String, PackTileMap>(10);
		this.animations = new HashMap<String, PackAnimation>(10);
		this.executionStacks = new ArrayList<Stack<StackFrame>>(10);
	}

	public void setMapName(String name) {
		this.mapName = name;
	}

	public String getMapName() {
		return mapName;
	}

	public void setPackName(String name) {
		this.packName = name;
	}

	public String getPackName() {
		return packName;
	}

	public void load() {
		if (inputStream == null) {
			return;
		}
		XMLElement root = XMLParser.parse(inputStream).getRoot();
		for (Iterator<?> itr = root.elements(); itr.hasNext();) {
			Object o = itr.next();
			if (o instanceof XMLElement) {
				XMLElement ele = (XMLElement) o;
				if ("packs".equalsIgnoreCase(ele.getName())) {
					for (Iterator<?> it = ele.elements("pack"); it.hasNext();) {
						LTexturePack pack = new LTexturePack((XMLElement) it
								.next());
						packs.put(pack.getName(), pack);
						if (packName == null) {
							packName = pack.getName();
						}
					}
				} else if ("scripts".equalsIgnoreCase(ele.getName())) {
					for (Iterator<?> it = ele.elements("script"); it.hasNext();) {
						XMLElement child = (XMLElement) it.next();
						String name = child.getAttribute("name").getValue();
						Script script = new Script(name);
						for (Iterator<?> it1 = child.elements(); it1.hasNext();) {
							Object obj = it1.next();
							if (obj instanceof XMLElement) {
								XMLElement child3 = (XMLElement) obj;
								if (child3.getName().equals("function")) {
									if (child3.hasAttribute("x")) {
										script.add(new WithPosition(child3
												.getAttribute("name")
												.getValue(), child3
												.getAttribute("x")
												.getIntValue(), child3
												.getAttribute("y")
												.getIntValue()));
									} else {
										if (child3.hasAttribute("text")) {
											script.add(new WithText(child3
													.getAttribute("name")
													.getValue(), child3
													.getAttribute("text")
													.getValue()));
										} else {
											script.add(new UserFunction(child3
													.getAttribute("name")
													.getValue()));
										}
									}
								}
								if (child3.getName().equals("wait")) {
									script
											.add(new WaitFunction(child3
													.getAttribute("time")
													.getIntValue()));
								}
								if (child3.getName().equals("jump")) {
									script
											.add(new JumpFunction(child3
													.getAttribute("script")
													.getValue()));
								}
								if (child3.getName().equals("execute")) {
									script.add(new Execute(child3.getAttribute(
											"script").getValue()));
								}
								if (child3.getName().equals("call")) {
									script
											.add(new CallFunction(child3
													.getAttribute("script")
													.getValue()));
								}
							}
						}
						scripts.put(name, script);
					}
				} else if ("animations".equalsIgnoreCase(ele.getName())) {
					String packName = ele.getAttribute("pack", null);
					for (Iterator<?> it = ele.elements("animation"); it.hasNext();) {
						XMLElement child = (XMLElement) it.next();
						String name = child.getAttribute("name", "");
						boolean looped = true;
						if (child.hasAttribute("noloop")) {
							looped = false;
						}
						LTexturePack pack = null;
						if (packName != null) {
							pack = packs.get(packName);
						} else {
							if (packName == null) {
								Set<String> set = packs.keySet();
								pack = packs.get(set.iterator().next());
							}
						}
						String context = child.getContents();
						String[] strings = StringUtils.split(context, ",");
						PackAnimation animation = new PackAnimation(name,
								looped);
						for (String res : strings) {
							PackEntry entry = pack.getEntry(res);
							if (entry == null) {
								try {
									entry = pack
											.getEntry(Integer.parseInt(res));
								} catch (Exception e) {
								}
							}
							if (entry != null) {
								animation.addFrame(new PackFrame(entry));
							}
						}
						animations.put(name, animation);
					}
				} else if ("maps".equalsIgnoreCase(ele.getName())) {
					for (Iterator<?> it = ele.elements("map"); it.hasNext();) {
						XMLElement child = (XMLElement) it.next();
						String name = child.getAttribute("name", String
								.valueOf(System.currentTimeMillis()));
						if (mapName == null) {
							mapName = name;
						}
						maps.put(name, new PackTileMap(tileFactory, child,
								screen));
					}
				}
			}
		}
		this.packCount = packs.size();
		if (inputStream != null) {
			try {
				inputStream.close();
				inputStream = null;
			} catch (Exception e) {
			}
		}
	}

	int update(Stack<StackFrame> stack, StackFrame frame, Callback callback) {
		if (frame.isComplete()) {
			return Script.COMPLETE_SCRIPT;
		}
		for (; frame.hasNext();) {
			Function command = frame.next();
			command.update(this, stack, callback);
		}
		if (frame.hasNext()) {
			return Script.UNCOMPLETE_FUNCTION;
		}
		return Script.COMPLETE_FUNCTION;
	}

	public void update(Stack<StackFrame> stack) {
		StackFrame frame = (StackFrame) stack.peek();
		frame.newTick();
		switch (update(stack, frame, callback)) {
		case Script.COMPLETE_SCRIPT:
			stack.pop();
			update();
			break;
		case Script.COMPLETE_FUNCTION:
			break;
		case Script.UNCOMPLETE_FUNCTION:
			update();
			break;
		}
	}

	public void update() {
		for (int i = 0; i < executionStacks.size(); i++) {
			Stack<StackFrame> stack = executionStacks.get(i);
			if (stack.empty()) {
				executionStacks.remove(stack);
			} else {
				update(stack);
			}
		}
	}

	public int getPackCount() {
		return packCount;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	void jump(Stack<StackFrame> stack, String name) {
		stack.pop();
		execute(stack, name);
	}

	void execute(Stack<StackFrame> stack, String name) {
		Script script = (Script) scripts.get(name);
		stack.push(new StackFrame(script));
	}

	public void call(String name) {
		Stack<StackFrame> stack = new Stack<StackFrame>();
		executionStacks.add(stack);
		execute(stack, name);
	}

	public void clear() {
		executionStacks.clear();
	}

	public PackTileFactory getTileFactory() {
		return tileFactory;
	}

	public void setTileFactory(PackTileFactory tileFactory) {
		this.tileFactory = tileFactory;
	}

	public PackAnimation getAnimation(String name) {
		PackAnimation animation = animations.get(name);
		if (animation != null) {
			return animations.get(name);
		}
		PackEntry entry = packs.get(packName).getEntry(name);
		if (entry != null) {
			animation = new PackAnimation(name, false);
			animation.addFrame(new PackFrame(entry));
		} else {
			try {
				entry = packs.get(packName).getEntry(Integer.parseInt(name));
				if (entry != null) {
					animation = new PackAnimation(name, false);
					animation.addFrame(new PackFrame(entry));
				}
			} catch (Exception e) {
			}
		}
		if (animation != null) {
			animations.put(name, animation);
		}
		return animation;
	}

	public Screen getScreen() {
		return screen;
	}

	public LTexturePack getPack(String name) {
		return packs.get(name);
	}

	public Script getScript(String name) {
		return scripts.get(name);
	}

	public PackTileMap getMap(String name) {
		return getMap(name, null);
	}

	public PackTileMap getMap(String name, PackTileFactory factory) {
		PackTileMap map = maps.get(name);
		if (map != null && map.isDirty()) {
			map.init(factory);
			map.update();
		}
		return map;
	}

	public void dispose() {
		if (packs != null) {
			for (LTexturePack pack : packs.values()) {
				if (pack != null) {
					pack.dispose();
					pack = null;
				}
			}
			packs.clear();
		}
		if (scripts != null) {
			scripts.clear();
			scripts = null;
		}
		if (packs != null) {
			packs.clear();
			packs = null;
		}
		if (maps != null) {
			maps.clear();
			maps = null;
		}
		if (animations != null) {
			animations.clear();
			animations = null;
		}
		if (executionStacks != null) {
			executionStacks.clear();
			executionStacks = null;
		}
		if (inputStream != null) {
			try {
				inputStream.close();
				inputStream = null;
			} catch (Exception e) {
			}
		}
	}

}
