package org.coursera.mutibo;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class OkHttpBuilder
{
    private static final String LOG_TAG = "OkHttpBuilder";

    public static OkHttpClient getSelfSignedOkHttpClient(Context context, final String allowedHost)
    {
        try {
            // security configuration (trust our self signed certificate)
            KeyStore keyStore = loadSelfSignedKeyStore(context, "changeit");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "changeit".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            // cache control
            int cacheSize = 50 * 1024 * 1024;   // 50 MiB
            Cache cache = new Cache(context.getCacheDir(), cacheSize);

            // create client
            OkHttpClient client = new OkHttpClient();
            client.setSslSocketFactory(sslContext.getSocketFactory());
            client.setHostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return hostname.equalsIgnoreCase(allowedHost);
                }
            });
            client.setCache(cache);

            return client;

        } catch (Exception e) {
            Log.w(LOG_TAG, "loadSelfSignedKeyStore", e);
            return null;
        }
    }

    private static KeyStore loadSelfSignedKeyStore(Context context, String password)
    {
        KeyStore keystore = null;

        try {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            Log.w(LOG_TAG, "loadSelfSignedKeyStore", e);
            return null;
        }

        try {
           java.io.InputStream fis = context.getResources().openRawResource(R.raw.keystore);
            try {
                keystore.load(fis, password.toCharArray());
            } finally {
                if (fis != null)
                {
                    fis.close();
                }
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "loadSelfSignedKeyStore", e);
            keystore = null;
        }

        return keystore;
    }

    public static OkHttpClient getUnsafeOkHttpClient()
    {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException
                        {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
