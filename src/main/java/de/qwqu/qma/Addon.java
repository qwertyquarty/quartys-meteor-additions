package de.qwqu.qma;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.qwqu.qma.commands.CeilCommand;
import de.qwqu.qma.commands.ElytraCommand;
import de.qwqu.qma.commands.EntityTPCommand;
import de.qwqu.qma.commands.LoopTPCommand;
import de.qwqu.qma.commands.OrbitCommand;
import de.qwqu.qma.commands.StickTargetCommand;
import de.qwqu.qma.commands.VFillCommand;
import de.qwqu.qma.commands.VGetCommand;
import de.qwqu.qma.commands.ZeroCommand;
import de.qwqu.qma.hud.StickTarget;
import de.qwqu.qma.modules.Elytra;
import de.qwqu.qma.modules.LoopTP;
import de.qwqu.qma.modules.ModuleExample;
import de.qwqu.qma.modules.NoCollision;
import de.qwqu.qma.modules.NoPitchLimit;
import de.qwqu.qma.modules.Orbit;
import de.qwqu.qma.modules.Slotter;
import de.qwqu.qma.modules.Stick;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
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
