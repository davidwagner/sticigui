// script sticiGuiSetTopics: 
// copyright 2009. P.B. Stark, statistics.berkeley.edu/~stark
// All rights reserved.
// Last modified 1/9/2009

var sticiGuiSetTopics = new Array;

for (var j = 0; j < chapterAssignmentXref.length; j++) {
       sticiGuiSetTopics[j] = [
                               '<a href="' + sticiRelPath + '/Text/' + chapterAssignment[j][0] +
                                   '.htm" target="_new">' + 
                                    chapterTitles[chapterNumber[chapterAssignment[j][0]]][0] + '</a>',
                               assignmentTitles[assignmentNumber[chapterAssignment[j][1]]][1],
                               chapterAssignment[j][1]
                              ];        
}
