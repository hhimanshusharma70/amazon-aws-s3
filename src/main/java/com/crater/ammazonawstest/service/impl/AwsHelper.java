package com.crater.ammazonawstest.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.crater.ammazonawstest.manager.AwsClientFactory;

public class AwsHelper {

	private static String bname;

	public static String createBucket() throws IOException {
		AmazonS3 s3 = AwsClientFactory.createClient();
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName = "wedoshoesproduct" + UUID.randomUUID();
		bname = bucketName;
		try {
			s3.createBucket(bucketName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bname;
	}

	public static void createProductCategory(final String bucketName,
			final String categoryName, final File file) throws IOException {
		AmazonS3 s3 = AwsClientFactory.createClient();
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, categoryName, file);
	       ObjectMetadata objectMetadata = new ObjectMetadata();
	       objectMetadata.setCacheControl("max-age=2*60");
	        objectMetadata.setHeader("Expires", "Thu, 21 Sep 2017 07:44:21 GMT");
	        putObjectRequest.setMetadata(objectMetadata);
		 PutObjectResult result = s3.putObject(putObjectRequest);
	        System.out.println("Put file '" + file + "' under key " + categoryName + " to bucket " + bucketName + " (SSE: " + result.getSSEAlgorithm() + ")");
	  
	}

	public static Object getProductDetails(String bucketName,
			String path) {
		AmazonS3 s3 = null;
		try {
			s3 = AwsClientFactory.createClient();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, path));
		
		try {
		InputStream objectData=object.getObjectContent();
		File targetFile = new File("/home/vaishali/wedoshoes_projects/amazon-aws/"+object.getKey());
	    FileUtils.copyInputStreamToFile(objectData, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}


	public static Object listBucketDetails(String bucketName, String prefix) {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider().getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. ",
					e);
		}
		AmazonS3 s3 = new AmazonS3Client();
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
				.withBucketName(bucketName)
				.withPrefix(prefix));
		return objectListing.getObjectSummaries();
	}

}
