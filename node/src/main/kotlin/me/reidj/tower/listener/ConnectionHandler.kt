package me.reidj.tower.listener

import me.func.mod.Alert
import me.func.mod.Alert.send
import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.func.mod.util.after
import me.func.mod.util.command
import me.func.protocol.Indicators
import me.func.protocol.Tricolor
import me.func.protocol.alert.NotificationData
import me.func.protocol.dialog.*
import me.reidj.tower.content.DailyRewardType
import me.reidj.tower.npc.NpcManager
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.cristalix.core.formatting.Formatting
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */

private const val NAMESPACE = "http://storage.c7x.ru/reidj/"

object ConnectionHandler : Listener {

    init {
        Alert.put(
            "resourcepack",
            NotificationData(
                null,
                "notify",
                "Рекомендуем установить ресурспак",
                0x2a66bd,
                0x183968,
                30000,
                listOf(
                    Alert.button(
                        "Установить",
                        "/resourcepack",
                        Tricolor(0, 180, 0)
                    ),
                    Alert.button(
                        "Закрыть",
                        "/anime:debug",
                        Tricolor(180, 0, 0)
                    )
                ),
                null
            )
        )
        command("info") { player, _ -> Anime.openDialog(player, "tournamentPageTwo") }
    }

    private val dialog = Dialog(
        Entrypoint(
            "tournamentPageOne",
            "Турнир",
            Screen("тут букафы какие то").buttons(
                Button("Начать").actions(Action(Actions.COMMAND).command("/tournament"), Action(Actions.CLOSE)),
                Button("Что это такое?").actions(Action.command("/info")),
                Button("Закрыть").actions(Action(Actions.CLOSE))
            )
        ),
        Entrypoint(
            "tournamentPageTwo",
            "Турнир",
            Screen("тут инфа про турнир").buttons(
                Button("Понятно").actions(Action(Actions.CLOSE)),
                Button("Назад").actions(Action(Actions.OPEN_SCREEN).screen(Screen("tournamentPageOne")))
            )
        )
    )

    @EventHandler
    fun PlayerJoinEvent.handle() = player.apply {
        NpcManager.createNpcWithPlayerSkin(uniqueId)

        val user = SessionListener.simulator.getUser<User>(uniqueId)

        user?.player = this
        gameMode = GameMode.ADVENTURE
        LobbyItems.initialActionsWithPlayer(this)

        // Отправляем наш мод
        after(5) {
            ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", this)
            Anime.hideIndicator(
                this,
                Indicators.EXP,
                Indicators.ARMOR,
                Indicators.HUNGER,
                Indicators.HEALTH,
                Indicators.VEHICLE,
                Indicators.AIR_BAR
            )

            Anime.dialog(this, dialog, "tournamentPageOne")

            user?.giveMoney(-0)

            if (!user?.isAutoInstallResourcepack!!) Alert.find("resourcepack")
                .send(this) else performCommand("resourcepack")

            Anime.loadTextures(this, "${NAMESPACE}health_bar.png", "${NAMESPACE}energy.png", "${NAMESPACE}xp_bar.png")

            val now = System.currentTimeMillis().toDouble()
            // Обнулить комбо сбора наград если прошло больше суток или комбо > 7
            if ((user.day > 0 && now - user.lastEnter > 24 * 60 * 60 * 1000) || user.day > 6)
                user.day = 0
            if (now - user.dailyClaimTimestamp > 12 * 60 * 60 * 1000) {
                user.dailyClaimTimestamp = now
                Anime.openDailyRewardMenu(this, user.day, *DailyRewardType.values().map { it.reward }.toTypedArray())
                val dailyReward = DailyRewardType.values()[user.day]
                sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.reward.title))
                performCommand("lootboxsound")
                dailyReward.give.accept(user)
                user.day++
            }
            user.lastEnter = now
        }
    }
}