package com.crater.ammazonawstest.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.crater.ammazonawstest.model.Bucket;
import com.crater.ammazonawstest.model.ProductBucketDetails;
import com.crater.ammazonawstest.model.ReplicationConfig;

@Path("/api/v1/products")
public interface ProductResource {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response createWedoBucket () throws IOException;
	
	@POST
	@Path("/category")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response  createProductCategory( ProductBucketDetails productBucketDetails) throws IOException;

	@GET
	@Path("{bucket_name}/category")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response  getProductDetails(@PathParam("bucket_name") String bucketName,
			@QueryParam("keyPath") String path);

	@GET
	@Path("{bucket_name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response listBucketDetails(@PathParam("bucket_name") String bucketName,
			@QueryParam("prefix") String prefix);
	
	/**
	 * Delete and Empty Bucket
	 */
	
	@DELETE
	@Path("{bucket_name}")
	@Produces(MediaType.APPLICATION_JSON)
	Response deleteBucket(@PathParam("bucket_name") String bucketName);
	
	@DELETE
	@Path("{bucket_name}/empty")
	@Produces(MediaType.APPLICATION_JSON)
	Response emptyBucket(@PathParam("bucket_name") String bucketName);
	
	
	/**
	 * BUCKET LIFECYCLE RELATED METHODS
	 */
	
	
	@POST 
	@Path("{bucket_name}/lifecycle")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response  createLifeBucketCycle(@PathParam("bucket_name") String bucketName, @QueryParam("standard_infrequent_access_time") @DefaultValue("35") Integer standardInfrequentAccessTime ,@QueryParam("glacier_access_time") @DefaultValue("365") Integer glacierAccessTime,@QueryParam("expiration_time") @DefaultValue("35") Integer expirationTime, @QueryParam("is_archive") @DefaultValue("true") Boolean isArchive) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException;

	@GET
	@Path("{bucket_name}/lifecycle")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response getBucketLifeCycle(@PathParam("bucket_name") String bucketName);
	
	@DELETE
	@Path("{bucket_name}/lifecycle")
	@Produces(MediaType.APPLICATION_JSON)
	Response removeBucketLifeCycle(@PathParam("bucket_name") String bucketName);
	
	
	/**
	 * RESTORING OBJECT FROM GLACIER 
	 */
	
	@POST
	@Path("/restore")
	@Produces(MediaType.APPLICATION_JSON)
	Response restoreBucket(Bucket bucket);
	
	
	
	/**
	 * Encryption and Decryption  on Objects
	 */
	
	@POST
	@Path("/upload/")
	@Produces(MediaType.APPLICATION_JSON)
	Response uploadWithEncrytion(Bucket bucket);
	
	@GET
	@Path("/retreive")
	@Produces(MediaType.APPLICATION_JSON)
	Response getObjectWithDecrytion(@QueryParam("bucket_name") String bucketName,
			@QueryParam("key") String path);
	
	/**
	 * Multipart Upload Case 
	 * @throws InterruptedException 
	 */
	
	@POST
	@Path("/multipart-upload")
	@Produces(MediaType.APPLICATION_JSON)
	Response multipartUpload(Bucket bucket) throws InterruptedException;
	
	@POST
	@Path("/abort/multipart-upload")
	@Produces(MediaType.APPLICATION_JSON)
	Response abortMultipartUpload(Bucket bucket) throws InterruptedException, IOException;
	
	/**
	 * Cross Region Replication 
	 */
	
	@POST
	@Path("/cross/replicate")
	@Produces(MediaType.APPLICATION_JSON)
	Response addCrossRegionReplicate(ReplicationConfig replicationConfig) throws InterruptedException, IOException;
	
	
}
