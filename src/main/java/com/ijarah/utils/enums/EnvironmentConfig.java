package com.ijarah.utils.enums;

import com.kony.adminconsole.commons.handler.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.controller.DataControllerRequest;

import java.io.Serializable;

public enum EnvironmentConfig implements Serializable {
    DUMMY;

    EnvironmentConfig() {
    }

    public String getValue(DataControllerRequest requestInstance) {
        return EnvironmentConfigurationsHandler.getServerAppPropertyValue(this.name(), requestInstance);
    }
}
