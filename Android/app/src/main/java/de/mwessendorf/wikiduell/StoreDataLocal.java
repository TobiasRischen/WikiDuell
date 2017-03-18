package de.mwessendorf.wikiduell;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by maximilian on 18.03.17.
 */

public class StoreDataLocal {


        private FileWriter fw;
        private BufferedWriter bw;


        public StoreDataLocal() throws IOException {
            super();
            this.fw = new FileWriter("log.txt", true);
            this.bw = new BufferedWriter(fw);
        }
        public void printString(String str) throws IOException {
            bw.write(str);
            bw.flush();
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


