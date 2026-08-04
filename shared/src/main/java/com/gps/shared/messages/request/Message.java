package com.gps.shared.messages.request;

import java.io.Serializable;

public interface Message extends Serializable {
    String macAddress();
}
