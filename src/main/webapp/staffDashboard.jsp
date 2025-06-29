<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff Dashboard - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <!-- [FIX] Changed the href to use the absolute context path -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    
    <style>
      

  .dashboard-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 30px;
    margin: auto;
    margin-bottom: 50px;
    width: 70%;
  }

  .dashboard-card {
    background-color: #fff;
    padding: 15px;
    border-radius: 10px;
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.07);
    text-align: center;
  }

  .dashboard-card h3 {
    font-size: 1em;
    margin-bottom: 5px;
    color: #555;
  }

  .dashboard-card .count {
    font-size: 2em;
    font-weight: 600;
    color: #4285f4;
  }

  .dashboard-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.07);
    width: 50%;
    margin: auto;
  }

  .dashboard-section h4 {
    font-size: 1.2em;
    margin-bottom: 15px;
    font-weight: 600;
    color: #333;
    border-bottom: 1px solid #eee;
    padding-bottom: 8px;
  }

  .item-list {
    list-style: none;
    padding: 0;
  }

  .item-list li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 0;
    font-size: 0.95em;
    border-bottom: 1px solid #eee;
  }

  .item-status {
    font-size: 0.75em;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 20px;
    background-color: #ffd93d;
    color: #000;
  }

  .view-details-btn {
    background-color: #0f60b6;
    color: #fff;
    padding: 6px 12px;
    font-size: 13px;
    border-radius: 6px;
    text-decoration: none;
  }

  .view-details-btn:hover {
    background-color: #0c4ea2;
  }
</style>

</head>
<body class="dashboard-page">
    <div class="page-wrapper">

    <c:set var="pageTitle" value="Staff Dashboard" scope="request"/>
            <jsp:include page="/includes/staffSidebar.jsp" />
            <jsp:include page="/includes/mainHeader.jsp" />
                    
    <div class="main-content">
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>Total Activities</h3>
                <div class="count">${totalActivities}</div>
                <p>All events in the system.</p>
            </div>
            <div class="dashboard-card">
                <h3>Total Students</h3>
                <div class="count">${totalStudents}</div>
                <p>Active students in the system.</p>
            </div>
            <div class="dashboard-card">
                <h3>Total Clubs</h3>
                <div class="count">${totalClubs}</div>
                <p>Registered clubs.</p>
            </div>
        </div>

        <div class="dashboard-section">
            <h4>Pending Activity Proposals</h4>
            <ul class="item-list">
                <c:choose>
                    <c:when test="${not empty pendingActivities}"><c:forEach var="activity" items="${pendingActivities}">
                        <li>
                            <span class="item-title">${activity.activity_name} by ${activity.club_name}</span>
                            <span class="item-status">PENDING</span>
                            <a href="${pageContext.request.contextPath}/staff/activityDetails?activity_id=${activity.activity_id}" class="view-details-btn">View Details</a>
                        </li>
                    </c:forEach></c:when>
                    <c:otherwise><li>No pending activity proposals.</li></c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</div>                <jsp:include page="/includes/mainFooter.jsp" />

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
  }</script>

</body>
</html>
