package facility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import exception.InvalidParamException;
import exception.LogisticsException;
import exception.UnknownFacilityException;

public class FacilityNetwork {

	public static final int DEFAULT_TRAVEL_SPEED_MPH = 50;
	public static final int DEFAULT_TRAVEL_HOUR_PER_DAY = 8;
	public static final int DEFAULT_TRANSPORT_COST = 500;
	
	private static int travelSpeed = DEFAULT_TRAVEL_SPEED_MPH;
	private static int travelHoursPerDay = DEFAULT_TRAVEL_HOUR_PER_DAY;
	
	private static class Vertex implements Comparable<Vertex> {
		private final Facility facility;
		
	  int minDistance = Integer.MAX_VALUE;
		Vertex previous;
	
		public Vertex(Facility facility) {
			this.facility = facility;
		}
		
		public Facility getFacility() {
			return facility;
		}
	
		public String toString() {
			return facility.getName();
		}
	
		public int getMinDistance() {
			return minDistance;
		}
	
		public int compareTo(Vertex other) {
			return Integer.compare(minDistance, other.minDistance);
		}
	}

	private static Map<Facility, Vertex> vertexMap =
			new HashMap<Facility, Vertex>();
	
	private static Vertex getVertex(Facility facility) {
		if (vertexMap.containsKey(facility)) {
			return vertexMap.get(facility);
		} else {
			Vertex v = new Vertex(facility);
			vertexMap.put(facility, v);
			return v;
		}
	}
	
	private static void computePaths(Vertex source) { //Dijkstra
		source.minDistance = 0; // set distance of source to 0
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>(); // make
	                                                                 // PriorityQueue
		vertexQueue.add(source); // add starting node to PriorityQueue

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();
	
			//visit each edge exiting u
			for (Map.Entry<Facility, Integer> entry : u.getFacility().getAdjacencies().entrySet()) 	{
				Vertex v = getVertex(entry.getKey()); // destination node
				int weight = entry.getValue(); // set distance
				int distanceThroughU = u.getMinDistance() + weight; // distance between
				                                                    // initial node to
				                                                    // next node
				if (distanceThroughU < v.getMinDistance()) {
					vertexQueue.remove(v);
		
					v.minDistance = distanceThroughU;
					v.previous = u;
					vertexQueue.add(v);
				}
			}
		}
	}

	private static List<Vertex> getShortestPathTo(Vertex target) {
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);
	
		Collections.reverse(path);
		return path;
	}
	
	public static int getTravelSpeed() {
		return travelSpeed;
	}

	public static void setTravelSpeed(int travelSpeed) throws LogisticsException {
		if (travelSpeed <= 0) {
			throw new InvalidParamException("Invalid travel speed value: " + travelSpeed);
		}
		FacilityNetwork.travelSpeed = travelSpeed;
	}

	public static int getTravelHoursPerDay() {
		return travelHoursPerDay;
	}

	public static void setTravelHoursPerDay(int travelHoursPerDay) throws LogisticsException {
		if (travelHoursPerDay <= 0) {
			throw new InvalidParamException("Invalid travel hours per day value: " + travelHoursPerDay);
		}
		FacilityNetwork.travelHoursPerDay = travelHoursPerDay;
	}
	
	public static List<Facility> getShortestPath(Facility src, Facility dst) {
		vertexMap.clear();
		computePaths(getVertex(src));
		List<Vertex> vList = getShortestPathTo(getVertex(dst));
		List<Facility> fList = new ArrayList<Facility>();
		
		for (Vertex v : vList) {
			fList.add(v.getFacility());
		}
		return fList;		
	}		

	public static double getShortestTravelTime(String srcName, String dstName) throws LogisticsException {
		Facility src = FacilityManager.getFacility(srcName);
		if (src == null) {
			throw new UnknownFacilityException(srcName);
		}
		Facility dst = FacilityManager.getFacility(dstName);
		if (dst == null) {
			throw new UnknownFacilityException(dstName);
		}
		return getShortestTravelTime(src, dst);
	}
	
	public static double getShortestTravelTime(Facility src, Facility dst) {
		List<Facility> list = getShortestPath(src, dst);
		Facility last = null;
		int totalDistance = 0;
		for (Facility f : list) {
			if (f == src) {
				last = f;
				continue;
			}
			totalDistance += f.getDistanceToAdjFacility(last);
			last = f;
		}
		return 1.0 * totalDistance / travelSpeed / travelHoursPerDay;
	}
}
