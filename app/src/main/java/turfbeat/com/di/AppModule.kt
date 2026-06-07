package turfbeat.com.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import turfbeat.com.BuildConfig
import turfbeat.com.data.remote.ApiService
import turfbeat.com.data.remote.AuthInterceptor
import turfbeat.com.data.remote.TokenManager
import turfbeat.com.data.repository.AuthRepository
import turfbeat.com.data.repository.ClubRepository
import turfbeat.com.data.repository.MatchRepository
import turfbeat.com.data.repository.UserRepository
import turfbeat.com.data.repository.VenueRepository
import turfbeat.com.ui.viewmodel.*
import java.util.concurrent.TimeUnit

val appModule = module {

    single<Gson> {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    single {
        AuthInterceptor(androidContext())
    }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL + "/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create(get<Gson>()))
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }
    single { TokenManager(androidContext()) }
    single { AuthRepository(get<ApiService>(), get<TokenManager>()) }
    single { ClubRepository(get<ApiService>()) }
    single { VenueRepository(get<ApiService>()) }
    single { UserRepository(get<ApiService>()) }
    single { MatchRepository(get<ApiService>()) }

    factory { AuthViewModel(get<AuthRepository>(), get<TokenManager>()) }
    factory { ClubViewModel(get<ClubRepository>()) }
    factory { VenueViewModel(get<VenueRepository>()) }
    factory { PlayerViewModel(get<UserRepository>()) }
    factory { MatchViewModel(get<MatchRepository>()) }
    factory { PlayerDashboardViewModel(get<UserRepository>(), get<ClubRepository>(), get<MatchRepository>()) }
    factory { ClubDashboardViewModel(get<ClubRepository>()) }
}
