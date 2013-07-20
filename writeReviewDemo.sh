#!/bin/bash

#this script provides a demo of writereview 
#your review is read from the input text given followed by [ENTER]

echo "Software Project 2013 Uni Heidelberg, Group 1: Sentiment Classification"
echo "Demo"
echo ""
echo "Type your review, followed by [ENTER]"
read text
newtext=$(echo $text | sed 's/ /\\ /g' | sed 's/!/\\!/g')
ant -Darg1="+$newtext+" WriteReview > outmessage_ant
echo ""
sed -n '10p' outmessage_ant | sed 's/\[echo\] your review:/your review:/g'
echo ""
sed -n '11p' outmessage_ant | sed 's/\[java\]/detected sentiment:/g' 
