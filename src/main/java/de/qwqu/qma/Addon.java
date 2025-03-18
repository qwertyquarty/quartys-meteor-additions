package de.qwqu.qma;

import com.mojang.logging.LogUtils;
import de.qwqu.qma.commands.*;
import de.qwqu.qma.hud.StickTarget;
import de.qwqu.qma.modules.*;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import net.minecraft.entity.Entity;

public class Addon extends MeteorAddon {
  public static final Logger LOG = LogUtils.getLogger();
  public static final Category CATEGORY = new Category("QMA");
  public static final HudGroup HUD_GROUP = new HudGroup("QMA");

  public static String stick_targetName = "";
  public static Entity stick_targetEntity = null;

  public static Double ltp_x = 0.0;
  public static Double ltp_y = 0.0;
  public static Double ltp_z = 0.0;

  public static Double orbit_x = 0.0;
  public static Double orbit_y = 0.0;
  public static Double orbit_z = 0.0;

  @Override
  public void onInitialize() {
    LOG.info("Initializing Meteor Addon Template");

    // Modules
    Modules.get().add(new ModuleExample());
    Modules.get().add(new Stick());
    Modules.get().add(new Slotter());
    Modules.get().add(new LoopTP());
    Modules.get().add(new Orbit());
    Modules.get().add(new NoCollision());
    Modules.get().add(new Elytra());
    Modules.get().add(new NoPitchLimit());
    Modules.get().add(new SpamPlus());
    Modules.get().add(new MileyCyrus());
    Modules.get().add(new InteractAura());
    Modules.get().add(new Pitcher());
    Modules.get().add(new FidgetSpinner());
    Modules.get().add(new Sticker());
    Modules.get().add(new BreakRenderer());
    Modules.get().add(new TeleportCrash());

    // Commands
    Commands.add(new EntityTPCommand());
    Commands.add(new StickTargetCommand());
    Commands.add(new LoopTPCommand());
    Commands.add(new ZeroCommand());
    Commands.add(new OrbitCommand());
    Commands.add(new CeilCommand());
    Commands.add(new VGetCommand());
    Commands.add(new ElytraCommand());
    Commands.add(new VFillCommand());
    Commands.add(new WorldBorderCommand());

    // Hud
    Hud.get().register(StickTarget.INFO);
  }

  @Override
  public void onRegisterCategories() {
    Modules.registerCategory(CATEGORY);
  }

  @Override
  public String getPackage() {
    return "de.qwqu.qma";
  }

  @Override
  public GithubRepo getRepo() {
    return new GithubRepo("qwertyquarty", "quartys-meteor-addditions");
  }
}
