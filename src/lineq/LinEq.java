//Christopher Kilian
//CS 301 - Spring 2018
//Programming project 1 - Solving systems of linear equations

package lineq;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//All methods and the user menu are implemented in this one class - "main" method handles user interface, and operations such as
//file reading or gaussian elimination are handled in helper methods.
public class LinEq {
 
    //the arrays being used for solving a system of linear equations
    //made globally accessible here for ease of access
    private static int[] outsideIndex;
    private static double[][] myCoefficients;
    private static double[] rhs;
    
    public static void main(String[] args) {
        //commented out code chunk used for testing the algorithm independent of the command line UI
        //included here for completeness
//        int n = 3;
//        int[] outsideIndex = new int[n];
//        double[][] myCoefficients = {{2, 4, -2}, {1, 3, 4}, {5, 2, 0}};
//        double[] rhs = {6, -1, 2};
//        
//        gauss(n, myCoefficients, outsideIndex);
//        double[] solutions = solve(n, myCoefficients, outsideIndex, rhs);
//        
//        for(int i = 0; i < n; i++){
//            System.out.println(solutions[i]);
//        }

        Console console = System.console();
        if (console == null) {
            System.out.println("No console: To run the menu, run from the command line!");
            System.exit(0);
        }
        
        //main menu loop
        while(true){
            String numEquations;
            int numEq;
            String menuChoice;
            boolean proceed = true;
            
            System.out.println("Welcome to the Linear Equation Solver.\n");
            
            while(true){
                System.out.println("Please choose an option:");
                System.out.println("1) Enter coefficients for each equation manually.");
                System.out.println("2) Enter a filename holding a matrix representation of the coefficients.");
                System.out.println("3) Quit the program.");
                menuChoice = console.readLine();
                if(menuChoice.equals("1") || menuChoice.equals("2") || menuChoice.equals("3")){
                    break;
                }else{
                    System.out.println("Invalid menu choice. Try again.");
                }
            }
            
            if(menuChoice.equals("3")){
                System.out.println("Goodbye!");
                break; //break outer loop and end program
            }
            
            System.out.println("Before proceeding, tell me how many linear equations you are attempting to solve. Note that you must input the correct "
                    + "number being solved to get the correct solution!");
            System.out.println("Enter now:");
            //loop on getting an appropriate value for "n"
            while(true){
                numEquations = console.readLine();
                
                if(numEquations.matches("\\d+") && !numEquations.equals("0")){
                    break;
                }else{
                    System.out.println("That is not a valid number - please only enter a positive integer value.");
                    System.out.println("Enter the number of linear equations again:");
                }
            }
            
            numEq = Integer.parseInt(numEquations); //the number of equations being solved (also the number of coefficients)
            //initialize arrays to size n based on user input
            outsideIndex = new int[numEq];
            myCoefficients = new double[numEq][numEq];
            rhs = new double[numEq];
            
            if(menuChoice.equals("1")){
                handleManualInput(numEq);
            }else if(menuChoice.equals("2")){
                proceed = handleFileInput(numEq);
                if(!proceed){
                    System.out.println("\nThe file was not properly formatted - either wrong number of variables for what was indicated, or non-numeric values present.");
                }
            }

            if(proceed){
                System.out.println("All equations entered. Processing...");
                //solve the system of linear equations
                gauss(numEq, myCoefficients, outsideIndex);
                double[] solutions = solve(numEq, myCoefficients, outsideIndex, rhs);

                System.out.println("\nSolutions to the system:");
                for(int i = 0; i < numEq; i++){
                    System.out.println("X" + (i+1) + " = " + solutions[i]);
                }
            }
            
            System.out.println("\nWould you like to run this program again? Type yes to run again.");
            String exitConfirm = console.readLine();
            if(!exitConfirm.equalsIgnoreCase("yes")){
                System.out.println("Goodbye!");
                break;
            }else{
                System.out.println("\n");
            }
        }

    }
    

    //handler for the case where the user wants to input the coefficient values of their
    //equations from a provided text file rather than manually. Returns a boolean representing whether the
    //file was successfully read in or not.
    public static boolean handleFileInput(int numEquations){
        Console console = System.console();
        boolean fileReadSuccessfully = true;
        String fileName;
        System.out.println("\nWhen entering a file name, please ensure you enter the full file path, or that the file is in your current working directory!");
        System.out.println("Please enter the name of the file now:");
        
        
        while(true){
            fileName = console.readLine();
            File check = new File(fileName);
            if(!check.exists()){
                System.out.println("\nFile not found - please check that it exists and enter the path again:");
            }else{
                break;
            }
        }
        
        int count = 0;//represents the equation number being processed
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                line.trim();
                String[] values = line.split("\\s+");
                if((values.length != (numEquations+1)) || !checkInput(values)){
                    fileReadSuccessfully = false;
                    break;
                }
                
                for(int j = 0; j < numEquations; j++){
                    //note that by this point in the loop, values have already been checked as being numeric for easy type conversion
                    myCoefficients[count][j] = Double.valueOf(values[j]);
                }
                rhs[count] = Double.valueOf(values[numEquations]); //last term in string is always the solution for that equation (right hand side value)
                count++;
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            fileReadSuccessfully = false;
        }
        
