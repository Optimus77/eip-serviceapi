package com.inspur.eip.util.common;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JaspytUtils {

    /**
     * Jasypt生成加密结果
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待加密值
     * @return pwd
     */
    public static String encryptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.encrypt(value);
    }

    /**
     * 解密
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待解密密文
     * @return pwd
     */
    public static String decyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.decrypt(value);
    }

    private static SimpleStringPBEConfig cryptOr(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }

    public static void main(String[] args) {
        // 加密
        System.out.println(encryptPwd("EbfYkitulv73I2p0mXI50JMXoaxZTKJ7", "admin"));
        System.out.println(encryptPwd("EbfYkitulv73I2p0mXI50JMXoaxZTKJ7", "admin"));
        // 解密

       // System.out.println(decyptPwd("EbfYkitulv73I2p0mXI50JMXoaxZTKJ7", "TSu5pS+BQFM5TbKzAgzUzQ=="));
        System.out.println(decyptPwd("EbfYkitulv73I2p0mXI50JMXoaxZTKJ7", "j0sUiT770r/4q8u6x7AkTw=="));
        System.out.println(decyptPwd("EbfYkitulv73I2p0mXI50JMXoaxZTKJ7", "j0sUiT770r/4q8u6x7AkTw=="));

//        System.out.println(decyptPwd("EbfYkitulv73I2p0mXI50JMXoaxZTKJ7", "98KylQ3eba/2AKXG8m+83g=="));


    }

}
