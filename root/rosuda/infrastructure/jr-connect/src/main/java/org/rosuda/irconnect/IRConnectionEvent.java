/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rosuda.irconnect;

/**
 * 
 * @author Ralf
 */
public interface IRConnectionEvent {

    public static enum Type {
        EVALUATE /* eval, voidEval */,
        SET /* assign */,
        R_SHOW_MESSAGE /*jri- rShowMessage */,
        R_CONSOLE_MESSAGE /*jri - rWriteConsole*/,
        ERROR_MSG /*  custom error message */,
        CLOSE /*close notification*/ ,
        AFTERCONNECTIONCLOSED, /*after r connection has been closed*/
        BEFORECONNECT /* before a connect takey place*/,
        AFTERCONNECT /* successfully connected*/
    };

    public Type getType();

    public String getMessage();

    /**
     * if an object is sent vie IJava2RConnection this object can be found here
     * 
     * @return
     */
    public Object getObject();

    public static final class Event implements IRConnectionEvent {

        private final Type type;
        private final String message;
        private final Object object;

        public Event(final Type type, final String message) {
            this(type, message, null);
        }

        public Event(final Type type, final String message, final Object object) {
            if (type == null)
                throw new IllegalArgumentException("type must be != null");
            this.type = type;
            this.message = message;
            this.object = object;
        }

        public final Type getType() {
            return type;
        }

        public final String getMessage() {
            return message;
        }

        public final Object getObject() {
            return object;
        }
    }
}
