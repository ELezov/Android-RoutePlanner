package elezov.com.routeplanner.model.api

import com.google.android.gms.common.api.Api

import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory

/**
 * Created by USER on 13.07.2017.
 */

public class ApiModule {

    companion object{
        fun getApiInterface(): ApiInterface
        {
            val url = "https://maps.googleapis.com/maps/"
            val builder = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

            val apiInterface = builder.build().create(ApiInterface::class.java)

            return apiInterface
        }
    }



}
