#!/bin/bash

log_yellow() {
    echo -e "\033[33m$(date +%Y-%m-%dT%H:%M:%S) -------- $1\033[0m"
}

log_green() {
    echo -e "\033[32m$(date +%Y-%m-%dT%H:%M:%S) -------- $1\033[0m"
}

log_red() {
    echo -e "\033[31m$(date +%Y-%m-%dT%H:%M:%S) -------- $1\033[0m"
}

error_exit() {
    echo -e "\033[31m$(date +%Y-%m-%dT%H:%M:%S) -------- ERROR: $1\033[0m"
    exit 1
}