package com.a2r.animeassistant.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// stores chat so she actually remembers stuff between sessions
// saves everything to a json file in the config folder
public class ChatMemory {
    // each msg is [role, text] - role is "user" or "model"
    private final List<String[]> messages = new ArrayList<>();

    // how many msgs to send to gemini for context
    // too many = slow and eats tokens, too few = she forgets
    private static final int CONTEXT_WINDOW = 20;

    private final Path historyFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ChatMemory(Path configDir) {
        this.historyFile = configDir.resolve("chat_history.json");
        loadHistory();
    }

    public void addUserMessage(String text) {
        messages.add(new String[]{"user", text});
    }

    public void addModelMessage(String text) {
        messages.add(new String[]{"model", text});
    }

    // grab recent msgs to send as context
    public List<String[]> getRecentMessages() {
        int start = Math.max(0, messages.size() - CONTEXT_WINDOW);
        return new ArrayList<>(messages.subList(start, messages.size()));
    }

    public int getTotalMessages() {
        return messages.size();
    }

    // load old chats from disk
    private void loadHistory() {
        try {
            if (Files.exists(historyFile)) {
                String json = Files.readString(historyFile);
                List<String[]> loaded = gson.fromJson(json,
                        new TypeToken<List<String[]>>(){}.getType());
                if (loaded != null) {
                    messages.addAll(loaded);
                    System.out.println("Loaded " + loaded.size() + " messages from history");
                }
            }
        } catch (Exception e) {
            System.err.println("couldnt load chat history: " + e.getMessage());
        }
    }

    // save chats to disk - caps at 200 msgs so the file doesnt get huge
    public void saveHistory() {
        try {
            int start = Math.max(0, messages.size() - 200);
            List<String[]> toSave = messages.subList(start, messages.size());

            String json = gson.toJson(toSave);
            Files.writeString(historyFile, json);
            System.out.println("Saved " + toSave.size() + " messages to history");
        } catch (IOException e) {
            System.err.println("couldnt save chat history: " + e.getMessage());
        }
    }

    public void clear() {
        messages.clear();
    }
}
