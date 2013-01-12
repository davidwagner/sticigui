### toLowercase.py
import sys
import string                                           # string handling package
import re                                               # regular expression package
#
f = open(sys.argv[1], 'r')  
line = f.readline()
while line:
      print line.lower() ,
      line = f.readline()
f.close()
