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

/**
 * GraphicElementFactory.java
 *
 * Created on 10 octobre 2000, 16:26
 */

package com.stratelia.webactiv.util.viewGenerator.html;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.silverpeas.util.StringUtil;
import com.silverpeas.util.i18n.I18NHelper;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.ComponentInstLight;
import com.stratelia.webactiv.util.ResourceLocator;
import com.stratelia.webactiv.util.viewGenerator.html.arrayPanes.ArrayPane;
import com.stratelia.webactiv.util.viewGenerator.html.arrayPanes.ArrayPaneSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.board.Board;
import com.stratelia.webactiv.util.viewGenerator.html.board.BoardSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.browseBars.BrowseBar;
import com.stratelia.webactiv.util.viewGenerator.html.browseBars.BrowseBarComplete;
import com.stratelia.webactiv.util.viewGenerator.html.buttonPanes.ButtonPane;
import com.stratelia.webactiv.util.viewGenerator.html.buttonPanes.ButtonPaneWA2;
import com.stratelia.webactiv.util.viewGenerator.html.buttons.Button;
import com.stratelia.webactiv.util.viewGenerator.html.buttons.ButtonSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.calendar.Calendar;
import com.stratelia.webactiv.util.viewGenerator.html.calendar.CalendarWA1;
import com.stratelia.webactiv.util.viewGenerator.html.formPanes.FormPane;
import com.stratelia.webactiv.util.viewGenerator.html.formPanes.FormPaneWA;
import com.stratelia.webactiv.util.viewGenerator.html.frame.Frame;
import com.stratelia.webactiv.util.viewGenerator.html.frame.FrameSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.iconPanes.IconPane;
import com.stratelia.webactiv.util.viewGenerator.html.iconPanes.IconPaneWA;
import com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.MonthCalendar;
import com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.MonthCalendarWA1;
import com.stratelia.webactiv.util.viewGenerator.html.navigationList.NavigationList;
import com.stratelia.webactiv.util.viewGenerator.html.navigationList.NavigationListSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.operationPanes.OperationPane;
import com.stratelia.webactiv.util.viewGenerator.html.operationPanes.OperationPaneSilverpeasV5Web20;
import com.stratelia.webactiv.util.viewGenerator.html.pagination.Pagination;
import com.stratelia.webactiv.util.viewGenerator.html.pagination.PaginationSP;
import com.stratelia.webactiv.util.viewGenerator.html.progressMessage.ProgressMessage;
import com.stratelia.webactiv.util.viewGenerator.html.progressMessage.ProgressMessageSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.tabs.TabbedPane;
import com.stratelia.webactiv.util.viewGenerator.html.tabs.TabbedPaneSilverpeasV5;
import com.stratelia.webactiv.util.viewGenerator.html.window.Window;
import com.stratelia.webactiv.util.viewGenerator.html.window.WindowWeb20V5;

/**
 * The GraphicElementFactory is the only class to instanciate in this package. You should have one
 * factory for each client (for future evolution). The GraphicElementFactory is responsible from
 * graphic component instanciation. You should never directly instanciate a component without using
 * this factory ! This class uses the "factory design pattern".
 */
public class GraphicElementFactory extends Object {
  public static final String GE_FACTORY_SESSION_ATT = "SessionGraphicElementFactory";
  private static ResourceLocator settings = null;
  private static String iconsPath = null;
  private ResourceLocator lookSettings = null;
  private ResourceLocator silverpeasLookSettings = null;
  private ResourceLocator favoriteLookSettings = null;
  private String defaultLook = "com.stratelia.webactiv.util.viewGenerator.settings.Initial";
  private static ResourceLocator generalSettings = null;
  private ResourceLocator multilang = null;

  private String currentLookName = null;
  private String externalStylesheet = null;

  private String componentId = null;
  private MainSessionController mainSessionController = null;

