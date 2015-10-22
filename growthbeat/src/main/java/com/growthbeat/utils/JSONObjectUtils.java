package com.growthbeat.utils;

import org.json.JSONObject;

public final class JSONObjectUtils {

    public static boolean hasAndIsNotNull(JSONObject jsonObject, String name) {
        return (jsonObject != null && jsonObject.has(name) && !jsonObject.isNull(name));
    }

}
