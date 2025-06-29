<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Activities - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .table-container {
            background-color: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            overflow-x: auto;
        }

        .activity-table {
            width: 100%;
            border-collapse: collapse;
            min-width: 800px;
            font-size: 15px;
        }

        .activity-table th, .activity-table td {
            text-align: left;
            padding: 14px;
            border-bottom: 1px solid #f0f0f0;
            vertical-align: middle;
        }

        .activity-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
            text-transform: uppercase;
            font-size: 12px;
        }

        .activity-table tbody tr:hover {
            background-color: #f1f7ff;
        }

        .activity-table td strong {
            font-weight: 600;
            color: #333;
        }

        .status-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            color: #fff;
        }

        .status-approved { background-color: #28a745; }
        .status-pending { background-color: #ffc107; color: #333; }
        .status-rejected, .status-cancelled { background-color: #dc3545; }
        .status-completed { background-color: #6c757d; }
        .status-in-progress { background-color: #17a2b8; }

        .view-details-btn {
            background-color: #007bff;
            color: white;
            padding: 8px 18px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: background-color 0.2s ease, box-shadow 0.2s ease;
        }

        .view-details-btn:hover {
            background-color: #0056b3;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
        }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Activities" scope="request"/>
    <jsp:include page="/includes/staffSidebar.jsp" />
    <jsp:include page="/includes/mainHeader.jsp" />
    
    <div class="main-content">

        <c:if test="${param.update == 'success'}">
            <div class="status-message success-message" style="margin-bottom: 20px;">
                Activity status has been updated successfully.
            </div>
        </c:if>

        <div class="table-container">
            <table class="activity-table">
                <thead>
                    <tr>
                        <th>Activity Name</th>
                        <th>Club</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>

                    <c:set var="recordsPerPage" value="7" />
                    <c:set var="currentPage" value="${param.page != null ? param.page : 1}" />
                    <c:set var="startIndex" value="${(currentPage - 1) * recordsPerPage}" />
                    <c:set var="endIndex" value="${startIndex + recordsPerPage}" />
                    <c:set var="totalRecords" value="${fn:length(activities)}" />
                    <c:set var="totalPages" value="${(totalRecords / recordsPerPage) + (totalRecords % recordsPerPage > 0 ? 1 : 0)}" />

                    <c:forEach var="i" begin="${startIndex}" end="${endIndex - 1}">
                        <c:if test="${i < totalRecords}">
                            <c:set var="activity" value="${activities[i]}" />
                            <tr>
                                <td><strong>${activity.activity_name}</strong></td>
                                <td>${activity.club_name}</td>
                                <td><fmt:formatDate value="${activity.activity_startdate}" pattern="dd MMM, yyyy"/></td>
                                <td>
                                    <span class="status-badge status-${fn:toLowerCase(activity.activity_status)}">
                                        ${activity.activity_status}
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/staff/activityDetails?activity_id=${activity.activity_id}" class="view-details-btn">View Details</a>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>

                </tbody>
            </table>
        </div>

        <div style="margin-top: 20px; display: flex; justify-content: flex-end; padding-right: 20px;">
            <c:if test="${totalPages > 1}">
                <nav>
                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}"
                           style="margin: 0 4px; padding: 6px 12px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px;">
                            Previous
                        </a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span style="margin: 0 4px; font-weight: bold; background-color: #007bff; color: white; padding: 6px 12px; border-radius: 4px;">
                                    ${i}
                                </span>
                            </c:when>
                            <c:otherwise>
                                <a href="?page=${i}"
                                   style="margin: 0 4px; padding: 6px 12px; color: #007bff; text-decoration: none; border-radius: 4px; border: 1px solid #007bff;">
                                    ${i}
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}"
                           style="margin: 0 4px; padding: 6px 12px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px;">
                            Next
                        </a>
                    </c:if>
                </nav>
            </c:if>
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