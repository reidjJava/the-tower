package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.pumping.Upgrade
import me.reidj.tower.pumping.UpgradeType
import me.reidj.tower.wave.Wave
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.common.SimulatorUser
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
class User(val id: UUID, var maxWavePassed: Int, var upgradeTypes: MutableMap<UpgradeType, Upgrade>, val tower: Tower) :
    SimulatorUser(), Upgradable {

    @Transient
    var wave: Wave? = null

    @Transient
    var player: Player? = null
        set(current) {
            tower.owner = current
            field = current
        }

    @Transient
    var inGame: Boolean = false

    @Transient
    lateinit var session: Session

    @Transient
    var tokens = 0

    fun giveTokens(tokens: Int) {
        this.tokens += tokens
        ModTransfer(tokens).send("tower:tokens", player)
    }

    fun giveMoney(money: Int) {
        this.money += money
        ModTransfer(money).send("tower:money", player)
    }

    fun giveExperience(exp: Int) {
        //val prevLevel = SessionListener.simulator.run { this@User.getLevel() }
        this.exp += exp
        ModTransfer()
            .integer(SessionListener.simulator.run { getLevel() })
            .integer(exp)
            .integer(SessionListener.simulator.run { getNextLevelExp() })
            .send("tower:exp", player)
        /*if (exp >= prevLevel) {
            Glow.animate(player!!, .5, GlowColor.GREEN)
            Anime.topMessage(
                player!!,
                "§bВаш уровень был повышен!\n§7$prevLevel §f ➠ §l${SessionListener.simulator.run { this@User.getLevel() }}"
            )
        }*/
    }

    override fun update(user: User, vararg type: UpgradeType) =
        type.forEach { ModTransfer(upgradeTypes[it]!!.getValue()).send("user:${it.name.lowercase()}", player) }

}