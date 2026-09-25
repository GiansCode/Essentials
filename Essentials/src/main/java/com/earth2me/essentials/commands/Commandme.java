package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.TranslatableException;
import net.essentialsx.api.v2.events.UserActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Commandme extends EssentialsCommand {
    public Commandme() {
        super("me");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.isMuted()) {
            final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
            if (dateDiff == null) {
                throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReason" : "voiceSilenced", user.getMuteReason());
            }
            throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReasonTime" : "voiceSilencedTime", dateDiff, user.getMuteReason());
        }

        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.formatMessage(user, "essentials.chat", message);

        user.setDisplayNick();
        final long chatRadius = ess.getSettings().getChatRadius();
        if (chatRadius < 1) {
            ess.broadcastTl("action", user.getDisplayName(), message);
            ess.getServer().getPluginManager().callEvent(new UserActionEvent(user, message, Collections.unmodifiableCollection(ess.getServer().getOnlinePlayers())));
            return;
        }
        final long squaredRadius = chatRadius * chatRadius;

        final String actionMessage = message;
        final World world = user.getWorld();
        final Location loc = user.getLocation();

        if (ess.getOnlinePlayers().size() < 2) {
            user.sendTl("localNoOne");
        }

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(user.getBase())) {
                user.sendTl("action", user.getDisplayName(), actionMessage);
                continue;
            }
            ess.runOnEntity(player, () -> {
                final User onlineUser = ess.getUser(player);
                final Location playerLoc = onlineUser.getLocation();
                final boolean differentWorld = playerLoc.getWorld() != world;
                final boolean ignored = onlineUser.isIgnoredPlayer(user);
                final boolean outOfRange = !differentWorld && playerLoc.distanceSquared(loc) > squaredRadius;
                if (differentWorld || ignored || outOfRange) {
                    if (onlineUser.isAuthorized("essentials.chat.spy")) {
                        onlineUser.sendTl("action", user.getDisplayName(), actionMessage);
                    }
                    return;
                }
                onlineUser.sendTl("action", user.getDisplayName(), actionMessage);
            });
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.replaceFormat(message);

        ess.broadcastTl("action", "@", message);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
