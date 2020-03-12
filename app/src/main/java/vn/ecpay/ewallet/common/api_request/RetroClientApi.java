package vn.ecpay.ewallet.common.api_request;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClientApi {
    public static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static final String contentType = "application/json";

    public static Retrofit getRetrofitClient(String url_base) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .header("Content-Type", contentType);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        httpClient.connectTimeout(20, TimeUnit.SECONDS);
        httpClient.readTimeout(20, TimeUnit.SECONDS);
        httpClient.writeTimeout(20, TimeUnit.SECONDS);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url_base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}