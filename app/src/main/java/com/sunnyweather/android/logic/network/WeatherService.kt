package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    //实况天气接口
//https://api.caiyunapp.com/v2.5/TAkhjf8d1nlSlspN/121.6544,25.1552/realtime.json
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng}, {lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
        Call<RealtimeResponse>

    //天气预报接口
    //https://api.caiyunapp.com/v2.5/TAkhjf8d1nlSlspN/121.6544,25.1552/daily.json
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>

}