<head>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/index.css">
    <%-- <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
    <script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script> --%>
    <title>Sign up-ZOHO Social</title>
</head>
<body>
<div class="bg">
    <div class="h-color">
        <div class="title">
            <img src="https://media.tenor.com/J2OLIFh0BsgAAAAi/zoho-go-zoho.gif" class="logo">
            <%-- <img src="https://www.zoho.com/branding/images/zoho-logo-512px.png" class="logo"> --%>
        </div>
    </div>
</div>
<div class="container" data-aos="fade-right">
    <h1>Register Page</h1>
    <form action="registeruser" method="post">
        <input type="text" name="name" required="required" placeholder="Enter User Name" value="${name}" />
        <input type="email" name="email" required="required" placeholder="Enter your Email"value="${email}" />
        <input type="number" name="phone_number" required="required" placeholder="Enter your Phone Number" value="${phone}"/>
        <input type="password" name="password" required="required" placeholder="Enter your password" value="${password}"/>
        <input type="submit" value="Register" required="required"/>
        <p class="err_msg">${error}</p>
        <p class="succ_msg">${success}</p>
    </form>
    <div>
        <jsp:include page="google-oauth.jsp"/>
        <div>
            <a href="index.jsp"><button>Back to index</button></a>
            <%-- <button onclick="transitionToPage('http://localhost:8080/Zoho_Social/index.jsp')">Back to index</button> --%>
        </div>
</body>
<script>
AOS.init();
// window.transitionToPage = function(href) {
//     document.querySelector('body').style.opacity = 0
//     setTimeout(function() { 
//         window.location.href = href
//     }, 500)
// }
// document.addEventListener('DOMContentLoaded', function(event) {
//     document.querySelector('body').style.opacity = 1
// })
</script>
</html>