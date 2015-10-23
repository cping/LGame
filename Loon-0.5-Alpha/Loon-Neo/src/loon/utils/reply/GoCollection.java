/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.reply;

public abstract class GoCollection<T> extends Bypass {

    private Var<Integer> _sizeView;
    
    public abstract int size ();

    public synchronized VarView<Integer> sizeView () {
        if (_sizeView == null) {
            _sizeView = Var.create(size());
        }
        return _sizeView;
    }

    public VarView<Boolean> isEmptyView () {
        return sizeView().map(Functions.lessThanEqual(0));
    }

    public VarView<Boolean> isNonEmptyView () {
        return sizeView().map(Functions.greaterThan(0));
    }

    protected void updateSize () {
        if (_sizeView != null) {
        	_sizeView.update(size());
        }
    }

    @Override protected void notify (Notifier notifier, Object a1, Object a2, Object a3) {
        try {
            super.notify(notifier, a1, a2, a3);
        } finally {
            updateSize();
        }
    }

}
