package kr.gdu.logic;

import java.util.ArrayList;
import java.util.List;

public class Cart {
	private List<ItemSet> itemSetList = new ArrayList<ItemSet>();

	public List<ItemSet> getItemSetList() {
		return itemSetList;
	}

	public void push(ItemSet itemSet) {
		for (ItemSet itemS : itemSetList) {
			if (itemS.getItem().equals(itemSet.getItem())) {
				itemS.setQuantity(itemS.getQuantity()+itemSet.getQuantity());
				return;
			}
		}
		itemSetList.add(itemSet);
	}

	public int getTotal() { // get프로퍼티 : total 프로퍼티
		return itemSetList.stream().mapToInt(s -> s.getItem().getPrice() * s.getQuantity()).sum();
	}
}
