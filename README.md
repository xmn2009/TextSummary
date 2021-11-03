# TextSummary

1. Java application for analyzing text within a file.
2. Extend the test set in a black-box fashion to debug the alternative implementation
3. Agile, test-driven process

## Insturctions:

* <filename>: required
* -c <string>[integer]: if specified, the  textsummary utility will add the number of times the provided <string> appears in the line to the start of each line in the file. 
* -d [integer]: if specified, the textsummary utility will output the words (where a word is any sequence of alphanumeric characters)  which are duplicated the most times throughout the file, and the number of times each appears. 
* -l|-s [integer]: if specified, the textsummary utility will keep only the longest(-l) or shortest(-s) [integer](a required positive integer) lines in the file. 
* -u: if specified, the textsummary utility will keep only the first instance of any unique word (where a word is any sequence of alphanumeric characters) in the file.  

## Example 1:
"file.txt" content:
dog bird cat cat
cat dog fish

In terminal, enter "textsummary -d 2 -c “d” 2 file.txt"

Result file:
2 dog bird cat cat

Output: “cat 3 dog 2”

## Example 2:
"file.txt" content:
dog bird cat cat
cat dog fish cat
dog
bird fish

In terminal, enter "textsummary -d -s 2 file1.txt"

Result file:
dog
bird fish

Outputs “cat 4”

