package com.ijarah.utils.enums;

import java.io.Serializable;

public enum ApplicationStatusForDashboard implements Serializable {

    LOAN_CREATED,
    REDEEMED,
    SANAD_WAITING,
    CSA_APPROVAL_WAITING,
    BOTH_APPROVED,
    PENDING_APPROVAL;

    public String getValue() {
        return this.name();
    }
}