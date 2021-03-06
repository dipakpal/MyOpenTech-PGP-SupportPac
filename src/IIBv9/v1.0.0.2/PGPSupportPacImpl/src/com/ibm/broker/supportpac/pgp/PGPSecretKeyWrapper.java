
package com.ibm.broker.supportpac.pgp;

import java.util.Iterator;

import org.bouncycastle.openpgp.PGPSecretKey;

/**
 * PGP Secret key wrapper class.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * PGP Secret key wrapper class.
 */
public class PGPSecretKeyWrapper  {
    PGPSecretKey base;

    /**
     * 
     * @param iBase
     */
    public PGPSecretKeyWrapper(PGPSecretKey iBase){
        base = iBase;
    }

    /**
     * 
     * @return
     */
    public PGPSecretKey getSecretKey(){
        return base;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public String toString() {
        StringBuilder outStr = new StringBuilder();
        Iterator iter = base.getUserIDs();

        outStr.append("KeyId (Hex): [0x");
        outStr.append(Integer.toHexString((int)base.getKeyID()).toUpperCase());
        outStr.append("] Key User Id: [");

        boolean single = true;
        while(iter.hasNext()){
        	if(single){
        		outStr.append(iter.next().toString());
        		single = false;
        	} else {
        		outStr.append("; " + iter.next().toString());
        	}            
        }
        
        outStr.append("]");

        return outStr.toString();
    }

}
