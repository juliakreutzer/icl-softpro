import sys
from random import shuffle

#author: julia

#e.g. python createSmallPoolCorpus.py corpus_final_formatted/books.test.corpus.final.formatted corpus_final_formatted/dvd.test.corpus.final.formatted corpus_final_formatted/electronics.test.corpus.final.formatted corpus_final_formatted/kitchen.test.corpus.final.formatted small.all.test.corpus.final.formatted


#concatenates given files to new file which is last in parameter list
#concatenated files are only included to a certain percentage
#small Pool should only be as big as single corpus files for single training
#-> 1200 instances for training, 400 for development, 400 for testing

if __name__ == "__main__":
		outfile = []
		p = 0 #part of each file that should be extracted
		if "train" in sys.argv[-1]:
			l = 1200
		else:
			l = 400
		p = int(float(l/(len(sys.argv)-2)))
		print "p: "+str(p)

		for filename in sys.argv[1:len(sys.argv)-1]: #last in list is at index filesnr-1, last concatenated at filesnr-2, slices is exclusive at upper end
			print filename
			f = open(filename,"r")
			outfile.extend(f.readlines()[0:p]) #files do contain many lines	
			f.close()
			print len(outfile)
		shuffle(outfile)
		h = open(sys.argv[len(sys.argv)-1], "w") #open output file
		h.write("".join(outfile))

