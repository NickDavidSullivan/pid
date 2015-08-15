

public class Main {
	

	public static void main(String args[]){
		/*double[] coef = new double[3];
		coef[0] = 2;
		coef[1] = 3;
		coef[2] = 1;
		Polynomial p1 = new Polynomial(coef);
		coef = new double[4];
		coef[0] = 6;
		coef[1] = 11;
		coef[2] = 6;
		coef[3] = 1;
		Polynomial p2 = new Polynomial(coef);
		//Polynomial[] p3 = p2.divide(p1);
		//Polynomial p4 = p3.subtract(p2);
		//Polynomial p5 = p1.multiply(p2);
		System.out.println(p1);

		//System.out.println(p3[0]);
		//System.out.println(p3[1]);
		Polynomial[] factorised1 = p1.factorise();
		System.out.println("factorised0: " + factorised1[0]);
		System.out.println("factorised1: " + factorised1[1]);
		System.out.println(p2);
		Polynomial[] factorised = p2.factorise();
		System.out.println("factorised0: " + factorised[0]);
		System.out.println("factorised1: " + factorised[1]);
		System.out.println("factorised2: " + factorised[2]);*/

		//System.out.println(p4);
		//System.out.println(p5);

		
		double m = 10.0;
		double k = 4;
		double c = 2;
		double kp = 20;
		double ki = 1;
		double kd = 2;
		/*
		double[] coef_gc_num = new double[3];
		coef_gc_num[0] = ki;
		coef_gc_num[1] = kp;
		coef_gc_num[2] = kd;
		double[] coef_gc_den = new double[2];
		coef_gc_den[0] = 0;
		coef_gc_den[1] = 1;
		Polynomial poly_gc_num = new Polynomial(coef_gc_num);
		Polynomial poly_gc_den = new Polynomial(coef_gc_den);
		TransferFunction gc = new TransferFunction(poly_gc_num, poly_gc_den);
		System.out.println(gc);
		
		double[] coef_g_num = new double[1];
		coef_g_num[0] = 1;
		double[] coef_g_den = new double[3];
		coef_g_den[0] = k;
		coef_g_den[1] = c;
		coef_g_den[2] = m;
		Polynomial poly_g_num = new Polynomial(coef_g_num);
		Polynomial poly_g_den = new Polynomial(coef_g_den);
		TransferFunction g = new TransferFunction(poly_g_num, poly_g_den);
		System.out.println(g);
		
		TransferFunction ol = gc.multiply(g);
		System.out.println(ol);
		
		TransferFunction cl = ol.closeLoop(1);
		System.out.println(cl);
		System.out.println("PARTIAL FRACTIONS\r\n");
		cl.partialFractions();
		
		boolean use_freq = true;
		// Frequency Domain
		if (use_freq){
			FreqPid pid = new FreqPid(1.0, 2.0, 3.0);
			double num[] = new double[1];
			double den[] = new double[3];
			num[0] = 1;
			den[0] = k;
			den[1] = c;
			den[2] = m;
			Polynomial nump = new Polynomial(num);
			Polynomial denp = new Polynomial(den);
			pid.getResponse(nump, denp);
			return;
		}*/
		
		// Time Domain
		try {
			int num_parallel_sys = 3;
			
			double pid_setpoint = 1.0;		// Desired position/velocity/temperature/whatever
			double pid_limit = 60.0;			// Maximum force able to be applied
			
			// Create system components
			System.out.println("Beginning PID test");
			MassSpringDamper[] msd = new MassSpringDamper[num_parallel_sys];
			
			Pid[] pid = new Pid[num_parallel_sys];
			pid[0] = new Pid(18, 0, 0);
			pid[1] = new Pid(14.4, 0.784, 0);
			pid[2] = new Pid(21.6, 0.49, 2);
			
			String[] series_names = new String[num_parallel_sys+1];
			series_names[0] = "Kp=5";
			series_names[1] = "Kp=3";
			series_names[2] = "Kp=1";
			series_names[3] = "desired";
			Grapher control_graph = new Grapher("PID Force Output", num_parallel_sys+1, series_names, "Time", "Force");
			Grapher pos_graph = new Grapher("PID Controlled Position", num_parallel_sys+1,series_names, "Time", "Position");
			
			
			// Set testing vars
			double dt = 0.05;
			double[] old_pos = new double[num_parallel_sys];
			double[] pos = new double[num_parallel_sys];
			
			// Set default values for all systems
			for (int i=0; i<num_parallel_sys; i++){
				msd[i] = new MassSpringDamper(m, k, c);
				pid[i].setSetpoint(pid_setpoint);
				pid[i].setLimits(-pid_limit, pid_limit);
				old_pos[i] = 0.0;
				pos[i] = 0.0;
			}
			
			// First segment, go from 0->1
			for (int i=0; i<400; i++){
				for (int sys_id = 0; sys_id<num_parallel_sys; sys_id++){
					// Measure the position
					pos[sys_id] = msd[sys_id].getPos();
					double dpos = pos[sys_id]-old_pos[sys_id];
					old_pos[sys_id] = pos[sys_id];
					
					// Calculate the force we want to apply
					double pid_force = pid[sys_id].update(dt, dpos);
					
					// Apply the force
					msd[sys_id].update(dt, pid_force);
					
					// Graph it
					pos_graph.addPoint(sys_id, dt*i, pos[sys_id]);
					control_graph.addPoint(sys_id, dt*i, pid_force);
				}
				// Graph desired position too
				pos_graph.addPoint(num_parallel_sys, dt*i, pid_setpoint);
			}
			
			// Second segment, go from 1->10
			pid_setpoint = 10;
			for (int i=0; i<num_parallel_sys; i++){
				pid[i].setSetpoint(pid_setpoint);
			}
			for (int i=500; i<1000; i++){
				for (int sys_id = 0; sys_id<num_parallel_sys; sys_id++){
					// Measure the position
					pos[sys_id] = msd[sys_id].getPos();
					double dpos = pos[sys_id]-old_pos[sys_id];
					old_pos[sys_id] = pos[sys_id];
					//System.out.println("pos: " + pos[sys_id]);
					
					// Calculate the force we want to apply
					double pid_force = pid[sys_id].update(dt, dpos);
					//System.out.println("pid_force: " + pid_force);
					
					// Apply the force
					msd[sys_id].update(dt, pid_force);
					
					// Graph it
					pos_graph.addPoint(sys_id, dt*i, pos[sys_id]);
					control_graph.addPoint(sys_id, dt*i, pid_force);
				}
				// Graph desired position too
				pos_graph.addPoint(num_parallel_sys, dt*i, pid_setpoint);
			}
			
		} catch (Exception e){
			System.out.println(e);
		}
		
		
	}
}