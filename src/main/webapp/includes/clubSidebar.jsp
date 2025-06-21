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
      transition: transform 0.3s ease;
    }
    
    .sidebar.hidden {
  transform: translateX(-100%);
}

    .sidebar .logo {
      width: 120px;
      display: block;
      margin: 0 auto 20px;
    }
    
    .sidebar.collapsed {
  width: 60px;
}

.sidebar.collapsed .logo {
  width: 40px;
  margin: 0 auto;
}

.sidebar.collapsed .nav-links a span,
.sidebar.collapsed .nav-links a img {
  display: none;
}

.sidebar.collapsed .nav-links a {
  justify-content: center;
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
}

    .sidebar.collapsed ~ .page-footer {
      margin-left: 60px;
    }
</style>

<div class="sidebar" id="sidebar">
    <div class="logo-container">
        <img src="${pageContext.request.contextPath}/images/logo unievt.png" alt="Logo" class="logo" />
    </div>
    <ul class="nav-links">
        <li class="${pageTitle == 'Club Dashboard' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/dashboard">
                <img src="${pageContext.request.contextPath}/images/dashboard-icon.png" alt="Dashboard" />
                <span>Dashboard</span>
            </a>
        </li>
        <li class="${pageTitle == 'My Activities' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/activities">
                <img src="${pageContext.request.contextPath}/images/activity-icon.png" alt="Activities" />
                <span>Activities</span>
            </a>
        </li>
        <li class="${pageTitle == 'Propose New Activity' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/activityProposal">
                <img src="${pageContext.request.contextPath}/images/activitypurpose-icon.png" alt="Propose Activity" />
                <span>Propose Activity</span>
            </a>
        </li>
        <li class="${pageTitle == 'Club Members' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/members">
                <img src="${pageContext.request.contextPath}/images/members-icon.png" alt="Members" />
                <span>Members</span>
            </a>
        </li>
        <li class="${pageTitle == 'Club Feedback' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/feedback">
                <img src="${pageContext.request.contextPath}/images/feedback-icon.png" alt="Feedback" />
                <span>Feedback</span>
            </a>
        </li>
        <li class="${pageTitle == 'About Club' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/about">
                <img src="${pageContext.request.contextPath}/images/info-icon.png" alt="About Club" />
                <span>About Club</span>
            </a>
        </li>
        <li class="${pageTitle == 'Past Events' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/pastEvents">
                <img src="${pageContext.request.contextPath}/images/pastevent-icon.png" alt="Past Events" />
                <span>Past Events</span>
            </a>
        </li>
        <li class="${pageTitle == 'Club Account' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/club/account">
                <img src="${pageContext.request.contextPath}/images/account-icon.png" alt="Account" />
                <span>Account</span>
            </a>
        </li>

        <!-- === NEW LOGOUT LINK === -->
        <li class="logout-link">
             <a href="${pageContext.request.contextPath}/logout.jsp">
                 <img src="${pageContext.request.contextPath}/images/logout-icon.png" alt="Logout" />
                 <span>Logout</span>
             </a>
         </li>
    </ul>
</div>
