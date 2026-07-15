package com.freemind.course.dto;

public enum CouponClaimResult {

    SUCCESS,          // 領取成功
    SOLD_OUT,         // 庫存已領完
    ALREADY_CLAIMED,  // 已經領取過
    NOT_PUBLISHED     // 尚未發布或已過期
}
