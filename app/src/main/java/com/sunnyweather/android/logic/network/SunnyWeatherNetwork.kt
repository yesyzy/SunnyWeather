package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object SunnyWeatherNetwork {

    //拿到retrofit接口动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(place: String) = placeService.searchPlace(place).await()

    private suspend fun <T> Call<T>.await(): T{
        return suspendCoroutine {
            enqueue(object : Callback<T>{
                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) it.resume(body)
                    else it.resumeWithException(
                            RuntimeException("body is null")
                    )
                }

            })
        }
    }

}