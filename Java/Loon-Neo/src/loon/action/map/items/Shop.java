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
package loon.action.map.items;

import loon.events.QueryEvent;
import loon.events.ShopListener;
import loon.utils.Calculator;
import loon.utils.TArray;

public class Shop<T> {

	private Calculator _amount;

	private TArray<TradeItem<T>> _list;

	private ShopListener<T> _shoplistener = null;

	public Shop(Calculator amount, TArray<TradeItem<T>> list) {
		this._amount = amount;
		this._list = list;
	}

	public boolean buyItem(final Shop<T> other, final TradeItem<T> item, final float quan) {
		if (item.quantity < quan) {
			return false;
		}
		if (!other._list.contains(item)) {
			return false;
		}

		float cost = item.buyPrice * quan;

		if (_amount.getFloat() < cost) {
			return false;
		}

		_amount = _amount.sub(cost);
		other._amount.add(cost);

		TradeItem<T> copy = item.cpy();
		copy.quantity = quan;

		if (_shoplistener != null) {
			_shoplistener.onBuys(copy);
		}
		if (other._shoplistener != null) {
			other._shoplistener.onSell(copy);
		}
		item.quantity -= quan;

		if (_list.contains(item)) {
			_list.find(new QueryEvent<TradeItem<T>>() {

				@Override
				public boolean hit(TradeItem<T> t) {
					boolean result = (t == item);
					if (result) {
						t.quantity += quan;
					}
					return result;
				}
			});
		} else {
			TradeItem<T> t = item.cpy();
			t.quantity = quan;
			_list.add(t);
		}
		if (item.quantity == 0) {
			other._list.remove(item);
		}
		return true;
	}
}
