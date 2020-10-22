package images.mvc;

//控制器接口
//视图能够调用的控制器方法都在这里
public interface ControllerInterface
{
    void start();
    void stop();
    void increaseBPM();
    void decreaseBPM();
    void setBPM(int bpm);
}