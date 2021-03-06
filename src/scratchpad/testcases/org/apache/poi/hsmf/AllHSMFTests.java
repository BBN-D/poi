/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hsmf;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.poi.hsmf.datatypes.*;
import org.apache.poi.hsmf.extractor.TestOutlookTextExtractor;
import org.apache.poi.hsmf.parsers.*;

public final class AllHSMFTests {
   public static Test suite() {
      TestSuite suite = new TestSuite(AllHSMFTests.class.getName());

      suite.addTestSuite(TestBasics.class);
      suite.addTestSuite(TestBlankFileRead.class);
      suite.addTestSuite(TestSimpleFileRead.class);
      suite.addTestSuite(TestOutlook30FileRead.class);
      suite.addTestSuite(TestFileWithAttachmentsRead.class);
      suite.addTestSuite(TestChunkData.class);
      suite.addTestSuite(TestTypes.class);
      suite.addTestSuite(TestSorters.class);
      suite.addTestSuite(TestOutlookTextExtractor.class);
      suite.addTestSuite(TestPOIFSChunkParser.class);
      suite.addTestSuite(TestMessageSubmissionChunkY2KRead.class);
      suite.addTestSuite(TestMessageSubmissionChunk.class);

      return suite;
   }
}
