<%--<%@ include file="check.jsp" %>--%>

<style type="text/css">
#informations{
width: 100%;
}

.fixe{
width : 100%;
height:25px;
}

.define{
float:left;
width:25%;
font-weight:bold;

}

#myInfoAction{
text-align: center;
padding-left: 40%;

}

.button {
float:left;
margin: 4px;
}


</style>
<view:tabs >
    <view:tab label="${wall}" action="ALL" selected="false" />
    <view:tab label="${infos}" action="MyInfos" selected="true" />
    <view:tab label="${events}" action="MyEvents" selected="false" />
    <view:tab label="${publications}" action="MyPubs" selected="false" />
    <view:tab label="${photos}" action="MyPhotos" selected="false" />
</view:tabs>

<!--<view:board  >-->
    <view:frame >

      
      <view:frame title="Informations professionnelles & Coordonnées ">
            <a  id="myInfoUpdate" href="#"  onclick="javascript:enableFields()">
                <img  src=" <c:url value="/directory/jsp/icons/edit_button.gif" />" width="15" height="15"
                      alt="connected"/>
            </a>
            <view:board>
                <form method="POST" name="myInfoUpdateForm" action="">
                    <div id="informations">
                      <div class="fixe">
                          <div class="define"><fmt:message key="GML.position" bundle="${GML}"/></div>
                            <div>
                                <fmt:message key="${snUserFull.userFull.accessLevel}" var="position" />
                                <fmt:message key="${position}" bundle="${GML}"/>
                            </div>
                      </div>
                        
                      <div class="fixe">
                        <div class="define"><fmt:message key="GML.eMail" bundle="${GML}"/></div>
                            <div >
                                ${snUserFull.userFull.eMail}
                            </div>
                      </div>
                       

                        <c:forEach items="${propertiesKey}" var="propertys" varStatus="status">

                             <div class="fixe">
                                <div class="define">
                                    ${propertiesKey[status.index]}
                                </div>
                                <div>
                                    <input type="text" id="${properties[status.index]}" name="prop_${properties[status.index]}" size="50" maxlength="99" value="${propertiesValue[status.index]}">
                                </div>
                            </div>
                        </c:forEach>
                    
                    </div>
                    <!--<table border="0" cellspacing="0" cellpadding="5" width="100%">

                       
                 </table>
                --></form>
            </view:board>
            <div id="myInfoAction">
                    <div class="button">
                        <fmt:message key="GML.validate" bundle="${GML}" var="validate" />
                        <view:button label="${validate}" action="javascript:submitUpdate()"  />
                    </div>
                    <div class="button">
                     <fmt:message key="GML.cancel" bundle="${GML}" var="cancel" />
                        <view:button label="${cancel}" action="MyInfos"  />
                    </div>
            </div>
        </view:frame>
    </view:frame>
<!--</view:board>-->
<script>
    desabledFields();
</script>


