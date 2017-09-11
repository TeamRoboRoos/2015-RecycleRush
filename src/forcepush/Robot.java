
package forcepush;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
	
	public static String STATUS_MESSAGE = "DB/String 0"; 
	public static String AUTONOMOUS_MESSAGE = "DB/String 5"; 
	public static String DIRECTION_MESSAGE = "DB/String 6"; 
	public static String LIFT_MESSAGE = "DB/String 7"; 
	public static String ACCEL_MESSAGE = "DB/String 3"; 
	public static String ARMS_MESSAGE = "DB/String 8"; 
	public static String BATTERY_MESSAGE = "DB/String 9"; 
	public static String SENSOR_MESSAGE = "DB/String 4"; 
	public static String MOTOR_MESSAGE = "DB/String 2"; 
	
	public static String ARMS_BUTTON = "DB/Button 0"; 
	public static String UNKNOWN_BUTTON = "DB/Button 1";
	public static String TOP_LIMITER_SWITCH = "DB/Button 2";
	public static String PARTY_BUTTON = "DB/Button 3"; 
	
	public static String SPEED_MULTIPLIER = "DB/Slider 0";
	public static String LIFT_MULTIPLIER = "DB/Slider 1";
	public static String AUTONOMOUS_CHOICE = "DB/Slider 2";

	public static String ACTIVE_LIGHT = "DB/LED 0"; 
	public static String PARTY_LIGHT = "DB/LED 3"; 
	
	private Watcher watcher;
	
	private Thread watcherThread;
	
	private Autonomous[] autonmousOptions = {
			new DriveForward(this),
			new DriveBack(this) };

    RobotDrive driveBase;
    Joystick driveStick;
    
    Accelerometer accel;
    
    Drivebase drivebase;
    
    SensorBar sensorBar;
    
    PowerDistributionPanel PDP;
    
    Lift lift;
    
    int goodDelay = 50;
    int goodDelayCount;
    
    private Controller controller;
    
    boolean canSemiAutonomous = true;
    boolean canDrive = false;
    
    private AutonomousController autonomousController;
    
    
    public Robot() {
        this.drivebase = new Drivebase(this);
        
        driveBase = this.drivebase.getRobotDrive();
        
        driveStick = new Joystick(1);
        
        this.controller = new Controller(this, 0);
        
        sensorBar = new SensorBar();
        
        PDP = new PowerDistributionPanel();
        
        this.accel = new BuiltInAccelerometer();
        
        this.lift = new Lift(this);
        
	    this.displayLED("DB/LED 0", false);
	    this.displayLED("DB/LED 1", false);
	    this.displayLED("DB/LED 2", false);
	    this.displayLED("DB/LED 3", false);
        
        this.watcher = new Watcher(this);
        Thread watcherThread = new Thread(this.watcher);
        watcherThread.start();
        
	    SmartDashboard.putBoolean("DB/LED 3", true);
	    
	    this.autonomousController = new AutonomousController(this);
	    
	   // this.autonomousModeDisplay();
    }
    
    public RobotDrive getRobotDrive()
    {
    	return driveBase;
    }
    
    
    public void setButton(String buttonName, boolean val)
    {
    	SmartDashboard.putBoolean(buttonName, val);
    
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() 
    {
    	driveBase.setSafetyEnabled(false);
    	
    	this.clearOutputs();
    	
    	double rawAutoChoice = SmartDashboard.getNumber(Robot.AUTONOMOUS_CHOICE);
    	int autoChoice = (int)rawAutoChoice;
    	
    	if (autoChoice < this.autonmousOptions.length)
    	{
    		this.displayText(Robot.AUTONOMOUS_MESSAGE, "A: " + this.autonmousOptions[autoChoice].getName());
    		this.autonmousOptions[autoChoice].run();
    	}
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	
        driveBase.setSafetyEnabled(false);
        
        while (isOperatorControl() && isEnabled()) 
        {
        	this.displayText(this.ACCEL_MESSAGE, "" + accelerometerDisplay());
        	
        	if (!this.canDrive)
        	{
        		this.drivebase.stop();
        	}
        	
        	if (!this.canDrive && this.controller.getSpeedTrigger())
        	{
        	    this.displayLED(Robot.ACTIVE_LIGHT, true);
        		this.canDrive = true;
        	}
        	
        	this.clearOutputs();
        	PDP.updateTable();
        	//this.displayText(BATTERY_MESSAGE, "" + PDP.getTotalPower() + "w");  //sends the current power draw to the dash

        	// Check for driverassist button
            if (!driveStick.getRawButton(2))
            {
                if (driveStick.getRawButton(7) && canSemiAutonomous)
                {
                	if (this.sensorBar.getMode() == SensorBar.IDLE)
                	{
                		this.sensorBar.setMode(SensorBar.DRIVER_ASSIST);
                    	this.displayText(STATUS_MESSAGE, "Driver Assist");
                	}
                }
                else if (SmartDashboard.getBoolean(Robot.PARTY_BUTTON))
                {
                	this.sensorBar.setMode(SensorBar.PARTY_MODE);
                    this.displayText(STATUS_MESSAGE, "Party");
                }
                else
                {
                	// Sensor bar
                	this.displayText(STATUS_MESSAGE, "Operator");
            		this.sensorBar.setMode(SensorBar.IDLE);
                }

        		// -----------------------------------------------------------------------------------------
        		// Lifting the arms up and down
        		
        		// First check - we raise the arms if the up button is true, and the down button is not
        		// being pushed.
        		if (this.controller.getUpButton() && !this.controller.getDownButton())
        		{
        			if (this.controller.getUpButton() && this.controller.getSpeedTrigger() && false) 
        				//if trigger is held as well, raised the height of one tote
            		{
            			this.lift.raiseATote(0.8f);
            		}
        			else // otherwise it raised until button is let go
        			{
        				this.lift.raise(0.8f);//was 0.4f
        			}
        			
        		}
        		
        		// Second check - we lower the arms if the down button is being pushed and the up button
        		// is not.
        		if (this.controller.getDownButton() && !this.controller.getUpButton())
        		{
        			if (this.controller.getDownButton() && this.controller.getSpeedTrigger() && false)
        				// same logic as the raise part
        			{
        				this.lift.lowerATote(0.5f);
        			}
        			else
        			{
        				this.lift.lower(0.5f);//was 0.25f
        			}
        		}
        		
        		// Third check - if either both buttons are being pushed, or neither button is, we stop.
        		// Note that later we might not stop, so much as keep going until a set height is reached.
        		if (this.controller.getDownButton() == this.controller.getUpButton())
        		{
        			this.lift.stop();
        		}
        		
        		// -----------------------------------------------------------------------------------------
        		// Moving the arms in and out
        		
        		// First check - we move the arms out if the out button is true, and the in button is not
        		// being pushed.
        		if ((this.controller.getOutButton() && !this.controller.getInButton()) || SmartDashboard.getBoolean(Robot.ARMS_BUTTON))
        		{
        			this.lift.moveOut();
                	this.sensorBar.setArms(SensorBar.ARMS_WIDE);
                    this.displayText(ARMS_MESSAGE, "Wide");
                    SmartDashboard.putBoolean(Robot.ARMS_BUTTON, true);
        		}
        		
        		// Second check - we move the arms in if the in button is being pushed and the out button
        		// is not.
        		if (this.controller.getInButton() && !this.controller.getOutButton() || !SmartDashboard.getBoolean(Robot.ARMS_BUTTON))
        		{
        			this.lift.moveIn();
                	this.sensorBar.setArms(SensorBar.ARMS_NARROW);
                    this.displayText(ARMS_MESSAGE, "Narrow");
                    SmartDashboard.putBoolean(Robot.ARMS_BUTTON, false);
        		}
        		
        		
        		// -----------------------------------------------------------------------------------------
        		// Moving the robot
    			if (this.canDrive)
    			{
	        		if (this.controller.getPOVLeft())
	        		{
	        			this.drivebase.left(0.8);
	        		}
	        		else if (this.controller.getPOVRight())
	        		{
	        			this.drivebase.right(0.8);
	        		}
	        		else if (this.controller.getPOVForward())
	        		{

	        			this.drivebase.forward(0.8);
	        		}
	        		else if (this.controller.getPOVBack())
	        		{
	        			this.drivebase.reverse(0.8);
	        		}
	        		else
	        		{
	        			canSemiAutonomous = true;
	        	
		            	boolean squaredInputs = true; //squares the inputs for fine-grained control
		        	
		            	//get raw values
		            	double axisX = driveStick.getX();
		            	double axisY = driveStick.getY();
		            	double axisZ = driveStick.getTwist();
		        	
		            	//square the inputs for more fine-grained control at lower speeds
		            	if (squaredInputs) {
		            		axisX = (axisX<0 ? -1 : 1)*Math.pow(axisX, 2);
		            		axisY = (axisY<0 ? -1 : 1)*Math.pow(axisY, 2);
		            		axisZ = (axisZ<0 ? -1 : 1)*Math.pow(axisZ, 2);
		            	}
		            	//System.out.println("X: "+axisX+"\nY: "+axisY+"\nZ: "+axisZ);
		            	this.drivebase.drive(axisX, axisY , axisZ);
        			}
        		}
            }
            
            if (driveStick.getRawButton(2) && canSemiAutonomous && this.canDrive)
            {
            	this.autonomousController.autoPosition();
            }
        }
    }

    private String String(double totalPower) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * Runs during test mode
     */
    public void test() {
    }
    
    public void autoPosition()
    {
    	this.autonomousController.autoPosition();
    }
 
    
    private void clearOutputs()
    {
    	/*
    	SmartDashboard.putString("DB/String 0", "Status");
    	SmartDashboard.putString("DB/String 5", "");
    	
    	SmartDashboard.putString("DB/String 1", "Direction");
    	SmartDashboard.putString("DB/String 6", "");
    	
    	SmartDashboard.putString("DB/String 2", "Lift");
    	SmartDashboard.putString("DB/String 7", "");
    	
    	//SmartDashboard.putString("DB/String 3", "Arms");
    	//SmartDashboard.putString("DB/String 8", "");
    	
    	SmartDashboard.putString("DB/String 4", "Message");
    	SmartDashboard.putString("DB/String 9", "");
    	*/
    }
    
    public void displayText(String location, String text)
    {
    	SmartDashboard.putString(location, text);
    }
    
    public void displayLED(String location, boolean state)
    {
    	SmartDashboard.putBoolean(location, state);
    }
    
    public Lift getLift()
    {
    	return this.lift;
    }
    
    public SensorBar getSensorBar()
    {
    	return this.sensorBar;
    }
    
    public Drivebase getDrivebase()
    {
    	return this.drivebase;
    }
    
    public Controller getController()
    {
    	return this.controller;
    }
    
    public Accelerometer getAccelerometer()
    {
    	return this.accel;
    }

    
    public Boolean getSemiAutonomous()
    {
    	return this.canSemiAutonomous;
    }
    
    public void setSemiAutonomous(boolean canSemiAutonomous)
    {
    	this.canSemiAutonomous = canSemiAutonomous;
    }

    public String accelerometerDisplay()  //Jack. Get and round accelerometer values for display
    {
    	return (this.getAccelerometerX() + " X m/s/s" + "  " + this.getAccelerometerY()  + " Y m/s/s");
    }

    public double getAccelerometerX()  //Jack. Get and round accelerometer values for display
    {
    	double x = this.accel.getX()*9.8;
    	
    	x = (x+0.2)*100;
   		x = Math.round(x);
   		x = x/100; 

    	if (x > -0.1 &&  x < 0.1)
   		{
   			x = 0.00;
   		}
    	
    	return x;
    }

    public double getAccelerometerY() 
    {
   		double y = this.accel.getY() * 9.8;
   		
   		y = y * 100;   
   		y = Math.round(y);
    	y = y / 100;
    	
    	if (y > -0.1 &&  y < 0.1)
    	{
    		y = 0.00;
    	}
    	
    	return y;
    }
}
