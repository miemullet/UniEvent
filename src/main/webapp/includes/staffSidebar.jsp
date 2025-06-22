<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    /* This styling is identical to the other sidebars to maintain consistency */
    .sidebar .nav-links {
        flex-grow: 1; 
        display: flex;
        flex-direction: column;
    }
    .sidebar .logout-link {
        margin-top: auto;
        padding-bottom: 20px;
    }
    .sidebar .logout-link a {
        color: #dc3545;
    }
    .sidebar .logout-link a:hover {
        background-color: #f8d7da;
    }
    
    .sidebar {
      position: fixed;
      top: 0;
      left: 0;
      width: 220px;
      height: 100vh;
      background: #ffffff;
      border-right: 1px solid #ccc;
      padding-top: 20px;
      z-index: 1000;
      transition: transform 0.3s ease, width 0.3s ease; /* Added width transition */
    }
    
    .sidebar.collapsed {
        transform: translateX(-100%); /* This will hide the sidebar */
    }
    
    .sidebar.collapsed .logo-container img.logo,
    .sidebar.collapsed .nav-links a span {
        display: none;
    }
    
    .sidebar.collapsed .nav-links a {
      justify-content: center;
    }
    
    .sidebar.collapsed .nav-links img {
        margin-right: 0;
    }

    .sidebar .logo {
      width: 120px;
      display: block;
      margin: 0 auto 20px;
    }
    
    .sidebar .nav-links {
        list-style: none;
        padding: 0;
        margin: 0;
        flex-grow: 1; 
        display: flex;
        flex-direction: column;
    }

    .sidebar .nav-links a {
        display: flex;
        align-items: center;
        padding: 14px 20px;
        text-decoration: none;
        color: #6b7280;
        font-weight: 500;
        white-space: nowrap; /* Prevent text from wrapping */
    }

    .sidebar .nav-links li.active a {
        background-color: #6c4eff;
        color: #ffffff;
        border-radius: 8px;
        margin: 0 10px;
    }

    .sidebar .nav-links img {
        width: 20px;
        height: 20px;
        margin-right: 15px;
        flex-shrink: 0;
    }
</style>

<div class="sidebar" id="sidebar">
    <!-- Logo -->
    <div class="logo-container">
        <img src="${pageContext.request.contextPath}/images/logo unievt.png" alt="Logo" class="logo">
    </div>

    <!-- Navigation Menu -->
    <div class="nav-wrapper">
        <ul class="nav-links">
            <li class="${pageTitle == 'Staff Dashboard' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/staff/dashboard">
                    <img src="${pageContext.request.contextPath}/images/dashboard-icon.png" alt="Dashboard" />
                    <span>Dashboard</span>
                </a>
            </li>
            <li class="${pageTitle == 'Activities' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/staff/activities">
                    <img src="${pageContext.request.contextPath}/images/event-icon.png" alt="Activities" />
                    <span>Activities</span>
                </a>
            </li>
            <li class="${pageTitle == 'Reports' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/staff/reports">
                    <img src="${pageContext.request.contextPath}/images/report-icon.png" alt="Reports" />
                    <span>Reports</span>
                </a>
            </li>
            <li class="${pageTitle == 'Feedback' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/staff/feedback">
                    <img src="${pageContext.request.contextPath}/images/feedback-icon.png" alt="Feedback" />
                    <span>Feedback</span>
                </a>
            </li>
            <li class="${pageTitle == 'Staff Account' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/staff/account">
                    <img src="${pageContext.request.contextPath}/images/account-icon.png" alt="Account" />
                    <span>Account</span>
                </a>
            </li>
        </ul>
    </div>

    <!-- Logout button fixed to bottom -->
    <ul class="nav-links logout-wrapper">
        <li class="logout-link">
            <a href="${pageContext.request.contextPath}/logout.jsp">
                <img src="${pageContext.request.contextPath}/images/logout-icon.png" alt="Logout" />
                <span>Logout</span>
            </a>
        </li>
    </ul>
</div>

<script>
    // This script runs after the page loads and REPLACES the faulty toggleSidebar function from the header.
    document.addEventListener('DOMContentLoaded', function () {
        // Redefine the global toggleSidebar function with the correct logic.
        window.toggleSidebar = function() {
            const sidebar = document.getElementById("sidebar");
            const topbar = document.getElementById("topbar");
            const subHeader = document.getElementById("subHeader");
            const mainContent = document.querySelector(".main-content") || document.querySelector(".main-content1");
            const footer = document.querySelector(".page-footer");

            sidebar.classList.toggle("collapsed");

            // Logic to HIDE the sidebar and expand content.
            if (sidebar.classList.contains("collapsed")) {
                sidebar.style.transform = "translateX(-100%)";
                
                if(topbar) {
                    topbar.style.left = "0";
                    topbar.style.width = "100%";
                }
                if(subHeader){
                    subHeader.style.left = "0";
                    subHeader.style.width = "100%";
                }
                if(mainContent) mainContent.style.marginLeft = "0";
                if(footer) footer.style.marginLeft = "0";

            } else {
                const expandedWidth = "220px";
                sidebar.style.transform = "translateX(0)";
                
                if(topbar) {
                    topbar.style.left = expandedWidth;
                    topbar.style.width = `calc(100% - ${expandedWidth})`;
                }
                if(subHeader){
                    subHeader.style.left = expandedWidth;
                    subHeader.style.width = `calc(100% - ${expandedWidth})`;
                }
                if(mainContent) mainContent.style.marginLeft = expandedWidth;
                if(footer) footer.style.marginLeft = expandedWidth;
            }
        };
    });
</script>
