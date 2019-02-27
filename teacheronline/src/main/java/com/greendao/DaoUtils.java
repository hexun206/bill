package com.greendao;

import android.database.sqlite.SQLiteConstraintException;

import com.google.gson.Gson;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by ljzyuhenda on 15/10/26.
 */
public class DaoUtils {
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static boolean LOG_SQL = true;
    public static boolean LOG_VALUES = true;
    private static DaoUtils instance;
    //exerciseDownloadPackage表 下载信息储存表
    private ExerciseDownloadPackageDao mExerciseDownloadPackageDao;
    private Query<ExerciseDownloadPackage> mExerciseDownloadPackageQuery;
    private DeleteQuery<ExerciseDownloadPackage> mExerciseDownloadPackageDeleteQuery;
    //QuestionDetails表 习题信息储存表
    private QuestionDetailDao mQuestionDetailDao;
    private DeleteQuery<QuestionDetail> mQuestionDetailDeleteQuery;
    private DeleteQuery<QuestionDetail> mQuestionDetailDeleteQidQuery;
    private Query<QuestionDetail> mQuestionDetailQuery;
    private Query<QuestionDetail> mQuestionDetailQueryById;
    //StudyRecords表 学习记录表
    private StudyRecordsDao mStudyRecordsDao;
    private Query<StudyRecords> mStudyRecordsQuery;
    private Query<StudyRecords> mStudyRecordsQueryById;
    private Query<StudyRecords> mStudyRecordQueryByType;
    private Query<StudyRecords> mStudyRecordQueryByUserId;
    private Query<StudyRecords> mStudyRecordQueryByChooseCategory;
    private Query<StudyRecords> mStudyRecordQueryForAll;
    //ExerciseStore表 习题收藏表
    private ExerciseStoreDao mExerciseStoreDao;
    private Query<ExerciseStore> mExerciseStoreQuery;
    // 考试科目表
    private ExamSubjectDao mExamSubjectDao;
    private Query<ExamSubject> mExamSubjectQueryById;
    //章节树表
    private ChapterTreeDao mChapterTreeDao;
    private Query<ChapterTree> mChapterTreeQuery;
    //视频下载记录
    private DirectBeanDao mDirectBeanDao;
    private Query<DirectBean> mDirectBeanQuery;
    private DeleteQuery<DirectBean> mDirectBeanDeleteQuery;
    private DeleteQuery<DirectBean> mDirectBeanDeleteHisQuery;
    private DeleteQuery<DirectBean> mDirectBeanDeleteClassQuery;
    private DeleteQuery<DirectBean> deletDirectBeanListForRid;
    private Query<DirectBean> mDirectBeanQueryByCCVedioId;
    private Query<DirectBean> mDirectBeanQueryByBjyVedioId;
    private Query<DirectBean> mDirectBeanQueryByGeeneVedioId;
    private Query<DirectBean> mDirectBeanHistoryByUserid;
    private Query<DirectBean> mDirectBeanCacheByUserid;
    private Query<DirectBean> mDirectBeanCacheClassByrid;

    private DaoUtils() {
    }

    public static DaoUtils getInstance() {
        if (instance == null) {
            synchronized (AsyncHttpClient.class) {
                if (instance == null) {
                    instance = new DaoUtils();
                    DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(CustomApplication.applicationContext, "huatudb", null);
                    instance.mDaoMaster = new DaoMaster(helper.getWritableDatabase());
                    instance.mDaoSession = instance.mDaoMaster.newSession();
                }
            }
        }

        return instance;
    }

    private static String getUserId() {
        return "ajb";
    }

