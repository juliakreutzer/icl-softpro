#-*- coding: utf-8 -*-
import sys

#extracts reviews from given corpus (par 1) in csv format and converts it to our format:
#category\tfeature:count f2:count2 ... #label#:negative
#features are only unigrams
#output in par2 file

#extracts reviews from a given file, returns a list
def extractReviews(filein):
    f = open(filein,"r")
    lines = f.readlines()
    out = []
    for line in lines:
        splitted = line.split('",')
        if len(splitted) < 8:
            break
        review = splitted[7].strip().replace('"',"")
        #print review
        title = splitted[6].strip().replace('"',"")
        #print title
        rating = splitted[3].strip().replace('"',"")
        #print rating
        #tuple of review (joined text and title) and rating
        out.append((title+" "+review,rating))
   # print out
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
        #remove punctuation , ; . ? !
        unigrams = []
        for wordPunct in unigramsWithPunct:
            unigrams.append(wordPunct.replace("!","").replace(".","").replace(",","").replace(";","").replace("?",""))
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
    for review in reviewList:
        #get label from number of stars (3 stars are not used here)
        stars = int(float(review[1]))
        if stars<3:
            label = -1
        elif stars>3:
            label = +1 
        elif stars==3:
            continue
        print >>out, "snacks\t%s #label#:%d"%(" ".join(review[0]), label)
        

if __name__ == "__main__":
    #input file
    filein = sys.argv[1]
    #output file
    fileout = sys.argv[2]
    #list of reviews
    reviewList = extractReviews(filein)
    reviewFeatureCounts = countFeaturesInList(reviewList)
    printReviewsToFile(reviewFeatureCounts, fileout)
