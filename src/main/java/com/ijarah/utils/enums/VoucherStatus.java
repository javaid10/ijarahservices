package com.ijarah.utils.enums;

import java.io.Serializable;

public enum VoucherStatus implements Serializable {

    CREATED,
    REDEEMED,
    UNUTILISED,
    CANCELLED,
    EXPIRED;
    public String getValue() {
        return this.name();
    }
}