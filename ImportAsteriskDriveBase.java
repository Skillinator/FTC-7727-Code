package com.qualcomm.ftcrobotcontroller.opmodes;

/*
 * ImportAsteriskDriveBase
 * Last Updated 1/26/2016
 *
 * This is the teleop drive code for the robot. Pretty basic stuff going on in here, frankly.
 */
public class ImportAsteriskDriveBase extends ImportAsteriskTelemetry

{
   
    public ImportAsteriskDriveBase ()

    {
       
    } 

    @Override public void loop ()

    {
        
        float l_gp1_left_stick_y = -gamepad1.left_stick_y;
        float l_left_drive_power
                = (float)scale_motor_power (l_gp1_left_stick_y);

        float l_gp1_right_stick_y = -gamepad1.right_stick_y;
        float l_right_drive_power
                = (float)scale_motor_power (l_gp1_right_stick_y);

        set_drive_power (l_left_drive_power, l_right_drive_power);

        if(gamepad2.right_trigger > 0.5){
            armServo.setPosition(0);
        }else{
            armServo.setPosition(1.0);
        }

        v_motor_bulldozer.setPower(-gamepad2.right_stick_y/2);
        
        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        telemetry.addData ("10", "GP1 Left: " + l_gp1_left_stick_y);
        telemetry.addData ("11", "GP1 Right: " + l_gp1_right_stick_y);
        telemetry.addData ("13", "GP2 X: " + gamepad2.x);
        telemetry.addData ("14", "GP2 Y: " + gamepad2.left_stick_y);

    }

}
