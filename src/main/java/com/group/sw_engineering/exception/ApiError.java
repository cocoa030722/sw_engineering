package com.group.sw_engineering.exception;

import java.time.Instant;
import java.util.List;

/** Uniform error body returned for every failed request. */
public record ApiError(Instant timestamp, int status, String error, List<String> messages, String path) {
}
