package com.mycompany.camel;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class FlightsAggregationStrategy implements AggregationStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Flight flight= newExchange.getIn().getBody(Flight.class);
		if(oldExchange==null)
		{
			List<Flight> flights=new ArrayList<Flight>();
			flights.add(flight);
			newExchange.getIn().setBody(flights);
			return newExchange;
		}else {
			List<Flight> flights=oldExchange.getIn().getBody(List.class);
			flights.add(flight);
			return oldExchange;
		}
		
	}

}
