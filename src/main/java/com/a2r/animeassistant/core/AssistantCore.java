package com.a2r.animeassistant.core;

import com.a2r.animeassistant.api.GeminiClient;

import java.util.List;

// main brain - connects config + memory + gemini together
public class AssistantCore {
    private final Config config;
    private final ChatMemory memory;
    private final GeminiClient gemini;
    private final String systemPrompt;

    public AssistantCore() {
        this.config = new Config();
        this.memory = new ChatMemory(config.getConfigDir());
        this.gemini = new GeminiClient(config.getApiKey(), config.getModel());

        // build personality prompt from config settings
        this.systemPrompt = GeminiClient.buildSystemPrompt(
            config.getWaifuName(), config.getUserName(), config.getPersonality()
        );
    }

    // send message, get reply, save to memory
    public String chat(String userMessage) {
        if (!config.hasApiKey()) {
            return "need an API key to chat! add it to: "
                + config.getConfigDir().resolve("config.properties");
        }

        List<String[]> history = memory.getRecentMessages();
        String reply = gemini.chat(systemPrompt, history, userMessage);

        memory.addUserMessage(userMessage);
        memory.addModelMessage(reply);

        return reply;
    }

    // greeting when app opens - different for new vs returning users
    public String getGreeting() {
        if (!config.hasApiKey()) {
            return "hey! im " + config.getWaifuName() + "~ before we can chat "
                + "you gotta add your gemini api key to:\n"
                + config.getConfigDir().resolve("config.properties") + "\n\n"
                + "get a free key at: https://aistudio.google.com/apikey";
        }

        String user = config.getUserName();

        if (memory.getTotalMessages() == 0) {
            // first time - let gemini generate a greeting in character
            String reply = gemini.chat(systemPrompt, memory.getRecentMessages(),
                "[" + user + " just opened your app for the first time. "
                + "Introduce yourself and greet them in character. Keep it short.]");
            memory.addModelMessage(reply);
            return reply;
        } else {
            // returning user - welcome back in character
            String reply = gemini.chat(systemPrompt, memory.getRecentMessages(),
                "[" + user + " just came back to talk to you again. "
                + "Give a short welcome back in character. "
                + "Maybe reference something from recent conversation if you can.]");
            memory.addModelMessage(reply);
            return reply;
        }
    }

    // save memory when app closes
    public void shutdown() {
        memory.saveHistory();
    }

    // nuke all chat history - used when switching personality or name
    public void wipeMemory() {
        memory.clear();
        memory.saveHistory();
    }

    public Config getConfig() { return config; }
    public String getWaifuName() { return config.getWaifuName(); }
}