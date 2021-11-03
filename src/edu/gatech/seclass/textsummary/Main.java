package edu.gatech.seclass.textsummary;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


public class
Main {

/*
 Georgia Tech CS6300.
 */

    public static void main(String[] args) {
        // Empty Skeleton Method
        try {

            if(args.length == 0){
                throw new IllegalArgumentException();
            }
            //if args != 0
            else {

                if (isOPTmistake(args)) {
                    throw new IllegalArgumentException();

                }
                else if ((isOPTexist(args, "-s") && isOPTexist(args, "-l"))
                        ||(isOPTexist(args, "-s") && isOPTexist(args, "-u")
                        ||(isOPTexist(args, "-l") && isOPTexist(args, "-u")))) {
                    throw new IllegalArgumentException();
                }
                else if(!isFileExist(args)){
                    throw new NullPointerException();
                }
                else {
                    //get the file name
                    String fileName = args[args.length - 1];
                    String fileContent = readTXTFile(getInputfile(fileName));

                    if(fileContent.isEmpty()){
                        throw new IOException();
                    }
                    else if(args.length == 1 ){
                        //simply print on the screen
                        System.out.print(lAndsResult("-l",fileContent,1));
                        writeTXTFile(getInputfile(fileName),fileContent);
                    }
                    else {
                        //handle -d opt first
                        handleDFirst(args, fileName);

                        //handle -c or -l|-s|-u
                        handleCorLorS(args, fileName);

                        //handle -u opt last
                        handleULast(args,fileName);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            usage();
        } catch (NullPointerException e){
            //usage();
            System.err.println("File Not Found");
        } catch (IOException e){
            usage();
        }
    }



    private static void handleULast(String[] args, String fileName) throws  IOException{
        String fileContent;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-u")){
                fileContent = readTXTFile(getInputfile(fileName));
                writeTXTFile(getInputfile(fileName), uResult(fileContent));
                System.out.println("");
            }
        }
    }

    private static void handleCorLorS(String[] args, String fileName)throws IOException {

        //make sure the file is re-read every time, since the file is changing
        String fileContent;

        for (int i = 0; i < args.length-1; i++) {
            if (args[i].equalsIgnoreCase("-c")) {

                //make sure the file is re-read every time, since the file is changing
                fileContent = readTXTFile(getInputfile(fileName));

                if(isNumeric(args[i+2]) ){
                    writeTXTFile(getInputfile(fileName),
                            cResult(fileContent, args[i+1],args[i+2]));
                }
                //if the second parameter is no integer, or there is no second parameter for -c
                else {
                    writeTXTFile(getInputfile(fileName),
                            cResult(fileContent, args[i + 1],
                                    /*String.valueOf(getInputfile(fileName).length()))*/ "0"));
                }

            }
            //handling -l|-s
            else if (args[i].equalsIgnoreCase("-l")
                    || args[i].equalsIgnoreCase("-s")) {
                String command = args[i];

                //boolean isNumeric = isNumeric(args[i+1]);
                if(isNumeric(args[i+1])){

                    //make sure the file is re-read every time, since the file is changing
                    fileContent = readTXTFile(getInputfile(fileName));

                    int lORsParemeter = Integer.parseInt(args[i + 1]);
                    int totalLineCount = sepTxtByLineList(fileContent).size();

                    if (lORsParemeter > 0 && lORsParemeter <= totalLineCount) {
                        writeTXTFile(getInputfile(fileName), lAndsResult(command,fileContent,lORsParemeter));
                    }
                    //if the -l|-s parameter is minus integer
                    else if(lORsParemeter > 0 && lORsParemeter > totalLineCount){
                        //System.err.println("The specified parameter is too large");
                        usage();
                    }
                    else {
                        //System.err.println("Invalid -l|-s parameter");
                        usage();
                    }
                }
            }
        }
    }

    private static void handleDFirst(String[] args, String fileName) throws IOException{

        String fileContent = readTXTFile(getInputfile(fileName));

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-d")) {

                boolean isNumeric = isNumeric(args[i+1]);
                if (isNumeric) {
                    int dParameter = Integer.parseInt(args[i + 1]);
                    //int totalWordCount = sepTXTbyWordAndRemoveSymbols(fileContent).size();

                    if (dParameter > 0
                        /*&& !args[i+1].matches("[0-9]+\\.[0-9]+")
                        && dParameter<= totalWordCount*/) {
                        System.out.print(dResult(fileContent, dParameter));
                    }
                    else {
                        //throw new IllegalArgumentException();
                        //System.err.println("Invalid -d parameter");
                        usage();
                    }

                } else {
                    System.out.print(dResult(fileContent, 1));
                }
            }
        }
    }

    private static boolean isFileExist(String[] args){
        boolean result = true;
        String fileName = args[args.length - 1];

        if(!getInputfile(fileName).exists()){
            result = false;
        }
        return result;
    }

    private static boolean isOPTexist(String[] args, String OPTstr){
        boolean result = false;
        for (String element:args) {
            if(element.equalsIgnoreCase(OPTstr) /*&& element.matches("^-[d|c|l|s|D|C|L|S]$")*/){
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean isOPTmistake(String[] args){
        boolean result = false;
        String arg = "";
        for (int i=0; i<args.length; i++) {
            arg = args[i];
            //only exam the element that start with "-"
            if(arg.matches("^-[a-zA-Z0-9]$")){

                //if any args doesn't match -d -c -l -s including capital letters
                // or number, break the program
                if(!arg.matches("^-[d|c|l|s|u|D|C|L|S|U|0-9]$")){
                    result = true;
                    break;
                }
                //when the arg matches the patter, check if the parameter meets the requirement
                else {
                    if(arg.equalsIgnoreCase("-d")){

                        //if not file name following -d and the parameter isn't a integer and it isn't other OPT
                        if((!isFileNameFormatCorrect(args[i+1])
                                && !isNumeric(args[i+1])
                                && args[i+1].matches("[^-[c|l|s|u|C|L|S|U]$]"))){
                            result = true;
                            break;
                        }
                        else if((args[i+1].matches("[0-9]+\\.[0-9]+"))){
                            result = true;
                            break;
                        }
                        //test if -d has more than two parameter
                        else {
                            if (i + 2 < args.length
                                    && !isFileNameFormatCorrect(args[i + 2]) //if is isn't a file name, that is ok
                                    && isNumeric(args[i+1]) //if it i+1 is an integer but i+2 not other OPT name
                                    && args[i + 2].matches("[^-[c|l|s|u|C|L|S|U]$]")) {
                                result = true;
                                break;
                            }
                        }
                    }

                    //check -l |-s format
                    else if(arg.equalsIgnoreCase("-l")
                                ||arg.equalsIgnoreCase("-s")){
                        //if the following string is not integer
                        if(!isNumeric(args[i+1])){
                            result = true;
                            break;
                        }
                        //test 2nd parameter after -l|-s
                        else {
                            if (i + 2 < args.length
                                    && !isFileNameFormatCorrect(args[i + 2]) //if is isn't a file name, that is ok
                                    //it could be other OPT name
                                    && args[i + 2].matches("[^-[d|c|u|D|C|U]$]")) {
                                result = true;
                                break;
                            }
                        }
                    }
                    //check -c format
                    else if(arg.equalsIgnoreCase("-c")){

                       if(i == args.length -2 && isFileNameFormatCorrect(args[i+1])){
                            result = true;
                            break;
                        }
                        //test 2nd pameter after -c
                       else if (i + 2 < args.length){
                                //the second c parameter could be filename, or other command
                           if(!isFileNameFormatCorrect(args[i + 2])
                                    && args[i + 2].matches("[^-[d|l|s|u|D|L|S|U]$]")
                                        && !isNumeric(args[i + 2])) {
                                    result = true;
                                    break;
                                }
                           else if(isNumeric(args[i + 2])){
                               if(Integer.parseInt(args[i+2]) < 0){
                                        result = true;
                                        break;
                                    }
                                    //when reasch here, the format for -d is almost right,
                                    //then the parameter following is the last step
                               else if(i + 3 < args.length){
                                        if(!isFileNameFormatCorrect(args[i + 3])
                                                && args[i + 3].matches("[^-[d|l|s|u|D|L|S|U]$]")) {
                                            result = true;
                                            break;
                                        }
                                    }
                           }
                           else if(args[i+2].matches("[0-9]+\\.[0-9]+")){
                                    result = true;
                                    break;
                           }
                           else {
                               i += 1;
                           }
                       }
                    }

                    else if(arg.equalsIgnoreCase("-u")){

                        //if the following string is not integer
                        if(!isFileNameFormatCorrect(args[i+1])
                            && args[i + 1].matches("[^-[d|c|D|C]$]")){
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean isFileNameFormatCorrect(String fileName/*String[] args*/){
        boolean result = false;
        //String fileName = args[args.length -1];
        if(fileName.matches("^.*.txt$")){
            result = true;
        }
        return result;
    }

    private static File getInputfile(String fileName){

        File inputFile = new File(fileName);
        return inputFile;
    }

    //read the file
    private static String readTXTFile(File fileName) throws IOException{

        String fileContent = null;

        FileInputStream fileInputStream = new FileInputStream(fileName);
        byte[] data = new byte[(int) fileName.length()];
        fileInputStream.read(data);
        fileInputStream.close();

        fileContent = new String(data,"UTF-8");
        return fileContent;
    }

    private static void writeTXTFile(File inputFile, String outputStr){
        try{

            FileWriter writer = new FileWriter(inputFile.getPath(), false);
            writer.write(outputStr);
            writer.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static String cResult(String inputStr, String providedStr, String secondParameter /*, File outputFile*/){

        List<String> txtList = sepTxtByLineList(inputStr);

        int count ;
        String result = "";

        for(String str: txtList){

            //calculate how many times a provided string appear in a String
            count = (str.length() - str.replaceAll(Pattern.quote(providedStr), "").length()) / providedStr.length();

            //only keep the line that have the count more than the second parameter
            if(count >= Integer.parseInt(secondParameter)){

                //result += count + " " + str + System.lineSeparator();
                result += count + " " + str;
            }
        }
        return result.trim();
    }

    private static String dResult(String fileContent, int num){

        List<String> wordList = sepTXTbyWordAndRemoveSymbols(fileContent);
        List<String> noDupList = new ArrayList<>();
        //Map<String, Integer> wordOccu = new TreeMap<>();
        Map<String, Integer> wordOccu = new LinkedHashMap<>();


        //generate a list without duplicate word
        for(String word: wordList){
            if(!noDupList.contains(word)){
                noDupList.add(word);
            }
        }

        //count the element occurrences in noDupList from wrodList,
        //then put the element and occurrences into a TreeMap
        int frequency ;
        for(String word:noDupList){
            frequency = Collections.frequency(wordList, word);
            wordOccu.put(word,frequency);
        }


        String output = "";
        if(wordOccu.size()>=num){
            output = dSortMap(wordOccu,num);
        }
        else {
            output = dSortMap(wordOccu,wordOccu.size());
            //System.err.println("-d parameter is larger than the total word number");
            usage();
        }
        return  output.trim();
    }

    public static String dSortMap(Map<String,Integer> oldmap,int limit) {
        ArrayList<Map.Entry<String,Integer>>  list = new ArrayList<>(oldmap.entrySet());

        Collections.sort(list,(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) -> {
            return o2.getValue() - o1.getValue();
        });

        String str = "";
        for(int i = 0; i < limit; i++) {
            str += list.get(i).getKey() + " " +list.get(i).getValue() + " ";
        }
        return str;
    }

    private static String lAndsResult(String command, String inputStr, int num /*, File outputFile*/){

        //put the String into a list, each line of String as an element
        List<String> txtList = sepTxtByLineList(inputStr);
        Map<String,Integer> lineCount = new LinkedHashMap<>();
        String resultStr = "";

        for(int i =0; i < txtList.size(); i++){
            lineCount.put(i+"|"+txtList.get(i),txtList.get(i).length());
        }

        /*if(command.equalsIgnoreCase("-l")){
            resultStr = lSortMap(lineCount,num);
        }
        else if(command.equalsIgnoreCase("-s")) {
            resultStr = sSortMap(lineCount,num);
        }*/

        List<String> sortedList = new ArrayList<>();
        if(command.equalsIgnoreCase("-l")){
            sortedList = lSortMap(lineCount,num);
        }
        else if(command.equalsIgnoreCase("-s")) {
            sortedList = sSortMap(lineCount,num);
        }

        //sort the list agian base on the staring index
        Collections.sort(sortedList);

        //remove the divide added
        for (String element:sortedList){
            int indexOfDiv = element.indexOf("|");
            resultStr += element.substring(indexOfDiv+1);
        }

        return resultStr.trim();
    }

    private static List<String> sSortMap(Map<String,Integer> oldmap,int limit)  {

        ArrayList<Map.Entry<String,Integer>>  list = new ArrayList<Map.Entry<String,Integer>>(oldmap.entrySet());
        Collections.sort(list,(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) -> {
            return o1.getValue() - o2.getValue();
        });

        List<String> sorted = new ArrayList<>();
        for(int i =0; i < limit; i++){
            sorted.add(list.get(i).getKey());
        }

        return sorted;
    }

    private static List<String> lSortMap(Map<String,Integer> oldmap,int limit){

        ArrayList<Map.Entry<String,Integer>>  list = new ArrayList<>(oldmap.entrySet());
        Collections.sort(list,(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) -> {
            return o2.getValue() - o1.getValue();
        });

        List<String> sorted = new ArrayList<>();
        for(int i =0; i < limit; i++){
            sorted.add(list.get(i).getKey());
        }

        return sorted;
    }

    /*private static String sSortMap(Map<String,Integer> oldmap,int limit)  {
        ArrayList<Map.Entry<String,Integer>>  list = new ArrayList<Map.Entry<String,Integer>>(oldmap.entrySet());

        Collections.sort(list,(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) -> {
            return o1.getValue() - o2.getValue();
        });

        List<String> sorted = new ArrayList<>();
        for(int i =0; i < limit; i++){
            sorted.add(list.get(i).getKey());
        }
        Collections.sort(sorted);
        String result="";
        for (String element:sorted){
            result += element.substring(1);
        }

        return result;
    }*/
    /*private static String lSortMap(Map<String,Integer> oldmap,int limit){

        ArrayList<Map.Entry<String,Integer>>  list = new ArrayList<>(oldmap.entrySet());
        Collections.sort(list,(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) -> {
            return o2.getValue() - o1.getValue();
        });

        List<String> sorted = new ArrayList<>();
        for(int i =0; i < limit; i++){
            sorted.add(list.get(i).getKey());
        }
        Collections.sort(sorted);
        String result="";
        for (String element:sorted){
            int indexOfDiv = element.indexOf("|");
            result += element.substring(indexOfDiv+1);
        }
        return result;
    }*/

    private static String uResult(String inputStr){

        String result = "";
        List<String> wordList = sepTXTbyWordAndRemoveSymbols(inputStr);
        List<String> noDupList = new ArrayList<>();

        //generate a list without duplicate word
        for(String word: wordList){
            if(!noDupList.contains(word)){
                noDupList.add(word);
            }
        }

        if(noDupList.isEmpty()){
            result = inputStr;
            System.out.println(inputStr);
        }
        else {
            for(int i=0; i <noDupList.size(); i ++){

                //get the word first
                String eachWord = noDupList.get(i);

                //find the word starting index and ending index
                int startIndex = inputStr.indexOf(eachWord);
                int subSrcStartIndex = startIndex + eachWord.length();

                //keep the first occurrence
                String firstPartStr =inputStr.substring(0,subSrcStartIndex);

                //the left over part will search for repeat
                String modiStr = inputStr.substring(subSrcStartIndex);

                //remove the repeat matches the whole word
                String removedStr = modiStr.replaceAll("\\b"+ eachWord+ "\\b","");

                //keep on adding the first part to the result, then change the removed string into inputsstring
                result += firstPartStr ;
                inputStr = removedStr;


                //when it reaches the last no repeat world, directly add left to to result
                if(i==noDupList.size()-1){
                    result += removedStr;
                }

            }
        }

        return result;
    }

    private static List<String> sepTXTbyWordAndRemoveSymbols(String fileContent) {
        //separate txt by word
        //remove all the non alphanumeric.
        //fileContent = fileContent.replaceAll("[a-zA-Z\\d\\s]","").replaceAll("\r\n"," ");

        fileContent = fileContent.replaceAll("[^a-zA-Z0-9+$]"," ")
                        .replaceAll("  +"," ");
        //fileContent = fileContent.replaceAll("\\s\\s+","\\s");
        List<String> wordList = Arrays.asList(fileContent.split(" "));
        return wordList;
    }

    private static List<String> sepTxtByLineList(String inputStr) {
        //put the String into a list, each line of String as an element
        //List<String> lineList = Arrays.asList(inputStr.split(System.lineSeparator()));

        //sep lines by \r\n or \r or \n
        //List<String> lineList = Arrays.asList(inputStr.split("(?<=(\\R)|(\\n)(\\r))"));
        //List<String> lineList = Arrays.asList(inputStr.split("(?<=(\\da-zA-Z\\r\\n\\b)|(\\n)|(\\r))"));
        //List<String> lineList = Arrays.asList(inputStr
               // .replaceAll("(\\\\r)?\\\\n", System.getProperty("line.separator"))
               // .split("(?<=(\\r?\\n))"));

        List<String> lineList = new ArrayList<>();
        if(inputStr.contains(System.lineSeparator())){
            lineList = Arrays.asList(inputStr.split("(?<=\\R)"));
        }
        else {
            lineList = Arrays.asList(inputStr.split("(?<=(\\n)|(\\r))"));
        }

        return lineList;
    }

    private static boolean isNumeric(String str) {
        //test all the - or + integer
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //method to output usage message for errors
    private static void usage() {
        System.err.println("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>");
    }
}