package com.looseboxes.spring.webapp.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author hp
 */
public final class PaginationUtilExt {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";

    private PaginationUtilExt() { }

    public static <T> ResponseEntity<List<T>> respond(Page<T> page){
        HttpHeaders headers = generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Generate pagination headers for a Spring Data {@link org.springframework.data.domain.Page} object.
     *
     * @param uriBuilder The URI builder.
     * @param page The page.
     * @param <T> The type of object.
     * @return http header.
     */
    public static <T> HttpHeaders generatePaginationHttpHeaders(UriComponentsBuilder uriBuilder, Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));

        StringBuilder link = new StringBuilder(100 * 5);

        appendSelfLink(uriBuilder, page, link);

        appendPaginationHttpHeadersLink(uriBuilder, page, link);

        headers.add(HttpHeaders.LINK, link.toString());

        return headers;
    }

    public static void appendSelfLink(UriComponentsBuilder uriBuilder, Page page, StringBuilder link) {
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        link.append(prepareLink(uriBuilder, pageNumber, pageSize, "self"))
            .append(",");
    }

    public static void appendPaginationHttpHeadersLink(UriComponentsBuilder uriBuilder, Page page, StringBuilder link) {
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next"))
                .append(",");
        }
        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev"))
                .append(",");
        }
        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last"))
            .append(",")
            .append(prepareLink(uriBuilder, 0, pageSize, "first"));
    }

    private static String prepareLink(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize, String relType) {
        return MessageFormat.format(HEADER_LINK_FORMAT, preparePageUri(uriBuilder, pageNumber, pageSize), relType);
    }

    private static String preparePageUri(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize) {
        return uriBuilder.replaceQueryParam("page", Integer.toString(pageNumber))
            .replaceQueryParam("size", Integer.toString(pageSize))
            .toUriString()
            .replace(",", "%2C")
            .replace(";", "%3B");
    }
}
