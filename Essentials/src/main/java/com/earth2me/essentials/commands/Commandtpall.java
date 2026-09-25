package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;

public class Commandtpall extends EssentialsCommand {
    public Commandtpall() {
        super("tpall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            if (sender.isPlayer()) {
                teleportAllPlayers(server, sender, ess.getUser(sender.getPlayer()), commandLabel);
                return;
            }
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, sender, args, 0);
        teleportAllPlayers(server, sender, target, commandLabel);
    }

    private void teleportAllPlayers(final Server server, final CommandSource sender, final User target, final String label) {
        sender.sendTl("teleportAll");
        ess.runOnEntity(target.getBase(), () -> {
            final Location loc = target.getLocation().clone();
            final String targetWorld = target.getWorld().getName();
            final boolean checkWorldPerms = sender.getSender().equals(target.getBase()) && ess.getSettings().isWorldTeleportPermissions();
            final boolean allowedWorld = !checkWorldPerms || target.isAuthorized("essentials.worlds." + targetWorld);
            for (final User player : ess.getOnlineUsers()) {
                if (target == player) {
                    continue;
                }
                ess.runOnEntity(player.getBase(), () -> {
                    if (checkWorldPerms && !allowedWorld && !player.getWorld().getName().equals(targetWorld)) {
                        return;
                    }
                    player.getAsyncTeleport().now(loc, false, TeleportCause.COMMAND, getNewExceptionFuture(sender, label));
                });
            }
        });
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(sender);
        } else {
            return Collections.emptyList();
        }
    }
}
