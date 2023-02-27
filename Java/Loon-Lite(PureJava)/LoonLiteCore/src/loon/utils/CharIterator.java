/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.utils;

import loon.LRelease;
import loon.LSysException;

public class CharIterator implements LRelease {
	
    public static final char DONE = '\uFFFF';

    private String text;
    private int begin;
    private int end;
 
    private int pos;

    public CharIterator(String text)
    {
        this(text, 0);
    }

    public CharIterator(String text, int pos)
    {
    this(text, 0, text.length(), pos);
    }

    public CharIterator(String text, int begin, int end, int pos) {
        if (text == null) {
            throw new LSysException();
        }
        this.text = text;
        if (begin < 0 || begin > end || end > text.length()) {
            throw new LSysException("Invalid substring range");
        }

        if (pos < begin || pos > end) {
            throw new LSysException("Invalid position");
        }
        this.begin = begin;
        this.end = end;
        this.pos = pos;
    }

    public void setText(String text) {
        if (text == null) {
            throw new LSysException();
        }
        this.text = text;
        this.begin = 0;
        this.end = text.length();
        this.pos = 0;
    }

    public char first()
    {
        pos = begin;
        return current();
    }

    public char last()
    {
        if (end != begin) {
            pos = end - 1;
        } else {
            pos = end;
        }
        return current();
     }

    public char setIndex(int p)
    {
    if (p < begin || p > end) {
            throw new LSysException("Invalid index");
    }
        pos = p;
        return current();
    }

    public char current()
    {
        if (pos >= begin && pos < end) {
            return text.charAt(pos);
        }
        else {
            return DONE;
        }
    }

    public char next()
    {
        if (pos < end - 1) {
            pos++;
            return text.charAt(pos);
        }
        else {
            pos = end;
            return DONE;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharIterator)) {
            return false;
        }
        CharIterator that = (CharIterator) obj;
        if (hashCode() != that.hashCode())
            return false;
        if (!text.equals(that.text))
            return false;
        if (pos != that.pos || begin != that.begin || end != that.end)
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        return text.hashCode() ^ pos ^ begin ^ end;
    }
    
    public char previous()
    {
        if (pos > begin) {
            pos--;
            return text.charAt(pos);
        }
        else {
            return DONE;
        }
    }

    public int getBeginIndex()
    {
        return begin;
    }

    public int getEndIndex()
    {
        return end;
    }

    public int getIndex()
    {
        return pos;
    }

	@Override
	public void close() {
		text = null;
	}

}
