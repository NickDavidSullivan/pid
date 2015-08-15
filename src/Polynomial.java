
/** Coefficients are stored in reverse order to Matlab, ie coef[0] is the constant. **/

import java.lang.Math;

public class Polynomial {
	private static final double PRECISION = 0.0001; 	// Anything less than this is treated as 0.
	private double[] coef;
	
    /* Create a polynomial from coefficients. coef[0] is the constant, coef[length-1] is the highest
	order term. The coefficients will be copied, so the variable 'coef' can be changed without 
	affecting the polynomial. */
    public Polynomial(double[] coef){
		this.coef = trimCoef(coef);
	}
	
	/********************************************
	 * Polynomial specific methods. None of these alter the polynomial itself, rather a new 
	 * polynomial is returned.
	 *******************************************/
	/* Find the result of a certain value. */
	public double evaluate(double val){
		double p = 0;
        for (int i = coef.length-1; i >= 0; i--)
            p = coef[i] + (val * p);
        return p;
	}
	
	/* Add polynomials. */
	public Polynomial add(Polynomial poly){
		double[] coef2 = poly.getCoefficients();
		double[] coef_result;
		int len = coef.length;
		int len2 = coef2.length;
		int len_max = Math.max(len, len2);
		coef_result = new double[len_max];
		for (int i=0; i<len_max; i++){
			if (i<len){
				coef_result[i] += coef[i];
			}
			if (i<len2){
				coef_result[i] += coef2[i];
			}
		}
		return new Polynomial(coef_result);
	}
	
	/* Subtract polynomials by flipping coefficients and adding. */
	public Polynomial subtract(Polynomial poly){
		double[] coef2 = poly.getCoefficients();
		int len2 = coef2.length;
		double[] coef_flip = new double[len2];
		for (int i=0; i<len2; i++){
			coef_flip[i] = coef2[i]*-1.0;
		}
		return this.add(new Polynomial(coef_flip));
	}
	
	/* Multiply polynomials. */
	public Polynomial multiply(Polynomial poly){
		double[] coef2 = poly.getCoefficients();
		int len = coef.length;
		int len2 = coef2.length;
		double[] coef_result = new double[len+len2];
		for (int i=0; i<len; i++){
			for (int j=0; j<len2; j++){
				coef_result[i+j] += coef[i]*coef2[j];
			}
		}
		return new Polynomial(coef_result);
	}
	/* Multiply by a constant. */
	public Polynomial multiply(double c){
		double[] coef_result = new double[coef.length];
		for (int i=0; i<coef.length; i++){
			coef_result[i] = c*coef[i];
		}
		return new Polynomial(coef_result);
	}
	/* Polynomial division, returns the result and the remainder. */
	public Polynomial[] divide(Polynomial poly_divisor){
		// Copy the dividend
		Polynomial poly_dividend = new Polynomial(this.coef);
		double[] coef_dividend = poly_dividend.getCoefficients();
		int len_dividend = coef_dividend.length;
		// Get divisor information
		double[] coef_divisor = poly_divisor.getCoefficients();
		int len_divisor = coef_divisor.length;
		// Create the result
		double[] coef_result = new double[Math.max(len_dividend,len_divisor)];
		double[] coef_part_result = new double[Math.max(len_dividend,len_divisor)];
		while (len_dividend >= len_divisor){
			//Divide the highest order coefficients
			double div = coef_dividend[len_dividend-1]/coef_divisor[len_divisor-1];
			// Set the value in the partial result and result
			coef_part_result[len_dividend-len_divisor] = div;
			coef_result[len_dividend-len_divisor] = div;
			// Multiply current result with divisor
			Polynomial poly_part_result = new Polynomial(coef_part_result);
			Polynomial poly_mult = poly_part_result.multiply(poly_divisor);
			// Subtract the multiply poly from the dividend
			Polynomial poly_sub = poly_dividend.subtract(poly_mult);
			// Set the subtraction as the new dividend
			poly_dividend = poly_sub;
			coef_dividend = poly_dividend.getCoefficients();
			len_dividend = coef_dividend.length;
			// Clear the partial result
			coef_part_result = new double[Math.max(len_dividend,len_divisor)];

		}
		// Pack result and return
		Polynomial[] result = new Polynomial[2];
		result[0] = new Polynomial(coef_result);
		result[1] = new Polynomial(coef_dividend);
		return result;
	}
	/* Divide by a constant. */
	public Polynomial divide(double c){
		double[] coef_result = new double[coef.length];
		for (int i=0; i<coef.length; i++){
			coef_result[i] = coef[i]/c;
		}
		return new Polynomial(coef_result);
	}
	
