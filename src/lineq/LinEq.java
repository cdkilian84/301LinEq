/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lineq;

/**
 *
 * @author Chris
 */
public class LinEq {

    
    public static void main(String[] args) {
        int n = 4;
        int[] outsideIndex = new int[n];
        double[][] myCoefficients = {{-1, 1, 0, -3}, {1, 0, 3, 1}, {0, 1, -1, -1}, {3, 0, 1, 2}};
        double[] rhs = {4, 0, 3, 1};
        
        gauss(n, myCoefficients, outsideIndex);
        double[] solutions = solve(n, myCoefficients, outsideIndex, rhs);
        
        for(int i = 0; i < n; i++){
            System.out.println(solutions[i]);
        }
        
        
    }
    
    //note that "coefficients" must be n x n
    public static void gauss(int n, double[][] coefficients, int[] index){
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
    }
    
    public static double[] solve(int n, double[][] coefficients, int[] index, double[] eqRHS){
        double[] solutions = new double[n];
        
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
        
        return solutions;
    }
    
}
