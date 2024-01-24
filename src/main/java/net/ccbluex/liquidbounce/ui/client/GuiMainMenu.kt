package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.minecraft.client.resources.I18n

class GuiMainMenu : WrappedGuiScreen() {
    override fun initGui() {
        val defaultHeight = (representedScreen.height/ 3.5).toInt()
        representedScreen.buttonList.add(classProvider.createGuiButton(1, representedScreen.width / 2 - 50, defaultHeight, 100, 20, I18n.format("Singleplayer")))
        representedScreen.buttonList.add(classProvider.createGuiButton(2, representedScreen.width / 2 - 50, defaultHeight + 24, 100, 20, I18n.format("Multiplayer")))
        representedScreen.buttonList.add(classProvider.createGuiButton(3, representedScreen.width / 2 - 50, defaultHeight + 24*2, 100, 20, "AltManager"))
        representedScreen.buttonList.add(classProvider.createGuiButton(4, representedScreen.width / 2 - 50, defaultHeight + 24*3, 100, 20, I18n.format("Options...")))
        representedScreen.buttonList.add(classProvider.createGuiButton(5, representedScreen.width / 2 - 50, defaultHeight + 24*4, 100, 20, I18n.format("Quit Game")))
        super.initGui()
    }
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)
        representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            1 -> mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
            2 -> mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
            3 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)))
            4 -> mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen, mc.gameSettings))
            5 -> mc.shutdown()
        }
    }
}