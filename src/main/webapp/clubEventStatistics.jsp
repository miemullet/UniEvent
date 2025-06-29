<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Event Statistics - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .stats-container { display: grid; grid-template-columns: 1fr 2fr; gap: 30px; margin-top: 20px;}
        .stats-table-container { background-color: #fff; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,.08); }
        .stats-chart-container { background-color: #fff; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,.08); }
        .stats-table { width: 100%; border-collapse: collapse; }
        .stats-table th, .stats-table td { padding: 12px; text-align: left; border-bottom: 1px solid #eee; }
        .stats-table th { background-color: #f8f9fa; font-weight: 600; }
        .content-header h2 { color: #333; }
        .card { background-color: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,.08); }
        @media (max-width: 992px) { .stats-container { grid-template-columns: 1fr; } }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Event Statistics" scope="request"/>
    <jsp:include page="/includes/clubSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />
        
        <div class="content-header">
            <h2>Statistics for: <c:out value="${activity.activity_name}"/></h2>
        </div>

        <c:choose>
            <c:when test="${not empty courseStats}">
                <div class="stats-container">
                    <div class="stats-table-container">
                        <h3>Registrations by Course</h3>
                        <table class="stats-table">
                            <thead><tr><th>Course</th><th># of Students</th></tr></thead>
                            <tbody>
                                <c:forEach var="stat" items="${courseStats}">
                                    <tr>
                                        <td><c:out value="${stat.courseName}"/></td>
                                        <td><c:out value="${stat.studentCount}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="stats-chart-container">
                        <h3>Visual Breakdown</h3>
                        <canvas id="courseStatsChart"></canvas>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card" style="text-align: center; padding: 40px;">
                    <p>No registration data available for this event yet.</p>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
        <jsp:include page="/includes/mainFooter.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const ctx = document.getElementById('courseStatsChart');
            if (ctx) {
                // Prepare data for Chart.js
                const labels = [<c:forEach var="stat" items="${courseStats}">'${fn:escapeXml(stat.courseName)}',</c:forEach>];
                const data = [<c:forEach var="stat" items="${courseStats}">${stat.studentCount},</c:forEach>];
                const backgroundColors = [
                    'rgba(255, 99, 132, 0.6)', 'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 206, 86, 0.6)', 'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)', 'rgba(255, 159, 64, 0.6)',
                    'rgba(199, 199, 199, 0.6)', 'rgba(83, 102, 255, 0.6)'
                ];
                const borderColors = [
                    'rgba(255, 99, 132, 1)', 'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)', 'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)', 'rgba(255, 159, 64, 1)',
                    'rgba(199, 199, 199, 1)', 'rgba(83, 102, 255, 1)'
                ];


                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '# of Students Registered',
                            data: data,
                            backgroundColor: backgroundColors,
                            borderColor: borderColors,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        indexAxis: 'y', // Makes it a horizontal bar chart
                        scales: {
                            x: { 
                                beginAtZero: true,
                                ticks: {
                                    stepSize: 1 // Ensure y-axis increments by whole numbers
                                }
                            }
                        },
                        plugins: {
                            legend: { display: false },
                            title: { display: true, text: 'Student Registrations by Course' }
                        }
                    }
                });
            }
        });
    </script>
</body>
</html>
