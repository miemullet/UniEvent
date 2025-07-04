<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Club Feedback - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .feedback-container {
            padding: 20px;
        }
        .feedback-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 25px;
        }
        .feedback-card {
            background-color: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            display: flex;
            flex-direction: column;
        }
        .feedback-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 10px;
        }
        .feedback-profile {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        .feedback-profile img {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            object-fit: cover;
        }
        .feedback-name {
            font-weight: 600;
            font-size: 16px;
            color: #333;
        }
        .feedback-date {
            font-size: 13px;
            color: #888;
        }
        .tag {
             background-color: #e9ecef;
            color: #495057;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            white-space: nowrap;
        }
        .stars {
            color: #FFD93D;
            margin: 5px 0 15px 0;
            font-size: 18px;
        }
        .feedback-comment {
            font-size: 15px;
            line-height: 1.6;
            color: #555;
            flex-grow: 1;
        }
        .no-feedback {
            text-align: center;
            padding: 40px;
            background-color: #fff;
            border-radius: 12px;
            color: #777;
            grid-column: 1 / -1;
        }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Club Feedback" scope="request"/>
    <jsp:include page="/includes/clubSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />
        <div class="feedback-container">
            <div class="feedback-grid">
                <c:choose>
                    <c:when test="${not empty feedbackList}">
                        <c:forEach var="feedback" items="${feedbackList}">
                            <div class="feedback-card">
                                <div class="feedback-header">
                                    <div class="feedback-profile">
                                        <%-- Dynamically set the image source for each student --%>
                                        <c:set var="imagePath" value="${not empty feedback.student_image_path ? feedback.student_image_path : 'images/user.jpg'}" />
                                        <img src="${pageContext.request.contextPath}/${imagePath}" 
                                             alt="Profile of ${feedback.student_name}"
                                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/user.jpg';">
                                        <div>
                                            <div class="feedback-name"><c:out value="${feedback.student_name}"/></div>
                                            <div class="feedback-date"><fmt:formatDate value="${feedback.feedback_date}" pattern="dd MMM, yyyy"/></div>
                                        </div>
                                    </div>
                                    <div class="tag"><c:out value="${feedback.activity_name}"/></div>
                                </div>
                                <div class="stars">
                                    <c:forEach begin="1" end="5" varStatus="loop">
                                        ${loop.index <= feedback.feedback_rating ? '★' : '☆'}
                                    </c:forEach>
                                </div>
                                <p class="feedback-comment">“<c:out value="${feedback.feedback_comment}"/>”</p>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="no-feedback">No feedback available for this club yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <jsp:include page="/includes/mainFooter.jsp" />
    </div>
</body>
</html>
