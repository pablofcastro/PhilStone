#!/bin/bash


function clean_up()
{
    echo "Cleaning up...\n"
    for proc in `jobs -p`
    do
        echo "killing proc " + $proc
        kill $proc
    done
    exit
}

trap clean_up SIGINT SIGHUP SIGTERM

cd output/;

for dot_file in `ls *.dot`; do
    dot -Tjpeg $dot_file > $dot_file.jpeg
done

for dir in `ls -d */`; do
    cd $dir;
    for dot_file in `ls *.dot`; do
        dot -Tjpeg $dot_file > $dot_file.jpeg
    done
    cd ..
done

echo "dot2jpeg done."
