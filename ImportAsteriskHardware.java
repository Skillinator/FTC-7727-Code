package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import android.hardware.Camera;

import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

//------------------------------------------------------------------------------
//
// PushBotHardware
//
//--------
// Extends the OpMode class to provide a single hardware access point for the
// Push Bot.
//--------
public class ImportAsteriskHardware extends OpMode
{

    //--------------------------------------------------------------------------
    //
    // v_dc_motor_controller_drive
    //
    //--------
    // This class member manages the aspects of the left drive motor.
    //--------
    private DcMotorController v_dc_motor_controller_drive;
    private DcMotorController v_dc_motor_controller_aux1;

    //--------------------------------------------------------------------------
    //
    // v_motor_left_drive
    // v_channel_left_drive
    //
    //--------
    // These class members manage the aspects of the left drive motor.
    //--------
    private DcMotor v_motor_left_drive;
    final int v_channel_left_drive = 1;

    //--------------------------------------------------------------------------
    //
    // v_motor_right_drive
    // v_channel_right_drive
    //
    //--------
    // These class members manage the aspects of the right drive motor.
    //--------
    private DcMotor v_motor_bulldozer;
    final int v_channel_bulldozer=3;

    public Servo armServo;
    final int v_channel_armServo = 1;

    //--------------------------------------------------------------------------
    //
    // PushBotHardware
    //
    //--------
    // Constructs the class.
    //;o
    // The system calls this member when the class is instantiated.
    //--------
    public ImportAsteriskHardware()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotManual::PushBotHardware
    private Camera camera;
    public CameraPreview preview;
    public Bitmap image;
    private int width;
    private int height;
    private YuvImage yuvImage = null;
    private int looped = 0;
    private String data;
    public int color;

    private int red(int pixel){
        return (pixel>>16) & 0xff;
    }

    private int green(int pixel) {
        return (pixel >> 8) & 0xff;
    }
    private int blue(int pixel){
        return pixel & 0xff;
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback(){
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters parameters = camera.getParameters();
            width = parameters.getPreviewSize().width;
            height = parameters.getPreviewSize().height;
            yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            looped += 1;
        }
    };

    private void convertImage(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 0, out);
        byte[] imageBytes = out.toByteArray();
        image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    //--------------------------------------------------------------------------
    //
    // init
    //
    //--------
    // Performs any actions that are necessary when the OpMode is enabled.
    //
    // The system calls this member once when the OpMode is enabled.
    //--------
    @Override public void init ()

    {
        //
        // Use the hardwareMap to associate class members to hardware ports.
        //
        // Note that the names of the devices (i.e. arguments to the get method)
        // must match the names specified in the configuration file created by
        // the FTC Robot Controller (Settings-->Configure Robot).
        //

        //
        // Connect the drive wheel motors.
        //
        // The direction of the right motor is reversed, so joystick inputs can
        // be more generically applied.
        //
        v_dc_motor_controller_drive
                = hardwareMap.dcMotorController.get("drive");

        v_dc_motor_controller_aux1
                = hardwareMap.dcMotorController.get("aux1");

        v_motor_left_drive = hardwareMap.dcMotor.get ("leftdrive");

        v_motor_right_drive = hardwareMap.dcMotor.get ("rightdrive");

        v_motor_bulldozer = hardwareMap.dcMotor.get("winch");
        v_motor_bulldozer.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        
        armServo = hardwareMap.servo.get("armservo");
        armServo.setPosition(0.5);

        if(((FtcRobotControllerActivity) hardwareMap.appContext).initializeCamera) {

            if (((FtcRobotControllerActivity) hardwareMap.appContext).camera == null) {
                ((FtcRobotControllerActivity) hardwareMap.appContext).camera = Camera.open();
                ((FtcRobotControllerActivity) hardwareMap.appContext).camera.startPreview();
            }

            camera = ((FtcRobotControllerActivity) hardwareMap.appContext).camera;
            camera.setPreviewCallback(previewCallback);

            Camera.Parameters parameters = camera.getParameters();
            data = parameters.flatten();


            ((FtcRobotControllerActivity) hardwareMap.appContext).initPreview(camera, this, previewCallback);
        }
    } // PushBotHardware::init

