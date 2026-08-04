package com.gps.shared;

public class Constants {

    public static final String FIXED_MAC = "ab:cd:ef:01:02:03";
    public static final String REGISTER_ENDPOINT = "http://localhost:8090/gps/register";
    public static final String POSITION_ENDPOINT = "http://localhost:9000/gps/position";
    public static final String POSITION_ENDPOINT_2 = "http://localhost:9002/gps/position";
    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String POSITION_QUE = "positionQueue";
    public static final String REGISTER_QUE = "registerQueue";
    public static final String CONNECTION_MONGO = "mongodb://localhost:27017/test";
    public static final String GPS_DATA_COLLECTION = "gpsAccounts";
    public static final String WARSAW_TIME = "Europe/Warsaw";
    public static final int MAX_SRV_CAPACITY = 4_000_000;

    private Constants() {}
}
