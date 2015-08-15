

public class TransferFunction {
	private Polynomial num;
	private Polynomial den;
	
  
    public TransferFunction(Polynomial num, Polynomial den) {
		this.num = num;
		this.den = den;
    }
	

	/********************************************
	 * Transfer Function specific methods
	 *******************************************/
	 
	/* Combine TFs. */
	public TransferFunction multiply(TransferFunction tf){
		Polynomial num_result = num.multiply(tf.getNumerator());
		Polynomial den_result = den.multiply(tf.getDenominator());
		return new TransferFunction(num_result, den_result);
	}
	
	/* Close the loop. */
	public TransferFunction closeLoop(double feedback){
		Polynomial num_result = num.copy();
		Polynomial den_result = num.add(den.multiply(feedback));
		return new TransferFunction(num_result, den_result);
	}
	
	/* Split into several TFs with denominators of linear or quadratic order. NOTE, DENOMINATOR
	MUST START WITH COEFFICENT 1. NOTE, DOESN'T WORK IF MULTIPLE LINEAR VALUES */
	public TransferFunction[] partialFractions(){
		Polynomial[] factors = den.factorise();
		for (int i=0; i<factors.length; i++){
			System.out.println(factors[i]);
		}
		// Handle partial fractions based on denominator type
		int len = factors.length;
		if (len > 3){
			System.out.println("can't do partial fractions, denominator is too big.");
		}
		// Three linear components
		if (len == 3){
			// Check
			for (int i=0; i<3; i++){
				if (factors[i].getDegree() > 1){
					System.out.println("can't do partial fractions, denominator is too big.");
				}
			}
			// Get the linear coefficients a,b,c 
			double a = factors[0].getCoefficients()[0];
			double b = factors[1].getCoefficients()[0];
			double c = factors[2].getCoefficients()[0];
			// Get the numerator coefficients A,B,C
			double[] coef_num = num.getCoefficients();
			double A = 0;
			double B = 0;
			double C = 0;
			if (coef_num.length >= 3) A = coef_num[2];
			if (coef_num.length >= 2) B = coef_num[1];
			if (coef_num.length >= 1) C = coef_num[0];
			double[] num2 = new double[1];
			double[] num1 = new double[1];
			double[] num0 = new double[1];

			num2[0] = C/((a-c)*(c-b));
			num1[0] = (B-num2[0]*(a+c))/(a+b);
			num0[0] = A-num1[0]-num0[0];
			TransferFunction tf0 = new TransferFunction(new Polynomial(num0),factors[0]);
			TransferFunction tf1 = new TransferFunction(new Polynomial(num1),factors[1]);
			TransferFunction tf2 = new TransferFunction(new Polynomial(num2),factors[2]);
			System.out.println(tf0);
			System.out.println(tf1);
			System.out.println(tf2);
			TransferFunction[] result = new TransferFunction[3];
			result[0] = tf0;
			result[1] = tf1;
			result[2] = tf2;
		}
		// Linear and quadratic, or linear and linear
		if (len == 2){
			// Linear and quadratic
			if (factors[1].getDegree() == 2){
				// Get the linear coefficients a,  and quadratic coefficients c,d (scaled by b)
				double a = factors[0].getCoefficients()[0];
				double b = factors[1].getCoefficients()[2];
				double c = factors[1].getCoefficients()[1]/b;
				double d = factors[1].getCoefficients()[0]/b;
				// Get the numerator coefficients A,B,C
				double[] coef_num = num.getCoefficients();
				double A = 0;
				double B = 0;
				double C = 0;
				if (coef_num.length >= 3) A = coef_num[2]/b;
				if (coef_num.length >= 2) B = coef_num[1]/b;
				if (coef_num.length >= 1) C = coef_num[0]/b;
				double[] num1 = new double[2];
				double[] num0 = new double[1];
				num1[0] = C/(d/(1-c)+a);
				num1[1] = B - num1[0]/(1-c);
				num0[0] = A-num1[1];
				TransferFunction tf0 = new TransferFunction(new Polynomial(num0),factors[0]);
				TransferFunction tf1 = new TransferFunction(new Polynomial(num1),factors[1].divide(b));
				System.out.println(tf0);
				System.out.println(tf1);
			}
		}
		
		return null;
	}
	/* Returns the magnitude of the time domain response at a specific time t. */
	public double valueAtTime(double t){
		return 0;
	}
	
	@Override
	public String toString(){
		String result = "";
		result += num.toString() + "\r\n";
		result += "---------\r\n";
		result += den.toString();
		return result;
	}
	/********************************************
	 * Internal helper methods
	 *******************************************/
	 
	/********************************************
	 * Getters and Setters
	 *******************************************/
	public void setNumerator(Polynomial num){
		this.num = num;
	}
	public void setDenominator(Polynomial den){
		this.den = den;
	}
	public Polynomial getNumerator(){
		return num;
	}
	public Polynomial getDenominator(){
		return den;
	}
   
}

