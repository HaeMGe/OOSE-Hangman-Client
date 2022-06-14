package org.example;

import java.awt.*;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Neue Ressourcen erschaffen und zu Server hinzufügen
 */
public class PostClass {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);   //neuen Request-Body mit json Inhalt
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = Main.client.newCall(request).execute();  //Response erhalten/Request losschicken
        return response.body().string();
    }
}