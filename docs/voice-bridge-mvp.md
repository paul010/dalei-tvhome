# Planned voice bridge

Not connected. Preserve vendor voice key. Verify recognition provider and audio destination before any invocation. Separate user-started recording, transcript review/edit/cancel and explicit send; stopping recording never sends. No empty submission or continuous recording.

A future authenticated bridge may submit a restricted ask, read only its status and request cancellation. Never expose raw Codex app-server RPC, command/exec, shell, arbitrary cwd/thread/model permissions. Credentials remain on the computer; internal stdio, execution tools/MCP/connectors explicitly disabled. Read-only mode alone is not a shell prohibition. Use idempotent request IDs, query uncertain results before retry, and require cancellation acknowledgment. LAN/TLS, pairing, provider and audio permissions require specific approval.
