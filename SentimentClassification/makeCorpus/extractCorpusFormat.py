#!/usr/bin/python2.7
#-*- coding: utf-8 -*-
import sys

#extracts reviews from given corpus (par 2) in csv format and converts it to our format:
#category\tfeature:count f2:count2 ... #label#:negative
#features are only unigrams
#output in par3 file
#category in par1
#e.g.
#

#extracts reviews from a given file, returns a list
def extractReviews(filein):
    f = open(filein,"r")
    lines = f.readlines()
    print "Going to read lines: "+ str(len(lines))
    f.close
    out = []
    nlines = 0
    for line in lines:
        nlines += 1
        splitted = line.split('",')
        if len(splitted) < 8:
            continue
        review = splitted[7].strip().replace('"',"")
        #print review
        title = splitted[6].strip().replace('"',"")
        #print title
        rating = splitted[3].strip().replace('"',"")
        #print rating
        #tuple of review (joined text and title) and rating
        out.append((title+" "+review,rating))
   # print out
    print str(nlines) + " lines read!"
    return out

#formats each review by splitting into unigrams and counting occurrences
#returns a list for reviews
def countFeaturesInList(reviewList):
    out = []
    for review in reviewList:
        featureCounts = []
        reviewtext = review[0]
        #split into unigrams
        unigramsWithPunct = reviewtext.split(" ")
       # print "%d unigrams"%(len(unigramsWithPunct))
        #remove punctuation , ; . ? ! and convert to lower case
        unigrams = []
        for wordPunct in unigramsWithPunct:
            unigrams.append(wordPunct.replace("!","").replace("(","").replace(")","").replace(".","").replace(",","").replace(";","").replace("?","").lower())
        #count features
        for word in unigrams:
            count = unigrams.count(word)
            featureCounts.append(word+":"+str(count))
       # print featureCounts
        out.append((featureCounts,review[1]))
   # print out
    return out

#prints the reviews in final format
def printReviewsToFile(reviewList, outfile):
    out = open(outfile,"w+")
    counter = 0
    for review in reviewList:
        #get label from number of stars (3 stars are not used here)
	try:
	        stars = int(float(review[1]))
	        if stars<3:
	            label = "negative"
	        elif stars>3:
	            label = "positive"
	        elif stars==3:
	            counter += 1
	            continue
	        print >>out, "%s\t%s #label#:%s"%(cat," ".join(review[0]), label)
	except (ValueError):
		counter += 1
    print "Forgot " + str(counter) + " reviews :-("

        

if __name__ == "__main__":
	#category
    cat = sys.argv[1]
    #input file
    filein = sys.argv[2]
    #output file
    fileout = sys.argv[3]
    #list of reviews
    print "Reading " + filein
    reviewList = extractReviews(filein)
    reviewFeatureCounts = countFeaturesInList(reviewList)
    printReviewsToFile(reviewFeatureCounts, fileout)
