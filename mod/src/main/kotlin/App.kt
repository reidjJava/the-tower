import com.sun.media.jfxmedia.events.PlayerEvent
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.render.Tessellator
import dev.xdark.clientapi.resource.ResourceLocation
import io.netty.buffer.Unpooled
import mob.MobManager
import player.Statistic
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import tower.BarManager
import tower.Cube
import tower.TowerManager
import java.util.*

lateinit var mod: App
const val NAMESPACE = "tower"
const val FILE_STORE = "http://storage.c7x.ru/reidj/"

class App : KotlinMod() {

    lateinit var cube: V3
    var inited = false
    var gameActive = false

    override fun onEnable() {
        mod = this
        UIEngine.initialize(this)

        Statistic

        val player = clientApi.minecraft().player
        val tessellator: Tessellator = clientApi.tessellator()
        val render = tessellator.bufferBuilder
        val sx = 16.0
        val sy = 16.0
        val sz = 16.0
        val uSize = sx
        val u1 = sx / uSize
        val u2 = u1 + sz / uSize
        val u3 = u2 + sx / uSize
        val u4 = u3 + sz / uSize
        val u5 = u3 + sx / uSize
        val u6 = u4 + sx / uSize

        val v1 = sy / sx
        val v2 = v1 + sz / sx
        val v3 = v2 + sy / sx

        loadTextures(
                load("health_bar.png", "35320C088F83D8890128127"),
                load("energy.png", "35320C088F83D8890128111"),
                load("xp_bar.png", "35320C094F83D8890128111")
        ).thenRun {
            BarManager
            TowerManager
            Cube
        }

        registerHandler<HealthRender> { isCancelled = true }
        registerHandler<ExpBarRender> { isCancelled = true }
        registerHandler<HungerRender> { isCancelled = true }
        registerHandler<ArmorRender> { isCancelled = true }
        registerHandler<AirBarRender> { isCancelled = true }
        registerHandler<VehicleHealthRender> { isCancelled = true }

        registerChannel("tower:update-state") {
            gameActive = readBoolean()
            if (gameActive) {
                mod.cube = V3(
                    readDouble(),
                    readDouble() + 1,
                    readDouble()
                )
                MobManager.moveSpeed = readDouble()
                TowerManager.ticksBeforeStrike = readInt()
                TowerManager.ticksStrike = readInt()
                TowerManager.healthBanner = Banners.create(
                    UUID.randomUUID(),
                    mod.cube.x,
                    mod.cube.y - 1.25,
                    mod.cube.z,
                    "",
                    2.0,
                    true
                )
                mod.inited = true

                MobManager

                UIEngine.schedule(1.0) { TowerManager.updateHealth() }
            } else {
                Banners.remove(TowerManager.healthBanner!!.uuid)
                MobManager.clear()
            }
        }

        registerHandler<EntityLeftClick> {
            clientApi.clientConnection().sendPayload(
                "mob:hit",
                Unpooled.copiedBuffer(entity.uniqueID.toString(), Charsets.UTF_8)
            )
        }
    }

    private fun load(path: String, hash: String): RemoteTexture {
        return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
    }
}