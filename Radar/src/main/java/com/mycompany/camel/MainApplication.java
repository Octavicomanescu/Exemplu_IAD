package com.mycompany.camel;

import org.apache.camel.main.Main;

import java.util.ResourceBundle;

public class MainApplication {
	
	private static final ResourceBundle BUNDLE=ResourceBundle.getBundle(MainApplication.class.getName());
	
	public static void main(String... args) throws Exception {
        Main main = new Main();
        
        double latitude=Double.parseDouble(BUNDLE.getString("latitude"));
        double longitude=Double.parseDouble(BUNDLE.getString("longitude"));
        double distance=Double.parseDouble(BUNDLE.getString("distance"));
        int port=Integer.parseInt(BUNDLE.getString("port"));
        main.addRouteBuilder(new MainRouteBuilder(latitude,longitude,distance,port));
        main.run(args);
    }
}