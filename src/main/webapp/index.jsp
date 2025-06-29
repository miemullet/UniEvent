<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>UniEvent Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <style>
        .hero-banner {
            position: relative;
            width: 100%;
            height: 500px;
            overflow: hidden;
        }

        .slide-image {
            position: absolute;
            width: 100%;
            height: 100%;
            object-fit: cover;
            opacity: 0;
            transform: scale(1.05);
            transition: opacity 1s ease-in-out, transform 1s ease-in-out;
        }

        .slide-image.active {
            opacity: 1;
            transform: scale(1);
        }

        .hero-text {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            text-align: center;
            color: white;
            font-size: 2em;
            font-family: Arial, sans-serif;
            text-shadow: 2px 2px 4px #000;
            z-index: 2;
        }

        .hero-text span {
            color: #FFD700;
        }

        /* Arrows */
        .arrow {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            font-size: 2rem;
            color: white;
            background-color: rgba(0,0,0,0.5);
            padding: 10px;
            cursor: pointer;
            z-index: 3;
        }

        .arrow.left {
            left: 20px;
        }

        .arrow.right {
            right: 20px;
        }

        /* Dots */
        .dots {
            position: absolute;
            bottom: 15px;
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            gap: 10px;
            z-index: 3;
        }

        .dot {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background-color: rgba(255,255,255,0.6);
            cursor: pointer;
        }

        .dot.active {
            background-color: #FFD700;
        }
    </style>
</head>
<body class="home-page">
<header>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
        <a href="${pageContext.request.contextPath}/signup.jsp">Sign Up</a>
    </nav>
</header>

<section class="logo-container">
    <img src="${pageContext.request.contextPath}/images/logo.png" alt="UniEvent Logo" class="left-logo">
    <img src="${pageContext.request.contextPath}/images/uitm.png" alt="UiTM Logo" class="left-logo">
</section>

<main class="hero-banner">
    <img src="${pageContext.request.contextPath}/images/banner1.png" class="slide-image active" alt="Banner 1">
    <img src="${pageContext.request.contextPath}/images/banner2.png" class="slide-image" alt="Banner 2">
    <img src="${pageContext.request.contextPath}/images/banner3.png" class="slide-image" alt="Banner 3">
    <img src="${pageContext.request.contextPath}/images/banner4.png" class="slide-image" alt="Banner 4">

    <div class="arrow left" onclick="changeSlide(-1)">&#10094;</div>
    <div class="arrow right" onclick="changeSlide(1)">&#10095;</div>

    <div class="dots" id="dots"></div>

</main>

<script>
    const slides = document.querySelectorAll('.slide-image');
    const dotsContainer = document.getElementById('dots');
    let current = 0;
    let slideInterval;

    // Generate dots
    slides.forEach((_, index) => {
        const dot = document.createElement('div');
        dot.classList.add('dot');
        if (index === 0) dot.classList.add('active');
        dot.addEventListener('click', () => goToSlide(index));
        dotsContainer.appendChild(dot);
    });

    const dots = document.querySelectorAll('.dot');

    function showSlide(index) {
        slides.forEach((slide, i) => {
            slide.classList.toggle('active', i === index);
            dots[i].classList.toggle('active', i === index);
        });
    }

    function changeSlide(step) {
        current = (current + step + slides.length) % slides.length;
        showSlide(current);
        resetTimer();
    }

    function goToSlide(index) {
        current = index;
        showSlide(current);
        resetTimer();
    }

    function autoSlide() {
        current = (current + 1) % slides.length;
        showSlide(current);
    }

    function resetTimer() {
        clearInterval(slideInterval);
        slideInterval = setInterval(autoSlide, 5000);
    }

    slideInterval = setInterval(autoSlide, 5000);
</script>
</body>
</html>
