package net.ccbluex.liquidbounce

import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.management.CombatManager
import net.ccbluex.liquidbounce.management.MemoryManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import org.lwjgl.opengl.Display
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon


object LiquidBounce {


    // Client information
    const val CLIENT_NAME = "LiquidBounceReborn"
    const val CLIENT_VERSION = "b74"
    const val CLIENT_CREATOR = "CCBlueX,LangYa"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var combatManager: CombatManager
    lateinit var user: String

    // HUD & ClickGUI
    lateinit var hud: HUD

    lateinit var clickGui: ClickGui

    lateinit var wrapper: Wrapper


    private fun displayTray(Title: String, Text: String, type: TrayIcon.MessageType?) {
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("icon.png")
        val trayIcon = TrayIcon(image, "Tray Demo")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "System tray icon demo"
        tray.add(trayIcon)
        trayIcon.displayMessage(Title, Text, type)
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        Display.setTitle("LiquidBounceReborn")
        displayTray("狼牙土豆","啊啊啊我是果糖含量的狗!",TrayIcon.MessageType.INFO)

        val start = System.currentTimeMillis()

        // Create file manager
        fileManager = FileManager()

        // Create event manager
        eventManager = EventManager()

        // Create combat manager
        combatManager = CombatManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(InventoryUtils())
        eventManager.registerListener(combatManager)
        eventManager.registerListener(MemoryManager())

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()
        FontLoaders.initFonts()
        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Remapper
        try {
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig
        )

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable Optifine fast render
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()

        // Set is starting status
        isStarting = false

        ClientUtils.getLogger().info("Loaded client in " + (System.currentTimeMillis() - start) + " ms.")

    }


    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        fileManager.saveAllConfigs()
    }

}