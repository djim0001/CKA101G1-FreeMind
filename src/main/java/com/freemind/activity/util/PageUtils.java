package com.freemind.activity.util;

public class PageUtils {

    public static int calculateTotalPages(long totalCount, int pageSize) {
        int totalPages = (int) (totalCount % pageSize == 0 
                ? (totalCount / pageSize) 
                : (totalCount / pageSize + 1));
        return totalPages == 0 ? 1 : totalPages;
    }
}