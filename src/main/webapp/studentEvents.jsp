<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Events - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .event-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(300px,1fr));gap:25px;padding-bottom:20px}.event-card{background-color:#fff;border-radius:12px;box-shadow:0 4px 12px rgba(0,0,0,.1);overflow:hidden;display:flex;flex-direction:column;transition:transform .3s ease}.event-card:hover{transform:translateY(-5px);box-shadow:0 6px 15px rgba(0,0,0,.15)}.event-card img{width:100%;height:180px;object-fit:cover;border-bottom:1px solid #eee}.event-card-content{padding:15px;flex-grow:1;display:flex;flex-direction:column}.event-card-content h3{font-size:20px;margin-top:0;margin-bottom:10px;color:#003366}.event-details{font-size:14px;color:#555;margin-bottom:8px}.event-details strong{color:#333}.event-description{font-size:14px;color:#666;line-height:1.5;margin-bottom:15px;flex-grow:1;overflow:hidden;text-overflow:ellipsis;display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical}.register-btn{background-color:#32d183;color:#fff;padding:10px 20px;border:none;border-radius:8px;font-weight:600;text-decoration:none;display:block;margin-top:auto;transition:background-color .2s ease;text-align:center}.register-btn:hover{background-color:#2ab870}.register-btn[disabled]{background-color:#a0a0a0;cursor:not-allowed}
        .category-filter { margin-bottom: 25px; text-align: center; display: flex; flex-wrap: wrap; gap: 10px; justify-content: center; }
        .filter-btn { background-color: #e9ecef; border: none; padding: 10px 20px; border-radius: 20px; cursor: pointer; font-weight: 600; color: #495057; transition: background-color .2s ease, color .2s ease, box-shadow .2s ease; }
        .filter-btn.active, .filter-btn:hover { background-color: #6b46f2; color: white; box-shadow: 0 4px 8px rgba(107, 70, 242, 0.3); }

        /* [NEW] Pagination Styles */
        .pagination-container {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 30px;
        }
        .pagination-container a, .pagination-container span {
            color: #6b46f2;
            padding: 8px 16px;
            text-decoration: none;
            transition: background-color .3s;
            border: 1px solid #ddd;
            margin: 0 4px;
            border-radius: 6px;
            font-weight: 600;
        }
        .pagination-container a:hover {
            background-color: #f1f1f1;
        }
        .pagination-container .active {
            background-color: #6b46f2;
            color: white;
            border: 1px solid #6b46f2;
        }
        .pagination-container .disabled {
            color: #ccc;
            pointer-events: none;
            border-color: #eee;
        }

    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Events" scope="request"/>
    <jsp:include page="/includes/studentSidebar.jsp" />
    <jsp:include page="/includes/mainHeader.jsp" />

    <div class="main-content">
        <div class="category-filter">
            <button class="filter-btn active" data-filter-category="all">All Categories</button>
            <c:forEach var="category" items="${categories}">
                <button class="filter-btn" data-filter-category="${category.category_id}">${category.category_name}</button>
            </c:forEach>
        </div>

        <div class="event-grid">
            <c:choose>
                <c:when test="${not empty availableActivities}">
                    <c:forEach var="activity" items="${availableActivities}">
                        <div class="event-card" data-category-id="${activity.category_id}">
                            <img src="${pageContext.request.contextPath}/${activity.image_path}" onerror="this.src='${pageContext.request.contextPath}/images/default_event_poster.png'">
                            <div class="event-card-content">
                                <h3>${activity.activity_name}</h3>
                                <p class="event-details"><strong>Club:</strong> ${activity.club_name}</p>
                                <p class="event-details"><strong>Date:</strong> <fmt:formatDate value="${activity.activity_startdate}" pattern="dd MMM, yyyy"/></p>
                                <p class="event-description">${activity.activity_desc}</p>
                                <c:choose>
                                    <c:when test="${activity.registered}">
                                        <button class="register-btn" disabled>Registered</button>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/EventRegistrationServlet" method="post" style="margin-top:auto;">
                                            <input type="hidden" name="action" value="register">
                                            <input type="hidden" name="activity_id" value="${activity.activity_id}">
                                            <button type="submit" class="register-btn">Register Now</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p style="grid-column: 1 / -1; text-align: center;">No events available for registration.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- [NEW] Pagination Controls -->
        <div class="pagination-container">
            <c:if test="${currentPage > 1}">
                <a href="?page=${currentPage - 1}">&laquo; Previous</a>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${currentPage eq i}">
                        <span class="active">${i}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="?page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="?page=${currentPage + 1}">Next &raquo;</a>
            </c:if>
        </div>

    <jsp:include page="/includes/mainFooter.jsp" />    
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const filterButtons = document.querySelectorAll('.filter-btn');

            // --- [NEW] Logic to handle filtering with pagination ---
            filterButtons.forEach(button => {
                button.addEventListener('click', function() {
                    const categoryId = this.dataset.filterCategory;
                    // To properly filter with pagination, we must reload the page
                    // with the category as a URL parameter.
                    window.location.href = '${pageContext.request.contextPath}/student/events?category=' + categoryId;
                });
            });

            // Set the active state for the category filter based on URL param
            const urlParams = new URLSearchParams(window.location.search);
            const activeCategory = urlParams.get('category') || 'all';
            document.querySelector(`.filter-btn[data-filter-category="${activeCategory}"]`).classList.add('active');
            document.querySelectorAll('.filter-btn').forEach(btn => {
                if (btn.dataset.filterCategory !== activeCategory) {
                    btn.classList.remove('active');
                }
            });
        });
    </script>
</body>
</html>
