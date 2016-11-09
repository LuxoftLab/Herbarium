package com.example.shand.herbarium.detector;

import android.util.Log;

import com.example.shand.herbarium.classification.Features;
import com.example.shand.herbarium.classification.LeafData;
import com.example.shand.herbarium.detector.lines.Line;
import com.example.shand.herbarium.detector.lines.Lines;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

//detect shape type of leaf
public class ShapeDetector extends DebugDetector implements Detector {
    private static int idx;
    private Detector nextDetector;

    //find longest perpendicular line
    private Line maxPerpendicularLine;
    private double maxPerpendicularLineLength = -1;
    private int maxPerpendicularLineNum = -1;

    public ShapeDetector(Detector n) {
        nextDetector = n;
    }

    public ShapeDetector() {
        nextDetector = null;
    }

    @Override
    public int getIdx() {
        return idx;
    }

    @Override
    public void setIdx(int i) {
        idx = i;
    }

    @Override
    public Detector detect(LeafData leafData, Features features) {
        startTimer();

        //get rgba and gray
        Mat rgba = leafData.getRgba().clone();
        Mat gray = leafData.getGray().clone();

        //threshold
        gray = LeafData.thresholdOTSU(gray);
        addDebugMat(leafData.getGray());

        //delete scape, get contour without scape and base point
        leafData.deleteScape();
        MatOfPoint contour = leafData.getContour();
        Point basePoint = leafData.getBasePoint();
        addDebugMat(gray);

        //fill contour
        ArrayList<MatOfPoint> contoursList = new ArrayList<>();
        contoursList.add(contour);
        gray = new Mat(gray.rows(), gray.cols(), gray.type(), new Scalar(0));
        Imgproc.drawContours(gray, contoursList, -1, new Scalar(255), -1);
        addDebugMat(gray);

        //downscale mat
        int scaleCoefficient = 10;
        Imgproc.resize(gray, gray, new Size(gray.width() / scaleCoefficient, gray.height() / scaleCoefficient));

        if (basePoint != null) {
            basePoint = basePoint.clone();

            if(debug) {
                //draw base point
                Imgproc.circle(rgba, basePoint, 10, new Scalar(255, 0, 0), -1);
            }

            //calculate base point in downscaled mat
            basePoint.x = basePoint.x / scaleCoefficient;
            basePoint.y = basePoint.y / scaleCoefficient;

            //find leaf contour in downscaled mat
            contour = LeafData.findLargestContour(gray);

            //find most remote point from base point
            Point peakPoint = findMostRemotePoint(contour, basePoint);

            //calculate leaf length
            double dist = Math.sqrt((basePoint.x - peakPoint.x) * (basePoint.x - peakPoint.x) +
                    (basePoint.y - peakPoint.y) * (basePoint.y - peakPoint.y));

            //find longest perpendicular line
            Line length = Lines.createLine(peakPoint, basePoint);
            findLongestPerpendicularLine(length, gray, rgba, scaleCoefficient);

            if(debug) {
                //show peak point, length and perpendicular
                Imgproc.circle(rgba, new Point(peakPoint.x * scaleCoefficient, peakPoint.y * scaleCoefficient), 10, new Scalar(255, 0, 0), -1);
                Imgproc.line(rgba, new Point(length.getStart().x * scaleCoefficient, length.getStart().y * scaleCoefficient), new Point(length.getEnd().x * scaleCoefficient, length.getEnd().y * scaleCoefficient), new Scalar(255, 0, 0), 10);
                Imgproc.line(rgba, new Point(maxPerpendicularLine.getStart().x * scaleCoefficient, maxPerpendicularLine.getStart().y * scaleCoefficient), new Point(maxPerpendicularLine.getEnd().x * scaleCoefficient, maxPerpendicularLine.getEnd().y * scaleCoefficient), new Scalar(255, 0, 0), 10);
            }

            Imgproc.line(leafData.getRgba(), new Point(length.getStart().x * scaleCoefficient, length.getStart().y * scaleCoefficient), new Point(length.getEnd().x * scaleCoefficient, length.getEnd().y * scaleCoefficient), new Scalar(255, 0, 0), 3);
            Imgproc.line(leafData.getRgba(), new Point(maxPerpendicularLine.getStart().x * scaleCoefficient, maxPerpendicularLine.getStart().y * scaleCoefficient), new Point(maxPerpendicularLine.getEnd().x * scaleCoefficient, maxPerpendicularLine.getEnd().y * scaleCoefficient), new Scalar(255, 0, 0), 3);

            //classify
            ShapeType type = classify(maxPerpendicularLineLength, dist, maxPerpendicularLineNum, 20);
            features.setFeature(getIdx(), type.ordinal());
        } else {
            //no scape found
            features.setFeature(getIdx(), ShapeType.UNKNOWN.ordinal());
        }

        stopTimer();
        addDebugText("Result: " + ShapeType.values()[features.getFeature(idx)]);

        Log.d("Classification", "RESULT: " + ShapeType.values()[features.getFeature(idx)]);

        addDebugMat(rgba);

        return nextDetector;
    }

