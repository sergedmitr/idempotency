package ru.sergdm.ws.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemName {

	public String getName() {
		String hostname = "Unknown";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
			return hostname;
		} catch (UnknownHostException ex) {
			System.out.println("Hostname can not be resolved");
			return "error";
		}
		
	}

	public String getAddress() {
		String address = "Unknown";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			address = addr.getHostAddress();
			return address;
		} catch (UnknownHostException ex) {
			System.out.println("Address can not be resolved");
			return "error";
		}
		
	}
}
