package com.crater.ammazonawstest.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.amazonaws.services.s3.model.SSECustomerKey;
import com.crater.ammazonawstest.model.Bucket;
import com.crater.ammazonawstest.model.ProductBucketDetails;
import com.crater.ammazonawstest.model.Signature;

public interface ProductService {

	String createWedoBucket() throws IOException;

	void createProductCategory(ProductBucketDetails productBucketDetails) throws IOException;

	Object getProductDetails(String bucketName, String path);

	Object listBucketDetails(String bucketName, String prefix);

	String createSignature(Signature signature) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException;


	void createLifeBucketCycle(String bucketName,Integer standardInfrequentAccessTime, Integer glacierAccessTime,
			Integer expirationTime, Boolean isArchive);

	Object getBucketLifeCycle(String bucketName);

	void removeBucketLifeCycle(String bucketName);

	Object restoreBucket(Bucket bucket);

	Object uploadWithEncryption(Bucket bucket);

	Object getObjectWithEncrytion(String bucketName, String path,
			SSECustomerKey encyptionKey);

}
