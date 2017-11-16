package smartAP.MessageConsumer_2.util;
import java.io.File;
import java.io.FileFilter;

public class DataFileFilter implements FileFilter {  
  
    @Override  
    public boolean accept(File file) {  
        // TODO Auto-generated method stub  
//      return false;  
          
        if(file.isDirectory())  
            return true;  
        else  
        {  
            String name = file.getName();  
            if(name.endsWith(".data"))  
                return true;  
            else  
                return false;  
        }  
          
    }  
  
}  