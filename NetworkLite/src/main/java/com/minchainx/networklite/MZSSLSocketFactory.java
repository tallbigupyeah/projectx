package com.minchainx.networklite;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.internal.tls.OkHostnameVerifier;

public class MZSSLSocketFactory extends SSLSocketFactory {

    private static final int ALT_DNS_NAME = 2;

    private final SSLSocketFactory delegate;
    private final SSLSocketFactory defaultsslSocketFactory;
    private final KeyStore keyStore;
    private TrustManager[] trustManagers;
    /**
     * 测试环境标记
     */
    private boolean testEnvFlag = false;

    public MZSSLSocketFactory(InputStream[] certificates) {
        X509TrustManager trustManager = systemDefaultTrustManager();
        this.defaultsslSocketFactory = systemDefaultSslSocketFactory(trustManager);
        try {
            //CertificateFactory用来证书生成
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null);

            if (certificates != null && certificates.length > 0) {
                int index = 0;
                for (InputStream certificate : certificates) {
                    String certificateAlias = Integer.toString(index++);
                    // 创建 Keystore 包含我们的证书
                    Certificate ca = certificateFactory.generateCertificate(certificate);
                    if (!checkHasDns((X509Certificate) ca)) {
                        throw new RuntimeException("Certificate has not DNS info");
                    }
                    keyStore.setCertificateEntry(certificateAlias, ca);
                    try {
                        if (certificate != null) {
                            certificate.close();
                        }
                    } catch (IOException ignored) {
                    }
                }

                // 创建一个 TrustManager 仅把 Keystore 中的证书 作为信任的锚点
                String algorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
                trustManagerFactory.init(keyStore);
                trustManagers = trustManagerFactory.getTrustManagers();
                testEnvFlag = false;
            } else {
                trustManagers = new TrustManager[]{
                        new X509TrustManager() {

                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };
                testEnvFlag = true;
            }

            // 用 TrustManager 初始化一个 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            this.delegate = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public X509TrustManager getX509TrustManager() {
        return (X509TrustManager) trustManagers[0];
    }

    private X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }

    private SSLSocketFactory systemDefaultSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return this.defaultsslSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.defaultsslSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        if (verify(host)) {
            return this.delegate.createSocket(s, host, port, autoClose);
        }
        return this.defaultsslSocketFactory.createSocket(s, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        if (verify(host)) {
            return this.delegate.createSocket(host, port);
        }
        return this.defaultsslSocketFactory.createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        if (verify(host)) {
            return this.delegate.createSocket(host, port, localHost, localPort);
        }
        return this.defaultsslSocketFactory.createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        if (verify(host.getHostAddress())) {
            return this.delegate.createSocket(host, port);
        }
        return this.defaultsslSocketFactory.createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        if (verify(address.getHostAddress())) {
            return this.delegate.createSocket(address, port, localAddress, localPort);
        }
        return this.defaultsslSocketFactory.createSocket(address, port, localAddress, localPort);
    }

    private boolean verify(String host) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (OkHostnameVerifier.INSTANCE.verify(host, (X509Certificate) keyStore.getCertificate(alias))) {
                    return true;
                }
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        if (testEnvFlag) {
            return true;
        }
        return false;
    }

    private static boolean checkHasDns(X509Certificate certificate) {
        List<String> result = new ArrayList<>();
        try {
            Collection<?> subjectAltNames = certificate.getSubjectAlternativeNames();
            if (subjectAltNames == null) {
                return false;
            }
            for (Object subjectAltName : subjectAltNames) {
                List<?> entry = (List<?>) subjectAltName;
                if (entry == null || entry.size() < 2) {
                    continue;
                }
                Integer altNameType = (Integer) entry.get(0);
                if (altNameType == null) {
                    continue;
                }
                if (altNameType == ALT_DNS_NAME) {
                    String altName = (String) entry.get(1);
                    if (altName != null) {
                        result.add(altName);
                    }
                }
            }
            return 0 < result.size();
        } catch (CertificateParsingException e) {
            Log.e("MySSLSocketFactory", "e:" + e);
            return true;
        }
    }
}
