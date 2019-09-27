package com.minchainx.network.entity;

import android.text.TextUtils;

import com.minchainx.networklite.util.GsonUtils;

import java.lang.reflect.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmResponseCache extends RealmObject {

    @PrimaryKey
    private String key;
    private String jsonText;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJsonText() {
        return jsonText;
    }

    public void setJsonText(String jsonText) {
        this.jsonText = jsonText;
    }

    @Override
    public String toString() {
        return "RealmResponseCache[key=" + key + ",jsonText=" + jsonText + "]";
    }

    public ResponseEntity convertToResponseEntity(Type type) {
        if (!TextUtils.isEmpty(jsonText)) {
            try {
                return GsonUtils.getGsonInstance().fromJson(jsonText, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static RealmResponseCache convertToRealmResponeCache(String key, ResponseEntity responseEntity) {
        String jsonText = "";
        try {
            jsonText = GsonUtils.getGsonInstance().toJson(responseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RealmResponseCache realmResponeCache = new RealmResponseCache();
        realmResponeCache.key = key;
        realmResponeCache.jsonText = jsonText;
        return realmResponeCache;
    }
}
