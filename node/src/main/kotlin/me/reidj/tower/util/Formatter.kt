package me.reidj.tower.util

import java.text.DecimalFormat

object Formatter {

    private val FORMAT = DecimalFormat("#,###,###,##0")

    fun toFormat(double: Double): String = FORMAT.format(double)
}