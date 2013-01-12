import java.awt.*;
import java.applet.*;
import PbsGui.*;

public class TTBar extends Applet
{
    private int places = 10;
    private TextBar digitBar;
    private TextBar decimalBar;
    private Label label;
    private TextField ta = new TextField(20);


    public void init()
    {
        digitBar = new TextBar(0, 0, 20,
                                places, (Validator) new
                                IntegerValidator(), TextBar.NO_BAR,
                                "digits");
        decimalBar = new TextBar(0, 0, 20,
                                places, (Validator) new
                                IntegerValidator(), TextBar.NO_BAR,
                                "decimals");
        setLayout(new BorderLayout());
        Panel p1 = new Panel();
        p1.setLayout(new GridLayout(5,1));
        p1.add(digitBar);
        p1.add(decimalBar);
        p1.add(ta);
        p1.add(label);
        add("Center",p1);
        return;
    }
    public boolean handleEvent(Event e)
    {
        if (e.id == Event.ACTION_EVENT)
        {
            double num = Format.strToDouble(ta.getText());
            label.setText(Format.numToString(num,(int) digitBar.getValue(),
                (int) decimalBar.getValue(),true));
            repaint();
            return true;
        }
        else return(super.handleEvent(e));
    }

}