#!/bin/bash

echo DnD Map Parser

java mapParser.DndMapParser && \
read -p "Press Enter to continue . . . " < /dev/tty
