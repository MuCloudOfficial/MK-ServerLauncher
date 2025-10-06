package me.mucloud.application.mk.serverlauncher.common.api.core.config.html

import me.mucloud.application.mk.serverlauncher.common.api.core.config.MuProperty

class MuHTMLTagProperty<T>(
    private val key: String,
    private val tag: MuHTMLFormTag,
    private val defValue: T? = null,
    private val avaliableValues: List<T> = listOf(),
    private val comment: String? = null,
    private val validation: (input: T) -> Boolean
): MuProperty<T> {

    private var value: T? = null

    override fun key(): String = key

    override fun value(): T? = value

    override fun defaultValue(): T? = defValue

    override fun comment(): String? = comment

    override fun validate(input: T): Boolean = validation(input)

    fun getHTMLFormTag(): MuHTMLFormTag = tag

}