    public DaoMaster getDaoMaster() {

        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /////////=============ExerciseDownloadPackage表 操作=================///////////////
    private void checkExerciseDownloadPackageDao() {
        if (mExerciseDownloadPackageDao == null) {
            mExerciseDownloadPackageDao = mDaoSession.getExerciseDownloadPackageDao();
        }
    }

    /**
     * 查询题包下载信息
     *
     * @param id 模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷id
     * @return
     */
    public ExerciseDownloadPackage queryExerciseDownloadPackageInfo(String id) {
        checkExerciseDownloadPackageDao();
        if (mExerciseDownloadPackageQuery == null) {
            QueryBuilder<ExerciseDownloadPackage> ExerciseDownloadPackage = mExerciseDownloadPackageDao.queryBuilder().where(ExerciseDownloadPackageDao.Properties.Id.eq(id));
            QueryBuilder queryBuilder = ExerciseDownloadPackage;
            mExerciseDownloadPackageQuery = queryBuilder.build();
        }

        Query<ExerciseDownloadPackage> query = mExerciseDownloadPackageQuery.forCurrentThread();
        query.setParameter(0, id);

        return query.unique();
    }

    /**
     * 更新题包下载信息
     *
     * @param id 模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷id
     */
    public void inserOrUpdateExerciseDownloadPackageInfo(String id, String versions) {
        checkExerciseDownloadPackageDao();

        deleteExerciseDownloadPackageInfo(id);
        ExerciseDownloadPackage exerciseDownloadPackage = new ExerciseDownloadPackage(id, "1", versions);
        mExerciseDownloadPackageDao.insert(exerciseDownloadPackage);
    }

    /**
     * 删除题包下载信息
     *
     * @param id 模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷id
     */
    public void deleteExerciseDownloadPackageInfo(String id) {
        checkExerciseDownloadPackageDao();

        if (mExerciseDownloadPackageDeleteQuery == null) {
            QueryBuilder queryBuilder = mExerciseDownloadPackageDao.queryBuilder().where(ExerciseDownloadPackageDao.Properties.Id.eq(id));

            mExerciseDownloadPackageDeleteQuery = queryBuilder.buildDelete();
        }

        DeleteQuery deleteQuery = mExerciseDownloadPackageDeleteQuery.forCurrentThread();
        deleteQuery.setParameter(0, id);

        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    /////////=============ExerciseDownloadPackage表 操作=================///////////////
    private void checkQuestionDetailDao() {
        if (mQuestionDetailDao == null) {
            mQuestionDetailDao = mDaoSession.getQuestionDetailDao();
        }
    }

    /**
     * 插入习题信息
     */
    public void insertQuestionDetail(QuestionDetail questionDetail) {
        checkQuestionDetailDao();

        Gson gson = new Gson();
        questionDetail.setAnswers(gson.toJson(questionDetail.answersArr, String[].class));
        questionDetail.setQrootpoint(gson.toJson(questionDetail.qrootpointArr, String[].class));
        questionDetail.setQpoint(gson.toJson(questionDetail.qpointArr, String[].class));
        questionDetail.setChoices(gson.toJson(questionDetail.choicesArr, String[].class));
        mQuestionDetailDao.insert(questionDetail);
    }

    /**
     * 插入或者更新习题信息1
     */
    public void insertOrUpdateQuestionDetail(QuestionDetail questionDetail) {
        checkQuestionDetailDao();
        QuestionDetail questionDetail1 = queryQuestionDetail(questionDetail.getQid(), questionDetail.getId());
        if (questionDetail1 != null) {
            Gson gson = new Gson();
            questionDetail.setAnswers(gson.toJson(questionDetail.answersArr, String[].class));
            questionDetail.setQrootpoint(gson.toJson(questionDetail.qrootpointArr, String[].class));
            questionDetail.setQpoint(gson.toJson(questionDetail.qpointArr, String[].class));
            questionDetail.setChoices(gson.toJson(questionDetail.choicesArr, String[].class));
            mQuestionDetailDao.insert(questionDetail);
        } else {
            DebugUtil.e("insertOrUpdateQuestionDetail" + questionDetail.toString());
            mQuestionDetailDao.update(questionDetail);
        }
    }

    /**
     * 插入或者更新习题信息2
     */
    public void insertOrUpdateQuestionDetail2(QuestionDetail questionDetail) {
        checkQuestionDetailDao();
        deleteQuestionDetailForQid(questionDetail.getQid(), questionDetail.getId());
        Gson gson = new Gson();
        questionDetail.setAnswers(gson.toJson(questionDetail.answersArr, String[].class));
        questionDetail.setQrootpoint(gson.toJson(questionDetail.qrootpointArr, String[].class));
        questionDetail.setQpoint(gson.toJson(questionDetail.qpointArr, String[].class));
        questionDetail.setChoices(gson.toJson(questionDetail.choicesArr, String[].class));
        DebugUtil.e("insertOrUpdateQuestionDetail2:" + questionDetail.toString());
        mQuestionDetailDao.insert(questionDetail);
    }

    /**
     * 根据qid删除题
     *
     * @param qid
     */
    private void deleteQuestionDetailForQid(String qid, String id) {
        checkQuestionDetailDao();
        if (mQuestionDetailDeleteQidQuery == null) {
            QueryBuilder queryBuilder = mQuestionDetailDao.queryBuilder().where(QuestionDetailDao.Properties.Qid.eq(qid), QuestionDetailDao.Properties.Id.eq(id));
            mQuestionDetailDeleteQidQuery = queryBuilder.buildDelete();
        }
        DeleteQuery<QuestionDetail> query = mQuestionDetailDeleteQidQuery.forCurrentThread();
        query.setParameter(0, qid);
        query.setParameter(1, id);
        query.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据id删除习题
     *
     * @param id 模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷pid
     */
    public void deleteQuestionDetail(String id) {
        checkQuestionDetailDao();

        if (mQuestionDetailDeleteQuery == null) {
            QueryBuilder queryBuilder = mQuestionDetailDao.queryBuilder().where(QuestionDetailDao.Properties.Id.eq(id));
            mQuestionDetailDeleteQuery = queryBuilder.buildDelete();
        }

        DeleteQuery<QuestionDetail> query = mQuestionDetailDeleteQuery.forCurrentThread();
        query.setParameter(0, id);
        query.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据id 和习题题号 查询该习题-》针对模块题海或者是今日特训
     *
     * @param qid 习题题号
     * @param id  模块题海或者今日特训 ->类型+地域+学段+科目; 真题测评 -> 试卷pid
     */
    public QuestionDetail queryQuestionDetail(String qid, String id) {
        checkQuestionDetailDao();

        if (mQuestionDetailQuery == null) {
            QueryBuilder queryBuilder = mQuestionDetailDao.queryBuilder().where(QuestionDetailDao.Properties.Qid.eq(qid), QuestionDetailDao.Properties.Id.eq
                    (id));
            mQuestionDetailQuery = queryBuilder.build();
        }

        Query<QuestionDetail> query = mQuestionDetailQuery.forCurrentThread();
        query.setParameter(0, qid);
        query.setParameter(1, id);

        if (query.list() != null && query.list().size() > 0) {
            return query.list().get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据试卷id查询所有试题-》真题测评
     *
     * @param id 试卷id
     * @return
     */
    public List<QuestionDetail> queryQuestionDetails(String id) {
        checkQuestionDetailDao();

        if (mQuestionDetailQueryById == null) {
            QueryBuilder queryBuilder = mQuestionDetailDao.queryBuilder().where(QuestionDetailDao.Properties.Id.eq(id));
            mQuestionDetailQueryById = queryBuilder.build();
        }
        Query<QuestionDetail> query = mQuestionDetailQueryById.forCurrentThread();
        query.setParameter(0, id);
        return query.list();
    }

    /////////=============StudyRecordsDao表操作=================///////////////
    private void checkSelfMakeExerciseDao() {
        if (mStudyRecordsDao == null) {
            mStudyRecordsDao = mDaoSession.getStudyRecordsDao();
        }
    }

    /**
     * 插入学习记录 此时today标志为1
     *
     * @param studyRecords
     */
    public long insertStudyRecord(StudyRecords studyRecords) {
        checkSelfMakeExerciseDao();
        DebugUtil.e("insertStudyRecord:" + studyRecords.toString());
        return mStudyRecordsDao.insert(studyRecords);
    }

    /**
     * 删除学习记录
     *
     * @param studyRecords
     */
    public void deleteStudyRecord(StudyRecords studyRecords) {
        checkSelfMakeExerciseDao();

        mStudyRecordsDao.delete(studyRecords);
    }

    /**
     * 根据id更新学习记录
     *
     * @param studyRecords
     */
    public void updateStudyRecord(StudyRecords studyRecords) {
        checkSelfMakeExerciseDao();

        mStudyRecordsDao.update(studyRecords);
    }

    /**
     * 根据userid、chooseCategory查询今日特训的记录
     *
     * @param today
     * @param chooseCategory
     * @param userId
     * @return
     */
    public List<StudyRecords> queryStudyRecordsForToday(String today, String chooseCategory, String userId) {
        checkSelfMakeExerciseDao();

        if (mStudyRecordsQuery == null) {
            QueryBuilder queryBuilder = mStudyRecordsDao.queryBuilder().where(StudyRecordsDao.Properties.Today.eq(today),
                    StudyRecordsDao.Properties.Choosecategory.eq(chooseCategory), StudyRecordsDao.Properties.Userid.eq(userId),
                    StudyRecordsDao.Properties.Type.eq("0"));

            mStudyRecordsQuery = queryBuilder.build();
        }

        Query<StudyRecords> query = mStudyRecordsQuery.forCurrentThread();
        query.setParameter(0, today);
        query.setParameter(1, chooseCategory);
        query.setParameter(2, userId);

        return query.list();
    }

    /**
     * 根据id查询学习记录
     *
     * @param id
     * @return
     */
    public StudyRecords queryStudyRecords(long id) {
        checkSelfMakeExerciseDao();

        if (mStudyRecordsQueryById == null) {
            QueryBuilder queryBuilder = mStudyRecordsDao.queryBuilder().where(StudyRecordsDao.Properties.Id.eq(id));

            mStudyRecordsQueryById = queryBuilder.build();
        }

        Query<StudyRecords> query = mStudyRecordsQueryById.forCurrentThread();
        query.setParameter(0, id);

        return query.unique();
    }

    /**
     * @return
     */
    public List<StudyRecords> queryStudyRecordsForAll() {
        checkSelfMakeExerciseDao();

        if (mStudyRecordQueryForAll == null) {
            QueryBuilder queryBuilder = mStudyRecordsDao.queryBuilder().where(StudyRecordsDao.Properties.Isdelete.notEq("1"),
                    StudyRecordsDao.Properties.Type.in("0", "1", "2", "5")).orderDesc(StudyRecordsDao.Properties.Date);


            mStudyRecordQueryForAll = queryBuilder.build();
        }

        Query<StudyRecords> query = mStudyRecordQueryForAll.forCurrentThread();
        return query.list();
    }

    /**
     * 查询用户对应的学习记录
     *
     * @param userid 用户id
     * @return
     */
    public List<StudyRecords> queryStudyRecordsByUserId(String userid) {
        checkSelfMakeExerciseDao();

        if (mStudyRecordQueryByUserId == null) {
            QueryBuilder queryBuilder = mStudyRecordsDao.queryBuilder().where(
                    StudyRecordsDao.Properties.Userid.eq(userid), StudyRecordsDao.Properties.Isdelete.notEq("1")).orderDesc(StudyRecordsDao.Properties.Date);

            mStudyRecordQueryByUserId = queryBuilder.build();
        }
        Query<StudyRecords> query = mStudyRecordQueryByUserId.forCurrentThread();
        query.setParameter(0, userid);
        return query.list();
    }

    /**
     * 查询模块题海的学习记录 每个知识点最多对应一个学习记录
     *
     * @param cid            当type为 1模块题海时, cid为知识点id
     *                       快速组卷 cid -> 为知识点id + quick
     *                       2真题测评时, cid为试题id
     *                       3错题中心, cid为知识点id
     *                       4收藏, cid为知识点id
     *                       5模考大赛, cid为试题id
     *                       type////0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
     * @param chooseCategory 类型+地域+学段+科目
     * @param userid         用户id
     * @return
     */
    public StudyRecords queryStudyRecordsByType(String type, String cid, String chooseCategory, String userid) {
        checkSelfMakeExerciseDao();

        if (mStudyRecordQueryByType == null) {
            QueryBuilder queryBuilder = mStudyRecordsDao.queryBuilder().where(StudyRecordsDao.Properties.Type.eq(type),
                    StudyRecordsDao.Properties.Cid.eq(cid), StudyRecordsDao.Properties.Choosecategory.eq(chooseCategory),
                    StudyRecordsDao.Properties.Userid.eq(userid));

            mStudyRecordQueryByType = queryBuilder.build();
        }
        DebugUtil.e("type:" + type + " cid:" + cid + " chooseCategory:" + chooseCategory + " userid:" + userid);
        Query<StudyRecords> query = mStudyRecordQueryByType.forCurrentThread();
        query.setParameter(0, type);
        query.setParameter(1, cid);
        query.setParameter(2, chooseCategory);
        query.setParameter(3, userid);
        for (int i = 0; i < query.list().size(); i++) {
            DebugUtil.e("queryStudyRecordsByType:" + query.list().get(i));
        }
        if (query.list().size() > 0) {
            return query.list().get(0);
        } else {
            return null;
        }
//        return query.unique();
    }

    /**
     * 根据类型+地域+学段+科目查找学习记录（主要用于模块题海更新题包删除学习记录）
     *
     * @param type           0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
     * @param chooseCategory 类型+地域+学段+科目
     * @param userid         用户id
     */
    public List<StudyRecords> queryStudyRecordsByChooseCategory(String type, String chooseCategory, String userid) {
        checkSelfMakeExerciseDao();

        if (mStudyRecordQueryByChooseCategory == null) {
            QueryBuilder queryBuilder = mStudyRecordsDao.queryBuilder().where(StudyRecordsDao.Properties.Type.eq(type)
                    , StudyRecordsDao.Properties.Choosecategory.eq(chooseCategory),
                    StudyRecordsDao.Properties.Userid.eq(userid));

            mStudyRecordQueryByChooseCategory = queryBuilder.build();
        }
        DebugUtil.e("type:" + type + " chooseCategory:" + chooseCategory + " userid:" + userid);
        Query<StudyRecords> query = mStudyRecordQueryByChooseCategory.forCurrentThread();
        query.setParameter(0, type);
        query.setParameter(1, chooseCategory);
        query.setParameter(2, userid);
        return query.list();
    }

    /////////=============ExerciseStore表 操作=================///////////////
    private void checkExerciseStoreDao() {
        if (mExerciseStoreDao == null) {
            mExerciseStoreDao = mDaoSession.getExerciseStoreDao();
        }
    }

    /**
     * 插入收藏数据
     *
     * @param exerciseStore
     */
    public void insertExerciseStore(ExerciseStore exerciseStore) {
        checkExerciseStoreDao();

        mExerciseStoreDao.insert(exerciseStore);
    }

    /**
     * 查询该记录是否存在
     *
     * @param qid    习题题号
     * @param userid 用户id
     * @return
     */
    public boolean queryExerciseStore(String qid, String userid) {
        checkExerciseStoreDao();

        if (mExerciseStoreQuery == null) {
            QueryBuilder queryBuilder = mExerciseStoreDao.queryBuilder().where(ExerciseStoreDao.Properties.Qid.eq(qid), ExerciseStoreDao.Properties.Userid.eq
                    (userid));
            mExerciseStoreQuery = queryBuilder.build();
        }

        Query<ExerciseStore> query = mExerciseStoreQuery.forCurrentThread();
        query.setParameter(0, qid);
        query.setParameter(1, userid);

        if (query.unique() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteExerciseStore(ExerciseStore exerciseStore) {
        checkExerciseStoreDao();

        mExerciseStoreDao.delete(exerciseStore);
    }

    /////////=============ExamSubjectDao表操作=================///////////////
    private void checkExamSubjectDao() {
        if (mExamSubjectDao == null) {
            mExamSubjectDao = mDaoSession.getExamSubjectDao();
        }
    }

    /**
     * 插入考试科目信息
     */
    public void insertExamSubject(ExamSubject examSubject) {
        checkExamSubjectDao();
        // 先删除再插入
        deleteExamSubject(examSubject);
        mExamSubjectDao.insert(examSubject);
    }

    /**
     * 删除考试科目信息
     */
    public void deleteExamSubject(ExamSubject examSubject) {
        checkExamSubjectDao();
        mExamSubjectDao.delete(examSubject);
    }

    /**
     * 根据id查询该考试科目列表
     *
     * @param id 类型+地域+学段
     */
    public ExamSubject queryExamSubject(String id) {
        checkExamSubjectDao();

        if (mExamSubjectQueryById == null) {
            QueryBuilder queryBuilder = mExamSubjectDao.queryBuilder().where(ExamSubjectDao.Properties.Id.eq(id));
            mExamSubjectQueryById = queryBuilder.build();
        }

        Query<ExamSubject> query = mExamSubjectQueryById.forCurrentThread();
        query.setParameter(0, id);

        return query.unique();
    }

    /////////=============ChapterTree表操作=================///////////////
    private void checkChapterTreeDao() {
        if (mChapterTreeDao == null) {
            mChapterTreeDao = mDaoSession.getChapterTreeDao();
        }
    }

    /**
     * 根据id查询章节树信息
     *
     * @param id 类型+地域+学段+科目
     * @return
     */
    public ChapterTree queryChapterTree(String id) {
        checkChapterTreeDao();

        if (mChapterTreeQuery == null) {
            QueryBuilder queryBuilder = mChapterTreeDao.queryBuilder().where(ChapterTreeDao.Properties.Id.eq(id));
            mChapterTreeQuery = queryBuilder.build();
        }

        Query<ChapterTree> query = mChapterTreeQuery.forCurrentThread();
        query.setParameter(0, id);

        return query.unique();
    }

    /**
     * 插入章节树信息 有则先删除后更新，无则插入
     *
     * @param chapterTree
     */
    public void insertOrUpdateChapterTree(ChapterTree chapterTree) {
        checkChapterTreeDao();

        mChapterTreeDao.delete(chapterTree);
        mChapterTreeDao.insert(chapterTree);
    }

    /////////=============DirectBeanDao表操作=================///////////////
    private void checkDirectBeanDao() {
        if (mDirectBeanDao == null) {
            mDirectBeanDao = mDaoSession.getDirectBeanDao();
        }
    }

    /**
     * 插入视频下载记录
     *
     * @param directBean
     */
    public void insertOrUpdateDirectBean(DirectBean directBean, int type) {
        checkDirectBeanDao();
//        if (type == 0) {
//            if (StringUtils.isEmpty(DirectBean.getCcCourses_id())) {
//                deletDirectBeanForVedioId(DirectBean.getRoom_id(), DirectBean.getSession_id(), DirectBean.getNumber());
//            } else {
//                deletDirectBeanForVedioId(DirectBean.getCcCourses_id(), 0, DirectBean.getNumber());
//            }
//
//
//        } else if (type == 1) {
//            deletDirectBeanForVedioId(DirectBean.getLubourl(), 1, DirectBean.getNumber());
//        }
        if (directBean.getVideoType() == 0) {
            mDirectBeanDao.queryBuilder()
                    .where(DirectBeanDao.Properties.Userid.eq(directBean.getUserid())
                            , DirectBeanDao.Properties.Bjyvideoid.eq(directBean.getBjyvideoid()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();


        } else {

            DirectBean queryInfo = queryDirectBeanForPlayBack(directBean.getUserid(), directBean.getRoom_id(), directBean.getSession_id(), directBean.getNumber());
            if (queryInfo != null) {
                mDirectBeanDao.delete(queryInfo);
            }


        }

        try {
            mDirectBeanDao.insert(directBean);
            DebugUtil.e("视频下载插入：" + directBean.toString());
        } catch (SQLiteConstraintException e) {
            mDirectBeanDao.update(directBean);
        }
    }

    /**
     * 删除视频下载记录
     *
     * @param DirectBean
     */
    public void deleteDirectBean(DirectBean DirectBean) {
        checkDirectBeanDao();
        mDirectBeanDao.delete(DirectBean);
    }

    /**
     * 更新视频下载记录
     *
     * @param DirectBean
     */
    public void updateDirectBean(DirectBean DirectBean) {
        checkDirectBeanDao();
        mDirectBeanDao.update(DirectBean);
    }

    /**
     * 根据userid查询下载记录
     *
     * @param userId
     * @return
     */
    public List<DirectBean> queryDirectBeanForUserId(String userId) {
        checkDirectBeanDao();

        if (mDirectBeanQuery == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId));
            mDirectBeanQuery = queryBuilder.build();
        }

        Query<DirectBean> query = mDirectBeanQuery.forCurrentThread();
        query.setParameter(0, userId);
        return query.list();
    }

    /**
     * 根据userid,CCvedioId查询下载记录
     *
     * @param userId
     * @param vedioId
     * @return DirectBean
     */
    public DirectBean queryDirectBeanForCCVedioId(String userId, String vedioId, String number) {
        checkDirectBeanDao();
        if (mDirectBeanQueryByCCVedioId == null) {
            //cc视频查询
            mDirectBeanQueryByCCVedioId = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties
                    .CcCourses_id.eq(vedioId), DirectBeanDao.Properties
                    .Number.eq(number)).build();
        }
        Query<DirectBean> query = mDirectBeanQueryByCCVedioId.forCurrentThread();
        query.setParameter(0, userId);
        query.setParameter(1, vedioId);
        query.setParameter(2, number);
        if (query.list().size() > 1) {
            return query.list().get(0);
        } else {
            return query.unique();
        }
    }

    /**
     * 根据userid,百家云vedioId查询下载记录
     *
     * @param userId
     * @param vedioId
     * @return DirectBean
     */
    public DirectBean queryDirectBeanForBjyVedioId(String userId, String vedioId, String number) {
        checkDirectBeanDao();
        if (mDirectBeanQueryByBjyVedioId == null) {
            //cc视频查询
            mDirectBeanQueryByBjyVedioId = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties
                    .Bjyvideoid.eq(vedioId), DirectBeanDao.Properties
                    .Number.eq(number)).build();
        }
        Query<DirectBean> query = mDirectBeanQueryByBjyVedioId.forCurrentThread();
        query.setParameter(0, userId);
        query.setParameter(1, vedioId);
        query.setParameter(2, number);
        if (query.list() != null && query.list().size() > 1) {
            return query.list().get(0);
        } else {
            return query.unique();
        }
    }

    public DirectBean queryDirectBeanForPlayBack(String userId, String roomid, String sessionId, String number) {
        checkDirectBeanDao();
        List<DirectBean> list = null;

        if (StringUtils.isEmpty(sessionId)) {
            list = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId),
                    DirectBeanDao.Properties.Room_id.eq(roomid),
                    DirectBeanDao.Properties.Number.eq(number))
                    .list();
        } else {
            list = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties.Room_id.eq(roomid),
                    DirectBeanDao.Properties.Session_id.eq(sessionId), DirectBeanDao.Properties.Number.eq(number))
                    .list();
        }


        if (list != null && list.size() != 0) {


            return list.get(0);

        }
        return null;


    }

    /**
     * 根据userid,展示vedioId查询下载记录
     *
     * @param userId
     * @param vedioId
     * @return DirectBean
     */
    public DirectBean queryDirectBeanForGeeneVedioId(String userId, String vedioId, String number) {
        checkDirectBeanDao();
        if (mDirectBeanQueryByGeeneVedioId == null) {
            QueryBuilder queryBuilder = null;
            //展示互动点播视频查询
            queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties.Lubourl.eq(vedioId), DirectBeanDao.Properties.Number.eq(number));
            mDirectBeanQueryByGeeneVedioId = queryBuilder.build();
        }
        Query<DirectBean> query = mDirectBeanQueryByGeeneVedioId.forCurrentThread();
        query.setParameter(0, userId);
        query.setParameter(1, vedioId);
        query.setParameter(2, number);
//        DebugUtil.e("queryDirectBeanForGeeneVedioId:"+userId+"  "+vedioId+"  "+number);
        if (query.list().size() > 1) {
            return query.list().get(0);
        } else {
            return query.unique();
        }
    }

    /**
     * 根据userid,展示vedioId查询下载记录
     *
     * @param userId
     * @param vedioId
     * @return DirectBean
     */
//    public DirectBean queryDirectBeanForGeeneZhiVedioId(String userId, String vedioId,String number) {
//        checkDirectBeanDao();
//        if (mDirectBeanQueryByGeeneVedioId == null) {
//            QueryBuilder queryBuilder = null;
//            //展示互动点播视频查询
//            queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties.Zhibourl.eq(vedioId), DirectBeanDao.Properties.Number.eq(number));
//            mDirectBeanQueryByGeeneVedioId = queryBuilder.build();
//        }
//        Query<DirectBean> query = mDirectBeanQueryByGeeneVedioId.forCurrentThread();
//        query.setParameter(0, userId);
//        query.setParameter(1, vedioId);
//        query.setParameter(2, number);
//        if(query.list().size() > 1){
//            return query.list().get(0);
//        }else {
//            return query.unique();
//        }
//    }

    /**
     * 根据视频id删除记录
     *
     * @param vedioId
     * @param type    0 代表cc 1代表展示 2代表 回放
     */
    public void deletDirectBeanForVedioId(String vedioId, int type, String number) {
        checkDirectBeanDao();

        if (mDirectBeanDeleteQuery == null) {
            if (type == 0) {
                QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.CcCourses_id.eq(vedioId), DirectBeanDao.Properties.Number.eq(number));
                mDirectBeanDeleteQuery = queryBuilder.buildDelete();
            } else if (type == 1) {
                QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Lubourl.eq(vedioId), DirectBeanDao.Properties.Number.eq(number));
                mDirectBeanDeleteQuery = queryBuilder.buildDelete();
            }
        }
        DeleteQuery<DirectBean> query = mDirectBeanDeleteQuery.forCurrentThread();
        query.setParameter(0, vedioId);
        query.setParameter(1, number);
        query.executeDeleteWithoutDetachingEntities();
    }


