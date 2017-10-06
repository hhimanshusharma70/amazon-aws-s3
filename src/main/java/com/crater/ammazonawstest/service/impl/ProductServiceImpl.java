package com.crater.ammazonawstest.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ReplicationDestinationConfig;
import com.amazonaws.services.s3.model.ReplicationRule;
import com.amazonaws.services.s3.model.ReplicationRuleStatus;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.crater.ammazonawstest.manager.AwsClientFactory;
import com.crater.ammazonawstest.model.Bucket;
import com.crater.ammazonawstest.model.ProductBucketDetails;
import com.crater.ammazonawstest.model.ReplicationConfig;
import com.crater.ammazonawstest.service.ProductService;


@Service
public class ProductServiceImpl  implements ProductService {

	   private static final String keyDir  = System.getProperty("java.io.tmpdir");
	    private static final SecureRandom srand = new SecureRandom();
	    
	@Override
	public String createWedoBucket() throws IOException {
		return AwsHelper.createBucket();
	}

    public void createProductCategory(final ProductBucketDetails productBucketDetails) throws IOException{
    	String cName=productBucketDetails.getCategoryName()+Instant.now().getEpochSecond();
    	File file = null;
		file = new File(productBucketDetails.getImage());
    	AwsHelper.createProductCategory(productBucketDetails.getProductBucketName(),productBucketDetails.getCategoryName(), file);
    }

	@Override
	public Object getProductDetails(final String bucketName,
			final String path) {
		return AwsHelper.getProductDetails(bucketName,path);
	}

	@Override
	public Object listBucketDetails(final String bucketName,final String prefix) {
		return AwsHelper.listBucketDetails(bucketName,prefix);
	}


	@Override
	public void createLifeBucketCycle(final String bucketName,final Integer standardInfrequentAccessTime,
			final Integer glacierAccessTime,final  Integer expirationTime,final Boolean isArchive) {
		 BucketLifecycleConfiguration.Rule rule =
	                new BucketLifecycleConfiguration.Rule()
	                        .withId("Transfer to IA, then GLACIER, then remove")
	                        .withFilter(new LifecycleFilter(
	                                new LifecycleTagPredicate(new Tag("archive", isArchive.toString()))))
	                        .addTransition(new BucketLifecycleConfiguration.Transition()
	                                .withDays(standardInfrequentAccessTime)
	                                .withStorageClass(StorageClass.StandardInfrequentAccess))
	                        .addTransition(new BucketLifecycleConfiguration.Transition()
	                                .withDays(glacierAccessTime)
	                                .withStorageClass(StorageClass.Glacier))
	                        .withExpirationInDays(expirationTime)
	                        .withStatus(BucketLifecycleConfiguration.ENABLED);
	        BucketLifecycleConfiguration conf =
	                new BucketLifecycleConfiguration()
	                        .withRules(rule);

	        AmazonS3 amazonS3 = null;
			try {
				amazonS3 = AwsClientFactory.createClient();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        amazonS3.setBucketLifecycleConfiguration(bucketName, conf);
	        BucketLifecycleConfiguration configuration = amazonS3.getBucketLifecycleConfiguration(bucketName);

	}

	@Override
	public Object getBucketLifeCycle(final String bucketName) {
		AmazonS3 amazonS3 = null;
		try {
			amazonS3 = AwsClientFactory.createClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return amazonS3.getBucketLifecycleConfiguration(bucketName);
	}

	@Override
	public void removeBucketLifeCycle(final String bucketName) {
		AmazonS3 amazonS3 = null;
		try {
			amazonS3 = AwsClientFactory.createClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
		amazonS3.deleteBucketLifecycleConfiguration(bucketName);
	}

	@Override
	public String restoreBucket(final Bucket bucket) {
		AmazonS3 amazonS3 = null;
		try {
			amazonS3 = AwsClientFactory.createClient();
		 RestoreObjectRequest requestRestore = new RestoreObjectRequest(bucket.getBucketName(), bucket.getKey(), bucket.getExpirationTime());
		 amazonS3.restoreObject(requestRestore);
         
         GetObjectMetadataRequest requestCheck = new GetObjectMetadataRequest(bucket.getBucketName(), bucket.getKey());          
         ObjectMetadata response = amazonS3.getObjectMetadata(requestCheck);
         
         Boolean restoreFlag = response.getOngoingRestore();
         return ((restoreFlag == true) ? "in progress" : "finished");
           
       } catch (AmazonS3Exception amazonS3Exception) {
           System.out.format("An Amazon S3 error occurred. Exception: %s", amazonS3Exception.toString());
       } catch (Exception ex) {
           System.out.format("Exception: %s", ex.toString());
       }  
		return StringUtils.EMPTY;
	}

	@Override
	public Object uploadWithEncryption(final Bucket bucket) {
		try {
			 File file = new File(bucket.getFileName());
	            EncryptionMaterials encryptionMaterials = new EncryptionMaterials(
	            		generateKey());
	            AmazonS3EncryptionClient encryptionClient = new AmazonS3EncryptionClient(
	                    AwsClientFactory.getAwsCredentialsProvider(),
	                    new StaticEncryptionMaterialsProvider(encryptionMaterials));
	            // 3. Upload the object.
	           encryptionClient.putObject(new PutObjectRequest(bucket.getBucketName(), bucket.getKey(),
	            		FileUtils.openInputStream(file), new ObjectMetadata()));
	        }         
	       catch (Exception ex) {
           System.out.format("Exception: %s", ex.toString());
       }  
		return null;
	}
	
	// Key Generates
	private  SecretKey generateSecretKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, new SecureRandom());
            return generator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }

}

