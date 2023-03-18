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

public class TradeItem<T> extends Item<T> {

	protected String description;

	protected float sellPrice;

	protected float buyPrice;

	protected float quantity;

	public TradeItem(String name, T item, float sell, float buy, float quan, String des) {
		super(name, item);
		this.sellPrice = sell;
		this.buyPrice = buy;
		this.quantity = quan;
		this.description = des;
	}

	public String getDescription() {
		return description;
	}

	public TradeItem<T> setDescription(String description) {
		this.description = description;
		return this;
	}

	public float getSellPrice() {
		return sellPrice;
	}

	public TradeItem<T> setSellPrice(float sellPrice) {
		this.sellPrice = sellPrice;
		return this;
	}

	public float getBuyPrice() {
		return buyPrice;
	}

	public TradeItem<T> setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
		return this;
	}

	public float getQuantity() {
		return quantity;
	}

	public TradeItem<T> setQuantity(float quantity) {
		this.quantity = quantity;
		return this;
	}

	public TradeItem<T> cpy() {
		return new TradeItem<T>(_name, _item, sellPrice, buyPrice, quantity, description);
	}
}
