/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rosuda.irconnect;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ralf
 */
public abstract class ARConnection implements IRConnection{

    protected final List<IRConnectionListener> listenerList = new ArrayList<IRConnectionListener>();

    protected abstract void login(final String userName, final String userPassword);

    public final synchronized void addRConnectionListener(final IRConnectionListener listener) {
        this.listenerList.add(listener);
    }

    /**
     * either synchronize or copy array - both unperfomant but required to avoid ConcurrentmodificationException
     * @param event
     */
    public synchronized final void notifyListeners(final IRConnectionEvent event) {
        for (final IRConnectionListener listener : listenerList) {
            listener.connectionPerformed(event);
        }        
    }
}
