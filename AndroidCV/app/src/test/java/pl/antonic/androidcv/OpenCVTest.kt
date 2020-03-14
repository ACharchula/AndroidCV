package pl.antonic.androidcv

import org.junit.Before

open class OpenCVTest {

    //remember to change the path if dir for libopencv_java411.so has changed
    @Before
    fun initOpenCV() {
        val openCVpath = "/home/antoni/Desktop/opencv-4.1.1/build/lib/libopencv_java411.so"
        System.load(openCVpath)
    }

}