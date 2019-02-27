package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class GreenDaoAuto {
    public static void main(String[] args) throws Exception {
        /**
         * 第一个参数 为版本
         * 第二个参数为 dao所在package
         */
        Schema schema = new Schema(1, "com.greendao");
//        schema.setDefaultJavaPackageTest("com.greendao.test");
//        schema.setDefaultJavaPackageDao("com.greendao.dao");
//        schema.enableKeepSectionsByDefault();

//        addQustion(schema);

        addPlans(schema);
        new DaoGenerator().generateAll(schema, "./");
    }

    private static void addNote(Schema schema, String tableName) {
        //Entity代表一个表，创建的表名默认是 传入的参数 ->note
        Entity note = schema.addEntity(tableName);
        note.addIdProperty().primaryKey().autoincrement();//设置自增长id为主键
        note.addStringProperty("infoType").notNull();//非null字段 插入数据时，该字段为null 则插入不成功
    }

    private static void addQustion(Schema shema) {
        Entity question = shema.addEntity("Question");
        question.addStringProperty("answers");
        question.addStringProperty("area");
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        user.setTableName("t_user");
        user.addIdProperty();
        user.addStringProperty("account").unique();
        user.addStringProperty("password");
        user.addDateProperty("birthday");
        user.addShortProperty("gender");
        user.addIntProperty("height");
        user.addFloatProperty("weight");
        user.addDateProperty("registerTime");
        user.implementsInterface("Jsonable<User>");
    }

    private static void addCustomerOrder(Schema schema) {
        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name").notNull();

        Entity order = schema.addEntity("Order");
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
        order.addIdProperty();//添加id主键
        Property orderDate = order.addDateProperty("date").getProperty();
        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
        order.addToOne(customer, customerId);//customid->order表的外键,customer表的主键

        ToMany customerToOrders = customer.addToMany(order, customerId);
        customerToOrders.setName("orders");//设计orderlist对应的字段名称
        customerToOrders.orderAsc(orderDate);
    }

    private static void addPlans(Schema schema) {
        /**
         * 学习记录
         */
        Entity studyRecords = schema.addEntity("StudyRecords");
        studyRecords.addIdProperty().primaryKey().autoincrement();
        studyRecords.addStringProperty("cid");//模块题海或者今日特训 则为知识点id;真题测评 则为试卷id
        studyRecords.addStringProperty("name");//知识点名称
        studyRecords.addStringProperty("eids");//本次练习知识点下习题id集合
        studyRecords.addDateProperty("date");//本次练习对应生成时间
        studyRecords.addStringProperty("currentprogress");//本次练习对应进度
        studyRecords.addStringProperty("completed");//本次练习是否完成 1表示完成
        studyRecords.addStringProperty("choicesforuser");//用户选项
        studyRecords.addStringProperty("userid");//用户id
        studyRecords.addStringProperty("type").notNull();//0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
        studyRecords.addStringProperty("today");//1当天计划 0非当天计划 -》只针对 今日特训
//        studyRecords.addStringProperty("choosecategory");//类型+地域+学段+科目
        studyRecords.addIntProperty("exercisenum");//本次练习总题数
        studyRecords.addStringProperty("isdelete");//1表示删除 不显示 0表示未删除 显示
        studyRecords.addIntProperty("usedtime");//使用时间
        studyRecords.addIntProperty("rightnum");//本次练习做对题数
        studyRecords.addIntProperty("lastprogress");//-》模块题海 做题进度
        studyRecords.addStringProperty("ptimelimit");//推荐时间 -》只针对真题测评
        Index index = new Index();
        Property property1 = studyRecords.addStringProperty("choosecategory").getProperty();//类型+地域+学段+科目
        index.addProperty(property1);
        studyRecords.addIndex(index);

        /**
         * 学段对应题包下载状况
         */
        Entity exerciseDownloadPackage = schema.addEntity("ExerciseDownloadPackage");
        exerciseDownloadPackage.addStringProperty("id");//模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷id
        exerciseDownloadPackage.addStringProperty("downloadstatus");//该题包是否下载 1表示已下载
        exerciseDownloadPackage.addStringProperty("versions");//该题包版本

        /**
         * 习题info表
         */
        Entity questionDetail = schema.addEntity("QuestionDetail");
//        questionDetail.addStringProperty("id");//模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷id
        questionDetail.addStringProperty("choices");//选项
//        questionDetail.addStringProperty("qid");//题号
        questionDetail.addStringProperty("answers");//答案
        questionDetail.addStringProperty("author");//作者
        questionDetail.addStringProperty("comment");//解析
        questionDetail.addStringProperty("question");//习题内容
        questionDetail.addStringProperty("qpoint");//所属知识点
        questionDetail.addStringProperty("qrootpoint");//所属根知识点
        questionDetail.addStringProperty("content");//习题内容题干
        questionDetail.addStringProperty("score");//习题分数
        questionDetail.addStringProperty("qvideo");//视频解析数据，数据格式 视频ID###UID###KEY
        questionDetail.addStringProperty("qtype");//习题类型
        questionDetail.addStringProperty("extension");//拓展 评分标准
        questionDetail.addStringProperty("stateQuestion");; //试题状态 0正常 -1删除 1更新 包含-1E 表示错题中心不显示 -1C表示收藏中心不显示-1M表示模块题海不显示
        Index index2 = new Index();
        Property property11 = questionDetail.addStringProperty("id").getProperty();//类型+地域+学段+科目
        Property property22 = questionDetail.addStringProperty("qid").getProperty();//题号
        index2.addProperty(property11);
        index2.addProperty(property22);
        questionDetail.addIndex(index2);

        /**
         * 习题收藏表
         */
        Entity storeExercise = schema.addEntity("ExerciseStore");
        storeExercise.addStringProperty("qid").primaryKey();//收藏的题号
        storeExercise.addStringProperty("userid");

        /**
         * 考试科目表
         */
        Entity examSubject = schema.addEntity("ExamSubject");
        examSubject.addStringProperty("id").primaryKey();//类型+地域+学段:选择国家后，id只保存类型；选择地区后，id保存类型+地区+学段
        examSubject.addStringProperty("jsonforsubject");
        examSubject.addStringProperty("version");

        /**
         * 章节树表
         */
        Entity chapterTree = schema.addEntity("ChapterTree");
        chapterTree.addStringProperty("id").primaryKey();//类型+地域+学段+科目
        chapterTree.addStringProperty("chaptertree");
        chapterTree.addStringProperty("version");//题库版本号

        /**
         * 下载视频记录
         */
        Entity downVideoRecords = schema.addEntity("DirectBean");
        downVideoRecords.addIdProperty().primaryKey().autoincrement();
        downVideoRecords.addStringProperty("rid");//课程id
        downVideoRecords.addStringProperty("TeacherDesc");//老师
        downVideoRecords.addStringProperty("Title");//直播标题
        downVideoRecords.addStringProperty("classTitle");//课程标题
        downVideoRecords.addStringProperty("lessionCount");//课时
        downVideoRecords.addStringProperty("price");//价格  老版接口
        downVideoRecords.addDoubleProperty("ActualPrice");//价格  新版接口
        downVideoRecords.addStringProperty("scaleimg");//图片地址
        downVideoRecords.addStringProperty("buy_lives");//购买人数
        downVideoRecords.addStringProperty("is_fufei");//1 付费 0 免费
        downVideoRecords.addStringProperty("content");//详情 webview加载
        downVideoRecords.addStringProperty("is_zhibo");//0 往期 1 直播 2 即将开始直播 -1课程已下线
        downVideoRecords.addStringProperty("passwd");//直播口令
        downVideoRecords.addStringProperty("yuming");//直播域名
        downVideoRecords.addStringProperty("number");//房间号
        downVideoRecords.addStringProperty("liveid");//点播id
        downVideoRecords.addStringProperty("video_status");//直播状态 0 正在直播 ，1点播地址未生成 ， 2可以点播，3，直播未开始 4正在播放
        downVideoRecords.addStringProperty("riqi");//播放日期
        downVideoRecords.addStringProperty("is_buy");//1购买过 0未购买
        downVideoRecords.addStringProperty("timeLength");//视频时长
        downVideoRecords.addStringProperty("zhibotime");//直播时间
        downVideoRecords.addStringProperty("zhiboendtime");//直播结束时间
        downVideoRecords.addStringProperty("kouling");//录播口令
        downVideoRecords.addStringProperty("zhibourl");//录播口令
        downVideoRecords.addStringProperty("lubourl");//录播ID
        downVideoRecords.addStringProperty("EffectDateDesc");//有效时间
        downVideoRecords.addStringProperty("photo_url");//老师介绍图片
        downVideoRecords.addStringProperty("Brief");//课程介绍图片
        downVideoRecords.addStringProperty("phaseName");//
        downVideoRecords.addStringProperty("ClassNo");//课程编号
        downVideoRecords.addStringProperty("is_living");//是否正在直播
        downVideoRecords.addStringProperty("TypeName");//班次类型
        downVideoRecords.addIntProperty("seq");//当前播放
        downVideoRecords.addStringProperty("isHasJy");//是否有讲义
        downVideoRecords.addIntProperty("videoType");//直播类型 1 是直播 0 网课
        downVideoRecords.addStringProperty("ccUid");//cc视频uid
        downVideoRecords.addStringProperty("ccApi_key");//cc视频Api_key
        downVideoRecords.addStringProperty("ccCourses_id");//cc视频id
        downVideoRecords.addIntProperty("isTrial");//是否试听
        downVideoRecords.addStringProperty("NetClassId");//课程的Rid
        downVideoRecords.addStringProperty("netclass_pdf");//pdf地址
        downVideoRecords.addStringProperty("is_ax_Type");//是否可激活
        downVideoRecords.addStringProperty("localPath");//下载到本地的地址
        downVideoRecords.addStringProperty("userid");//下载的用户
        downVideoRecords.addStringProperty("errorcode");//下载错误信息
        downVideoRecords.addLongProperty("start");//cc下载位置
        downVideoRecords.addLongProperty("end");//cc下载总大小
        downVideoRecords.addLongProperty("position");//gensse下载进度位置 最大100
        downVideoRecords.addStringProperty("down_status");//下载状态 100 下载等待 200 开始下载 300 下载暂停 400 下载完成 600 等待下载
        downVideoRecords.addIntProperty("footprint");//足迹 0没有；1有 2我的课程（为了缓存离线观看视频） 3已购买课程课程表列表（为了缓存离线观看视频）
        downVideoRecords.addIntProperty("status");//出售状态 0下架；1在售
        downVideoRecords.addStringProperty("camera");//是否开启人像模式 0不开启 1开启
        downVideoRecords.addStringProperty("customer");//乐语咨询地址
        downVideoRecords.addStringProperty("hd");//是否高清视频
        downVideoRecords.addStringProperty("oid");//订单id
        downVideoRecords.addStringProperty("is_next_day");//24小时直播是否下一天的 1 是 0否
        downVideoRecords.addStringProperty("pic");//播放未开始时默认图片
        downVideoRecords.addStringProperty("Common_problem");//常见问题
        downVideoRecords.addIntProperty("course");//我的直播课 1有课程信息与通知 0没有
        downVideoRecords.addBooleanProperty("is_last");//教师评价弹窗
        downVideoRecords.addStringProperty("bjytoken");//百家云播放token
        downVideoRecords.addStringProperty("bjyvideoid");//百家云视频vid


        downVideoRecords.addStringProperty("room_id");//百家云直播房间id
        downVideoRecords.addStringProperty("session_id");// 百家云回放的序列号
        downVideoRecords.addStringProperty("student_code");// 百家云直播参加码
        downVideoRecords.addStringProperty("bjyhftoken");// 百家云回放token

//        private String is_next_day;//是否下一天的 1 是 0否
//        private String pic;//播放未开始时默认图片
//        private String Common_problem;//常见问题
//        private int course;// 我的直播课 1有课程信息与通知 0没有
//        private boolean is_last = false;//教师评价弹窗
//        private String bjytoken;//百家云token
//        private String bjyvideoid;//百家云vid

    }
}
