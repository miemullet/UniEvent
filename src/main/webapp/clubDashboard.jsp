<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Club Dashboard - UniEvent</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        html, body {
    height: 100%;
    margin: 0;
}

body.dashboard-page {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

.main-content1 {
    flex: 1;
}

        .dashboard-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 20px;
            margin: 0px auto;       /* ✅ centers the section */
            max-width: 900px;         /* ✅ limits how wide it can stretch */
            padding: 0 10px;          /* ✅ adds some horizontal breathing room */
        }


        .main-content1 {
            margin-left: 220px;
            padding: 20px 30px 0px 30px;
            flex-grow: 1;
            margin-top: 70px; /* Space for topbar and sub-header */
            transition: margin-left 0.3s; 
        }

        .card {
            background-color: #fff;
            padding: 15px; /* Reduced from 25px */
            border-radius: 12px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
            text-align: center;
            transition: transform 0.2s ease-in-out;
            height: 100px; /* Smaller fixed height */
            display: flex;
            flex-direction: column;
            justify-content: center;
        }


        .card:hover {
            transform: translateY(-5px);
        }

        .card h3 {
            font-size: 1.1em;
            color: #555;
            margin-bottom: 10px;
            font-weight: 600;
        }

        .card .value {
            font-size: 2.5em;
            font-weight: 700;
            color: #6b46f2;
        }

        .dashboard-sections {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            justify-content: center; /* ✅ Center the cards horizontally */
            margin: 10px auto;        /* ✅ Add some margin and center container */
            max-width: 1000px;        /* ✅ Optional: control overall width */
        }

        .list-container {
            background-color: #fff;
            padding: 15px 20px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,.06);
            width: 100%;
            max-width: 500px;
            flex: 1 1 45%;
        }

        .section-header {
            font-size: 1.2em;
            color: #333;
            margin-bottom: 10px;
            font-weight: 600;
            border-bottom: 1px solid #ddd;
            padding-bottom: 5px;
        }

        .list-container ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .list-container li {
            display: flex;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
            font-size: 0.95em;
        }

        .list-container li:last-child {
            border-bottom: none;
        }

        .item-icon {
            width: 32px;
            height: 32px;
            background-color: #e6e6fa;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.1em;
            color: #6b46f2;
            flex-shrink: 0;
        }

        .item-info {
            flex-grow: 1;
            margin-left: 15px;
        }

        .item-title {
            font-weight: 600;
            color: #333;
        }

        .item-meta {
            font-size: 0.85em;
            color: #777;
        }

        .no-data-message {
            text-align: center;
            padding: 30px;
            color: #777;
            font-style: italic;
        }

        @media (max-width: 992px) {
            .dashboard-sections {
                flex-direction: column;
            }
        }
        
        .content-header1 {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0px;
        }
        
        h7 {
            display: block;
            font-size: 1.5em;
            margin-block-start: 0.67em;
            margin-block-end: 0.67em;
            margin-inline-start: 0px;
            margin-inline-end: 0px;
            font-weight: bold;
            unicode-bidi: isolate;
        }

    </style>
</head>
<body class="dashboard-page">
    <div class="page-wrapper">
    <c:set var="pageTitle" value="Club Dashboard" scope="request"/>
    <jsp:include page="/includes/mainHeader.jsp" />

    <div class="main-content1">
            <jsp:include page="/includes/clubSidebar.jsp" />

        <div class="content-header1">
            <h7>Welcome, <c:out value="${sessionScope.studentName}"/>!</h7>
        </div>

        <div class="dashboard-cards">
            <div class="card">
                <h3>Upcoming Events</h3>
                <div class="value"><c:out value="${upcomingEvents.size()}"/></div>
            </div>
            <div class="card">
                <h3>Total Members</h3>
                <div class="value"><c:out value="${totalMembers}"/></div>
            </div>
            <div class="card">
                <h3>Approved Activities</h3>
                <div class="value"><c:out value="${approvedActivitiesCount}"/></div>
            </div>
            <div class="card">
                <h3>Pending Proposals</h3>
                <div class="value"><c:out value="${pendingActivitiesCount}"/></div>
            </div>
        </div>

        <div class="dashboard-sections">
            <div class="list-container">
                <h2 class="section-header">Upcoming Events</h2>
                <ul>
                    <c:choose>
                        <c:when test="${not empty upcomingEvents}">
                            <c:forEach var="event" items="${upcomingEvents}">
                                <li>
                                    <div class="item-icon">📅</div>
                                    <div class="item-info">
                                        <div class="item-title"><c:out value="${event.activity_name}"/></div>
                                        <div class="item-meta">
                                            <fmt:formatDate value="${event.activity_startdate}" pattern="MMMM dd, yyyy"/>
                                            - <c:out value="${event.activity_location}"/>
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="no-data-message">No upcoming events.</p>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>

            <div class="list-container">
                <h2 class="section-header">Newest Members</h2>
                <ul>
                    <c:choose>
                        <c:when test="${not empty newMembers}">
                            <c:forEach var="member" items="${newMembers}">
                                <li>
                                    <div class="item-icon">👤</div>
                                    <div class="item-info">
                                        <div class="item-title"><c:out value="${member.student_name}"/></div>
                                        <div class="item-meta"><c:out value="${member.student_no}"/></div>
                                    </div>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="no-data-message">No new members.</p>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </div>
    </div>        <jsp:include page="/includes/mainFooter.jsp" />

</body>
</html>
