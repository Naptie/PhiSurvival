package zone.phi.bukkit.survival.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkullManager {

    private final List<List<String>> numericSkulls;

    public SkullManager() {
        this.numericSkulls = new ArrayList<>();
        this.numericSkulls.add(List.of("https://textures.minecraft.net/texture/7544bf0d20d8b4f486e2510d3995e7b552451055b2b552d7f2d5175211d02909",
                "https://textures.minecraft.net/texture/105b356be68ac56cfe0611b95027adf1ee67050e4cd66a0a99678fc2e25b6b0f",
                "https://textures.minecraft.net/texture/40ec45e8ffb9cb714d1412bb9ee54fd1e7a3140c02e35ae0d3f308f1666ba126",
                "https://textures.minecraft.net/texture/2466629a31aae22dba15a7b710a4517308656c19ba39a89ac2de2915a46e2823",
                "https://textures.minecraft.net/texture/97a1956ed6f32e18b139f2d709355ac6945d0ab8fecd04d4c159d08006bfbe58",
                "https://textures.minecraft.net/texture/855fad012c8844fc313510436b4919e002d93d6a0761f7bdaf78069683eee5e6",
                "https://textures.minecraft.net/texture/c0400111ae026101077b59d8482c84a832c74f4f2105fc13b7c94f5b8000ea3",
                "https://textures.minecraft.net/texture/a34505ed2567ea2efbab49977f7da8f1178db9ae53a0e99737286dc56e63636a",
                "https://textures.minecraft.net/texture/a17ddf12485fb54cab9c9ffb55feea62a2d90b825772c6ea38561ce750cb6f83",
                "https://textures.minecraft.net/texture/f75022feb11b736e50bc7d19d26d4ee00cecd6734a2138d2579f799b119b6734",
                "https://textures.minecraft.net/texture/6b84208e4be2233fcd96e7f02d9613d5437f1d006236277c57d40652c5be2f23",
                "https://textures.minecraft.net/texture/19aa8a65ba29b4b7cbb9c66cf59fb537ba2c3013a958620b976b9f06248e8dda",
                "https://textures.minecraft.net/texture/7dc1330b302e800c31c8dfefbd1b996016c34649f3a396749d3e048637364aad",
                "https://textures.minecraft.net/texture/7e98a3884e0eac967c73a2bcbff7df62ab2fb327b4ceb9e636c74254866b5c7c",
                "https://textures.minecraft.net/texture/3866643decb2d148999f9768dae89fbdb196b30d55f8846b1fbbe736bb7e5042",
                "https://textures.minecraft.net/texture/744a95611950cc7f154c9f56ad472c1941376ec171efb8ba476e6cec4f866526",
                "https://textures.minecraft.net/texture/1546959556eff4b8827ca50d8df686e8f20d5c843da689dddc48bff0a217efbe",
                "https://textures.minecraft.net/texture/1f4fb00e824c97d9cfba4c44def27a79513978979515048f2e69eb5b7123c159",
                "https://textures.minecraft.net/texture/9216b42018004a86081b5c1ee87dc8f12770f1859266e2cd359b75b44b5e7680",
                "https://textures.minecraft.net/texture/93c7097291042c394267892c56feee9ba3936826ea5b2232f46cb5474f9cd937",
                "https://textures.minecraft.net/texture/4f85a13977d01865a9d95d3279528285872551c36fd5231c7e3ab9897a7560fd",
                "https://textures.minecraft.net/texture/cdb512403f3610966d10bd3ca9fc61fdc5021268382f69719ac5679f3efd4c14",
                "https://textures.minecraft.net/texture/cadaef824771250374fc3e82efe020ab765e665340cc2393f3b4ae8d6d18529c",
                "https://textures.minecraft.net/texture/266f896c37a9d7fb857ff8c9f6f5e3540d17e1b893419f523eb199b71e71dde3",
                "https://textures.minecraft.net/texture/f081f9274e4ef802453ac95381c00426e462440d3bb085fb3afefa6ded6a8d01",
                "https://textures.minecraft.net/texture/85fda42caf159b116404929795f2812cb800929e34cb45bddfd3080ac289f7a9",
                "https://textures.minecraft.net/texture/d751a278e4d54e7345f9e9442e8ead409f18f6618486dfacbdbbaf2dfc2d7710",
                "https://textures.minecraft.net/texture/dd3c2be4eba9f104700fb1a45028aed9420b0f0ed8ce33a04efc221103ae0015",
                "https://textures.minecraft.net/texture/5d6b70034fe2b084727a2c5d57b372173f014fa79f7168c7187262441553ef6",
                "https://textures.minecraft.net/texture/1fff526e562bab4dead3fa95e7360aa4078ee17a0ce119851785346a384d4ec3",
                "https://textures.minecraft.net/texture/ee4b192f70806f0166ca7f20047c9788751a82a7fd0486243e0c265847b55103",
                "https://textures.minecraft.net/texture/87721be37f9332ad8be43cbdf156f4349267ea2c110fafec23768e68d830caf6"));
        this.numericSkulls.add(List.of("https://textures.minecraft.net/texture/c6ec4dc46c3bb959d6e76f83d154b5b6b1d8eb0ca2f082da82b2da724a67d7e6",
                "https://textures.minecraft.net/texture/52d6a15735e9ab2ce6223d233a3f688d2301bbf3f204bc03ff44ae8a7947f011",
                "https://textures.minecraft.net/texture/5c3663b0b3acb7eb660b769cbe985e8f7dc0ad2b458291471f762c724d19aa32",
                "https://textures.minecraft.net/texture/198078f2997355244e06f7e547666f2c63cb8f4aad589744ef323a68931cfdbd",
                "https://textures.minecraft.net/texture/adf639036cf4993e90511ba04b0dbcd8a1f5b456c2711dc07d485d823e452c3b",
                "https://textures.minecraft.net/texture/93eaab1209ece96cc418ecae517efbcde8b82691502b36a0a894235f7cb3395",
                "https://textures.minecraft.net/texture/8a32228f422a294ddbf4b98862c9191a6cfb3a35841891aff09c4dd623a9387c",
                "https://textures.minecraft.net/texture/2789a5ac3c8674a63c982f69ef1d6a3deacd25df66bc91fcb5180f6d745c82d8",
                "https://textures.minecraft.net/texture/232eef4435e11df5eadb2037982ce6529a891bc910b2b5a83a54773b4f558d8f",
                "https://textures.minecraft.net/texture/38ae7da54820bf80f5df39d056fba268ad3ad158817e59d720389f68f71d0f35",
                "https://textures.minecraft.net/texture/9c490eb30cf62238ceded4774ffc29ebb23f737102fd20aa05d6d6602f615990"));
    }

    public List<ItemStack> generateNumericSkulls(int begin, int end, int style) {
        List<ItemStack> list = new ArrayList<>();
        if (end > this.numericSkulls.get(style).size()) {
            throw new IllegalArgumentException("end is greater than the size of list of skulls in the given style (" + end + " > " + this.numericSkulls.get(style).size() + ")");
        }
        for (int i = begin; i < end; i++) {
            list.add(getCustomSkull(this.numericSkulls.get(style).get(i)));
        }
        return list;
    }

    public ItemStack getCustomSkull(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        final Base64 base64 = new Base64();
        byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta headMeta = head.getItemMeta();
        assert headMeta != null;
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);
        return head;
    }
}
