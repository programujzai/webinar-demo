Review the current conversation. If you identify universal insights (architecture, conventions, dependencies, testing rules, terminology, pitfalls, or durable project decisions), update CLAUDE.md.

Rules for updating:

-Only include project-wide, recurring, or architectural insights — skip one-off debugging or ephemeral notes.

-Preserve existing structure and tone: short imperative rules, markdown sections.

-If no section fits, use one of:

-Pitfalls & Gotchas (known recurring problems).

-Domain Glossary (shared terms, business concepts).

-Open Questions (unresolved issues).

-Deduplicate existing entries. If clarifying an old one, edit it instead of duplicating.

-Keep lines under ~100 characters.

-Never add secrets, PII, or private URLs.

Output format:

Show me a unified diff of CLAUDE.md (or say “No universal insights found”).

If the change is >30 lines or alters Project Overview, pause and ask for confirmation.