    @Override
    public String getFeatureName(int i) {
        return ShapeType.values()[i].toString();
    }

    private ShapeType classify(double width, double height, int lineNum, int lineCount) {
        lineNum++;
        if (width >= height || (width / height) > 0.6) {
            if (lineNum <= 0.35 * lineCount) {
                return ShapeType.WIDE_EGG_SHAPED;
            } else if (lineNum <= 0.55 * lineCount) {
                return ShapeType.ROUND;
            } else {
                return ShapeType.WIDE_INVERSE_EGG_SHAPED;
            }
        } else if ((width / height) > 0.3) {
            if (lineNum <= 0.35 * lineCount) {
                return ShapeType.EGG_SHAPED;
            } else if (lineNum <= 0.55 * lineCount) {
                return ShapeType.ELLIPTIC;
            } else {
                return ShapeType.INVERSE_EGG_SHAPED;
            }
        } else if ((width / height) > 0.15) {
            if (lineNum <= 0.35 * lineCount) {
                return ShapeType.NARROW_EGG_SHAPED;
            } else if (lineNum <= 0.55 * lineCount) {
                return ShapeType.OBLONG;
            } else {
                return ShapeType.NARROW_INVERSE_EGG_SHAPED;
            }
        } else {
            return ShapeType.LINEAR;
        }
    }

    private void findLongestPerpendicularLine(Line length, Mat gray, Mat rgba, int t) {
        int numPoints = 20;
        double width;
        Point lt = new Point(0, 0), rb = new Point(gray.width(), gray.height());
        int j = 0;
        for (Point curPoint : length.moveAlongLine(numPoints)) {
            Line l = perpendicularLine(curPoint, length.perpendicular(curPoint, lt, rb), gray, rgba);
            width = l.length();

            if(debug) {
                //show current perpendicular line and its ends
                Imgproc.line(rgba, new Point(l.getStart().x * t, l.getStart().y * t), new Point(l.getEnd().x * t, l.getEnd().y * t), new Scalar(255, 0, 0));
                Imgproc.circle(rgba, new Point(l.getStart().x * t, l.getStart().y * t), 10, new Scalar(255, 255, 255));
                Imgproc.circle(rgba, new Point(l.getEnd().x * t, l.getEnd().y * t), 10, new Scalar(255, 255, 255));
            }

            if (width > maxPerpendicularLineLength) {
                maxPerpendicularLineLength = width;
                maxPerpendicularLineNum = j;
                maxPerpendicularLine = l;
            }
            j++;
        }
    }

    private Point findMostRemotePoint(MatOfPoint contour, Point basePoint){
        double maxDist = -1;
        int maxi = 0;
        for (int i = 0; i < contour.rows(); i++) {
            double dist = Math.sqrt((basePoint.x - contour.get(i, 0)[0]) * (basePoint.x - contour.get(i, 0)[0]) +
                    (basePoint.y - contour.get(i, 0)[1]) * (basePoint.y - contour.get(i, 0)[1]));
            if (dist > maxDist) {
                maxDist = dist;
                maxi = i;
            }
        }

        return new Point(contour.get(maxi, 0)[0], contour.get(maxi, 0)[1]);
    }

    private Line perpendicularLine(Point center, Line widthLine, Mat gray, Mat rgba) {
        Point start = edgeBinarySearch(widthLine.getStart(), center, gray);
        Point end = edgeBinarySearch(widthLine.getEnd(), center, gray);

        return Lines.createLine(start, end);
    }

    //find leaf edge
    private Point edgeBinarySearch(Point black, Point white, Mat mat) {
        while(Math.abs((int)(black.x - white.x)) >= 2 || Math.abs((int)(black.y - white.y)) >= 2) {
            Point mid = new Point((int)(black.x + white.x) / 2, (int)(black.y + white.y) / 2);
            double color[] = mat.get((int)mid.y, (int)mid.x);

            if(color == null) {
                black = mid;
                continue;
            }

            if(color[0] == 255) {
                if((int)white.x == (int)mid.x && (int)white.y == (int)mid.y) break;
                white = mid;
            } else {
                if((int)black.x == (int)mid.x && (int)black.y == (int)mid.y) break;
                black = mid;
            }
        }

        return white;
    }

    @Override
    public String toString() {
        return "Shape";
    }

    private enum ShapeType {
        UNKNOWN("не найден черешок"),
        WIDE_EGG_SHAPED("1 - широкояйцевидный лист"),
        ROUND("2 - округлый"),
        WIDE_INVERSE_EGG_SHAPED("3 - обратноширокояйцевидный"),
        EGG_SHAPED("4 - яйцевидный"),
        ELLIPTIC("5 - эллиптический"),
        INVERSE_EGG_SHAPED("6 - обратнояйцевидный"),
        NARROW_EGG_SHAPED("7 - узкояйцевидный"),
        OBLONG("8 - ланцетный"),
        NARROW_INVERSE_EGG_SHAPED("10 - обратноузкояйцевидный"),
        LINEAR("11 - линейный");

        private String name;

        ShapeType(String s) {
            name = s;
        }

        public String getName() {
            return name;
        }
    }
}

