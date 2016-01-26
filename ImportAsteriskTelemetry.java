package com.qualcomm.ftcrobotcontroller.opmodes;


/*
 * ImportAsteriskTelemetry
 * Last Updated 1/26/2016
 * 
 * This file serves to report back telemetry information to the drive station phone.
 * Not a whole lot of interesting stuff going on here.
 */
 
public class ImportAsteriskTelemetry extends ImportAsteriskHardware

{
    //--------------------------------------------------------------------------
    //
    // update_telemetry
    //
    /**
     * Update the telemetry with current values from the base class.
     */
    public void update_telemetry ()

    {
        //
        // Send telemetry data to the driver station.
        //
        telemetry.addData
                ( "01"




                        , "Left Drive: "
                                + a_left_drive_power ()
                                + ", "
                                + a_left_encoder_count ()
                );
        telemetry.addData
                ( "02"
                        , "Right Drive: "
                                + a_right_drive_power ()
                                + ", "
                                + a_right_encoder_count ()
                );
    } // PushBotTelemetry::loop

} // PushBotTelemetry
