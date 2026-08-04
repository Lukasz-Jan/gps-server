package com.gps.devices.message;

import java.net.http.HttpRequest;

public record RegistrationRequestDataWrapper(String macAddress, HttpRequest request) {
}
