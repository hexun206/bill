package com.huatu.teacheronline.personal.upload;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.RecorderActivity;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kinndann on 2018/11/8.
 * description:视频合并
 */
public class VideoMerger extends AsyncTask<Void, Integer, Void> {
    public interface OnVideoListener {
        void onVideoMerged();

        void onVideoMergeFailed(Exception e);

    }

    private final String TAG = "VideoMerger:";

    private ArrayList<File> videoFiles;
    private File finalVideoFile;
    private Exception exception;
    private OnVideoListener mListener;
    private Dialog progressDialog;
    private Context context;
    private TextView tv_loadingMessage;
    private long finalVideoFileSize;
    // Whether the thread that is checking file size is running or not
    private boolean fileSizeCheckerThread;

    public VideoMerger(Context context, ArrayList<File> videoFiles,
                       File finalVideoFile) {
        super();
        this.context = context;
        this.videoFiles = videoFiles;
        this.finalVideoFile = finalVideoFile;
    }

    public void setVideoMergeListener(OnVideoListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_video_merge_loading, null);
        tv_loadingMessage = (TextView) view
                .findViewById(R.id.tv_msg);
        tv_loadingMessage.setText("视频处理中...(1%)");
        progressDialog.setContentView(view);
        progressDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);


        Integer progress = values[0];
        progress = progress == 100 ? 99 : progress;
        tv_loadingMessage.setText("视频处理中...(" + progress + "%)");
    }

    @Override
    protected Void doInBackground(Void... params) {

        //处理前置摄像头与后置摄像头视频合并问题
//        rotateFrontVideo();//该方法无效  暂时无法支持


        List<Movie> movies = new ArrayList<Movie>();
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        try {
            for (int i = 0; i < videoFiles.size(); i++) {
                movies.add(MovieCreator.build(videoFiles.get(i)
                        .getAbsolutePath()));
            }
            for (Movie m : movies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie finalMovie = new Movie();

            if (audioTracks.size() > 0) {
                finalMovie.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                finalMovie.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            Container out = new DefaultMp4Builder().build(finalMovie);

            FileChannel fc = new RandomAccessFile(finalVideoFile, "rw")
                    .getChannel();
            for (int i = 0; i < out.getBoxes().size(); i++) {
                finalVideoFileSize += out.getBoxes().get(i).getSize();
            }
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    fileSizeCheckerThread = true;
                    Logger.e("VideoMerge  Started");
                    long currentFileSize = 0;
                    if (finalVideoFile.exists())
                        currentFileSize = finalVideoFile.length();
                    while (currentFileSize < finalVideoFileSize) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (finalVideoFile.exists()) {
                            currentFileSize = finalVideoFile.length();
                            Logger.e("VideoMerge Total: " + finalVideoFileSize
                                    + " current: " + currentFileSize);
                            int progress = (int) (currentFileSize * 100 / finalVideoFileSize);
                            publishProgress(progress);
                        }
                        if (currentFileSize >= finalVideoFileSize)
                            break;

                    }
                    fileSizeCheckerThread = false;
                    Logger.e("VideoMerge Ended");

                }
            });
            t.start();

            out.writeContainer(fc);
            fc.close();
//            for (int i = 0; i < videoFiles.size(); i++) {
//                videoFiles.get(i).delete();
//            }
            publishProgress(100);
            if (fileSizeCheckerThread)
                Thread.sleep(5000);

        } catch (Exception e) {
            exception = e;
            Logger.e(TAG + e.toString());
        }
        return null;
    }


    /**
     * 对前置摄像头video旋转270度
     */
    private void rotateFrontVideo() {
        for (int i = 0; i < videoFiles.size(); i++) {
            File file = videoFiles.get(i);
            boolean isFront = file.getName().contains(RecorderActivity.TAG_FRONT);
            if (isFront) {
                String newFilePath = file.getParentFile() + "/rotation_" + file.getName();
                File newFile = new File(newFilePath);

                IsoFile isoFile = null;
                try {
                    isoFile = new IsoFile(file.getAbsolutePath());

                    Movie m = new Movie();
                    List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);
                    for (TrackBox trackBox : trackBoxes) {
                        trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_90);
                        m.addTrack(new Mp4TrackImpl("output1", trackBox) {
                        });
                    }
                    Container finalContainer = new DefaultMp4Builder().build(m);
                    @SuppressWarnings("resource")
                    FileChannel finalStream = new RandomAccessFile(newFile.getAbsolutePath(), "rw").getChannel();
                    finalContainer.writeContainer(finalStream);
                    finalStream.close();
                    isoFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                file.delete();
                videoFiles.remove(i);
                videoFiles.add(i, newFile);

            }


        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        if (mListener != null) {
            if (exception != null)
                mListener.onVideoMergeFailed(exception);
            else
                mListener.onVideoMerged();
        }
    }

}