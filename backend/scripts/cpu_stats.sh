#!/bin/bash
awk -v FS=" " '/cpu / {usage=($2+$4)*100/($2+$4+$5)} END {print usage"%"}' /proc/stat
