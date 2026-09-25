package com.earth2me.essentials;

import com.earth2me.essentials.commands.Commandhome;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HomeTest {
    private Settings settings;
    private Commandhome commandhome;
    private net.ess3.api.IEssentials essentials;

    @BeforeEach
    public void before() throws Exception {
        settings = mock(Settings.class);
        commandhome = mock(Commandhome.class);
        essentials = mock(net.ess3.api.IEssentials.class);

        when(settings.getWorldGroupHomeLimit("test-wg")).thenReturn(7);
        when(settings.getWorldHomeLimit("world_the_end")).thenReturn(6);
        when(settings.getWorldGroupHomeList("test-wg")).thenReturn(new HashSet<>(Arrays.asList("world", "world_nether")));
        when(settings.getHomeLimit(any(User.class))).thenCallRealMethod();
        when(settings.getHomeLimit("default")).thenReturn(3);
        when(settings.getHomeLimit("vip")).thenReturn(5);
        when(settings.getMultipleHomes()).thenReturn(new HashSet<>(Collections.singletonList("vip")));
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(false);
        when(settings.getHomesPerWorldGroup()).thenReturn(new HashSet<>(Collections.singletonList("test-wg")));
        when(settings.getHomesPerWorld()).thenReturn(new HashSet<>(Collections.singletonList("world_the_end")));
        when(settings.isUserInWorld(any(User.class), any(String.class))).thenCallRealMethod();
        when(settings.isUserInWorldGroup(any(User.class), any(String.class))).thenCallRealMethod();

        when(essentials.getSettings()).thenReturn(settings);
        when(commandhome.isUserHomeInWorldOrWorldGroupWorld(any(String.class), any(String.class))).thenCallRealMethod();
        setCommandEssentials(commandhome, essentials);
    }

    @Test
    public void testHomeLimitWithoutMultiplePerm() {
        final User user = mock(User.class);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(false);

        assertEquals(1, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithoutWorldHomeLimit() {
        final User user = mock(User.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(false);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);

        assertEquals(3, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithoutWorldHomeLimitVip() {
        final User user = mock(User.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(false);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(true);

        assertEquals(5, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldHomeLimit() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world_the_end");

        assertEquals(6, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldHomeLimitDefault() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertEquals(3, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldGroupHomeLimit() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertEquals(7, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldGroupHomeLimitOutsideDefault() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world_another");

        assertEquals(3, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeInWG() throws Exception {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        assertTrue(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world_nether"));
    }

    @Test
    public void testHomeNotInWG() throws Exception {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        assertFalse(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world_the_end"));
    }

    @Test
    public void testHomeInWorld() throws Exception {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(false);

        assertTrue(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world"));
    }

    @Test
    public void testHomeNotInWorld() throws Exception {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(false);

        assertFalse(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world_nether"));
    }

    private static void setCommandEssentials(final Commandhome command, final net.ess3.api.IEssentials essentials) throws Exception {
        final Field field = EssentialsCommand.class.getDeclaredField("ess");
        field.setAccessible(true);
        field.set(command, essentials);
    }
}
