package pl.antonic.server;

class OpenCVTest {

    //remember to change the path if dir for libopencv_java411.so has changed
    void loadOpenCV() {
        String openCVpath = "/home/antoni/Desktop/opencv-4.1.1/build/lib/libopencv_java411.so";
        System.load(openCVpath);
    }
}
