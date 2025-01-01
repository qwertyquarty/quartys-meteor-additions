package de.qwqu.qma;

import com.mojang.logging.LogUtils;

import de.qwqu.qma.commands.*;
import de.qwqu.qma.modules.*;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.Entity;

import org.slf4j.Logger;

public class Addon extends MeteorAddon {
  public static final Logger LOG = LogUtils.getLogger();
  public static final Category CATEGORY = new Category("QMA");

  public static String stick_targetName = "";
  public static Entity stick_targetEntity = null;

  @Override
  public void onInitialize() {
    LOG.info("Initializing Meteor Addon Template");

    // Modules
    Modules.get().add(new ModuleExample());
    Modules.get().add(new Stick());

    // Commands
    Commands.add(new EntityTPCommand());
    Commands.add(new StickTargetCommand());
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
