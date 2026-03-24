#!/bin/bash

# A "helpful" shell utility to extract log files from the RIO

DOWNLOAD_PATH=~/extracted_`date +%Y-%m-%d_%H-%M`

echo "Saving logs to $DOWNLOAD_PATH"

mkdir -p $DOWNLOAD_PATH

scp -r lvuser@10.34.14.2:/media/sda1/logs/*.wpilog $DOWNLOAD_PATH

# BLOW IT UP!!!!!!!

ssh -t admin@10.34.14.2 "sudo rm -rf /media/sda1/logs/*.wpilog"