    public void updatePlayBack(DirectBean bean) {
        checkDirectBeanDao();
        DirectBean queryBean = queryDirectBeanForPlayBack(bean.getUserid(), bean.getRoom_id(), bean.getSession_id(), bean.getNumber());
        if (queryBean != null) {
            mDirectBeanDao.delete(queryBean);
        }
        mDirectBeanDao.insert(bean);


    }


    //删除回放视频
    public void deletDirectBeanForVedioId(String roomId, String session_id, String number) {
        checkDirectBeanDao();

        if (mDirectBeanDeleteQuery == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Room_id.eq(roomId), DirectBeanDao.Properties.Session_id.eq(session_id), DirectBeanDao.Properties.Number.eq(number));
            mDirectBeanDeleteQuery = queryBuilder.buildDelete();
        }

        DeleteQuery<DirectBean> query = mDirectBeanDeleteQuery.forCurrentThread();
        query.setParameter(0, roomId);
        query.setParameter(1, session_id);
        query.setParameter(2, number);
        query.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 插入足迹
     *
     * @param DirectBean
     */
    public void insertOrUpdateDirectBeanHistory(DirectBean DirectBean) {
        checkDirectBeanDao();
        DirectBean.setFootprint(1);
        deleteDirectBeanForRid(DirectBean);
        mDirectBeanDao.insert(DirectBean);
    }

