package ru.praktikum.services.scooter;

public class DeleteCourier {
    private boolean ok;

    public DeleteCourier(boolean ok) {
        this.ok = ok;
    }

    public DeleteCourier() {
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
