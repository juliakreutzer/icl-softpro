#!/usr/bin/python

import os, sys

def readFile(filename):
	f = open(filename, 'r')
	print "Reading " + filename
	fileContent = f.readlines()
	f.close()
	return fileContent

def getFolderContent(folder):
	files = []
	count = 0
	for filenames in os.walk(folder):
		if(count==0):
			files = filenames[2]
			count += 1
	return files

def stringToCorpus(content, numTrain, numTest, label):
	numLines = len(content)
	print "Number of lines to process: " + str(numLines)
	realNumTrain = int(numLines * (float(numTrain)/100))
	realNumTest = numLines - realNumTrain
	print "Number of Samples:"
	print "  Train: " + str(realNumTrain)
	print "  Test:  " + str(realNumTest)
	lineCount = 0
	lineTest = label + "\t"
	lineTrain = label + "\t"
	isFirstTrain = True
	isFirstTest = True

	for line in content:
		lineCount += 1
		if (lineCount < realNumTrain):
			if (isFirstTrain):
				lineTrain += line.rstrip('\n')
				isFirstTrain = False
			else:
				lineTrain += " <> " + line.rstrip('\n')
		else:
			if (isFirstTest):
				lineTest += line.rstrip('\n')
				isFirstTest = False
			else:
				lineTest += " <> " + line.rstrip('\n')

	return { "train": lineTrain, "test": lineTest }

def writeCorpus(filename, textCorpus):
	f = open(filename, "w")
	f.write(textCorpus)
	f.close()

if __name__ == "__main__":

	# Percentage of each file to be  used for test and training corpus
	numTrain = 60
	numTest = 100-numTrain

	# Path to corpus files
	corpus = sys.argv[1]

	
	content = []
	print "Looking for content in '" + corpus + "'"
	for filename in getFolderContent(corpus):
		content.extend( readFile(corpus + '/' + filename) )
	
	corpora = stringToCorpus(content, numTrain, numTest, corpus)
	writeCorpus(corpus + '.test.corpus', corpora['test'])
	writeCorpus(corpus + '.train.corpus', corpora['train'])
	
