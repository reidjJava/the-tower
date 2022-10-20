package player

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.resource.ResourceLocation
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import screenCheck
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */
class PlayerManager {

    private val gemBox = createBox(true, "gem")
    private val moneyBox = createBox(false, "coin")
    private val levelBox = carved {
        origin = BOTTOM
        align = BOTTOM
        offset.y -= 24.0
        size = V3(66.0, 12.0)
        color = Color(0, 0, 0, 0.62)
        +carved {
            origin = CENTER
            align = CENTER
            size = V3(0.0, 11.0, 0.0)
            color = Color(34, 184, 77, 1.0)
        }
        +text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(0.9, 0.9)
            content = "Загрузка..."
        }
    }

    companion object {
        var swordDamage = 1.0
    }

    init {
        UIEngine.overlayContext.addChild(gemBox, moneyBox, levelBox)

        mod.registerChannel("tower:money") {
            val money = readDouble()
            (moneyBox.children[4] as TextElement).content = Formatter.toMoneyFormat(money)
        }

        mod.registerChannel("tower:gem") {
            val gem = readInt()
            (gemBox.children[4] as TextElement).content = gem.toString()
        }

        mod.registerChannel("tower:exp") {
            val experience = readDouble()
            val requiredExperience = readInt()

            (levelBox.children[3] as RectangleElement).animate(1) { size.x = 66.0 / requiredExperience * experience }
            (levelBox.children[4] as TextElement).content = "${experience.toInt()} из $requiredExperience"
        }

        mod.registerChannel("user:sword") {
            swordDamage = readDouble()
        }

        mod.registerHandler<GameLoop> {
            val has = if (mod.gameActive) false else screenCheck()
            moneyBox.enabled = has
            gemBox.enabled = has
            levelBox.enabled = has
        }
    }

    private fun createBox(isLeft: Boolean, texture: String) = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(if (isLeft) -62.5 else 62.5, -24.0)
        size = V3(57.0, 12.0)
        color = Color(0, 0, 0, 0.62)
        +rectangle {
            origin = CENTER
            align = if (isLeft) LEFT else RIGHT
            size = V3(9.5, 9.5, 9.5)
            color = WHITE
            if (isLeft) offset.x += 8.0 else offset.x -= 8.0
            textureLocation = ResourceLocation.of("minecraft", "mcpatcher/cit/tower/$texture.png")
        }
        +text {
            origin = CENTER
            align = CENTER
            shadow = true
            if (isLeft) offset.x += 4.0 else offset.x -= 4.0
            scale = V3(0.9, 0.9)
            content = "Загрузка..."
        }
    }
}