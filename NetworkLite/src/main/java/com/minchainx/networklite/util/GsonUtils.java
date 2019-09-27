package com.minchainx.networklite.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GsonUtils {

    private static volatile Gson gson;

    public static Gson getGsonInstance() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.registerTypeAdapter(int.class, new IntDeserializer())
                    .registerTypeAdapter(Integer.class, new IntDeserializer())
                    .registerTypeAdapter(long.class, new LongDeserializer())
                    .registerTypeAdapter(Long.class, new LongDeserializer())
                    .registerTypeAdapter(float.class, new FloatDeserializer())
                    .registerTypeAdapter(Float.class, new FloatDeserializer())
                    .registerTypeAdapter(Double.class, new DoubleDeserializer())
                    .registerTypeAdapter(double.class, new DoubleDeserializer()).create();
        }
        return gson;
    }

    private static class IntDeserializer implements JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Integer.MIN_VALUE;
        }
    }

    private static class LongDeserializer implements JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsLong();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Long.MIN_VALUE;
        }
    }

    private static class FloatDeserializer implements JsonDeserializer<Float> {
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsFloat();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Float.MIN_VALUE;
        }
    }

    private static class DoubleDeserializer implements JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Double.MIN_VALUE;
        }
    }
}
