#!/usr/bin/python

import os, sys

def readFile (filename):
	f = open (filename, 'r')
	print "Reading " + filename
	fileContent = f.readlines ()
	f.close ()
	return fileContent 

def getFolderContent (folder):
	files = []
	count = 0
	for filenames in os.walk (folder):
		if(count==0):
			files = filenames[2]
			count += 1
	return files
	
def countDimensions (content):
	dimensions = []
	for line in content:
		category, value = line.split ("\t")
		value = value.replace ('<>','').split (' ')
		print "Number tokens: " + str (len (value))
		for featureValue in value:
			if (featureValue!=''):
				feature, muell = featureValue.split (':')
				if (feature not in dimensions):
					dimensions.append (feature)
		print "Dimensions up to now: " + str (len (dimensions))
	return len(dimensions)


if __name__ == "__main__":
	folder = sys.argv[1]
	print "Reading " + folder
	content = []
	for filename in getFolderContent (folder):
		content.extend (readFile (folder + '/' + filename))
	print "Counting dimensions..."
	print "Number of dimensions: " + str (countDimensions (content))
	
	
