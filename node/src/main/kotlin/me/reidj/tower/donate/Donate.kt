package me.reidj.tower.donate

import me.reidj.tower.data.Stat
import me.reidj.tower.user.User

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
interface Donate {

    fun getTitle(): String

    fun getDescription(): String

    fun getTexture(): String

    fun getObjectName(): String

    fun getPrice(): Long

    fun give(user: User)

    fun getCurrent(stat: Stat): Boolean

    fun setCurrent(stat: Stat)

}