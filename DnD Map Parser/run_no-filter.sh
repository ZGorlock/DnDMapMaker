#!/bin/bash

echo DnD Map Parser - No Filter

java mapParser.DndMapParser _ 0 && \
read -p "Press Enter to continue . . . " < /dev/tty
