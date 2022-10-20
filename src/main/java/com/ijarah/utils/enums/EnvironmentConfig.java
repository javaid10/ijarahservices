package com.ijarah.utils.enums;

import com.kony.adminconsole.commons.handler.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;

import java.io.Serializable;

public enum EnvironmentConfig implements Serializable {
    DUMMY,
    SME_SCHEMA_NAME_IJARAH,
    SANAD_ACCESS_TOKEN_AUTHORIZATION,

    SIGN_SECRET;

    EnvironmentConfig() {
    }

    public String getValue(DataControllerRequest requestInstance) {
        return EnvironmentConfigurationsHandler.getServerAppPropertyValue(this.name(), requestInstance);
    }
}
