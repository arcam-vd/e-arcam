/*
 * ExportDataUtilsTest.java
 * 
 * Project: Cyber Admin
 * 
  * 
 * Copyright (c) 2013, ARCAM - Association de la Region Cossonay-Aubonne-Morges
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification,are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * Neither the name of the ARCAM - Association de la Region 
 * Cossonay-Aubonne-Morges nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package org.arcam.cyberadmin.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author mmn
 *
 */
@Test
// CHECKSTYLE:OFF MagicNumber
public class ExportDataUtilsTest {
    
    public void testEncodeStringValue() throws Exception {
    	Assert.assertNull(ExportDataUtils.encodeStringValue(null));
        Assert.assertEquals("", ExportDataUtils.encodeStringValue(""));
        Assert.assertEquals("", ExportDataUtils.encodeStringValue("  "));
        
        Assert.assertEquals("AAA", ExportDataUtils.encodeStringValue("AAA"));
        Assert.assertEquals("AAA", ExportDataUtils.encodeStringValue("   AAA   "));
        Assert.assertEquals("AAA BBB", ExportDataUtils.encodeStringValue("AAA\nBBB"));
        Assert.assertEquals("William \"Bill\" Gates",
                ExportDataUtils.encodeStringValue("William \"Bill\" Gates"));
        Assert.assertEquals("'AAA'", ExportDataUtils.encodeStringValue("'AAA'"));
        Assert.assertEquals("AAA", ExportDataUtils.encodeStringValue("AAA\n"));
        Assert.assertEquals("AAA", ExportDataUtils.encodeStringValue("\nAAA\n"));
        Assert.assertEquals("AAA", ExportDataUtils.encodeStringValue("\nAAA\n"));
        Assert.assertEquals("A AA", ExportDataUtils.encodeStringValue("A\tAA"));
        
        Assert.assertEquals("Hello \"\" World", ExportDataUtils.encodeStringValue("Hello \"\" World"));
        Assert.assertEquals("Hello  World", ExportDataUtils.encodeStringValue("Hello\t\r\nWorld"));
    }

    public void testEncodeNumberValue() throws Exception {
        Double nullValue = null;
        Assert.assertEquals("", ExportDataUtils.encodeNumberValue(nullValue));
        Assert.assertEquals("1000.01", ExportDataUtils.encodeNumberValue(new Double(1000.01)));
        Assert.assertEquals("-1.00", ExportDataUtils.encodeNumberValue(new Double(-1)));
        Assert.assertEquals("0", ExportDataUtils.encodeNumberValue(new Double(0)));
        Assert.assertEquals("0", ExportDataUtils.encodeNumberValue(new Double(-0)));
    } 
    
    public void testEncodeMultiEntryValue() throws Exception {
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue(), "");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue(""), "");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue("", ""), "\\");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue(null, null), "\\");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue(5.0, 3.0), "5.0\\3.0");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue("abc", "xyz"), "abc\\xyz");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue(5, 3, ""), "5\\3\\");
        Assert.assertEquals(ExportDataUtils.encodeMultiEntryValue("abc", "xyz", ""), "abc\\xyz\\");
    }
    
    // CHECKSTYLE:ON MagicNumber
}
