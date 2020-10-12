    var onoff = true//根据此布尔值判断当前为注册状态0还是登录状态1
    var confirm1 = document.getElementsByClassName("confirm")[1]
    var confirm = document.getElementsByClassName("confirm")[0]
    var verification = document.getElementsByClassName("confirm")[2]
    var user = document.getElementById("user")
    var passwd = document.getElementById("passwd")
    var con_pass = document.getElementById("confirm-passwd")
    var tel = document.getElementById("tel")
    var hit = document.getElementById("hint").getElementsByTagName("p")[0]
    confirm.style.height = 0
    verification.style.height = 0
    confirm1.style.height = 0

    //自动居中title
    var name_c = document.getElementById("title")
    name1 = name_c.innerHTML.split("")
    name_c.innerHTML = ""
    for (i = 0; i < name1.length; i++)
        if (name1[i] != ",")
            name_c.innerHTML += "<i>" + name1[i] + "</i>"
    //引用hint()在最上方弹出提示
    function hint() {
        var hit = document.getElementById("hint")
        hit.style.display = "block";
		setTimeout(function(){hit.style.opacity = 1}, 0)
        setTimeout(function(){hit.style.opacity = 0}, 2000)
        setTimeout(function(){hit.style.display = 'none'}, 3000)
    }

    //注册按钮
    function signin() {

        var status = document.getElementById("status").getElementsByTagName("i")
        var hit = document.getElementById("hint").getElementsByTagName("p")[0]

        if (onoff) {
            confirm.style.height = 51 + "px"
            confirm1.style.height = 51 + "px"
            verification.style.height = 90 + "px"
            status[0].style.top = 35 + "px"
            status[1].style.top = 0
            onoff = !onoff
        } else {
            // if (!/^[A-Za-z0-9]+$/.test(user.value))
            //     hit.innerHTML = "账号只能为英文和数字"
            if (user.value.length < 6)
                hit.innerHTML = "账号长度必须大于6位"
            else if (passwd.value.length < 6)
                hit.innerHTML = "密码长度必须大于6位"
            else if (passwd.value != con_pass.value)
                hit.innerHTML = "两次密码不相等"

            else if (passwd.value = con_pass.value) {
                    $.get("/doc/user/checkUsername?username=" + $("#user").val(), function (data) {
                        if (data == 1) {
                            hit.innerHTML = "用户名已存在"
                            hint()
                            $("#user").select();
                        }
                        else{
                            var code = $.trim($("#Verification").val());
                            var phone =/^\w{3,}(\.\w+)*@[A-z0-9]+(\.[A-z]{2,5}){1,2}$/;
                            var phones = $.trim($("#tel").val());
                            if ($.trim(phones) == "") {
                                hit.innerHTML = "请填写邮箱号码！"
                                hint()
                                $("#tel").focus();
                                return;
                            }
                            if(!phone.exec(phones)){
                                hit.innerHTML = "邮箱输入格式不正确,请从新输入"
                                hint()
                                $("#tel").focus();
                                return;
                            }
                            if ($.trim(code) == "") {
                                hit.innerHTML = "动态密码未填写！"
                                hint()
                               // alert(code)
                                $("#Verification").focus();
                                return;
                            }
                            else {
                                $.ajax({
                                    type: 'POST',
                                    url: '/doc/email/check',
                                    data: {
                                        "code": code,
                                    },
                                    success: function (data) {
                                        if (data == "0") {
                                            hit.innerHTML = "验证码错误！"
                                            hint()
                                        } else {
                                            // alert("验证码正确");
                                            // var yes=step.nextStep();
                                            $.post("/doc/user/register", {
                                                username: user.value,
                                                password: passwd.value,
                                                tel: tel.value
                                            }, function (data) {
                                                if (data == 1) {
                                                    hit.innerHTML = "注册成功，请登录"
                                                    hint()

                                                    window.location = "/doc/user/index";
                                                } else {
                                                    hit.innerHTML = "网络错误，请重试"
                                                    hint()
                                                }
                                            });
                                        }

                                    },
                                    error: function (data) {
                                        alert("邮件发送失败");
                                        alert(data);
                                    },
                                });
                            }
                        }
                    });
            }
            hint()
        }
    }
