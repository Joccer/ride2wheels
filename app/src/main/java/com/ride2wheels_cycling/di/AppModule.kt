package com.ride2wheels_cycling.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.ride2wheels_cycling.db.RidingDatabase
import com.ride2wheels_cycling.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.ride2wheels_cycling.other.Constants.KEY_NAME
import com.ride2wheels_cycling.other.Constants.KEY_WEIGHT
import com.ride2wheels_cycling.other.Constants.PREFERENCES_NAME
import com.ride2wheels_cycling.other.Constants.RIDING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRidingDatabeas(@ApplicationContext app: Context) = Room.databaseBuilder(
        app, RidingDatabase::class.java, RIDING_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRideDao(db: RidingDatabase) = db.getRideDao()

    @Singleton
    @Provides
    fun providePreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(preferences: SharedPreferences) = preferences.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(preferences: SharedPreferences) = preferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(preferences: SharedPreferences) = preferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
}