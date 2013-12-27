package org.squirrelframework.foundation.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.annotation.OnActionExecException;
import org.squirrelframework.foundation.fsm.annotation.OnActionExecute;
import org.squirrelframework.foundation.fsm.annotation.OnTransitionBegin;
import org.squirrelframework.foundation.fsm.annotation.OnTransitionComplete;
import org.squirrelframework.foundation.fsm.annotation.OnTransitionDecline;
import org.squirrelframework.foundation.fsm.annotation.OnTransitionException;

import com.google.common.base.Stopwatch;

public class StateMachineLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(StateMachineLogger.class);
	
	private Stopwatch sw;
	
	private final StateMachine<?,?,?,?> stateMachine;
	
	private final String stateMachineLabel;
	
	public StateMachineLogger(StateMachine<?,?,?,?> stateMachine) {
	    this.stateMachine = stateMachine;
	    this.stateMachineLabel = stateMachine.getClass().getSimpleName()
	            +"("+stateMachine.getIdentifier()+")";
	}
	
	public void startLogging() {
	    stateMachine.addDeclarativeListener(this);
	}
	
	public void terminateLogging() {
	    stateMachine.removeDeclarativeListener(this);
	}
	
    @OnTransitionBegin
    public void onTransitionBegin(Object sourceState, Object event, Object context) {
        sw = new Stopwatch();
        sw.start();
        logger.info(stateMachineLabel + ": Transition from \"" + sourceState + 
                "\" on \"" + event + "\" with context \""+context+"\" begin.");
    }
    
    @OnTransitionComplete
    public void onTransitionComplete(Object sourceState, Object targetState, Object event, Object context) {
        logger.info(stateMachineLabel + ": Transition from \"" + sourceState + 
                "\" to \"" + targetState + "\" on \"" + event
                + "\" complete which took " + sw.elapsedMillis() + "ms.");
    }
    
    @OnTransitionDecline
    public void onTransitionDeclined(Object sourceState, Object event) {
        logger.warn(stateMachineLabel + ": Transition from \"" + sourceState + "\" on \"" + event+ "\" declined.");
    }
    
    @OnTransitionException
    public void onTransitionException(Object sourceState, Object targetState, Object event, Object context, Exception e) {
        logger.error(stateMachineLabel + ": Transition from \"" + sourceState + 
                "\" to \"" + targetState + "\" on \"" + event + "\" caused exception.", e);
    }
    
    @OnActionExecute
    public void onActionExecute(Object sourceState, Object targetState, 
            Object event, Object context, int[] mOfN, Action<?, ?, ?,?> action) {
        logger.info("Before execute method call action \""+action.name()+"\" ("+ mOfN[0] + " of "+mOfN[1]+").");
    }
    
    @OnActionExecException
    public void onActionExecException(Action<?, ?, ?,?> action, Exception e) {
        logger.error("Executing method call action \""+action.name()+"\" caused exception.");
    }
}
