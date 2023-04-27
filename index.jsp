</body>
</html>

<html>
<head>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="./css/index.css">
    <link rel="stylesheet" href="./css/upload.css">
    <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
    <script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
    <title>ZOHO Social</title>
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
<div class="container" data-aos="fade-left">
    <center>
        <h1>Index page</h1>
        <a href="Login.jsp"><button class="btn-red"><i class="fa-solid fa-z"></i> Login</button></a>
        <a href="AdLogin.jsp"><button class="btn-green"><i class="fa-brands fa-windows"></i> AD Login</button></a>
         <a href="SSOAdLogin.jsp"><button class="btn-green"><i class="fa-brands fa-windows"></i> SSO AD Login</button></a>
        <a href="http://localhost:8080/Zoho_Social/authorize"><button class="btn-blue"><i class="fa-brands fa-microsoft"></i> Asure AD Login</button></a>
        <a href="https://dev-20721398.okta.com/app/dev-20721398_zohosocial_1/exk7s5imii2fazN3l5d7/sso/saml"><button id="ssoLogin" class="btn-yellow"><i class="fa-solid fa-user-check"></i> Okta Login</button></a>
        <a href="https://win-971madet2qs.zohor.com/adfs/ls/idpinitiatedsignon.apsx"><button id="ssoLogin" class="btn-yellow"><i class="fa-solid fa-user-check"></i> ADFS Login</button></a>
            <jsp:include page="google-oauth.jsp"/>
        <a href="Register.jsp"><button><i class="fa-solid fa-z"></i> Register</button></a>
    </center>

</div>
</body>
<script>
AOS.init();
</script>
</html>