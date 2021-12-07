package com.example.counting_center.util;

import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
@NoArgsConstructor
public class RSA {

    public String encrypt(Certificate certificate, String message) {
        RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();

        BigInteger integerMessage = new BigInteger(message.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedBytes = integerMessage.modPow(publicKey.getPublicExponent(), publicKey.getModulus()).toByteArray();

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        RSAPrivateKey privateKey = Keys.getEncryptPrivateKey();
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        BigInteger integer = new BigInteger(encryptedBytes);
        byte[] decryptedBytes = integer.modPow(privateKey.getPrivateExponent(), privateKey.getModulus()).toByteArray();

        return new String(decryptedBytes);
    }

    public String signMessage(String message) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateKey signingKey = Keys.getSignPrivateKey();

        String hashedMessage = getSHA256Hash(message);
        byte[] hashedBytes = hashedMessage.getBytes(StandardCharsets.UTF_8);
        BigInteger bigInteger = new BigInteger(hashedBytes);

        byte[] signBytes = bigInteger.modPow(signingKey.getPrivateExponent(), signingKey.getModulus()).toByteArray();

        return Base64.getEncoder().encodeToString(signBytes);
    }

    public boolean varifySignature(String signature, String message, Certificate signingCertificate) {
        RSAPublicKey publicVarifyKey = (RSAPublicKey) signingCertificate.getPublicKey();

        byte[] decodeSign = Base64.getDecoder().decode(signature);
        BigInteger bigInteger = new BigInteger(decodeSign);
        byte[] varifyBytes = bigInteger.modPow(publicVarifyKey.getPublicExponent(), publicVarifyKey.getModulus()).toByteArray();

        String messageToVarify = new String(varifyBytes);
        String hashedOriginalMessage = getSHA256Hash(message);

        boolean verification;
        verification = messageToVarify.equals(hashedOriginalMessage);

        return verification;
    }

    public RSAPublicKey getPublicKeyFromCertificate(String filepath) throws FileNotFoundException, CertificateException {
        FileInputStream inputStream = new FileInputStream(filepath);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(inputStream);

        return (RSAPublicKey) certificate.getPublicKey();
    }

    public String getSHA256Hash(String data) {
        return DigestUtils.sha256Hex(data);
    }
}
