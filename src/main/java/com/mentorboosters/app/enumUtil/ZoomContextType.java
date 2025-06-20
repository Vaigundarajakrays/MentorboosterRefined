package com.mentorboosters.app.enumUtil;

// enum by default static and final
// Enums can have methods, fields, and constructors
public enum ZoomContextType {
    NEW, RESCHEDULE;

    public boolean isReschedule(){
        return this == RESCHEDULE;
    }
}
