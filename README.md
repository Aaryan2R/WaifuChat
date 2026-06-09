# 🌸 WaifuChat ~ your AI waifu companion

> *"It's not like I wanted to talk to you or anything... baka!"*

a desktop waifu companion app built in java. she lives on your desktop, has her own personality, remembers your conversations, and actually talks back with real feelings. powered by google gemini.

this started as a college project and kinda got out of hand lol

---

## ✨ what it does

- 💬 **chat with your waifu** — she responds in character with real personality and emotions
- 🧠 **she remembers stuff** — conversation history is saved between sessions
- 🎭 **9 personality types** — pick the one that matches your taste (see below)
- 🖼️ **custom waifu image** — use your own waifu with mouth animation support
- 🗣️ **mouth animation** — she "talks" by alternating between open/closed mouth images
- ⚙️ **easy config** — everything is in one simple config file with instructions

---

## 🎭 personality types

| Type | Vibe | Example Characters |
|------|------|--------------------|
| **tsundere** | cold outside, warm inside. "b-baka!" | Taiga, Asuka |
| **yandere** | sweet but obsessive. "youre mine forever~" | Yuno Gasai |
| **kuudere** | emotionally flat. "...I see." | Rei Ayanami, Violet Evergarden |
| **dandere** | painfully shy. "I-I... um..." | Komi Shouko, Hinata |
| **deredere** | openly loving. pure sunshine | Rem, Tohru Honda |
| **kamidere** | god complex. "you should be grateful" | Light Yagami energy |
| **gyaru** | trendy, social, slang queen | Marin Kitagawa |
| **oneesan** | mature caring big sis. "ara ara~" | Akeno vibes |
| **tomboy** | athletic, blunt, bad with feelings | Tomo-chan |

each personality has unique speech patterns, reactions to compliments/insults/flirting, and their own likes/dislikes. they actually feel different to talk to.

---

## 🚀 setup

### what you need
- **Java 17+** ([download here](https://www.oracle.com/java/technologies/downloads/))
- **Gemini API key** (free) — get one at [aistudio.google.com/apikey](https://aistudio.google.com/apikey)

### option 1: just run it (no maven needed)
1. grab the `anime-assistant-1.0-SNAPSHOT.jar` from releases (or build it yourself)
2. put it in a folder with `run.bat`
3. double-click `run.bat`
4. add your API key in Settings

### option 2: build from source
```bash
# you need maven for this
mvn clean package

# then run
java -jar target/anime-assistant-1.0-SNAPSHOT.jar

# or just use run.bat
```

### first time setup
1. app creates a config file at `~/.animeassistant/config.properties`
2. click **Settings** (top right) and paste your Gemini API key
3. pick a personality
4. start chatting!

---

## 🖼️ custom waifu image

you can replace the default waifu with your own image!

**for mouth animation** (recommended):
1. prepare 2 nearly identical images of your waifu
   - one with mouth **closed** (resting face)
   - one with mouth **open** (talking)
2. set both paths in the config file:
   ```
   waifu_image_closed=C:/path/to/your/waifu_closed.png
   waifu_image_open=C:/path/to/your/waifu_open.png
   ```

**or just use 1 image** — set only `waifu_image_closed` and leave `waifu_image_open` empty. she'll display but without the talking animation.

---

## 📁 project structure

```
src/main/java/com/a2r/animeassistant/
├── App.java              # entry point
├── api/
│   └── GeminiClient.java # talks to gemini api, has all personality prompts
├── core/
│   ├── AssistantCore.java # connects everything together
│   ├── ChatMemory.java    # saves/loads chat history
│   └── Config.java        # settings and config file management
├── theme/
│   └── Theme.java         # ui colors
└── ui/
    └── MainWindow.java    # the actual window with chat and waifu display
```

---

## ⚠️ notes

- free tier has **500 requests/day** on `gemini-3.1-flash-lite` — should be plenty for normal chatting
- if you get quota errors, check your model in config. some models have as low as 20 req/day
- chat history is saved at `~/.animeassistant/chat_history.json`
- she remembers the last 20 messages for context (keeps responses fast)
- the config file has all the options documented with descriptions

---

## 🛠️ tech stack

- **Java 17** + Swing (ui)
- **Google Gemini API** (the brain)
- **Gson** (json parsing)
- **Maven** (build)

---

## 📝 license

do whatever you want with it lol.

---

*made with love and too much free time ♡*
