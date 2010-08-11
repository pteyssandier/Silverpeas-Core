<%--

    Copyright (C) 2000 - 2009 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have received a copy of the text describing
    the FLOSS exception, and it is also available here:
    "http://repository.silverpeas.com/legal/licensing"

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ include file="check.jsp" %>
<html>
  <head>
    <view:looknfeel />
    <link rel="stylesheet" type="text/css" href="<c:url value="/socialNetwork/jsp/myContactProfil/myContactProfil.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/directory/jsp/directoryPopup.css"/>"/>
    <title><fmt:message key="invitation.action.title" /> </title>
    <script type="text/javascript" src="<c:url value="/util/javaScript/jquery/jquery-1.3.2.min.js" />" ></script>
    <script type="text/javascript" src="<c:url value="/directory/jsp/directory.js" />" ></script>
    <script language="JavaScript">


      <%--*****************   profil Body *******************************************--%>
        var offset=0;
        var hscroll ;var vscroll

        function getNext(url)
        {

          hscroll = (document.all ? document.scrollLeft : window.pageXOffset);
          vscroll = (document.all ? document.scrollTop : window.pageYOffset);
          $.getJSON(url+offset,
          function(data){
            var listEmpty=true;
            var html='<ol class="message_list">';
            $.each(data, function(key,map){
              html+='<li>';
              html+='<table cellpadding=\"5\" cellspacing=\"0\" border=\"0\" width=\"98%\" class=\"tableBoard\"><tr><td nowrap=\"nowrap\">';
              $.each(map, function(i,listSocialInfo){

                listEmpty=false;
                if(i==0)
                {
                  html+='<b>'+listSocialInfo.day+'</b><br>'
                }else
                {
                  $.each(listSocialInfo, function(index,socialInfo){
                    html+='<table class="socialTable">';
                    html+='<tr ><td class="socialIcon">';
                    if(socialInfo.type=='RELATIONSHIP')
                    {
                      html+='<img src="'+socialInfo.icon+'" id="socialIcon" />';
                      html+='</td>';
                      html+='<td class="socialTitle">';
                      html+=socialInfo.author+' ${relationShipSuffix} '+'<a href="'+socialInfo.url+
                        '" >'+socialInfo.title+'</a>'+' ${relationShipPrefix} ';
                      html+='</td><td>'+socialInfo.hour+'</td></tr>';
                      html+='</table>'
                    }else  if(socialInfo.type=='STATUS')
                    {
                      html+='<img src="'+socialInfo.icon+'"  />';
                      html+='</td>';
                      html+='<td class="socialTitle">';
                      html+='<a href="#" >'+socialInfo.title+'</a>'+' ${statusSuffix} ';
                      html+='</td><td>'+socialInfo.hour+'</td></tr>';
                      html+='<td colspan="3">'+socialInfo.description;
                      html+='</td> </tr> </table>'
                    }else
                    {
                      html+='<img src="'+socialInfo.icon+'" id="socialIcon" />';
                      html+='</td>';
                      html+='<td class="socialTitle">';
                      html+='<a href="'+socialInfo.url+'" >'+socialInfo.title+'</a>  ';
                      html+='</td><td>'+socialInfo.hour+'</td></tr>';
                      html+='<td colspan="3">'+socialInfo.description;
                      html+='</td> </tr> </table>'
                    }
                    html+=' <br>';

                  });
                }
              });
              html+='</li>';
              html+='</td></tr></table>';
            });
            html+='</ol>';

            $('#SocialInformations').append(html);

            window.scrollTo(hscroll, vscroll);


            if(listEmpty){
              objDiv = document.getElementById("getNext").style;
              objDiv.visibility='hidden';
            }

          });

          offset++;// incrementer the offset (First element )

          var contenu = $.trim($(".list").html());
          if (contenu == ""){
            $(".list").remove();
          }

        }


        function zoom(test)
        {
          alert(test);
          var event =  window.event;
          var Id='PHOTO'+event.target.id ;


          $("table#"+Id).css({'z-index' : '40'}); /*Add a higher z-index value so this image stays on top*/
          $("table#"+Id).find('img').animate({
            marginTop: '0px', /* The next 4 lines will vertically align this image */
            marginLeft: '0px',
            top: '50%',
            left: '50%',
            width: '174px', /* Set new width */
            height: '174px' /* Set new height */

          }, 200)
        }
        function backToDefault(test)
        {
          var event =  window.event;
          var Id='PHOTO'+event.target.id ;

          $("div#"+Id).css({'z-index' : '0'}); /* Set z-index back to 0 */
          $("div#"+Id).find('img').animate({
            marginTop: '0', /* Set alignment back to default */
            marginLeft: '0',
            top: '0',
            left: '0',
            width: '32px', /* Set width back to default */
            height: '32px' /* Set height back to default */

          }, 400);
        }


      <%--*****************   profil Head *******************************************--%>

        function getLastStatus(url)
        {

          $.getJSON(url, function(data) {
            $('.StatusDiv').empty();
            var html='';
            html+='<textarea onblur="javascript:updateStatus(\'${urlUpdateStatus}\')" id="enabledStat" ';
            html+='type="text" cols="50" rows="3" >'+data.status+'</textarea>';
            $('.StatusDiv').append(html);
            desableStatusZone();
          });
        }
      
        function desableStatusZone()
        {
          document.getElementById("enabledStat").style.backgroundColor="#F2F2F2";
          document.getElementById("enabledStat").disabled=true;

        }
     

    </script>
  </head>
  <body>
   <view:window>
      <div id="navigation">

        <%@include file="profilNavigation.jsp" %>

      </div>
      <div id="contentAndHeader">
        <div id="header">
          <view:board>
            <%@include file="profilHead.jsp" %>
          </view:board>
        </div>
        <div id="content">
          <view:board>
            <%@include file="profilBody.jsp" %>
          </view:board>
        </div>
      </div>
      <div id="boxes">
        <div class="window" id="directory" >
          <div id="directoryHeader">
            <a href="#"class="close">Fermer</a>
          </div>
          <div id="indexAndSearch"><div id="search">
              <form name="search" action="javascript:directory('searchByKey')" method="post">
                <input type="text" name="key" value="" id="key" size="40" maxlength="60"
                       style="height: 20px"  />
                <img
                  src="<c:url value="/directory/jsp/icons/advsearch.jpg"/>"
                  width="10" height="10" alt="advsearch" />
              </form>
            </div>
          </div>
          <div id="users">

          </div>
        </div>
                  <!-- Mask to cover the whole screen -->
        <div id="mask"></div>
      </div>
    </view:window>
  </body>
</html>