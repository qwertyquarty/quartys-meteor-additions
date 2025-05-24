package de.qwqu.qma.mixin;

import de.qwqu.qma.Addon;
import de.qwqu.qma.modules.NoScreen;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("RETURN"))
    private void onSetScreen(Screen screen, CallbackInfo cbInfo) {
        if (screen == null) return;

        Modules modules = Modules.get();
        if (modules == null) return;

        NoScreen noScreenModule = modules.get(NoScreen.class);
        if (noScreenModule == null) return;

        if (!noScreenModule.isActive()) return;

        Settings settings = noScreenModule.settings;

        boolean logScreens = ((BoolSetting) settings.get("log-screens")).get();
        boolean logDisabled = ((BoolSetting) settings.get("log-disabled")).get();
        boolean logToChat = ((BoolSetting) settings.get("log-to-chat")).get();
        List<String> screens = ((StringListSetting) settings.get("screens")).get();

        String screenName = screen.getClass().getSimpleName();
        boolean isDisabled = screens.contains(screenName);

        if (logScreens) {
            if (logDisabled || !isDisabled) {
                String message = String.format("[NoScreen] %s", screenName);
                Addon.LOG.info(message);
                if (logToChat) info(message);
            }
        }

        if (!isDisabled) return;

        mc.setScreen(null);
    }
}
