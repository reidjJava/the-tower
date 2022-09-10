package me.reidj.tower.game

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.menu.button
import me.func.mod.menu.choicer
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.ResearchType
import me.reidj.tower.game.wave.Wave
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.user.Session
import me.reidj.tower.util.PATH
import me.reidj.tower.util.flying
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
interface Game {

    companion object {
        private const val MOVE_SPEED: Double = .01
        private const val CONST_TICKS_BEFORE_STRIKE = 20
        private const val TICKS_BEFORE_STRIKE = 40

        val menu = choicer {
            title = "Tower Simulator"
            description = "Выберите под-режим"
            buttons(
                button {
                    title = "Обычный"
                    texture = "${PATH}default.png"
                    description = "Игроков: §3${TournamentManager.getOnlinePlayers().size}"
                    hint("Играть")
                    onClick { player, _, _ -> player.performCommand("default") }
                },
                button {
                    title = "Турнир"
                    texture = "${PATH}rating.png"
                    description = "Игроков: §3${TournamentManager.getTournamentPlayers()}"
                    hint("Играть")
                    onClick { player, _, _ -> player.performCommand("tournament") }
                }
            )
        }
    }

    fun start(player: Player) {
        Anime.close(player)
        (app.getUser(player) ?: return).run {
            session = Session(tower!!.upgrades)

            val session = session!!

            session.towerImprovement.values.forEach { it.level = 1 }

            hideFromAll()

            player.run {
                inventory.clear()
                teleport(session.arenaSpawn)
                flying()
            }

            giveTokens(getLevel() + 1.0)

            tower!!.run tower@{
                health = maxHealth
                updateHealth()
                updateBulletDelay()
                updateDamage()
                updateProtection()
                update(
                    this@run,
                    ImprovementType.REGEN,
                    ImprovementType.RADIUS,
                )
            }

            update(
                this,
                ImprovementType.CASH_BONUS_KILL,
                ImprovementType.CASH_BONUS_WAVE_PASS,
                ResearchType.CASH_BONUS_WAVE_PASS,
                ResearchType.CASH_BONUS_KILL
            )

            // Отправляем точки со спавнерами
            session.generators.forEach { label -> ModTransfer(label.x, label.y, label.z).send("mobs:init", player) }

            Anime.counting321(player)

            inGame = true
            after(3 * 20) {
                wave = Wave(System.currentTimeMillis(), 1, mutableListOf(), mutableListOf(), player)
                wave!!.start()

                // Игра началась
                ModTransfer(
                    true,
                    session.cubeLocation.x,
                    session.cubeLocation.y,
                    session.cubeLocation.z,
                    MOVE_SPEED,
                    TICKS_BEFORE_STRIKE,
                    CONST_TICKS_BEFORE_STRIKE
                ).send("tower:update-state", player)
            }
        }
    }
}