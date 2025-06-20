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
        .proposal-form-container{background-color:#fff;padding:30px;border-radius:12px;box-shadow:0 5px 15px rgba(0,0,0,.1);width:100%;max-width:800px;margin:20px auto}.form-section{display:none}.form-section.active{display:block}.form-header{font-size:1.8em;color:#333;margin-bottom:25px;text-align:center;font-weight:600}.form-navigation{display:flex;justify-content:space-between;margin-top:30px}.form-navigation button{background-color:#6b46f2;color:#fff;padding:12px 25px;border:none;border-radius:8px;cursor:pointer;font-size:1.1em;font-weight:600}.form-navigation button:disabled{background-color:#ccc}.progress-bar-container{width:100%;background-color:#e0e0e0;border-radius:5px;margin-bottom:30px}.progress-bar{width:0%;height:10px;background-color:#6b46f2;border-radius:5px;transition:width .4s ease-in-out}.error-message{color:#dc3545;font-size:.9em;display:none}.form-group.invalid .error-message{display:block}.form-group.invalid input,.form-group.invalid textarea,.form-group.invalid select{border-color:#dc3545}
        .server-error-popup { padding: 15px; margin-bottom: 20px; border-radius: 8px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; font-weight: 500; }
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
                
                <!-- Step 1: Basic Information -->
                <div class="form-section active" id="step1">
                    <h2 class="form-header">Step 1: Basic Information</h2>
                    <div class="form-group"><label for="eventTitle">Activity Title</label><input type="text" id="eventTitle" name="eventTitle" required><span class="error-message">Activity Title is required.</span></div>
                    <div class="form-group"><label for="category">Category</label><select id="category" name="category" required><option value="">Select Category</option><option value="1">Student Bodies</option><option value="2">Cultural Society</option><option value="3">Community Centric</option><option value="4">Performing Arts</option><option value="5">Sports & Recreation</option><option value="6">Academical Society</option></select><span class="error-message">Category is required.</span></div>
                    <div class="form-group">
                        <label for="startDateTime">Start Date & Time</label>
                        <input type="datetime-local" id="startDateTime" name="startDateTime" required>
                        <span class="error-message" id="startDateError">Start Date is required.</span>
                    </div>
                    <div class="form-group">
                        <label for="endDateTime">End Date & Time</label>
                        <input type="datetime-local" id="endDateTime" name="endDateTime" required>
                        <span class="error-message" id="endDateError">End Date is required.</span>
                    </div>
                    <div class="form-group"><label for="venue">Venue</label><input type="text" id="venue" name="venue" required><span class="error-message">Venue is required.</span></div>
                    <div class="form-group"><label for="participantLimit">Participant Limit</label><input type="number" id="participantLimit" name="participantLimit" min="1" required><span class="error-message">Limit must be a positive number.</span></div>
                </div>

                <!-- Step 2: Detailed Information -->
                <div class="form-section" id="step2">
                    <h2 class="form-header">Step 2: Detailed Information</h2>
                    <div class="form-group"><label for="objectives">Objectives</label><textarea id="objectives" name="objectives" rows="4" required></textarea><span class="error-message">Objectives are required.</span></div>
                    <div class="form-group"><label for="description">Activity Description</label><textarea id="description" name="description" rows="4" required></textarea><span class="error-message">Description is required.</span></div>
                    <div class="form-group"><label for="targetAudience">Target Audience</label><input type="text" id="targetAudience" name="targetAudience" required><span class="error-message">Target Audience is required.</span></div>
                    <div class="form-group"><label for="committeeList">Committee Details (one per line)</label><textarea id="committeeList" name="committeeList" rows="5" placeholder="e.g., Project Manager: John Doe (12345)" required></textarea><span class="error-message">Committee Details are required.</span></div>
                    <div class="form-group"><label for="promotionStrategy">Promotion Strategy</label><textarea id="promotionStrategy" name="promotionStrategy" rows="4" required></textarea><span class="error-message">Promotion Strategy is required.</span></div>
                </div>

                <!-- Step 3: Attachments, Budget & Merit -->
                <div class="form-section" id="step3">
                    <h2 class="form-header">Step 3: Attachments, Budget & Merit</h2>
                    <div class="form-group"><label for="poster">Event Poster (Image)</label><input type="file" id="poster" name="poster" accept="image/*" required><span class="error-message">Poster is required.</span></div>
                    <div class="form-group"><label for="programFlow">Program Flow (PDF)</label><input type="file" id="programFlow" name="programFlow" accept=".pdf" required><span class="error-message">Program Flow is required.</span></div>
                    <div class="form-group"><label for="budgetFile">Budget Plan (PDF)</label><input type="file" id="budgetFile" name="budgetFile" accept=".pdf" required><span class="error-message">Budget Plan is required.</span></div>
                    <div class="form-group"><label for="totalBudget">Total Estimated Budget (RM)</label><input type="number" id="totalBudget" name="totalBudget" min="0" step="0.01" required><span class="error-message">Budget is required.</span></div>
                    
                    <div class="form-group">
                        <label for="activityMerit">Merit Points Awarded Upon Registration</label>
                        <input type="number" id="activityMerit" name="activityMerit" min="0" value="10" required>
                        <span class="error-message">Merit points are required (can be 0).</span>
                    </div>
                </div>
                
                <!-- Navigation -->
                <div class="form-navigation">
                    <button type="button" id="prevBtn" onclick="nextPrev(-1)">Previous</button>
                    <button type="button" id="nextBtn" onclick="nextPrev(1)">Next</button>
                    <button type="submit" id="submitBtn" style="display:none;">Submit Proposal</button>
                </div>
            </form>
        </div>
        <jsp:include page="/includes/mainFooter.jsp" />
    </div>
    <script>
        let currentTab = 0;
        showTab(currentTab);

        function showTab(n) {
            let x = document.getElementsByClassName("form-section");
            x[n].style.display = "block";
            x[n].classList.add("active");
            document.getElementById("prevBtn").style.display = n == 0 ? "none" : "inline-block";
            document.getElementById("nextBtn").style.display = n === x.length - 1 ? "none" : "inline-block";
            document.getElementById("submitBtn").style.display = n === x.length - 1 ? "inline-block" : "none";
            updateProgressBar(n, x.length);
        }

        function nextPrev(n) {
            if (n === 1 && !validateForm()) return false;
            let x = document.getElementsByClassName("form-section");
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
            let x, y, i, valid = true;
            x = document.getElementsByClassName("form-section");
            y = x[currentTab].querySelectorAll("input[required], textarea[required], select[required]");
            
            for (i = 0; i < y.length; i++) {
                const formGroup = y[i].closest(".form-group");
                if (y[i].value.trim() === "") {
                    formGroup.classList.add("invalid");
                    valid = false;
                } else {
                    formGroup.classList.remove("invalid");
                }
            }

            // --- NEW: DATE VALIDATION LOGIC ---
            if (x[currentTab].id === 'step1') {
                const startDateInput = document.getElementById("startDateTime");
                const endDateInput = document.getElementById("endDateTime");
                const startDateError = document.getElementById("startDateError");
                const endDateError = document.getElementById("endDateError");
                
                const startDate = new Date(startDateInput.value);
                const endDate = new Date(endDateInput.value);
                const now = new Date();

                // Reset error messages and styles
                startDateError.textContent = "Start Date is required.";
                startDateInput.closest(".form-group").classList.remove("invalid");
                endDateError.textContent = "End Date is required.";
                endDateInput.closest(".form-group").classList.remove("invalid");

                // 1. Check if start date is in the past
                if (startDate < now) {
                    startDateError.textContent = "Start date cannot be in the past.";
                    startDateInput.closest(".form-group").classList.add("invalid");
                    valid = false;
                }

                // 2. Check if end date is before start date
                if (endDate < startDate) {
                    endDateError.textContent = "End date cannot be before the start date.";
                    endDateInput.closest(".form-group").classList.add("invalid");
                    valid = false;
                }
            }
            // --- END OF DATE VALIDATION ---

            return valid;
        }

        function updateProgressBar(n, total) {
            const progressBar = document.getElementById("progressBar");
            progressBar.style.width = ((n + 1) / total) * 100 + "%";
        }
    </script>
</body>
</html>
