<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">

<style>
.topbar {
    background-color: #0f60b6;
    color: white;
    padding: 0 30px;
    height: 60px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: fixed;
    top: 0;
    left: 220px;
    right: 0px;
    width: calc(100% - 280px);
    z-index: 1001;
    transition: left 0.3s, width 0.3s;
}

.sub-header {
    position: fixed;
    top: 60px; /* Directly below the topbar */
    left: 220px;
    width: calc(100% - 280px);
    background-color: #fcd94c;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 30px;
    font-size: 14px;
    font-weight: 500;
    z-index: 1000;
    transition: left 0.3s, width 0.3s;
}
</style>

<div class="topbar" id="topbar">
    <button class="menu-toggle" onclick="toggleSidebar()">☰</button>
    <div class="topbar-inner">
        <div class="user-info">
            <div class="user-box">
                <%-- Display name based on role --%>
                <c:choose>
                    <c:when test="${sessionScope.role == 'Club Organizer'}">
                        ${sessionScope.studentName}<br><small>${sessionScope.clubName}</small>
                    </c:when>
                    <c:when test="${sessionScope.role == 'Student'}">
                        ${sessionScope.studentName}<br><small>${sessionScope.username}</small>
                    </c:when>
                    <c:when test="${sessionScope.role == 'Admin/Staff'}">
                        ${sessionScope.staffName}<br><small>${sessionScope.username}</small>
                    </c:when>
                </c:choose>
            </div>

            <%-- Set image path dynamically --%>
            <c:set var="imagePath" value="images/user.jpg" />
            <c:if test="${(sessionScope.role == 'Student' or sessionScope.role == 'Club Organizer') and not empty sessionScope.studentImagePath}">
                <c:set var="imagePath" value="${sessionScope.studentImagePath}" />
            </c:if>
            <c:if test="${sessionScope.role == 'Admin/Staff' and not empty sessionScope.staffImagePath}">
                <c:set var="imagePath" value="${sessionScope.staffImagePath}" />
            </c:if>

            <img src="${pageContext.request.contextPath}/${imagePath}" 
                 alt="User" class="profile-pic"
                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/user.jpg';">
        </div>
    </div>
</div>

<div class="sub-header" id="subHeader">
    <div>${pageTitle}</div>
    <div>Home &gt; <strong>${pageTitle}</strong></div>
</div>

<script>
function toggleSidebar() {
  const sidebar = document.getElementById("sidebar");
  const topbar = document.getElementById("topbar");
  const subHeader = document.getElementById("subHeader");
  const mainContent = document.querySelector(".main-content");
  const mainContent1 = document.querySelector(".main-content1");

  sidebar.classList.toggle("collapsed");

  if (sidebar.classList.contains("collapsed")) {
    sidebar.style.transform = "translateX(-100%)";
    topbar.style.left = "0";
    topbar.style.width = "100%";
    subHeader.style.left = "0";
    subHeader.style.width = "100%";

    if (mainContent) mainContent.style.marginLeft = "0";
    if (mainContent1) mainContent1.style.marginLeft = "0";
  } else {
    sidebar.style.transform = "translateX(0)";
    topbar.style.left = "220px";
    topbar.style.width = "calc(100% - 220px)";
    subHeader.style.left = "220px";
    subHeader.style.width = "calc(100% - 220px)";

    if (mainContent) mainContent.style.marginLeft = "220px";
    if (mainContent1) mainContent1.style.marginLeft = "220px";
  }
}
</script>
