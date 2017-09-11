package forcepush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Watcher implements Runnable
{
	private Robot robot;
	
	private boolean stopThread = false;
	
	private DigitalInput topLimiter;
	private DigitalInput bottomLimiter;
	private Talon motor;
	
	public Watcher(Robot robot)
	{
		this.robot = robot;
		
		this.topLimiter = this.robot.getLift().getTopLimiter();
		this.bottomLimiter = this.robot.getLift().getBottomLimiter();
		this.motor = this.robot.getLift().getMotor();
	}
	
	public void stopThread()
	{
		this.stopThread = true;
	}
	
	public void run() 
	{
    	this.robot.displayText(Robot.ARMS_MESSAGE, "Help! Scott's arduino is burning!");
	    System.out.println("Thread begun");
		try 
		{
	         while (true) 
	         {
	        	 if (this.stopThread == true) 
	        	 {
	        		 throw new InterruptedException("Stopped");
	        	 }
	        	 
	        	 if (this.robot.getLift().isMoving())
	        	 {
	        		 this.robot.getLift().move();
	        	 }
	        	 
	     		 if((this.robot.getLift().getDirection() > 0 ||this.robot.getLift().raiseSpeed > 0)&& this.topLimiter.get() == false)
	     		 {
	     			 this.motor.set(0.05);
	     		     this.motor.set(0);
	     		     this.robot.getLift().raiseSpeed = 0;
	     		 }
	     		
	     		 if((this.robot.getLift().getDirection() < 0 || this.robot.getLift().raiseSpeed < 0)&& this.bottomLimiter.get() == false)
	     		 {
	     			 this.motor.set(-0.05);
	     		     this.motor.set(0);
	     		     this.robot.getLift().raiseSpeed = 0;
	     		 }
	     		 
	     		 if(this.robot.getLift().getStoppedStatus() == true)
	     			 // if the robot arms are meant to be stopped
	     		 {
	     			 if (this.robot.getLift().getStoppedHeight() > this.robot.getLift().getCurrentHeight()) 
	     				 // if the current height is less than the height it was stopped at
	     			 {
	     				 if (this.robot.getLift().raiseSpeed > -0.1)
	     					 // and the speed of the arms is not already insanely fast
	     				 {
	     					 this.robot.getLift().raiseSpeed -= 0.01; 
	     					 this.robot.getLift().getMotor().set(this.robot.getLift().raiseSpeed);
	     					// increase the upwards speed of the arms so they approach the height they were stopped at
	     				 }
	     			 }
	     			 if (this.robot.getLift().getStoppedHeight() < this.robot.getLift().getCurrentHeight())
	     				 // if the current height is higher than the height they were stopped at
	     			 {
	     				 if (this.robot.getLift().raiseSpeed < 0.1)
	     					 // and the downwards speed is not already insanely fast
	     				 {
	     					this.robot.getLift().raiseSpeed += 0.01;
	     					this.robot.getLift().getMotor().set(this.robot.getLift().raiseSpeed);
	     					// increase the downwards speed slightly so they approach the arm they were stopped at
	     				 }
	     			 }
	     		 }
	         }
	     } 
		catch (InterruptedException e) 
		{
			
		}
	}
}