    public int highestColor(int red, int green, int blue){
        int[] color = {red,green,blue};
        int value = 0;
        for(int i = 1; i < 3; i++){
            if(color[value] < color[i]){
                value = i;
            }
        }
        return value;
    }

    public String getColor(){

        String colorString = "";
        if(yuvImage != null){
            int redValue = 0;
            int blueValue = 0;
            int greenValue = 0;
            convertImage();
            for(int x = 0; x < width; x++){
                for(int y = 0; y < height; y++) {
                    int pixel = image.getPixel(x, y);
                    redValue += red(pixel);
                    blueValue += blue(pixel);
                    greenValue += green(pixel);
                }
            }
            int color = highestColor(redValue, greenValue, blueValue);

            switch(color){
                case 0:
                    colorString="RED";
                    break;
                case 1:
                    colorString="GREEN";
                    break;
                case 2:
                    colorString="BLUE";
                    break;
            }
            telemetry.addData("Color:", "Color detected is:" + colorString);
        }
        return colorString;
    }

    //--------------------------------------------------------------------------
    //
    // start
    //
    //--------
    // Performs any actions that are necessary when the OpMode is enabled.
    //
    // The system calls this member once when the OpMode is enabled.
    //--------
    @Override public void start ()

    {
        //
        // Only actions that are common to all Op-Modes (i.e. both automatic and
        // manual) should be implemented here.
        //
        // This method is designed to be overridden.
        //

    } // PushBotHardware::start

    //--------------------------------------------------------------------------
    //
    // loop
    //
    //--------
    // Performs any actions that are necessary while the OpMode is running.
    //
    // The system calls this member repeatedly while the OpMode is running.
    //--------
    @Override public void loop ()

    {
        //
        // Only actions that are common to all OpModes (i.e. both auto and\
        // manual) should be implemented here.
        //
        // This method is designed to be overridden.
        //

    } // PushBotHardware::loop

    //--------------------------------------------------------------------------
    //
    // stop
    //
    //--------
    // Performs any actions that are necessary when the OpMode is disabled.
    //
    // The system calls this member once when the OpMode is disabled.
    //--------
    @Override public void stop ()
    {
        //
        // Nothing needs to be done for this OpMode.
        //

    } // PushBotHardware::stop

    //--------------------------------------------------------------------------
    //
    // scale_motor_power
    //
    //--------
    // Scale the joystick input using a nonlinear algorithm.
    //--------
    double scale_motor_power (double p_power)
    {
        //
        // Assume no scaling.
        //
        double l_scale = 0.0f;

        //
        // Ensure the values are legal.
        //
        double l_power = Range.clip (p_power, -1, 1);

        double[] l_array =
                { 0.00, 0.05, 0.09, 0.10, 0.12
                        , 0.15, 0.18, 0.24, 0.30, 0.36
                        , 0.43, 0.50, 0.60, 0.72, 0.85
                        , 1.00, 1.00
                };

        //
        // Get the corresponding index for the specified argument/parameter.
        //
        int l_index = (int) (l_power * 16.0);
        if (l_index < 0)
        {
            l_index = -l_index;
        }
        else if (l_index > 16)
        {
            l_index = 16;
        }

        if (l_power < 0)
        {
            l_scale = -l_array[l_index];
        }
        else
        {
            l_scale = l_array[l_index];
        }

        return l_scale;

    } // PushBotManual::scale_motor_power

    //--------------------------------------------------------------------------
    //
    // a_left_drive_power
    //
    //--------
    // Access the left drive motor's power level.
    //--------
    double a_left_drive_power ()
    {
        return v_motor_left_drive.getPower ();

    } // PushBotManual::a_left_drive_power

    //--------------------------------------------------------------------------
    //
    // a_right_drive_power
    //
    //--------
    // Access the right drive motor's power level.
    //--------
    double a_right_drive_power ()
    {
        return v_motor_right_drive.getPower ();

    } // PushBotManual::a_right_drive_power

