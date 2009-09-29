/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.webactiv.util.indexEngine.parser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.StringTokenizer;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.ResourceLocator;

/**
 * The ParserManager class manages all the parsers which will be used to parse
 * the indexed files.
 */
public final class ParserManager {

  /**
   * Set the parser for a given file format.
   */
  static public void setParser(String format, Parser parser) {
    parserMap.put(format, parser);
  }

  /**
   * Returns the set of all the known file formats.
   * 
   * The returned set is a Set of String.
   */
  static public Set getFormatNames() {
    return parserMap.keySet();
  }

  /**
   * Get the parser for a given file format.
   */
  static public Parser getParser(String format) {
    Parser parser = (Parser) parserMap.get(format);

    if (parser == null) {
      SilverTrace.debug("indexEngine", "ParserManager",
          "indexEngine.MSG_UNKNOWN_FILE_FORMAT", format);
    }

    return parser;
  }

  /**
   * Set all the parsers declared in Parsers.properties file.
   */
  static private void init() {
    Enumeration FormatNames = null;

    try {
      ResourceLocator MyResource = new ResourceLocator(
          "com.stratelia.webactiv.util.indexEngine.Parser", "");

      FormatNames = MyResource.getKeys();
      while (FormatNames.hasMoreElements()) {
        String name = "";
        String newCall = "";
        String className = "";

        try {
          name = (String) FormatNames.nextElement();
          newCall = MyResource.getString(name);
          if ("ignored".equals(newCall) || "".equals(newCall)) {
            continue; // we skip ignored mime type
          }
          className = getClassName(newCall);

          Class classe = Class.forName(className);
          Class[] parametersClass = getParametersClass(newCall);
          Constructor constructor = classe.getConstructor(parametersClass);
          Object[] parameters = getParameters(newCall);
          Parser parser = (Parser) constructor.newInstance(parameters);

          parserMap.put(name, parser);
          SilverTrace.debug("indexEngine", "ParserManager",
              "indexEngine.MSG_INIT_PARSER", name + ", " + newCall);

        } catch (ClassNotFoundException e) {
          SilverTrace.error("indexEngine", "ParserManager",
              "indexEngine.MSG_UNKNOWN_PARSER_CLASS", name + ", " + className);
        } catch (Exception e) {
          SilverTrace.fatal("indexEngine", "ParserManager",
              "indexEngine.MSG_PARSER_INITIALIZATION_FAILED", name);
        }
      }
    } catch (MissingResourceException e) {
      SilverTrace.fatal("indexEngine", "ParserManager",
          "indexEngine.MSG_MISSING_PARSER_PROPERTIES");
    }
  }

  /**
   * Returns the class name in a string like "className(args, args, ...)"
   */
  static private String getClassName(String newCall) {
    int par = newCall.indexOf("(");
    if (par == -1)
      return newCall.trim();
    else
      return newCall.substring(0, par).trim();
  }

  /**
   * Returns the args values in a string like "className(args, args, ...)"
   */
  static private Object[] getParameters(String newCall) {
    int lPar = newCall.indexOf("(");
    int rPar = newCall.indexOf(")");
    if (lPar == -1 || rPar == -1 || lPar + 1 >= rPar)
      return new Object[0];

    List args = new ArrayList();
    String argsString = newCall.substring(lPar + 1, rPar);
    StringTokenizer st = new StringTokenizer(argsString, ",", false);
    while (st.hasMoreTokens()) {
      args.add(st.nextToken().trim());
    }
    return args.toArray();
  }

  /**
   * Returns the args types in a string like "className(args, args, ...)"
   */
  static private Class[] getParametersClass(String newCall) {
    int lPar = newCall.indexOf("(");
    int rPar = newCall.indexOf(")");
    if (lPar == -1 || rPar == -1 || lPar + 1 >= rPar)
      return new Class[0];

    List args = new ArrayList();
    String argsString = newCall.substring(lPar + 1, rPar);
    StringTokenizer st = new StringTokenizer(argsString, ",", false);
    while (st.hasMoreTokens()) {
      // always java.lang.String.
      args.add(st.nextToken().getClass());
    }
    return (Class[]) args.toArray(new Class[args.size()]);
  }

  /**
   * The map giving the parser for a specific file format.
   * 
   * The type of this map is : Map (String -> Parser).
   */

  static private final Map parserMap;

  /**
   * At class initialization time, the parser's map is built and initialized
   * from the Parsers.properties file.
   */

  static {
    parserMap = new HashMap();
    init();
  }
}
