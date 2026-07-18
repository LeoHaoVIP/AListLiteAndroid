package com.leohao.android.alistlite.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * 自签名证书生成工具（BouncyCastle）
 *
 * @author LeoHao
 */
public class SelfSignedCertGenerator {

    static {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成自签名证书和密钥，导出为 PEM 文件
     *
     * @param certPath   证书文件路径
     * @param keyPath    私钥文件路径
     * @param commonName 证书 CN（如 IP 地址）
     */
    public static void generate(String certPath, String keyPath, String commonName) throws Exception {
        // 生成 RSA 2048 密钥对
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 证书有效期：从当前时间起 10 年
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + 3650L * 24 * 60 * 60 * 1000);

        // 构建自签名证书
        X500Name issuer = new X500Name("CN=" + commonName);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, serial, notBefore, notAfter, issuer, keyPair.getPublic());

        X509CertificateHolder certHolder = certBuilder.build(
                new JcaContentSignerBuilder("SHA256WithRSA")
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                        .build(keyPair.getPrivate()));

        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certHolder);

        // 导出证书 PEM
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(certPath))) {
            pemWriter.writeObject(cert);
        }

        // 导出私钥 PEM
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(keyPath))) {
            pemWriter.writeObject(keyPair.getPrivate());
        }
    }
}
