/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.auton;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.DefaultFunctions;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;
import java.util.Locale;

@Autonomous(name = "24 Points left", group = "Final Autonomous")
public class TwoFourPtsLeft extends LinearOpMode {

    private DcMotor armMotor1;
    private DcMotor armMotor2;
    private Servo intakeServo;

    public void armMovement(int armTicks) throws InterruptedException {
        armMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armMotor1.setTargetPosition(armTicks);
        armMotor1.setPower(0.5);
        armMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armMotor2.setTargetPosition(armTicks);
        armMotor2.setPower(0.5);
        armMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (armMotor1.isBusy()) {
        idle();
    }
}
    public void servoPositioning(double servoPosition) {intakeServo.setPosition(servoPosition);}

    private DefaultFunctions defaultFunctions;
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double TagSize = 0.166;

    // Tag ID 18 from the 36h11 family
    int LEFT = 1;
    int MIDDLE = 2;
    int RIGHT = 3;

    AprilTagDetection tagOfInterest = null;

    @Override
    public void runOpMode() throws InterruptedException{
        armMotor1 = hardwareMap.get(DcMotor.class, "armmotor1");
        armMotor2= hardwareMap.get(DcMotor.class, "armmotor2");

        intakeServo = hardwareMap.get(Servo.class, "Intake");

        armMotor1.setDirection(DcMotorSimple.Direction.FORWARD);
        armMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(TagSize, fx, fy, cx, cy);

        SampleMecanumDrive drivetrain = new SampleMecanumDrive(hardwareMap);

        TrajectorySequence LeftPark = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineTo(new Vector2d(0, 32))
                .build();

        TrajectorySequence RightPark = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineTo(new Vector2d(0, -10))
                .build();

        TrajectorySequence MiddlePark = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineTo(new Vector2d(0, 12))
                .build();

        TrajectorySequence FirstBloodPartOne = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineTo(new Vector2d(0, -2.5))
                .lineTo(new Vector2d(26,-2.5))
                .lineTo(new Vector2d(26, -8.5))
                .build();

        TrajectorySequence FirstBloodPartTwo = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineTo(new Vector2d(-4,0))
                .build();

        TrajectorySequence FirstBloodPartThree = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineTo(new Vector2d(4,0))
                .build();

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

        telemetry.setMsTransmissionInterval(50);
        servoPositioning(0.3);

        while (!isStarted() && !isStopRequested())
        {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if(currentDetections.size() != 0)
            {
                boolean tagFound = false;

                for(AprilTagDetection tag : currentDetections)
                {
                    if(tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT)
                    {
                        tagOfInterest = tag;
                        tagFound = true;
                        break;
                    }
                }

                if(tagFound)
                {
                    telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                    tagToTelemetry(tagOfInterest);
                }
                else
                {
                    telemetry.addLine("Don't see tag of interest :(");

                    if(tagOfInterest == null)
                    {
                        telemetry.addLine("(The tag has never been seen)");
                    }
                    else
                    {
                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                        tagToTelemetry(tagOfInterest);
                    }
                }

            }
            else
            {
                telemetry.addLine("Don't see tag of interest :(");

                if(tagOfInterest == null)
                {
                    telemetry.addLine("(The tag has never been seen)");
                }
                else
                {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                    tagToTelemetry(tagOfInterest);
                }

            }

            telemetry.update();
            sleep(20);
        }

        /*
         * The START command just came in: now work off the latest snapshot acquired
         * during the init loop.
         */

        /* Update the telemetry */
        if(tagOfInterest != null)
        {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
        }
        else
        {
            telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
            telemetry.update();
        }

        if(tagOfInterest == null || tagOfInterest.id == LEFT){
            drivetrain.followTrajectorySequence(FirstBloodPartOne);
            drivetrain.followTrajectorySequence(FirstBloodPartTwo);
            armMovement(1600);
            drivetrain.followTrajectorySequence(FirstBloodPartThree);
            armMovement(-600);
            servoPositioning(0.5);
            armMovement(300);
            drivetrain.followTrajectorySequence(LeftPark);
        }else if(tagOfInterest.id == MIDDLE){
            drivetrain.followTrajectorySequence(FirstBloodPartOne);
            drivetrain.followTrajectorySequence(FirstBloodPartTwo);
            armMovement(1600);
            drivetrain.followTrajectorySequence(FirstBloodPartThree);
            armMovement(-600);
            servoPositioning(0.5);
            armMovement(300);
            drivetrain.followTrajectorySequence(MiddlePark);
        }else if(tagOfInterest.id == RIGHT){
            drivetrain.followTrajectorySequence(FirstBloodPartOne);
            drivetrain.followTrajectorySequence(FirstBloodPartTwo);
            armMovement(1600);
            drivetrain.followTrajectorySequence(FirstBloodPartThree);
            armMovement(-600);
            servoPositioning(0.5);
            armMovement(300);
            drivetrain.followTrajectorySequence(RightPark);
        }
    }

    void tagToTelemetry(@NonNull AprilTagDetection detection)
    {
        telemetry.addLine(String.format(Locale.ENGLISH,"Detected tag ID=%d", detection.id));
        telemetry.addLine(String.format(Locale.ENGLISH,"Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format(Locale.ENGLISH,"Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format(Locale.ENGLISH,"Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format(Locale.ENGLISH,"Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format(Locale.ENGLISH,"Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format(Locale.ENGLISH,"Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }
}