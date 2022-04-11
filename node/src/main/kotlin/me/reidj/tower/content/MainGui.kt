package me.reidj.tower.content

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.user.User
import org.bukkit.Material.CLAY_BALL
import org.bukkit.Material.STAINED_GLASS_PANE
import org.bukkit.entity.Player
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.ControlledInventory
import ru.cristalix.core.inventory.InventoryContents
import ru.cristalix.core.inventory.InventoryProvider
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object MainGui {

    val backItem = item {
        type = CLAY_BALL
        text("§cНазад")
        nbt("other", "cancel")
    }

    val glass = item {
        type = STAINED_GLASS_PANE
        data = 7
        text("&f")
    }

    private val menu = ControlledInventory.builder()
        .title("Tower Simulator")
        .rows(5)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "XXXDUAXXX",
                    "XXXXSXXXX",
                    "XXXXXXXXX",
                    "XXXXQXXXX"
                )

                val user = SessionListener.simulator.getUser<User>(player.uniqueId)!!

                contents.add('U', ClickableItem.of(UpgradeInventory.workshop) { player.performCommand("workshop") })
                contents.add('S', ClickableItem.empty(item {
                    type = CLAY_BALL
                    nbt("other", "quest_week")
                    text(
                        "§f§l > §bСтатистика\n" +
                                "§7    Монет: §e${user.money}\n" +
                                "§7    Волн пройдено: §b${user.maxWavePassed}\n"
                    )
                }))
                contents.add('Q', ClickableItem.of(backItem) { player.closeInventory() })
                contents.fillMask('X', ClickableItem.empty(glass))
            }
        }).build()

    init {
        B.regConsumerCommand({ player, _ -> menu.open(player) }, "menu")
    }
}