package com.example.retrofit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class Retro {
    public static Api api(){
return new Retrofit.Builder().baseUrl("http://designoweb.work/").addConverterFactory(GsonConverterFactory.create()).build().create(Api.class);

    }
}
