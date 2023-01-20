package me.ehp246.test.asb.eventgrid.storage;

import java.time.Instant;

import me.ehp246.test.asb.eventgrid.storage.EvenPayload.Data;

/**
 * @author Lei Yang
 *
 */
record EvenPayload(String id, String source, String specversion, String type, String subject, Instant time, Data data) {

    record Data(String api, String requestId, String eTag, String contentType, long contentLength, String url,
            String blobType) {
    }
}
