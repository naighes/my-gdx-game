package com.mygdx.game.controllers;

import com.mygdx.game.Guest;

public class TalkWithGuestResult extends PlayerStateControllerResult {
    private final Guest guest;

    public TalkWithGuestResult(PlayerState state, Guest guest) {
        super(state);
        this.guest = guest;
    }

    public void process() {
        guest.talk();
    }
}
