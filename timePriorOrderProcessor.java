package processor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import facility.Facility;
import facility.FacilityManager;
import facility.FacilityNetwork;

public class TimePriorOrderProcessor extends AbstractOrderProcessor{
	
	private static TimePriorOrderProcessor instance = new TimePriorOrderProcessor();

	@Override
	public Priority getPriority() {
		return Priority.TIME;
	}


	@Override
	protected List<Facility> getPrioritizedFacilityList(final String itemId, final int itemQuantity, final int orderTime, final Facility dst) {	
		List<Facility> list = FacilityManager.getFacilitiesHaveItem(itemId);
		Collections.sort(list, new Comparator<Facility>() {

		public int compare(Facility left, Facility right) {
			double leftTime = getTotalDays(left);
			double rightTime = getTotalDays(right);
			
			if (leftTime > rightTime) {
				return 1;
			} else if (leftTime < rightTime) {
				return -1;
			} else {
				if (left.getMaxQuantityToProvide(itemId) < right.getMaxQuantityToProvide(itemId)) {
					return 1;
				} else {
					return 0;
				}
			}
		}	
		
		private int getTotalDays(Facility curr) {
			int available = Math.min(curr.getMaxQuantityToProvide(itemId), itemQuantity);
			return curr.getSchedule().tryToBook(orderTime, available, false).lastKey() 
					+ 1
					+ (int)Math.round(FacilityNetwork.getShortestTravelTime(curr, dst)); 
		}
		});
		return list;
	}
	
	public static TimePriorOrderProcessor getInstance() {
		return instance;
	}


}
