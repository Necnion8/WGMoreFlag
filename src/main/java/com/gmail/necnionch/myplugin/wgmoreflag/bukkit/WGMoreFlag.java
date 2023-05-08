package com.gmail.necnionch.myplugin.wgmoreflag.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;


public final class WGMoreFlag extends JavaPlugin implements Listener {

    @Override
    public void onLoad() {
        Flags.register(this);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public ApplicableRegionSet getRegionSet(Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
    }


    @EventHandler(ignoreCancelled = true)
    public void onExitViaTeleportMyself(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
//        getLogger().info("test1");
        if (Flags.EXIT_VIA_TP_MYSELF.checkBypass(player)) return;

//        getLogger().info("test2");
        switch (event.getCause()) {
            case SPECTATE:
            case ENDER_PEARL:
            case CHORUS_FRUIT:
                break;
            default:
                return;
        }

        BukkitPlayer bPlayer = new BukkitPlayer(WorldGuardPlugin.inst(), player);

        Location tpFrom = event.getFrom();
        Location tpTo = event.getTo();

        ApplicableRegionSet fromLocSet = getRegionSet(tpFrom);
//        getLogger().info("test3");
        if (fromLocSet.testState(bPlayer, Flags.EXIT_VIA_TP_MYSELF.FLAG)) return;

        Set<ProtectedRegion> deniedRegions = new HashSet<>();

        for (ProtectedRegion region : fromLocSet.getRegions()) {
            if (StateFlag.State.DENY.equals(region.getFlag(Flags.EXIT_VIA_TP_MYSELF.FLAG)))
                deniedRegions.add(region);
        }

        ApplicableRegionSet toLocSet = getRegionSet(tpTo);
        for (ProtectedRegion region : toLocSet.getRegions()) {
            if (StateFlag.State.DENY.equals(region.getFlag(Flags.EXIT_VIA_TP_MYSELF.FLAG)))
                deniedRegions.remove(region);
        }

//        getLogger().info("exit!");
        if (!deniedRegions.isEmpty()) {
            event.setCancelled(true);
//            getLogger().info("cancelled!");
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onCrafting(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (Flags.CRAFTING.checkBypass(player)) return;

        BukkitPlayer bPlayer = new BukkitPlayer(WorldGuardPlugin.inst(), player);
        Location loc = player.getLocation();

        ApplicableRegionSet fromLocSet = getRegionSet(loc);
        if (fromLocSet.testState(bPlayer, Flags.CRAFTING.FLAG)) return;

        event.setCancelled(true);

    }



}
