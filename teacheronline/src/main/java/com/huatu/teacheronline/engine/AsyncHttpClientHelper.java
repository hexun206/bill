package com.huatu.teacheronline.engine;

import org.apache.http.client.params.ClientPNames;

public class AsyncHttpClientHelper {
    private static com.loopj.android.http.AsyncHttpClient asyncHttpClient;

    public static com.loopj.android.http.AsyncHttpClient createInstance() {
        if (asyncHttpClient == null) {
            synchronized (com.loopj.android.http.AsyncHttpClient.class) {
                if (asyncHttpClient == null) {
                    asyncHttpClient = new com.loopj.android.http.AsyncHttpClient();
                }
            }
        }

        asyncHttpClient.setTimeout(30 * 1000);
        asyncHttpClient.addHeader("Accept-Encoding", "identity");
        asyncHttpClient.addHeader("Accept-Encoding","gzip, deflate");
//        asyncHttpClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        return asyncHttpClient;
    }
}
