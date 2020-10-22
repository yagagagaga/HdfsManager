package images.mvc;

public class BeatController implements ControllerInterface
{
    BeatModelInterface model;
    DJView view;

    //这里为什么要通过构造函数传递一个模型呢？而不是直接在控制器中实例化一个模型？
    //因为实际上，模型是可以更换的，而且我们可以通过适配器模式，将一个非模型转换为模型，如后面的心脏适配器
    public BeatController(BeatModelInterface model)
    {
        this.model = model;
        view = new DJView(this, model);
        view.createView();
        view.createControls();
        view.disableStopMenuItem();
        view.enableStartMenuItem();
        model.initialize();
    }

    public void start() {
        model.on();
        view.disableStartMenuItem();
        view.enableStopMenuItem();
    }

    public void stop() {
        model.off();
        view.disableStopMenuItem();
        view.enableStartMenuItem();
    }

    public void increaseBPM()
    {
        //控制器会解释用户输入，并调用模型处理
        int bpm = model.getBPM();
        model.setBPM(bpm + 1);
    }

    public void decreaseBPM()
    {
        int bpm = model.getBPM();
        model.setBPM(bpm - 1);
    }

    public void setBPM(int bpm)
    {
        //交给模型处理
        model.setBPM(bpm);
    }
}