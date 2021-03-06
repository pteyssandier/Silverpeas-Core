/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.silverpeas.wysiwyg.dynamicvalue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.silverpeas.wysiwyg.dynamicvalue.control.DynamicValueReplacement;

/**
 * Test class for TestDynamicValueReplacement
 */
public class TestDynamicValueReplacement extends AbstractBaseDynamicValue {

  /**
   * Test method for
   * {@link com.silverpeas.wysiwyg.dynamicvalue.control.DynamicValueReplacement#buildHTMLSelect()}.
   */
  @Test
  public final void testBuildHTMLSelect() {
    String select = DynamicValueReplacement.buildHTMLSelect("fr", "default");
    assertTrue(select.contains("java_version") && select.contains("version"));
  }

  /**
   * replaces all the key occurrences by their values Test method for
   * {@link com.silverpeas.wysiwyg.dynamicvalue.control.DynamicValueReplacement#replaceKeyByValue(java.lang.String)}
   * .
   * @throws IOException
   */
  @Test
  public final void testReplaceKeyByValueDefault() throws IOException {
    String text = getContentFromFile("test.html");
    DynamicValueReplacement replacement = new DynamicValueReplacement();
    text = replacement.replaceKeyByValue(text);
    assertEquals(true, text.contains("version 2.3") && text.contains("jdk1.6.0_17"));
  }

  /**
   * calls replaceKeyByValue with a text without content to replace Test method for
   * {@link com.silverpeas.wysiwyg.dynamicvalue.control.DynamicValueReplacement#replaceKeyByValue(java.lang.String)}
   * .
   * @throws IOException
   */
  @Test
  public final void testReplaceKeyByValueWithoutKeyToReplace() throws IOException {

    String originalText = getContentFromFile("test-without_keys.html");
    DynamicValueReplacement replacement = new DynamicValueReplacement();
    String finalText = replacement.replaceKeyByValue(originalText);
    assertEquals(originalText.length(), finalText.length());
  }

  /**
   * gets file content as a String
   * @return String Object which contain file data
   * @throws IOException
   */
  private String getContentFromFile(String fileName) throws IOException {
    StringBuilder contents = new StringBuilder();
    BufferedReader input =
        new BufferedReader(new InputStreamReader(TestDynamicValueReplacement.class
        .getResourceAsStream(fileName)));
    try {
      String line = null;
      while ((line = input.readLine()) != null) {
        contents.append(line);
        // contents.append(System.getProperty("line.separator"));
      }
    } finally {
      input.close();
    }
    return contents.toString();
  }

  /**
   * Test method for
   * {@link com.silverpeas.wysiwyg.dynamicvalue.control.DynamicValueReplacement#isActivate()}.
   */
  @Test
  public final void testIsActivate() {
    assertEquals(false, DynamicValueReplacement.isActivate());

  }

}
