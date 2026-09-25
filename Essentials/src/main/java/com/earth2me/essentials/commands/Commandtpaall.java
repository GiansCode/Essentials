package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import net.ess3.api.events.TPARequestEvent;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class Commandtpaall extends EssentialsCommand {
    public Commandtpaall() {
        super("tpaall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            if (sender.isPlayer()) {
                tpaAll(sender, ess.getUser(sender.getPlayer()));
                return;
            }
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, sender, args, 0);
        tpaAll(sender, target);
    }

    private void tpaAll(final CommandSource sender, final User target) {
        sender.sendTl("teleportAAll");
        ess.runOnEntity(target.getBase(), () -> {
            final String targetWorld = target.getWorld().getName();
            final boolean checkWorldPerms = sender.getSender().equals(target.getBase()) && ess.getSettings().isWorldTeleportPermissions();
            final boolean allowedWorld = !checkWorldPerms || target.isAuthorized("essentials.worlds." + targetWorld);
            for (final User player : ess.getOnlineUsers()) {
                if (target == player) {
                    continue;
                }
                ess.runOnEntity(player.getBase(), () -> {
                    if (!player.isTeleportEnabled()) {
                        return;
                    }
                    if (checkWorldPerms && !allowedWorld && !player.getWorld().getName().equals(targetWorld)) {
                        return;
                    }

                    try {
                        final TPARequestEvent tpaEvent = new TPARequestEvent(sender, player, true);
                        ess.getServer().getPluginManager().callEvent(tpaEvent);
                        if (tpaEvent.isCancelled()) {
                            sender.sendTl("teleportRequestCancelled", player.getDisplayName());
                            return;
                        }
                        player.requestTeleport(target, true);
                        player.sendTl("teleportHereRequest", target.getDisplayName());
                        player.sendTl("typeTpaccept");
                        if (ess.getSettings().getTpaAcceptCancellation() != 0) {
                            player.sendTl("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation());
                        }
                    } catch (final Exception ex) {
                        ess.showError(sender, ex, getName());
                    }
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
