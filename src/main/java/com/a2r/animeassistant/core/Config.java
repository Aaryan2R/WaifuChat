package com.a2r.animeassistant.core;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

// config stuff - loads settings from properties file
// saves to ~/.animeassistant/config.properties
public class Config {
    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".animeassistant");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");

    private String apiKey = "";
    private String waifuName = "Sakura";
    private String userName = "Senpai";
    private String model = "gemini-3.1-flash-lite";
    private String personality = "tsundere";
    private String waifuImageClosed = "";
    private String waifuImageOpen = "";

    public Config() {
        load();
    }

    private void load() {
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
            if (!Files.exists(CONFIG_FILE)) {
                createDefaultConfig();
                return;
            }

            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
                props.load(in);
            }

            apiKey = props.getProperty("api_key", "").trim();
            waifuName = props.getProperty("waifu_name", "Sakura").trim();
            userName = props.getProperty("user_name", "Senpai").trim();
            model = props.getProperty("model", "gemini-3.1-flash-lite").trim();
            personality = props.getProperty("personality", "tsundere").trim().toLowerCase();
            waifuImageClosed = props.getProperty("waifu_image_closed", "").trim();
            waifuImageOpen = props.getProperty("waifu_image_open", "").trim();

        } catch (IOException e) {
            System.err.println("config load failed: " + e.getMessage());
        }
    }

    // config template with instructions - created on first run
    private void createDefaultConfig() throws IOException {
        String content = """
                # ======= Anime Waifu Companion Config =======

                # get your api key from: https://aistudio.google.com/apikey
                api_key=

                # your name (what she calls you)
                user_name=Senpai

                # waifu name
                waifu_name=Sakura

                # gemini model (change if quota errors)
                # options: gemini-3.1-flash-lite, gemini-2.5-flash-lite, gemini-2.5-flash
                model=gemini-3.1-flash-lite

                # ======= Personality Type =======
                # tsundere  = cold outside, secretly caring. gets angry when flustered
                # yandere   = sweet and devoted but obsessive and possessive
                # kuudere   = calm, cool, emotionally distant. rarely shows feelings
                # dandere   = super shy, anxious, stutters. opens up slowly
                # deredere  = openly affectionate, warm, bubbly, no drama
                # kamidere  = god complex, thinks shes better than everyone
                # gyaru     = fashionable, trendy, social butterfly. casual slang
                # oneesan   = mature, caring, teasing older-sister energy. ara ara~
                # tomboy    = athletic, blunt, one of the boys. bad with feelings
                personality=tsundere

                # ======= Custom Waifu Image =======
                # you can set your own waifu image!
                #
                # how the mouth animation works:
                #   the app alternates between a "closed mouth" and "open mouth" image
                #   while your waifu is "talking" to create a speaking effect.
                #
                # option 1: provide TWO images (recommended for animation)
                #   prepare 2 nearly identical images of your waifu:
                #   - one with mouth CLOSED (resting face)
                #   - one with mouth OPEN (talking face)
                #   set both paths below. she will animate while replying!
                #
                # option 2: provide just ONE image
                #   set only waifu_image_closed and leave waifu_image_open empty.
                #   she will still display but without mouth animation.
                #
                # leave both empty to use the default built-in waifu.
                # use full file paths like: C:/Users/you/Pictures/waifu_closed.png
                waifu_image_closed=
                waifu_image_open=
                """;
        Files.writeString(CONFIG_FILE, content);
        System.out.println("config created at: " + CONFIG_FILE);
        System.out.println("add your gemini api key to start chatting");
    }

    public void save() {
        try {
            // write config manually so we keep all the helpful comments
            // Properties.store() strips them which sucks for new users
            String content = "# ======= Anime Waifu Companion Config =======\n"
                + "# get your api key from: https://aistudio.google.com/apikey\n"
                + "api_key=" + apiKey + "\n"
                + "\n"
                + "# your name (what she calls you)\n"
                + "user_name=" + userName + "\n"
                + "\n"
                + "# waifu name\n"
                + "waifu_name=" + waifuName + "\n"
                + "\n"
                + "# gemini model (change if quota errors)\n"
                + "# options: gemini-3.1-flash-lite, gemini-2.5-flash-lite, gemini-2.5-flash\n"
                + "model=" + model + "\n"
                + "\n"
                + "# ======= Personality Type =======\n"
                + "# tsundere  = cold outside, secretly caring. gets angry when flustered\n"
                + "# yandere   = sweet and devoted but obsessive and possessive\n"
                + "# kuudere   = calm, cool, emotionally distant. rarely shows feelings\n"
                + "# dandere   = super shy, anxious, stutters. opens up slowly\n"
                + "# deredere  = openly affectionate, warm, bubbly, no drama\n"
                + "# kamidere  = god complex, thinks shes better than everyone\n"
                + "# gyaru     = fashionable, trendy, social butterfly. casual slang\n"
                + "# oneesan   = mature, caring, teasing older-sister energy. ara ara~\n"
                + "# tomboy    = athletic, blunt, one of the boys. bad with feelings\n"
                + "personality=" + personality + "\n"
                + "\n"
                + "# ======= Custom Waifu Image =======\n"
                + "# you can set your own waifu image!\n"
                + "#\n"
                + "# how the mouth animation works:\n"
                + "#   the app alternates between a \"closed mouth\" and \"open mouth\" image\n"
                + "#   while your waifu is \"talking\" to create a speaking effect.\n"
                + "#\n"
                + "# option 1: provide TWO images (recommended for animation)\n"
                + "#   prepare 2 nearly identical images of your waifu:\n"
                + "#   - one with mouth CLOSED (resting face)\n"
                + "#   - one with mouth OPEN (talking face)\n"
                + "#   set both paths below. she will animate while replying!\n"
                + "#\n"
                + "# option 2: provide just ONE image\n"
                + "#   set only waifu_image_closed and leave waifu_image_open empty.\n"
                + "#   she will still display but without mouth animation.\n"
                + "#\n"
                + "# leave both empty to use the default built-in waifu.\n"
                + "# use full file paths like: C:/Users/you/Pictures/waifu_closed.png\n"
                + "waifu_image_closed=" + waifuImageClosed + "\n"
                + "waifu_image_open=" + waifuImageOpen + "\n";

            Files.writeString(CONFIG_FILE, content);
        } catch (IOException e) {
            System.err.println("config save failed: " + e.getMessage());
        }
    }

    // getters
    public String getApiKey() { return apiKey; }
    public String getWaifuName() { return waifuName; }
    public String getUserName() { return userName; }
    public String getModel() { return model; }
    public String getPersonality() { return personality; }
    public String getWaifuImageClosed() { return waifuImageClosed; }
    public String getWaifuImageOpen() { return waifuImageOpen; }
    public Path getConfigDir() { return CONFIG_DIR; }

    // setters
    public void setApiKey(String key) { this.apiKey = key; }
    public void setWaifuName(String name) { this.waifuName = name; }
    public void setUserName(String name) { this.userName = name; }
    public void setModel(String m) { this.model = m; }
    public void setPersonality(String p) { this.personality = p.toLowerCase(); }
    public void setWaifuImageClosed(String path) { this.waifuImageClosed = path; }
    public void setWaifuImageOpen(String path) { this.waifuImageOpen = path; }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    // true if user set at least the closed mouth image
    public boolean hasCustomImage() {
        return waifuImageClosed != null && !waifuImageClosed.isBlank();
    }

    // true if user provided both images for animation
    public boolean hasCustomAnimation() {
        return hasCustomImage() && waifuImageOpen != null && !waifuImageOpen.isBlank();
    }
}
