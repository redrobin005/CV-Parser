package com.bham.pij.assignments.candidates;
/**
 * @author Anwin Robin
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.*;

public class CleaningUp {

  // the fields
  int cvCount = 0;
  final String NUM_FORMAT = "0000";
  int           cvNum;
  String        cvNumStr       = "";
  DecimalFormat df             = new DecimalFormat(NUM_FORMAT);
  String        surname        = "";
  String        identifier     = "";
  String        qualification  = "";
  String        position1      = "";
  String        experience1Str = "";
  String        position2      = "";
  String        experience2Str = "";
  String        eMail          = "";
  int           posCount       = 0;
  int           expCount       = 0;

  // for outputting
  String            cleanStr;
  String            delimiter  = ",";
  String            outputStr;
  ArrayList<String> outputList = new ArrayList<>();

  public void cleanUpFile() {
    Path fileIn  = Paths.get("dirtycv.txt");
    Path fileOut = Paths.get("cleancv.txt");

    // try block to retrieve the fields from the file and create output strings
    try (
        InputStream input =
            new BufferedInputStream(Files.newInputStream(fileIn));
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(input));
    ) {

      // new buffered reader to read from start of file again
      String s = reader.readLine();
      // loop to extract all the parameters and place into 2D array
      while (s != null) {
        if (s.contains("CV")) {
          String[] cvNumArray = s.split(" ");
          cvNum    = Integer.parseInt(cvNumArray[1]);
          cvNumStr = df.format(cvNum);
          s       = reader.readLine();
        }
        if (s.contains("Surname")) {
          String[] nameArray = s.split(":");
          surname    = nameArray[1];
          identifier = surname + cvNumStr;
          s         = reader.readLine();
        }
        if (s.contains("Qualification")) {
          String[] qualArray = s.split(":");
          qualification = qualArray[1];
          s            = reader.readLine();
        }
        if (s.contains("Position") && posCount == 0) {
          String[] pos1Array = s.split(":");
          position1 = pos1Array[1];
          posCount++;
          s = reader.readLine();
        }
        if (s.contains("Experience") && expCount == 0) {
          String[] expArray = s.split(":");
          experience1Str = expArray[1];
          expCount++;
          s = reader.readLine();
        }
        if (s.contains("Position") && posCount == 1) {
          String[] pos2Array = s.split(":");
          position2 = pos2Array[1];
          posCount  = 0;
          s        = reader.readLine();
        }
        if (s.contains("Experience") && expCount == 1) {
          String[] exp2Array = s.split(":");
          experience2Str = exp2Array[1];
          expCount       = 0;
          s             = reader.readLine();
        }
        if (s.contains("eMail")) {
          String[] eMailArray = s.split(":");
          eMail = eMailArray[1];
          s    = reader.readLine();
        }
        if (s.contains("End")) {
          cvCount++;
          // construct string
          cleanStr =
              identifier + delimiter + qualification + delimiter + position1 +
              delimiter + experience1Str + delimiter + position2 + delimiter +
              experience2Str + delimiter + eMail + delimiter;

          // re-construct so no excess commas if any of the fields are empty
          StringBuilder sb         = new StringBuilder();
          String[]      cleanArray = cleanStr.split(",");
          for (int i = 0; i < cleanArray.length; i++) {
            if (!cleanArray[i].isEmpty()) {
              sb.append(cleanArray[i]);
              sb.append(delimiter);
            }
          }
          outputStr = sb.toString();
          // add contents of each row an ArrayList
          outputList.add(outputStr);

          // clear all the parameters
          identifier = qualification = position1 = experience1Str =
          position2  = experience2Str = eMail = "";
        }
        s = reader.readLine();
      }
    } catch (IOException e) {
      System.out.println(e);
    }

    // try-block for outputting to cleancv.txt
    try (
        OutputStream output =
            new BufferedOutputStream(Files.newOutputStream(fileOut, CREATE,
                                                           WRITE));
        BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(output));
    ) {
      // write a line for each cv, corresponding to an element in the list
      for (int i = 0; i < outputList.size(); i++) {
        writer.write(outputList.get(i));
        writer.newLine();
      }

    } catch (IOException e) {
      System.out.println(e);
    }

  }
}
