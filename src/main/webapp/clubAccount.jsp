<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Club Account - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .account-container { background-color: #fff; border-radius: 12px; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08); overflow: hidden; }
        .account-banner { width: 100%; height: 250px; background: linear-gradient(to right, #6b46f2, #8b5cf6); background-size: cover; background-position: center; display: flex; align-items: center; justify-content: center; color: #fff; text-align: center; }
        .logo-container { position: relative; }
        .club-logo { width: 100px; height: 100px; border-radius: 50%; border: 4px solid #fff; margin-bottom: 10px; object-fit: cover; }
        .upload-overlay { position: absolute; bottom: 10px; right: 0; background: rgba(0,0,0,0.6); color: white; border-radius: 50%; padding: 8px; cursor: pointer; opacity: 0; transition: opacity 0.2s; }
        .logo-container:hover .upload-overlay { opacity: 1; }
        .account-content { padding: 30px; }
        .account-content h2 { font-size: 24px; font-weight: 700; color: #333; margin-bottom: 20px; }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .form-group { margin-bottom: 15px; grid-column: span 2; }
        .form-group.half { grid-column: span 1; }
        .form-group label { display: block; font-weight: 600; color: #555; margin-bottom: 5px; }
        .form-group input, .form-group textarea, .form-group select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; font-family: 'Poppins', sans-serif; }
        .form-group input[readonly], .form-group textarea[readonly], .form-group select[disabled] { background-color: #f8f9fa; cursor: not-allowed; }
        .action-buttons { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
        .action-buttons button { padding: 10px 20px; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; }
        .edit-btn, .save-btn { background-color: #6b46f2; color: #fff; }
        .cancel-btn { background-color: #6c757d; color: #fff; }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="Club Account" scope="request"/>
    <jsp:include page="/includes/clubSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />

        <div class="account-container">
            <c:choose>
                <c:when test="${not empty club}">
                    <div class="account-banner">
                        <div>
                            <form action="${pageContext.request.contextPath}/ImageUploadServlet" method="post" enctype="multipart/form-data" id="clubIconForm">
                                <input type="hidden" name="uploadType" value="clubIcon">
                                <div class="logo-container">
                                    <label for="clubIconFile">
                                        <img src="${pageContext.request.contextPath}/${not empty club.logo_path ? club.logo_path : 'images/default_club_logo.png'}" 
                                             alt="Club Logo" class="club-logo" id="logoPreview"
                                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/default_club_logo.png';">
                                        <span class="upload-overlay">✏️</span>
                                    </label>
                                    <input type="file" name="imageFile" id="clubIconFile" style="display: none;" accept="image/*">
                                </div>
                            </form>
                             <h1><c:out value="${club.club_name}"/></h1>
                        </div>
                    </div>
                    <div class="account-content">
                        <h2>Club Details</h2>
                        <form id="clubDetailsForm" action="${pageContext.request.contextPath}/club/account" method="POST">
                            <input type="hidden" name="action" value="updateClub">
                            <div class="form-grid">
                                <div class="form-group half">
                                    <label for="clubName">Club Name</label>
                                    <input type="text" id="clubName" name="clubName" value="${club.club_name}" readonly required>
                                </div>
                                <div class="form-group half">
                                    <label for="clubCategory">Club Category</label>
                                    <select id="clubCategory" name="clubCategory" disabled required>
                                        <option value="Student Bodies" ${club.club_category == 'Student Bodies' ? 'selected' : ''}>Student Bodies</option>
                                        <option value="Cultural Society" ${club.club_category == 'Cultural Society' ? 'selected' : ''}>Cultural Society</option>
                                        <option value="Community Centric" ${club.club_category == 'Community Centric' ? 'selected' : ''}>Community Centric</option>
                                        <option value="Performing Arts" ${club.club_category == 'Performing Arts' ? 'selected' : ''}>Performing Arts</option>
                                        <option value="Sports & Recreation" ${club.club_category == 'Sports & Recreation' ? 'selected' : ''}>Sports & Recreation</option>
                                        <option value="Academical Society" ${club.club_category == 'Academical Society' ? 'selected' : ''}>Academical Society</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="clubDesc">Club Description</label>
                                    <textarea id="clubDesc" name="clubDesc" rows="4" readonly required>${club.club_desc}</textarea>
                                </div>
                                <div class="form-group half">
                                    <label for="clubPresidentID">President ID</label>
                                    <input type="text" id="clubPresidentID" name="clubPresidentID" value="${club.club_presidentID}" readonly>
                                </div>
                            </div>

                            <div class="action-buttons">
                                <button type="button" id="editBtn" class="edit-btn">Edit Details</button>
                                <button type="submit" id="saveBtn" class="save-btn" style="display:none;">Save Changes</button>
                                <button type="button" id="cancelBtn" class="cancel-btn" style="display:none;">Cancel</button>
                            </div>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="account-content" style="text-align: center;"><h2>Club Information Not Found</h2></div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <jsp:include page="/includes/mainFooter.jsp" />
    </div>
    <script>
        document.getElementById('clubIconFile').addEventListener('change', function(event) {
            const [file] = event.target.files;
            if (file) {
                document.getElementById('logoPreview').src = URL.createObjectURL(file);
                document.getElementById('clubIconForm').submit();
            }
        });

        document.addEventListener('DOMContentLoaded', () => {
            const editBtn = document.getElementById('editBtn');
            const saveBtn = document.getElementById('saveBtn');
            const cancelBtn = document.getElementById('cancelBtn');
            const editableInputs = document.querySelectorAll('#clubName, #clubDesc');
            const categorySelect = document.getElementById('clubCategory');
            
            let originalValues = {};

            editBtn.addEventListener('click', () => {
                editableInputs.forEach(input => {
                    originalValues[input.id] = input.value;
                    input.readOnly = false;
                    input.style.backgroundColor = '#fff';
                });
                originalValues[categorySelect.id] = categorySelect.value;
                categorySelect.disabled = false;
                categorySelect.style.backgroundColor = '#fff';

                editBtn.style.display = 'none';
                saveBtn.style.display = 'inline-block';
                cancelBtn.style.display = 'inline-block';
            });

            cancelBtn.addEventListener('click', () => {
                editableInputs.forEach(input => {
                    input.value = originalValues[input.id];
                    input.readOnly = true;
                    input.style.backgroundColor = '';
                });
                categorySelect.value = originalValues[categorySelect.id];
                categorySelect.disabled = true;
                categorySelect.style.backgroundColor = '';

                editBtn.style.display = 'inline-block';
                saveBtn.style.display = 'none';
                cancelBtn.style.display = 'none';
            });
        });
    </script>
</body>
</html>