	@Override
	public Object getObjectWithEncrytion(final String bucketName,
			 final String path) {
		AmazonS3 amazonS3 = null;
		S3Object object = null;
		try {
			  EncryptionMaterials encryptionMaterials = new EncryptionMaterials(
					  generateKey());
	            AmazonS3EncryptionClient encryptionClient = new AmazonS3EncryptionClient(
	            		AwsClientFactory.getAwsCredentialsProvider(),
	                    new StaticEncryptionMaterialsProvider(encryptionMaterials));
			
			S3Object downloadedObject = encryptionClient.getObject(bucketName, path);
		  InputStream objectData=downloadedObject.getObjectContent();
				File targetFile = new File("/home/vaishali/Desktop/"+downloadedObject.getKey());
			    FileUtils.copyInputStreamToFile(objectData, targetFile);
				
		  } catch (IOException e) {
					e.printStackTrace();
				}
		return object;
		
	}
	
	public KeyPair generateKey() {
		try{
		  byte[] bytes = FileUtils.readFileToByteArray(new File(
	                keyDir + "/private.key"));
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
	        PrivateKey pk = kf.generatePrivate(ks);

	        bytes = FileUtils.readFileToByteArray(new File(keyDir + "/public.key"));
	        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
	                new X509EncodedKeySpec(bytes));
	        KeyPair loadedKeyPair = new KeyPair(publicKey, pk);
	        return loadedKeyPair;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void multipartUpload(final Bucket bucket) throws InterruptedException {
		 TransferManager tm = null;
		try {
			tm = new TransferManager(AwsClientFactory.getAwsCredentialsProvider());
		} catch (IOException e) {
			e.printStackTrace();
		}        
		 File file = new File(bucket.getFileName());
	        Upload upload = tm.upload(
	        		bucket.getBucketName(), bucket.getKey(), file);
	        	upload.waitForCompletion();
	        	System.out.println("Upload complete.");
	    }

	@Override
	public void abortMultipartUpload(final Bucket bucket) throws IOException {
		// This will delete how many days before to delete 
		TransferManager tm = new TransferManager(new ProfileCredentialsProvider());        

        int sevenDays = 1000 * 60 * 60 * 24 * 7;
		Date oneWeekAgo = new Date(System.currentTimeMillis() - sevenDays);
        
        try {
        	tm.abortMultipartUploads(bucket.getBucketName(), oneWeekAgo);
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }
        
        // This will delete based on upload Id
        AmazonS3 s3Client = new AmazonS3Client(AwsClientFactory.getAwsCredentialsProvider()); 
        InitiateMultipartUploadRequest initRequest =
        	    new InitiateMultipartUploadRequest(bucket.getBucketName(), bucket.getKey());
        	InitiateMultipartUploadResult initResponse = 
        	               s3Client.initiateMultipartUpload(initRequest);

        	
        	s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
        	            bucket.getBucketName()
        	            , bucket.getKey(), initResponse.getUploadId()));
        	