  private static final String JQUERY_JS = "jquery-1.3.2.min.js";
  private static final String JQUERYUI_JS = "jquery-ui-1.7.3.custom.min.js";
  private static final String JQUERYUI_CSS = "ui-lightness/jquery-ui-1.7.3.custom.css";

  /**
   * Creates new GraphicElementFactory
   */
  public GraphicElementFactory() {
  }

  /**
   * Constructor declaration
   * @param look
   * @see
   */
  public GraphicElementFactory(String look) {
    setLook(look);
  }

  public static String getIconsPath() {
    if (iconsPath == null) {
      iconsPath = getGeneralSettings().getString("ApplicationURL")
          + getSettings().getString("IconsPath");
    }
    return iconsPath;
  }

  public static ResourceLocator getGeneralSettings() {
    if (generalSettings == null) {
      generalSettings = new ResourceLocator("com.stratelia.webactiv.general",
          I18NHelper.defaultLanguage);
    }
    return generalSettings;
  }

  public ResourceLocator getMultilang() {
    if (multilang == null) {
      String language = getLanguage();
      multilang = new ResourceLocator(
          "com.stratelia.webactiv.util.viewGenerator.multilang.graphicElementFactoryBundle",
          language);
    }
    return multilang;
  }

  private String getLanguage() {
    String language = I18NHelper.defaultLanguage;
    if (mainSessionController != null) {
      language = mainSessionController.getFavoriteLanguage();
    }
    return language;
  }

