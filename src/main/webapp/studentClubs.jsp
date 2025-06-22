<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Clubs - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .category-filter {
            margin-bottom: 20px;
            text-align: center;
        }
        .category-filter button {
            background-color: #e0e0e0;
            border: none;
            padding: 10px 20px;
            border-radius: 20px;
            margin: 5px;
            cursor: pointer;
            font-weight: 600;
            color: #555;
            transition: background-color .2s ease, color .2s ease;
        }
        .category-filter button.active,
        .category-filter button:hover {
            background-color: #0f60b6;
            color: #fff;
        }
        .club-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 25px;
            padding-bottom: 20px;
        }
        .club-card {
            background-color: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, .1);
            overflow: hidden;
            text-align: center;
            display: flex;
            flex-direction: column;
            transition: transform .3s ease;
        }
        .club-card:hover {
            transform: translateY(-5px);
        }
        .club-card img {
            width: 100%;
            height: 180px;
            object-fit: cover;
            border-bottom: 1px solid #eee;
        }
        .club-card-content {
            padding: 15px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .club-card-content h3 {
            font-size: 20px;
            margin-top: 0;
            margin-bottom: 10px;
            color: #003366;
        }
        .club-card-content p {
            font-size: 14px;
            color: #666;
            line-height: 1.5;
            margin-bottom: 15px;
            flex-grow: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
        }
        /* [UPDATE] New container for buttons */
        .club-card-actions {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-top: auto;
        }
        .view-club-btn, .join-club-btn {
            background-color: #4285f4;
            color: #fff;
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            display: block;
            cursor: pointer;
            transition: background-color .2s ease;
        }
        .view-club-btn:hover, .join-club-btn:hover {
            background-color: #3a75e0;
        }
        .join-club-btn[disabled] {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        .status-message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            font-weight: 500;
        }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .info { background-color: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb; }

    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Clubs" scope="request"/>
    <jsp:include page="/includes/studentSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />

        <!-- [UPDATE] Display status messages from servlet -->
        <c:if test="${not empty param.status}">
            <div class="status-message 
                <c:if test="${param.status == 'join_success'}">success</c:if>
                <c:if test="${param.status == 'already_member'}">info</c:if>
                <c:if test="${param.status == 'error'}">error</c:if>">
                <c:choose>
                    <c:when test="${param.status == 'join_success'}">Successfully joined the club!</c:when>
                    <c:when test="${param.status == 'already_member'}">You are already a member of this club.</c:when>
                    <c:when test="${param.status == 'error'}">An error occurred. Please try again.</c:when>
                </c:choose>
            </div>
        </c:if>

        <div class="category-filter">
            <button class="filter-btn active" onclick="filterClubs('all', this)">All</button>
            <c:forEach var="category" items="${categories}">
                <button class="filter-btn" onclick="filterClubs('${category.toLowerCase()}', this)">${category}</button>
            </c:forEach>
        </div>

        <div class="club-grid">
            <c:forEach var="club" items="${clubs}">
                <div class="club-card" data-category="${club.club_category.toLowerCase()}">
                    <img src="${pageContext.request.contextPath}/${club.logo_path}" onerror="this.src='${pageContext.request.contextPath}/images/default_club_logo.png'">
                    <div class="club-card-content">
                        <h3>${club.club_name}</h3>
                        <p>${club.club_desc}</p>
                        
                        <!-- [UPDATE] New action buttons container -->
                        <div class="club-card-actions">
                            <a href="${pageContext.request.contextPath}/student/clubDetails?club_id=${club.club_id}" class="view-club-btn">View Details</a>
                            
                            <c:choose>
                                <c:when test="${joinedClubIds.contains(club.club_id)}">
                                    <button class="join-club-btn" disabled>Joined</button>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/JoinClubServlet" method="POST">
                                        <input type="hidden" name="club_id" value="${club.club_id}">
                                        <button type="submit" class="join-club-btn">Join Club</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <jsp:include page="/includes/mainFooter.jsp" />
    </div>
    
    <script>
        function filterClubs(category, clickedButton) {
            document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
            clickedButton.classList.add('active');
            
            document.querySelectorAll('.club-card').forEach(card => {
                if (category === 'all' || card.dataset.category === category) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        }
    </script>
</body>
</html>
