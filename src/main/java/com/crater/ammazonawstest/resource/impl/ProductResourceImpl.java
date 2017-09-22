package com.crater.ammazonawstest.resource.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.SSECustomerKey;
import com.crater.ammazonawstest.model.Bucket;
import com.crater.ammazonawstest.model.ProductBucketDetails;
import com.crater.ammazonawstest.model.Signature;
import com.crater.ammazonawstest.resource.ProductResource;
import com.crater.ammazonawstest.service.ProductService;

@Component
public class ProductResourceImpl implements ProductResource {

	private final ProductService productService;
	
	@Autowired
	public ProductResourceImpl(final ProductService productService) {
		this.productService = productService;
	}

	@Override
	public Response createWedoBucket() throws IOException {
		return Response.ok(productService.createWedoBucket()).build();
		
	}

	@Override
	public Response createProductCategory(final ProductBucketDetails productBucketDetails) throws IOException {
		productService.createProductCategory(productBucketDetails);
		return Response.noContent().build();
	}

	

	@Override
	public Response getProductDetails(java.lang.String bucketName,
			java.lang.String path) {
		return Response.ok(productService.getProductDetails(bucketName,path)).build();

	}

	@Override
	public Response listBucketDetails(java.lang.String bucketName,
			java.lang.String prefix) {
		return Response.ok(productService.listBucketDetails(bucketName,prefix)).build();

	}

	@Override
	public Response createSignature(Signature signature) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        return Response.ok(productService.createSignature(signature).toString()).build();
	}

	@Override
	public Response createLifeBucketCycle(final String bucketName,final Integer standardInfrequentAccessTime,
			final Integer glacierAccessTime,final  Integer expirationTime,final Boolean isArchive)
			throws InvalidKeyException, NoSuchAlgorithmException,
			IllegalStateException, UnsupportedEncodingException {
	productService.createLifeBucketCycle(bucketName,standardInfrequentAccessTime,glacierAccessTime,expirationTime, isArchive);
	return Response.noContent().build();
	}

	@Override
	public Response getBucketLifeCycle(String bucketName) {
		return Response.ok(productService.getBucketLifeCycle(bucketName)).build();
	}

	@Override
	public Response removeBucketLifeCycle(String bucketName) {
		productService.removeBucketLifeCycle(bucketName);
		return Response.noContent().build();
	}

	@Override
	public Response restoreBucket(final Bucket bucket) {
		return Response.ok(productService.restoreBucket(bucket)).build();
	}

	@Override
	public Response uploadWithEncrytion(Bucket bucket) {
		return Response.ok(productService.uploadWithEncryption(bucket)).build();
	}

	@Override
	public Response getObjectWithEncrytion(String bucketName,
			 String path,SSECustomerKey encyptionKey) {
		return Response.ok(productService.getObjectWithEncrytion(bucketName,path,encyptionKey)).build();
	}

}
