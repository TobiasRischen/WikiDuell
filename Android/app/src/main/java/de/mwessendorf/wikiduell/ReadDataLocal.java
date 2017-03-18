package de.mwessendorf.wikiduell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by maximilian on 18.03.17.
 */

public class ReadDataLocal {
    private FileReader fw;
    private BufferedReader bw;


    public ReadDataLocal() throws IOException {
        super();
        this.fw = new FileReader("log.txt");
        this.bw = new BufferedReader(fw);
    }
    public String[] readAll() throws IOException {
        ArrayList<String> content = new ArrayList<String>();
        String temp = "";
        while((temp=bw.readLine())!=null) {
            content.add(temp);
        }
        return (String[]) content.toArray();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        fw = null;
        bw = null;
        super.finalize();
    }
}
