<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Feedback - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .feedback-form-card,
        .modal-content {
            background-color: #fff;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,.1);
            max-width: 800px;
            margin: auto;
        }

        .feedback-form-card h4,
        .past-feedback-section h4 {
            font-size: 22px;
            color: #003366;
            margin-bottom: 20px;
            text-align: center;
        }

        .rating {
            display: flex;
            gap: 5px;
            font-size: 30px;
            color: #ffd93d;
            cursor: pointer;
            justify-content: center;
            margin-bottom: 20px;
        }

        .star {
            transition: transform .2s ease;
        }

        .star:hover {
            transform: scale(1.1);
        }

        .submit-feedback-btn {
            background-color: #0f60b6;
            color: #fff;
            padding: 12px 25px;
            border: none;
            border-radius: 8px;
            font-weight: 700;
            cursor: pointer;
            font-size: 16px;
            width: 100%;
        }

        .past-feedback-section {
            padding-bottom: 40px;
        }

        .feedback-list {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }

        .feedback-card {
            background-color: #fff;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0,0,0,.1);
        }

        .feedback-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }

        .feedback-stars {
            color: #ffd93d;
            font-size: 18px;
        }

        .feedback-comment {
            font-size: 14px;
            color: #555;
        }

        .feedback-activity-tag {
            background-color: #c6c2dd;
            color: #333;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        /* MODAL */
        .modal-overlay {
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.5);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 999;
        }

        .modal-content {
            position: relative;
            width: 90%;
            max-width: 600px;
            animation: fadeIn 0.3s ease;
        }

        .close-modal {
            position: absolute;
            top: 10px; right: 15px;
            background: none;
            border: none;
            font-size: 24px;
            color: #888;
            cursor: pointer;
        }

        .open-modal-btn {
            background-color: #0f60b6;
            color: white;
            padding: 10px 25px;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            display: block;
            margin: 0 auto 20px auto;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body class="dashboard-page">
<c:set var="pageTitle" value="Feedback" scope="request"/>
<jsp:include page="/includes/studentSidebar.jsp"/>

<div class="main-content">
    <jsp:include page="/includes/mainHeader.jsp"/>

    <!-- Modal Popup Form -->
    <div class="modal-overlay" id="feedbackModal">
        <div class="modal-content">
            <button class="close-modal" onclick="closeModal()">×</button>
            <h4>Submit New Feedback</h4>
            <form action="${pageContext.request.contextPath}/FeedbackServlet" method="post">
                <input type="hidden" name="action" value="submitFeedback">
                <div class="form-group">
                    <label for="activityId">Select Activity</label>
                    <select id="activityId" name="activity_id" required>
                        <option value="">-- Select an activity --</option>
                        <c:forEach var="activity" items="${attendedActivities}">
                            <option value="${activity.activity_id}">${activity.activity_name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Overall Rating</label>
                    <div class="rating" id="starRating">
                        <span class="star" data-value="1">☆</span>
                        <span class="star" data-value="2">☆</span>
                        <span class="star" data-value="3">☆</span>
                        <span class="star" data-value="4">☆</span>
                        <span class="star" data-value="5">☆</span>
                    </div>
                    <input type="hidden" name="rating" id="ratingInput" value="0" required>
                </div>
                <div class="form-group">
                    <label for="comment">Your Comment</label>
                    <textarea id="comment" name="comment" placeholder="Share your thoughts..." rows="5" required></textarea>
                </div>
                <button type="submit" class="submit-feedback-btn">Submit Feedback</button>
            </form>
        </div>
    </div>

    <!-- Past Feedback Section -->
    <div class="past-feedback-section">
        <h4>Your Past Feedback</h4>
        <button class="open-modal-btn" onclick="openModal()">+ Submit New Feedback</button>

        <div class="feedback-list">
            <c:choose>
                <c:when test="${not empty studentFeedbackList}">
                    <c:forEach var="feedback" items="${studentFeedbackList}">
                        <div class="feedback-card">
                            <div class="feedback-header">
                                <div>
                                    <div class="feedback-name-date">
                                        You on <fmt:formatDate value="${feedback.feedback_date}" pattern="dd MMM, yyyy"/>
                                    </div>
                                    <div class="feedback-stars">
                                        <c:forEach begin="1" end="${feedback.feedback_rating}">★</c:forEach>
                                        <c:forEach begin="${feedback.feedback_rating + 1}" end="5">☆</c:forEach>
                                    </div>
                                </div>
                                <div class="feedback-activity-tag">${feedback.activity_name}</div>
                            </div>
                            <p class="feedback-comment"><c:out value="${feedback.feedback_comment}"/></p>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p style="text-align:center;">You haven't submitted any feedback yet.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <jsp:include page="/includes/mainFooter.jsp"/>
</div>

<script>
    const modal = document.getElementById("feedbackModal");
    const ratingInput = document.getElementById("ratingInput");
    const stars = document.querySelectorAll("#starRating .star");

    function openModal() {
        modal.style.display = "flex";
    }

    function closeModal() {
        modal.style.display = "none";
    }

    stars.forEach((star, index) => {
        star.addEventListener("click", () => {
            ratingInput.value = index + 1;
            updateStars(index + 1);
        });

        star.addEventListener("mouseover", () => updateStars(index + 1));
        star.addEventListener("mouseout", () => updateStars(ratingInput.value));
    });

    function updateStars(activeCount) {
        stars.forEach((s, i) => s.textContent = i < activeCount ? '★' : '☆');
    }

    updateStars(0); // default
</script>
</body>
</html>