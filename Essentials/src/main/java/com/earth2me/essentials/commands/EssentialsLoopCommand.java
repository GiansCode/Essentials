package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public abstract class EssentialsLoopCommand extends EssentialsCommand {
    public EssentialsLoopCommand(final String command) {
        super(command);
    }

    protected void loopOfflinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final String[] commandArgs) throws TranslatableException, NotEnoughArgumentsException {
        loopOfflinePlayersConsumer(server, sender, multipleStringMatches, matchWildcards, searchTerm, user -> updatePlayer(server, sender, user, commandArgs));
    }

    protected void loopOfflinePlayersConsumer(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final UserConsumer userConsumer) throws TranslatableException, NotEnoughArgumentsException {
        if (searchTerm.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        if (sender.isPlayer() && (searchTerm.equals("@s") || searchTerm.equals("@p"))) {
            acceptUser(sender, (User) sender.getUser(), userConsumer);
            return;
        }

        final UUID uuid = StringUtil.toUUID(searchTerm);
        if (uuid != null) {
            final User matchedUser = ess.getUser(uuid);
            if (matchedUser == null) {
                throw new PlayerNotFoundException();
            }
            acceptUser(sender, matchedUser, userConsumer);
        } else if (matchWildcards && searchTerm.contentEquals("**")) {
            for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                final User user = ess.getUsers().loadUncachedUser(u);
                if (user != null) {
                    acceptUser(sender, user, userConsumer);
                }
            }
        } else if (matchWildcards && searchTerm.contentEquals("*")) {
            final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
            for (final User onlineUser : ess.getOnlineUsers()) {
                acceptOnline(sender, onlineUser, skipHidden, userConsumer);
            }
        } else if (multipleStringMatches) {
            if (searchTerm.trim().length() < 3) {
                throw new PlayerNotFoundException();
            }
            final List<Player> matchedPlayers = server.matchPlayer(searchTerm);
            if (matchedPlayers.isEmpty()) {
                final User matchedUser = getPlayer(server, searchTerm, true, true);
                acceptUser(sender, matchedUser, userConsumer);
            }
            for (final Player matchPlayer : matchedPlayers) {
                final User matchedUser = ess.getUser(matchPlayer);
                acceptUser(sender, matchedUser, userConsumer);
            }
        } else {
            final User user = getPlayer(server, searchTerm, true, true);
            acceptUser(sender, user, userConsumer);
        }
    }

    protected void loopOnlinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final String[] commandArgs) throws TranslatableException, NotEnoughArgumentsException {
        loopOnlinePlayersConsumer(server, sender, multipleStringMatches, matchWildcards, searchTerm, user -> updatePlayer(server, sender, user, commandArgs));
    }

    protected void loopOnlinePlayersConsumer(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final UserConsumer userConsumer) throws NotEnoughArgumentsException, TranslatableException {
        if (searchTerm.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        if (sender.isPlayer() && (searchTerm.equals("@s") || searchTerm.equals("@p"))) {
            acceptUser(sender, (User) sender.getUser(), userConsumer);
            return;
        }

        final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();

        if (matchWildcards && (searchTerm.contentEquals("**") || searchTerm.contentEquals("*"))) {
            for (final User onlineUser : ess.getOnlineUsers()) {
                acceptOnline(sender, onlineUser, skipHidden, userConsumer);
            }
        } else if (multipleStringMatches) {
            if (searchTerm.trim().length() < 2) {
                throw new PlayerNotFoundException();
            }
            boolean foundUser = false;
            final List<Player> matchedPlayers = server.matchPlayer(searchTerm);

            if (matchedPlayers.isEmpty()) {
                final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
                boolean scheduled = false;
                for (final User player : ess.getOnlineUsers()) {
                    final Player base = player.getBase();
                    if (base != null && base.isOnline() && !ess.isEntityThread(base)) {
                        scheduled = true;
                        ess.scheduleEntityDelayedTask(base, () -> {
                            try {
                                final String displayName = FormatUtil.stripFormat(player.getDisplayName()).toLowerCase(Locale.ENGLISH);
                                if (displayName.contains(matchText) && !shouldSkipHidden(sender, player, skipHidden)) {
                                    userConsumer.accept(player);
                                }
                            } catch (final Exception ex) {
                                showError(sender.getSender(), ex, getName());
                            }
                        });
                        continue;
                    }
                    final String displayName = FormatUtil.stripFormat(player.getDisplayName()).toLowerCase(Locale.ENGLISH);
                    if (displayName.contains(matchText) && acceptOnline(sender, player, skipHidden, userConsumer)) {
                        foundUser = true;
                    }
                }
                if (scheduled) {
                    foundUser = true;
                }
            } else {
                for (final Player matchPlayer : matchedPlayers) {
                    final User player = ess.getUser(matchPlayer);
                    if (acceptOnline(sender, player, skipHidden, userConsumer)) {
                        foundUser = true;
                    }
                }
            }
            if (!foundUser) {
                throw new PlayerNotFoundException();
            }
        } else {
            final User player = getPlayer(server, sender, searchTerm);
            acceptUser(sender, player, userConsumer);
        }
    }

    private void acceptUser(final CommandSource sender, final User user, final UserConsumer userConsumer) throws NotEnoughArgumentsException, TranslatableException {
        acceptOnline(sender, user, false, userConsumer);
    }

    private boolean acceptOnline(final CommandSource sender, final User user, final boolean skipHidden, final UserConsumer userConsumer) throws NotEnoughArgumentsException, TranslatableException {
        final Player base = user.getBase();
        if (base != null && base.isOnline() && !ess.isEntityThread(base)) {
            ess.scheduleEntityDelayedTask(base, () -> {
                try {
                    if (shouldSkipHidden(sender, user, skipHidden)) {
                        return;
                    }
                    userConsumer.accept(user);
                } catch (final Exception ex) {
                    showError(sender.getSender(), ex, getName());
                }
            });
            return true;
        }
        if (shouldSkipHidden(sender, user, skipHidden)) {
            return false;
        }
        userConsumer.accept(user);
        return true;
    }

    private boolean shouldSkipHidden(final CommandSource sender, final User user, final boolean skipHidden) {
        return skipHidden && sender.isPlayer() && user.isHidden(sender.getPlayer()) && user.isHiddenFrom(sender.getPlayer());
    }

    protected abstract void updatePlayer(Server server, CommandSource sender, User user, String[] args) throws NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException;

    @Override
    protected List<String> getPlayers(final CommandSource interactor) {
        final List<String> players = super.getPlayers(interactor);
        players.add("**");
        players.add("*");
        return players;
    }

    @Override
    protected List<String> getPlayers(final User interactor) {
        final List<String> players = super.getPlayers(interactor);
        players.add("**");
        players.add("*");
        return players;
    }

    public interface UserConsumer {
        void accept(User user) throws NotEnoughArgumentsException, TranslatableException;
    }
}
