package com.huatu.teacheronline.direct.db;

import com.huatu.teacheronline.direct.bean.WatchRecord;
import com.huatu.teacheronline.personal.db.VideoUploadInfo;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by kinndann on 2018/8/9/009.
 * description:
 */
@Database(name = PlayerDataBase.name, version = PlayerDataBase.version)
public class PlayerDataBase {

    public static final String name = "playerdatabase";
    public static final int version = 13;


    /**
     * 用于重命名表，增加列
     * priority 越小越优先处理
     */
    @Migration(priority = 0, version = 5, database = PlayerDataBase.class)
    public static class EditToWatchRecordMigration extends AlterTableMigration<WatchRecord> {

        public EditToWatchRecordMigration(Class<WatchRecord> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "courseWareId");

//            addForeignKeyColumn(SQLiteType.INTEGER, "category_id", FlowManager.getTableName(Category.class) +"(`id`)
        }
    }
    @Migration(priority = 0, version = 13, database = PlayerDataBase.class)
    public static class EditToVideoUploadInfoMigration extends AlterTableMigration<VideoUploadInfo> {

        public EditToVideoUploadInfoMigration(Class<VideoUploadInfo> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "agree");

        }
    }

//    @Migration(priority = 0, version = 11, database = PlayerDataBase.class)
//    public static class EditToInterviewVideoUploadHistoryMigration extends AlterTableMigration<InteviewVideoInfoUploadRequest> {
//
//        public EditToInterviewVideoUploadHistoryMigration(Class<InteviewVideoInfoUploadRequest> table) {
//            super(table);
//        }
//
//        @Override
//        public void onPreMigrate() {
//            addColumn(SQLiteType.INTEGER, "uploadProgress");
//            addColumn(SQLiteType.TEXT, "localPath");
//            addColumn(SQLiteType.INTEGER, "uploadState");
//            addColumn(SQLiteType.TEXT, "upload_url");
//
////            addForeignKeyColumn(SQLiteType.INTEGER, "category_id", FlowManager.getTableName(Category.class) +"(`id`)
//        }
//    }
}
