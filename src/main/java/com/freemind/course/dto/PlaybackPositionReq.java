package com.freemind.course.dto;

import java.math.BigDecimal;

public record PlaybackPositionReq(
        Integer courseOrderId,
        Integer courseId,
        Integer playbackSeconds,
        BigDecimal playbackPercentage
) {
}