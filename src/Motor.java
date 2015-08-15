
public class Motor {
	
	private double velocity = 0;
	
	public Motor() {
		
	}
	
	public void setVelocity(double velocity){
		this.velocity = velocity;
	}
	
	/* Get the distance travelled over the time increment. 'dt' is in seconds. */ 
	public double getDist(double dt){
		return (dt*velocity);
	}
	

	
	
	
}