<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .sidebar {
        position: fixed;
        top: 0;
        left: 0;
        width: 220px;
        height: 100vh;
        background: #fff;
        border-right: 1px solid #e0e0e0;
        z-index: 1000;
        display: flex;
        flex-direction: column;
        padding-top: 20px;
    }

    .logo-container {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
    }

    .logo {
        width: 140px;
        height: auto;
    }

    .nav-wrapper {
        flex-grow: 1;
    }

    .nav-links {
        list-style: none;
        padding-left: 0;
        margin: 0;
    }

    .nav-links li {
        display: flex;
        align-items: center;
    }

    .nav-links li a {
        display: flex;
        align-items: center;
        padding: 12px 20px;
        color: #333;
        text-decoration: none;
        width: 100%;
        gap: 10px;
        font-weight: 500;
    }

    .nav-links li.active a,
    .nav-links li a:hover {
        background-color: #f2f2f2;
        font-weight: 600;
    }

    .nav-links img {
        width: 20px;
        height: 20px;
    }

    .logout-wrapper {
        padding-bottom: 20px;
    }

    .logout-link a {
        color: #dc3545;
        font-weight: 600;
    }

    .logout-link a:hover {
        background-color: #f8d7da;
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
