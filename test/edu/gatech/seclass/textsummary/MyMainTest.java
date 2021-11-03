package edu.gatech.seclass.textsummary;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyMainTest {
	
/*
Place all  of your tests in this class, optionally using MainTest.java as an example.
*/

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
     *  TEST UTILITIES
     */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file =  createTmpFile();

        OutputStreamWriter fileWriter =
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

        fileWriter.write(input);

        fileWriter.close();
        return file;
    }


    //Read File Utility
    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /*
     * TEST FILE CONTENT
     */
    //empty file
    private static final String testFile1 = "";

    //no words have same occurrence, no lines have same length
    private static final String testFile2
            = "hello hello hello" + System.lineSeparator()
            + "world world" + System.lineSeparator()
            + "~";

    //there are words have same occurrence, no lines have same length
    private static final String testFile3
            = "cat cat cat" + System.lineSeparator()
            + "bird bird bird"+ System.lineSeparator()
            + "...";

    //no words have same occurrence, there are lines have same length
    private static final String testFile4
            = "go go go" + System.lineSeparator()
            + "to ab to"+ System.lineSeparator()
            + "....";

    //there are words have same occurrence, there are lines have same length,
    private static final String testFile5
            = "wow wow wow" + System.lineSeparator()
            + "ahh ahh ahh"+ System.lineSeparator()
            + "testing bbbb*";

    //short file
    private static final String testFile6
            = "a"+ System.lineSeparator()
            + "a";

    /*
     *   TEST CASES
     */
    //Purpose:test if user offers an empty file, the utility will catch the error.
    //Frame #: 1
    @Test
    public void textsummaryTest1()throws Exception {
        File inputFile = createInputFile(testFile1);
        String args[]  = {"-d", "-c", "bs", inputFile.getPath()};
        Main.main(args);
        //assertEquals("Empty file, please check your file again", errStream.toString().trim());
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }

    // Purpose: test when there are multiple OPT inputs, -d will be the first to run, the others will run by its position in the commend
    // Frame #: 44
    @Test
    public void textsummaryTest2() throws Exception {
        File inputFile = createInputFile(testFile3);
        String args[] = {"-c", "hello", "-d", "1","-s","1", inputFile.getPath()};
        Main.main(args);
        String expected = "0 ...";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("cat 3", outStream.toString().trim());
    }

    // Purpose: test if -c working properly when giving parameter mix of Char Symbol or Number.
    // Frame #: 19
    @Test
    public void textsummaryTest3() throws Exception {
        File inputFile = createInputFile(testFile5);
        String args[] = {"-c", "b*", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 wow wow wow" + System.lineSeparator()
                + "0 ahh ahh ahh"+ System.lineSeparator()
                + "1 testing bbbb*";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }

    // Purpose: test when the user input -c with none MixOfNumCharSymb
    // Frame #: 18
    @Test
    public void textsummaryTest4() throws Exception{
        File inputFile = createInputFile(testFile3);
        String args[] = {"-c", "...", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 cat cat cat" + System.lineSeparator()
                + "0 bird bird bird"+ System.lineSeparator()
                + "1 ...";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }


    // Purpose: test when the string for -c is longer than the whole file
    // Frame #:5
    @Test
    public void textsummaryTest5() throws Exception {
        File inputFile = createInputFile(testFile6);
        String args[] = {"-c", "abc", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 a" + System.lineSeparator()
                + "0 a";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }

    //Purpose:test when parameter for -d is larger than the total number of words, the behavior of software.
    //Frame #: 7
    @Test
    public void textsummaryTest6()throws Exception  {
        File inputFile = createInputFile(testFile4);
        String args[]  = {"-d", "30", inputFile.getPath()};
        Main.main(args);
        assertEquals("go 3 to 2 ab 1", outStream.toString().trim());
        //assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
        //assertEquals("-d parameter is larger than the total word number", errStream.toString().trim());
    }


    // Purpose: test when -d has no parameter but the file has words with same duplicated numbers.
    // Frame #: 15
    @Test
    public void textsummaryTest7() throws Exception {
        File inputFile = createInputFile(testFile3);
        String args[] = {"-d", inputFile.getPath()};
        Main.main(args);
        assertEquals("cat 3", outStream.toString().trim());
    }

    //Purpose:test if user giving a non positive integer for -d OPT
    //Frame #: 6
    @Test
    public void textsummaryTest8() throws Exception  {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-d", "-8", inputFile.getPath()};
        Main.main(args);
        //assertEquals("Invalid -d parameter", errStream.toString().trim());
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }

    //Purpose: test if the behavior of -d with correct parameter
    //Frame #: 16
    @Test
    public void textsummaryTest9() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[] = {"-d", "2", inputFile.getPath()};
        Main.main(args);
        assertEquals("hello 3 world 2", outStream.toString().trim());
    }


    //Purpose:test if -l and -s are exclusive
    //Frame #: 9
    @Test
    public void textsummaryTest10() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-l", "1", "-s", "2", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }

    //Purpose:test if put -d first, then -c and -S, the result is correct
    //Frame #: 26
    @Test
    public void textsummaryTest11() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[] = {"-d", "1", "-c", "2", "-s", "2", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 world world" + System.lineSeparator()
                + "0 ~";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("hello 3", outStream.toString().trim());
    }

    //Purpose: test when the parameter of -s is equal to the line number
    //Frame #: 13
    @Test
    public void textsummaryTest12() throws Exception{
        File inputFile = createInputFile(testFile3);
        String args[]  = {"-s", "3", inputFile.getPath()};
        Main.main(args);
        String expected
                = "cat cat cat" + System.lineSeparator()
                + "bird bird bird"  + System.lineSeparator()
                + "...";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }

    //Purpose: test if -s working properly when there are more than one lines have same length
    //Frame #: 21
    @Test
    public void TextsummaryTest13() throws Exception{
        File inputFile = createInputFile(testFile5);
        String args[] = {"-s", "1", inputFile.getPath()};
        Main.main(args);
        String expected = "wow wow wow";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }

    //Purpose: test the combination of -l|-s and -c
    //Frame #: 54
    @Test
    public void TextsummaryTest14() throws Exception{
        File inputFile = createInputFile(testFile2);
        String args[] = {"-l", "1", "-c","he", inputFile.getPath()};
        Main.main(args);
        String expected = "3 hello hello hello";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }

    //Purpose: test when all the OPT has been selected
    //Frame #: 34
    @Test
    public void TextsummaryTest15() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[] = {"-d", "1", "-l", "2", "-c", "t.", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 hello hello hello" + System.lineSeparator()
                + "0 world world";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("hello 3", outStream.toString().trim());
    }


    //Purpose: test OPT input but no filename
    //Frame #: 2
    @Test
    public void TextsummaryTest16() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-l", "1", "-d", "2"};
        Main.main(args);
        assertEquals("File Not Found", errStream.toString().trim());
    }

    //Purpose: no parameter for -l or -s
    //Frame #: 10
    @Test
    public void TextsummaryTest17() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-l", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }


    //Purpose: the parameter for -c doesn't exist
    //not from frame: 4
    @Test
    public void TextsummaryTest18() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-c", inputFile+".txt"};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }


    //Purpose: invalid parameter for -s, eg: -2
    //Frame #: 11
    @Test
    public void TextsummaryTest19() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-s", "-2", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
        //assertEquals("Invalid -l|-s parameter", errStream.toString().trim());
    }


    //Purpose: very large parameter for -l, eg: 30
    //Frame #: 12
    @Test
    public void textsummaryTest20()throws Exception  {
        File inputFile = createInputFile(testFile4);
        String args[]  = {"-l", "30", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
        //assertEquals("The specified parameter is too large", errStream.toString().trim());
    }


    //Purpose: if test is case sensitive, the purpose it to make OPT not case sensitive
    //Frame #:  it is not the test cases generated in Assignment 6.
    @Test
    public void textsummaryTest21() throws Exception {
        File inputFile = createInputFile(testFile5);
        String args[] = {"-C", "b*", "-L", "2", "-D", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 wow wow wow" + System.lineSeparator()
                + "0 ahh ahh ahh";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("wow 3", outStream.toString().trim());
    }

    //Purpose: test when -d put at first, then with all the other commands, with the file
    //Frame #: 23
    @Test
    public void textsummaryTest22() throws Exception {
        File inputFile = createInputFile(testFile5);
        String args[] = {"-d", "-c", "ah", "-s", "2", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 wow wow wow" + System.lineSeparator()
                + "3 ahh ahh ahh";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("wow 3", outStream.toString().trim());
    }

    //Purpose: test the -c behavior when giving mix char, number and symbol, combining with other OPT
    //Frame #: 32
    @Test
    public void textsummaryTest23() throws Exception {
        File inputFile = createInputFile(testFile3);
        String args[] = {"-d", "-c", "log: 8", "-l", "2", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 cat cat cat" + System.lineSeparator()
                + "0 bird bird bird";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("cat 3", outStream.toString().trim());
    }

    //Purpose: test when put -c at beginning, the behavior of the code with the input txt file has non word with same occurrence
    // and no line has the same length
    //Frame #: 38
    @Test
    public void textsummaryTest24() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[] = {"-c", "Hello", "-l", "2", "-d", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 hello hello hello" + System.lineSeparator()
                + "0 world world";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("hello 3", outStream.toString().trim());
    }

    //Purpose: test the case when the -d parameter equal the total world number
    //Frame #: 8
    @Test
    public void textsummaryTest25() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[] = {"-d", "2", inputFile.getPath()};
        Main.main(args);
        assertEquals("hello 3 world 2", outStream.toString().trim());
    }

    //Purpose: test the case when the -l|-s parameter equal the total world number
    //Frame #: 13
    @Test
    public void textsummaryTest26() throws Exception {
        File inputFile = createInputFile(testFile3);
        String args[] = {"-l", "3", inputFile.getPath()};
        Main.main(args);
        String expected
                = "cat cat cat" + System.lineSeparator()
                + "bird bird bird" + System.lineSeparator()
                + "...";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
    }

    //Purpose: no integer parameter for -l or -s
    //Frame #: 10
    @Test
    public void TextsummaryTest27() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-s","hello", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }

    //Purpose: test when put -s at beginning, the behavior of the code with the input txt file has more
    //than one word with same occurrence and no line has the same length
    //Frame #: 60
    @Test
    public void textsummaryTest28() throws Exception {
        File inputFile = createInputFile(testFile3);
        String args[] = {"-s", "2", "-c", "*", "-d","2", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 cat cat cat" + System.lineSeparator()
                + "0 ...";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("cat 3 bird 3", outStream.toString().trim());
    }

    //Purpose: test when put -l at beginning, the behavior of the code with the input txt file has more
    //than one word with same occurrence and more than one line has the same length
    //Frame #: 69
    @Test
    public void textsummaryTest29() throws Exception {
        File inputFile = createInputFile(testFile5);
        String args[] = {"-l", "2", "-c", "b*", "-d","1", inputFile.getPath()};
        Main.main(args);
        String expected
                = "0 wow wow wow" + System.lineSeparator()
                + "0 ahh ahh ahh";
        String actual = getFileContent(inputFile.getPath());
        assertEquals("The files differ!", expected, actual);
        assertEquals("wow 3", outStream.toString().trim());
    }

    //Purpose: OPT have more than one parameter
    //not from frame: I did not think about the condition that user input more than 2 parameter for
    //each OPT, or user forget to add OPT
    @Test
    public void TextsummaryTest30() throws Exception {
        File inputFile = createInputFile(testFile2);
        String args[]  = {"-s", "hello", "2", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: textsummary [-d [int]] [-c string] [-l int | -s int | -u] <filename>", errStream.toString().trim());
    }

}
