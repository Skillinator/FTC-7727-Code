package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotTelemetry
//
/**
 * Extends the PushBotHardware class to provide basic telemetry for the Push
 * Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-02-13-57
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
