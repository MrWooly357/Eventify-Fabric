package net.mrwooly357.eventify.event;

import net.mrwooly357.eventify.event.context.EventContext;

public abstract class SingleTickEvent<C extends EventContext> extends Event<C> {

    protected SingleTickEvent(C context) {
        super(context);
    }


    public abstract void emit(C context);
}
