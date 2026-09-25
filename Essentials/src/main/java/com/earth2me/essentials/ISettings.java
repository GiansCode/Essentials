package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.utils.NumberUtil;
import net.essentialsx.api.v2.ChatType;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface ISettings extends IConf {
    String DEBUG_FLAG_NAMESPACE = "net.essentialsx";

    File getConfigFile();

    boolean areSignsDisabled();

    IText getAnnounceNewPlayerFormat();

    boolean getAnnounceNewPlayers();

    String getNewPlayerKit();

    String getBackupCommand();

    long getBackupInterval();

    boolean isAlwaysRunBackup();

    String getChatFormat(String group);

    String getChatFormat(String group, ChatType chatType);

    String getWorldAlias(String world);

    int getChatRadius();

    int getNearRadius();

    char getChatShout();

    char getChatQuestion();

    @Deprecated
    Map<String, BigDecimal> getCommandCosts();

    @Deprecated
    boolean isShoutDefault();

    boolean isPersistShout();

    boolean isChatQuestionEnabled();

    boolean isUsePaperChatEvent();

    BigDecimal getCommandCost(IEssentialsCommand cmd);

    @Deprecated
    BigDecimal getCommandCost(String label);

    String getCurrencySymbol();

    boolean isCurrencySymbolSuffixed();

    int getOversizedStackSize();

    int getDefaultStackSize();

    double getHealCooldown();

    Set<String> getSocialSpyCommands();

    boolean getSocialSpyListenMutedPlayers();

    boolean isSocialSpyMessages();

    boolean isSocialSpyDisplayNames();

    Set<String> getMuteCommands();

    @Deprecated
    CommentedConfigurationNode getKitSection();

    boolean isSkippingUsedOneTimeKitsFromKitList();

    String getLocale();

    boolean isPerPlayerLocale();

    String getNewbieSpawn();

    String getNicknamePrefix();

    boolean isResetNickOnNameChange();

    String getOperatorColor() throws Exception;

    boolean getPerWarpPermission();

    boolean getProtectBoolean(final String configName, boolean def);

    int getProtectCreeperMaxHeight();

    List<Material> getProtectList(final String configName);

    List<String> getProtectListRaw(final String configName);

    boolean getProtectPreventSpawn(final String creatureName);

    String getProtectString(final String configName);

    boolean getRespawnAtHome();

    String getRandomSpawnLocation();

    String getRandomRespawnLocation();

    boolean isRespawnAtAnchor();

    Set getMultipleHomes();

    Set<String> getHomesPerWorld();

    Set<String> getHomesPerWorldGroup();

    int getHomeLimit(String set);

    int getWorldHomeLimit(String set);

    int getWorldGroupHomeLimit(String set);

    Set<String> getWorldGroupHomeList(String set);

    boolean isHomeLimitPerWorldEnabled();

    boolean isHomeLimitPerWorldGroupEnabled();

    int getHomeLimit(User user);

    int getSpawnMobLimit();

    BigDecimal getStartingBalance();

    boolean isTeleportSafetyEnabled();

    boolean isForceDisableTeleportSafety();

    boolean isAlwaysTeleportSafety();

    boolean isConsiderWorldHeightForTeleportSafety();

    boolean isTeleportPassengerDismount();

    boolean isForcePassengerTeleport();

    double getTeleportCooldown();

    double getTeleportDelay();

    boolean hidePermissionlessHelp();

    boolean isCommandDisabled(final IEssentialsCommand cmd);

    boolean isCommandDisabled(String label);

    Set<String> getDisabledCommands();

    boolean isVerboseCommandUsages();

    boolean isCommandOverridden(String name);

    boolean isDebug();

    boolean isDebug(DebugFlag flag);

    Long getDebugLong(DebugFlag flag);

    void setDebug(boolean debug);

    boolean isEcoDisabled();

    @Deprecated
    boolean isTradeInStacks(int id);

    boolean isTradeInStacks(Material type);

    List<Material> itemSpawnBlacklist();

    List<EssentialsSign> enabledSigns();

    boolean permissionBasedItemSpawn();

    boolean showNonEssCommandsInHelp();

    boolean warnOnBuildDisallow();

    boolean warnOnSmite();

    BigDecimal getMaxMoney();

    BigDecimal getMinMoney();

    boolean isEcoLogEnabled();

    boolean isEcoLogUUIDEnabled();

    boolean isEcoLogUpdateEnabled();

    boolean realNamesOnList();

    boolean removeGodOnDisconnect();

    boolean changeDisplayName();

    boolean changePlayerListName();

    boolean changeTabCompleteName();

    boolean isPlayerCommand(String string);

    boolean useBukkitPermissions();

    boolean addPrefixSuffix();

    boolean disablePrefix();

    boolean disableSuffix();

    long getAutoAfk();

    long getAutoAfkTimeout();

    List<String> getAfkTimeoutCommands();

    boolean getFreezeAfkPlayers();

    boolean cancelAfkOnMove();

    boolean cancelAfkOnInteract();

    boolean cancelAfkOnChat();

    boolean cancelAfkOnFish();

    boolean sleepIgnoresAfkPlayers();

    boolean sleepIgnoresVanishedPlayers();

    boolean isVanishFakeJoinLeave();

    boolean isAfkListName();

    String getAfkListName();

    boolean broadcastAfkMessage();

    boolean areDeathMessagesEnabled();

    KeepInvPolicy getVanishingItemsPolicy();

    KeepInvPolicy getBindingItemsPolicy();

    int getJoinQuitMessagePlayerCount();

    boolean hasJoinQuitMessagePlayerCount();

    Set<String> getNoGodWorlds();

    boolean getUpdateBedAtDaytime();

    boolean allowUnsafeEnchantments();

    boolean getRepairEnchanted();

    boolean isWorldTeleportPermissions();

    boolean isWorldHomePermissions();

    int getMaxTreeCommandRange();

    boolean registerBackInListener();

    boolean getDisableItemPickupWhileAfk();

    EventPriority getRespawnPriority();

    EventPriority getSpawnJoinPriority();

    long getTpaAcceptCancellation();

    int getTpaMaxRequests();

    long getTeleportInvulnerability();

    boolean isTeleportInvulnerability();

    long getLoginAttackDelay();

    int getSignUsePerSecond();

    double getMaxFlySpeed();

    double getMaxWalkSpeed();

    int getMailsPerMinute();

    long getEconomyLagWarning();

    long getPermissionsLagWarning();

    void setEssentialsChatActive(boolean b);

    long getMaxMute();

    long getMaxTempban();

    Map<String, Object> getListGroupConfig();

    int getMaxNickLength();

    boolean ignoreColorsInMaxLength();

    boolean hideDisplayNameInVanish();

    int getMaxUserCacheCount();

    long getMaxUserCacheValueExpiry();

    boolean allowSilentJoinQuit();

    boolean isCustomJoinMessage();

    String getCustomJoinMessage();

    boolean isCustomQuitMessage();

    String getCustomQuitMessage();

    String getCustomNewUsernameMessage();

    boolean isCustomNewUsernameMessage();

    boolean isCustomServerFullMessage();

    boolean isCustomWhitelistMessage();

    boolean isNotifyNoNewMail();

    boolean isDropItemsIfFull();

    boolean isLastMessageReplyRecipient();

    boolean isReplyToVanished();

    BigDecimal getMinimumPayAmount();

    boolean isPayExcludesIgnoreList();

    long getLastMessageReplyRecipientTimeout();

    boolean isMilkBucketEasterEggEnabled();

    boolean isSendFlyEnableOnJoin();

    boolean isWorldTimePermissions();

    boolean isSpawnOnJoin();

    List<String> getSpawnOnJoinGroups();

    boolean isUserInSpawnOnJoinGroup(IUser user);

    boolean isTeleportToCenterLocation();

    @Deprecated
    boolean isCommandCooldownsEnabled();

    boolean isWorldChangeFlyResetEnabled();

    boolean isWorldChangePreserveFlying();

    boolean isGamemodeChangePreserveFlying();

    boolean isWorldChangeSpeedResetEnabled();

    @Deprecated
    CommentedConfigurationNode getCommandCooldowns();

    @Deprecated
    long getCommandCooldownMs(String label);

    @Deprecated
    Entry<Pattern, Long> getCommandCooldownEntry(String label);

    @Deprecated
    boolean isCommandCooldownPersistent(String label);

    boolean isCommandWarmupsEnabled();

    long getCommandWarmupMs(String label);

    Entry<Pattern, Long> getCommandWarmupEntry(String label);

    boolean isCommandWarmupPersistent(String label);

    boolean isNpcsInBalanceRanking();

    NumberFormat getCurrencyFormat();

    List<EssentialsSign> getUnprotectedSignNames();

    boolean isKitAutoEquip();

    boolean isPastebinCreateKit();

    boolean isUseBetterKits();

    boolean isAllowBulkBuySell();

    boolean isAllowSellNamedItems();

    boolean isAddingPrefixInPlayerlist();

    boolean isAddingSuffixInPlayerlist();

    int getNotifyPlayerOfMailCooldown();

    int getMotdDelay();

    boolean isDirectHatAllowed();

    List<String> getDefaultEnabledConfirmCommands();

    boolean isConfirmCommandEnabledByDefault(String commandName);

    TeleportWhenFreePolicy getTeleportWhenFreePolicy();

    boolean isJailOnlineTime();

    boolean isCompassTowardsHomePerm();

    boolean isAllowWorldInBroadcastworld();

    String getItemDbType();

    boolean allowOldIdSigns();

    boolean isWaterSafe();

    boolean isSafeUsermap();

    boolean logCommandBlockCommands();

    boolean logConsoleCommands();

    Set<Predicate<String>> getNickBlacklist();

    double getMaxProjectileSpeed();

    boolean isRemovingEffectsOnHeal();

    boolean isSpawnIfNoHome();

    boolean isConfirmHomeOverwrite();

    boolean isStrictHomeLimit();

    boolean infoAfterDeath();

    boolean isRespawnAtBed();

    boolean isUpdateCheckEnabled();

    boolean showZeroBaltop();

    String getNickRegex();

    BigDecimal getMultiplier(final User user);

    int getMaxItemLore();

    String getPrimaryColor();

    String getSecondaryColor();

    BigDecimal getBaltopMinBalance();

    long getBaltopMinPlaytime();

    int getBaltopEntryLimit();

    enum KeepInvPolicy {
        KEEP,
        DELETE,
        DROP
    }

    enum TeleportWhenFreePolicy {
        SPAWN,
        BACK,
        OFF
    }

    // TODO: consider separating out non-bool values? or replace this with an object-mapped class?
    enum DebugFlag {
        GENERIC("debug.generic", true),
        USERMAP_PRINT_STACK("usermap.print-stack", false),
        USERMAP_MAX_WARNS("usermap.max-warns", false),
        ;

        private final String flagKey;
        private final boolean isSetByGlobal;

        DebugFlag(final String flagKey, final boolean isSetByGlobal) {
            this.flagKey = flagKey;
            this.isSetByGlobal = isSetByGlobal;
        }

        public String getFlagKey() {
            return flagKey;
        }

        public boolean isSetByGlobal() {
            return isSetByGlobal;
        }

        public String getSystemPropertyKey() {
            return DEBUG_FLAG_NAMESPACE + "." + flagKey;
        }

        public String getConfigKey() {
            return "debug." + (flagKey.replace("debug.", ""));
        }

        public String getSystemPropertyValue() {
            return System.getProperty(getSystemPropertyKey(), "false");
        }

        public boolean getSystemPropertyBoolean() {
            return Boolean.parseBoolean(getSystemPropertyValue());
        }

        public Long getSystemPropertyLong() {
            final String value = getSystemPropertyValue();
            if (NumberUtil.isLong(value)) {
                return Long.parseLong(value);
            }
            return null;
        }
    }

}
