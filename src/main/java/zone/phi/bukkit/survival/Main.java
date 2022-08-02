package zone.phi.bukkit.survival;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import zone.phi.bukkit.survival.commands.Arcaea;
import zone.phi.bukkit.survival.listeners.PlayerJoin;
import zone.phi.bukkit.survival.listeners.PlayerQuit;
import zone.phi.bukkit.survival.utils.AUAHelper;
import zone.phi.bukkit.survival.utils.ChatUtils;
import zone.phi.bukkit.survival.utils.SkullManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private Logger logger;
    private AUAHelper auaHelper;
    private YamlConfiguration auaConfig;
    private SkullManager skullManager;
    private BossBar bar;
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        this.logger = getLogger();
        registerConfigs();
        ChatUtils.init();
        this.skullManager = new SkullManager();
        this.auaHelper = new AUAHelper(Objects.requireNonNull(getConfig().getString("aua.root")), getConfig().getString("aua.token"), this.auaConfig);
        this.bar = Bukkit.createBossBar(ChatUtils.t("&a您正在 &b&lmc.phi.zone &a上游玩"), BarColor.WHITE, BarStyle.SOLID);
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.bar.addPlayer(player);
        }
        registerListeners();
        registerCommands();
        this.logger.info("已启用 " + getDescription().getName() + " v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        saveConfigs(this.auaHelper.getBindings());
        Bukkit.getScheduler().cancelTasks(this);
        this.bar.removeAll();
        this.bar = null;
        this.skullManager = null;
        this.auaHelper = null;
        instance = null;
        this.logger.info("已禁用 " + getDescription().getName() + " v" + getDescription().getVersion());
        this.logger = null;
    }

    private void registerConfigs() {
        saveDefaultConfig();
        File auaFile = new File(getDataFolder(), "aua.yml");
        if (!auaFile.exists()) saveResource("aua.yml", true);
        this.auaConfig = YamlConfiguration.loadConfiguration(auaFile);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
    }

    private void saveConfigs(Map<UUID, Integer> bindings) {
        try {
            for (UUID key : bindings.keySet()) {
                this.auaConfig.set("bindings." + key.toString(), bindings.get(key));
            }
            File file = new File(getDataFolder(), "aua.yml");
            getLogger().info("正在保存至 " + file.getAbsolutePath());
            this.auaConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("arcaea")).setExecutor(new Arcaea());
    }

    public AUAHelper getAuaHelper() {
        return this.auaHelper;
    }

    public static Main getInstance() {
        return instance;
    }

    public SkullManager getSkullManager() {
        return this.skullManager;
    }

    public BossBar getBar() {
        return this.bar;
    }
}
