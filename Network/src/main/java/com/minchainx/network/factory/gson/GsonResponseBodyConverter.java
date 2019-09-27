package com.minchainx.network.factory.gson;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import com.minchainx.network.exception.NetworkException;

import java.io.EOFException;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final String TAG = "GsonResponseBodyConver";
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            T t = adapter.read(jsonReader);

            return t;
        } catch (EOFException e) {
            throw new NetworkException(NetworkException.ErrorType.SERVER, "the response is null. " + e.fillInStackTrace().toString());
        } catch (MalformedJsonException e) {
            throw new NetworkException(NetworkException.ErrorType.JSON, "JSON Error: " + e.fillInStackTrace().toString());
        } catch (JsonSyntaxException e) {
            throw new NetworkException(NetworkException.ErrorType.JSON, "JSON Error: " + e.fillInStackTrace().toString());
        } finally {
            value.close();
        }
    }
}
