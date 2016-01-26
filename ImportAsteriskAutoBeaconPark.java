package com.qualcomm.ftcrobotcontroller.opmodes;


public class ImportAsteriskAutoBeaconPark extends ImportAsteriskTelemetry

{
    int directionModifier = 1; // This is what allows us to unify the code chain. 1 goes in the direction of red, -1 goes in the direction of blue. 
    
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
    public ImportAsteriskAutoBeaconPark ()

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

    @Override public void start ()

    {
        super.start ();
        reset_drive_encoders ();

    }

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
                run_using_encoders ();
                set_drive_power ((-directionModifier)*leftRightRatio*speed, directionModifier*speed);
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
                armServo.setPosition(1.0);
                break;
            default:
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
