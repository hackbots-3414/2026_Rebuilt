package frc.robot.binding;

import frc.robot.superstructure.Superstructure;

public class MultiBindings implements Binder {
    private final Binder[] binders;

    public MultiBindings(Binder... binders) {
        this.binders = binders;
    }

    public void bind(Superstructure superstructure) {
        for (Binder binder : binders) {
            binder.bind(superstructure);
        }
    }
}
