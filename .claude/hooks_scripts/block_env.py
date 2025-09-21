import sys
import json


def main():
    chunks = []
    for chunk in sys.stdin:
        chunks.append(chunk)

    tool_args = json.loads("".join(chunks))

    # readPath is the path to the file that Claude is trying to read
    read_path = tool_args.get("tool_input", {}).get("file_path") or tool_args.get("tool_input",
                                                                                  {}).get(
        "path") or ""

    # Ensure Claude isn't trying to read the .env file
    if ".env" in read_path:
        print("You cannot read the .env file read example.env instead", file=sys.stderr)
        sys.exit(2)
    else:
        sys.exit(0)


main()
