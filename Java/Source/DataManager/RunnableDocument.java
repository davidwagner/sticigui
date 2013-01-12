package DataManager;

import java.net.*;

public class RunnableDocument extends Document implements ManagerRunnable
{

    DataManager data_manager = null;

    public RunnableDocument(String s)
    {
        super(s);
    }

    public RunnableDocument(URL s)
    {
        super(s);
    }

    public void run()
    {
        load();
        data_manager.insertDataSet(this,toString());
    }

    public DataManager manager(DataManager d)
    {
        data_manager = d;
        return(d);
    }

}