        return fileReadSuccessfully;
    }
    
    
    //handler for the case where the user wants to input the coefficient values of their
    //equations manually rather than reading in from a file
    public static void handleManualInput(int numEquations){
        Console console = System.console();
        
        System.out.println("You have selected to manually enter your coefficients.");
        System.out.println("Please enter them in the following fashion:\n");
        System.out.println("For an equation of the form 2x - 3y = 6, enter: 2 -3 6");
        System.out.println("Please note the spaces between each term - values without a space are considered a single number.");
        System.out.println("You will be asked to enter each of the " + numEquations + " individually - do not enter them all on a single line.\n");
        
        for(int i = 0; i < numEquations; i++){
            while(true){ //used to handle reprocessing of improper input
                System.out.println("Enter equation " + (i+1) + " now:");
                String line = console.readLine();
                
                line.trim(); //get rid of user entered leading or trailing whitespace
                String[] values = line.split("\\s+");
                
                if(values.length != (numEquations + 1)){ //numEquations+1 is the coefficients of the equation plus its solution
                    System.out.println("You entered an improper number of values - please enter them again for this equation.\n");
                    continue;
                }else if(!checkInput(values)){
                    System.out.println("You entered an improper character value. Only enter numeric values for coefficients. Please try again.\n");
                    continue;
                }
                
                //for a given n+1 terms (n coefficients and a RHS solution), walk through first n terms and place into coefficients array
                for(int j = 0; j < numEquations; j++){
                    //note that by this point in the loop, values have already been checked as being numeric for easy type conversion
                    myCoefficients[i][j] = Double.valueOf(values[j]);
                }
                rhs[i] = Double.valueOf(values[numEquations]); //last term in string is always the solution for that equation (right hand side value)
                break; //if arriving here without hitting a "continue" statement, end the while loop and proceed to next iteration of outer "for" loop
            }
        }
    }
    
    
    //Method to check input and ensure that the values are numeric - positive or negative numbers are allowed, as are
    //number with or without decimal values. Any non-numeric value will return false.
    //return true if the input is valid
    public static boolean checkInput(String[] userInput){
        boolean goodInputFlag = true;
        
        for(String input : userInput){
            //System.out.println("checking input " + input);
            if(!input.matches("-?\\d+(\\.\\d+)?")){
                goodInputFlag = false;
                break;
            }
        }
        
        //System.out.println("All input verified. Flag reads: " + goodInputFlag);
        
        return goodInputFlag;
    }
        
    
    //Method which performs gaussian elimination with partial pivoting - implemented version of pseudocode found in
    //Numerical Mathematics and Computing, chapter 2 section 2
    //note that "coefficients" must be n x n
    public static void gauss(int n, double[][] coefficients, int[] index){
        try{
            double sMax = 0.0;
            double r = 0.0;
            double[] scaleArr = new double[n];

            for(int i = 0; i < n; i++){
                index[i] = i;
                sMax = 0.0;
                for(int j = 0; j < n; j++){
                    sMax = Math.max(sMax, Math.abs(coefficients[i][j]));
                }
                scaleArr[i] = sMax;
            }

            int j = n-1;
            for(int k = 0; k < (n-1); k++){
                double rMax = 0.0;

                for(int i = k; i < n; i++){
                    r = Math.abs(coefficients[index[i]][k]/scaleArr[index[i]]);
                    if(r > rMax){
                        rMax = r;
                        j = i;
                    }
                }
                int temp = index[j];
                index[j] = index[k];
                index[k] = temp;

                for(int i = (k+1); i < n; i++){
                    double xMult = coefficients[index[i]][k]/coefficients[index[k]][k];
                    coefficients[index[i]][k] = xMult;
                    for(j=k+1; j < n; j++){
                        coefficients[index[i]][j] = coefficients[index[i]][j] - xMult * coefficients[index[k]][j];
                    }
                }
            }
        }catch(Exception x){ //catch errors including divide by zero or matrix errors (mostly here "just in case")
            System.out.println("There was an error processing the matrix! Problem in Gaussian Elimination phase!");
            System.out.println("Cause: " + x.getCause());
            System.out.println(x.fillInStackTrace());
        }
    }
    
    
    //Method which works to perform the back substitution on a matrix which has been processed through the 
    //Gaussian elimination phase already - the returned solution array will only be correct if Gaussian elimination is done first.
    //Implemented version of pseudocode found in Numerical Mathematics and Computing, chapter 2 section 2
    public static double[] solve(int n, double[][] coefficients, int[] index, double[] eqRHS){
        double[] solutions = new double[n];
        try{
            for(int k = 0; k < (n-1); k++){
                for(int i = k+1; i < n; i++){
                    eqRHS[index[i]] = eqRHS[index[i]] - (coefficients[index[i]][k] * eqRHS[index[k]]);
                }
            }

            solutions[(n-1)] = eqRHS[index[(n-1)]]/coefficients[index[(n-1)]][(n-1)];

            for(int i = (n-2); i >= 0; i--){
                double sum = eqRHS[index[i]];
                for(int j = i+1; j < n; j++){
                    sum = sum - coefficients[index[i]][j] * solutions[j];
                }
                solutions[i] = sum/coefficients[index[i]][i];
            }
        }catch(Exception x){//catch errors including divide by zero or matrix errors (mostly here "just in case")
            System.out.println("There was an error processing the matrix! Problem in solution phase!");
            System.out.println("Cause: " + x.getCause());
            System.out.println(x.fillInStackTrace());
        }
        return solutions;
    }
    
}