	/* Factorises into linear and quadratic with imaginary roots. Only works for order 3 and less. */
	public Polynomial[] factorise(){
		if (coef.length-1 > 3){
			System.out.println("Sorry, can't do > order 3 polynomials.");
			return null;
		}
		// =3, find a single root with Secant method, then divide
		if (coef.length-1 == 3){
			double x0 = -3;
			double x1 = 3;
			double y0 = this.evaluate(x0);
			double y1 = this.evaluate(x1);
			double x_root =0;
			for (int i=0; i<1000; i++){
				x0=x1-y1*(x1-x0)/(y1-y0);
				y0=this.evaluate(x0);
				System.out.println("x: " + x0+", y: "+y0);
				if (Math.abs(y0) < PRECISION) {
					x_root = x0;
					break;
				}
				x1=x0-y0*(x0-x1)/(y0-y1);
				y1=this.evaluate(x1);
				System.out.println("x: " + x1+", y: "+y1);
				if (Math.abs(y1) < PRECISION) {
					x_root = x1;
					break;
				}
			}
			double[] coef_sing = new double[2];
			coef_sing[0] = -x_root;
			coef_sing[1] = 1;
			Polynomial poly_sing = new Polynomial(coef_sing);
			System.out.println("Sing: " + poly_sing);
			// Divide the root thats been found
			Polynomial poly_quad = (this.divide(poly_sing))[0];
			System.out.println("Quad: " + poly_quad);
			// If the quadratic is factorable:
			double[] coef_quad = poly_quad.getCoefficients();
			double a = coef_quad[2];
			double b = coef_quad[1];
			double c = coef_quad[0];
			double determinant = b*b-4*a*c;
			if (determinant >= 0){
				Polynomial[] result = new Polynomial[3];
				double root1 = (-b+Math.sqrt(determinant))/2*a;
				double root2 = (-b-Math.sqrt(determinant))/2*a;
				double[] coef1 = new double[2];
				coef1[0]=-root1;
				coef1[1]=1;
				double[] coef2 = new double[2];
				coef2[0]=-root2;
				coef2[1]=1;
				result[0] = poly_sing;
				result[1] = new Polynomial(coef1);
				result[2] = new Polynomial(coef2);
				return result;
			} else {
				Polynomial[] result = new Polynomial[2];
				result[0] = poly_sing;
				result[1] = poly_quad;
				return result;
			}
		}
		// =2, straight to quadratic formula
		if (coef.length-1 == 2){
			double a = coef[2];
			double b = coef[1];
			double c = coef[0];
			double determinant = b*b-4*a*c;
			if (determinant >= 0){
				Polynomial[] result = new Polynomial[2];
				double root1 = (-b+Math.sqrt(determinant))/2*a;
				double root2 = (-b-Math.sqrt(determinant))/2*a;
				double[] coef1 = new double[2];
				coef1[0]=-root1;
				coef1[1]=1;
				double[] coef2 = new double[2];
				coef2[0]=-root2;
				coef2[1]=1;
				result[0] = new Polynomial(coef1);
				result[1] = new Polynomial(coef2);
				return result;
			} else {
				Polynomial[] result = new Polynomial[1];
				result[0] = this.copy();
				return result;
			}
		}
		// <= 1, return this
		if (coef.length-1 <= 1){
			Polynomial[] result = new Polynomial[1];
			result[0] = this.copy();
		}
		return null;
	}
	
	/* Creates a new polynomial with the same coefficients as this one. */
	public Polynomial copy(){
		return new Polynomial(coef);
	}
	
	@Override
	public String toString(){
		String result = "";
		for (int i=coef.length-1; i>=0; i--){
			if (i == 0) {
				result+=coef[i];
			} else if (i ==1){
				result+=coef[i] + "x ";
			} else {
				result+=coef[i] + "x^"+i+" ";
			}
		}
		return result;
	}
	/********************************************
	 * Internal helper methods
	 *******************************************/
	
	/* Removes any leading 0's, and makes a copy of the array. */
	public static double[] trimCoef(double[] coef){
		int len = coef.length;
		int num_to_trim = 0;
		for (int i=len-1; i>=0; i--){
			if (Math.abs(coef[i]) < PRECISION){
				num_to_trim++;
			} else {
				break;
			}
		}
		double[] return_coef = new double[len-num_to_trim];
		for (int i=0; i<len-num_to_trim; i++){
			return_coef[i]=coef[i];
		}
		return return_coef;
	}
	
	/********************************************
	 * Getters and Setters
	 *******************************************/
	public void setCoefficients(double[] coef){
		this.coef = coef;
	}
	public double[] getCoefficients(){
		return coef;
	}
	public int getDegree(){
		return coef.length-1;
	}

}

