package com.silverpeas.util;

import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class StringUtil {

  private static final String PATTERN_START = "{";
  private static final String PATTERN_END = "}";
  private static final String TRUNCATED_TEXT_SUFFIX = "...";
  private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";

  public static boolean isDefined(String parameter) {
    return (parameter != null && parameter.length() > 0 && !parameter.equalsIgnoreCase("null"));
  }

  public static boolean isInteger(String id) {
    try {
      Integer.parseInt(id);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Parse a String into an int. Returns the default value if the String is incorrect
   * @param value: the String to be parsed.
   * @param defaultValue: the value to be returned in case the String is not a valid int.
   * @return the int correspondind to this String or the default value if the String is not parseable.
   */
  public static int parseInteger(String value, int defaultValue) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * @param text
   * @return a String with all quotes replaced by spaces
   */
  public static String escapeQuote(String text) {
    return text.replaceAll("'", " ");
  }

  /**
   * Format a string by extending the principle of the the method format() of
   * the class java.text.MessageFormat to string arguments. For instance, the
   * string '{key}' contained in the original string to format will be replaced
   * by the value corresponding to this key contained into the values map.
   * 
   * @param label
   *          The string to format
   * @param values
   *          The values to insert into the string
   * @return The formatted string, filled with values of the map.
   */
  public static String format(String label, Map<String, ?> values) {
    StringBuffer sb = new StringBuffer();
    int startIndex = label.indexOf(PATTERN_START);
    int endIndex;
    String patternKey;
    Object value;
    while (startIndex != -1) {
      endIndex = label.indexOf(PATTERN_END, startIndex);
      if (endIndex != -1) {
        patternKey = label.substring(startIndex + 1, endIndex);
        if (values.containsKey(patternKey)) {
          value = values.get(patternKey);
          sb.append(label.substring(0, startIndex)).append(
              value != null ? value.toString() : "");
        } else {
          sb.append(label.substring(0, endIndex + 1));
        }
        label = label.substring(endIndex + 1);
        startIndex = label.indexOf(PATTERN_START);
      } else {
        sb.append(label);
        label = "";
        startIndex = -1;
      }
    }
    sb.append(label);
    return sb.toString();
  }

  /**
   * @param text
   *          The string to truncate if its size is greater than the maximum
   *          length given as parameter.
   * @param maxLength
   *          The maximum length required.
   * @return The truncated string followed by '...' if needed. Returns the
   *         string itself if its length is smaller than the required maximum
   *         length.
   */
  public static String truncate(String text, int maxLength) {
    if (text == null || text.length() <= maxLength) {
      return text;
    } else if (maxLength <= 3) {
      return TRUNCATED_TEXT_SUFFIX;
    } else {
      return text.substring(0, maxLength - 3) + TRUNCATED_TEXT_SUFFIX;
    }
  }

  /**
   * Validate the form of an email address.
   * 
   * <P>
   * Return <tt>true</tt> only if
   *<ul>
   * <li> <tt>aEmailAddress</tt> can successfully construct an
   * {@link javax.mail.internet.InternetAddress}
   * <li>when parsed with "@" as delimiter, <tt>aEmailAddress</tt> contains two
   * tokens which satisfy {@link hirondelle.web4j.util.Util#textHasContent}.
   *</ul>
   * 
   *<P>
   * The second condition arises since local email addresses, simply of the form
   * "<tt>albert</tt>", for example, are valid for
   * {@link javax.mail.internet.InternetAddress}, but almost always undesired.
   * 
   * @param aEmailAddress
   *          the address to be validated
   * @return true is the address is a valid email address - false otherwise.
   */
  public static boolean isValidEmailAddress(String aEmailAddress) {
    if (aEmailAddress == null) {
      return false;
    }
    boolean result = true;
    try {
      new InternetAddress(aEmailAddress);
      result = Pattern.matches(EMAIL_PATTERN, aEmailAddress);
    } catch (AddressException ex) {
      result = false;
    }
    return result;
  }
}
