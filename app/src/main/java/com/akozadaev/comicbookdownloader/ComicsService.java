package com.akozadaev.comicbookdownloader;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComicsService {
    public static String BASE_URL = "bUPDj3NcY7TPvoShGVEilLJJmiYHzdqyirJx04n4";
//    for akozad669530727
//    free account 3000 calls/day
    public static String PRIVATE_KEY = "bdcb34be1a059d4877a543f3eddb7abebf830f69";
    public static String PUBLIC_KEY = "0dbad827084dc216c34c7923500ff3ee";

    ComicsApi api;

    public ComicsService() {
        Retrofit retrofit = createRetrofit();
        api = retrofit.create(ComicsApi.class);
    }

    public ComicsApi getApi() {
        return api;
    }

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        String hash = DigestUtils
                .md5Hex(ts + PRIVATE_KEY + PUBLIC_KEY);
        httpClient.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();
                final HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("apikey", PUBLIC_KEY)
                        .addQueryParameter("hash", hash)
                        .addQueryParameter("ts", ts.toString())
                        .build();
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
