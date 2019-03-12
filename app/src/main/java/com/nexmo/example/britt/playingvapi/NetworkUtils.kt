package com.nexmo.example.britt.playingvapi

import com.squareup.moshi.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


const val ANSWER_URL = "https://nexmo-community.github.io/ncco-examples/first_call_talk.json"
const val CALLER_PHONE_NUM = "12345678901"
val SAMPLE_NCCO = listOf(
    Talk("Hi this is nexmo talking!!"),
    Stream(listOf(Tunes.DixieHorn.url)),
    Talk("Press 1 if you're happy, press 2 if you're incredibly happy, followed by the hash key"),
    Input(timeOut = 5, submitOnHash = true),
    Talk("Thank you for the input! Bye Bye")
)


var moshi = Moshi.Builder()
    .add(
        PolymorphicJsonAdapterFactory.of(NccoAction::class.java, "action")
            .withSubtype(Talk::class.java, ActionType.talk.name)
            .withSubtype(Stream::class.java, ActionType.stream.name)
            .withSubtype(Input::class.java, ActionType.input.name)
    )
    .add(KotlinJsonAdapterFactory())
    .build()

val httpLogging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
val httpClient = OkHttpClient.Builder().addInterceptor(httpLogging).build()

var retrofit = Retrofit.Builder()
    .baseUrl("https://api.nexmo.com/v1/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(httpClient)
    .build()

var nexmoService = retrofit.create<NexmoApiService>(NexmoApiService::class.java)
