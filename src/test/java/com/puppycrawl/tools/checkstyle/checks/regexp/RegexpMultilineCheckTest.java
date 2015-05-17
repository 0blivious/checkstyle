////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.regexp;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.puppycrawl.tools.checkstyle.BaseFileSetCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static com.puppycrawl.tools.checkstyle.checks.regexp.MultilineDetector.REGEXP_EXCEEDED;

public class RegexpMultilineCheckTest extends BaseFileSetCheckTestSupport {
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DefaultConfiguration checkConfig;

    @Before
    public void setUp() {
        checkConfig = createCheckConfig(RegexpMultilineCheck.class);
    }

    @Test
    public void testIt() throws Exception {
        final String illegal = "System\\.(out)|(err)\\.print(ln)?\\(";
        checkConfig.addAttribute("format", illegal);
        final String[] expected = {
            "69: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
        };
        verify(checkConfig, getPath("InputSemantic.java"), expected);
    }

    @Test
    public void testMessageProperty()
        throws Exception {
        final String illegal = "System\\.(out)|(err)\\.print(ln)?\\(";
        final String message = "Bad line :(";
        checkConfig.addAttribute("format", illegal);
        checkConfig.addAttribute("message", message);
        final String[] expected = {
            "69: " + message,
        };
        verify(checkConfig, getPath("InputSemantic.java"), expected);
    }

    @Test
    public void testIgnoreCaseTrue() throws Exception {
        final String illegal = "SYSTEM\\.(OUT)|(ERR)\\.PRINT(LN)?\\(";
        checkConfig.addAttribute("format", illegal);
        checkConfig.addAttribute("ignoreCase", "true");
        final String[] expected = {
            "69: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
        };
        verify(checkConfig, getPath("InputSemantic.java"), expected);
    }

    @Test
    public void testIgnoreCaseFalse() throws Exception {
        final String illegal = "SYSTEM\\.(OUT)|(ERR)\\.PRINT(LN)?\\(";
        checkConfig.addAttribute("format", illegal);
        checkConfig.addAttribute("ignoreCase", "false");
        final String[] expected = {};
        verify(checkConfig, getPath("InputSemantic.java"), expected);
    }

    @Test
    public void testIllegalFailBelowErrorLimit() throws Exception {
        final String illegal = "^import";
        checkConfig.addAttribute("format", illegal);
        final String[] expected = {
            "7: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
            "8: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
            "9: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
        };
        verify(checkConfig, getPath("InputSemantic.java"), expected);
    }

    @Test
    public void testCarriageReturn() throws Exception {
        final String illegal = "\\r";
        checkConfig.addAttribute("format", illegal);
        final String[] expected = {
            "1: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
            "3: " + getCheckMessage(REGEXP_EXCEEDED, illegal),
        };

        final File file = temporaryFolder.newFile();
        Files.write("first line \r\n second line \n\r third line", file, Charsets.UTF_8);

        verify(checkConfig, file.getPath(), expected);
    }

}