//邮箱验证
    function Sendpwd(sender) {
        var validCode=true;
        var time=30;
        var phones = $.trim($("#tel").val());
        var hit = document.getElementById("hint").getElementsByTagName("p")[0]
        var phone =/^\w{3,}(\.\w+)*@[A-z0-9]+(\.[A-z]{2,5}){1,2}$/;
        if ($.trim(phones) == "") {
            hit.innerHTML = "请填写邮箱号码！"
            hint()
            $("#tel").focus();
            return;
        }
        if(!phone.exec(phones)){
            hit.innerHTML = "邮箱输入格式不正确,请从新输入"
            hint()
            $("#tel").focus();
            return;
        }

        var code=$(sender);
        if (validCode) {
            validCode=false;
            code.addClass("msgs1").attr("disabled",true);;
            $.ajax({
                type: 'POST',
                url: '/doc/email/send',
                data : {
                    "email" :phones ,
                },
                success: function(data) {


                },
                error: function(data) {
                },
            });
            var t=setInterval(function  () {
                time--;
                code.val(time+"秒");
                if (time==0) {
                    clearInterval(t);
                    code.val("重新获取");
                    validCode=true;
                    code.removeClass("msgs1").attr("disabled",false);

                }
            },1000);
        }

    }


    //登录按钮
    function login() {
        var hit = document.getElementById("hint").getElementsByTagName("p")[0]
        if (onoff) {
			$.post("/doc/user/login", {username: user.value, password: passwd.value}, function (data) {
				if (data == 0) {
					hit.innerHTML = "用户名或者密码有误"
                    hint()
					$("#user").val("").focus();
					$("#passwd").val("");
				} else if(data=="1"){
					window.location = "/doc/myfile/list";
				}
				else if(data=="2")
                {
                    window.location = "/doc/admin/index";
                }
			});

        } else {
            var status = document.getElementById("status").getElementsByTagName("i")
            confirm.style.height = 0
            confirm1.style.height = 0
            verification.style.height = 0
            status[0].style.top = 0
            status[1].style.top = 35 + "px"
            onoff = !onoff
        }
    }
//AJAX验证用户名是否存在
function checkUsername(obj) {
    if (onoff==0) {
        $.get("/doc/user/checkUsername?username=" + $(obj).val(), function (data) {
            if (data == 1) {
                hit.innerHTML = "用户名已存在"
                hint()
                $("#user").select();
            }
        });
    }
}

//-------------------------------------------------
//-------------------------------------------------
//-------------------------------------------------

//注册方法
/* function register() {
    var pwd1 = $("#rp1").val();
    var pwd2 = $("#rp2").val();
    if (pwd1 != pwd2) {
        $("#rnh").html("两次输入的密码不一致!");
        $("#rb").attr("disabled", true);
        $("#rb").css("background-color", "#cccccc");
        $("#rp1").val("").focus();
        $("#rp2").val("");
    } else {
        var rn = $("#rn").val();
        $.post("/crm1/user/register", {username: rn, password: pwd1}, function (data) {
            if (data == 1) {
                window.location = "/crm/user/index";
            } else {
                $("#rnh").html("注册失败,请稍后再试!");
            }
        });
    }
}
 */
//登录方法
/*
function login() {
 
    $.post("/doc/user/login", {username: user, password: passwd}, function (data) {
        if (data == 0) {
			hit.innerHTML = "用户名或者密码有误"
            $("#user").val("").focus();
            $("#passwd").val("");
        } else {
            window.location = "/doc/customer/list";
        }
    });
}*/
