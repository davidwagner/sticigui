
import java.awt.*;
import java.applet.*;

public class affine extends Applet {

    public void init() {

        super.init();

        //{{INIT_CONTROLS
        setLayout(null);
        resize(378,159);
        old=new TextField(10);
        add(old);
        old.resize(old.preferredSize());
        old.move(0,38);
        timesLabel=new Label("x", Label.CENTER);
        timesLabel.disable();
        add(timesLabel);
        timesLabel.resize(timesLabel.preferredSize());
        timesLabel.move(103,38);
        factor=new TextField(10);
        add(factor);
        factor.resize(factor.preferredSize());
        factor.move(138,38);
        plusLabel=new Label("+", Label.CENTER);
        plusLabel.disable();
        add(plusLabel);
        plusLabel.resize(plusLabel.preferredSize());
        plusLabel.move(242,38);
        constant=new TextField(10);
        add(constant);
        constant.resize(constant.preferredSize());
        constant.move(277,38);
        equalsLabel=new Label("=", Label.CENTER);
        equalsLabel.disable();
        add(equalsLabel);
        equalsLabel.resize(equalsLabel.preferredSize());
        equalsLabel.move(126,84);
        answer=new TextField(10);
        add(answer);
        answer.resize(answer.preferredSize());
        answer.move(163,84);
        oldLabel=new Label("Original Value", Label.CENTER);
        oldLabel.setFont(new Font("Dialog",Font.BOLD,10));
        oldLabel.disable();
        add(oldLabel);
        oldLabel.resize(oldLabel.preferredSize());
        oldLabel.move(4,8);
        factorLabel=new Label("Factor", Label.CENTER);
        factorLabel.disable();
        add(factorLabel);
        factorLabel.resize(factorLabel.preferredSize());
        factorLabel.move(140,8);
        constantLabel=new Label("Constant", Label.CENTER);
        constantLabel.disable();
        add(constantLabel);
        constantLabel.resize(constantLabel.preferredSize());
        constantLabel.move(278,8);
        newLabel=new Label("New Value", Label.CENTER);
        newLabel.disable();
        add(newLabel);
        newLabel.resize(newLabel.preferredSize());
        newLabel.move(266,83);
        Compute=new Button("Compute");
        Compute.setFont(new Font("Dialog",Font.BOLD,12));
        add(Compute);
        Compute.resize(Compute.preferredSize());
        Compute.move(18,84);
        title=new Label("Affine Transformation", Label.CENTER);
        title.setFont(new Font("Dialog",Font.BOLD,14));
        title.disable();
        add(title);
        title.resize(title.preferredSize());
        title.move(97,125);
        //}}
    }

    public boolean handleEvent(Event event) {
        if (event.id == Event.ACTION_EVENT && event.target == Compute) {
                clickedCompute();
                return true;
        }
        return super.handleEvent(event);
    }

    //{{DECLARE_CONTROLS
    TextField old;
    Label timesLabel;
    TextField factor;
    Label plusLabel;
    TextField constant;
    Label equalsLabel;
    TextField answer;
    Label oldLabel;
    Label factorLabel;
    Label constantLabel;
    Label newLabel;
    Button Compute;
    Label title;
    //}}

    public void clickedCompute() {
        double ans = strToNum(old.getText())*strToNum(factor.getText())+
            strToNum(constant.getText());
        answer.setText(""+ans);
        repaint();
    }

    public double strToNum(String s)
    {
        double v = 0;
        try
        {
            v = new Double(s.trim()).doubleValue();
        }
        catch (NumberFormatException e)
        {
            v=0;
        }
        return v;
    }
}
