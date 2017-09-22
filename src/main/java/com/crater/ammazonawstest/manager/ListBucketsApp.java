package com.crater.ammazonawstest.manager;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListBucketsApp {
    private static final Logger LOGGER = Logger.getLogger(ListBucketsApp.class.getName());

    public static void main(String[] args) throws IOException {
        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        List<Bucket> buckets = amazonS3.listBuckets();
        for (Bucket bucket : buckets) {
            LOGGER.log(Level.INFO, bucket.getName() + ": " + bucket.getCreationDate());
        }
    }
}
