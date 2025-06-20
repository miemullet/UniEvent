<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>About Our Club - UniEvent</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .about-container { background-color: #fff; border-radius: 12px; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08); overflow: hidden; }
        .about-banner { width: 100%; height: 250px; background: linear-gradient(to right, #6b46f2, #8b5cf6); background-size: cover; background-position: center; display: flex; align-items: center; justify-content: center; color: #fff; text-align: center; }
        .logo-container { position: relative; }
        .club-logo { width: 100px; height: 100px; border-radius: 50%; border: 4px solid #fff; margin-bottom: 10px; object-fit: cover; }
        .upload-overlay { position: absolute; bottom: 10px; right: 0; background: rgba(0,0,0,0.6); color: white; border-radius: 50%; padding: 8px; cursor: pointer; opacity: 0; transition: opacity 0.2s; }
        .logo-container:hover .upload-overlay { opacity: 1; }
        .about-content { padding: 30px; }
        .about-content h2 { font-size: 28px; font-weight: 700; color: #333; margin-bottom: 10px; }
        .category-tag { display: inline-block; background-color: #e0e7ff; color: #4f46e5; padding: 5px 15px; border-radius: 20px; font-weight: 600; font-size: 14px; margin-bottom: 20px; }
        .president-info { background-color: #f9fafb; padding: 15px; border-radius: 8px; border-left: 4px solid #6b46f2; }
        .president-info p { margin: 0; font-size: 15px; color: #444; }
    </style>
</head>
<body class="dashboard-page">
    <c:set var="pageTitle" value="About Club" scope="request"/>
    <jsp:include page="/includes/clubSidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/includes/mainHeader.jsp" />

        <div class="about-container">
            <c:choose>
                <c:when test="${not empty club}">
                    <div class="about-banner">
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
                    <div class="about-content">
                        <div class="category-tag"><c:out value="${club.club_category}"/></div>
                        <h2>About Us</h2>
                        <p><c:out value="${club.club_desc}"/></p>

                        <div class="president-info">
                            <p><strong>Club President ID:</strong> <c:out value="${club.club_presidentID}"/></p>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="about-content" style="text-align: center;"><h2>Club Information Not Found</h2></div>
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
    </script>
</body>
</html>
