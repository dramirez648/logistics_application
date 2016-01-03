package processor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import facility.Facility;
import facility.FacilityManager;
import facility.FacilityNetwork;

public class CostPriorOrderProcessor extends AbstractOrderProcessor{

	private static CostPriorOrderProcessor instance = new CostPriorOrderProcessor();
	@Override
	public Priority getPriority() {
		return Priority.COST;
	}

	@Override
	protected List<Facility> getPrioritizedFacilityList(final String itemId, final int itemQuantity, final int orderTime, final Facility dst) {	
		List<Facility> list = FacilityManager.getFacilitiesHaveItem(itemId);
		Collections.sort(list, new Comparator<Facility>() {

		public int compare(Facility left, Facility right) {
			double leftCost = getExtraUnitCost(left);
			double rightCost = getExtraUnitCost(right);
			
			if (leftCost > rightCost) {
				return 1;
			} else if (leftCost < rightCost) {
				return -1;
			} else {
				if (left.getMaxQuantityToProvide(itemId) < right.getMaxQuantityToProvide(itemId)) {
					return 1;
				} else {
					return 0;
				}
			}
		}	
		
		private double getExtraUnitCost(Facility curr) {
			int available = Math.min(curr.getMaxQuantityToProvide(itemId), itemQuantity);
			return curr.getCost() / curr.getRate()
					+ Math.round(FacilityNetwork.getShortestTravelTime(curr, dst)) * FacilityNetwork.DEFAULT_TRANSPORT_COST / available;
		}
		});
		return list;
	}
	
	public static CostPriorOrderProcessor getInstance() {
		return instance;
	}

}
