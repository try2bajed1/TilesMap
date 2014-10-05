package com.example.AlternativeMap;

import android.graphics.Point;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 12:27
 */
public class SmartPoint extends Point {

    public SmartPoint(int x, int y) {
        super(x, y);
    }

    public SmartPoint(Point p) {
        super(p);
    }

    public SmartPoint add(Point other) {
        return new SmartPoint(x + other.x, y + other.y);
    }

    public SmartPoint diff(Point other) {
        return new SmartPoint(x - other.x, y - other.y);
    }

    public SmartPoint mul(int n) {
        return new SmartPoint(x * n, y * n);
    }

    public SmartPoint div(int n) {
        return new SmartPoint(x / n, y / n);
    }
}
