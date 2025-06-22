<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Award Achievement - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .form-container {
            background-color: #fff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0,0,0,.1);
            max-width: 700px;
            margin: 20px auto;
        }
        .form-container h2 {
            text-align: center;
            color: #003366;
            margin-bottom: 25px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 8px;
            color: #333;
        }
        .form-group select,
        .form-group input[type="text"],
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 8px;
            font-size: 1em;
            box-sizing: border-box; /* Important for padding */
        }
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        .submit-btn {
            width: 100%;
            padding: 15px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: 700;
            cursor: pointer;
            transition: background-color 0.2s ease;
        }
        .submit-btn:hover {
            background-color: #218838;
        }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Award Achievement" scope="request"/>
    <jsp:include page="/includes/staffSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />
        <div class="form-container">
            <h2>Award a New Achievement</h2>
            <form action="${pageContext.request.contextPath}/AwardAchievementServlet" method="POST">
                <div class="form-group">
                    <label for="studentId">Select Student</label>
                    <select id="studentId" name="student_id" required>
                        <option value="">-- Choose a student --</option>
                        <c:forEach var="student" items="${studentList}">
                            <option value="${student.student_no}">${student.student_name} (${student.student_no})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="title">Achievement Title</label>
                    <input type="text" id="title" name="title" placeholder="e.g., Dean's List Award" required>
                </div>
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" placeholder="Describe the achievement..." required></textarea>
                </div>
                 <%-- Activity ID is optional for manually awarded achievements --%>
                <input type="hidden" name="activity_id" value="0">
                <button type="submit" class="submit-btn">Grant Achievement</button>
            </form>
        </div>
        <jsp:include page="/includes/mainFooter.jsp" />
    </div>
</body>
</html>
