package zone.phi.bukkit.survival.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zone.phi.bukkit.survival.Main;
import zone.phi.bukkit.survival.utils.AUAHelper;
import zone.phi.bukkit.survival.utils.ChatUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Arcaea implements CommandExecutor {

    private final Map<String, String> commands;

    public Arcaea() {
        this.commands = new LinkedHashMap<>();
        this.commands.put("bind", "<用户名或好友代码>");
        this.commands.put("unbind", "");
        this.commands.put("info", "[最近记录查询数 (0~7)]");
        this.commands.put("best", "<曲目名称> [难度]");
        this.commands.put("b30", "[溢出记录查询数 (0~10)]");
        this.commands.put("song", "<曲目名称>");
        this.commands.put("random", "[最低定级] [最高定级]");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        AUAHelper auaHelper = Main.getInstance().getAuaHelper();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(t("&c请在游戏内以玩家身份执行该指令！"));
            return true;
        }
        if (strings.length < 1) {
            StringBuilder message = new StringBuilder(ChatUtils.HYPHEN + "\n&a欢迎使用 &bPhizarc &a模块！\n&r\n&e指令列表：");
            for (String key : this.commands.keySet()) {
                message.append("\n&7- &a/arcaea &b").append(key).append(" ").append(this.commands.get(key));
            }
            message.append("\n" + ChatUtils.HYPHEN);
            player.sendMessage(t(message.toString()));
            return true;
        }
        List<String> args = List.of("bind", "best", "song");
        String arg = strings[0].toLowerCase();
        if (args.contains(arg) && strings.length < 2) {
            player.sendMessage(t("&c用法：/arcaea " + arg + " " + commands.get(arg)));
            return true;
        }
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            switch (strings[0].toLowerCase()) {
                case "bind" -> player.sendMessage(t(auaHelper.bind(player, strings[1])));
                case "unbind" -> player.sendMessage(t(auaHelper.unbind(player)));
                case "info" -> {
                    String recent = strings.length > 1 ? strings[1] : "0";
                    player.sendMessage(t(Objects.equals(recent, "0") ? "&a正在查询 &6Arcaea 账号信息&a，请稍候" : "&a正在查询 &6Arcaea 账号信息&a与&6最近" + recent + "条游玩记录&a，请稍候"));
                    player.sendMessage(t(auaHelper.getUserInfo(player, recent)));
                }
                case "b30" -> {
                    String overflow = strings.length > 1 ? strings[1] : "0";
                    player.sendMessage(t(Objects.equals(overflow, "0") ? "&a正在查询 &6Best 30&a，请稍候" : "&a正在查询 &6Best 30&a 与 &6Overflow " + overflow + "&a，请稍候"));
                    player.sendMessage(t(auaHelper.getBest30(player, overflow)));
                }
                case "best" -> {
                    String song = strings[1], difficulty = strings.length > 2 ? strings[2] : "2";
                    player.sendMessage(t("&a正在查询&6谱面最高分&a，请稍候"));
                    player.sendMessage(t(auaHelper.getBest(player, song, difficulty)));
                }
                case "song" -> {
                    player.sendMessage(t("&a正在获取&6曲目详情&a，请稍候"));
                    player.sendMessage(t(auaHelper.getSongInfo(strings[1])));
                }
                case "random" -> {
                    player.sendMessage(t("&a正在获取&6随机谱面&a，请稍候"));
                    player.sendMessage(t(auaHelper.random(strings.length > 1 ? parseDifficulty(strings[1]) : 0, strings.length > 2 ? parseDifficulty(strings[2]) : 24)));
                }
            }
        });
        return true;
    }

    private int parseDifficulty(String str) {
        String num = str.endsWith("+") ? str.substring(0, str.length() - 1) : str;
        return Integer.parseInt(num) * 2 + (str.endsWith("+") ? 1 : 0);
    }

    private String t(String str) {
        return ChatUtils.t(str, "Phizarc");
    }
}