    /**
     * 根据userid,查询课程详情足迹
     *
     * @param userId
     */
    public List<DirectBean> queryDirectBeanForHistoryForUserid(String userId) {
        checkDirectBeanDao();
        if (mDirectBeanHistoryByUserid == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties.Footprint.eq
                    (1)).orderDesc(DirectBeanDao.Properties.Id);
            mDirectBeanHistoryByUserid = queryBuilder.build();
        }
        Query<DirectBean> directBeanQuery = mDirectBeanHistoryByUserid.forCurrentThread();
        directBeanQuery.setParameter(0, userId);
        directBeanQuery.setParameter(1, 1);
        return directBeanQuery.list();
    }

    /**
     * 根据课程rid,删除课程列表里面的被删掉的课程
     *
     * @param rid
     */
    public void deletDirectBeanListForRid(String rid) {
        checkDirectBeanDao();
        if (deletDirectBeanListForRid == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Rid.eq(rid), DirectBeanDao.Properties.Footprint.eq
                    (3));
            deletDirectBeanListForRid = queryBuilder.buildDelete();
            ;
        }
        deletDirectBeanListForRid.setParameter(0, rid);
        deletDirectBeanListForRid.setParameter(1, 3);
        deletDirectBeanListForRid.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据Rid,删除课程(注：Footprint ：1代表足迹 2课程 3课程下面的课时)
     */
    public void deleteDirectBeanForRid(DirectBean directBean) {
        checkDirectBeanDao();
        if (mDirectBeanDeleteHisQuery == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Rid.eq(directBean.getRid()), DirectBeanDao.Properties
                    .Footprint.eq(directBean.getFootprint()));
            mDirectBeanDeleteHisQuery = queryBuilder.buildDelete();
        }
        DeleteQuery<DirectBean> query = mDirectBeanDeleteHisQuery.forCurrentThread();
        query.setParameter(0, directBean.getRid());
        query.setParameter(1, directBean.getFootprint());
        query.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据Rid,number删除课时(注：只针对Footprint=3   )
     */
    public void deleteDirectBeanForRidNumber(DirectBean directBean) {
        checkDirectBeanDao();
        if (mDirectBeanDeleteClassQuery == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Rid.eq(directBean.getRid()), DirectBeanDao.Properties
                    .Footprint.eq(directBean.getFootprint()), DirectBeanDao.Properties.Number.eq(directBean.getNumber()));
            mDirectBeanDeleteClassQuery = queryBuilder.buildDelete();
        }
        DeleteQuery<DirectBean> query = mDirectBeanDeleteClassQuery.forCurrentThread();
        query.setParameter(0, directBean.getRid());
        query.setParameter(1, directBean.getFootprint());
        query.setParameter(2, directBean.getNumber());
        query.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 插入我的直播
     *
     * @param DirectBean
     */
    public void insertOrUpdateDirectBeanCache(DirectBean DirectBean) {
        checkDirectBeanDao();
        DirectBean.setFootprint(2);
        deleteDirectBeanForRid(DirectBean);
        mDirectBeanDao.insert(DirectBean);
    }

    /**
     * 根据userid,查询我的的直播
     *
     * @param userId
     */
    public List<DirectBean> queryDirectBeanForCacheForUserid(String userId) {
        checkDirectBeanDao();
        if (mDirectBeanCacheByUserid == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Userid.eq(userId), DirectBeanDao.Properties.Footprint.eq
                    (2)).orderAsc(DirectBeanDao.Properties.Id);
            mDirectBeanCacheByUserid = queryBuilder.build();
        }
        Query<DirectBean> directBeanQuery = mDirectBeanCacheByUserid.forCurrentThread();
        directBeanQuery.setParameter(0, userId);
        directBeanQuery.setParameter(1, 2);
        return directBeanQuery.list();
    }

    /**
     * 插入我的直播课 课程
     *
     * @param directBean
     */
    public void insertOrUpdateDirectBeanCacheClass(DirectBean directBean) {
        checkDirectBeanDao();
        directBean.setFootprint(3);
        deleteDirectBeanForRidNumber(directBean);
        mDirectBeanDao.insert(directBean);
    }

    /**
     * 根据rid,Footprint=3查询该课程下面的课时
     */
    public List<DirectBean> queryDirectBeanForCacheClassForRid(String rid) {
        checkDirectBeanDao();
        if (mDirectBeanCacheClassByrid == null) {
            QueryBuilder queryBuilder = mDirectBeanDao.queryBuilder().where(DirectBeanDao.Properties.Rid.eq(rid), DirectBeanDao.Properties.Footprint.eq
                    (3));
            mDirectBeanCacheClassByrid = queryBuilder.build();
        }
        Query<DirectBean> directBeanQuery = mDirectBeanCacheClassByrid.forCurrentThread();
        directBeanQuery.setParameter(0, rid);
        directBeanQuery.setParameter(1, 3);
        return directBeanQuery.list();
    }
}
