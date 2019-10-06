# Naming Conventions

Coboli uses the following naming conventions:

## File naming

### Class files
Use UpperCamelCase for Class names, just per the Java standard.

### Resource files
Resource files use snake_case.
Prefix the filename with the item type, e.g. menu_ for a menu.

## Variables and Constants
- Use lowerCamelCase for variables
- Use CONSTANTS_CASE for constants

## IDs
IDs use snake_case.
Prefix the filename with the item type, e.g. menu_, text_, etc.
The prefix follows the function of the item rather than its class.
So if using a TextView as a label, use label_. If using a TextView for a big chunk of text output,
use text_. If using a TextView as a borderless button, use button_.