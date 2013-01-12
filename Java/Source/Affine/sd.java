
import java.awt.*;
import java.applet.*;

public class sd extends Applet {

    public void init() {

        super.init();

        //{{INIT_CONTROLS
        setLayout(null);
        resize(216,189);
        data=new TextArea("Enter your data here",10,10);
        data.setFont(new Font("Dialog",Font.PLAIN,10));
        add(data);
        data.reshape(14,7,87,174);
        data.selectAll();
        computeButton=new Button("Compute SD");
        add(computeButton);
        computeButton.reshape(119,15,78,22);
        answerField=new TextField(8);
        add(answerField);
        answerField.reshape(126,61,71,23);
        //}}
    }

    public boolean handleEvent(Event event) {
        if (event.id == Event.ACTION_EVENT && event.target == computeButton) {
                clickedComputeButton();
                return true;
        }

        return super.handleEvent(event);
    }

    public double strToDouble(String s)
    {
        v=0;
        try
        {
            v = Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e)
        {
            v=0;
        }
        return v;
    }

    //{{DECLARE_CONTROLS
    TextArea data;
    Button computeButton;
    TextField answerField;
    //}}
    public void clickedComputeButton() {

    }
}
