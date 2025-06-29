<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Activity Details - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }

        .details-container {
            background-color: #fff;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,.1);
            max-width: 950px;
            margin: 30px auto;
            font-size: 13px;
        }

        .details-group {
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px dashed #eee;
        }

        .details-group:last-child {
            border-bottom: none;
        }

        .details-group h4 {
            font-size: 15px;
            color: #003366;
            margin-bottom: 6px;
        }

        .details-group p, .committee-list li {
            font-size: 13px;
            line-height: 1.4;
            color: #555;
            margin-bottom: 6px;
        }

        .details-group strong {
            color: #333;
        }

        .document-link {
            display: inline-block;
            background-color: #4285f4;
            color: #fff;
            padding: 6px 12px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 500;
            margin-right: 8px;
            transition: background-color 0.2s ease;
        }

        .document-link:hover {
            background-color: #3a75e0;
        }

        .committee-list {
            list-style: none;
            padding: 0;
        }

        .committee-list li {
            background-color: #f8f9fa;
            padding: 8px 12px;
            border-radius: 6px;
            margin-bottom: 6px;
            font-size: 13px;
        }

        .action-buttons {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 20px;
            flex-wrap: wrap;
        }

        .action-buttons button {
            padding: 10px 18px;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            font-size: 13px;
        }

        .approve-btn { background-color: #28a745; color: #fff; }
        .reject-btn { background-color: #dc3545; color: #fff; }
        .return-btn { background-color: #6c757d; color: #fff; }

        .status-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
            color: #fff;
        }

        .status-badge.pending { background-color: #ffc107; color: #212529; }
        .status-badge.approved { background-color: #28a745; }
        .status-badge.rejected { background-color: #dc3545; }
        .status-badge.completed { background-color: #6c757d; }
        .status-badge.cancelled { background-color: #343a40; }
        .status-badge.inprogress { background-color: #007bff; }

        .hidden { display: none; }

        .nav-button {
            background-color: #0056b3;
            color: #fff;
            font-size: 13px;
            font-weight: 600;
            border: none;
            border-radius: 30px;
            padding: 10px 22px;
            cursor: pointer;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
            transition: all 0.2s ease-in-out;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .nav-button:hover {
            background-color: #004494;
            transform: translateY(-2px);
        }

        .next-button-wrapper {
            display: flex;
            justify-content: flex-end;
            margin-top: 10px;
        }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Activity Details" scope="request"/>
    <jsp:include page="/includes/staffSidebar.jsp" />
    <jsp:include page="/includes/mainHeader.jsp" />

    <div class="main-content">
        <div class="details-container">
            <c:if test="${not empty activity}">

                <!-- PAGE 1 -->
                <div id="page1">
                    <div class="details-group">
                        <h4>Basic Information</h4>
                        <p><strong>Activity Name:</strong> ${activity.activity_name}</p>
                        <p><strong>Organizing Club:</strong> ${activity.club_name}</p>
                        <p><strong>Status:</strong>
                            <span class="status-badge status-${fn:toLowerCase(fn:replace(activity.activity_status, '-', ''))}">
                                ${activity.activity_status}
                            </span>
                        </p>
                    </div>

                    <div class="details-group">
                        <h4>Event Details</h4>
                        <p><strong>Objectives:</strong> <c:out value="${activity.activity_objectives}"/></p>
                        <p><strong>Description:</strong> <c:out value="${activity.activity_desc}"/></p>
                        <p><strong>Target Audience:</strong> <c:out value="${activity.target_audience}"/></p>
                    </div>

                    <div class="details-group">
                        <h4>Logistics</h4>
                        <p><strong>Date & Time:</strong>
                            <fmt:formatDate value="${activity.activity_startdate}" pattern="dd MMM, yyyy, hh:mm a"/> 
                            to 
                            <fmt:formatDate value="${activity.activity_enddate}" pattern="hh:mm a"/>
                        </p>
                        <p><strong>Location:</strong> ${activity.activity_location}</p>
                    </div>

                    <div class="details-group">
                        <h4>Budget & Documents</h4>
                        <p><strong>Total Budget:</strong> RM 
                            <fmt:formatNumber value="${activity.total_budget}" pattern="#,##0.00"/>
                        </p>
                        <p>
                            <a href="${pageContext.request.contextPath}/${activity.program_flow_path}" target="_blank" class="document-link">View Program Flow</a>
                            <a href="${pageContext.request.contextPath}/${activity.budget_path}" target="_blank" class="document-link">View Budget</a>
                            <a href="${pageContext.request.contextPath}/${activity.image_path}" target="_blank" class="document-link">View Poster</a>
                        </p>

                        <!-- NEXT button aligned to bottom right of Budget section -->
                        <div class="next-button-wrapper">
                            <button class="nav-button" onclick="togglePage('page1', 'page2')">
                                Next <span>&#x2192;</span>
                            </button>
                        </div>
                    </div>
                </div>

                <!-- PAGE 2 -->
                <div id="page2" class="hidden">
                    <div class="details-group">
                        <h4>Committee</h4>
                        <ul class="committee-list">
                            <c:forTokens var="rawMember" items="${activity.committee_list}" delims="&#10;&#13;">
                                <c:set var="member" value="${fn:trim(rawMember)}"/>
                                <c:if test="${not empty member}">
                                    <li>${member}</li>
                                </c:if>
                            </c:forTokens>
                        </ul>
                    </div>

                    <c:if test="${activity.activity_status == 'PENDING'}">
                        <div class="action-buttons">
                            <form action="${pageContext.request.contextPath}/staff/activityDetails" method="post">
                                <input type="hidden" name="action" value="approveActivity">
                                <input type="hidden" name="activity_id" value="${activity.activity_id}">
                                <button type="submit" class="approve-btn">Approve</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/staff/activityDetails" method="post">
                                <input type="hidden" name="action" value="rejectActivity">
                                <input type="hidden" name="activity_id" value="${activity.activity_id}">
                                <button type="submit" class="reject-btn">Reject</button>
                            </form>
                        </div>
                    </c:if>

                    <div class="next-button-wrapper">
                        <button class="nav-button" onclick="togglePage('page2', 'page1')">
                            <span>&#x2190;</span> Previous
                        </button>
                    </div>
                </div>

            </c:if>
        </div>
        <jsp:include page="/includes/mainFooter.jsp" />
    </div>

    <script>
        function togglePage(hideId, showId) {
            document.getElementById(hideId).classList.add('hidden');
            document.getElementById(showId).classList.remove('hidden');
            window.scrollTo(0, 0);
        }
    </script>
</body>
</html>