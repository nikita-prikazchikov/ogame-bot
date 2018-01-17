package ru.tki.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataParserTest {

    @Test
    public void parseResourceSimple() throws Exception {
        assertEquals(1234, DataParser.parseResource("1.234"));
    }

    @Test
    public void parseResourceWithM() throws Exception {
        assertEquals(7670000, DataParser.parseResource("7.67М"));
        assertEquals(3835000, DataParser.parseResource("3.835М"));
    }

}