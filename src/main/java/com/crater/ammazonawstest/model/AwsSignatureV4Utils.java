package com.crater.ammazonawstest.model;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsSignatureV4Utils {
    private static final Logger logger = LoggerFactory.getLogger(AwsSignatureV4Utils.class);

    public static byte[] computeHmacSHA256(byte[] key, String data) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException,
            UnsupportedEncodingException {
        String algorithm = "HmacSHA256";
        String charsetName = "UTF-8";

        Mac sha256_HMAC = Mac.getInstance(algorithm);
        SecretKeySpec secret_key = new SecretKeySpec(key, algorithm);
        sha256_HMAC.init(secret_key);

        return sha256_HMAC.doFinal(data.getBytes(charsetName));
    }

    public static byte[] computeHmacSHA256(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException,
            UnsupportedEncodingException {
        return computeHmacSHA256(key.getBytes(), data);
    }

    public static String getSignatureV4(String accessSecretKey, String date, String region, String regionService, String signing, String stringToSign)
            throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {

        byte[] dateKey = computeHmacSHA256(accessSecretKey, date);
        logger.debug("dateKey: {}", Hex.encodeToString(dateKey));

        byte[] dateRegionKey = computeHmacSHA256(dateKey, region);
        logger.debug("dateRegionKey: {}", Hex.encodeToString(dateRegionKey));

        byte[] dateRegionServiceKey = computeHmacSHA256(dateRegionKey, regionService);
        logger.debug("dateRegionServiceKey: {}", Hex.encodeToString(dateRegionServiceKey));

        byte[] signingKey = computeHmacSHA256(dateRegionServiceKey, signing);
        logger.debug("signingKey: {}", Hex.encodeToString(signingKey));

        byte[] signature = computeHmacSHA256(signingKey, stringToSign);
        logger.debug("signature: {}", Hex.encodeToString(signature));

        return Hex.encodeToString(signature);
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        // http://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-post-example.html
        String accessSecretKey = "AWS4" + "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
        String date = "20130806";
        String region = "us-east-1";
        String regionService = "s3";
        String signing = "aws4_request";
        String stringToSign = "eyAiZXhwaXJhdGlvbiI6ICIyMDEzLTA4LTA3VDEyOjAwOjAwLjAwMFoiLA0KICAiY29uZGl0aW9ucyI6IFsNCiAgICB7ImJ1Y2tldCI6ICJleGFtcGxlYnVja2V0In0sDQogICAgWyJzdGFydHMtd2l0aCIsICIka2V5IiwgInVzZXIvdXNlcjEvIl0sDQogICAgeyJhY2wiOiAicHVibGljLXJlYWQifSwNCiAgICB7InN1Y2Nlc3NfYWN0aW9uX3JlZGlyZWN0IjogImh0dHA6Ly9leGFtcGxlYnVja2V0LnMzLmFtYXpvbmF3cy5jb20vc3VjY2Vzc2Z1bF91cGxvYWQuaHRtbCJ9LA0KICAgIFsic3RhcnRzLXdpdGgiLCAiJENvbnRlbnQtVHlwZSIsICJpbWFnZS8iXSwNCiAgICB7IngtYW16LW1ldGEtdXVpZCI6ICIxNDM2NTEyMzY1MTI3NCJ9LA0KICAgIFsic3RhcnRzLXdpdGgiLCAiJHgtYW16LW1ldGEtdGFnIiwgIiJdLA0KDQogICAgeyJ4LWFtei1jcmVkZW50aWFsIjogIkFLSUFJT1NGT0ROTjdFWEFNUExFLzIwMTMwODA2L3VzLWVhc3QtMS9zMy9hd3M0X3JlcXVlc3QifSwNCiAgICB7IngtYW16LWFsZ29yaXRobSI6ICJBV1M0LUhNQUMtU0hBMjU2In0sDQogICAgeyJ4LWFtei1kYXRlIjogIjIwMTMwODA2VDAwMDAwMFoiIH0NCiAgXQ0KfQ==";

        logger.info("signature: {}", getSignatureV4(accessSecretKey, date, region, regionService, signing, stringToSign));
    }

}