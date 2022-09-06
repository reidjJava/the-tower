package me.reidj.tower.game

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.util.error
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class Rating : Game {

    private val queueSlots = 20

    companion object {
        private val queue = mutableListOf<UUID>()

        fun queueLeave(player: Player) = queue.removeIf {
            ModTransfer().send("queue:hide", player)
            ModTransfer(queue.size).send("queue:online", player)
            Anime.killboardMessage(player, "§cВы покинули очередь!")
            player.uniqueId in queue
        }
    }

    override fun start(player: Player) {
        if (TournamentManager.isTournamentDay()) {
            if (TournamentManager.getTournamentPlayers() != 0) {
                Anime.killboardMessage(player, "Турнирная игра уже началась!")
                return
            }
            if (queue.size < queueSlots) {
                Anime.killboardMessage(player, "§aВы добавлены в очередь!")
                queue.add(player.uniqueId)
                ModTransfer(queue.size).send("queue:online", player)
                ModTransfer("tournament.png", queueSlots).send("queue:show", player)
                if (queue.size == queueSlots) {
                    ModTransfer().send("queue:hide", player)
                    Anime.killboardMessage(player, "Игра начнётся через 2 секунды...")
                    after(2 * 20) {
                        Anime.title(player, "§dНачинаем!")
                        queue.forEach { uuid ->
                            (app.getUser(player) ?: return@forEach).isTournament = true
                            super.start(Bukkit.getPlayer(uuid))
                            queue.clear()
                        }
                    }
                } else {
                    player.error("Турнир ещё не начался!")
                }
            }
        }
    }

    init {
        command("tournament") { player, _ ->
            (app.getUser(player) ?: return@command).run {
                if (stat.tournament.passedWaves.size != 3) {
                    start(player)
                } else {
                    player.error("У вас закончились попытки!")
                }
            }
        }
        Anime.createReader("queue:leave") { player, _ -> queueLeave(player) }
    }
}