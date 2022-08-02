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
        this.bar = Bukkit.createBossBar(ChatUtils.t("&a您正在 &b&lmc.phi.zone &a上游玩"), BarColor.GREEN, BarStyle.SOLID);
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.bar.addPlayer(player);
        }
//        setBarAnimation();
        registerListeners();
        registerCommands();
        this.logger.info("已启用 " + getDescription().getName() + " v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        this.logger.info("已禁用 " + getDescription().getName() + " v" + getDescription().getVersion());
        saveConfigs(this.auaHelper.getBindings());
        Bukkit.getScheduler().cancelTasks(this);
        this.bar.removeAll();
        this.bar = null;
        this.skullManager = null;
        this.auaHelper = null;
        this.logger = null;
        instance = null;
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

//    private void setBarAnimation() {
//        BossBar bar = Main.getInstance().getBar();
//        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
//            try {
//                for (BarColor color : BarColor.values()) {
//                    TimeUnit.SECONDS.sleep(1);
//                    bar.setColor(color);
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }, 0, 0);
//        List<String> titles = List.of(
//                "&f您&a正在 &b&lmc.phi.zone &a上游玩",
//                "&a您&f正&a在 &b&lmc.phi.zone &a上游玩",
//                "&a您正&f在&a &b&lmc.phi.zone &a上游玩",
//                "&a您正在 &f&lm&b&lc.phi.zone &a上游玩",
//                "&a您正在 &b&lm&f&lc&b&l.phi.zone &a上游玩",
//                "&a您正在 &b&lmc&f&l.&b&lphi.zone &a上游玩",
//                "&a您正在 &b&lmc.&f&lp&b&lhi.zone &a上游玩",
//                "&a您正在 &b&lmc.p&f&lh&b&li.zone &a上游玩",
//                "&a您正在 &b&lmc.ph&f&li&b&l.zone &a上游玩",
//                "&a您正在 &b&lmc.phi&f&l.&b&lzone &a上游玩",
//                "&a您正在 &b&lmc.phi.&f&lz&b&lone &a上游玩",
//                "&a您正在 &b&lmc.phi.z&f&lo&b&lne &a上游玩",
//                "&a您正在 &b&lmc.phi.zo&f&ln&b&le &a上游玩",
//                "&a您正在 &b&lmc.phi.zon&f&le &a上游玩",
//                "&a您正在 &b&lmc.phi.zone &f上&a游玩",
//                "&a您正在 &b&lmc.phi.zone &a上&f游&a玩",
//                "&a您正在 &b&lmc.phi.zone &a上游&f玩",
//                "&a您正在 &b&lmc.phi.zone &a上游玩");
//        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
//            try {
//                for (String title : titles) {
//                    bar.setTitle(ChatUtils.t(title));
//                    TimeUnit.MILLISECONDS.sleep(50);
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }, 0, 0);
//    }

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
