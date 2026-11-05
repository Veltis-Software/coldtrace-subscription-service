package com.acme.coldtrace.platform.billing.application.ports;

public interface AssetUsageReader {
  record Usage(Integer locations,Integer assets,Integer iotDevices) {}
  Usage fetch(Long organizationId);
}
