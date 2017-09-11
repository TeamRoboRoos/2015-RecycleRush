package forcepush;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SensorBar {
	 private SerialPort serialPort = new SerialPort(9600, SerialPort.Port.kMXP, 8, SerialPort.Parity.kNone, SerialPort.StopBits.kOne);

	 public final static int GOOD = 9;
	 public final static int FORWARD = 2;
	 public final static int BACK = 3;
	 public final static int LEFT = 4;
	 public final static int RIGHT = 5;
	 public final static int TURN_LEFT = 6;
	 public final static int TURN_RIGHT = 7;
	 public final static int BACK_A_BIT = 8;
	 public final static int FORWARD_A_BIT = 1;
	 public final static int BAD_THINGS_HAPPENED = 0;
	 
	 public final static byte IDLE = 1;
	 public final static byte US_INITIAL_APPROACH = 2;
	 public final static byte IR_AUTONOMOUS = 3;
	 public final static byte US_FINAL_APPROACH = 4;
	 public final static byte DRIVER_MODE = 5;
	 public final static byte DRIVER_ASSIST = 6;
	 public final static byte PARTY_MODE = 7;
	 
	 public final static byte ARMS_WIDE = 8;
	 public final static byte ARMS_NARROW = 9;
	 
	 private byte armState;
	 
	 private String lastValue;
	 
	 private byte mode = IDLE;
	 
	 public SensorBar ()
	 {
		 serialPort.setReadBufferSize(1);
		 lastValue = "" + BAD_THINGS_HAPPENED;
		 this.armState = ARMS_NARROW;
	 }
	 
	 public int getDirection()
	 {
		 String input = serialPort.readString();
		 
		 String output = "" + BAD_THINGS_HAPPENED;
		 
		 if (!input.equals("") && !input.equals("0") && !input.equals("99"))
		 {
			 output = input;
			 lastValue = input;
		 }
		
		 else 
		 {
			 output = lastValue;
		 }
		 
		 int out;
		 
		 try 
		 {
			 out = Integer.parseInt("" + (output.charAt(0)));
		 }
		 catch (Exception e)
		 {
			 out = 0;
		 }
		 
		 return out;
	 }
	 
	 public void setMode(byte mode)
	 {
		 if (mode != this.mode)
		 {
			 byte[] number = { mode };
			 serialPort.write(number,1);
			 this.mode = mode;
			 
			 while (getDirection() == GOOD)
			 {
				 getDirection();
			 }
		 }
	 }
	 
	 public byte getMode()
	 {
		 return this.mode;
	 }

	 
	 public void setArms(byte mode)
	 {
		 if (mode != this.armState)
		 {
			 byte[] number = { mode };
			 serialPort.write(number,1);
			 this.armState = mode;
			 
			 while (getDirection() == GOOD)
			 {
				 getDirection();
			 }
		 }
	 }
	 
	 public byte getArms()
	 {
		 return this.armState;
	 }
}
