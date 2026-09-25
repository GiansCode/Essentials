package com.earth2me.essentials;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Shows and hides players the way VanishNoPacket does.
 * Calling show straight after hide does not make the server send a new spawn packet, so the
 * player stays invisible on the client. Hide first, then show a few ticks later.
 */
final class VanishVisibility {
    private static final long RESHOW_DELAY_TICKS = 8L;
    private static final Method HIDE = find("hidePlayer");
    private static final Method SHOW = find("showPlayer");

    private final Essentials ess;
    private boolean showImmediately;

    VanishVisibility(final Essentials ess) {
        this.ess = ess;
    }

    void showImmediately() {
        showImmediately = true;
    }

    void update(final Player subject, final boolean vanished) {
        for (final Player viewer : ess.getOnlinePlayers()) {
            if (viewer.equals(subject)) {
                continue;
            }
            final User viewerUser = ess.getUser(viewer);
            if (vanished && !viewerUser.isAuthorized("essentials.vanish.see")) {
                hide(viewer, subject);
            } else {
                reveal(viewer, subject);
            }
        }
    }

    void concealFrom(final Player viewer) {
        if (ess.getUser(viewer).isAuthorized("essentials.vanish.see")) {
            return;
        }
        for (final String name : ess.getVanishedPlayersNew()) {
            final Player vanished = ess.getServer().getPlayerExact(name);
            if (vanished != null && vanished.isOnline() && !vanished.equals(viewer)) {
                hide(viewer, vanished);
            }
        }
    }

    private void hide(final Player viewer, final Player subject) {
        onViewer(viewer, () -> setHidden(viewer, subject, true));
    }

    private void reveal(final Player viewer, final Player subject) {
        if (showImmediately) {
            if (viewer.isOnline() && subject.isOnline()) {
                setHidden(viewer, subject, false);
            }
            return;
        }
        onViewer(viewer, () -> {
            if (viewer.canSee(subject)) {
                setHidden(viewer, subject, true);
            }
            final Runnable show = () -> {
                if (viewer.isOnline() && subject.isOnline()) {
                    setHidden(viewer, subject, false);
                }
            };
            ess.scheduleEntityDelayedTask(viewer, show, RESHOW_DELAY_TICKS);
        });
    }

    private void onViewer(final Player viewer, final Runnable action) {
        if (!viewer.isOnline()) {
            return;
        }
        if (ess.isEntityThread(viewer)) {
            action.run();
        } else {
            ess.scheduleEntityDelayedTask(viewer, action);
        }
    }

    private void setHidden(final Player viewer, final Player subject, final boolean hidden) {
        final Method method = hidden ? HIDE : SHOW;
        if (method != null) {
            try {
                method.invoke(viewer, ess, subject);
                return;
            } catch (final ReflectiveOperationException ignored) {
                // Fall through to the 1.8 methods.
            }
        }
        if (hidden) {
            //noinspection deprecation
            viewer.hidePlayer(subject);
        } else {
            //noinspection deprecation
            viewer.showPlayer(subject);
        }
    }

    private static Method find(final String name) {
        try {
            return Player.class.getMethod(name, Plugin.class, Player.class);
        } catch (final NoSuchMethodException ignored) {
            return null;
        }
    }
}
