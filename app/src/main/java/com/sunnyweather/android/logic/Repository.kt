package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    /**
     * liveData协程构造方法提供了一个协程代码块参数，当LiveData被观察时，里面的操作就会执行。
     * LiveData 协程构造方法还可以接收一个 Dispatcher 作为参数，这样就可以将这个协程移至另一个线程。
     */
    fun searchPlaces(place: String) = fire(Dispatchers.IO){
        val placeResponse = SunnyWeatherNetwork.searchPlaces(place)
        if (placeResponse.status == "ok"){
            val places = placeResponse.places
            Result.success(places)
        }else{
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO){
        /**
         * coroutScope函数可以保证其作用域内的【所有代码】和【子协程】在全部执行完之前
         * 外部协程会一直被挂起
         * 调用了async函数之后，代码块中的代码就会立刻开始执行，await()会阻塞当前协程
         */
        /**
         * coroutScope函数可以保证其作用域内的【所有代码】和【子协程】在全部执行完之前
         * 外部协程会一直被挂起
         * 调用了async函数之后，代码块中的代码就会立刻开始执行，await()会阻塞当前协程
         */
        coroutineScope{
            //并行请求实时天气和预报天气
            val deferredRealtime = async{
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            }else{
                Result.failure(RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                ))
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavePlace() = PlaceDao.getSavePlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
            liveData<Result<T>>(context){
                val result = try {
                    block()
                }catch (e: Exception){
                    Result.failure<T>(e)
                }
                emit(result)
            }
}