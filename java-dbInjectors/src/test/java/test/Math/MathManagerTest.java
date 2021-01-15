package test.Math;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class MathManagerTest {

    @Test
    public void testName() {
        DoubleStream doubleStream = MathManager.sinFunction(1d, 10d, 0d, 100d, 0d);
        DoubleStream limit = doubleStream.limit(100);
        System.out.println(limit.mapToObj(e -> e).collect(Collectors.toList()));
    }
}