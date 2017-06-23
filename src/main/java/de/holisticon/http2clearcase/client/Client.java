package de.holisticon.http2clearcase.client;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Jetty;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Created by janweinschenker on 02.06.17.
 */
public class Client {

  private static final Logger LOG = Logger.getLogger(Client.class);


  public static void main(String[] args) {
    Client app = new Client();
    String host = "localhost";
    int port = 8080;
    String path = "/greeting?name=JavaLand";
    app.performAsyncHttpRequest(host, port, path);
    app.performDefaultHttpRequest(host, port, path);
    System.exit(0);
  }

  public void performAsyncHttpRequest(String host, int port, String path) {
    LOG.debug("============================================= Asynchronous example ===");
    try {
      HttpClient httpClient = getHttpClient();
      String uri = getFormatedUri(host, port, path);

      Request request =
          httpClient.newRequest(uri)
                    .onResponseContent((response, byteBuffer) -> {
                      LOG.debug("content: " + BufferUtil.toString(byteBuffer));
                      LOG.debug("");
                    });
      request.send(result -> {
        LOG.debug("http version: " +
            result.getResponse().getVersion());
      });

      // watch the console log: the following message will be printed before the request has finished.
      LOG.debug("request created!!!");
      Thread.sleep(5000);

    } catch (Exception e) {
      LOG.error("Exception:", e);
    }
  }


  public void performDefaultHttpRequest(String host, int port, String path) {
    LOG.debug("============================================= Synchronous example ===");
    try {
      HttpClient httpClient = getHttpClient();
      String uri = getFormatedUri(host, port, path);
      ContentResponse response = httpClient.GET(uri);

      LOG.debug("http version: " + response.getVersion());
      LOG.debug(response.getContentAsString());
      LOG.debug("");

    } catch (Exception e) {
      LOG.error("Exception:", e);
    }
  }

  /**
   * Create a jetty http client capable to speak http/2.
   *
   * @return
   * @throws Exception
   */
  private static HttpClient getHttpClient() throws Exception {
    SslContextFactory sslContextFactory = new SslContextFactory();
    HttpClientTransportOverHTTP2 transport = new HttpClientTransportOverHTTP2(
        new HTTP2Client());
    transport.setUseALPN(false);
    HttpClient httpClient = new HttpClient(transport, sslContextFactory);

    httpClient.setFollowRedirects(false);
    httpClient.start();

    return httpClient;
  }


  /**
   * Create the necessary request headers.
   *
   * @param host
   * @param port
   * @param path
   * @param http2Client
   * @return
   */
  private static HeadersFrame getRequestHeaders(String host, int port, String path, HTTP2Client http2Client) {
    HttpFields requestFields = new HttpFields();
    requestFields.put("User-Agent", http2Client.getClass().getName() + "/" + Jetty.VERSION);
    MetaData.Request metaData = new MetaData.Request("GET", new HttpURI("https://" + host + ":" + port + path), HttpVersion.HTTP_2, requestFields);
    HeadersFrame headersFrame = new HeadersFrame(metaData, null, true);
    return headersFrame;
  }

  /**
   * Create uri from the three method parameters.
   *
   * @param host
   * @param port
   * @param path
   * @return
   */
  private static String getFormatedUri(String host, int port, String path) {
    return String.format("https://%s:%s%s", host, port, path);
  }

}
