package com.crater.ammazonawstest.manager;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.SecretKey;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.AwsEnvVarOverrideRegionProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;

public class AwsClientFactory {

    public static AmazonS3 createClient() throws IOException {
        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(2*60 * 1000);
        clientConf.setProtocol(Protocol.HTTPS);
        AWSCredentialsProvider credentialsProvider = getAwsCredentialsProvider();
        AwsEnvVarOverrideRegionProvider regionProvider = createRegionProvider();
        System.out.println(credentialsProvider.getCredentials().getAWSAccessKeyId());
        System.out.println(regionProvider.getRegion());
        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConf)
                .withCredentials(credentialsProvider)
                .withRegion(regionProvider.getRegion())
                .build();
    }

    public static AmazonS3 createEncryptionClient() throws IOException {
        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(60 * 1000);
        AWSCredentialsProvider credentialsProvider = getAwsCredentialsProvider();
        AwsEnvVarOverrideRegionProvider regionProvider = createRegionProvider();
        CryptoConfiguration cryptoConfiguration = new CryptoConfiguration();
        cryptoConfiguration.setAwsKmsRegion(RegionUtils.getRegion(regionProvider.getRegion()));
        KMSEncryptionMaterialsProvider materialsProvider = new
                KMSEncryptionMaterialsProvider("<kms-customer-master-key-id>");
        return AmazonS3EncryptionClientBuilder.standard()
                .withClientConfiguration(clientConf)
                .withCredentials(credentialsProvider)
                .withRegion(regionProvider.getRegion())
                .withCryptoConfiguration(cryptoConfiguration)
                .withEncryptionMaterials(materialsProvider)
                .build();
    }

    public static AmazonS3 createEncryptionClient(SecretKey secretKey) throws IOException {
        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(60 * 1000);
        AWSCredentialsProvider credentialsProvider = getAwsCredentialsProvider();
        AwsEnvVarOverrideRegionProvider regionProvider = createRegionProvider();
        CryptoConfiguration cryptoConfiguration = new CryptoConfiguration();
        cryptoConfiguration.setAwsKmsRegion(RegionUtils.getRegion(regionProvider.getRegion()));
        EncryptionMaterials materials = new EncryptionMaterials(secretKey);
        StaticEncryptionMaterialsProvider materialsProvider = new StaticEncryptionMaterialsProvider(materials);
        return AmazonS3EncryptionClientBuilder.standard()
                .withClientConfiguration(clientConf)
                .withCredentials(credentialsProvider)
                .withRegion(regionProvider.getRegion())
                .withCryptoConfiguration(cryptoConfiguration)
                .withEncryptionMaterials(materialsProvider)
                .build();
    }

    public static AWSCredentialsProvider getAwsCredentialsProvider() throws IOException {
        AWSCredentials credentials = getAwsCredentials();
        return new AWSStaticCredentialsProvider(credentials);
    }

    public static AwsEnvVarOverrideRegionProvider createRegionProvider() {
        return new AwsEnvVarOverrideRegionProvider();
    }

    private static AWSCredentials getAwsCredentials() throws IOException {
        AWSCredentials credentials;
        try (InputStream is = ListBucketsApp.class.getResourceAsStream("/credentials.properties")) {
            if (is == null) {
                throw new RuntimeException("Unable to load credentials from properties file.");
            }
            credentials = new PropertiesCredentials(is);
        }
        return credentials;
    }
}
