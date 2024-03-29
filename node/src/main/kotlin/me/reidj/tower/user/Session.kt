package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.arena.ArenaManager
import me.reidj.tower.data.Improvement
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.Pumping
import me.reidj.tower.game.Gem
import me.reidj.tower.upgrade.Upgradable
import java.util.concurrent.ConcurrentHashMap

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Session(var towerImprovement: MutableMap<ImprovementType, Improvement>) : Upgradable {

    var arena = ArenaManager.arenas[0]
    val gems = ConcurrentHashMap.newKeySet<Gem>()

    override fun update(user: User, vararg pumping: Pumping) {
        pumping.filterIsInstance<ImprovementType>()
            .forEach {
                ModTransfer(towerImprovement[it]!!.getValue()).send(
                    "tower:${it.name.lowercase()}",
                    user.player
                )
            }
    }
}
