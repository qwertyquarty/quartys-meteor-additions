package de.qwqu.qma.modules;

import java.util.List;
import java.util.Objects;
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

  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<List<String>> messages = this.sgGeneral
      .add(new StringListSetting.Builder()
          .name("spam-plus")
          .description("Spam with Unicode.").defaultValue(new String[] { "Meteor on Crack!" }).build());

  private final Setting<Integer> delay = this.sgGeneral
      .add(new IntSetting.Builder()
          .name("delay")
          .description("The delay between specified messages in ticks.")
          .defaultValue(Integer.valueOf(20))
          .min(0)
          .sliderMax(200)
          .build());

  private final Setting<Boolean> disableOnLeave = this.sgGeneral
      .add(new BoolSetting.Builder()
          .name("disable-on-leave")
          .description("Disables spam when you leave a server.")
          .defaultValue(true)
          .build());

  private final Setting<Boolean> disableOnDisconnect = this.sgGeneral
      .add(new BoolSetting.Builder()
          .name("disable-on-disconnect")
          .description("Disables spam when you are disconnected from a server.")
          .defaultValue(true)
          .build());

  private final Setting<Boolean> random = this.sgGeneral
      .add(new BoolSetting.Builder()
          .name("randomise")
          .description("Selects a random message from your spam message list.")
          .defaultValue(false)
          .build());

  private final Setting<Boolean> autoSplitMessages = this.sgGeneral
      .add(new BoolSetting.Builder()
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
    this.timer = ((Integer) this.delay.get()).intValue();
    this.messageI = 0;
    this.splitNum = 0;
  }

  @EventHandler
  private void onScreenOpen(OpenScreenEvent event) {
    if (((Boolean) this.disableOnDisconnect.get()).booleanValue() && event.screen instanceof DisconnectedScreen)
      toggle();
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    if (((Boolean) this.disableOnLeave.get()).booleanValue())
      toggle();
  }

  public SpamPlus() {
    super(Addon.CATEGORY, "spam+", "Spams specified messages with Unicode in chat.");
    Objects.requireNonNull(this.autoSplitMessages);
    this.splitLength = this.sgGeneral.add(
        new IntSetting.Builder()
            .name("split-length").description("The length after which to split messages in chat")
            .visible(this.autoSplitMessages::get).defaultValue(Integer.valueOf(256)).min(1).sliderMax(256).build());
    Objects.requireNonNull(this.autoSplitMessages);

    this.autoSplitDelay = this.sgGeneral.add(
        new IntSetting.Builder()
            .name("split-delay")
            .description("The delay between split messages in ticks.")
            .visible(this.autoSplitMessages::get)
            .defaultValue(Integer.valueOf(20))
            .min(0)
            .sliderMax(200)
            .build());

    this.bypass = this.sgGeneral.add(new BoolSetting.Builder()
        .name("bypass").description("Add random text at the end of the message to try to bypass anti spams.")
        .defaultValue(false).build());

    Objects.requireNonNull(this.bypass);
    this.uppercase = this.sgGeneral.add(
        new BoolSetting.Builder()
            .name("include-uppercase-characters")
            .description("Whether the bypass text should include uppercase characters.").visible(this.bypass::get)
            .defaultValue(true).build());
    Objects.requireNonNull(this.bypass);
    this.length = this.sgGeneral.add(
        new IntSetting.Builder()
            .name("length").description("Number of characters used to bypass anti spam.").visible(this.bypass::get)
            .defaultValue(Integer.valueOf(16)).sliderRange(1, 256).build());
    Objects.requireNonNull(this.bypass);
    this.unicode = this.sgGeneral.add(
        new BoolSetting.Builder()
            .name("unicode").description("Whether the bypass text should include unicode.").visible(this.bypass::get)
            .defaultValue(true).build());
    Objects.requireNonNull(this.unicode);
    this.minUnicodeRange = this.sgGeneral.add(
        new IntSetting.Builder()
            .name("Minimum unicode").description("Minimum range").visible(this.unicode::get)
            .defaultValue(Integer.valueOf(10)).sliderRange(1, 155063).build());
    Objects.requireNonNull(this.unicode);
    this.maxUnicodeRange = this.sgGeneral.add(
        new IntSetting.Builder()
            .name("Maximum range").description("Generate within a range of Unicode Point.")
            .visible(this.unicode::get).defaultValue(Integer.valueOf(1000)).sliderRange(1, 155063).build());
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    if ((this.messages.get()).isEmpty())
      return;
    if (this.timer <= 0) {
      if (this.text == null) {
        int i;
        if (((Boolean) this.random.get()).booleanValue()) {
          i = Utils.random(0, (this.messages.get()).size());
        } else {
          if (this.messageI >= this.messages.get().size())
            this.messageI = 0;
          i = this.messageI++;
        }
        this.text = ((List<String>) this.messages.get()).get(i);
        if (((Boolean) this.bypass.get()).booleanValue()) {
          String bypass;
          if (!((Boolean) this.unicode.get()).booleanValue()) {
            bypass = RandomStringUtils.randomAlphabetic(((Integer) this.length.get()).intValue());
            if (!((Boolean) this.uppercase.get()).booleanValue())
              bypass = bypass.toLowerCase();
          } else {
            bypass = generateRandomUnicodeString(((Integer) this.length.get()).intValue(),
                ((Integer) this.minUnicodeRange.get()).intValue(), ((Integer) this.maxUnicodeRange.get()).intValue());
          }
          this.text = this.text + " " + bypass;
        }
      }
      if (((Boolean) this.autoSplitMessages.get()).booleanValue()
          && this.text.length() > ((Integer) this.splitLength.get()).intValue()) {
        double length = this.text.length();
        int splits = (int) Math.ceil(length / ((Integer) this.splitLength.get()).intValue());
        int start = this.splitNum * ((Integer) this.splitLength.get()).intValue();
        int end = Math.min(start + ((Integer) this.splitLength.get()).intValue(), this.text.length());
        ChatUtils.sendPlayerMsg(this.text.substring(start, end));
        this.splitNum = ++this.splitNum % splits;
        this.timer = ((Integer) this.autoSplitDelay.get()).intValue();
        if (this.splitNum == 0) {
          this.timer = ((Integer) this.delay.get()).intValue();
          this.text = null;
        }
      } else {
        if (this.text.length() > 256)
          this.text = this.text.substring(0, 256);
        ChatUtils.sendPlayerMsg(this.text);
        this.timer = ((Integer) this.delay.get()).intValue();
        this.text = null;
      }
    } else {
      this.timer--;
    }
  }
}
