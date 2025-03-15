package de.qwqu.qma.modules;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DisconnectedScreen;

public class SpamPlus extends Module {
  public static String generateRandomUnicodeString(int length, int minRange, int range) {
    Random random = new Random();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int randomCodePoint = random.nextInt(minRange, range);
      if (Character.isBmpCodePoint(randomCodePoint) && Character.isDefined(randomCodePoint)) {
        stringBuilder.append(Character.toChars(randomCodePoint));
      } else {
        i--;
      }
    }
    return stringBuilder.toString();
  }

  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
      .name("spam-plus")
      .description("Spam with Unicode.")
      .defaultValue(new String[] { "Meteor on Crack!" })
      .build());

  private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
      .name("delay")
      .description("The delay between specified messages in ticks.")
      .defaultValue(20)
      .min(0)
      .sliderMax(200)
      .build());

  private final Setting<Boolean> disableOnLeave = sgGeneral.add(new BoolSetting.Builder()
      .name("disable-on-leave")
      .description("Disables spam when you leave a server.")
      .defaultValue(true)
      .build());

  private final Setting<Boolean> disableOnDisconnect = sgGeneral.add(new BoolSetting.Builder()
      .name("disable-on-disconnect")
      .description("Disables spam when you are disconnected from a server.")
      .defaultValue(true)
      .build());

  private final Setting<Boolean> random = sgGeneral.add(new BoolSetting.Builder()
      .name("randomise")
      .description("Selects a random message from your spam message list.")
      .defaultValue(false)
      .build());

  private final Setting<Boolean> autoSplitMessages = sgGeneral.add(new BoolSetting.Builder()
      .name("auto-split-messages")
      .description("Automatically split up large messages after a certain length")
      .defaultValue(false)
      .build());

  private final Setting<Integer> splitLength;

  private final Setting<Integer> autoSplitDelay;

  private final Setting<Boolean> bypass;

  private final Setting<Boolean> uppercase;

  private final Setting<Integer> length;

  private final Setting<Boolean> unicode;

  private final Setting<Integer> minUnicodeRange;

  private final Setting<Integer> maxUnicodeRange;

  private int messageI;

  private int timer;

  private int splitNum;

  private String text;

  public void onActivate() {
    timer = delay.get();
    messageI = 0;
    splitNum = 0;
  }

  @EventHandler
  private void onScreenOpen(OpenScreenEvent event) {
    if (disableOnDisconnect.get() && event.screen instanceof DisconnectedScreen)
      toggle();
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    if (disableOnLeave.get())
      toggle();
  }

  public SpamPlus() {
    super(Addon.CATEGORY, "spam+", "Spams specified messages with Unicode in chat.");

    splitLength = sgGeneral.add(new IntSetting.Builder()
        .name("split-length")
        .description("The length after which to split messages in chat")
        .visible(autoSplitMessages::get)
        .defaultValue(Integer.valueOf(256))
        .min(1)
        .sliderMax(256)
        .build());

    autoSplitDelay = sgGeneral.add(new IntSetting.Builder()
        .name("split-delay")
        .description("The delay between split messages in ticks.")
        .visible(autoSplitMessages::get)
        .defaultValue(Integer.valueOf(20))
        .min(0)
        .sliderMax(200)
        .build());

    bypass = sgGeneral.add(new BoolSetting.Builder()
        .name("bypass")
        .description("Add random text at the end of the message to try to bypass anti spams.")
        .defaultValue(false)
        .build());

    uppercase = sgGeneral.add(new BoolSetting.Builder()
        .name("include-uppercase-characters")
        .description("Whether the bypass text should include uppercase characters.").visible(bypass::get)
        .defaultValue(true)
        .build());

    length = sgGeneral.add(new IntSetting.Builder()
        .name("length")
        .description("Number of characters used to bypass anti spam.")
        .visible(bypass::get)
        .defaultValue(Integer.valueOf(16))
        .sliderRange(1, 256)
        .build());

    unicode = sgGeneral.add(new BoolSetting.Builder()
        .name("unicode")
        .description("Whether the bypass text should include unicode.")
        .visible(bypass::get)
        .defaultValue(true)
        .build());

    minUnicodeRange = sgGeneral.add(new IntSetting.Builder()
        .name("Minimum unicode")
        .description("Minimum range")
        .visible(unicode::get)
        .defaultValue(10)
        .sliderRange(1, 155063)
        .build());

    maxUnicodeRange = sgGeneral.add(new IntSetting.Builder()
        .name("Maximum range")
        .description("Generate within a range of Unicode Point.")
        .visible(unicode::get)
        .defaultValue(1000)
        .sliderRange(1, 155063).build());
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    if ((messages.get()).isEmpty())
      return;

    if (timer > 0) {
      timer--;
      return;
    }

    int i;
    if (random.get()) {
      i = Utils.random(0, messages.get().size());
    } else {
      if (messageI >= messages.get().size())
        messageI = 0;
      i = messageI++;
    }

    text = messages.get().get(i);

    if (minUnicodeRange.get() >= maxUnicodeRange.get()) {
      toggle();
      info("Invalid range, disabling!");
      return;
    }

    if (bypass.get()) {
      String bypass = unicode.get()
          ? generateRandomUnicodeString(length.get(), minUnicodeRange.get(), (maxUnicodeRange.get()))
          : (uppercase.get()
              ? RandomStringUtils.randomAlphabetic(length.get())
              : RandomStringUtils.randomAlphabetic(length.get()).toLowerCase());

      text += " " + bypass;
    }

    if (!autoSplitMessages.get() || text.length() <= splitLength.get()) {
      if (text.length() > 256)
        text = text.substring(0, 256);
      ChatUtils.sendPlayerMsg(text);
      timer = delay.get();
      text = null;
      return;
    }

    double length = text.length();
    int splits = (int) Math.ceil(length / splitLength.get());

    int start = splitNum * splitLength.get();
    int end = Math.min(start + (splitLength.get()), text.length());

    String msg = text.substring(start, end);
    ChatUtils.sendPlayerMsg(msg);

    splitNum = ++splitNum % splits;
    timer = (autoSplitDelay.get());
    if (splitNum == 0) {
      timer = (delay.get());
      text = null;
    }
  }
}
