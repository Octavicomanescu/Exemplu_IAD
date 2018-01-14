package com.mycompany.camel;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.apache.camel.model.dataformat.JsonLibrary;
public class MainRouteBuilder extends RouteBuilder {

	private double latitude;
	private double longitude;
	private double dinstance;
	private int port;

	public MainRouteBuilder(double latitude, double longitude, double distance, int port) {
		this.latitude=latitude;
		this.longitude=longitude;
		this.dinstance=distance;
		this.port=port;
		
	}

	public MainRouteBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure() throws Exception {
		
		WebsocketComponent component=getContext().getComponent("websocket",WebsocketComponent.class);	
		
		component.setPort(port);
		component.setStaticResources("classpath:.");
		onException(Exception.class).log("${body}");
		from("timer:poll?fixedRate=true&period=1000&delay=0")
		.doTry()
		.setHeader(Exchange.HTTP_METHOD,constant("GET"))
		.setBody(constant(""))
		.to("jetty:https://www.flightradar24.com/balance.json")
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		.bean(LoadBalancingInstance.class,"extract")
		.setHeader("id")
		.method(MainRouteBuilder.class,"getBatchId")
		.split().body()
		.aggregate(new LoadBalancingInstanceAggregationStrategy())
		.header("id")
		.completionTimeout(1000)
		.setHeader(Exchange.HTTP_METHOD,constant("GET"))
		.setHeader("host",simple("${body.url}"))
		.setBody(constant(""))
		.recipientList(simple("jetty:https://data.flightradar24.com/zones/fcgi/feed.js?bounds=44.43,26.1,131.55,133.55&adsb=1&mlat=1&faa=1&flarm=1&estimated=1&air=1&gnd=1&vehicles=1&gliders=1&array=1"))
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		.setHeader("id")
		.method(MainRouteBuilder.class,"getBatchId")
		.bean(Flight.class,"extract")
		.split().body()
		.filter(new FlightsFilterPredicate(latitude,longitude,dinstance))
		.enrich("direct:flight",new FlightAggregationStrategy())
		.resequence(simple("${body.distance}"))
		.aggregate(new FlightsAggregationStrategy())
		.header("id")
		.completionTimeout(1000)
		.marshal().json(JsonLibrary.Jackson)
		.to("websocket:flights?sendToAll=true");
		from("direct:flight")
		.doTry()
		.setHeader(Exchange.HTTP_METHOD,constant("GET"))
		.setHeader(Exchange.HTTP_QUERY,simple("?f=${body.code}"))
		.setBody(constant(""))
		.recipientList(simple("jetty:https://data.flightradar24.com/_external/planedata_json.1.3.php"))
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		;
	}
	
	public static String getBatchId() {
		return UUID.randomUUID().toString();
	}
	
	
}
