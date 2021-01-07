package com.example.javacvimage2videodemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.javacvimage2videodemo.FileUtils.NoSdcardException
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.FrameRecorder
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat
import org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage
import org.bytedeco.opencv.opencv_core.IplImage
import java.io.File


class ImageToVideoActivity : AppCompatActivity() {
    companion object{
        @JvmStatic
        val IMAGE_TYPE = ".bmp"//".png";
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_to_video)
    }
    fun start(v: View){
        Log.d("ddebug", "---start---")
//        imageToMp4()

        object :Thread(){
            override fun run() {
                super.run()
                //合成的MP4
                //合成的MP4
                val mp4SavePath = "/storage/emulated/0/ScreenRecord/${System.currentTimeMillis()}test_abc.mp4"//"D:\\javacv\\mp4\\img.mp4"
                //图片地址 这里面放了几张图片
                val img = "/storage/emulated/0/ScreenRecord/temp"
                val width = 1600
                val height = 900
                //读取所有图片
                val file = File(img)
                val files = file.listFiles()
                val imgMap: MutableMap<Int, File> = HashMap()
                var num = 0
                for (imgFile in files) {
                    imgMap[num] = imgFile
                    num++
                }
                Utils.createMp4(mp4SavePath, imgMap, width, height,
                    object : Utils.CompleteListener{
                        override fun onComplete(path: String?) {
                            runOnUiThread{
                                val tv:TextView = findViewById(R.id.tv)
                                tv.setText("录制已完成：$path")
                            }
                        }
                    });

            }
        }.start()
    }

    /**
     * 图片转视频
     */
    private fun imageToMp4() {
        // 生成的新文件名
        val newFileName = "test_" + System.currentTimeMillis() + ".mp4"
        // 保存的路径
        var temp: String? = null
        val frameRate = 5.0
        try {
            temp = (FileUtils().sdCardRoot + "ScreenRecord"
                    + File.separator + newFileName)
        } catch (e1: NoSdcardException) {
            e1.printStackTrace()
        }
        val savePath = temp
        object : Thread() {
            override fun run() {
                Log.d("test", "开始将图片转成视频啦...frameRate=$frameRate")
                try {
                    // 临时文件路径即存储源图片的路径
                    val tempFilePath = (FileUtils().sdCardRoot
                            + "ScreenRecord/temp" + File.separator)
                    Log.i("test", "tempFilePath=$tempFilePath")
                    val testBitmap = BitmapFactory.decodeFile(
                        tempFilePath
                                + "icon1" + ImageToVideoActivity.IMAGE_TYPE
                    )
                    Log.i(
                        "test",
                        "testBitmap=$testBitmap testBitmap.width = ${testBitmap.width}  testBitmap.height = ${testBitmap.height}  savePath = $savePath"
                    )

                    //创建一个记录者
                    val recorder = FFmpegFrameRecorder(
                        savePath, testBitmap.width,
                        testBitmap.height
                    )
                    // 设置视频格式
                    recorder.format = "mp4"
                    // 录像帧率
                    recorder.frameRate = frameRate
                    // 记录开始
                    recorder.start()
                    var index = 0
                    while (index < 8) {
                        // 获取图片--图片格式为head1.png,head2.png...head8.png
                        //org.bytedeco.opencv.opencv_core.IplImage
                        val image: IplImage = cvLoadImage(
                            tempFilePath
                                    + "head" + index
                                    + IMAGE_TYPE
                        )
                        val converterToMat = ToMat()
                        val frame = converterToMat.convert(image)
                        recorder.record(frame)
                        index++
                    }
                    Log.d("test", "录制完成....")
                    // 录制结束
                    recorder.stop()
                } catch (e: NoSdcardException) {
                    e.printStackTrace()
                } catch (e: FrameRecorder.Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}