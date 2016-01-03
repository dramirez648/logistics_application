package processor;

import java.util.List;
import java.util.TreeMap;

import order.Order;
import order.OrderItem;
import order.OrderItemProcessRecord;
import exception.LogisticsException;
import facility.Facility;
import facility.FacilityManager;
import facility.FacilityNetwork;

public abstract class AbstractOrderProcessor implements OrderProcessor{
	protected abstract List<Facility> getPrioritizedFacilityList(String itemId, int itemQuantity, int orderTime, Facility dst);
	
	public void process(Order order) {
		Facility dstFacility = FacilityManager.getFacility(order.getDstFacilityName());
		for (OrderItem orderItem : order.getItems()) {
			String itemId = orderItem.getItemId();
			int total = orderItem.getQuantity();
			List<Facility> factoryList = getPrioritizedFacilityList(itemId, total, order.getOrderTime(), dstFacility);
			for (Facility facility : factoryList) {
				int maxQuantity = facility.getMaxQuantityToProvide(orderItem.getItemId());
				int available = Math.min(total, maxQuantity);
				try {
					facility.getInventory().consumeItem(itemId, available);
				} catch (LogisticsException e) {}
				total -= available;
				TreeMap<Integer, Integer> bookMap = facility.getSchedule().tryToBook(order.getOrderTime(), available, true);
				int travelDays = 0;
				if (facility != dstFacility) {
					travelDays = (int)Math.round(FacilityNetwork.getShortestTravelTime(facility, dstFacility));
				}
				orderItem.addItemProcessRecord(
						new OrderItemProcessRecord(itemId, available, facility, 1.0 * available / facility.getRate(), order.getOrderTime() + bookMap.firstKey(), order.getOrderTime() + bookMap.lastKey(), travelDays));
				if (total > 0) continue;
				else 	break;
			}
			orderItem.processCompleted();
		}
	}
}
