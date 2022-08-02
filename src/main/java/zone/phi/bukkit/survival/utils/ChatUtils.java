package zone.phi.bukkit.survival.utils;

import org.bukkit.ChatColor;
import zone.phi.bukkit.survival.Main;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    private static String prefix;

    public static void init() {
        prefix = Main.getInstance().getConfig().getString("prefix");
    }

    public static String t(String str, String module) {
        return ChatColor.translateAlternateColorCodes('&', prefix.replace("%module%", module) + " " + str);
    }

    public static String t(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> t(List<String> strings) {
        List<String> list = new ArrayList<>();
        for (String str : strings) {
            list.add(t(str));
        }
        return list;
    }

    public final static String HYPHEN = "&r \n&b&l---------------------------------------------&r\n ";

}
