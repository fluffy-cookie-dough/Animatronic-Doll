#include <Servo.h>
#include <SoftwareSerial.h>

SoftwareSerial BluetoothSerial(2, 3);
SoftwareSerial HeadSerial(10, 11);

Servo neck;
Servo left_arm;
Servo right_arm;
Servo waist;

void setup() 
{
  // Communication setup
  BluetoothSerial.begin(9600);
  HeadSerial.begin(115200);

  // Setup servo motors
  neck.attach(7);
  left_arm.attach(6);
  right_arm.attach(5);
  waist.attach(4);

  // Initialize motor angles
  int initial_angle = 90;
  neck.write(initial_angle);
  left_arm.write(initial_angle);
  right_arm.write(initial_angle);
  waist.write(initial_angle);
  
  // Enable serial reception
  BluetoothSerial.listen();
  HeadSerial.listen();
}

void loop() 
{
  // When a command is received from Bluetooth
  if (BluetoothSerial.available()) 
  {
    String command = BluetoothSerial.readString();

    if (command != "")
    {
      // Check the index of commas in the command
      int emotion_index = command.indexOf(",");
      int left_ear_angle_index = command.indexOf(",", emotion_index+1);
      int right_ear_angle_index = command.indexOf(",", left_ear_angle_index+1);
      int neck_angle_index = command.indexOf(",", right_ear_angle_index+1);
      int left_arm_angle_index = command.indexOf(",", neck_angle_index+1);
      int right_arm_angle_index = command.indexOf(",", left_arm_angle_index+1);
      int waist_angle_index = command.indexOf(",", right_arm_angle_index+1);

      // If there aren’t enough commas, exit the loop
      int err_index = -1;
      if (emotion_index == err_index || left_ear_angle_index == err_index || right_ear_angle_index == err_index ||
        neck_angle_index == err_index || left_arm_angle_index == err_index || right_arm_angle_index == err_index || 
        waist_angle_index == err_index)
      {
        return;
      }
      
      // Get emotion & motors angles from received command
      String emotion = command.substring(0, emotion_index);
      String left_ear_angle = command.substring(emotion_index+1, left_ear_angle_index);
      String right_ear_angle = command.substring(left_ear_angle_index+1, right_ear_angle_index);
      int neck_angle = command.substring(right_ear_angle_index+1, neck_angle_index).toInt();
      int left_arm_angle = command.substring(neck_angle_index+1, left_arm_angle_index).toInt();
      int right_arm_angle = command.substring(left_arm_angle_index+1, right_arm_angle_index).toInt();
      int waist_angle = command.substring(right_arm_angle_index+1, waist_angle_index).toInt();

      if (emotion.indexOf("(DANCE)") != -1)
      {
        Perform_Dance();
      }
      else
      {
        // Send command to the head controller
        String head_command = left_ear_angle + "," + right_ear_angle + "," + emotion;
        HeadSerial.println(head_command);
        
        // Set motor angles according to the command
        delay(3000);
        neck.write(neck_angle);
        left_arm.write(left_arm_angle);
        right_arm.write(right_arm_angle);
        waist.write(waist_angle);
      }
    }
  }
}

// Other dance performances need to be implemented as well
void Perform_Dance()
{
  // Send command to head & Change motor angles
  if (emotion == "NORMAL DANCE (DANCE)")
  {
    HeadSerial.println("80,100,SMILE");
    Perform_Normal_Dance();
  }
  else if (emotion == "GREETINNG (DANCE)")
  {
    HeadSerial.println("90,90,HELLO");
    Greeting_Dance();
  }
}

void Perform_Normal_Dance()
{
  delay(3000);
  neck.write(100);
  waist.write(80);
  Normal_Dance_ArmShake();
  Normal_Dance_ArmShake();

  delay(1000);
  neck.write(80);
  waist.write(100);
  Normal_Dance_ArmShake();
  Normal_Dance_ArmShake();
}

void Normal_Dance_ArmShake()
{
  left_arm.write(70);
  right_arm.write(110);
  delay(1000);
  
  left_arm.write(110);
  right_arm.write(70);
  delay(1000);
}

void Greeting_Dance()
{
  delay(3000);
  neck.write(82);
  waist.write(90);
  Greeting_ArmShake();
  Greeting_ArmShake();
  Greeting_ArmShake();
}

void Greeting_ArmShake()
{
  right_arm.write(110);
  delay(1000);
  
  right_arm.write(90);
  delay(1000);
}
