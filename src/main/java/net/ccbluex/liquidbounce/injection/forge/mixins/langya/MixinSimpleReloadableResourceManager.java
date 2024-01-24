package net.ccbluex.liquidbounce.injection.forge.mixins.langya;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager {
    @Shadow @Final private List<IResourceManagerReloadListener> reloadListeners;

    @Inject(method = "notifyReloadListeners", at = @At("HEAD"), cancellable = true)
    private void notifyReloadListeners(CallbackInfo ci) {
        for (IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiLanguage) {
                if (!(iresourcemanagerreloadlistener instanceof GuiLanguage)) {
                    continue;
                }
            }
            iresourcemanagerreloadlistener.onResourceManagerReload((IResourceManager) this);
        }
    }
}
