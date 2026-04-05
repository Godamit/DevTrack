#!/bin/bash
free -m | awk 'NR==2{printf "%s/%sMB\n",$3,$2}'
