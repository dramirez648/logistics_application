package facility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import exception.InvalidParamException;
import exception.LogisticsException;
import exception.UnknownItemException;

public class Inventory {

	private Map<String, Integer> itemMap = new HashMap<String, Integer>();
	private Map<String, Integer> backedItemMap = new HashMap<String, Integer>();
	
	public void setItemQuantity(String itemId, int quantity) throws LogisticsException {
		if (!Catalog.hasItem(itemId)) {
			throw new UnknownItemException(itemId);
		}
		if (quantity < 0) {
			throw new InvalidParamException("Negative item quantity: " + quantity);
		}
		itemMap.put(itemId, quantity);
		backedItemMap.put(itemId, quantity);
	}
	
	public boolean hasItem(String itemId) {
		return itemMap.containsKey(itemId) && itemMap.get(itemId) > 0;
	}
	
	public int getItemQuantity(String itemId) {
		if (hasItem(itemId)) {
			return itemMap.get(itemId);
		} else {
			return 0;
		}
	}
	
	public Set<String> getDepletedItems() {
		Set<String> items = new HashSet<String>();
		for (Map.Entry<String, Integer> item : itemMap.entrySet()) {
			if (item.getValue() == 0) {
				items.add(item.getKey());
			}
		}
		
		return items;
	}
	
	public void consumeItem(String itemName, int quantity) throws LogisticsException {
		Integer total = itemMap.get(itemName);
		if (total == null) {
			throw new LogisticsException("Consuming a non-existing item: " + itemName);
		} else if (total < quantity) {
			throw new LogisticsException("Not enough quantity to consume, item: " 
					+ itemName + ", available quantity: " + total + ", consuming quantity: " + quantity);
		} else {
			itemMap.put(itemName, total - quantity);
		}
	}
	
	public Map<String, Integer> getItems() {
		return itemMap;
	}
	
	public void reset() {
		itemMap.clear();
		itemMap.putAll(backedItemMap);
	}
	
	
}
