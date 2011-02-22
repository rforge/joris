/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rosuda.irconnect;

/**
 *
 * @author Ralf
 */
public interface IRConnectionListener {

    /**
     * 
     * @param event
     */
    public void connectionPerformed(final IRConnectionEvent event);
}
