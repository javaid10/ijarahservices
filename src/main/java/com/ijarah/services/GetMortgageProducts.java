package com.ijarah.services;

import com.google.gson.Gson;
import com.ijarah.Model.MORTGAGE_PRODUCT.MortgageProductResponse;
import com.ijarah.Model.MORTGAGE_PRODUCT.MortgageproductItem;

import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.StatusEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.MORTGAGE_PRODUCT_GET_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.kony.adminconsole.commons.utils.InlineServiceExecutor.LOG;

public class GetMortgageProducts implements JavaService2 {

    String[] MORTGAGE_PRODUCT;

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Result result = StatusEnum.error.setStatus();
        Result getMortgageProductsResponse = getMortgageProducts(dataControllerRequest);
        Gson gson = new Gson();
        MortgageProductResponse mortgageProductResponse = gson.fromJson(ResultToJSON.convert(getMortgageProductsResponse), MortgageProductResponse.class);
        extractValuesFromMortgageProductResponse(mortgageProductResponse);
        LOG.error("MORTGAGE_PRODUCT 0:: " + MORTGAGE_PRODUCT[0]);
        LOG.error("MORTGAGE_PRODUCT 5:: " + MORTGAGE_PRODUCT[5]);
        LOG.error("MORTGAGE_PRODUCT 11:: " + MORTGAGE_PRODUCT[MORTGAGE_PRODUCT.length - 1]);
        return result;
    }

    private void extractValuesFromMortgageProductResponse(MortgageProductResponse mortgageProductResponse) {
        List<MortgageproductItem> mortgageProductList = mortgageProductResponse.getMortgageproduct();
        MORTGAGE_PRODUCT = new String[mortgageProductList.size()];
        int index = 0;
        for (MortgageproductItem mortgageproductItem : mortgageProductList) {
            MORTGAGE_PRODUCT[index] = mortgageproductItem.getMortgageProductValue();
            index++;
        }
    }

    private Result getMortgageProducts(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParams = new HashMap<>();
            return ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, MORTGAGE_PRODUCT_GET_OPERATION_ID, inputParams, null, dataControllerRequest);
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerApplicationData :: " + ex);
        }
        return result;
    }
}
