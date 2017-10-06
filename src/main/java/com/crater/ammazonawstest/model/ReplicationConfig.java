package com.crater.ammazonawstest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplicationConfig {
	
	private final String sourceBucketName;
	private final String key;
	private final String accountId;
	private final String roleName;
	private final String destinationBucketName;
	
	public ReplicationConfig() {
		this(null,null,null,null,null);
	}
	
	
	public ReplicationConfig(String sourceBucketName,final String key, String accountId,
			String roleName, String destinationBucketName) {
		super();
		this.sourceBucketName = sourceBucketName;
		this.key=key;
		this.accountId = accountId;
		this.roleName = roleName;
		this.destinationBucketName = destinationBucketName;
	}


	@JsonProperty("source_bucket_name")
	public String getSourceBucketName() {
		return sourceBucketName;
	}


	@JsonProperty("account_id")
	public String getAccountId() {
		return accountId;
	}

	@JsonProperty("role_name")
	public String getRoleName() {
		return roleName;
	}


	@JsonProperty("destination_bucket_name")
	public String getDestinationBucketName() {
		return destinationBucketName;
	}


	@JsonProperty("key")
	public String getKey() {
		return key;
	}
	
	

}
