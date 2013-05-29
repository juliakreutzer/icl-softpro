#-*- coding: utf-8 -*-
import sys

#e.g. python concatFiles.py ../../corpus/books.train.corpus ../../corpus/dvd.train.corpus ../../corpus/electronics.train.corpus ../../corpus/kitchen.train.corpus ../../corpus/all.train.corpus


#concatenates given files to new file which is last in parameter list
#separates files by special characters "<>"
if __name__ == "__main__":
	outfile = []
	for filename in sys.argv[1:len(sys.argv)-1]: #last in list is at index filesnr-1, last concatenated at filesnr-2, slices is esclusive at upper end
		print filename
		f = open(filename,"r")
		outfile.append(f.readline()) #files do only contain one line
		f.close()
	print len(outfile)
	h = open(sys.argv[len(sys.argv)-1], "w") #open output file
	h.write(" <> ".join(outfile))
	

