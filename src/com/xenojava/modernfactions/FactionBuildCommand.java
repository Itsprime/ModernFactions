package com.xenojava.modernfactions;

import java.io.File;
import java.util.Iterator;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class FactionBuildCommand extends FCommand implements Listener {

	private final ModernFactions plugin;

	public FactionBuildCommand(ModernFactions plugin) {
		this.plugin = plugin;

		// Register Events
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		// Aliases
		this.addAliases("build", "b");
		this.addOptionalArg("undo", "Add this argument to undo a build!");
		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.LEADER));
	}

	// Must Override

	@Override
	public void perform() {

		Player p = (Player) sender;

        if(!p.hasPermission("modernfactions.access")){
            return;
        }

		if (args.size() != 0) {
			if (arg(0).equalsIgnoreCase("undo")) {

				LocalPlayer lp = plugin.getLocalPlayer(p);
				LocalSession s = plugin.getWorldEdit().getWorldEdit()
						.getSession(lp);

				EditSession undone = s.undo(s.getBlockBag(lp), lp);

				if (undone != null) {
					Chat.messagePlayer(p, "&aFaction build has been undone!");

				} else {
					Chat.messagePlayer(p, "&cNo builds left to undo!");
				}

				return;
			}
		}

		if (!isTerritory(p)) {
			Chat.messagePlayer(p, "&cYou must be in your territory to build");
			return;
		}

		Chat.messagePlayer(p, "&cWritten by &b&nXenoJava.com&r");
		p.openInventory(ModernFactions.factions_menu);

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemClick(InventoryClickEvent e) throws Exception {

		Player player = (Player) e.getWhoClicked();

		if (!e.getInventory().getTitle()
				.equalsIgnoreCase(ModernFactions.factions_menu.getTitle())) {
			return;
		}

		if (e.getCurrentItem() == null) {
			return;
		}

        for (FactionBuild build : plugin.getFactionBuilds()) {
            if (build.getItem().getType() == e.getCurrentItem().getType()) {

                player.closeInventory();

                if (!this.withdrawBalance(player.getName(), build.getCost())) {

                    Chat.messagePlayer(player,
                            "&cYou do not have enough money to build.");
                    return;

                }

                LocalPlayer localPlayer = plugin.getLocalPlayer(player);
                LocalSession localSession = plugin.getWorldEdit()
                        .getWorldEdit().getSession(localPlayer);

                EditSession editSession = localSession
                        .createEditSession(localPlayer);

                localSession.tellVersion(localPlayer);

                File schematicFile = new File(plugin.getSchematicFolder(),
                        build.getSchematicName() + ".schematic");

                SchematicFormat schematic = SchematicFormat
                        .getFormat(schematicFile);
                CuboidClipboard clipboard = schematic.load(schematicFile);

                ModernFactions.logger.info(localPlayer.getName() + " loaded "
                        + schematicFile.getCanonicalPath());

                clipboard.paste(editSession,
                        BukkitUtil.toVector(player.getLocation()), true);
                localPlayer.findFreePosition();

                Chat.messagePlayer(player,
                        "&aYour choice of build has been pasted relative to your location!");

            }
        }

		e.setCancelled(true);
		player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

	}

	public boolean withdrawBalance(String name, double amount) {
		if (plugin.getEconomy().getBalance(name) >= amount) {
			plugin.getEconomy().withdrawPlayer(name, amount);
			return true;
		} else {
			return false;
		}
	}

	private boolean isTerritory(Player p) {
		UPlayer me = UPlayer.get(p.getName());
		Faction bfaction = BoardColls.get().getFactionAt(
				PS.valueOf(p.getLocation()));
		if (bfaction.getComparisonName().equalsIgnoreCase(
				me.getFaction().getComparisonName())) {

			return false;
		}

		return true;
	}

}
