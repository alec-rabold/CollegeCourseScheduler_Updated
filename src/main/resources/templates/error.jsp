<%@ page import="io.collegeplanner.my.collegecoursescheduler.util.Constants" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="header.jsp"/>

<div class="col-xs-11 col-md-offset-2 col-md-8">
    <h2 class="form bold f-shadow" style="text-align: center">
        <c:out value="{Error ${errorCode}}"></c:out>
    </h2>
    <hr class="preview-rule">
    <h4 class="error-message">
        <c:out value="${Constants.ERROR_MESSAGE_BASE}"></c:out>
    </h4>
    <h4 class="error-message">
        <c:out value="${errorMessage}"></c:out>
    </h4>
</div>
<jsp:include page="footer.jsp"/>