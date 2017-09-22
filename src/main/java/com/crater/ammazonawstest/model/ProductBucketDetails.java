package com.crater.ammazonawstest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ProductBucketDetails {
	
	private final String productBucketName;
	private final String categoryName;
	private final String image;
	
	@SuppressWarnings("unused")
	private ProductBucketDetails() {
		this(null,null,null);
	}
	
	
	public ProductBucketDetails(final String productBucketName,final String categoryName,final String image) {
		this.productBucketName=productBucketName;
		this.categoryName=categoryName;
		this.image=image;
	}

	
	
	@JsonProperty("product_bucket_name")
	public String getProductBucketName() {
		return productBucketName;
	}


	@JsonProperty("category_name")
	public String getCategoryName() {
		return categoryName;
	}

	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	
	
	
}
