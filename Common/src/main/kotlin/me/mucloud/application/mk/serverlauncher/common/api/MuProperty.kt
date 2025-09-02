package me.mucloud.application.mk.serverlauncher.common.api

interface MuProperty<T> {

    fun key(): String

    fun value(): T

    fun defaultValue(): T

    fun comment(): List<String>

}