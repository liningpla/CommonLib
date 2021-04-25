package com.dds.openssl;

public class OpenCipher {
    static {
        System.loadLibrary("native-cipher");
    }


    public native String Encrypt(String content);

    public native String Decrypt(String content);


    private static OpenCipher gmCipher;

    public static OpenCipher getInstance() {
        if (gmCipher == null) {
            gmCipher = new OpenCipher();
        }
        return gmCipher;
    }
}
