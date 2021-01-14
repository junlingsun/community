package com.junling.comunity.utility;

public class RedisKey {
    private final static String SPLIT = ":";
    private final static String PREFIX_ENTITY_LIKE = "like:entity";
    private final static String PREFIX_USER_LIKE = "like:user";
    private final static String PREFIX_FOLLOWEE = "followee";
    private final static String PREFIX_FOLLOWER = "follower";
    private final static String PREFIX_KAPTCHA = "kaptcha";
    private final static String PREFIX_TICKET = "ticket";
    private final static String PREFIX_USER = "user";
    private final static String PREFIX_UV = "uv";
    private final static String PREFIX_DAU = "dau";

    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    public static String getFollowerKey(int userId) {
        return PREFIX_FOLLOWER + SPLIT + userId;
    }

    public static String getFolloweeKey(int userId) {
        return PREFIX_FOLLOWEE + SPLIT + userId;
    }

    public static String getKaptchaKey(String label) {
        return PREFIX_KAPTCHA + SPLIT + label;
    }
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey (int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }
}
