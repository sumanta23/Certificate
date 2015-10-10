package org.sumanta.test.it;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class ITutil {

    String responseString = "";

    boolean isHttps = false;

    HttpPost httpPost = null;
    HttpGet httpGet = null;

    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

    //DefaultHttpClient defaultHttpClient = null;

    public String executeCommand(String baseUrl, String command) {

        if (baseUrl.contains("https")) {
            isHttps = true;
            defaultHttpClient = getHttpsConnection();
        }

        command = command.replaceAll(" ", "%20");

        try {

            httpPost = getHttpPost(baseUrl + command);

            final CloseableHttpResponse closeableHttpResponse = defaultHttpClient.execute(httpPost);
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + closeableHttpResponse.getStatusLine());
            }

            // Read the response body.
            HttpEntity f = closeableHttpResponse.getEntity();
            responseString = convertStreamToString(f.getContent());
            
            closeableHttpResponse.close();
        } catch (IOException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.

            httpPost.releaseConnection();
        }
        return responseString;
    }

    public InputStream downloadCommand(String downloadUrl) {

        if (downloadUrl.contains("https")) {
            isHttps = true;
            defaultHttpClient = getHttpsConnection();
        }

        InputStream responseStream = null;

        httpGet = getHttpget(downloadUrl);

        try {
            // Execute the method.
            CloseableHttpResponse closeableHttpResponse = defaultHttpClient.execute(httpGet);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + closeableHttpResponse.getStatusLine());
            }

            // Read the response body.
            responseStream = closeableHttpResponse.getEntity().getContent();
            // responseStream = method.getResponseBodyAsStream();

        } catch (IOException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.
            //method.releaseConnection();
        }
        return responseStream;
    }

    public String fetchSerialNumber(final String fullResult) {
        StringTokenizer stringTokenizer = new StringTokenizer(fullResult, "\t");
        stringTokenizer.nextElement();
        stringTokenizer.nextElement();
        String serial = (String) stringTokenizer.nextElement();
        return serial;
    }

    private DefaultHttpClient getHttpsConnection() {
        try {
            /*
             * KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType()); trustStore.load(null, null); SSLContext sslContext=new SSLContextBuilder().build(); SSLConnectionSocketFactory
             * sf=new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
             */

            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLSocketFactory sslsf = new SSLSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            HttpParams params = new BasicHttpParams();

            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sslsf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);

            // return httpclient;
        } catch (final Exception e) {

        }
        return null;
    }

    private HttpPost getHttpPost(String url) {
        HttpPost method = null;
        try {
            method = new HttpPost(new URI(url));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return method;

    }

    private HttpGet getHttpget(String url) {
        HttpGet method = new HttpGet(url);
        return method;
    }

    String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
