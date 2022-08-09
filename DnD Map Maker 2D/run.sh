#!/bin/bash

echo DnD Map Maker

java mapMaker2D.DndMapMaker2D && \
read -p "Press Enter to continue . . . " < /dev/tty
