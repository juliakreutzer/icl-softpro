#-*- coding:utf-8 -*-

#author: Julia

import sys, random

#splits corpus files for given category into test, training and development set
if __name__ == "__main__":
	cat = sys.argv[1] #command line parameter is category name
	
	pos = open(cat+"/positive.review","r").readlines()
	neg = open(cat+"/negative.review","r").readlines()

	print str(len(pos))+" positive reviews read"
	print str(len(neg))+" negative reviews read"	

	train_pos=pos[:600] #end is excluded, beginning included 
	print str(len(train_pos))+" positive reviews for training"
	test_pos=pos[600:800]
	print str(len(test_pos))+" positive reviews for testing"	
	dev_pos=pos[800:]
	print str(len(dev_pos))+" positive reviews for dev"

	train_neg=neg[:600]
	print str(len(train_neg))+" negative reviews for training"
	test_neg=neg[600:800]
	print str(len(test_neg))+" negative reviews for testing"	
	dev_neg=neg[800:]
	print str(len(dev_neg))+" negative reviews for dev"

	train=(train_neg+train_pos)[:]
	print str(len(train))+" training reviews"
	test=(test_neg+test_pos)[:]
	print str(len(test))+" test reviews"
	dev=(dev_neg+dev_pos)[:]
	print str(len(dev))+" dev reviews"

	random.shuffle(train) #shuffle lists so that pos and neg reviews appear in random order
	random.shuffle(test)
	random.shuffle(dev)

	open(cat+".train.corpus.final","w").write(cat+"\t"+" <> ".join(train).replace("\n",""))
	open(cat+".test.corpus.final","w").write(cat+"\t"+" <> ".join(test).replace("\n",""))
	open(cat+".dev.corpus.final","w").write(cat+"\t"+" <> ".join(dev).replace("\n",""))
	

