package com.servustech.eduson.features.account;

import java.util.regex.Pattern;

public class PasswordValidator {
  public static boolean isValid(String passwordhere/*, List<String> errorList*/) {

    // Pattern specailCharPatten = Pattern.compile("[^a-zA-Z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    // errorList.clear();

    boolean flag=true;

    if (passwordhere.length() < 8) {
        // errorList.add("Password length must have alleast 8 character !!");
        flag=false;
    }
    // if (!specailCharPatten.matcher(passwordhere).find()) {
    //     // errorList.add("Password must have at least one special character !!");
    //     flag=false;
    // }
    if (!UpperCasePatten.matcher(passwordhere).find()) {
        // errorList.add("Password must have at least one uppercase character !!");
        flag=false;
    }
    if (!lowerCasePatten.matcher(passwordhere).find()) {
        // errorList.add("Password must have at least one lowercase character !!");
        flag=false;
    }
    if (!digitCasePatten.matcher(passwordhere).find()) {
        // errorList.add("Password must have at least one digit character !!");
        flag=false;
    }

    return flag;

  }
}
