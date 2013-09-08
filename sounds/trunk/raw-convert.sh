#!/bin/sh

FFMPEG="ffmpeg1"
FORMAT="u8 -sample_rate 11025 -channels 1"

for name in `ls *.raw | cut -d . -f 1`; do
    ffmpeg1 -f ${FORMAT} -i $name.raw $name.wav
done
