package com.ijarah.utils.enums;

import java.io.Serializable;

public enum MISReportModes implements Serializable {

    FROM_TO,
    NATIONAL_ID,
    MOBILE,
    DATE,
    APPLICATION_ID;
    public String getValue() {
        return this.name();
    }
}