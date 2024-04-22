package sit.cp23ms2.sportconnect;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMain {
    public static void main(String[] args) {
        Point point = new Point(1,2);
        System.out.println(point.x);
        System.out.println(String.valueOf(point.x).isEmpty());
        Integer test = point.x;
        String test2 = String.valueOf(point.x + " " + point.y);
        System.out.println(test2);
        System.out.println(point.x + " " + point.y);
    }
}
