package com.looseboxes.spring.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public class RestOperations {

    private final Logger log = LoggerFactory.getLogger(RestOperations.class);

    private final RestTemplate restTemplate;

    public RestOperations(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> get(String url, Object token, Collection<String> cookies, Class<T> responseType) {
        ClientHttpRequestInterceptor requestInterceptor = token == null ? null : new BearerTokenRequestInterceptor(token);
        try{
            if(requestInterceptor != null) {
                restTemplate.getInterceptors().add(requestInterceptor);
            }
            return sendRequest(url, HttpMethod.GET, MediaType.APPLICATION_JSON, cookies,
                null, Collections.EMPTY_MAP, responseType);
        }finally{
            if(requestInterceptor != null) {
                restTemplate.getInterceptors().remove(requestInterceptor);
            }
        }
    }

    public <T> ResponseEntity<T> sendRequest(
            HttpMethod httpMethod, String url, Object token, MediaType contentType,
            Collection<String> cookies, Object requestBody, Map params, Class<T> responseType) {
        ClientHttpRequestInterceptor requestInterceptor = token == null ? null : new BearerTokenRequestInterceptor(token);
        return this.sendRequest(httpMethod, requestInterceptor, url, contentType, cookies, requestBody, params, responseType);
    }

    public <T> ResponseEntity<T> sendRequest(
            HttpMethod httpMethod,
            ClientHttpRequestInterceptor requestInterceptor, String url,
            MediaType contentType, Collection<String> cookies,
            Object requestBody, Map params, Class<T> responseType) {
        try{
            if(requestInterceptor != null) {
                restTemplate.getInterceptors().add(requestInterceptor);
            }
            return sendRequestAndCollectCookies(url, httpMethod,
                    contentType, cookies, requestBody, params, responseType);
        }finally{
            if(requestInterceptor != null) {
                restTemplate.getInterceptors().remove(requestInterceptor);
            }
        }
    }

    private <T> ResponseEntity<T> sendRequestAndCollectCookies(
            String url, HttpMethod method,
            MediaType contentType, Collection<String> cookies,
            Object requestBody, Map params, Class<T> responseType) {

        ResponseEntity<T> response = sendRequest(
                url, method, contentType, cookies, requestBody, params, responseType);

        cookies.addAll(this.getCookies(response));

        log.debug("->  Status: {}", response.getStatusCode());
        log.trace("-> Headers: {}\n->    Body: {}", response.getHeaders(), response.getBody());

        return response;
    }

    private <T> ResponseEntity<T> sendRequest(
            String url, HttpMethod method,
            MediaType contentType, Collection<String> cookies,
            Object requestBody, Map params, Class<T> responseType) {

        log.debug("->     URL: {}\n-> Cookies: {}\n->  Params: {}", url, cookies, params);
        log.trace("->    Body: {}", requestBody);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.set("Cookie", cookies.stream().collect(Collectors.joining(";")));

        final HttpEntity request;
        if(HttpMethod.POST.equals(method) || HttpMethod.PATCH.equals(method) || HttpMethod.PUT.equals(method)) {
            if(requestBody instanceof Map) {
                requestBody = toMultiValueMap((Map)requestBody);
            }
            request = new HttpEntity<>(requestBody, headers);
        }else{
            request = new HttpEntity<>(headers);
        }

        final ResponseEntity<T> response = restTemplate.exchange(url, method, request, responseType, params);

        return response;
    }

    private MultiValueMap toMultiValueMap(Map params) {
        // Use either model object or MultiValueMap for request body
        // If you use a java.util.Map, you get the following exception:
        // org.springframework.web.client.RestClientException: No HttpMessageConverter for java.util.HashMap and content type "application/x-www-form-urlencoded"
        final MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<>();
        params.forEach((k, v) -> {
            if(k != null && v != null) {
                mvm.add(k.toString(), v);
            }
        });
        return mvm;
    }

    private List<String> getCookies(ResponseEntity responseEntity) {
        final List<String> cookiesReceived = responseEntity.getHeaders().get("Set-Cookie");
        log.trace("Cookies received: {}", cookiesReceived);
        return cookiesReceived == null ? Collections.EMPTY_LIST : cookiesReceived;
    }

    private static class BearerTokenRequestInterceptor implements ClientHttpRequestInterceptor {

        private final Object token;

        private BearerTokenRequestInterceptor(Object token) {
            this.token = Objects.requireNonNull(token);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().add("Authorization", "Bearer " + token);
            return execution.execute(request, bytes);
        }
    }
}
