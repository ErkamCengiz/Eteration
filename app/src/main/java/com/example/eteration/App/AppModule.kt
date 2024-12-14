package com.example.eteration.App

import android.app.Application
import androidx.room.Room
import com.example.eteration.Interfaces.PackageDao
import com.example.eteration.Interfaces.RetrofitApi
import com.example.eteration.LocalDB.LocalDB
import com.example.eteration.ViewModels.AppViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL ="https://5fc9346b2af77700165ae514.mockapi.io/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient()
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder: OkHttpClient.Builder =
            client.newBuilder().addInterceptor(interceptor)

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }

    @Provides
    fun provideApi(): RetrofitApi {
        return provideRetrofit().create(RetrofitApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(): LocalDB {
        return Room.databaseBuilder(
            App.getAppContext(),
            LocalDB::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePackageDao(): PackageDao {
        return provideRoomDatabase().packageDao()
    }

    @Provides
    @Singleton
    fun provideAppViewModel(): AppViewModel {
        return AppViewModel(this)
    }
}
