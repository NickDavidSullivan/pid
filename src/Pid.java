
/** A PID controller implementation that uses time-based or frequency-based control.
Assumes that the sensed value is the same as the value that we want to control. 

Process Variable (PV): The sensed variable.
Setpoint (SP): The desired value of the PV.
Control Variable (CV): The controlled output of the PID controller.

For example, for a MassSpringDamper system where we can produce a force, we want to set the 
position of the mass to 5cm, and we have a sensor telling us the position:
The PV is the mass position. The SP is 5cm. The CV is the force applied on the mass.
**/
public class Pid {
	private double Kp = 1.0;
	private double Ki = 1.0;
	private double Kd = 1.0;
	
	private double process_var = 0.0;
	private double setpoint = 0.0;

	private double process_var_error_accum = 0.0;	// Integral component error accumulation
	private double old_process_var_error = 0.0;	// Used for standard derivative control
	
	private double control_var_min = 0.0;
	private double control_var_max = 1.0;
		
	/* Create a new PID controller. */
	public Pid(double Kp, double Ki, double Kd){
		this.Kp = Kp;
		this.Ki = Ki;
		this.Kd = Kd;
	}
	
	/* Change the coefficients of the PID controller. */
	public void setCoefficients(double Kp, double Ki, double Kd){
		this.Kp = Kp;
		this.Ki = Ki;
		this.Kd = Kd;
	}
	
	/* Sets the output limits of the controller. */
	public void setLimits(double control_var_min, double control_var_max){
		if (control_var_min > control_var_max) return;
		this.control_var_min = control_var_min;
		this.control_var_max = control_var_max;
		// Realtime update
		if (process_var_error_accum > control_var_max) process_var_error_accum=control_var_max;
		if (process_var_error_accum < control_var_min) process_var_error_accum=control_var_min;
	}
	
	/* Sets the desired setpoint for the process variable. */
	public void setSetpoint(double setpoint){
		this.setpoint = setpoint;
	}
	
	/* Performs a 'tick' for the controller. 'dt' time has passed, and the process variable
	has changed by 'dprocess_var' within that timeframe. Returns the desired value of the control
	variable. */
	public double update(double dt, double dprocess_var){
		// Update the process variable position
		process_var += dprocess_var;
		
		// Calculate errors
		double error = setpoint-process_var;
		double derror = error-old_process_var_error;
		old_process_var_error = error;
		
		// Proportional
		double p = Kp*error;
		
		// Integral
		process_var_error_accum += Ki*(error*dt);
		// If the limits are reached, integral accumulation should stop, otherwise there will be
		// delay to come back down, called windup:
		if (process_var_error_accum > control_var_max) process_var_error_accum=control_var_max;
		if (process_var_error_accum < control_var_min) process_var_error_accum=control_var_min;
		double i = process_var_error_accum;
		
		// Derivative
		// This is the standard derivative component:
		//     double d = Kd*(derror/dt);
		// But when the setpoint changes, the error derivative becomes incredibly large, 
		// called 'derivative kick', so to remove this we only use the change in control variable.
		// The derivative component doesn't care about the setpoint, only the rate at which we head
		// in a particular direction:
		double d = -Kd*(dprocess_var/dt);		// with derivative kickback mitigation
		
		// Combined
		double pid = p+i+d;
		// Do not output anything outside the limits
		if(pid > control_var_max) pid=control_var_max;
		if(pid < control_var_min) pid=control_var_min;
		return pid;
	}
	
}


