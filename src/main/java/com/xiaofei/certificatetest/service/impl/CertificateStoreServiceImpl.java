package com.xiaofei.certificatetest.service.impl;

import com.xiaofei.certificatetest.service.CertificateStoreService;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.springframework.stereotype.Service;

import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * Author: xiaofei
 * Date: 2023-03-01, 22:34
 * Description:
 */
@Service
public class CertificateStoreServiceImpl implements CertificateStoreService {
    public static void main(String[] args) {
        try {
            KeyStore testKeyStore = loadKeyStore(new FileInputStream("testKeyStore.jks"),"123".toCharArray());
            KeyStore firstCert = loadKeyStore(new FileInputStream("firstCert.jks"),"pin".toCharArray());

            //use sign
            byte[] digitalSignature = sign("hello", testKeyStore,"111","321".toCharArray());
            //use verify
            System.out.println(verify(digitalSignature,"hello",firstCert,"firstCert"));
            System.out.println(verify(digitalSignature,"hello",testKeyStore,"111"));


            practiceCertGenerate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    private static void generateSampleCertificate() throws NoSuchAlgorithmException, CertificateException, SignatureException, NoSuchProviderException, InvalidKeyException, KeyStoreException, IOException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048,new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        X500Principal issuer = new X500Principal("cn=test CA");
        X500Principal subject = new X500Principal("cn=test certificate");

        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + 365 * 24 * 60 * 60 * 1000L);

        BigInteger bigInteger = new BigInteger(128, new SecureRandom());

        X509V3CertificateGenerator certificateGenerator = new X509V3CertificateGenerator();
        
        certificateGenerator.setSerialNumber(bigInteger);
        certificateGenerator.setNotAfter(notAfter);
        certificateGenerator.setNotBefore(notBefore);
        certificateGenerator.setIssuerDN(issuer);
        certificateGenerator.setSubjectDN(subject);
        certificateGenerator.setPublicKey(keyPair.getPublic());
        certificateGenerator.setSignatureAlgorithm("SHA256withRSA");

        X509Certificate certificate = certificateGenerator.generate(keyPair.getPrivate());
        certificate.verify(keyPair.getPublic());
        
        
        String keyStoreType = "PKCS12";
        char[] keyStorePass = "123".toCharArray();
        String alias = "111";
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null,keyStorePass);
        keyStore.setKeyEntry(alias,keyPair.getPrivate(),"321".toCharArray(),new Certificate[]{certificate});
        FileOutputStream outputStream = new FileOutputStream("testKeyStore.jks");
        keyStore.store(outputStream,keyStorePass);
        outputStream.close();
    }
    
    private static KeyStore loadKeyStore(FileInputStream inputStream, char[] pin) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream,pin);
        
//        Certificate certificate = keyStore.getCertificate("111");
//        Key privateKey = keyStore.getKey("111", "321".toCharArray());
//        System.out.println(certificate.getType());
        
        return keyStore;
    }
    
    
    public static byte[] sign(String message, KeyStore keyStore, String alias,char[] privateKeyPin) throws Exception {
        Key privateKey = keyStore.getKey(alias,privateKeyPin);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign((PrivateKey) privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }
    
    public static boolean verify(byte[] digitalSignature, String message, KeyStore keyStore, String alias) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Certificate certificate = keyStore.getCertificate(alias);
        PublicKey publicKey = certificate.getPublicKey();
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        return signature.verify(digitalSignature);
    }
    
    
    public static void practiceCertGenerate() throws NoSuchAlgorithmException, CertificateException, SignatureException, InvalidKeyException, KeyStoreException, IOException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048,new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        X500Principal issuer = new X500Principal("cn=I'm CA");
        X500Principal subject = new X500Principal("cn=I'm entity");

        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime()+365*24*60*60*1000L);

        BigInteger serialNumber = new BigInteger(128, new SecureRandom());

        X509V3CertificateGenerator x509V3CertificateGenerator = new X509V3CertificateGenerator();
        x509V3CertificateGenerator.setSubjectDN(subject);
        x509V3CertificateGenerator.setNotBefore(notBefore);
        x509V3CertificateGenerator.setNotAfter(notAfter);
        x509V3CertificateGenerator.setIssuerDN(issuer);
        x509V3CertificateGenerator.setSerialNumber(serialNumber);
        x509V3CertificateGenerator.setPublicKey(keyPair.getPublic());
        x509V3CertificateGenerator.setSignatureAlgorithm("SHA256withRSA");

        X509Certificate certificate = x509V3CertificateGenerator.generate(keyPair.getPrivate());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null,"pin".toCharArray());
        keyStore.setKeyEntry("firstCert",keyPair.getPrivate(),null, new X509Certificate[]{certificate});

        FileOutputStream outputStream = new FileOutputStream("firstCert.jks");
        keyStore.store(outputStream,"pin".toCharArray());
        
        
    }

}
