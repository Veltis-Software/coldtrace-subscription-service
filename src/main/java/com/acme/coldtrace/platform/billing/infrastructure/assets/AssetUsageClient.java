package com.acme.coldtrace.platform.billing.infrastructure.assets;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Usage comes from the owning Asset service; outages never mean zero usage. */
@Component
public class AssetUsageClient implements com.acme.coldtrace.platform.billing.application.ports.AssetUsageReader {

  private final RestClient client;
  public AssetUsageClient(@Value("${coldtrace.asset.url}")String url,
      @Value("${coldtrace.internal.service-key}")String key) {
    if(key==null||key.length()<32)throw new IllegalArgumentException("Internal service key is required");
    var factory=new org.springframework.http.client.JdkClientHttpRequestFactory(
      java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());
    factory.setReadTimeout(Duration.ofSeconds(5));
    client=RestClient.builder().baseUrl(url).requestFactory(factory).defaultHeader("X-Service-Key",key).build();
  }
  public Usage fetch(Long org) {
    var usage=client.get().uri("/internal/assets/organizations/{id}/usage",org).retrieve().body(Usage.class);
    if(usage==null||usage.locations()==null||usage.assets()==null||usage.iotDevices()==null
        ||usage.locations()<0||usage.assets()<0||usage.iotDevices()<0)
      throw new IllegalStateException("Asset usage unavailable");
    return usage;
  }
}
