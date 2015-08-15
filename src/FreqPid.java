
/** A PID controller implementation that uses time-based or frequency-based control.
Assumes that the sensed value is the same as the value that we want to control. 

Process Variable (PV): The sensed variable.
Setpoint (SP): The desired value of the PV.
Control Variable (CV): The controlled output of the PID controller.

For example, for a MassSpringDamper system where we can produce a force, we want to set the 
position of the mass to 5cm, and we have a sensor telling us the position:
The PV is the mass position. The SP is 5cm. The CV is the force applied on the mass.
**/
public class FreqPid {
	private double Kp = 1.0;
	private double Ki = 1.0;
	private double Kd = 1.0;
	
	//private double process_var = 0.0;
	private double setpoint = 0.0;

	//private double process_var_error_accum = 0.0;	// Integral component error accumulation
	//private double old_process_var_error = 0.0;	// Used for standard derivative control
	
	//private double control_var_min = 0.0;
	//private double control_var_max = 1.0;
	private Polynomial tf_num;
	private Polynomial tf_den;
	
	
	/* Create a new PID controller. */
	public FreqPid(double Kp, double Ki, double Kd){
		this.Kp = Kp;
		this.Ki = Ki;
		this.Kd = Kd;
		double[] num_coef = new double[3];
		double[] den_coef = new double[2];
		num_coef[2] = Kd;
		num_coef[1] = Kp;
		num_coef[0] = Ki;
		den_coef[1] = 1;
		den_coef[0] = 0;
		tf_num = new Polynomial(num_coef);
		tf_den = new Polynomial(den_coef);
	}
	
	/* Change the coefficients of the PID controller. */
	public void setCoefficients(double Kp, double Ki, double Kd){
		this.Kp = Kp;
		this.Ki = Ki;
		this.Kd = Kd;
		double[] num_coef = new double[3];
		double[] den_coef = new double[2];
		num_coef[2] = Kd;
		num_coef[1] = Kp;
		num_coef[0] = Ki;
		den_coef[1] = 1;
		den_coef[0] = 0;
		tf_num = new Polynomial(num_coef);
		tf_den = new Polynomial(den_coef);
	}

	
	/* Sets the desired setpoint for the process variable. */
	public void setSetpoint(double setpoint){
		this.setpoint = setpoint;
	}
	
	
	/*public void getResponse(Polynomial num, Polynomial den){
		System.out.println("Controller: ");
		System.out.println(tf_num);
		System.out.println(tf_den);
		System.out.println("Plant: ");
		System.out.println(num);
		System.out.println(den);
		
		//Open loop
		Polynomial ol_tf_num = tf_num.times(num);
		Polynomial ol_tf_den = tf_den.times(den);
		System.out.println("Open loop:");
		System.out.println(ol_tf_num);
		System.out.println(ol_tf_den);
		
		//Closed loop (using the equation GGc/(1+GGc) = num/(den+num))
		Polynomial cl_tf_num = ol_tf_num;
		Polynomial cl_tf_den = ol_tf_den.plus(ol_tf_num);
		System.out.println("Closed loop:");
		System.out.println(cl_tf_num);
		System.out.println(cl_tf_den);
		
	}*/
	

	
}


