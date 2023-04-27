<html>
<head>
    <title>Sign in-ZOHO Social</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
    <script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
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
    <h1><i class="fa-brands fa-windows"></i> Active Directory Login</h1>
    <form action="Adloginuser" method="post">
        <input type="text" name="phone_email_username" placeholder="Enter PhoneNumber/Username/Email" required="required"/>
        <input type="password" name="password" placeholder="Enter your Password" required="required"/>
        <input type="submit" value="Login" />
    </form>
    <div class="btns">
        <a href="Register.jsp"><button>Register</button></a>
        <a href="index.jsp"><button>Back to index</button></a>
    </div>
    <div>
        <div>
        </div>
</body>
<script>
AOS.init();

</script>
</html>