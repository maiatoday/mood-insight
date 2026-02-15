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

| Issue | Branch                       | Activity |
|-------|------------------------------|----------|
| [#1]  | [01-setup]                   |          |
| [#2]  | [02-foundation]              |          |
| [#3]  | [03-ui-scaffold]             |          |
| [#4]  | [04-nav-deep]                |          |
| [#5]  | [05-logic-ref]               |          |
| [#6]  | [06-design-sync]             |          |
| [#7]  | [07-testing]                 |          |
| [#8]  | [08-debug-pr]                |          |
| [#9]  | [09-bonus-notification-docs] |          |


[#1]: https://github.com/maiatoday/mood-snap/issues/1
[#2]: https://github.com/maiatoday/mood-snap/issues/1
[#3]: https://github.com/maiatoday/mood-snap/issues/1
[#4]: https://github.com/maiatoday/mood-snap/issues/1
[#5]: https://github.com/maiatoday/mood-snap/issues/1
[#6]: https://github.com/maiatoday/mood-snap/issues/1
[#7]: https://github.com/maiatoday/mood-snap/issues/1
[#8]: https://github.com/maiatoday/mood-snap/issues/1
[#9]: https://github.com/maiatoday/mood-snap/issues/1
[01-setup]: https://github.com/maiatoday/mood-snap/tree/01-setup
[02-foundation]: https://github.com/maiatoday/mood-snap/tree/02-foundation
[03-ui-scaffold]: https://github.com/maiatoday/mood-snap/tree/03-ui-scaffold
[04-nav-deep]: https://github.com/maiatoday/mood-snap/tree/04-nav-deep
[05-logic-ref]: https://github.com/maiatoday/mood-snap/tree/05-logic-ref
[06-design-sync]: https://github.com/maiatoday/mood-snap/tree/06-design-sync
[07-testing]: https://github.com/maiatoday/mood-snap/tree/07-testing
[08-debug-pr]: https://github.com/maiatoday/mood-snap/tree/08-debug-pr
[09-bonus-notification-docs]: https://github.com/maiatoday/mood-snap/tree/09-bonus-notification-docs