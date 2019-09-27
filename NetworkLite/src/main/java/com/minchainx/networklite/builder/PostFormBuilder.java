package com.minchainx.networklite.builder;

import com.minchainx.networklite.request.PostFormRequest;
import com.minchainx.networklite.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder>
        implements IParametersConfigure {

    private List<FileInput> mFiles = new ArrayList<FileInput>();

    public PostFormBuilder addFiles(String key, Map<String, File> files) {
        if (files != null) {
            for (String filename : files.keySet()) {
                addFile(key, filename, files.get(filename));
            }
        }
        return this;
    }

    public PostFormBuilder addFile(String key, String filename, File file) {
        mFiles.add(new FileInput(key, filename, file));
        return this;
    }

    @Override
    public PostFormBuilder setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostFormBuilder addParam(String key, String value) {
        if (this.params == null) {
            params = new LinkedHashMap<String, String>();
        }
        params.put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFormRequest(url, params, headers, mFiles, tag, id).build();
    }

    public static class FileInput {

        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
