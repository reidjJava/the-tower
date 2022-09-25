package me.reidj.tower.game.wave

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.protocol.math.Position
import me.reidj.tower.app
import me.reidj.tower.arena.ArenaManager
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.ResearchType
import me.reidj.tower.game.wave.mob.Mob
import me.reidj.tower.game.wave.mob.MobType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Wave(
    var startTime: Long = 0,
    var level: Int,
    val aliveMobs: MutableList<Mob>,
    private val mobs: MutableList<Mob>,
    private val player: Player
) {

    fun start() {
        aliveMobs.clear()
        ModTransfer(40).send("tower:bar", player)
        Anime.overlayText(player, Position.BOTTOM_RIGHT, "Волна: §3$level")
        repeat(6 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, {
                val session = (app.getUser(player) ?: return@runTaskLater).session ?: return@runTaskLater
                drawMob(session.arena.generators.random().apply {
                    x += Math.random() * 4 - 2
                    z += Math.random() * 4 - 2
                })
            }, minOf(it.toLong() * 12, 400))
        }
        startTime = System.currentTimeMillis()
    }

    fun end() {
        val user = app.getUser(player) ?: return
        val stat = user.stat
        val session = user.session!!
        startTime = 0
        level++
        mobs.clear()
        user.giveTokenWithBooster(stat.userImprovementType[ImprovementType.CASH_BONUS_WAVE_PASS]!!.getValue() + stat.researchType[ResearchType.CASH_BONUS_WAVE_PASS]!!.getValue())
        if (level % 10 == 0) {
            Anime.cursorMessage(user.player, "§e+10 §fмонет")
            user.giveMoneyWithBooster(10.0)
        } else if (level == 16) {
            Anime.alert(player, "Поздравляем!", "Вы прошли ${session.arena.arenaNumber} уровень!")
            session.arena = ArenaManager.arenas[ArenaManager.arenas.indexOf(session.arena) + 1]
            val cubeLocation = session.arena.cubeLocation
            ModTransfer(cubeLocation.x, cubeLocation.y, cubeLocation.z).send("tower:map-change", player)
            player.teleport(session.arena.arenaSpawn)
            Anime.overlayText(player, Position.BOTTOM_RIGHT, "Уровень: §3${session.arena.arenaNumber}")
            level = 0
        }
        Anime.counting321(user.player)
        after(3 * 20) { start() }
    }

    private fun drawMob(location: Location) {
        val hpFormula = level * if ((app.getUser(player) ?: return).session!!.arena.arenaNumber > 1) 0.5 else 0.3
        val damageFormula = level * 0.05
        MobType.values()
            .filter { it.wave.any { wave -> level % wave == 0 } }
            .forEach {
                if (level % 10 == 0 && mobs.none { mob -> mob.isBoss } && it.isBoss) {
                    Mob {
                        hp = it.hp + hpFormula
                        damage = it.damage + damageFormula
                        type = EntityType.valueOf(it.name)
                        isBoss = true
                        speedAttack = it.speedAttack
                        moveSpeed = it.moveSpeed
                        attackRange = it.attackRange
                        isShooter = it.isShooter
                    }.location(location).create(player).run {
                        aliveMobs.add(this)
                        mobs.add(this)
                    }
                } else if (!it.isBoss) {
                    Mob {
                        hp = it.hp + hpFormula
                        damage = it.damage + damageFormula
                        type = EntityType.valueOf(it.name)
                        speedAttack = it.speedAttack
                        attackRange = it.attackRange
                        isShooter = it.isShooter
                    }.location(location).create(player).run {
                        aliveMobs.add(this)
                        mobs.add(this)
                    }
                }
            }
    }
}
