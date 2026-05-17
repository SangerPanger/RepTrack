package com.example.fitnessapp.`data`.local.database

import kotlin.reflect.KClass

internal fun KClass<AppDatabase>.instantiateImpl(): AppDatabase =
    com.example.fitnessapp.`data`.local.database.AppDatabase_Impl()
