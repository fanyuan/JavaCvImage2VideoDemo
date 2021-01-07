package com.example.javacvimage2videodemo;

import android.util.Log;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.File;
import java.util.Map;

import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;

public class Utils {
    /**
     * 录制成功监听
     */
    public interface CompleteListener{
        public void onComplete(String path);
    }
    public static void createMp4(String mp4SavePath, Map<Integer, File> imgMap, int width, int height,CompleteListener listener) throws FrameRecorder.Exception {
        //视频宽高最好是按照常见的视频的宽高  16：9  或者 9：16
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(mp4SavePath, width, height);
        //设置视频编码层模式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        //设置视频为25帧每秒
        recorder.setFrameRate(25);
        //设置视频图像数据格式
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFormat("mp4");
        try {
            recorder.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Log.d("test","imgMap.size() = " + imgMap.size());
            //录制一个imgMap size秒数的的视频，imgmap容量有多大就录多少秒
            for (int i = 0; i < imgMap.size(); i++) {
                if(imgMap.get(i) == null){
                    continue;
                }
                IplImage image = cvLoadImage(imgMap.get(i).getAbsolutePath());//
                OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
                Frame frame = converterToMat.convert(image);
                //一秒是30帧 所以要记录30次
                for(int j = 0; j < 30; j++){
                    recorder.record(frame);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后一定要结束并释放资源
            recorder.stop();
            recorder.release();
            if(listener != null){
                listener.onComplete(mp4SavePath);
            }
            Log.d("test","工作已完成 ");
        }
    }

}
