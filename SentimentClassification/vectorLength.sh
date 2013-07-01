#!/bin/bash

#counts the number of features for each weight vector file
wvfiles=$(ls weightVectors)

allcounts=""

for wv in ${wvfiles[@]}
do
count=$(cat weightVectors/$wv | sed 's/\s/\n/g' | wc -w )
counts=$wv"	"$count
echo $counts
done


