package com.mycompany.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class FlightsFilterPredicate implements Predicate {

	private double latitude;
	private double longitude;
	@SuppressWarnings("unused")
	private double dinstance;

	public FlightsFilterPredicate(double latitude, double longitude, double dinstance) {
		this.latitude=latitude;
		this.longitude=longitude;
		this.dinstance=dinstance;
	
	}

	@Override
	public boolean matches(Exchange exchange) {
		Flight flight=exchange.getIn().getBody(Flight.class);
		double distance=distanceBetween(latitude, longitude, flight.getLatitude(), flight.getLongitude());
		flight.setDistance(distance/1000);
		//return flight.getCode()!=null&& flight.getDistance()<=this.dinstance;
		
		return true;
	}
	
	public static double distanceBetween(double sourceLatitude,double sourceLongitude,double destinationLatitude,double destinationLongitude) {
		double R=6378137;
		double dLat=(destinationLatitude-sourceLatitude)*Math.PI/180;
		double dLng=(destinationLongitude-sourceLongitude)*Math.PI/180;
		double a=Math.sin(dLat/2)*Math.sin(dLat)/2+Math.cos(sourceLatitude*Math.PI/180)*Math.cos(destinationLatitude*Math.PI/180)*Math.sin(dLng/2)*Math.sin(dLng/2);
		double c=2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
		double d=R*c;
		return Math.round(d);
	}
	
	
	
	
	
	
	

}
