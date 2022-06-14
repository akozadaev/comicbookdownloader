package com.akozadaev.comicbookdownloader;

import android.util.Log;

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
    public static final String BASE_URL = "https://api.nasa.gov/EPIC/api/";
    public static final String KEY = "83MY66xw4ME8KUnxzDTaRG91481vhZUFzfbbpvPw";

//    Your API key for alexey.kozadaev@gmail.com is:
//
//            83MY66xw4ME8KUnxzDTaRG91481vhZUFzfbbpvPw
//    You can start using this key to make web service requests. Simply pass your key in the URL when making a web request. Here's an example:
//
//    https://api.nasa.gov/planetary/apod?api_key=83MY66xw4ME8KUnxzDTaRG91481vhZUFzfbbpvPwx
//    For additional support, please contact us. When contacting us, please tell us what API you're accessing and provide the following account details so we can quickly find you:
//
//    Account Email: alexey.kozadaev@gmail.com
//    Account ID: b3318c39-8360-4695-9dee-b3b546f524a2
    final ComicsApi api;

    public ComicsService() {
        Retrofit retrofit = createRetrofit();
        api = retrofit.create(ComicsApi.class);
    }

    public ComicsApi getApi() {
        return api;
    }

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();
                final HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", KEY)
                        .build();

                Log.d("url", url.toString());
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                final Request request = requestBuilder.build();
                try {
                    return chain.proceed(request);
                } catch (Throwable t) {
                    Log.e("Error" , t.getMessage());
                }
                return null;
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
