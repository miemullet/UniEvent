<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - UniEvent</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        html, body {
        height: 100%;
        margin: 0;
        overflow: hidden;
    }

    .main-content {
        height: calc(100vh - 80px);
        overflow-y: auto;
        padding: 10px 15px;
    }
        .signup-logo {
            width: 150px;
            margin-bottom: 0px;
        }
        
        .form-title {
            font-size: 32px;
            margin-bottom: 10px;
            color: #333;
            margin-top: 10px;
        }

        .form-page {
            display: flex;
            flex-direction: column;
            height: 100vh;
        }

        .form-page .main-content {
            display: flex;
            flex-grow: 1;
            margin: 0;
            padding: 0;
        }

        .left-panel {
            width: 50%;
            padding: 10px 80px;
            background-color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            box-sizing: border-box;
        }

        .right-panel {
            width: 50%;
            background-image: url('images/bg.jpg');
            background-size: cover;
            background-position: center;
        }

        .form-logo, .signup-logo {
            width: 150px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-row {
            display: flex;
            gap: 20px;
        }

        .form-row .form-group {
            flex: 1;
        }

        .radio-group {
            margin-bottom: 16px;
        }

        .blue-button {
            width: 100%;
            padding: 10px;
            background-color: #003366;
            color: white;
            font-weight: bold;
            border: none;
            border-radius: 5px;
        }

        .agreement-label {
            font-size: 14px;
        }

        .role-specific-fields {
            display: none;
        }

        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="password"],
        textarea {
            width: 100%;
            padding: 8px 12px;
            font-size: 14px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }

        label {
            font-size: 14px;
            font-weight: 500;
        }
    </style>
</head>
<body class="form-page">
    <header>
        <nav>
            <a href="index.jsp">Home</a>
            <a href="login.jsp">Login</a>
            <a href="signup.jsp">Sign Up</a>
        </nav>
    </header>
    <div class="main-content">
        <div class="left-panel">
            <div class="logo-container">
                <img src="${pageContext.request.contextPath}/images/logo unievt.png" alt="UniEvent Logo" class="signup-logo">
            </div>
            <h2 class="form-title">Get Started Now</h2>
            <form action="RegisterServlet" method="post">
                <div class="radio-group" onchange="updateFormFields()">
                    <label><input type="radio" name="role" value="Student" checked> Student</label>
                    <label><input type="radio" name="role" value="Club Organizer"> Club Organizer</label>
                    <label><input type="radio" name="role" value="Admin/Staff"> Admin/Staff</label>
                </div>

                <div class="form-group"><label>Full Name</label><input type="text" name="name" required></div>

                <div class="form-row">
                    <div class="form-group"><label>Email Address</label><input type="email" name="email" required></div>
                    <div class="form-group"><label>Phone Number</label><input type="tel" name="phone" required></div>
                </div>

                <div class="form-group"><label>Password</label><input type="password" name="password" required></div>

                <div id="student-organizer-fields" class="role-specific-fields">
                    <div class="form-group"><label>Student ID</label><input type="text" name="studentId"></div>
                    <div class="form-row">
                        <div class="form-group"><label>Course</label><input type="text" name="course"></div>
                        <div class="form-group"><label>Faculty</label><input type="text" name="faculty"></div>
                    </div>
                </div>

                <div id="club-organizer-only-fields" class="role-specific-fields">
                    <div class="form-row">
                        <div class="form-group"><label>New Club Name</label><input type="text" name="clubName"></div>
                        <div class="form-group"><label>Club Category</label><input type="text" name="clubCategory"></div>
                    </div>
                    <div class="form-group"><label>Club Description</label><textarea name="clubDesc" rows="2"></textarea></div>
                </div>

                <div id="staff-fields" class="role-specific-fields">
                    <div class="form-row">
                        <div class="form-group"><label>Staff ID</label><input type="text" name="staffId"></div>
                        <div class="form-group"><label>Staff Role</label><input type="text" name="staffRole" placeholder="e.g., Admin"></div>
                    </div>
                </div>

                <div class="form-group agreement">
                    <label class="agreement-label">
                        <input type="checkbox" name="terms" required>
                        <span>I agree to the <a href="#">terms & policy</a></span>
                    </label>
                </div>

                <button type="submit" class="blue-button">Signup</button>
            </form>
            <p class="signup-text">Already have an account? <a href="login.jsp">Log In</a></p>
        </div>
        <div class="right-panel"></div>
    </div>

    <script>
        function updateFormFields() {
            const role = document.querySelector('input[name="role"]:checked').value;
            document.querySelectorAll('.role-specific-fields').forEach(el => el.style.display = 'none');
            if (role === 'Student' || role === 'Club Organizer') {
                document.getElementById('student-organizer-fields').style.display = 'block';
            }
            if (role === 'Club Organizer') {
                document.getElementById('club-organizer-only-fields').style.display = 'block';
            }
            if (role === 'Admin/Staff') {
                document.getElementById('staff-fields').style.display = 'block';
            }
        }
        document.addEventListener('DOMContentLoaded', updateFormFields);
    </script>
</body>
</html>
