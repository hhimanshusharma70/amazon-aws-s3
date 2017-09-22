package com.crater.ammazonawstest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bucket {
	
	private final String bucketName;
	private final String key;
	private final String fileName;
	private final Integer standardIAccessTime;
	private final Integer glacierTime;
	private final Integer expirationTime;
	
	
	@SuppressWarnings("unused")
	private Bucket() {
		this(null,null,null,null,null,null);
	}

	public Bucket(String bucketName, String key, String fileName,final Integer standardIAccessTime,
			final Integer glacierTime,final Integer expirationTime) {
		super();
		this.bucketName = bucketName;
		this.key = key;
		this.fileName = fileName;
		this.standardIAccessTime=standardIAccessTime;
		this.glacierTime=glacierTime;
		this.expirationTime=expirationTime;
	}

	@JsonProperty("bucket_name")
	public String getBucketName() {
		return bucketName;
	}

	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	@JsonProperty("file")
	public String getFileName() {
		return fileName;
	}

	@JsonProperty("standard_infrequent_access_time")
	public Integer getStandardIAccessTime() {
		return standardIAccessTime;
	}

	@JsonProperty("glacier_time")
	public Integer getGlacierTime() {
		return glacierTime;
	}

	@JsonProperty("expiration_time")
	public Integer getExpirationTime() {
		return expirationTime;
	}
	
    
}
