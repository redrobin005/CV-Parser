package com.bham.pij.assignments.candidates;
/**
 * @author Anwin Robin
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class CandidatesToInterview {
  // for creating the 2D array to store everything
  int numOfCV = 0;
  final int NUM_PARAMS = 7;
  String[][] cvData = null;

  // the fields
  String identifier     = "";
  String qualification  = "";
  String position1      = "";
  String experience1Str = "";
  String position2      = "";
  String experience2Str = "";
  String eMail          = "";

  public void findCandidates() {
    String[] keywordDegree = {"Degree in Computer Science", "Masters in " +
                                                            "Computer Science"};
    String[] keywordExperience = {"Data analyst", "Programmer",
                                  "Computer programmer", "Operator"};

    Path fileIn  = Paths.get("cleancv.txt");
    Path fileOut = Paths.get("to-interview.txt");

    boolean           qualMatch   = false;
    ArrayList<String> matchListTI = new ArrayList<>();

    // try block to retrieve the fields from the file and create output strings
    try (
        InputStream input =
            new BufferedInputStream(Files.newInputStream(fileIn));
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(input));
        InputStream input2 =
            new BufferedInputStream(Files.newInputStream(fileIn));
        BufferedReader reader2 =
            new BufferedReader(new InputStreamReader(input2));
    ) {

      // loop to determine the number of CVs and create the 2D array
      String s = reader.readLine();
      while (s != null) {
        numOfCV++;
        s = reader.readLine();
      }
      // 2D array will have rows = num of CVs, column = num of parameters
      cvData = new String[numOfCV][NUM_PARAMS];
      // fill the array with empty strings to prevent null exceptions
      for (String[] row : cvData) {
        Arrays.fill(row, "");
      }

      // new buffered reader to read from start of file again
      String s2 = reader2.readLine();
      // loop to extract all the parameters and place into 2D array
      while (s2 != null) {
        for (int row = 0; row < numOfCV; row++) {
          String[] rowArray = s2.split(",");
          for (int col = 0; col < rowArray.length; col++) {
            if (col == 0) {
              identifier     = rowArray[col];
              cvData[row][0] = identifier;
            }
            if (col == 1) {
              qualification  = rowArray[col];
              cvData[row][1] = qualification;
              // search through degree keywords with qualification keyword
              Arrays.sort(keywordDegree);
              if (Arrays.binarySearch(keywordDegree, qualification) >= 0) {
                qualMatch = true;
              }
            }
            if (col == 2 && rowArray.length > 3) {
              position1      = rowArray[col];
              cvData[row][2] = position1;
            }
            if (col == 3 && rowArray.length > 3) {
              experience1Str = rowArray[col];
              cvData[row][3] = experience1Str;
            }
            if (col == 4 && rowArray.length > 5) {
              position2      = rowArray[col];
              cvData[row][4] = position2;
            }
            if (col == 5 && rowArray.length > 5) {
              experience2Str = rowArray[col];
              cvData[row][5] = experience2Str;
            }
            if (rowArray[col].contains("@")) {
              eMail          = rowArray[col];
              cvData[row][6] = eMail;
            }
          }
          //filter out the cvs with a keyWordDegree && a keyWordExperience
          Arrays.sort(keywordExperience);
          if ((Arrays.binarySearch(keywordExperience, position1) >= 0
               && qualMatch == true) ||
              (Arrays.binarySearch(keywordExperience, position2) >= 0
               && qualMatch == true)) {
            StringBuilder sb = new StringBuilder();
            for (String value : rowArray) {
              sb.append(value);
              sb.append(" ");
            }
            String outputStr = sb.toString();
            matchListTI.add(outputStr);
          }
          // reset the parameters
          qualMatch  = false;
          identifier = qualification = position1 = experience1Str =
          position2  = experience2Str = eMail = "";

          s2 = reader2.readLine();
        }
      }
    } catch (IOException e) {
      System.out.println(e);
    }

    //try-block for outputting to to-interview.txt
    try (
        OutputStream output =
            new BufferedOutputStream(Files.newOutputStream(fileOut, CREATE,
                                                           WRITE));
        BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(output));
    ) {
      // write a line for each cv, corresponding to an element in the list
      for (String s : matchListTI) {
        writer.write(s);
        writer.newLine();
      }

    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void candidatesWithExperience() {
    String[] keywordDegree = {"Degree in Computer Science", "Masters in " +
                                                            "Computer Science"};
    String[] keywordExperience = {"Data analyst", "Programmer",
                                  "Computer programmer", "Operator"};
    Path fileOut = Paths.get("to-interview-experience.txt");

    boolean           qualMatch    = false;
    ArrayList<String> matchListTIE = new ArrayList<>();

    for (int row = 0; row < cvData.length; row++) {
      // check to see if qualification and position match
      // by parsing through the 2D array like in the previous method
      Arrays.sort(keywordDegree);
      if (Arrays.binarySearch(keywordDegree, cvData[row][1]) >= 0) {
        qualMatch = true;
      }
      Arrays.sort(keywordExperience);
      if ((Arrays.binarySearch(keywordExperience, cvData[row][2]) >= 0
           && qualMatch == true)) {

        // if the checks are ok, then proceed with checking years of exp
        int       expNum      = 0;
        final int DEFAULT_VAL = 0;
        // handle error when blanks are parsed to get the experience int
        try {
          expNum = Integer.parseInt(cvData[row][3]);
        } catch (NumberFormatException e) {
          expNum = DEFAULT_VAL;
        }
        StringBuilder sb = new StringBuilder();
        // if > 5 years experience in position1, append
        // assuming position1 is always at column 3
        // (and identifier is always column 0)
        if (expNum > 5) {
          sb.append(cvData[row][0]);
          sb.append(" ");
          sb.append(cvData[row][3]);
          String outputStr = sb.toString();
          matchListTIE.add(outputStr);
        }
      }
      // reset qualMatch
      qualMatch = false;
    }

    //try-block for outputting to to-interview-experience.txt
    try (
        OutputStream output =
            new BufferedOutputStream(Files.newOutputStream(fileOut, CREATE,
                                                           WRITE));
        BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(output));
    ) {
      // write a line for each cv, corresponding to an element in the list
      for (String s : matchListTIE) {
        writer.write(s);
        writer.newLine();
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void createCSVFile() {
    String[] keywordDegree = {"Degree in Computer Science", "Masters in " +
                                                            "Computer Science"};
    String[] keywordExperience = {"Data analyst", "Programmer",
                                  "Computer programmer", "Operator"};

    Path              fileOut = Paths.get("to-interview-table-format.csv");
    ArrayList<String> csvList = new ArrayList<>();
    // add column titles
    csvList.add("Identifier, Qualification, Position1, Experience1, " +
                "Position2, Experience2, eMail");

    // parse through 2D array intialised in previous method
    // filter out based on same basis as when creating to-interview
    // write each record on each line, separated by commas
    for (int row = 0; row < cvData.length; row++) {
      boolean       qualMatch = false;
      StringBuilder sb        = new StringBuilder();
      for (int col = 0; col < cvData[row].length; col++) {
        Arrays.sort(keywordDegree);
        if (Arrays.binarySearch(keywordDegree, cvData[row][1]) >= 0) {
          qualMatch = Boolean.TRUE;
        }
        Arrays.sort(keywordExperience);
        if ((Arrays.binarySearch(keywordExperience, cvData[row][2]) >= 0
             && qualMatch == true) ||
            (Arrays.binarySearch(keywordExperience, cvData[row][4]) >= 0
             && qualMatch == true)) {
          //check if entire row is not empty
          boolean rowEmpty = false;
          for (String value : cvData[row]) {
            if (value.isEmpty()) {
              rowEmpty = true;
            } else {
              rowEmpty = false;
            }
          }
          // if the row is empty, then skip this row
          if (rowEmpty) {
            break;
          }
          // if the row isn't empty and this index IS empty
          // then append a blank and a comma
          else if (!rowEmpty && cvData[row][col].isEmpty()) {
            sb.append(" ");
            sb.append(",");
          }
          // if this index is not empty
          // then append index and a comma
          if (!cvData[row][col].isEmpty()) {
            sb.append(cvData[row][col]);
            sb.append(",");
          }
        }
      }
      String outputStr = sb.toString();
      csvList.add(outputStr);
    }

    //try-block for outputting to to-interview-experience.txt
    try (
        OutputStream output =
            new BufferedOutputStream(Files.newOutputStream(fileOut, CREATE,
                                                           WRITE));
        BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(output));
    ) {
      // write a line for each cv, corresponding to an element in the list
      for (String s : csvList) {
        if (!s.isEmpty()) {
          writer.write(s);
          writer.newLine();
        }
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void createReport() {
    Path fileIn = Paths.get("to-interview-table-format.csv");
    try (InputStream input =
             new BufferedInputStream(Files.newInputStream(fileIn));
         BufferedReader reader =
             new BufferedReader(new InputStreamReader(input))) {
      String s = reader.readLine();
      while (s != null) {
        String[] outputArray = s.split(",");
        // format each line using printf
        // longest value + 5 for the spacing
        // for field(column) headings
        if (s.contains("Identifier")){
          System.out.printf("%-15s%-32s%-17s%-16s%-21s%n",
                            outputArray[0], outputArray[1], outputArray[2],
                            outputArray[3], outputArray[outputArray.length-1]);
          s = reader.readLine();
        }else{
        // for field data
          // +1 to identifier to align to the format given
          System.out.printf("%-16s%-32s%-17s%-16s%-21s%n",
                            outputArray[0], outputArray[1], outputArray[2],
                            outputArray[3], outputArray[outputArray.length-1]);
        s = reader.readLine();
        }
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
