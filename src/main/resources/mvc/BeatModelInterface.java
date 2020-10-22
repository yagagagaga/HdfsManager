package images.mvc;

//模型是负责处理真正的业务逻辑
public interface BeatModelInterface
{
    void initialize();

    void on();

    void off();

    void setBPM(int bpm);

    int getBPM();

    //模型中实现了观察者模式
    void registerObserver(BeatObserver o);

    void removeObserver(BeatObserver o);

    void registerObserver(BPMObserver o);

    void removeObserver(BPMObserver o);
}