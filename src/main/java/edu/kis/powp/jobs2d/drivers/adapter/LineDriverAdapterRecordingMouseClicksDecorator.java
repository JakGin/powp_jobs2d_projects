package edu.kis.powp.jobs2d.drivers.adapter;

import edu.kis.legacy.drawer.shape.ILine;
import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.features.LinesRecorder;
import edu.kis.powp.jobs2d.features.WorkspaceTransformationRecorder;
import edu.kis.powp.jobs2d.transformations.ScaleTransformation;
import edu.kis.powp.jobs2d.transformations.ShiftTransformation;

import java.awt.Point;

public class LineDriverAdapterRecordingMouseClicksDecorator implements Job2dDriver {
    private final LineDriverAdapter lineDriverAdapter;

    public LineDriverAdapterRecordingMouseClicksDecorator(LineDriverAdapter lineDriverAdapter) {
        this.lineDriverAdapter = lineDriverAdapter;
    }

    @Override
    public void setPosition(int i, int i1) {
        lineDriverAdapter.setPosition(i, i1);
    }

    @Override
    public void operateTo(int i, int i1) {
        lineDriverAdapter.operateTo(i, i1);

        try {
            ILine line = lineDriverAdapter.getLineCopy();
            ILine untransformedLine = lineDriverAdapter.getLineCopy();

            Point startPoint = new Point(line.getStartCoordinateX(), line.getStartCoordinateY());
            Point endPoint = new Point(line.getEndCoordinateX(), line.getEndCoordinateY());

            Point currentShift = WorkspaceTransformationRecorder.getInstance().getShift();
            double currentZoom = WorkspaceTransformationRecorder.getInstance().getZoom();

            Point transformedStartPoint = new ShiftTransformation(-currentShift.x, -currentShift.y).transform(startPoint);
            Point transformedEndPoint = new ShiftTransformation(-currentShift.x, -currentShift.y).transform(endPoint);

            transformedStartPoint = new ScaleTransformation(1/currentZoom).transform(transformedStartPoint);
            transformedEndPoint = new ScaleTransformation(1/currentZoom).transform(transformedEndPoint);

            untransformedLine.setStartCoordinates(transformedStartPoint.x, transformedStartPoint.y);
            untransformedLine.setEndCoordinates(transformedEndPoint.x, transformedEndPoint.y);

            LinesRecorder.getLinesRecorder().addLine(line, untransformedLine);
        } catch (CloneNotSupportedException ignored) {
        }
    }
}