       //This will list all mutli part upload 
        	ListMultipartUploadsRequest allMultpartUploadsRequest = 
        		     new ListMultipartUploadsRequest(bucket.getBucketName());
        		MultipartUploadListing multipartUploadListing = 
        		     s3Client.listMultipartUploads(allMultpartUploadsRequest); 	
        		System.out.println(multipartUploadListing.getBucketName());
       
	}

	@Override
	public void addCrossRegionReplicate(final ReplicationConfig replicationConfig) {
		String sourceBucketName = replicationConfig.getSourceBucketName(); 
	    String roleARN = "arn:aws:iam::"+replicationConfig.getAccountId()+":role/"+replicationConfig.getRoleName(); 
	    String destinationBucketArn = "arn:aws:s3:::"+replicationConfig.getDestinationBucketName(); 
		 AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	        try {
	            Map<String, ReplicationRule> replicationRules = new HashMap<String, ReplicationRule>();
	            replicationRules.put(
	                    "a-sample-rule-id",
	                    new ReplicationRule()
	                        .withPrefix("Tax/")
	                        .withStatus(ReplicationRuleStatus.Enabled)
	                        .withDestinationConfig(
	                                new ReplicationDestinationConfig()
	                                  .withBucketARN(destinationBucketArn)
	                                  .withStorageClass(StorageClass.Standard)
	                        )
	            );
	            s3Client.setBucketReplicationConfiguration(
	                    sourceBucketName,
	                    new BucketReplicationConfiguration()
	                        .withRoleARN(roleARN)
	                        .withRules(replicationRules)
	            ); 
	            BucketReplicationConfiguration replicationConfiguration = s3Client.getBucketReplicationConfiguration(sourceBucketName);
	            ReplicationRule rule = replicationConfiguration.getRule("a-sample-rule-id");
	            // FOR replicatin Status 
	            GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(replicationConfig.getSourceBucketName(), replicationConfig.getDestinationBucketName());
	            metadataRequest.setKey(replicationConfig.getKey());
	            ObjectMetadata metadata = s3Client.getObjectMetadata(metadataRequest);

	            System.out.println("Replication Status : " + metadata.getRawMetadataValue(Headers.OBJECT_REPLICATION_STATUS));
	        } catch (AmazonServiceException ase) {
	           ase.printStackTrace();
	        } catch (AmazonClientException ace) {
	           ace.printStackTrace();
	        }
	    }

	@Override
	public void removeBucket(final String bucketName) {
		AmazonS3 s3 = null;
		try {
			s3 = AwsClientFactory.createClient();
			removeObjects(s3,bucketName);
			removeVersions(s3,bucketName);
			s3.deleteBucket(bucketName);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void removeVersions(final AmazonS3 s3,final String bucketName) {
		VersionListing version_listing = s3
				.listVersions(new ListVersionsRequest()
						.withBucketName(bucketName));
		while (true) {
			for (Iterator<?> iterator = version_listing
					.getVersionSummaries().iterator(); iterator.hasNext();) {
				S3VersionSummary vs = (S3VersionSummary) iterator.next();
				s3.deleteVersion(
				bucketName, vs.getKey(), vs.getVersionId());
			}
			if (version_listing.isTruncated()) {
				version_listing = s3
						.listNextBatchOfVersions(version_listing);
			} else {
				break;
			}
		}
	}

	private void removeObjects(final AmazonS3 s3, final String bucketName) {
		ObjectListing object_listing = s3.listObjects(bucketName);
		while (true) {
			for (Iterator<?> iterator = object_listing.getObjectSummaries()
					.iterator(); iterator.hasNext();) {
				S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
				s3.deleteObject(bucketName, summary.getKey());
			}
			if (object_listing.isTruncated()) {
				object_listing = s3.listNextBatchOfObjects(object_listing);
			} else {
				break;
			}
		}
	}

	@Override
	public void emptyBucket(final String bucketName) {
		AmazonS3 s3 = null;
		try {
			s3 = AwsClientFactory.createClient();
			removeObjects(s3,bucketName);
			removeVersions(s3,bucketName);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
}
