package com.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.greendao.StudyRecords;
import com.greendao.ExerciseDownloadPackage;
import com.greendao.QuestionDetail;
import com.greendao.ExerciseStore;
import com.greendao.ExamSubject;
import com.greendao.ChapterTree;

import com.greendao.StudyRecordsDao;
import com.greendao.ExerciseDownloadPackageDao;
import com.greendao.QuestionDetailDao;
import com.greendao.ExerciseStoreDao;
import com.greendao.ExamSubjectDao;
import com.greendao.ChapterTreeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 *
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig studyRecordsDaoConfig;
    private final DaoConfig exerciseDownloadPackageDaoConfig;
    private final DaoConfig questionDetailDaoConfig;
    private final DaoConfig exerciseStoreDaoConfig;
    private final DaoConfig examSubjectDaoConfig;
    private final DaoConfig chapterTreeDaoConfig;

    private final StudyRecordsDao studyRecordsDao;
    private final ExerciseDownloadPackageDao exerciseDownloadPackageDao;
    private final QuestionDetailDao questionDetailDao;
    private final ExerciseStoreDao exerciseStoreDao;
    private final ExamSubjectDao examSubjectDao;
    private final ChapterTreeDao chapterTreeDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        studyRecordsDaoConfig = daoConfigMap.get(StudyRecordsDao.class).clone();
        studyRecordsDaoConfig.initIdentityScope(type);

        exerciseDownloadPackageDaoConfig = daoConfigMap.get(ExerciseDownloadPackageDao.class).clone();
        exerciseDownloadPackageDaoConfig.initIdentityScope(type);

        questionDetailDaoConfig = daoConfigMap.get(QuestionDetailDao.class).clone();
        questionDetailDaoConfig.initIdentityScope(type);

        exerciseStoreDaoConfig = daoConfigMap.get(ExerciseStoreDao.class).clone();
        exerciseStoreDaoConfig.initIdentityScope(type);

        examSubjectDaoConfig = daoConfigMap.get(ExamSubjectDao.class).clone();
        examSubjectDaoConfig.initIdentityScope(type);

        chapterTreeDaoConfig = daoConfigMap.get(ChapterTreeDao.class).clone();
        chapterTreeDaoConfig.initIdentityScope(type);

        studyRecordsDao = new StudyRecordsDao(studyRecordsDaoConfig, this);
        exerciseDownloadPackageDao = new ExerciseDownloadPackageDao(exerciseDownloadPackageDaoConfig, this);
        questionDetailDao = new QuestionDetailDao(questionDetailDaoConfig, this);
        exerciseStoreDao = new ExerciseStoreDao(exerciseStoreDaoConfig, this);
        examSubjectDao = new ExamSubjectDao(examSubjectDaoConfig, this);
        chapterTreeDao = new ChapterTreeDao(chapterTreeDaoConfig, this);

        registerDao(StudyRecords.class, studyRecordsDao);
        registerDao(ExerciseDownloadPackage.class, exerciseDownloadPackageDao);
        registerDao(QuestionDetail.class, questionDetailDao);
        registerDao(ExerciseStore.class, exerciseStoreDao);
        registerDao(ExamSubject.class, examSubjectDao);
        registerDao(ChapterTree.class, chapterTreeDao);
    }

    public void clear() {
        studyRecordsDaoConfig.getIdentityScope().clear();
        exerciseDownloadPackageDaoConfig.getIdentityScope().clear();
        questionDetailDaoConfig.getIdentityScope().clear();
        exerciseStoreDaoConfig.getIdentityScope().clear();
        examSubjectDaoConfig.getIdentityScope().clear();
        chapterTreeDaoConfig.getIdentityScope().clear();
    }

    public StudyRecordsDao getStudyRecordsDao() {
        return studyRecordsDao;
    }

    public ExerciseDownloadPackageDao getExerciseDownloadPackageDao() {
        return exerciseDownloadPackageDao;
    }

    public QuestionDetailDao getQuestionDetailDao() {
        return questionDetailDao;
    }

    public ExerciseStoreDao getExerciseStoreDao() {
        return exerciseStoreDao;
    }

    public ExamSubjectDao getExamSubjectDao() {
        return examSubjectDao;
    }

    public ChapterTreeDao getChapterTreeDao() {
        return chapterTreeDao;
    }

}