    //--------------------------------------------------------------------------
    //
    // set_drive_power
    //
    //--------
    // Scale the joystick input using a nonlinear algorithm.
    //--------
    void set_drive_power (double p_left_power, double p_right_power)
    {
        v_motor_left_drive.setPower (p_left_power);
        v_motor_right_drive.setPower (p_right_power);

    } // PushBotManual::set_drive_power

    void drive_to (int left_position, int right_position)
    {
        v_motor_left_drive.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        v_motor_right_drive.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        v_motor_left_drive.setTargetPosition(left_position);
        v_motor_right_drive.setTargetPosition(right_position);
        v_motor_left_drive.setPower(0.25);
        v_motor_right_drive.setPower(0.25);

    }

    //--------------------------------------------------------------------------
    //
    // run_using_encoders
    //
    /**
     * Sets both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_using_encoders ()

    {
        DcMotorController.RunMode l_mode
                = v_dc_motor_controller_drive.getMotorChannelMode
                ( v_channel_left_drive
                );
        if (l_mode == DcMotorController.RunMode.RESET_ENCODERS)
        {
            v_dc_motor_controller_drive.setMotorChannelMode
                    ( v_channel_left_drive
                            , DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
        }

        l_mode = v_dc_motor_controller_drive.getMotorChannelMode
                ( v_channel_right_drive
                );
        if (l_mode == DcMotorController.RunMode.RESET_ENCODERS)
        {
            v_dc_motor_controller_drive.setMotorChannelMode
                    ( v_channel_right_drive
                            , DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
        }

    } // PushBotAuto::run_using_encoders

    //--------------------------------------------------------------------------
    //
    // reset_drive_encoders
    //
    /**
     * Resets both drive wheel encoders.
     */
    public void reset_drive_encoders ()

    {
        //
        // Reset the motor encoders on the drive wheels.
        //
        v_dc_motor_controller_drive.setMotorChannelMode
                ( v_channel_left_drive
                        , DcMotorController.RunMode.RESET_ENCODERS
                );

        v_dc_motor_controller_drive.setMotorChannelMode
                ( v_channel_right_drive
                        , DcMotorController.RunMode.RESET_ENCODERS
                );

    } // PushBotAuto::reset_drive_encoders

    //--------------------------------------------------------------------------
    //
    // a_left_encoder_count
    //
    //--------
    // Access the left encoder's count.
    //--------
    int a_left_encoder_count ()
    {
        return -v_motor_left_drive.getCurrentPosition ();

    } // PushBotManual::a_left_encoder_count

    //--------------------------------------------------------------------------
    //
    // a_right_encoder_count
    //
    //--------
    // Access the right encoder's count.
    //--------
    int a_right_encoder_count ()
    {
        return v_motor_right_drive.getCurrentPosition ();

    } // PushBotManual::a_right_encoder_count

    //--------------------------------------------------------------------------
    //
    // have_drive_encoders_reached
    //
    //--------
    // Scale the joystick input using a nonlinear algorithm.
    //--------
    boolean have_drive_encoders_reached
    ( double p_left_count
            , double p_right_count
    )
    {
        //
        // Assume failure.
        //
        boolean l_status = false;

        //
        // Have the encoders reached the specified values?
        //
        // TODO Implement stall code using these variables.
        //
        if ((Math.abs (v_motor_left_drive.getCurrentPosition ()) > p_left_count) &&
                (Math.abs (v_motor_right_drive.getCurrentPosition ()) > p_right_count))
        {
            //
            // Set the status to a positive indication.
            //
            l_status = true;
        }

        //
        // Return the status.
        //
        return l_status;

    } // PushBotManual::have_encoders_reached

    //--------------------------------------------------------------------------
    //
    // have_drive_encoders_reset
    //
    //--------
    // Scale the joystick input using a nonlinear algorithm.
    //--------
    boolean have_drive_encoders_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_status = false;

        //
        // Have the encoders reached zero?
        //
        if ((a_left_encoder_count() == 0) && (a_right_encoder_count() == 0))
        {
            //
            // Set the status to a positive indication.
            //
            l_status = true;
        }

        //
        // Return the status.
        //
        return l_status;

    } // PushBotManual::have_drive_encoders_reset


}
