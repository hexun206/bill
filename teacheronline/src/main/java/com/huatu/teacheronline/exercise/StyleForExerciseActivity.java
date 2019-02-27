package com.huatu.teacheronline.exercise;

/**这就是自己写的js
 * Created by ljzyuhenda on 16/1/20.
 */
public class StyleForExerciseActivity {

        public static String bodyCompletion = "<div  id = 'showTextField'  style='display:  block; font-size:15px;'></div>";

        //主观题布局参数
        public static String gfzgHead = "<link href=\"file:///android_asset/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "<link href=\"file:///android_asset/bootstrap-slider.min.css\" rel=\"stylesheet\">\n" +
                "<script type='text/javascript' src=\"file:///android_asset/jquery.min.js\"></script>\n" +
                "<script type='text/javascript' src=\"file:///android_asset/touche.js\"></script>\n" +
                "<script type='text/javascript' src=\"file:///android_asset/bootstrap-slider.min.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "\t$(function(){\n" +
                "if($(\".qs-title\").height()+200>document.body.clientHeight){\n" +
                "           $(\".mrtitle\").addClass(\"mrtitlehover\");\n" +
                "           $(\".mrtitlehover\").removeClass(\"mrtitle\");\n" +
                "        }\n"+
                "\t\t$(\".myclose\").on(\"click\",function(){\n" +
                "clickClose();" +
                "\t\t});\n" +
                "\t\t$('.tab label').on('click',function () {\n" +
                "\t\t\tif($(this).html()=='解析'){\n" +
                "\t\t\t\t$('.tabcontent').eq(1).hide();\n" +
                "\t\t\t\t$('.tabcontent').eq(0).show();\n" +
                "\t\t\t\t$(this).css({'margin-left':'21.5%','top':'0'});\n" +
                "\t\t\t\t$(this).addClass('sc-color').siblings('label').removeClass('sc-color').css({'top':'-1.5px'});\n" +
                "\t\t\t}else{\n" +
                "\t\t\t    $('.tabcontent').eq(0).hide();\n" +
                "                $('.tabcontent').eq(1).show();\n" +
                "\t\t\t\t$(this).addClass('sc-color').siblings('label').removeClass('sc-color').css({'top':'-1.5px','margin-left':'1%'});\n" +
                "\t\t\t\t$(this).css({'top':'0'});\n" +
                "\t\t\t}\n" +
                "\t\t});"+
                "\n" +
                "\t\t$(\"#scoreRange\").on('touchstart',function(event){\n" +
//                "\t\t\t$(\".txt\").html('开始了');\n" +
                "\t\t\tevent.preventDefault();\n" +
                "\t\t});\n" +
                "\t\t$(\"#scoreRange\").on('touchmove',function(event){\n" +
//                "\t\t\t$(\".txt\").html('移动中');\n" +
//                "\t\t\tevent.preventDefault();\n" +
                "\t\t});\n" +
                "\t\t$(\"#scoreRange\").on('touchend touchcancel',function(event){\n" +
//                "\t\t\t$(\".txt\").html('touchend松开了');\n" +
                "\t\t});"+
                "\t\t$(\".tooltip-inner\").hide();\n" +
                "\t});\n" +
                "/*评分Range*/\n" +
                "\tfunction doChange()\n" +
                "\t{\n" +
                "\t\tvar scoreRange = document.getElementById(\"scoreRange\");\n" +
                "\t\tvar rangeBgDiv = document.getElementById(\"showRangeValueBgDiv\");\n" +
                "\t\t/*获取估分条的宽度*/\n" +
                "\t\tvar scoreRangeWidth = scoreRange.offsetWidth;\n" +
                "\t\trangeBgDiv.style.width = scoreRangeWidth + \"px\";\n" +
                "\n" +
                "\t\tvar rangeObj = document.getElementById(\"scoreRange\");\n" +
                "\t\trangeObj.style.background = '-webkit-linear-gradient(#00B38a, #00B38a) no-repeat';\n" +
                "\t\tvar currentPersent = parseInt((rangeObj.value/rangeObj.max)*100);\n" +
                "\n" +
                "\t\tif (currentPersent < 50) {currentPersent++;}\n" +
                "\t\tif (currentPersent > 90) {currentPersent--;}\n" +
                "\t\trangeObj.style.backgroundSize = currentPersent + '% 100%';\n" +
                "\t\trangeObj.style.backgroundColor = 'white';\n" +
                "\n" +
                "\t\tvar showRangeValueSpan = document.getElementById(\"showRangeValue\");\n" +
                "\t\tvar spanNeedOffsetWidth = (rangeObj.value/rangeObj.max)*(rangeBgDiv.offsetWidth - 28);\n" +
//                "\t\tif (rangeObj.value > 9) {spanNeedOffsetWidth = spanNeedOffsetWidth;}\n" +
                "\t\tshowRangeValueSpan.style.marginLeft = spanNeedOffsetWidth +\"px\";\n" +
                "\t\tshowRangeValueSpan.innerText = rangeObj.value;\n" +
                "\t\t\tsliderErClick(rangeObj.value);\n" +
                "\t}"+
                "</script>";

        //再加一个新的样式
        public static String newStyle="<style>"+
                "table{\n" +
                "max-width:300px; \n" +
                "width:expression(this.width > 300&& this.height < this.width ? 300: true); }\n"+
                "img{max-width:300px;vertical-align:middle;}"+
                "*{margin:0;padding:0;}"+
                "ul,li{list-style:none}"+
                ".exam-box .qs-title{"+
                "padding:20px 10px 10px;" +
                "line-height:20px;" +
                "box-sizing: border-box;" +
                "font-size:16px;color:#323232}" +
                ".exam-box .qs-c-list{" +
                "font-size:14px;" +
                "line-height:16px;padding-bottom:20px;" +
                "}" +
                ".exam-box .qs-c-list li{padding:8px 10px 8px 40px;position:relative;margin-top: 20px;color:#666;}.exam-box .qs-c-list li a{color:#666;}.exam-box .qs-c-list li .the-index{position:absolute;left:10px;top:15px;width:50px;height:32px;text-align:center;line-height:20px;font-size:16px;color:#323232}.exam-box .qs-c-list .r-·cs-single .the-index{background:#31bc77;border-color:#31bc77;color:#fff;border-radius:50%;}.exam-box .qs-c-list .w-cs-single .the-index{background:#e5b5e4;border-color:#e5b5e4;color:#fff;}.exam-box .qs-c-list .u-choose-single .the-index{border-color:#ff5a5a;background:#ff5a5a;color:#fff;}.exam-box .qs-c-list .r-cs .the-index{background:#31bc77;border-color:#31bc77;color:#fff;}.exam-box .qs-c-list .w-cs .the-index{background:#e5b5e4;border-color:#e5b5e4;color:#fff;}.exam-box .qs-c-list .u-choose .the-index{border-color:#ff5a5a;background:#ff5a5a;color:#fff;}.exam-box .correct-answer{padding:10px;}.exam-box .correct-answer .correct{color:#31bc77;}.exam-box .correct-answer .yours{color:#ff5a5a;}.answer-intro{padding:10px 10px 20px;margin-top:10px;}.answer-intro h3{font-size:16px;margin:8px 0 16px;font-weight:normal;color:#323232;}.answer-intro .txt{font-size:14px;line-height:20px;color:#666;}.live-intro .txt span{background:url(file:///android_assetve_btn.png) no-repeat right center;padding:8px 42px 7px 0;background-size: 30px;}.tabxmht thead th{color: #010101;font-size: 14px;}.tabxmht thead th,.tabxmht tbody tr td{border-top: 1px solid #dbdbdb;border-right: 1px solid #dbdbdb;font-weight: normal;line-height: 32px;height:32px;}.tabxmht thead th:first-child ,.tabxmht tbody tr td:first-child { border-left: 1px solid #dbdbdb;}.tabxmht tbody tr td{border-bottom: 1px solid #dbdbdb;text-align: center;font-size: 18px;}.table1 tbody tr td:nth-child(1){color: #FFA71C;}.table1 tbody tr td:nth-child(2){color: #2FBC75;}.table1 tbody tr td:nth-child(3){color:#FF5C5B;}.h3{font-size:16px;line-height:42px;height:42px;width:90%;text-align:left;font-weight:normal;margin:0 auto;}.qs-c-list .qs-c-input{width: 40%;height: 32px;padding-left: 8px;margin-left: 10px;border: none;border-bottom: 1px solid #323232;font-size: 14px;color: #31bc77;} .answer-intro .txt{padding:10px; color:#666;}\n" +
                "        </style>";
        //主观题样式
        public static String zgstyle= "<style>" +
                "    input[type=range] {\n" +
                "            -webkit-appearance: none;\n" +
                "            border-radius: 10px;/*这个属性设置使填充进度条时的图形为圆角*/\n" +
                "        }\n" +
                "        \n" +
                "        input[type=range]::-webkit-slider-thumb {\n" +
                "            -webkit-appearance: none;\n" +
                "        }\n" +
                "        input[id=bgRange]::-webkit-slider-thumb {\n" +
                "            -webkit-appearance: none;\n" +
                "            height: 0px;\n" +
                "            width: 0px;\n" +
                "            margin-top: 0px;\n" +
                "            /*使滑块超出轨道部分的偏移量相等*/\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 50%;\n" +
                "            /*外观设置为圆形*/\n" +
                "            border: solid 0.125em rgba(205, 224, 230, 0.5);\n" +
                "            /*设置边框*/\n" +
                "            box-shadow: 0 .125em .125em #3b4547;\n" +
                "            /*添加底部阴影*/\n" +
                "        }\n" +
                "\n" +
                "        input[id=scoreRange]::-webkit-slider-thumb {\n" +
                "            -webkit-appearance: none;\n" +
                "            height: 25px;\n" +
                "            width: 25px;\n" +
                "            margin-top: -9px;\n" +
                "            /*使滑块超出轨道部分的偏移量相等*/\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 50%;\n" +
                "            /*外观设置为圆形*/\n" +
                "            border: solid 0.125em #ffffff;\n" +
                "            /*设置边框*/\n" +
                "            box-shadow: 0px .125em .125em #c7c7c7;\n" +
                "            /*添加底部阴影*/\n" +
                "        }\n" +
                "        \n" +
                "        input[type=range]::-webkit-slider-runnable-track {\n" +
                "            height: 8px;\n" +
                "            border-radius: 4px;\n" +
                "            /*将轨道设为圆角的*/\n" +
                "            box-shadow: 0px 1px 1px lightgray; /*轨道内置阴影效果*/\n" +
                "            /*background: -webkit-linear-gradient(#00B38a, #00B38a) no-repeat;\n" +
                "            background-size: 0% 100%;*/\n" +
                "        }\n" +
                "        \n" +
                "        input[type=range]:focus {\n" +
                "            outline: none;\n" +
                "        }" +
                "    #rangeDiv {\n" +
//                "        display: block;\n" +
                "        background-color:#f4f4f4;\n" +
                "        height: 80px; \n" +
                "        position:fixed;\n" +
                "        bottom:0px;\n" +
                "        width: 100%; \n" +
                "        margin: 0; \n" +
                "        padding: 0; \n" +
                "        text-align: center;\n" +
//                "        margin-left: 10px;\n" +
//                "        margin-right: -10px;\n" +
                "        padding-left: 5%;\n" +
//                "        z-index:9999;" +
//                "        padding-right: 10px; \n" +
                "    }\n" +
                "\n" +
                "    #rangeDiv input {\n" +
                "        background-color:white; \n" +
                "        color:white; \n" +
                "        position: relative; \n" +
                "        left: 10px; \n" +
                "        width:90%;\n" +
                "    }\n" +
                "\n" +
                "    #showRangeValueBgDiv {\n" +
//                "        display: block; \n" +
                "        position:relative; \n" +
                "        top: -12px; \n" +
                "        left: 5%; \n" +
                "        text-align: left; \n" +
                "        width: 80%;\n" +
                "    }\n" +
                "\n" +
                "    #showRangeValue {\n" +
                "        height:40px; \n" +
                "        line-height:40px; \n" +
                "        width: 80px;\n" +
                "        color:#00B38a; \n" +
                "        font-size: 14px; \n" +
                "        margin-left: -15px;\n" +
                "    }"+
                "img{max-width:300px;vertical-align:middle;}*{margin:0;padding:0;}ul,li{list-style:none}.exam-box .qs-title{padding:20px 10px 10px;" +
                "line-height:20px;box-sizing: border-box;font-size:16px;color:#323232}.exam-box .qs-c-list{font-size:14px;line-height:16px;" +
                "padding-bottom:20px;float: left;width: 100%}.exam-box .qs-c-list li{padding:8px 10px 8px 40px;position:relative;margin-top: 20px;color:#666;" +
                "}.exam-box .qs-c-list li a{color:#666;}.exam-box .qs-c-list li .the-index{position:absolute;left:10px;top:5px;width:20px;height:20px;" +
                "border:solid 1px #cacaca;text-align:center;line-height:20px;}.exam-box .qs-c-list li.single-cs .the-index{border-radius:50%;}.exam-box " +
                ".qs-c-list .r-·cs-single .the-index{background:#31bc77;border-color:#31bc77;color:#fff;border-radius:50%;}.exam-box .qs-c-list .w-cs-single " +
                ".the-index{background:#e5b5e4;border-color:#e5b5e4;color:#fff;border-radius:50%;}.exam-box .qs-c-list .u-choose-single " +
                ".the-index{border-color:#ff5a5a;background:#ff5a5a;color:#fff;border-radius:50%;}.exam-box .qs-c-list .r-cs .the-index{background:#31bc77;" +
                "border-color:#31bc77;color:#fff;}.exam-box .qs-c-list .w-cs .the-index{background:#e5b5e4;border-color:#e5b5e4;color:#fff;}.exam-box " +
                ".qs-c-list .u-choose .the-index{border-color:#ff5a5a;background:#ff5a5a;color:#fff;}.exam-box .correct-answer{padding:10px;}.exam-box " +
                ".correct-answer .correct{color:#31bc77;}.exam-box .correct-answer .yours{color:#ff5a5a;}.answer-intro{padding:5px 0px;margin-top:10px;" +
                "background-color:#f2f4f5; padding-bottom: 120px;float: left;width: 100%;min-height: 320px;}.answer-intro h3{font-size:16px;margin:8px 0 " +
                "16px;font-weight:normal;color:#323232;}.answer-intro .tabcontent{padding:0px 12px;font-size:15px;line-height:20px;color:#666;}.live-intro " +
                ".txt span{background:url(file:///android_asset/live_btn.png) no-repeat right center;padding:8px 42px 7px 0;background-size: 30px;}.tabxmht " +
                "thead th{color: #010101;font-size: 14px;}.tabxmht thead th,.tabxmht tbody tr td{border-top: 1px solid #dbdbdb;border-right: 1px solid " +
                "#dbdbdb;font-weight: normal;line-height: 32px;height:32px;}.tabxmht thead th:first-child ,.tabxmht tbody tr td:first-child { border-left: " +
                "1px solid #dbdbdb;}.tabxmht tbody tr td{border-bottom: 1px solid #dbdbdb;text-align: center;font-size: 18px;}.table1 tbody tr td:nth-child" +
                "(1){color: #FFA71C;}.table1 tbody tr td:nth-child(2){color: #2FBC75;}.table1 tbody tr td:nth-child(3){color:#FF5C5B;}.h3{font-size:16px;" +
                "line-height:42px;height:42px;width:90%;text-align:left;font-weight:normal;margin:0 auto;}.single-cs{float: right;}.single-cs .ckjx{width: " +
                "80px;outline:medium; border:none;height: 24px;font-size: 16px;line-height:24px;color: #00B38A;border: 1px solid #00B38A;background-color: " +
                "#fff;margin-right: 10px;-moz-border-radius: 5px;      \n" +
                "        -webkit-border-radius: 5px;border-radius:5px; }\n" +
                ".mrtitle{font-size: 16px;color: #CCCCCC;text-align: center;bottom: 24px;width: 100%;position: fixed;}\n" +
                ".mrtitlehover{font-size: 16px;color: #CCCCCC;text-align: center;margin-bottom: 24px;width: 100%}\n" +
                "\n" +
                ".tab{width: 100%;text-align: center; border-bottom: 1px solid #CCCCCC;margin-bottom: 10px;}\n" +
                "\t\t.tab label{display: inline-block;color: #000000;font-size: 12px;margin-bottom:0px; height: 36px; line-height: 36px;}\n" +
                "\t\t.tab .sc-color{color: #00B38A;font-size: 16px;}\n" +
                "\t\t.tab label:nth-child(2){padding-left: 14px; top: -1.5px; z-index: 1;\n}\n" +
                "\t\t.tab label:first-child{margin-left: 21.5%;}\n" +
                "\n" +
                "\t\t.answer-intro .tab .myclose{float: right;background-image: url(file:///android_asset/jiexi_close.png);width:15px;height: 15px; " +
                "background-repeat:no-repeat;background-size:100% 100%;margin-right:9px; margin-top: 8px;}\n" +
                "\n" +
                "\n" +
                "\t\t.answer-points{width: 100%;position: fixed;bottom: 0;background-color: #f2f4f5;padding-bottom: 28px;left: 0px;z-index: 999;\n}\n" +
                "\t\t.answer-points label{font-size: 16px;color: #00B38A;padding-top: 12px;margin-bottom:0px;padding-bottom:12px;width: 100%;text-indent: " +
                ".8em;}\n" +
                "\t\t.points-line{width: 85%;margin: 0 auto;}\n" +
                "\t\t#ex1Slider{width: 100%;margin: 0 auto;}\n" +
                ".slider-handle{\n" +
                "\t\tbackground-image: none !important;\n" +
                "\t\tbackground: #f90;\n" +
                "\t}"+
                "\n" +
                ".answer-intro .txt {padding:7px 10px 10px 15px; color:#666;}\n" +
                "\t\t.answer-intro h3 {padding-left:10px;}\n" +
                "\t</style>";

        //这个是样式
    public static String style = "<style>" +
                "table{\n" +
                "max-width:300px; \n" +
                "width:expression(this.width > 300&& this.height < this.width ? 300: true); }\n"+
            "img{max-width:300px;vertical-align:middle;}" +
            "*{margin:0;padding:0;}" +
            "ul,li{list-style:none}" +
            ".exam-box .qs-title{" +
            "padding:20px 10px 10px;" +
            "line-height:20px;" +
            "box-sizing: border-box;" +
            "font-size:16px;color:#323232" +
            "}" +
            ".exam-box .qs-c-list{" +
            "font-size:14px;" +
            "line-height:16px;" +
            "padding-bottom:20px;" +
            "}" +
            ".exam-box .qs-c-list li{" +
            "padding:8px 10px 8px 40px;" +
            "position:relative;" +
            "margin-top: 20px;" +
            "color:#666;" +
            "}" +
            ".exam-box .qs-c-list li a{" +
            "color:#666;" +
            "}" +
            ".exam-box .qs-c-list li .the-index{" +
            "position:absolute;" +
            "left:10px;" +
            "top:5px;" +
            "width:20px;" +
            "height:20px;" +
            "border:solid 1px #cacaca;" +
            "text-align:center;" +
            "line-height:20px;" +
            "}" +
            ".exam-box .qs-c-list li.single-cs .the-index{" +
            "border-radius:50%;" +
            "}" +
            ".exam-box .qs-c-list .r-·cs-single .the-index{" +
            "background:#31bc77;" +
            "border-color:#31bc77;" +
            "color:#fff;" +
            "border-radius:50%;" +
            "}" +
            ".exam-box .qs-c-list .w-cs-single .the-index{" +
            "background:#e5b5e4;" +
            "border-color:#e5b5e4;" +
            "color:#fff;" +
            "border-radius:50%;" +
            "}" +
            ".exam-box .qs-c-list .u-choose-single .the-index{" +
            "border-color:#ff5a5a;" +
            "background:#ff5a5a;" +
            "color:#fff;" +
            "border-radius:50%;" +
            "}" +
            ".exam-box .qs-c-list .r-cs .the-index{" +
            "background:#31bc77;" +
            "border-color:#31bc77;" +
            "color:#fff;" +
            "}" +
            ".exam-box .qs-c-list .w-cs .the-index{" +
            "background:#e5b5e4;" +
            "border-color:#e5b5e4;" +
            "color:#fff;" +
            "}" +
            ".exam-box .qs-c-list .u-choose .the-index{" +
            "border-color:#ff5a5a;" +
            "background:#ff5a5a;" +
            "color:#fff;" +
            "}" +
            ".exam-box .correct-answer{" +
            "padding:10px;" +
            "}" +
            ".exam-box .correct-answer .correct{" +
            "color:#31bc77;" +
            "}" +
            ".exam-box .correct-answer .yours{" +
            "color:#ff5a5a;" +
            "}" +
            ".answer-intro{" +
            "padding:10px 10px 20px;" +
            "margin-top:10px;" +
            "}" +
            ".answer-intro h3{" +
            "font-size:16px;" +
            "margin:8px 0 16px;" +
            "font-weight:normal;" +
            "color:#323232;" +
            "}" +
            ".answer-intro .txt{" +
            "font-size:14px;" +
            "line-height:20px;" +
            "color:#666;" +
            "}.live-intro .txt span{" +
            "background:url(file:///android_asset/live_btn.png)" +
            " no-repeat right center;" +
            "padding:8px 42px 7px 0;" +
            "background-size: 30px;" +
            "}" + ".tabxmht thead th{" +
            "color: #010101;" +
            "font-size: 14px;" +
            "}" +
            ".tabxmht thead th,.tabxmht tbody tr td{" +
            "border-top: 1px solid #dbdbdb;" +
            "border-right: 1px solid #dbdbdb;" +
            "font-weight: normal;" +
            "line-height: 32px;" +
            "height:32px;" +
            "}" +
            ".tabxmht thead th:first-child ,.tabxmht tbody tr td:first-child { " +
            "border-left: 1px solid #dbdbdb;" +
            "}" +
            ".tabxmht tbody tr td{" +
            "border-bottom: 1px solid #dbdbdb;" +
            "text-align: center;" +
            "font-size: 18px;" +
            "}" +
            ".table1 tbody tr td:nth-child(1){" +
            "color: #FFA71C;" +
            "}" +
            ".table1 tbody tr td:nth-child(2){" +
            "color: #2FBC75;" +
            "}" +
            ".table1 tbody tr td:nth-child(3){" +
            "color:#FF5C5B;" +
            "}" +
            ".h3{" +
            "font-size:16px;" +
            "line-height:42px;" +
            "height:42px;" +
            "width:90%;" +
            "text-align:left;" +
            "font-weight:normal;" +
            "margin:0 auto;" +
            "}" +
            "</style>";

    public static String bodyStyle_prefix = "<div class='mbox exam-box'>";
    public static String bodyStyle_suffix = "</div></body>";
    //题目 1->题目内容
        public static String choiceTitle ="<p style='line-height:30px;'><font style='color: red; float: left;'>%1$s</font>";
        public static String bodyStyle = "<div class='qs-title' style='word-wrap:break-word;padding:20px 10px 10px;line-height:30px;'>" +
                "%1$s" + "</div>";

        //    public static String bodyStyle = "<div class='qs-title'>" +
//            "%1$s" +
//            "</div>";
        //通过js互掉，填充网页内容
//    public static String jsCode = " <script type='text/javascript'>" +
//            "        function choose(idn){" +
//            "                window.WebViewJavascriptBridge.callHandler(" +
//            "                   'onChoiceClick'" +
//            "                   , {'param':idn}" +
//            "                   ,function(responseData) {}" +
//            "                );" +
//            "                var x=document.getElementById(idn);" +
//            "                if(x.className==\"u-choose\"){" +
//            "                    x.className=\"\"" +
//            "                }else if(x.className==\"u-choose-single\"){" +
//            "                    x.className=\"single-cs\"" +
//            "                }else if(x.className==\"\"){" +
//            "                    x.className=\"u-choose\"" +
//            "                }else{" +
//            "                    x.className=\"u-choose-single\"" +
//            "               }" +
//            "            }" +
//                "function toActivity(param){\n" +
//                "var av=document.getElementsByClassName(\"qs-c-input\").item(0).value;"+
//                "window.js.jumpActivity(av);" +
//                "}"+
//            "    </script>";
        public static String jsCode = " <script type='text/javascript'>" +
                "var datas;"+
                "        function choose(idn){" +
                "                window.WebViewJavascriptBridge.callHandler(" +
                "                   'onChoiceClick'" +
                "                   , {'param':idn}" +
                "                   ,function(responseData) {}" +
                "                );" +
                "                var x=document.getElementById(idn);" +
                "                if(x.className=='u-choose'){" +
                "                    x.className=''" +
                "                }else if(x.className=='u-choose-single'){" +
                "                    x.className='single-cs'" +
                "                }else if(x.className==''){" +
                "                    x.className='u-choose'" +
                "                }else{" +
                "                    x.className='u-choose-single'" +
                "               }" +
                "            }" +

                "function toActivity(param){" +
                "var av=document.getElementsByClassName('qs-c-input').item(0).value;"+
                "window.js.jumpActivity(av);" +
                "}"+
                "function connectWebViewJavascriptBridge(callback) {\n" +
                "        if (window.WebViewJavascriptBridge) {\n" +
                "            callback(WebViewJavascriptBridge)\n" +
                "        } else {\n" +
                "            document.addEventListener(\n" +
                "                'WebViewJavascriptBridgeReady'\n" +
                "                , function() {\n" +
                "                    callback(WebViewJavascriptBridge)\n" +
                "                },\n" +
                "                false\n" +
                "            );\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    connectWebViewJavascriptBridge(function(bridge) {\n" +
                "        bridge.init(function(message, responseCallback) {\n" +
                "            console.log('JS got a message', message);\n" +
                "            var data = {\n" +
                "                'Javascript Responds': '测试中文!'\n" +
                "            };\n" +
                "            console.log('JS responding with', data);\n" +
                "            responseCallback(data);\n" +
                "        });\n" +
                "\n" +
                "        bridge.registerHandler(\"payResult\", function(data, responseCallback) {\n" +
                "            alert(data);\n" +
                "            var responseData = \"Javascript Says Right back aka!\";\n" +
                "            responseCallback(data);\n" +
                "        });\n" +
                "bridge.registerHandler(\"inputfield\",\n" +
                "function(data, responseCallback) {\n" +
                "var showTextFieldDiv = document.getElementById('showTextField');\n" +
                //这里面应该有个样式
                "var inputString = \"<span>&nbsp;&nbsp答:</span>\"; datas = data.split(\",\");\n" +
                "for (var i = 0; i < datas.length; i++) {\n" +
                "var index = i + 1;\n" +
                "var margingLeft = \"1px\";\n" +
                "if (i != 0) {\n" +
                "margingLeft = \"25px\";\n" +
                "}\n" +
                "inputString += \"<input id='doAnswer\" + index + \"' type='text' name='doAnswer' onclick='skipTo()'" +
                "style='border-top: none;border-left: none;border-right: none; outline:medium; border-bottom-color: lightGray;background-color: #ffffff; border-radius: 0; " +
                "border-bottom-width: 1.0px; width: 85%; color: #20c873; font-size:15px;height:32px; margin-left: \" + margingLeft + \";' placeholder = '\" + index + " +
                "\"'/>\";\n" +
                "}\n" +
                "showTextFieldDiv.innerHTML = inputString; \n" +
                "var responseData = data;\n" +
                "// response层\n" +
                "\tresponseCallback(data);\n" +
                "});\n"+
                "bridge.registerHandler(\"getinputcontent\",\n" +
                "function(data, responseCallback) {\n"+
                " var inputResult = \"\";"+
                "for (var i = 0; i < datas.length; i++) {\n"+
                " var index = i + 1;"+"  var inputText = document.getElementById('doAnswer'+index);"+
                "if (i == (datas.length - 1)) {"+"  inputResult = inputResult + inputText.value;"+
                " }else{"+"  inputResult = inputResult + inputText.value + '##JSZXSP1##';"+
                " }\n"+
                "}\n"+
                "responseCallback(inputResult);"+
                " })\n"+
                " /*设置用户已输入的内容*/\n" +
                "    bridge.registerHandler('setUserInput_id', function(data, responseCallback) {\n" +
                "      var inputdatas = data.split(\"|\");\n" +
                "      for (var i = 0; i < inputdatas.length; i++) {\n" +
                "        var index = i + 1;\n" +
                "        var inputText = document.getElementById('doAnswer'+index);\n" +
                "        inputText.value = inputdatas[i];\n" +
                "      }\n" +
                "       responseCallback(inputdatas.length);"+
                "    })"+
                " /*设置用户已输入的内容*/\n" +
                "    bridge.registerHandler('ontestClick', function(data, responseCallback) {\n" +
                "       responseCallback(data);"+
                "    })"+
                "    /*禁止输入*/\n" +
                "    bridge.registerHandler('setUserInputDisabled_id', function(data, responseCallback) {\n" +
                "      for (var i = 0; i < datas.length; i++) {\n" +
                "        var index = i + 1;\n" +
                "        var inputText = document.getElementById('doAnswer'+index);\n" +
                "        inputText.disabled = \"disabled\"\n" +
                "        inputText.style.color = \"#000000\";\n" +
                "      }\n" +
                "       responseCallback(datas.length);"+
                "    })"+
                "    })\n"+
                "    </script>";
        //查看解析视频
    public static String jsCode_jiexi = " <script type='text/javascript'>" +
            "        function videoclick(){" +
            "                window.WebViewJavascriptBridge.callHandler(" +
            "                   'onVideoClick'" +
            "                   , {'param':'video'}" +
            "                   ,function(responseData) {}" +
            "                );" +
            "            }" +
            "    </script>";
        //模块题海主观题查看解析
        public static String zhuguan_jiexi = "<script type='text/javascript' src=\"file:///android_asset/jquery.min.js\"></script>\n"+" <script type='text/javascript'>" +
                "$(function(){\n" +
                "if($(\".qs-title\").height()+200>document.body.clientHeight){            \n" +
                "\t$(\".mrtitle\").addClass(\"mrtitlehover\");            \n" +
                "\t$(\".mrtitlehover\").removeClass(\"mrtitle\");       \n" +
                "  }\n" +
                "});\n"+
                "        function parsingClick(){" +
                "                window.WebViewJavascriptBridge.callHandler(" +
                "                   'parsingClick'" +
                "                   , {'param':'text'}" +
                "                   ,function(responseData) {}" +
                "                );" +
                "            }" +
                "function toActivity(param){\n" +
                "var av=document.getElementsByClassName(\"qs-c-input\").item(0).value;"+
                "window.js.jumpActivity(av);" +
                "}"+
                "    </script>";
        //真题主观题查看解析js
        public static String zhuguan_jiexi_Er = " <script type='text/javascript'>" +
                "function connectWebViewJavascriptBridge(callback) {\n" +
                "        if (window.WebViewJavascriptBridge) {\n" +
                "            callback(WebViewJavascriptBridge)\n" +
                "        } else {\n" +
                "            document.addEventListener(\n" +
                "                'WebViewJavascriptBridgeReady'\n" +
                "                , function() {\n" +
                "                    callback(WebViewJavascriptBridge)\n" +
                "                },\n" +
                "                false\n" +
                "            );\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    connectWebViewJavascriptBridge(function(bridge) {\n" +
                "        bridge.init(function(message, responseCallback) {\n" +
                "            console.log('JS got a message', message);\n" +
                "            var data = {\n" +
                "                'Javascript Responds': '测试中文!'\n" +
                "            };\n" +
                "            console.log('JS responding with', data);\n" +
                "            responseCallback(data);\n" +
                "        });\n" +
                "\n" +
                " bridge.registerHandler(\"setDrage_id\", function(data, responseCallback) {\n" +
                " var rangeValueArray = new Array();" +
                "      var inputdatas = data.split(\",\");\n" +
                "rangeValueArray[0]= new Number(inputdatas[0]); rangeValueArray[1] = new Number(inputdatas[1]);"+
                "if(rangeValueArray[0] == 0){" +
                "//设置分值默认显示状态\n" +
                "            var showRangeValueSpan = document.getElementById('showRangeValue');\n" +
                "            showRangeValue.style.marginLeft = '-10px';\n" +
                "            showRangeValue.innerText = '滑动估分';\n" +
                "\n" +
                "            //设置滑动条位置\n" +
                "            var scoreRange = document.getElementById('scoreRange');\n" +
                "            scoreRange.max = rangeValueArray[0];\n" +
                "            scoreRange.value = \"0\";\n" +
                "            scoreRange.style.backgroundSize = '0%';"+
                "}else{" +
                "var rangeObj = document.getElementById(\"scoreRange\");\n" +
                "            rangeObj.max = rangeValueArray[0];\n" +
                "            rangeObj.value = rangeValueArray[1];\n" +
                "\n" +
                "\t        var rangeBgDiv = document.getElementById(\"showRangeValueBgDiv\");\n" +
                "            /*获取估分条的宽度*/\n" +
                "\t        var scoreRangeWidth = rangeObj.offsetWidth;\n" +
                "\t        rangeBgDiv.style.width = scoreRangeWidth + \"px\";\n" +
                "\n" +
                "            rangeObj.style.background = '-webkit-linear-gradient(#00B38a, #00B38a) no-repeat';\n" +
                "            var currentPersent = parseInt((rangeObj.value/rangeObj.max)*100);\n" +
                "\n" +
                "            if (currentPersent < 50) {currentPersent++;}\n" +
                "            if (currentPersent > 90) {currentPersent--;}\n" +
                "            rangeObj.style.backgroundSize = currentPersent + '% 100%';\n" +
                "        \n" +
                "            var showRangeValueSpan = document.getElementById(\"showRangeValue\");\n" +
                "            var spanNeedOffsetWidth = (rangeObj.value/rangeObj.max)*(rangeBgDiv.offsetWidth - 28);\n" +
//                "            if (rangeObj.value > 9) {spanNeedOffsetWidth = spanNeedOffsetWidth;}\n" +
                "            showRangeValueSpan.style.marginLeft = spanNeedOffsetWidth +\"px\";\n" +
                "\n" +
                "            showRangeValueSpan.innerText = rangeObj.value;\n" +
                "       responseCallback(data.length);"+
                "}"+
                "    })\n"+

                "})\n"+
                "        function parsingErClick(){" +
                "                window.WebViewJavascriptBridge.callHandler(" +
                "                   'parsingErClick'" +
                "                   , {'param':'text'}" +
                "                   ,function(responseData) {}" +
                "                );" +
                "            }" +
                "\t//客户端交互函数，获取滑块当前值\n" +
                "\t function sliderErClick(score){ \n" +
                "\t\t window.WebViewJavascriptBridge.callHandler( \n" +
                "\t\t\t\"sliderErClick\",score\n" +
                "\t\t\t,function(responseData){});\n" +
                "\t}"+
                "\t//客户端点击关闭\n" +
                "\tfunction clickClose(){\n" +
                "\t\twindow.WebViewJavascriptBridge.callHandler(\n" +
                "\t\t\t\"clickClose\",function(responseData){});\n" +
                "\t}"+
                "function toActivity(param){\n" +
                "var av=document.getElementsByClassName(\"qs-c-input\").item(0).value;"+
                "window.js.jumpActivity(av);" +
                "}"+
                "$('html,body').animate({\n" +
                "            \tscrollTop: $(\".ckjx\").offset().top - 60\n" +
                "        \t}, 100);\n"+
                "    </script>";

        //这个模块题海查看解析，加样式
        public static String zgjiexi="<li class='single-cs'><input type='button' value='查看解析' onclick='parsingClick()' class='ckjx'/></li>";
        //这个真题查看解析，加样式
        public static String zgjiexi_Er="<li class='single-cs'><input type='button' value='查看解析' onclick='parsingErClick()' class='ckjx'/></li>";
       public static String choiceStyleNormal_Prefix = "<ul class='qs-c-list qs-choose' id='exam-cs-list'>";
       public static String dati="<li class='single-cs'><span class='the-index'>答案：</span><input type='text' value='%1$s' class='qs-c-input'/></li>";
      //1$s 选项（A，B，C，D） 2$s 选项样式 （choiceClassName决定样式，被选择是什么样式，没被选择是什么样式）3$s 选项内容（A.gongchang B.awohdoad C.wadawo D.awdawiojdaw）
      public static String choiceStyleNormal = "<li id='%1$s' onclick='choose(\"%1$s\")' class='%2$s'>" +
                    "<span class='the-index'>%1$s</span>" +
                    "%3$s" +
                    "</li>";
    public static String choiceStyleNormal_suffix = "</ul>";
    public static String[] choiceClassname = {"r-cs", "w-cs", "u-choose", "", "single-cs", "r-cs-single", "w-cs-single", "u-choose-single"};
    //1$s正确单  2$s 选择答案
    public static String answerStyle_right = "<div class='correct-answer'>" +
            "正确答案是 <strong class='correct'>%1$s</strong>，你的答案是 <strong class='yours'>%2$s</strong>,回答正确" +
            "</div>";
    public static String answerStyle_wrong = "<div class='correct-answer'>" +
            "正确答案是 <strong class='correct'>%1$s</strong>，你的答案是 <strong class='yours'>%2$s</strong>,回答错误" +
            "</div>";

    //视频解析
    public static String videoAnalysisStyle = "<div class='answer-intro live-intro' onclick='videoclick()'>" +
            "<h3>视频解析</h3>" +
            "<div class='txt'>" +
            "<span>" +
            "%1$s" +
            "</span>" +
            "</div>" +
            "</div>";

    //答案解析 1$s
    public static String answerAnalysisStyle = "<div class='answer-intro' style=\"background-color:#f4f4f4\">" +
            "<h3>答案解析</h3>" +
            "<div class='txt'>" +
            "%1$s" +
            "</div>" +
            "</div>";
//        //答案解析 主观题的
//        public static String answerAnalyzgtyle = "<div class='answer-intro'>" +
//                "<h3>答案解析</h3>" +
//                "<div class='txt'>" +
//                "%1$s" +
//                "</div>" +
//                "</div>";

    //1$s正确单  2$s 选择答案   3$s作答次数  4$准确率
    public static String answerStyle_more_right = "<div class='correct-answer'>" +
            "正确答案是 <strong class='correct'>%1$s</strong>，你的答案是 <strong class='yours'>%2$s</strong>,回答正确。</br>" +
            "</div>";
    //        "本题共作答<strong class='correct'>%3$s</strong>次，准确率<strong class='correct'>%4$s</strong>。" +
    public static String answerStyle_more_wrong = "<div class='correct-answer'>" +
            "正确答案是 <strong class='correct'>%1$s</strong>，你的答案是 <strong class='yours'>%2$s</strong>,回答错误。</br>" +
            "</div>";
    //        "本题共作答<strong class='correct'>%3$s</strong>次，准确率<strong class='correct'>%4$s</strong>。" +
    public static String table = "<h3 class='h3'>%1$s</h3>" +
            "<table  width=90% border=0 cellspacing=0 cellpadding=0 class='table1 tabxmht' style='margin:0 auto;'>" +
            "<thead>" +
            "<th>%2$s</th>" +
            "<th>%3$s</th>" +
            "<th>%4$s</th>" +
            "</thead>" +
            "<tbody>" +
            "<tr>" +
            "<td>%5$s</td>" +
            "<td>%6$s</td>" +
            "<td>%7$s</td>" +
            "</tr>" +
            "</tbody>" +
            "" +
            "</table>";
    public static String regix = "<(?!img)[^>]*>";
    public static final String mimeType = "text/html";
    public static final String encoding = "utf-8";


        public static String buzhichi = "<div class=\"mrtitle\">%1$s</div>\n";
        //主观题div
        public static String estimate_div = "<div class=\"mrtitle\" style=\"display:none;\">%1$s</div>\n" +
                "            <div id=\"rangeDiv\">\n" +
                "                <input id=\"bgRange\" type=\"range\" min=\"0\" max=\"100\" value=\"1\" step=\"0\" style=\"top: 47.5px;\">"+
                "                <input id=\"scoreRange\" type=\"range\" min=\"0\" max=\"%2$s\" value=\"%3$s\" step=\"0.5\" style=\"top:40px;\" oninput=\"doChange" +
                "()\" onchange=\"doChange()\">\n" +
                "                <div id=\"showRangeValueBgDiv\">\n" +
                "                    <span id=\"showRangeValue\">滑动估分</span> \n" +
                "                </div>\n" +
                "            </div>"+
                "<div class='answer-intro' style=\"\">\n" +
                "\t<div class=\"tab\">\n" +
                "\t\t<label class=\"taba sc-color\" >解析</label>\n" +
                "\t\t<label class=\"taba\" >评分标准</label>\n" +
                "\t\t<div class=\"myclose\" ></div>\n" +
                "\t</div>\n" +
                "\t<div class='txt tabcontent'>%4$s</div>\n" +
                "\t<div class=\"pfbz tabcontent\" style=\"display:none;\">%5$s</div>\n" +
                "</div>\n" +
                "</div>";
        public static final String scorllEstimate = "<script>\n"+
                "$('html,body').animate({\n"+
                "            \tscrollTop: $(\".ckjx\").offset().top - 60\n"+
                "        \t}, 100)</script>";
}
