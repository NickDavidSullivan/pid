
/** This system assumes a mass being held by a linear spring and damper to a solid wall. The mass
can be moved by applying a force on it. Assumes position of 0 produces no spring force. **/
public class MassSpringDamper {
	private double m;
	private double k;
	private double c;
	
	private double position = 0.0;
	private double velocity = 0.0;
	
	public MassSpringDamper(double m, double k, double c){
		this.m = m;
		this.k = k;
		this.c = c;
	}
	
	/* Getters. */
	public double getPos(){
		return position;
	}
	
	/* Updates the system based on a time segment and applied force. */
	public void update(double dt, double applied_force){
		// Calculate internal force
		double spring_force = -k*position;
		double damper_force = -c*velocity;

		// Calculate overall force
		double force = applied_force + spring_force + damper_force;
		
		// Change values accordingly
		position += velocity*dt;
		velocity += force*dt;
	}
	
}