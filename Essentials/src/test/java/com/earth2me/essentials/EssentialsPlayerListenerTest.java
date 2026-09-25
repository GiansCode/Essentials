package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.provider.KnownCommandsProvider;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EssentialsPlayerListenerTest {
    private IEssentials ess;
    private ISettings settings;
    private User user;
    private Player player;
    private CommandFilters commandFilters;
    private CommandFilter cooldownFilter;
    private EssentialsPlayerListener listener;

    @BeforeEach
    public void setUp() {
        player = MockBukkit.mock().addPlayer();
        ess = mock(IEssentials.class);
        settings = mock(ISettings.class);
        user = mock(User.class);
        final Server commandServer = mock(Server.class);
        final PluginCommand pluginCommand = mock(PluginCommand.class);
        final KnownCommandsProvider knownCommandsProvider = mock(KnownCommandsProvider.class);
        commandFilters = mock(CommandFilters.class);
        cooldownFilter = mock(CommandFilter.class);

        when(ess.getServer()).thenReturn(commandServer);
        when(ess.getSettings()).thenReturn(settings);
        when(ess.getUser(player)).thenReturn(user);
        when(ess.getCommandFilters()).thenReturn(commandFilters);
        when(commandFilters.getCommandCooldown(user, "feed", CommandFilter.Type.REGEX)).thenReturn(cooldownFilter);
        when(ess.provider(KnownCommandsProvider.class)).thenReturn(knownCommandsProvider);
        when(knownCommandsProvider.getKnownCommands()).thenReturn(Collections.singletonMap("efeed", pluginCommand));
        when(commandServer.getPluginCommand("efeed")).thenReturn(pluginCommand);
        when(pluginCommand.getName()).thenReturn("feed");
        when(settings.getSocialSpyCommands()).thenReturn(Collections.emptySet());
        when(settings.getMuteCommands()).thenReturn(Collections.emptySet());
        when(settings.isCommandCooldownsEnabled()).thenReturn(true);
        when(user.getCommandCooldowns()).thenReturn(Collections.emptyMap());
        listener = new EssentialsPlayerListener(ess);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testUnregisteredCaseVariantDoesNotStartCooldown() {
        listener.onPlayerCommandPreprocess(new PlayerCommandPreprocessEvent(player, "/EFEED"));

        verify(commandFilters, never()).getCommandCooldown(any(), anyString(), any());
        verify(cooldownFilter, never()).applyCooldownTo(any());
    }

    @Test
    public void testRegisteredAliasStartsCanonicalCommandCooldown() {
        listener.onPlayerCommandPreprocess(new PlayerCommandPreprocessEvent(player, "/efeed"));

        verify(commandFilters).getCommandCooldown(user, "feed", CommandFilter.Type.REGEX);
        verify(cooldownFilter).applyCooldownTo(user);
    }
}
