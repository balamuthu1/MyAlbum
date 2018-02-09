package leboncoin.test.com.myphotos.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Muthu on 09/02/2018.
 */

public class MyPhotoRetrofitClient {
    private static Retrofit retrofit = null;

    //Builds the retrofit client with base url for api calls
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiUtils.BASE_URL)
                    //convertor factory to convert json to pojo
                    .addConverterFactory(GsonConverterFactory.create())
                    //optional: Okhttp client for timeouts and loggings
                    .client(buildClient())
                    .build();
        }
        return retrofit;
    }

    //Builds OkHttpClient to set Timeouts, loggings and other stuffs
    private static OkHttpClient buildClient() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }
}
