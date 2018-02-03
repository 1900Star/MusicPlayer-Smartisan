package com.yibao.biggirl.test;

import com.yibao.biggirl.util.WallPaperUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testAdd()
            throws Exception
    {
        WallPaperUtil paperUtil = new WallPaperUtil();
        int           sum       = paperUtil.add(2, 6);
        assertEquals(8, sum);

    }


}