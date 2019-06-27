package com.leopardslab.dunner;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;

import java.util.HashMap;
import java.util.Map;

public class Result {
    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Map toMap() {
        final HashMap result = new HashMap();
        result.put("success", success);
        result.put("message", message);
        return result;
    }

    public int responseCode() {
        return DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
    }
}
