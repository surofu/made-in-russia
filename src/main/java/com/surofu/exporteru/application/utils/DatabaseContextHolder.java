package com.surofu.exporteru.application.utils;

public class DatabaseContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setWriteDataSource() {
        contextHolder.set("write");
    }

    public static void setReadDataSource() {
        contextHolder.set("read");
    }

    public static String getDataSourceType() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
