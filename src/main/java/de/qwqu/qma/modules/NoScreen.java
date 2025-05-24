package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;

import java.util.List;

public class NoScreen extends Module {
    public NoScreen() {
        super(Addon.CATEGORY, "no-screen", "Disables some screens.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> logScreens = sgGeneral.add(new BoolSetting.Builder()
        .name("log-screens")
        .description("Logs the screen names.")
        .defaultValue(false)
        .build());

    private final Setting<Boolean> logDisabled = sgGeneral.add(new BoolSetting.Builder()
        .name("log-disabled")
        .description("Also logs disabled screens.")
        .visible(logScreens::get)
        .defaultValue(false)
        .build());


    private final Setting<Boolean> logToChat = sgGeneral.add(new BoolSetting.Builder()
        .name("log-to-chat")
        .description("Logs to chat too.")
        .visible(logScreens::get)
        .defaultValue(true)
        .build());

    private final Setting<List<String>> screens = sgGeneral.add(new StringListSetting.Builder()
        .name("screens")
        .description("Screens to disable.")
        .build());
}
