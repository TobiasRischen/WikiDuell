package de.mwessendorf.wikiduell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by maximilian on 18.03.17.
 */

public class ReadDataLocal {
    private FileReader fr;
    private BufferedReader br;


    public ReadDataLocal() throws IOException {
        super();
        this.fr = new FileReader("log.txt");
        this.br = new BufferedReader(fr);
    }
    public String readAll() throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        fr = null;
        br = null;
        super.finalize();
    }
}
