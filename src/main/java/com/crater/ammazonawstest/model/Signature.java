package com.crater.ammazonawstest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Signature {
	
	String key;
	String dateStamp;
	String  regionName;
	String serviceName;
	
	Signature(){
		
	}

	public Signature(String key, String dateStamp, String regionName,
			String serviceName) {
		super();
		this.key = key;
		this.dateStamp = dateStamp;
		this.regionName = regionName;
		this.serviceName = serviceName;
	}


	@JsonProperty("key")
	public String getKey() {
		return key;
	}


	@JsonProperty("date_stamp")
	public String getDateStamp() {
		return dateStamp;
	}


	@JsonProperty("region_name")
	public String getRegionName() {
		return regionName;
	}


	@JsonProperty("service_name")
	public String getServiceName() {
		return serviceName;
	}
	
	

}
