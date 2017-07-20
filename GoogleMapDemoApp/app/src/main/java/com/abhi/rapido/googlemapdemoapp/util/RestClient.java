package com.abhi.rapido.googlemapdemoapp.util;



import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RestClient {
    public static Retrofit retrofit;

    public static <T> T createService(Class<T> service) {
        if (retrofit == null) {
            createRetrofit();
        }
        return retrofit.create(service);
    }

    private static void createRetrofit() {
        /*OkHttp:An HTTP & HTTP/2 client for Android and Java applications.*/
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(NetworkConstants.TIMEOUT, TimeUnit.SECONDS);
        httpClient.readTimeout(NetworkConstants.TIMEOUT, TimeUnit.SECONDS);

        // add your other interceptors …
        if (NetworkConstants.IS_DEBUG) {
            /*An OkHttp interceptor which logs request and response information.*/
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);  // <-- this is the important line!
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
}
