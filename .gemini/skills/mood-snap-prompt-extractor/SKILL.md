---
name: mood-snap-prompt-extractor
description: Extracts prompt templates from .idea/project.prompts.xml to individual .md files in the prompts folder. Use when adding or updating prompts in the XML file that need to be synced as files.
---

# Mood Snap Prompt Extractor

This skill automates the extraction of JetBrains IDE prompt templates into Markdown files for better visibility and management within the repository.

## Workflow

1.  **Locate Source**: Ensures `.idea/project.prompts.xml` is present.
2.  **Run Extraction**: Executes the bundled Python script to parse the XML and generate `.md` files in the `prompts/` directory.
3.  **Naming Convention**: Replaces spaces with underscores and ensures a `.md` extension.
4.  **Preservation**: Skips files that already exist to avoid accidental overwrites.

## Usage

Run the extraction script directly:

```bash
python3 scripts/extract.py
```