  /**
   * Get the settings for the factory.
   * @return The ResourceLocator returned contains all default environment settings necessary to
   * know wich component to instanciate, but also to know how to generate html code.
   */
  public static ResourceLocator getSettings() {
    if (settings == null) {
      settings = new ResourceLocator(
          "com.stratelia.webactiv.util.viewGenerator.settings.graphicElementFactorySettings", "");
    }
    return settings;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public ResourceLocator getLookSettings() {
    SilverTrace.info("viewgenerator",
        "GraphicElementFactory.getLookSettings()", "root.MSG_GEN_ENTER_METHOD");
    if (lookSettings == null) {
      ResourceLocator silverpeasSettings = getSilverpeasLookSettings();
      SilverTrace.info("viewgenerator",
          "GraphicElementFactory.getLookSettings()",
          "root.MSG_GEN_EXIT_METHOD", "lookSettings == null");
      // get the customer lookSettings
      try {
        lookSettings = new ResourceLocator(
            "com.stratelia.webactiv.util.viewGenerator.settings.lookSettings", "", silverpeasSettings);
      } catch (java.util.MissingResourceException e) {
        // the customer lookSettings is undefined
        // get the default silverpeas looks
        lookSettings = silverpeasSettings;
      }
    }
    SilverTrace.info("viewgenerator",
        "GraphicElementFactory.getLookSettings()", "root.MSG_GEN_EXIT_METHOD");
    return lookSettings;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public ResourceLocator getSilverpeasLookSettings() {
    SilverTrace.info("viewgenerator",
        "GraphicElementFactory.getSilverpeasLookSettings()",
        "root.MSG_GEN_ENTER_METHOD");
    if (silverpeasLookSettings == null) {
      silverpeasLookSettings = new ResourceLocator(
          "com.stratelia.webactiv.util.viewGenerator.settings.defaultLookSettings", "");
    }
    return silverpeasLookSettings;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public ResourceLocator getFavoriteLookSettings() {
    return this.favoriteLookSettings;
  }

  /**
   * Method declaration
   * @param look
   * @see
   */
  public final void setLook(String look) {
    lookSettings = getLookSettings();
    String selectedLook = null;

    // get the customer lookSettings
    try {
      selectedLook = lookSettings.getString(look, null);
    } catch (java.util.MissingResourceException e) {
      // the customer lookSettings is undefined
      // get the default silverpeas looks
      // lookSettings = null;
      SilverTrace.info("viewgenerator", "GraphicElementFactory.setLook()",
          "root.MSG_GEN_PARAM_VALUE", " customer lookSettings is undefined !");
      lookSettings = getSilverpeasLookSettings();

      selectedLook = silverpeasLookSettings.getString(look, null);
      if (selectedLook == null) {
        // ce look n'existe plus, look par defaut
        selectedLook = defaultLook;
      }
    }

    SilverTrace.info("viewgenerator", "GraphicElementFactory.setLook()",
        "root.MSG_GEN_PARAM_VALUE", " look = " + look
            + " | corresponding settings = " + selectedLook);
    this.favoriteLookSettings = new ResourceLocator(selectedLook, "");

    currentLookName = look;
  }

  public String getCurrentLookName() {
    return currentLookName;
  }

  public void setExternalStylesheet(String externalStylesheet) {
    this.externalStylesheet = externalStylesheet;
  }

  public String getExternalStylesheet() {
    return this.externalStylesheet;
  }

  public boolean hasExternalStylesheet() {
    return (externalStylesheet != null);
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public String getLookFrame() {
    SilverTrace.info("viewgenerator", "GraphicElementFactory.getLookFrame()",
        "root.MSG_GEN_PARAM_VALUE", " FrameJSP = "
            + getFavoriteLookSettings().getString("FrameJSP"));
    return getFavoriteLookSettings().getString("FrameJSP");
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public String getLookStyleSheet() {
    SilverTrace.info("viewgenerator",
        "GraphicElementFactory.getLookStyleSheet()",
        "root.MSG_GEN_ENTER_METHOD");
    String standardStyle = "/util/styleSheets/globalSP_SilverpeasV5.css";
    String standardStyleForIE = "/util/styleSheets/globalSP_SilverpeasV5-IE.css";
    String lookStyle = getFavoriteLookSettings().getString("StyleSheet");
    String contextPath = getGeneralSettings().getString("ApplicationURL");
    String charset = getGeneralSettings().getString("charset", "ISO-8859-1");
    StringBuilder code = new StringBuilder();
    code.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
    code.append(charset);
    code.append("\"/>\n");

    String specificJS = null;

    if (externalStylesheet == null) {

      code.append("<link type=\"text/css\" href=\"").append(contextPath).append(
          "/util/styleSheets/jquery/").append(JQUERYUI_CSS).append("\" rel=\"stylesheet\"/>\n");

      code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(contextPath);
      code.append(standardStyle).append("\"/>\n");

      code.append("<!--[if IE]>\n");
      code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(
          contextPath).append(standardStyleForIE).append("\"/>\n");
      code.append("<![endif]-->\n");

      if (lookStyle.length() > 0) {
        code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        code.append(lookStyle).append("\"/>\n");
      }

      // append CSS style sheet dedicated to current component
      if (StringUtil.isDefined(componentId) && mainSessionController != null) {
        ComponentInstLight component =
            mainSessionController.getOrganizationController().getComponentInstLight(componentId);
        if (component != null) {
          String componentName = component.getName();
          code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(contextPath)
              .append("/").append(componentName).append("/jsp/styleSheets/").append(componentName)
              .append(".css").append("\"/>\n");

          String specificStyle = getFavoriteLookSettings().getString("StyleSheet." + componentName);
          if (StringUtil.isDefined(specificStyle)) {
            code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            code.append(specificStyle).append("\"/>\n");
          }

          specificJS = getFavoriteLookSettings().getString("JavaScript." + componentName);
        }
      }

    } else {
      code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(externalStylesheet)
          .append("\"/>\n");
    }

    // append javascript
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(
        "/util/javaScript/jquery/").append(JQUERY_JS).append("\"></script>\n");
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(
        "/util/javaScript/jquery/").append(JQUERYUI_JS).append("\"></script>\n");
    if (StringUtil.isDefined(specificJS)) {
      code.append("<script type=\"text/javascript\" src=\"").append(specificJS).append(
          "\"></script>\n");
    }

    code.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(
    		"/util/javaScript/jquery/jquery.ui.datepicker-").append(getLanguage()).append(".js\"></script>");
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(
        "/util/javaScript/silverpeas-defaultDatePicker.js\"></script>");

    // include javascript to manage in-progress message
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(
        "/util/javaScript/progressMessage.js\"></script>\n");

    // include specific browseBar javaScript
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(
        "/util/javaScript/browseBarComplete.js\"></script>\n");

    if (getFavoriteLookSettings() != null
        && getFavoriteLookSettings().getString("OperationPane").toLowerCase()
            .endsWith("web20"))
      code.append(getYahooElements());

    SilverTrace
        .info("viewgenerator", "GraphicElementFactory.getLookStyleSheet()",
            "root.MSG_GEN_EXIT_METHOD");
    return code.toString();
  }

  private String getYahooElements() {
    String contextPath = getGeneralSettings().getString("ApplicationURL");
    StringBuilder code = new StringBuilder();

    code.append("<!-- CSS for Menu -->\n");
    code.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
    code.append(getSettings().getString("YUIMenuCss",
        contextPath + "/util/yui/menu/assets/menu.css"));
    code.append("\"/>\n");
    code.append("<!-- Page-specific styles -->\n");
    code.append("<style type=\"text/css\">\n");
    code.append("    div.yuimenu {\n");
    code.append("    position:dynamic;\n");
    code.append("    visibility:hidden;\n");
    code.append("    }\n");
    code.append("</style>\n");
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath);
    code.append("/util/yui/yahoo-dom-event/yahoo-dom-event.js\"></script>\n");
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath);
    code.append("/util/yui/container/container_core-min.js\"></script>\n");
    code.append("<script type=\"text/javascript\" src=\"").append(contextPath);
    code.append("/util/yui/menu/menu-min.js\"></script>\n");
    return code.toString();
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public String getIcon(String iconKey) {
    SilverTrace.info("viewgenerator", "GraphicElementFactory.getIcon()",
        "root.MSG_GEN_ENTER_METHOD", "iconKey = " + iconKey);
    return getFavoriteLookSettings().getString(iconKey, null);
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public Vector<String> getAvailableLooks() {
    ResourceLocator lookSettings = getLookSettings();
    Enumeration<String> keys = lookSettings.getKeys();
    Vector<String> vector = new Vector<String>();

    while (keys.hasMoreElements()) {
      vector.add(keys.nextElement());
    }
    return vector;
  }

  /**
   * Construct a new button.
   * @param label The new button label
   * @param action The action associated exemple : "javascript:onClick=history.back()", or
   * "http://www.stratelia.com/"
   * @param disabled Specify if the button is disabled or not. If disabled, no action will be
   * possible.
   * @return returns an object implementing the FormButton interface. That's the new button to use.
   */
  public Button getFormButton(String label, String action, boolean disabled) {
    Button button = null;
    String buttonClassName = getFavoriteLookSettings().getString("Button");

    try {
      button = (Button) Class.forName(buttonClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getFormButton()",
          "viewgenerator.EX_CANT_GET_BUTTON", "", e);
      button = new ButtonSilverpeasV5();
    } finally {
      button.init(label, action, disabled);
    }
    return button;
  }

  /**
   * Construct a new frame.
   * @param title The new frame title
   * @return returns an object implementing the Frame interface. That's the new frame to use.
   */
  public Frame getFrame() {
    Frame frame = null;
    String frameClassName = getFavoriteLookSettings().getString("Frame");

    try {
      frame = (Frame) Class.forName(frameClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator", "GraphicElementFactory.getFrame()",
          "viewgenerator.EX_CANT_GET_FRAME", "", e);
      frame = new FrameSilverpeasV5();
    }
    return frame;
  }

  /**
   * Construct a new board.
   * @return returns an object implementing the Board interface. That's the new board to use.
   */
  public Board getBoard() {
    Board board = null;
    String boardClassName = getFavoriteLookSettings().getString("Board");

    try {
      board = (Board) Class.forName(boardClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator", "GraphicElementFactory.getBoard()",
          "viewgenerator.EX_CANT_GET_FRAME", "", e);
      board = new BoardSilverpeasV5();
    }
    return board;
  }

  /**
   * Construct a new navigation list.
   * @return returns an object implementing the NavigationList interface.
   */
  public NavigationList getNavigationList() {
    NavigationList navigationList = null;
    String navigationListClassName = getFavoriteLookSettings().getString(
        "NavigationList");

    try {
      navigationList = (NavigationList) Class.forName(navigationListClassName)
          .newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getNavigationList()",
          "viewgenerator.EX_CANT_GET_NAVIGATIONLIST", "", e);
      navigationList = new NavigationListSilverpeasV5();
    }
    return navigationList;
  }

  /**
   * Construct a new button.
   * @deprecated
   * @return returns an object implementing the FormButton interface. That's the new button to use.
   * @param label The new button label
   * @param action The action associated exemple : "javascript:history.back()", or
   * "http://www.stratelia.com/"
   * @param disabled Specify if the button is disabled or not. If disabled, no action will be
   * possible.
   * @param imagePath The path where the images needed to display buttons will be found.
   */
  public Button getFormButton(String label, String action, boolean disabled,
      String imagePath) {
    return getFormButton(label, action, disabled);
  }

  /**
   * Build a new TabbedPane.
   * @return An object implementing the TabbedPane interface.
   */
  public TabbedPane getTabbedPane() {
    String tabbedPaneClassName = getFavoriteLookSettings().getString(
        "TabbedPane");
    TabbedPane tabbedPane = null;

    try {
      tabbedPane = (TabbedPane) Class.forName(tabbedPaneClassName)
          .newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getTabbedPane()",
          "viewgenerator.EX_CANT_GET_TABBED_PANE", "", e);
      tabbedPane = new TabbedPaneSilverpeasV5();
    } finally {
      tabbedPane.init(1);
    }
    return tabbedPane;
  }

  /**
   * Build a new TabbedPane.
   * @return An object implementing the TabbedPane interface.
   */
  public TabbedPane getTabbedPane(int nbLines) {
    String tabbedPaneClassName = getFavoriteLookSettings().getString(
        "TabbedPane");
    TabbedPane tabbedPane = null;

    try {
      tabbedPane = (TabbedPane) Class.forName(tabbedPaneClassName)
          .newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getTabbedPane()",
          "viewgenerator.EX_CANT_GET_TABBED_PANE", " nbLines = " + nbLines, e);
      tabbedPane = new TabbedPaneSilverpeasV5();
    } finally {
      tabbedPane.init(nbLines);
    }
    return tabbedPane;
  }

  /**
   * Build a new ArrayPane.
   * @deprecated
   * @param name The name from your array. This name has to be unique in the session. It will be
   * used to put some information (including the sorted column), in the session. exemple :
   * "MyToDoArrayPane"
   * @param pageContext The page context computed by the servlet or JSP. The PageContext is used to
   * both get new request (sort on a new column), and keep the current state (via the session).
   * @return An object implementing the ArrayPane interface.
   */
  public ArrayPane getArrayPane(String name,
      javax.servlet.jsp.PageContext pageContext) {
    String arrayPaneClassName = getFavoriteLookSettings()
        .getString("ArrayPane");
    ArrayPane arrayPane = null;

    try {
      arrayPane = (ArrayPane) Class.forName(arrayPaneClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getArrayPane()",
          "viewgenerator.EX_CANT_GET_ARRAY_PANE", " name = " + name, e);
      arrayPane = new ArrayPaneSilverpeasV5();
    } finally {
      arrayPane.init(name, pageContext);
    }
    return arrayPane;
  }

  /**
   * Build a new ArrayPane.
   * @deprecated
   * @param name The name from your array. This name has to be unique in the session. It will be
   * used to put some information (including the sorted column), in the session. exemple :
   * "MyToDoArrayPane"
   * @param request The http request (to get entering action, like sort operation)
   * @param session The client session (to get the old status, like on which column we are sorted)
   * @return An object implementing the ArrayPane interface.
   */
  public ArrayPane getArrayPane(String name,
      javax.servlet.ServletRequest request,
      javax.servlet.http.HttpSession session) {
    String arrayPaneClassName = getFavoriteLookSettings()
        .getString("ArrayPane");
    ArrayPane arrayPane = null;

    try {
      arrayPane = (ArrayPane) Class.forName(arrayPaneClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getArrayPane()",
          "viewgenerator.EX_CANT_GET_ARRAY_PANE", " name = " + name, e);
      arrayPane = new ArrayPaneSilverpeasV5();
    } finally {
      arrayPane.init(name, request, session);
    }
    return arrayPane;
  }

  /**
   * Build a new ArrayPane.
   * @param name The name from your array. This name has to be unique in the session. It will be
   * used to put some information (including the sorted column), in the session. exemple :
   * "MyToDoArrayPane"
   * @param url The url to root sorting action. This url can contain parameters. exemple :
   * http://localhost/webactiv/Rkmelia/topicManager?topicId=12
   * @param request The http request (to get entering action, like sort operation)
   * @param session The client session (to get the old status, like on which column we are sorted)
   * @return An object implementing the ArrayPane interface.
   */
  public ArrayPane getArrayPane(String name, String url,
      javax.servlet.ServletRequest request,
      javax.servlet.http.HttpSession session) {
    String arrayPaneClassName = getFavoriteLookSettings()
        .getString("ArrayPane");
    ArrayPane arrayPane = null;

    try {
      arrayPane = (ArrayPane) Class.forName(arrayPaneClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getArrayPane()",
          "viewgenerator.EX_CANT_GET_ARRAY_PANE", " name = " + name, e);
      arrayPane = new ArrayPaneSilverpeasV5();
    } finally {
      arrayPane.init(name, url, request, session);
    }
    return arrayPane;
  }

  /**
   * Build a new main Window using the object specified in the properties.
   * @return An object implementing Window interface
   */
  public Window getWindow() {
    String windowClassName = getFavoriteLookSettings().getString("Window");
    Window window = null;

    try {
      window = (Window) Class.forName(windowClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator", "GraphicElementFactory.getWindow()",
          "viewgenerator.EX_CANT_GET_WINDOW", "", e);
      window = new WindowWeb20V5();
    } finally {
      window.init(this);
    }
    return window;
  }

  /**
   * Build a new ButtonPane.
   * @return An object implementing the ButtonPane interface
   */
  public ButtonPane getButtonPane() {
    String buttonPaneClassName = getFavoriteLookSettings().getString(
        "ButtonPane");

    try {
      return (ButtonPane) Class.forName(buttonPaneClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getButtonPane()",
          "viewgenerator.EX_CANT_GET_BUTTON_PANE", "", e);
      return new ButtonPaneWA2();
    }
  }

  /**
   * Build a new IconPane.
   * @return An object implementing the IconPane interface.
   */
  public IconPane getIconPane() {
    String iconPaneClassName = getFavoriteLookSettings().getString("IconPane");

    try {
      return (IconPane) Class.forName(iconPaneClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator", "GraphicElementFactory.getIconPane()",
          "viewgenerator.EX_CANT_GET_ICON_PANE", "", e);
      return new IconPaneWA();
    }
  }

  /**
   * Build a new FormPane.
   * @param name
   * @param actionURL
   * @param pageContext
   * @return
   */
  public FormPane getFormPane(String name, String actionURL,
      javax.servlet.jsp.PageContext pageContext) {
    return new FormPaneWA(name, actionURL, pageContext);
  }

  /**
   * Build a new OperationPane.
   * @return An object implementing the OperationPane interface.
   */
  public OperationPane getOperationPane() {
    String operationPaneClassName = getFavoriteLookSettings().getString(
        "OperationPane");

    try {
      return (OperationPane) Class.forName(operationPaneClassName)
          .newInstance();
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getOperationPane()",
          "viewgenerator.EX_CANT_GET_OPERATION_PANE", "", e);
      return new OperationPaneSilverpeasV5Web20();
    }
  }

  /**
   * Build a new BrowseBar.
   * @return An object implementing the BrowseBar interface.
   */
  public BrowseBar getBrowseBar() {
    String browseBarClassName = getFavoriteLookSettings()
        .getString("BrowseBar");

    try {
      BrowseBar browseBar = (BrowseBar) Class.forName(browseBarClassName).newInstance();
      browseBar.setComponentId(componentId);
      browseBar.setMainSessionController(mainSessionController);
      return browseBar;
    } catch (Exception e) {
      SilverTrace.error("viewgenerator",
          "GraphicElementFactory.getBrowseBar()",
          "viewgenerator.EX_CANT_GET_BROWSE_BAR", "", e);

      BrowseBar browseBar = new BrowseBarComplete();
      browseBar.setComponentId(componentId);
      browseBar.setMainSessionController(mainSessionController);
      return browseBar;
    }
  }

  /**
   * Build a new monthCalendar.
   * @param String : the language to use by the monthCalendar
   * @return an object implementing the monthCalendar interface
   */
  public MonthCalendar getMonthCalendar(String language) {
    return new MonthCalendarWA1(language);
  }

  /**
   * Build a new Calendar.
   * @param String : the language to use by the monthCalendar
   * @return an object implementing the monthCalendar interface
   */
  public Calendar getCalendar(String context, String language, Date date) {
    return new CalendarWA1(context, language, date);
  }

  public Pagination getPagination(int nbItems, int nbItemsPerPage,
      int firstItemIndex) {
    String paginationClassName = getFavoriteLookSettings().getString("Pagination");
    Pagination pagination = null;
    if (paginationClassName == null) {
      paginationClassName =
          "com.stratelia.webactiv.util.viewGenerator.html.pagination.PaginationSP";
    }
    try {
      pagination = (Pagination) Class.forName(paginationClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.info("viewgenerator", "GraphicElementFactory.getPagination()",
          "viewgenerator.EX_CANT_GET_PAGINATION", "", e);
      pagination = new PaginationSP();
    }
    pagination.init(nbItems, nbItemsPerPage, firstItemIndex);
    pagination.setMultilang(getMultilang());
    return pagination;
  }

  public ProgressMessage getProgressMessage(List<String> messages) {
    String progressClassName = getFavoriteLookSettings().getString("Progress");
    ProgressMessage progress = null;
    if (progressClassName == null) {
      progressClassName =
          "com.stratelia.webactiv.util.viewGenerator.html.progressMessage.ProgressMessageSilverpeasV5";
    }
    try {
      progress = (ProgressMessage) Class.forName(progressClassName).newInstance();
    } catch (Exception e) {
      SilverTrace.info("viewgenerator", "GraphicElementFactory.getProgressMessage()",
          "viewgenerator.EX_CANT_GET_PROGRESSMESSAGE", "", e);
      progress = new ProgressMessageSilverpeasV5();
    }
    progress.init(messages);
    progress.setMultilang(getMultilang());
    return progress;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  public String getComponentId() {
    return componentId;
  }

  public void setMainSessionController(MainSessionController mainSessionController) {
    this.mainSessionController = mainSessionController;
  }

  public MainSessionController getMainSessionController() {
    return mainSessionController;
  }

}