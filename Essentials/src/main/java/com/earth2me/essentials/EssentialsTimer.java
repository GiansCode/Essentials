package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class EssentialsTimer implements Runnable {
    private final transient IEssentials ess;
    private final transient Set<UUID> onlineUsers = new HashSet<>(); // Field is necessary for hidden users
    private final transient LinkedList<Double> history = new LinkedList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private final long tickInterval = 50;
    private transient long lastPoll = System.nanoTime();

    EssentialsTimer(final IEssentials ess) {
        this.ess = ess;
        history.add(20d);
    }

    @Override
    public void run() {
        final long startTime = System.nanoTime();
        final long currentTime = System.currentTimeMillis();
        recordTick(startTime);

        final Set<UUID> currentlyOnline = new HashSet<>();
        for (final Player player : ess.getOnlinePlayers()) {
            currentlyOnline.add(player.getUniqueId());
            ess.scheduleEntityDelayedTask(player, () -> {
                try {
                    final User user = ess.getUser(player);
                    user.setLastOnlineActivity(currentTime);
                    user.checkActivity();
                    user.checkMuteTimeout(currentTime);
                    user.checkJailTimeout(currentTime);
                    user.resetInvulnerabilityAfterTeleport();
                } catch (final Exception e) {
                    ess.getLogger().log(Level.WARNING, "EssentialsTimer Error:", e);
                }
            });
        }

        onlineUsers.removeIf(uuid -> {
            if (currentlyOnline.contains(uuid)) {
                return false;
            }
            final User user = ess.getUser(uuid);
            if (user != null && user.getLastOnlineActivity() > user.getLastLogout() && !user.isHidden()) {
                user.setLastLogout(user.getLastOnlineActivity());
            }
            return true;
        });
        onlineUsers.addAll(currentlyOnline);
    }

    /**
     * Records how long actually elapsed since the previous run() firing, relative to this
     * task's nominal {@link #tickInterval}-tick period, as one more sample in the rolling TPS
     * history. Synchronized because getAverageTPS() below can now be called from another
     * thread (a player's /tps, or chat placeholder resolution) while this method is still only
     * ever invoked from Folia's global region thread via the repeating task this class is
     * scheduled as.
     */
    private synchronized void recordTick(final long startTime) {
        long timeSpent = (startTime - lastPoll) / 1000;
        if (timeSpent == 0) {
            timeSpent = 1;
        }
        if (history.size() > 10) {
            history.remove();
        }
        final double tps = tickInterval * 1000000.0 / timeSpent;
        if (tps <= 21) {
            history.add(tps);
        }
        lastPoll = startTime;
    }

    /**
     * Approximates the global region's own TPS, based on how far this task's actual firing
     * interval has drifted from its nominal {@link #tickInterval}-tick period. This reflects the
     * health of the global region only - a per-player region could be lagging independently
     * while this stays at or near 20.
     */
    public synchronized double getAverageTPS() {
        double avg = 0;
        for (final Double f : history) {
            if (f != null) {
                avg += f;
            }
        }
        return avg / history.size();
    }
}
