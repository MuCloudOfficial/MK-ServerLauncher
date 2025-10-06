package me.mucloud.application.mk.serverlauncher.common.api.core.config

interface MuProperty<T> {

    fun key(): String

    fun value(): T?

    fun defaultValue(): T?

    fun comment(): String?

    fun validate(input: T): Boolean

}