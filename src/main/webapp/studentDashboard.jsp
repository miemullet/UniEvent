<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .dashboard-page{display:flex;flex-direction:column;min-height:100vh}
        .main-content{margin-left:220px;padding-top:80px;padding-left:30px;padding-right:30px;flex-grow:1}
        .dashboard-hero{display:flex;justify-content:space-between;align-items:center;background:#fff;padding:20px 30px;border-radius:16px;box-shadow:0 4px 8px rgba(0,0,0,.05)}
        .hero-left h1{font-size:32px;font-weight:700;color:#2c3e50}
        .dashboard-sections{display:grid; grid-template-columns: 2fr 1fr; gap:20px;margin-top:30px;}
        .left-section,.right-section{display: flex; flex-direction: column; gap: 20px;}
        .card{background:#fff;padding:20px;border-radius:16px;box-shadow:0 4px 12px rgba(0,0,0,.05)}
        .card h3, .card h4 { margin-top: 0; margin-bottom: 15px; color: #333; }
        .achievement-list,.top-student-card ul,.best-event-list{list-style:none;padding:0;margin:0}
        .badge{background:#6c4eff;color:#fff;padding:4px 8px;border-radius:20px;font-size:12px;font-weight:600}
        .achievement-list li,.top-student-card li, .best-event-list li{display:flex;justify-content:space-between;align-items:center;background:#f9f9f9;padding:8px 12px;border-radius:10px;margin-bottom:10px}
        .best-event-list li:last-child, .top-student-card li:last-child, .achievement-list li:last-child { margin-bottom: 0; }
        .club-avatars{display:flex;gap:8px;flex-wrap:wrap}
        .club-avatars img{width:40px;height:40px;border-radius:50%;object-fit:cover}
        .event-img{width:32px;height:32px;object-fit:cover;border-radius:50%;vertical-align:middle;margin-right:8px}
        .merit-circle{position:relative;width:100px;height:100px;margin:10px auto}
        .merit-text{position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);font-weight:700;font-size:24px;color:#4caf50}
        .view-btn{background-color:#6c4eff;color:#fff;padding:6px 12px;border-radius:6px;text-decoration:none;font-weight:500;transition:background-color .3s ease}
        .view-btn:hover{background-color:#5941c8}
        .rating-stars { color: #ffd700; font-size: 16px; font-weight: bold; }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Dashboard" scope="request"/>
    <jsp:include page="/includes/studentSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />

        <div class="dashboard-hero">
            <div class="hero-left">
                <h2>WELCOME</h2>
                <h1>${sessionScope.studentName}</h1>
            </div>
            <div class="hero-right"><img src="${pageContext.request.contextPath}/images/dashboard.png" alt="Banner" style="width: 160px;"></div>
        </div>

        <div class="dashboard-sections">
            <div class="left-section">
                 <div class="card">
                    <h3>In-Progress & Upcoming Events</h3>
                    <c:choose>
                        <c:when test="${not empty inProgressEvents}"><c:forEach var="activity" items="${inProgressEvents}">
                            <div style="margin-bottom:10px;padding:8px;background:#fafafa;border-radius:8px;">
                                <img src="${pageContext.request.contextPath}/${activity.image_path}" class="event-img" onerror="this.src='${pageContext.request.contextPath}/images/default_event_poster.png'">
                                <span>${activity.activity_name}</span><span style="float:right;">⏳</span>
                            </div>
                        </c:forEach></c:when>
                        <c:otherwise><p>No events currently in progress.</p></c:otherwise>
                    </c:choose>
                 </div>

                <div class="card">
                    <h3>Achievements</h3>
                    <ul class="achievement-list">
                        <c:choose>
                            <c:when test="${not empty achievements}"><c:forEach var="achievement" items="${achievements}">
                                <li><span>${achievement.title}</span><a href="${pageContext.request.contextPath}/certificate?certId=${achievement.achievement_id}" target="_blank" class="view-btn">View</a></li>
                            </c:forEach></c:when>
                            <c:otherwise><li>No achievements earned yet.</li></c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>

            <div class="right-section">
                <div class="card">
                    <h4>Total Merit Points</h4>
                    <div class="merit-circle">
                        <canvas id="meritChart" width="100" height="100"></canvas>
                        <div class="merit-text">${totalMerit}</div>
                    </div>
                </div>
                <div class="card">
                    <h4>Top Students by Merit</h4>
                    <ul class="top-student-card">
                        <c:choose>
                            <c:when test="${not empty topStudents}"><c:forEach var="student" items="${topStudents}">
                                <li><span>${student.student_name}</span><span class="badge">${student.student_merit} Merit</span></li>
                            </c:forEach></c:when>
                            <c:otherwise><li>Top student data is currently unavailable.</li></c:otherwise>
                        </c:choose>
                    </ul>
                </div>

                <div class="card">
                    <h4>Best Events by Feedback</h4>
                    <ul class="best-event-list">
                        <c:choose>
                            <c:when test="${not empty topRatedEvents}"><c:forEach var="event" items="${topRatedEvents}">
                                <li>
                                    <div>
                                        <strong>${event.activity_name}</strong><br>
                                        <small>${event.club_name}</small>
                                    </div>
                                    <span class="rating-stars">
                                        <fmt:formatNumber value="${event.averageRating}" maxFractionDigits="1"/> ★
                                    </span>
                                </li>
                            </c:forEach></c:when>
                            <c:otherwise><li>No feedback has been submitted yet.</li></c:otherwise>
                        </c:choose>
                    </ul>
                </div>

                <div class="card">
                    <h4>Joined Clubs</h4>
                    <div class="club-avatars">
                         <c:choose>
                            <c:when test="${not empty joinedClubs}"><c:forEach var="club" items="${joinedClubs}">
                                <img src="${pageContext.request.contextPath}/${club.logo_path}" title="${club.club_name}" onerror="this.src='${pageContext.request.contextPath}/images/default_club_logo.png'">
                            </c:forEach></c:when>
                            <c:otherwise><p>You haven't joined any clubs yet.</p></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/includes/mainFooter.jsp" />
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const ctx = document.getElementById('meritChart').getContext('2d');
            const meritScore = ${totalMerit};
            const maxScore = 200; 
            new Chart(ctx, {
                type: 'doughnut', data: { datasets: [{ data: [meritScore, Math.max(0, maxScore - meritScore)], backgroundColor: ['#4caf50', '#e0e0e0'], borderWidth: 0 }] },
                options: { cutout: '80%', plugins: { tooltip: { enabled: false }, legend: { display: false } }, responsive: true, maintainAspectRatio: false }
            });
        });
    </script>
</body>
</html>
