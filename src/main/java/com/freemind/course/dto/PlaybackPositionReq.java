package com.freemind.course.dto;

public record PlaybackPositionReq(Integer courseOrderId, Integer courseId, Long playbackSeconds) {
}