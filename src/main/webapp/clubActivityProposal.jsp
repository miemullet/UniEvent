<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Propose New Activity - UniEvent</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        body { overflow-x: hidden; }

        .proposal-form-container {
            background-color: #fff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0,0,0,.1);
            width: 100%;
            max-width: 700px;
            margin: auto;
            height: auto;
        }

        .form-header {
            font-size: 1.0em;
            color: #333;
            margin-bottom: 15px;
            text-align: center;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            font-weight: 500;
            margin-bottom: 5px;
            font-size: 0.95em;
        }

        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 90%;
            padding: 8px 10px;
            font-size: 0.95em;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .form-group textarea {
            resize: vertical;
        }

        .form-section { display: none; }
        .form-section.active { display: block; }

        .form-navigation {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }

        .form-navigation button {
            background-color: #6b46f2;
            color: #fff;
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 1em;
            font-weight: 600;
        }

        .form-navigation button:disabled { background-color: #ccc; }

        .progress-bar-container {
            width: 100%;
            background-color: #e0e0e0;
            border-radius: 5px;
            margin-bottom: 25px;
        }

        .progress-bar {
            width: 0%;
            height: 8px;
            background-color: #6b46f2;
            border-radius: 5px;
            transition: width 0.4s ease-in-out;
        }

        .error-message {
            color: #dc3545;
            font-size: 0.85em;
            display: none;
        }

        .form-group.invalid .error-message { display: block; }
        .form-group.invalid input,
        .form-group.invalid textarea,
        .form-group.invalid select { border-color: #dc3545; }

        .server-error-popup {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            font-weight: 500;
        }

        .form-row {
            display: flex;
            gap: 10px;
            justify-content: space-between;
        }
    </style>
</head>
<body class="dashboard-page">
<c:set var="pageTitle" value="Propose New Activity" scope="request"/>
<jsp:include page="/includes/clubSidebar.jsp" />

<div class="main-content">
    <jsp:include page="/includes/mainHeader.jsp" />

    <div class="proposal-form-container">
        <c:if test="${param.popup == 'error'}">
            <div class="server-error-popup">
                <strong>Submission Failed!</strong> An unexpected error occurred. Please check your inputs and try again.
            </div>
        </c:if>

        <div class="progress-bar-container"><div class="progress-bar" id="progressBar"></div></div>
        <form id="activityProposalForm" action="${pageContext.request.contextPath}/ActivityProposalServlet" method="post" enctype="multipart/form-data">
            <input type="hidden" name="clubId" value="${sessionScope.clubId}"/>

            <!-- Step 1 -->
            <div class="form-section active" id="step1">
                <h2 class="form-header">Step 1: Basic Information</h2>
                <div class="form-group"><label for="eventTitle">Activity Title</label><input type="text" id="eventTitle" name="eventTitle" required><span class="error-message">Required.</span></div>
                <div class="form-group"><label for="category">Category</label><select id="category" name="category" required><option value="">Select</option><option value="1">Student Bodies</option><option value="2">Cultural Society</option><option value="3">Community Centric</option><option value="4">Performing Arts</option><option value="5">Sports & Recreation</option><option value="6">Academical Society</option></select><span class="error-message">Required.</span></div>

                <div class="form-row">
                    <div class="form-group" style="flex: 1;">
                        <label for="startDateTime">Start Date & Time</label>
                        <input type="datetime-local" id="startDateTime" name="startDateTime" required>
                        <span class="error-message" id="startDateError">Required.</span>
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="endDateTime">End Date & Time</label>
                        <input type="datetime-local" id="endDateTime" name="endDateTime" required>
                        <span class="error-message" id="endDateError">Required.</span>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group" style="flex: 1;">
                        <label for="venue">Venue</label>
                        <input type="text" id="venue" name="venue" required>
                        <span class="error-message">Required.</span>
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="participantLimit">Participant Limit</label>
                        <input type="number" id="participantLimit" name="participantLimit" min="1" required>
                        <span class="error-message">Must be positive.</span>
                    </div>
                </div>
            </div>

            <!-- Step 2 -->
            <div class="form-section" id="step2">
                <h2 class="form-header">Step 2: Details</h2>

                <div class="form-group">
                    <label for="objectives">Objectives</label>
                    <textarea id="objectives" name="objectives" rows="2" required></textarea>
                    <span class="error-message">Required.</span>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="2" required></textarea>
                    <span class="error-message">Required.</span>
                </div>

                <div class="form-row">
                    <div class="form-group" style="flex: 1;">
                        <label for="targetAudience">Target Audience</label>
                        <input type="text" id="targetAudience" name="targetAudience" required>
                        <span class="error-message">Required.</span>
                    </div>

                    <div class="form-group" style="flex: 1;">
                        <label for="committeeList">Committee List</label>
                        <textarea id="committeeList" name="committeeList" rows="2" placeholder="e.g., Project Manager: John Doe" required></textarea>
                        <span class="error-message">Required.</span>
                    </div>
                </div>

                <div class="form-group">
                    <label for="promotionStrategy">Promotion Strategy</label>
                    <textarea id="promotionStrategy" name="promotionStrategy" rows="2" required></textarea>
                    <span class="error-message">Required.</span>
                </div>
            </div>

            <!-- Step 3 -->
            <div class="form-section" id="step3">
                <h2 class="form-header">Step 3: Budget & Upload</h2>
                <div class="form-group"><label for="poster">Poster (Image)</label><input type="file" id="poster" name="poster" accept="image/*" required><span class="error-message">Required.</span></div>
                <div class="form-group"><label for="programFlow">Program Flow (PDF)</label><input type="file" id="programFlow" name="programFlow" accept=".pdf" required><span class="error-message">Required.</span></div>
                <div class="form-group"><label for="budgetFile">Budget Plan (PDF)</label><input type="file" id="budgetFile" name="budgetFile" accept=".pdf" required><span class="error-message">Required.</span></div>
                <div class="form-group"><label for="totalBudget">Total Budget (RM)</label><input type="number" id="totalBudget" name="totalBudget" min="0" step="0.01" required><span class="error-message">Required.</span></div>
                <div class="form-group"><label for="activityMerit">Merit Points</label><input type="number" id="activityMerit" name="activityMerit" min="0" value="10" required><span class="error-message">Required.</span></div>
            </div>

            <div class="form-navigation">
                <button type="button" id="prevBtn" onclick="nextPrev(-1)">Previous</button>
                <button type="button" id="nextBtn" onclick="nextPrev(1)">Next</button>
                <button type="submit" id="submitBtn" style="display:none;">Submit</button>
            </div>
        </form>
    </div>

    <jsp:include page="/includes/mainFooter.jsp" />
</div>

<script>
    let currentTab = 0;
    showTab(currentTab);

    function showTab(n) {
        const x = document.getElementsByClassName("form-section");
        x[n].style.display = "block";
        x[n].classList.add("active");
        document.getElementById("prevBtn").style.display = n === 0 ? "none" : "inline-block";
        document.getElementById("nextBtn").style.display = n === x.length - 1 ? "none" : "inline-block";
        document.getElementById("submitBtn").style.display = n === x.length - 1 ? "inline-block" : "none";
        updateProgressBar(n, x.length);
    }

    function nextPrev(n) {
        if (n === 1 && !validateForm()) return false;
        const x = document.getElementsByClassName("form-section");
        x[currentTab].classList.remove("active");
        x[currentTab].style.display = "none";
        currentTab += n;
        if (currentTab >= x.length) {
            document.getElementById("activityProposalForm").submit();
            return false;
        }
        showTab(currentTab);
    }

    function validateForm() {
        const x = document.getElementsByClassName("form-section");
        const inputs = x[currentTab].querySelectorAll("input[required], textarea[required], select[required]");
        let valid = true;

        inputs.forEach(input => {
            const group = input.closest(".form-group");
            if (!input.value.trim()) {
                group.classList.add("invalid");
                valid = false;
            } else {
                group.classList.remove("invalid");
            }
        });

        if (x[currentTab].id === 'step1') {
            const start = new Date(document.getElementById("startDateTime").value);
            const end = new Date(document.getElementById("endDateTime").value);
            const now = new Date();
            if (start < now) {
                document.getElementById("startDateError").textContent = "Cannot be in the past.";
                document.getElementById("startDateTime").closest(".form-group").classList.add("invalid");
                valid = false;
            }
            if (end < start) {
                document.getElementById("endDateError").textContent = "End must be after start.";
                document.getElementById("endDateTime").closest(".form-group").classList.add("invalid");
                valid = false;
            }
        }

        return valid;
    }

    function updateProgressBar(n, total) {
        document.getElementById("progressBar").style.width = ((n + 1) / total) * 100 + "%";
    }
</script>
</body>
</html>
