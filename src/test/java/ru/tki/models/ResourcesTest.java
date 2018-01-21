package ru.tki.models;

import org.junit.Test;

public class ResourcesTest {

    @Test
    public void testMultiply() throws Exception {
        Resources resources = new Resources(60, 15);

        System.out.println(resources.multiply(1.5));
        System.out.println(resources.multiply(Math.pow(1.5, 3)));
        System.out.println(resources.multiply(Math.pow(1.5, 10)));
        System.out.println(resources.multiply(Math.pow(1.5, 0)));
    }

    @Test
    public void testAdd() throws Exception {
        Resources resources = new Resources(60, 15);
        Resources resources1 = new Resources(100, 15);

        System.out.println(resources.add(resources1));
    }

    @Test
    public void testEnough() throws Exception {
        Resources resources = new Resources(60, 15);
        Resources resources1 = new Resources(100, 15);

        System.out.println(resources.isEnoughFor(resources1));
        System.out.println(resources1.isEnoughFor(resources1));
        System.out.println(resources1.isEnoughFor(resources));
    }
}