package zone.phi.bukkit.survival.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import zone.phi.bukkit.survival.Main;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class AUAHelper {

    private final YamlConfiguration auaConfig;
    private final RequestHelper req;
    private Map<UUID, Integer> bindings;

    private Map<Integer, String> errors, difficultyTags;
    private final List<ItemStack> b30Skulls, o10Skulls;

    public AUAHelper(String apiRoot, String token, YamlConfiguration auaConfig) {
        this.req = new RequestHelper(apiRoot.endsWith("/") ? apiRoot : apiRoot + "/", token);
        this.auaConfig = auaConfig;
        resolveConfig();
        loadErrors();
        loadDifficultyTags();
        this.b30Skulls = Main.getInstance().getSkullManager().generateNumericSkulls(1, 31, 0);
        this.o10Skulls = Main.getInstance().getSkullManager().generateNumericSkulls(1, 11, 1);
    }

    private void resolveConfig() {
        ConfigurationSection section = Objects.requireNonNull(this.auaConfig.getConfigurationSection("bindings"));
        this.bindings = new HashMap<>();
        for (String key : section.getKeys(false)) {
            this.bindings.put(UUID.fromString(key), section.getInt(key));
        }
    }

    private void loadErrors() {
        this.errors = new HashMap<>();
        this.errors.put(-1, "用户名或好友代码无效");
        this.errors.put(-2, "好友代码无效");
        this.errors.put(-3, "账号不存在");
        this.errors.put(-4, "用户数量过多");
        this.errors.put(-5, "曲目名称或 ID 无效");
        this.errors.put(-6, "曲目 ID 无效");
        this.errors.put(-7, "曲目未收录");
        this.errors.put(-8, "返回结果数量过多");
        this.errors.put(-9, "难度无效");
        this.errors.put(-10, "最近记录 或 溢出记录 查询数无效");
        this.errors.put(-11, "分配 Arcaea 账号时出现错误");
        this.errors.put(-12, "删除好友时出现错误");
        this.errors.put(-13, "添加好友时出现错误");
        this.errors.put(-14, "本曲目没有该难度");
        this.errors.put(-15, "未游玩");
        this.errors.put(-16, "账号被封禁");
        this.errors.put(-17, "请求 Best 30 时出现错误");
        this.errors.put(-18, "更新服务不可用");
        this.errors.put(-19, "搭档无效");
        this.errors.put(-20, "文件不可用");
        this.errors.put(-21, "范围无效");
        this.errors.put(-22, "范围结束值小于起始值");
        this.errors.put(-23, "潜力值小于 7.0，无法查询 Best 30");
        this.errors.put(-24, "需要更新 Arcaea，请联系管理员");
        this.errors.put(-233, "出现内部错误");
    }

    private void loadDifficultyTags() {
        this.difficultyTags = new HashMap<>();
        this.difficultyTags.put(0, "&r&3&l[PST]&r");
        this.difficultyTags.put(1, "&r&2&l[PRS]&r");
        this.difficultyTags.put(2, "&r&5&l[FTR]&r");
        this.difficultyTags.put(3, "&r&4&l[BYD]&r");
    }

    public String unbind(Player player) {
        if (!bindings.containsKey(player.getUniqueId())) {
            return "&c您还没有绑定 Arcaea 账号";
        }
        bindings.remove(player.getUniqueId());
        return "&a解绑成功";
    }

    public String bind(Player player, String user) {
        Map<String, String> params = new HashMap<>();
        if (bindings.containsKey(player.getUniqueId())) {
            params.put("usercode", String.valueOf(bindings.get(player.getUniqueId())));
            JSONObject resp = this.req.get("user/info", params).getJSONObject("content").getJSONObject("account_info");
            return "&c您已绑定 Arcaea 账号：" + resp.getString("name") + " (" + resp.getString("code") + ")\n若要换绑，请先输入 /arcaea unbind 进行解绑";
        }
        params.put("user", user);
        JSONObject resp = this.req.get("user/info", params);
        int status = resp.getInteger("status");
        if (status != 0) return "&c" + errors.get(status);
        bindings.put(player.getUniqueId(), Integer.parseInt(resp.getJSONObject("content").getJSONObject("account_info").getString("code")));
        return "&a绑定成功：&6" + resp.getJSONObject("content").getJSONObject("account_info").getString("name") + " &e(" + resp.getJSONObject("content").getJSONObject("account_info").getString("code") + ")";
    }

    public String getUserInfo(Player player, String recent) {
        if (!bindings.containsKey(player.getUniqueId())) {
            return "&c请先使用 /arcaea bind <用户名或好友代码> 绑定您的 Arcaea 账号！";
        }
        Map<String, String> params = new HashMap<>();
        params.put("usercode", String.valueOf(bindings.get(player.getUniqueId())));
        params.put("recent", recent);
        params.put("withsonginfo", "true");
        JSONObject resp = this.req.get("user/info", params);
        int status = resp.getInteger("status");
        if (status != 0) return "&c" + errors.get(status);
        resp = resp.getJSONObject("content");
        StringBuilder message = new StringBuilder(ChatUtils.HYPHEN + "\n&6&lArcaea 账号信息&r\n");
        message.append("\n&a用户 ID&f：&e").append(resp.getJSONObject("account_info").getString("user_id"))
                .append("\n&a用户名&f：&e").append(resp.getJSONObject("account_info").getString("name"))
                .append("\n&a好友代码&f：&e").append(resp.getJSONObject("account_info").getString("code"))
                .append("\n&a潜力值&f：&e").append(resp.getJSONObject("account_info").getInteger("rating") / 100.0)
                .append("\n&a加入时间&f：&e").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(resp.getJSONObject("account_info").getLong("join_date"))))
                .append("\n&a头像&f：&b&nhttps://arcaea.phizone.workers.dev/assets/icon?partner=").append(resp.getJSONObject("account_info").getInteger("character"))
                .append("\n&a搭档&f：&b&nhttps://arcaea.phizone.workers.dev/assets/char?partner=").append(resp.getJSONObject("account_info").getInteger("character"));
        if (resp.containsKey("recent_score")) {
            JSONArray recentArr = resp.getJSONArray("recent_score");
            JSONArray songInfoArr = resp.getJSONArray("songinfo");
            message.append("\n" + ChatUtils.HYPHEN + "\n&6&l最近游玩记录&r");
            for (int i = 0; i < recentArr.size(); i++) {
                message.append("\n ");
                JSONObject result = recentArr.getJSONObject(i);
                JSONObject songInfo = songInfoArr.getJSONObject(i);
                message.append("\n&7").append(i + 1).append(songInfo.getInteger("side") == 0 ? ". &b&l" : ". &d&l").append(songInfo.getString("name_en")).append(" ").append(this.difficultyTags.get(result.getInteger("difficulty")))
                        .append("\n   &a曲师&f：&e").append(songInfo.getString("artist"))
                        .append("\n   &a画师&f：&e").append(songInfo.getString("jacket_designer"))
                        .append("\n   &a谱师&f：&e").append(songInfo.getString("chart_designer"))
                        .append("\n   &a分数&f：&e").append(result.getInteger("score"))
                        .append("\n   &a潜力值&f：&e").append(result.getBigDecimal("rating").setScale(3, RoundingMode.HALF_UP).doubleValue())
                        .append("\n   &a回忆率&f：&e").append(result.getInteger("health"))
                        .append("\n   &a判定&f：&bPURE &f").append(result.getInteger("perfect_count")).append(" &7(+").append(result.getInteger("shiny_perfect_count")).append(") &8· &eFAR &f").append(result.getInteger("near_count")).append(" &8· &cLOST &f").append(result.getInteger("miss_count"))
                        .append("\n   &a游玩时间&f：&e").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(result.getLong("time_played"))));
            }
        }
        message.append("\n" + ChatUtils.HYPHEN);
        return message.toString();
    }

    public String getBest30(Player player, String overflow) {
        if (!bindings.containsKey(player.getUniqueId())) {
            return "&c请先使用 /arcaea bind <用户名或好友代码> 绑定您的 Arcaea 账号！";
        }
        Map<String, String> params = new HashMap<>();
        params.put("usercode", String.valueOf(bindings.get(player.getUniqueId())));
        params.put("overflow", overflow);
        params.put("withsonginfo", "true");
        JSONObject resp = this.req.get("user/best30", params);
        int status = resp.getInteger("status");
        if (status != 0) return "&c" + errors.get(status);
        resp = resp.getJSONObject("content");
        JSONArray arr = resp.getJSONArray("best30_list");
        JSONArray songInfoArr = resp.getJSONArray("best30_songinfo");
        if (resp.containsKey("best30_overflow")) {
            Inventory inv = Bukkit.createInventory(null, 54, ChatUtils.t("&3&lBest 30 &r&8& &3&lOverflow " + overflow));
            for (int i = 0; i < arr.size(); i++) {
                JSONObject result = arr.getJSONObject(i);
                JSONObject songInfo = songInfoArr.getJSONObject(i);
                ItemStack item = b30Skulls.get(i);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatUtils.t((i < 9 ? " &7" : "&7") + (i + 1) + (songInfo.getInteger("side") == 0 ? ". &b&l" : ". &d&l") + songInfo.getString("name_en") + " " + this.difficultyTags.get(result.getInteger("difficulty"))));
                List<String> lore = new ArrayList<>();
                lore.add("    &a曲师&f：&e" + songInfo.getString("artist"));
                lore.add("    &a画师&f：&e" + songInfo.getString("jacket_designer"));
                lore.add("    &a谱师&f：&e" + songInfo.getString("chart_designer"));
                lore.add("    &a分数&f：&e" + result.getInteger("score"));
                lore.add("    &a潜力值&f：&e" + result.getBigDecimal("rating").setScale(3, RoundingMode.HALF_UP).doubleValue());
                lore.add("    &a回忆率&f：&e" + result.getInteger("health"));
                lore.add("    &a判定&f：&bPURE &f" + result.getInteger("perfect_count") + " &7(+" + result.getInteger("shiny_perfect_count") + ") &8· &eFAR &f" + result.getInteger("near_count") + " &8· &cLOST &f" + result.getInteger("miss_count"));
                lore.add("    &a游玩时间&f：&e" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(result.getLong("time_played"))));
                meta.setLore(ChatUtils.t(lore));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
            arr = resp.getJSONArray("best30_overflow");
            songInfoArr = resp.getJSONArray("best30_overflow_songinfo");
            for (int i = 0; i < arr.size(); i++) {
                JSONObject result = arr.getJSONObject(i);
                JSONObject songInfo = songInfoArr.getJSONObject(i);
                ItemStack item = o10Skulls.get(i);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatUtils.t("&7" + (i + 31) + (songInfo.getInteger("side") == 0 ? ". &b&l" : ". &d&l") + songInfo.getString("name_en") + " " + this.difficultyTags.get(result.getInteger("difficulty"))));
                List<String> lore = new ArrayList<>();
                lore.add("    &a曲师&f：&e" + songInfo.getString("artist"));
                lore.add("    &a画师&f：&e" + songInfo.getString("jacket_designer"));
                lore.add("    &a谱师&f：&e" + songInfo.getString("chart_designer"));
                lore.add("    &a分数&f：&e" + result.getInteger("score"));
                lore.add("    &a潜力值&f：&e" + result.getBigDecimal("rating").setScale(3, RoundingMode.HALF_UP).doubleValue());
                lore.add("    &a回忆率&f：&e" + result.getInteger("health"));
                lore.add("    &a判定&f：&bPURE &f" + result.getInteger("perfect_count") + " &7(+" + result.getInteger("shiny_perfect_count") + ") &8· &eFAR &f" + result.getInteger("near_count") + " &8· &cLOST &f" + result.getInteger("miss_count"));
                lore.add("    &a游玩时间&f：&e" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(result.getLong("time_played"))));
                meta.setLore(ChatUtils.t(lore));
                item.setItemMeta(meta);
                inv.setItem(36 + i, item);
            }
            player.openInventory(inv);
            return "&a查询成功";
        }
        StringBuilder message = new StringBuilder(ChatUtils.HYPHEN + "\n&6&lBest 30&r\n");
        for (int i = 0; i < arr.size(); i++) {
            JSONObject result = arr.getJSONObject(i);
            JSONObject songInfo = songInfoArr.getJSONObject(i);
            message.append(i < 9 ? "\n &7" : "\n&7").append(i + 1).append(songInfo.getInteger("side") == 0 ? ". &b&l" : ". &d&l").append(songInfo.getString("name_en")).append(" ").append(this.difficultyTags.get(result.getInteger("difficulty"))).append("&r &8@ &7").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(result.getLong("time_played"))))
                    .append("\n    &a分数&f：&e").append(result.getInteger("score"))
                    .append("  &a潜力值&f：&e").append(result.getBigDecimal("rating").setScale(3, RoundingMode.HALF_UP).doubleValue())
                    .append("  &a回忆率&f：&e").append(result.getInteger("health"))
                    .append("\n    &a判定&f：&bPURE &f").append(result.getInteger("perfect_count")).append(" &7(+").append(result.getInteger("shiny_perfect_count")).append(") &8· &eFAR &f").append(result.getInteger("near_count")).append(" &8· &cLOST &f").append(result.getInteger("miss_count"));
        }
        message.append("\n" + ChatUtils.HYPHEN);
        return message.toString();
    }

    public String getBest(Player player, String song, String difficulty) {
        if (!bindings.containsKey(player.getUniqueId())) {
            return "&c请先使用 /arcaea bind <用户名或好友代码> 绑定您的 Arcaea 账号！";
        }
        Map<String, String> params = new HashMap<>();
        params.put("usercode", String.valueOf(bindings.get(player.getUniqueId())));
        params.put("songname", song);
        params.put("difficulty", difficulty);
        params.put("withsonginfo", "true");
        JSONObject resp = this.req.get("user/best", params);
        int status = resp.getInteger("status");
        if (status != 0) return "&c" + errors.get(status);
        JSONObject record = resp.getJSONObject("content").getJSONObject("record");
        JSONObject songInfo = resp.getJSONObject("content").getJSONArray("songinfo").getJSONObject(0);
        return ChatUtils.HYPHEN + "\n&6&l谱面最高分&r\n" + (songInfo.getInteger("side") == 0 ? "\n \n&b&l" : "\n \n&d&l") + songInfo.getString("name_en") + " " + this.difficultyTags.get(record.getInteger("difficulty")) +
                "\n&a曲师&f：&e" + songInfo.getString("artist") +
                "\n&a画师&f：&e" + songInfo.getString("jacket_designer") +
                "\n&a谱师&f：&e" + songInfo.getString("chart_designer") +
                "\n&a分数&f：&e" + record.getInteger("score") +
                "\n&a潜力值&f：&e" + record.getBigDecimal("rating").setScale(3, RoundingMode.HALF_UP).doubleValue() +
                "\n&a回忆率&f：&e" + record.getInteger("health") +
                "\n&a判定&f：&bPURE &f" + record.getInteger("perfect_count") + " &7(+" + record.getInteger("shiny_perfect_count") + ") &8· &eFAR &f" + record.getInteger("near_count") + " &8· &cLOST &f" + record.getInteger("miss_count") +
                "\n&a游玩时间&f：&e" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(record.getLong("time_played"))) +
                "\n" + ChatUtils.HYPHEN;
    }

    public String getSongInfo(String song) {
        Map<String, String> params = new HashMap<>();
        params.put("songname", song);
        JSONObject resp = this.req.get("song/info", params);
        int status = resp.getInteger("status");
        if (status != 0) return "&c" + errors.get(status);
        JSONArray difficulties = resp.getJSONObject("content").getJSONArray("difficulties");
        StringBuilder message = new StringBuilder(ChatUtils.HYPHEN + "\n&6&l曲目详情&r");
        for (int i = 0; i < difficulties.size(); i++) {
            JSONObject info = difficulties.getJSONObject(i);
            message.append(info.getInteger("side") == 0 ? "\n \n&b&l" : "\n \n&d&l").append(info.getString("name_en")).append(" ").append(this.difficultyTags.get(i)).append(" &7v").append(info.getString("version"))
                    .append("\n&a曲包&f：&e").append(info.getString("set_friendly"))
                    .append("\n&a曲师&f：&e").append(info.getString("artist"))
                    .append("\n&a画师&f：&e").append(info.getString("jacket_designer"))
                    .append("\n&a曲绘&f：&b&nhttps://arcaea.phizone.workers.dev/assets/song?songid=").append(resp.getJSONObject("content").getString("song_id"))
                    .append("\n&aBPM&f：&e").append(info.getString("bpm"))
                    .append("\n&a时长&f：&e").append(convTime(info.getInteger("time")))
                    .append("\n&a谱师&f：&e").append(info.getString("chart_designer"))
                    .append("\n&a定数&f：&e").append(info.getInteger("rating") / 10.0)
                    .append("\n&a物量&f：&e").append(info.getInteger("note"));
        }
        message.append("\n&r\n&a别名&f：&e");
        JSONArray aliases = resp.getJSONObject("content").getJSONArray("alias");
        for (int i = 0; i < aliases.size(); i++) {
            String str = aliases.getString(i);
            message.append(i > 0 ? "、" + str : str);
        }
        message.append("\n" + ChatUtils.HYPHEN);
        return message.toString();
    }

    public String random(int l, int h) {
        Map<String, String> params = new HashMap<>();
        params.put("withsonginfo", "true");
        params.put("start", String.valueOf(l));
        params.put("end", String.valueOf(h));
        JSONObject resp = this.req.get("song/random", params);
        int status = resp.getInteger("status");
        if (status != 0) return "&c" + errors.get(status);
        StringBuilder message = new StringBuilder(ChatUtils.HYPHEN + "\n&6&l随机谱面&r\n");
        JSONObject info = resp.getJSONObject("content").getJSONObject("songinfo");
        message.append(info.getInteger("side") == 0 ? "\n&b&l" : "\n&d&l").append(info.getString("name_en")).append(" ").append(this.difficultyTags.get(resp.getJSONObject("content").getInteger("ratingClass"))).append(" &7v").append(info.getString("version"))
                .append("\n&a曲包&f：&e").append(info.getString("set_friendly"))
                .append("\n&a曲师&f：&e").append(info.getString("artist"))
                .append("\n&a画师&f：&e").append(info.getString("jacket_designer"))
                .append("\n&a曲绘&f：&b&nhttps://arcaea.phizone.workers.dev/assets/song?songid=").append(resp.getJSONObject("content").getString("id"))
                .append("\n&aBPM&f：&e").append(info.getString("bpm"))
                .append("\n&a时长&f：&e").append(convTime(info.getInteger("time")))
                .append("\n&a谱师&f：&e").append(info.getString("chart_designer"))
                .append("\n&a定数&f：&e").append(info.getInteger("rating") / 10.0)
                .append("\n&a物量&f：&e").append(info.getInteger("note"));
        message.append("\n" + ChatUtils.HYPHEN);
        return message.toString();
    }

    private String convTime(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%s分%s秒", minutes, seconds);
    }

    public Map<UUID, Integer> getBindings() {
        return this.bindings;
    }

    private record RequestHelper(String apiRoot, String token) {

        private String resolveQueryParams(Map<String, String> queryParams) {
            StringBuilder str = new StringBuilder("?");
            for (String key : queryParams.keySet()) {
                str.append(key).append("=").append(queryParams.get(key)).append("&");
            }
            return str.substring(0, str.length() - 1);
        }

        private JSONObject get(String path, Map<String, String> queryParams) {
            try {
                HttpsURLConnection request = (HttpsURLConnection) (new URL(this.apiRoot + path + resolveQueryParams(queryParams))).openConnection();
                Main.getInstance().getLogger().info("正在访问 " + request.getURL());
                request.setRequestProperty("User-Agent", this.token);
                request.connect();
                return JSON.parseObject(IOUtils.toString((InputStream) request.getContent(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
