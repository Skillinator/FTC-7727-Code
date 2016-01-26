package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotAuto
//
/**
 * Extends the PushBotTelemetry and PushBotHardware classes to provide a basic
 * autonomous operational mode for the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class ImportAsteriskAutoBlueBeaconPark extends ImportAsteriskTelemetry

{
    //--------------------------------------------------------------------------
    //
    // v_state
    //
    //--------
    // This class member remembers which state is currently active.  When the
    // start method is called, the state will be initialized (0).  When the loop
    // starts, the state will change from initialize to state_1.  When state_1
    // actions are complete, the state will change to state_2.  This implements
    // a state machine for the loop method.
    //--------
    int v_state = 0;

    int first_drive = 5800;
    int turn = 450;
    int endDrive = 2200;

    float leftRightRatio = 0.25f;
    float speed = 0.6f;
    boolean armStartInit = false;
    boolean triggered = false;
    long armStart = 0;

    /**
     * Constructs the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public ImportAsteriskAutoBlueBeaconPark ()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotAuto::PushBotAuto

    //--------------------------------------------------------------------------
    //
    // start
    //
    /**
     * Performs any actions that are necessary when the OpMode is enabled.
     *
     * The system calls this member once when the OpMode is enabled.
     */
    @Override public void start ()

    {
        super.start ();

        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_drive_encoders ();

    } // PushBotAuto::start

    //--------------------------------------------------------------------------
    //
    // loop
    //
    /**
     * Implement a state machine that controls the robot during auto-operation.
     *
     * The system calls this member repeatedly while the OpMode is running.
     */
    static int[] l_times = new int [3];
    @Override public void loop ()

    {

        //----------------------------------------------------------------------
        //
        // State: Initialize (i.e. state_0).
        //
        switch (v_state)
        {
            //
            // Synchronoize the state machine and hardware.
            //
            case 0:
                //
                // Reset the encoders to ensure they are at a known good value.
                //
                reset_drive_encoders ();

                //
                // Transition to the next state when this method is called again.
                //
                l_times[0] = 0;
                l_times[1] = 0;
                l_times[2] = 0;
                v_state++;

                break;
            //
            // Drive forward until the encoders exceed the specified values.
            //
            case 1:
                //
                // Tell the system that motor encoders will be used.  This call MUST
                // be in this state and NOT the previous or the encoders will not
                // work.  It doesn't need to be in subsequent states.
                //
                run_using_encoders ();

                //
                // Start the drive wheel motors at full power.
                //
                set_drive_power (leftRightRatio*speed, speed);

                //
                // Have the motor shafts turned the required amount?
                //
                // If they haven't, then the op-mode remains in this state (i.e this
                // block will be executed the next time this method is called).
                //
                if (have_drive_encoders_reached (first_drive, first_drive))
                {
                    //
                    // Reset the encoders to ensure they are at a known good value.
                    //
                    reset_drive_encoders ();

                    //
                    // Stop the motors.
                    //
                    set_drive_power (0.0f, 0.0f);

                    //
                    // Transition to the next state when this method is called
                    // again.
                    //
                    v_state++;
                }
                break;
            //
            // Wait...
            //
            case 2:
                if (have_drive_encoders_reset ())
                {
                    v_state++;
                }
                else
                {
                    l_times[0]++;
                }
                break;
            //
            // Turn left until the encoders exceed the specified values.
            //
            case 3:
                run_using_encoders();
                set_drive_power (leftRightRatio*speed, -speed);
                if (have_drive_encoders_reached (turn, turn))
                {
                    reset_drive_encoders ();
                    set_drive_power (0.0f, 0.0f);
                    v_state++;
                }
                break;
            //
            // Wait...
            //
            case 4:
                if (have_drive_encoders_reset ())
                {
                    v_state++;
                }
                else
                {
                    l_times[1]++;
                }
                break;
            //
            // Turn right until the encoders exceed the specified values.
            //
            case 5:
                run_using_encoders ();
                set_drive_power (leftRightRatio*speed, speed);
                if (have_drive_encoders_reached (endDrive, endDrive))
                {
                    reset_drive_encoders ();
                    set_drive_power (0.0f, 0.0f);
                    v_state++;
                }
                break;
            //
            // Wait...
            //
            case 6:
                if (have_drive_encoders_reset ())
                {
                    v_state++;
                }
                else
                {
                    l_times[2]++;
                }
                break;
            case 7:
                if(!armStartInit) {
                    armStart = System.currentTimeMillis();
                    armStartInit = true;
                }
                v_motor_autoArm.setPower(-0.25);
                if((System.currentTimeMillis() - armStart) > 1000){
                    v_motor_autoArm.setPower(0.0);
                    v_state++;
                    triggered = true;
                }

                break;
            case 8:
                climberServo.setPosition(1.0);
            //
            // Perform no action - stay in this case until the OpMode is stopped.
            // This method will still be called regardless of the state machine.
            //
            default:
                v_motor_autoArm.setPower(0.0);
                //
                // The autonomous actions have been accomplished (i.e. the state has
                // transitioned into its final state.
                //
                break;
        }

        update_telemetry (); // Update common telemetry

        telemetry.addData ("13", "Time1: " + armStart);
        telemetry.addData("14", "Time2: " + System.currentTimeMillis());
        telemetry.addData("15", "TimeDelta:" + (System.currentTimeMillis() - armStart));
        telemetry.addData("16", "Triggered:" + triggered);
    } // PushBotAuto::loop

} // PushBotAuto
