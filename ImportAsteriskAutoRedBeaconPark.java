package com.qualcomm.ftcrobotcontroller.opmodes;

public class ImportAsteriskAutoBlueBeaconPark extends ImportAsteriskAutoBeaconPark

{

    public ImportAsteriskAutoBlueBeaconPark ()

    {
        
    }
    
    @Override public void start ()

    {
        super.start ();
        directionModifier = 1;

        reset_drive_encoders ();

    }

}
