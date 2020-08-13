package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jt on 2019-08-08.git
 */
@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {


    private final Integer maxTotalConnection;

    private final Integer defaultMaxTotalConnection;

    private final Integer connectionRequestTimeOut;

    private final Integer timeout;

    public BlockingRestTemplateCustomizer(@Value("${sfg.maxtotalconnections}")Integer maxTotalConnection,
                                          @Value("${sfg.defaultmaxtotalconnections}") Integer defaultMaxTotalConnection,
                                          @Value("${sfg.connectionrequesttimeout}") Integer connectionRequestTimeOut,
                                          @Value("${sfg.sockettimeout}") Integer timeout) {
        this.maxTotalConnection = maxTotalConnection;
        this.defaultMaxTotalConnection = defaultMaxTotalConnection;
        this.connectionRequestTimeOut = connectionRequestTimeOut;
        this.timeout = timeout;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory(){

        System.out.println("maxTotalConnection="+maxTotalConnection);

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnection);
        connectionManager.setDefaultMaxPerRoute(defaultMaxTotalConnection);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeOut)
                .setSocketTimeout(timeout)
                .build();

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        System.out.println("Entered Blocking Customize********");
        restTemplate.setRequestFactory(this.clientHttpRequestFactory());
        System.out.println("exiting Blocking Customize********");
    }
}
