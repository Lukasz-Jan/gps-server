package com.gps.devices.message;

import java.net.http.HttpRequest;

public record RegisterRequestDataWrapper(String macAddress, HttpRequest request) {
}
