package com.xenojava.modernfactions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Factions;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * ----- ModernFactions -----
 *
 * @author Xeno
 * @version 1.0
 */

public class ModernFactions extends JavaPlugin {

    public final static Logger logger = Logger
            .getLogger("Minecraft.ModernFactions");

    //Debugging code
    private final ArrayList<LocalPlayer> local_players = new ArrayList<LocalPlayer>();
    private final ArrayList<FactionBuild> faction_builds = new ArrayList<FactionBuild>();
    private final File schematicFolder = new File(getDataFolder(), "schematics");
    public static Inventory factions_menu;
    public static String PREFIX;
    private Economy econ;

    public void onEnable() {

        saveDefaultConfig();
        registerFactionBuilds();
        logger.setParent(Bukkit.getLogger());

        PREFIX = getConfig().getString("chat_prefix");

        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }

        if (setupEconomy()) {
            getLogger().info(
                    ChatColor.AQUA + "Found Vault! Hooking in for economy!");
        }

        this.getFactions().getOuterCmdFactions()
                .addSubCommand(new FactionBuildCommand(this));

    }

    private void registerFactionBuilds() {
        factions_menu = Bukkit.createInventory(null,
                getConfig().getInt("menu_slots"), ChatColor.BOLD
                        + "Choose a build!");

        for (String buildname : getConfig()
                .getConfigurationSection("faction_builds").getKeys(false)) {

            ArrayList<String> item_lore = new ArrayList<String>();

            FactionBuild build = new FactionBuild(buildname);
            registerBuild(build);

            build.setSchematicName(getConfig().getString(
                    "faction_builds." + buildname + ".schematic_name"));
            build.setCost(getConfig().getDouble(
                    "faction_builds." + buildname + ".cost"));
            @SuppressWarnings("deprecation")
            ItemStack item = new ItemStack(getConfig().getInt(
                    "faction_builds." + buildname + ".item_id"));

            if (getConfig().getStringList(
                    "faction_builds." + buildname + ".item_lore") != null) {

                for (String s : getConfig().getStringList(
                        "faction_builds." + buildname + ".item_lore")) {

                    item_lore.add(ChatColor
                            .translateAlternateColorCodes('&', s));

                }
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes(
                    '&',
                    getConfig().getString(
                            "faction_builds." + buildname + ".item_name")));
            meta.setLore(item_lore);
            item.setItemMeta(meta);

            build.setItem(item);
            factions_menu.addItem(item);

        }

    }

    private void registerBuild(FactionBuild build) {
        if (!faction_builds.contains(build)) {
            faction_builds.add(build);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public LocalPlayer getLocalPlayer(Player player) {
        for (LocalPlayer lp : local_players) {
            if (lp.getName().equalsIgnoreCase(player.getName())) {
                return lp;
            }
        }
        LocalPlayer lp = getWorldEdit().wrapPlayer(player);
        local_players.add(lp);
        return lp;
    }


    public Economy getEconomy() {
        return econ;
    }

    public File getSchematicFolder() {
        return schematicFolder;
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null;
        }

        return (WorldEditPlugin) plugin;
    }

    public Factions getFactions() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Factions");

        if (plugin == null || !(plugin instanceof Factions)) {
            return null;
        }

        return (Factions) plugin;
    }

    public ArrayList<FactionBuild> getFactionBuilds() {
        return faction_builds;
    }

}
