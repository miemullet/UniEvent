<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Activity History - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .table-container { background-color: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); overflow-x: auto; }
        .activity-history-table { width: 100%; border-collapse: collapse; min-width: 700px; }
        .activity-history-table th, .activity-history-table td { text-align: left; padding: 12px 15px; border-bottom: 1px solid #eee; font-size: 14px; vertical-align: middle; }
        .activity-history-table th { background-color: #f8f9fa; font-weight: 600; color: #555; text-transform: uppercase; }
        .activity-history-table tbody tr:hover { background-color: #f0f8ff; }
        .status-badge { display: inline-block; padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; text-transform: uppercase; color: #fff; }
        .status-badge.completed { background-color: #28a745; }
        .status-badge.in-progress { background-color: #ffc107; color: #212529; }
        .status-badge.approved { background-color: #007bff; }
        .status-badge.pending { background-color: #ffc107; color: #212529; }
        .status-badge.rejected { background-color: #dc3545; }
        .status-badge.cancelled { background-color: #6c757d; }
        .action-btns { display: flex; gap: 8px; }
        .view-details-btn, .view-cert-btn { display: inline-block; padding: 8px 15px; border:none; border-radius:6px; text-decoration: none; font-size:13px; font-weight: 500; color: #fff; text-align:center; transition: background-color 0.2s ease; }
        .view-details-btn { background-color:#0f60b6; }
        .view-details-btn:hover { background-color:#0d529a; }
        .view-cert-btn { background-color: #28a745; }
        .view-cert-btn:hover { background-color: #218838; }
        .view-cert-btn.disabled { background-color: #6c757d; cursor: not-allowed; pointer-events: none; }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Activity History" scope="request"/>
    <jsp:include page="/includes/studentSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />
        <div class="table-container">
            <table class="activity-history-table">
                <thead>
                    <tr><th>No.</th><th>Activity Name</th><th>Club</th><th>Date</th><th>Status</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty registeredActivities}">
                            <c:forEach var="activity" items="${registeredActivities}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><strong><c:out value="${activity.activity_name}"/></strong></td>
                                    <td><c:out value="${activity.club_name}"/></td>
                                    <td><fmt:formatDate value="${activity.activity_startdate}" pattern="dd MMM, yyyy"/></td>
                                    <td><span class="status-badge ${fn:toLowerCase(fn:replace(activity.activity_status, ' ', '-'))}"><c:out value="${activity.activity_status}"/></span></td>
                                    <td>
                                        <div class="action-btns">
                                            <a href="${pageContext.request.contextPath}/student/eventDetails?activity_id=${activity.activity_id}" class="view-details-btn">Details</a>
                                            <c:choose>
                                                <c:when test="${not empty activity.registration_cert_path}">
                                                    <a href="${pageContext.request.contextPath}/${activity.registration_cert_path}" target="_blank" class="view-cert-btn">View Certificate</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="#" class="view-cert-btn disabled">Certificate N/A</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="6" style="text-align: center; padding: 25px;">You have not registered for any activities yet.</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
      <jsp:include page="/includes/mainFooter.jsp" />
    </div>
          
          
          <script>
   function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');
    const topbar = document.querySelector('.topbar');
    const subHeader = document.querySelector('.sub-header');
    const footer = document.querySelector('.page-footer');

    sidebar.classList.toggle("hidden");
    sidebar.classList.toggle("collapsed");


    const isHidden = sidebar.classList.contains("hidden");
    const margin = isHidden ? "0" : "220px";

    mainContent.style.marginLeft = margin;
    topbar.style.marginLeft = margin;
    subHeader.style.marginLeft = margin;
    footer.style.marginLeft = margin;
  }
</script>
</body>
</html>
