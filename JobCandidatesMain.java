package com.bham.pij.assignments.candidates;
/**
 * @author Anwin Robin
 */

public class JobCandidatesMain {
  public static void main(String[] args) {
    CleaningUp clean = new CleaningUp();
    clean.cleanUpFile();

    CandidatesToInterview can = new CandidatesToInterview();
    can.findCandidates();
    can.candidatesWithExperience();
    can.createCSVFile();
    can.createReport();
  }
}
