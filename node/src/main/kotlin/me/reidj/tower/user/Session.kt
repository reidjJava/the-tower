package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeType

data class Session(var upgrade: MutableMap<UpgradeType, Upgrade>): Upgradable {
    override fun update(user: User, vararg type: UpgradeType) =
        type.forEach { ModTransfer(upgrade[it]!!.getValue()).send("user:${it.name.lowercase()}", user.player) }
}