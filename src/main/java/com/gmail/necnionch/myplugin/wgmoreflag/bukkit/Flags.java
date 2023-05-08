package com.gmail.necnionch.myplugin.wgmoreflag.bukkit;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Flags {
    EXIT_VIA_TP_MYSELF(
            new StateFlag("exit-via-teleport-myself", true),
            new Permission("worldguardmoreflag.bypass.exit-via-teleport-myself", PermissionDefault.OP)
    ),
    CRAFTING(
            new StateFlag("crafting", true),
            new Permission("worldguardmoreflag.bypass.crafting", PermissionDefault.OP)
    );

    public final StateFlag FLAG;
    private Permission bypassPermission;

    Flags(StateFlag flag, Permission bypassPermission) {
        this.FLAG = flag;
        this.bypassPermission = bypassPermission;
    }

    public Permission getBypassPermission() {
        return bypassPermission;
    }

    public boolean checkBypass(Permissible owner) {
        return owner.hasPermission(bypassPermission);
    }

    static public void register(WGMoreFlag pl) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        for (Flags flag : Flags.values()) {
            try {
                registry.register(flag.FLAG);
            } catch (FlagConflictException e) {
                String m = e.getMessage();
                pl.getLogger().severe("Unable to register '" + flag.FLAG.getName() + "' flag:" + ((m != null) ? m : "null"));
            }
        }


    }

}
