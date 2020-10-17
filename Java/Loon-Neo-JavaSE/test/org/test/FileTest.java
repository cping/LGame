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
package org.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import loon.utils.CollectionUtils;
import loon.utils.StringUtils;

public class FileTest<T> implements Iterable<FileTest<T>> {

    public T data;
    public FileTest<T> parent;
    public List<FileTest<T>> children;
    
    private static int fileCount;
    private static int dirCount;

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    private List<FileTest<T>> elementsIndex;

    public FileTest(T data) {
        this.data = data;
        this.children = new LinkedList<FileTest<T>>();
        this.elementsIndex = new LinkedList<FileTest<T>>();
        this.elementsIndex.add(this);
    }

    public FileTest<T> addChild(T child) {
        FileTest<T> childNode = new FileTest<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    private void registerChildForSearch(FileTest<T> node) {
        elementsIndex.add(node);
        if (parent != null)
            parent.registerChildForSearch(node);
    }

    public FileTest<T> findTreeNode(Comparable<T> cmp) {
        for (FileTest<T> element : this.elementsIndex) {
            T elData = element.data;
            if (cmp.compareTo(elData) == 0)
                return element;
        }

        return null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @Override
    public Iterator<FileTest<T>> iterator() {
        TreeNodeIter<T> iter = new TreeNodeIter<T>(this);
        return iter;
    }

    public static FileTest<File> createDirTree(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        FileTest<File> DirRoot = new FileTest<File>(folder);
        File[] files = folder.listFiles();
        
        for (File file : files) {
            if (file.isDirectory()) {
                appendDirTree(file, DirRoot);
            } else {
                appendFile(file, DirRoot);
            }
        }
        return DirRoot;
    }

 
    public static void appendDirTree(File folder, FileTest<File> DirRoot)
    {
    	dirCount++;
        DirRoot.addChild(folder);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                appendDirTree(file,
                        DirRoot.children.get(DirRoot.children.size() - 1));
            } else {
                appendFile(file,
                        DirRoot.children.get(DirRoot.children.size() - 1));
            }
        }
    }

    public static void appendFile(File file, FileTest<File> filenode) {
    	fileCount++;
        filenode.addChild(file);
    }


    public static String renderDirectoryTree(FileTest<File> tree) {
        List<StringBuilder> lines = renderDirectoryTreeLines(tree);
        String newline = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(lines.size() * 20);
        for (StringBuilder line : lines) {
            sb.append(line);
            sb.append(newline);
        }
        return sb.toString();
    }

    public static List<StringBuilder>
        renderDirectoryTreeLines(FileTest<File> tree) {
    	
            List<StringBuilder> result = new LinkedList<>();
            result.add(new StringBuilder().append(tree.data.getName()));
            Iterator<FileTest<File>> iterator = tree.children.iterator();
            while (iterator.hasNext()) {
                List<StringBuilder> subtree =
                    renderDirectoryTreeLines(iterator.next());
                if (iterator.hasNext()) {
                    addSubtree(result, subtree);
                } else {
                    addLastSubtree(result, subtree);
                }
            }
            return result;
                }

    private static void addSubtree(List<StringBuilder> result,
            List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
     //   for(;iterator.hasNext();){
          StringBuilder sbr=	iterator.next();
          result.add(sbr.insert(0, "├── "));
     //   }
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "│   "));
        }
    }

    private static void addLastSubtree(List<StringBuilder> result,
            List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least
        /*one
            line which is tree.getData()
            result.add(iterator.next().insert(0, "└── "));*/
       // for(;iterator.hasNext();){
            StringBuilder sbr=	iterator.next();
            result.add(sbr.insert(0, "└── "));
       //   }
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "    "));
        }
    }

    public static void main(String[] args) {
        File file = new File("D:\\ttss\\src");
        FileTest<File> DirTree = createDirTree(file);
        String result = renderDirectoryTree(DirTree);
        System.out.println(result);
        try {
            File newTextFile = new File("D:\\ttss\\DirectoryTree.txt");

            FileWriter fw = new FileWriter(newTextFile);
            fw.write(result);
            fw.write("File:"+fileCount+",Directory:"+dirCount);
            fw.close();

        } catch (IOException iox) {
            iox.printStackTrace();
        }

    }
}