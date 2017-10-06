package com.crater.ammazonawstest.service;

import java.io.IOException;

import com.crater.ammazonawstest.model.Bucket;
import com.crater.ammazonawstest.model.ProductBucketDetails;
import com.crater.ammazonawstest.model.ReplicationConfig;

public interface ProductService {

	String createWedoBucket() throws IOException;

	void createProductCategory(ProductBucketDetails productBucketDetails) throws IOException;

	Object getProductDetails(String bucketName, String path);

	Object listBucketDetails(String bucketName, String prefix);


	void createLifeBucketCycle(String bucketName,Integer standardInfrequentAccessTime, Integer glacierAccessTime,
			Integer expirationTime, Boolean isArchive);

	Object getBucketLifeCycle(String bucketName);

	void removeBucketLifeCycle(String bucketName);

	Object restoreBucket(Bucket bucket);

	Object uploadWithEncryption(Bucket bucket);

	Object getObjectWithEncrytion(String bucketName, String path);

	void multipartUpload(Bucket bucket) throws InterruptedException;

	void abortMultipartUpload(Bucket bucket) throws IOException;

	void addCrossRegionReplicate(ReplicationConfig replicationConfig);

	void removeBucket(String bucketName);

	void emptyBucket(String bucketName);

}
