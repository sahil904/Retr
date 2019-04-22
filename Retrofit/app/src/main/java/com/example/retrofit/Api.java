package com.example.retrofit;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("gambling/Gambling_api/table_loadmore")
    Call<ModelClassforretro>MODEL_CALL(@Field("id") int id);
}
