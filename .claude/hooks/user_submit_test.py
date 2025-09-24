#!/usr/bin/env python3
"""
UserPromptSubmit Hook - Todo List Application
Enhances user prompts with project context and shortcuts
"""

import os
import json
import sys
from datetime import datetime


def main():
    try:
        # Read JSON input from stdin
        input_data = json.loads(sys.stdin.read())

        # Extract prompt from input
        prompt = input_data.get('prompt', '')

        if not prompt:
            sys.exit(0)

        # Add project context automatically
        current_dir = os.getcwd()
        project_context = f"""
Project Context:
- Backend: Spring Boot + Kotlin in /be
- Frontend: Next.js + TypeScript in /fe
- Database: PostgreSQL via docker-compose
- Current directory: {current_dir}
"""

        # Handle custom shortcuts
        shortcuts = {
            '%backend': 'Focus on backend development in /be directory using Spring Boot and Kotlin.',
            '%frontend': 'Focus on frontend development in /fe directory using Next.js and TypeScript.',
            '%fullstack': 'This task involves both backend (/be) and frontend (/fe). Please split into separate tasks.',
        }

        # Replace shortcuts in prompt
        enhanced_prompt = prompt
        for shortcut, expansion in shortcuts.items():
            if shortcut in enhanced_prompt:
                enhanced_prompt = enhanced_prompt.replace(shortcut, expansion)

        # For UserPromptSubmit hooks, stdout is added as context to Claude
        print(project_context)
        if enhanced_prompt != prompt:
            print(f"Enhanced prompt: {enhanced_prompt}")

        # Success - prompt will be processed with added context
        sys.exit(0)

    except json.JSONDecodeError:
        # Handle JSON decode errors gracefully
        sys.exit(0)
    except Exception:
        # Handle any other errors gracefully
        sys.exit(0)


if __name__ == '__main__':
    main()