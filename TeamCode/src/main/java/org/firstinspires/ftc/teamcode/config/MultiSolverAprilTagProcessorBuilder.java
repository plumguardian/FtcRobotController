/*
 * Copyright (c) 2023 FIRST
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.config;

import static org.firstinspires.ftc.vision.apriltag.AprilTagProcessor.THREADS_DEFAULT;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.MultiSolverAprilTagProcessorImpl;

@Deprecated
public class MultiSolverAprilTagProcessorBuilder {
    private Position cameraPosition = new Position();
    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0);
    private double fx, fy, cx, cy;
    private AprilTagProcessor.TagFamily tagFamily = AprilTagProcessor.TagFamily.TAG_36h11;
    private AprilTagLibrary tagLibrary = AprilTagGameDatabase.getCurrentGameTagLibrary();
    private DistanceUnit outputUnitsLength = DistanceUnit.INCH;
    private AngleUnit outputUnitsAngle = AngleUnit.DEGREES;
    private int threads = THREADS_DEFAULT;
    private boolean suppressCalibrationWarnings;

    private boolean drawAxes = true;  // Changed to highlight Cluster Origin Location.
    private boolean drawCube = false;
    private boolean drawOutline = true;
    private boolean drawTagId = true;

    /**
     * Set the camera pose relative to the robot origin.
     * @param position Position of camera relative to the robot origin
     * @param orientation Orientation of camera relative to the robot origin
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setCameraPose(Position position, YawPitchRollAngles orientation)
    {
        cameraPosition = position;
        cameraOrientation = orientation;

        return this;
    }

    /**
     * Set the camera calibration parameters (needed for accurate 6DOF pose unless the
     * SDK has a built in calibration for your camera)
     * @param fx see opencv 8 parameter camera model
     * @param fy see opencv 8 parameter camera model
     * @param cx see opencv 8 parameter camera model
     * @param cy see opencv 8 parameter camera model
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setLensIntrinsics(double fx, double fy, double cx, double cy)
    {
        this.fx = fx;
        this.fy = fy;
        this.cx = cx;
        this.cy = cy;
        return this;
    }

    /**
     * Set whether any warnings about camera calibration should be suppressed
     * @param suppressCalibrationWarnings whether to suppress calibration warnings
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setSuppressCalibrationWarnings(boolean suppressCalibrationWarnings)
    {
        this.suppressCalibrationWarnings = suppressCalibrationWarnings;
        return this;
    }

    /**
     * Set the tag family this detector will be used to detect (it can only be used
     * for one tag family at a time)
     * @param tagFamily the tag family this detector will be used to detect
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setTagFamily(AprilTagProcessor.TagFamily tagFamily)
    {
        this.tagFamily = tagFamily;
        return this;
    }

    /**
     * Inform the detector about known tags. The tag library is used to allow solving
     * for 6DOF pose, based on the physical size of the tag. Tags which are not in the
     * library will not have their pose solved for
     * @param tagLibrary a library of known tags for the detector to use when trying to solve pose
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setTagLibrary(AprilTagLibrary tagLibrary)
    {
        this.tagLibrary = tagLibrary;
        return this;
    }

    /**
     * Set the units you want translation and rotation data provided in, inside any
     * {@link AprilTagPoseRaw} or {@link AprilTagPoseFtc} objects
     * @param distanceUnit translational units
     * @param angleUnit rotational units
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setOutputUnits(DistanceUnit distanceUnit, AngleUnit angleUnit)
    {
        this.outputUnitsLength = distanceUnit;
        this.outputUnitsAngle = angleUnit;
        return this;
    }

    /**
     * Set whether to draw a 3D crosshair on the tag (what Vuforia did)
     * @param drawAxes whether to draw it
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setDrawAxes(boolean drawAxes)
    {
        this.drawAxes = drawAxes;
        return this;
    }

    /**
     * Set whether to draw a 3D cube projecting from the tag
     * @param drawCube whether to draw it lol
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setDrawCubeProjection(boolean drawCube)
    {
        this.drawCube = drawCube;
        return this;
    }

    /**
     * Set whether to draw a 2D outline around the tag detection
     * @param drawOutline whether to draw it
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setDrawTagOutline(boolean drawOutline)
    {
        this.drawOutline = drawOutline;
        return this;
    }

    /**
     * Set whether to annotate the tag detection with its ID
     * @param drawTagId whether to annotate the tag with its ID
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setDrawTagID(boolean drawTagId)
    {
        this.drawTagId = drawTagId;
        return this;
    }

    /**
     * Set the number of threads the tag detector should use
     * @param threads the number of threads the tag detector should use
     * @return the {@link MultiSolverAprilTagProcessorBuilder} object, to allow for method chaining
     */
    public MultiSolverAprilTagProcessorBuilder setNumThreads(int threads)
    {
        this.threads = threads;
        return this;
    }

    /**
     * Create a {@link VisionProcessor} object which may be attached to
     * a {@link org.firstinspires.ftc.vision.VisionPortal} using
     * {@link org.firstinspires.ftc.vision.VisionPortal.Builder#addProcessor(VisionProcessor)}
     * @return a {@link VisionProcessor} object
     */
    public MultiSolverAprilTagProcessorImpl build()
    {
        if (tagLibrary == null)
        {
            throw new RuntimeException("Cannot create AprilTagProcessor without setting tag library!");
        }

        if (tagFamily == null)
        {
            throw new RuntimeException("Cannot create AprilTagProcessor without setting tag family!");
        }

        OpenGLMatrix cameraRotationMatrix = new Orientation(
                AxesReference.INTRINSIC, AxesOrder.ZXZ, AngleUnit.DEGREES,
                (float) cameraOrientation.getYaw(AngleUnit.DEGREES),
                (float) cameraOrientation.getPitch(AngleUnit.DEGREES),
                (float) cameraOrientation.getRoll(AngleUnit.DEGREES),
                cameraOrientation.getAcquisitionTime())
                .getRotationMatrix();

        OpenGLMatrix robotInCameraFrame = OpenGLMatrix.identityMatrix()
                .translated(
                        (float) cameraPosition.toUnit(DistanceUnit.INCH).x,
                        (float) cameraPosition.toUnit(DistanceUnit.INCH).y,
                        (float) cameraPosition.toUnit(DistanceUnit.INCH).z)
                .multiplied(cameraRotationMatrix)
                .inverted();

        return new MultiSolverAprilTagProcessorImpl(
                robotInCameraFrame, fx, fy, cx, cy,
                outputUnitsLength, outputUnitsAngle, tagLibrary,
                drawAxes, drawCube, drawOutline, drawTagId,
                tagFamily, threads, suppressCalibrationWarnings
        );
    }
}
