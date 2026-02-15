# Mood Snap
This is a sample project to explore [Gemini in Android](https://developer.android.com/studio/gemini/overview). The app is a simple mood tracker which can be built by completing the steps as described in the Issues 1 - 9.
There is a matching branch which is the starting point for each Issue. 

```mermaid
graph LR
    %% Core Nodes
    Start((Workshop: Mood Snap)) --> Config[1 - Configuration]
    Start --> Build[2 - Build & Setup]
    Start --> Dev[3 - Feature Development]
    Start --> QA[4 - Quality & Ops]

    %% Configuration
    Config --> Rules[GEMINI.md: Enforce Standards]
    Config --> Privacy[.aiexclude: Security]
    Config --> Models[LLM Selection: Agent Mode]

    %% Build & Setup
    Build --> Sync[Agent Mode: Fix Build/AGP]
    Build --> Logcat[Logcat: Explain Crash]

    %% Feature Development
    Dev --> UI[UI: Multimodal & Figma MCP]
    Dev --> Nav[Nav: Navigation 3 & Deeplinks]
    Dev --> Logic[Logic: Algorithm Deep-dive]
    Dev --> Refactor[Refactor: JUnit 4 to 5]
    Dev --> Bonus[Bonus: Add a notification]

    %% Quality & Ops
    QA --> Testing[Journeys: Natural Language E2E]
    QA --> Debug[Agent Mode: Device Debugging]
    QA --> PR[Changes Drawer: AI PR Writing]

    %% Styling
    style Start fill:#f9f,stroke:#333,stroke-width:4px
    style Rules fill:#dfd,stroke:#333
    style Testing fill:#ddf,stroke:#333
    style Sync fill:#fdd,stroke:#333
```

| Issue | Branch | Activity |
|-------|--------|----------|
| [#1]  |        |          |
| [#2]  |        |          |
| [#3]  |        |          |
| [#4]  |        |          |
| [#5]  |        |          |
| [#6]  |        |          |
| [#7]  |        |          |
| [#8]  |        |          |
| [#9]  |        |          |


[#1]: https://github.com/maiatoday/mood-snap/issues/1
[#2]: https://github.com/maiatoday/mood-snap/issues/1
[#3]: https://github.com/maiatoday/mood-snap/issues/1
[#4]: https://github.com/maiatoday/mood-snap/issues/1
[#5]: https://github.com/maiatoday/mood-snap/issues/1
[#6]: https://github.com/maiatoday/mood-snap/issues/1
[#7]: https://github.com/maiatoday/mood-snap/issues/1
[#8]: https://github.com/maiatoday/mood-snap/issues/1
[#9]: https://github.com/maiatoday/mood-snap/issues/1
