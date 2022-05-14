package com.gs.javablockchain.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public abstract class SignatureUtils {
    /**
     * KeyFactory that initializes with the algorithm to generates public-private key par
     * */
    private static KeyFactory keyFactory = null;

    static {
        try {
            keyFactory = KeyFactory.getInstance("DSA", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e){
            log.error("Exception in keyFactory: ", e);
        }
    }

    /**
     * Generate a public-private key pair
     * @return KeyPair key pair
     * */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);
        return keyGen.generateKeyPair();
    }

    /**
     * verify a Signature for given data and public key
     * @param info data signed to be verified
     * @param signature to be verified
     * @param publicKey public key associated to the private key that has been used to sign the data
     * @return true if the sign is valid
     * */
    public static boolean validateSignature(byte[] info, byte[] signature, byte[] publicKey) throws
            InvalidKeySpecException, InvalidKeyException, NoSuchProviderException,
            NoSuchAlgorithmException, SignatureException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        PublicKey publicKeyObj = keyFactory.generatePublic(keySpec);

        Signature sign = getSignatureInstance();
        sign.initVerify(publicKeyObj);
        sign.update(info);
        return sign.verify(signature);
    }

    /**
     * Sign data for a given private key
     * */
    public static byte[] sign(byte[] info, byte[] privateKey) throws Exception{
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        PrivateKey privateKeyObj = keyFactory.generatePrivate(keySpec);

        Signature sign = getSignatureInstance();
        sign.initSign(privateKeyObj);
        sign.update(info);
        return sign.sign();
    }

    private static Signature getSignatureInstance() throws NoSuchProviderException, NoSuchAlgorithmException{
        return Signature.getInstance("SHA1withDSA", "SUN");
    }
}
