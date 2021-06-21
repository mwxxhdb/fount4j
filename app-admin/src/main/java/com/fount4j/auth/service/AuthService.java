package com.fount4j.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Morven 2021-06-21
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final DiscoveryClient discoveryClient;

    public boolean validateRedirectUrl(String redirect) {
        return discoveryClient.getServices()
            .stream()
            .map(discoveryClient::getInstances)
            .flatMap(List::stream)
            .map(ServiceInstance::getMetadata)
            .map(meta -> meta.get(""))
            .anyMatch(redirect::startsWith);
    }
}
