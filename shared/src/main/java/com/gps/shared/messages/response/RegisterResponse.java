package com.gps.shared.messages.response;

import jakarta.validation.constraints.NotEmpty;

public record RegisterResponse(@NotEmpty String macAddress,
                               @NotEmpty RegisterResult registerResult) {}
