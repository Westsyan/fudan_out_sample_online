function createCode(form) {
    $(form).formValidation("validateField", "phone")
    let valid = $(form).data("formValidation").getMessages("phone")
    if (valid.length === 0) {
        $.ajax({
            url: "/getValidCode",
            type: "post",
            data: $(form).serialize(),
            success: function (data) {
                if (data.valid === "true") {
                    $("#code").attr("disabled", "true")
                    $("#code").css("background", "#e7e7e7")
                    time(60)
                } else {
                    let s = 60 - data.second
                    $("#code-error").html(`操作过于频繁，请<span id="second">${s}</span>秒后再试！`)
                    second(s)
                }
            }
        })
    }

}

function second(s) {
    if (s == 0) {
        $("#code-error").empty();
    } else {
        $("#second").text(s);
        setTimeout(function () {
            second(s - 1)
        }, 1000)
    }
}

function time(s) {
    if (s == 0) {
        $("#code").removeAttr("disabled")
        $("#code").css("background", "white")
        $("#code").val("获取验证码");
    } else {
        $("#code").val(s + " 秒");
        setTimeout(function () {
            time(s - 1)
        }, 1000)
    }
}

function getPost() {
    const post = $("input[name='post']:checked").val();
    switch (post) {
        case "12" :
            $("#post2").hide();
            $("#post3").hide();
            break;
        case "13" :
            $("#post2").show();
            $("#post3").hide();
            break;
        default :
            $("#post2").show();
            $("#post3").show();
            break;
    }
}



function loginFormValidation() {
    $('#pwdForm').formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            phone: {
                validators: {
                    notEmpty: {
                        message: '账号不能为空!'
                    }
                }
            },
            pwd: {
                validators: {
                    notEmpty: {
                        message: "请输入密码！"
                    }
                }
            }
        }
    });
    $('#msgForm').formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            phone: {
                validators: {
                    notEmpty: {
                        message: '账号不能为空!'
                    },
                    regexp:{
                        regexp: '^1(3|4|5|7|8)\\d{9}$',
                        message:"账号格式不正确，请重新正确的手机号码！"
                    },
                }
            },
            code: {
                validators: {
                    notEmpty: {
                        message: "请输入密码！"
                    }
                }
            }
        }
    });
}

function registerFormValidation() {
    $('#form').formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            phone: {
                validators: {
                    notEmpty: {
                        message: '手机号不能为空!'
                    },
                    regexp:{
                      regexp: '^1(3|4|5|7|8)\\d{9}$',
                      message:"手机号格式不正确，请重新输入！"
                    },
                    remote: {
                        type: 'POST',
                        url: '/phoneIsExsit',
                        message:`手机号已存在！<a style='color:#2D8B67' href='/'>点击去登录</a>`,
                        delay: 1000
                    },
                }
            },
            pwd: {
                validators: {
                    notEmpty: {
                        message: "请输入密码！"
                    },
                    stringLength: {
                        min: 6,
                        message:"最少输入6位数密码！"
                    },
                }
            },
            rPwd:{
                validators: {
                    identical:{
                        field: 'pwd',
                        message:'两次输入密码不一致'
                    }
                }
            },
            code: {
                validators: {
                    notEmpty: {
                        message: "请输入验证码！"
                    }
                }
            },
            project: {
                validators: {
                    notEmpty: {
                        message: "请输入所在项目名称！"
                    }
                }
            },
            department: {
                validators: {
                    notEmpty: {
                        message: "请输入所在接受部门名称！"
                    }
                }
            },
            team: {
                validators: {
                    notEmpty: {
                        message: "请输入所在课题组名称！"
                    }
                }
            },
        }
    });
}


