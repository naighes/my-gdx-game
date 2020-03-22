package com.mygdx.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.DialogTextBox;
import com.mygdx.game.Guest;
import com.mygdx.game.Player;

import java.util.HashMap;
import java.util.Map;

class PendingConversation {
    private final Player player;
    final Guest guest;
    final String text;

    PendingConversation(Player player,
                        Guest guest,
                        String text) {
        this.player = player;
        this.guest = guest;
        this.text = text;
    }
}

public class ConversationsController {
    private final DialogTextBox textBox;
    private final Map<String, Float> conversationHistory;
    private PendingConversation pendingConversation;

    public ConversationsController(DialogTextBox textBox) {
        this.textBox = textBox;
        this.conversationHistory = new HashMap<>();
    }

    public boolean canStartConversation(Guest guest) {
        if (!guest.wannaTalk()) {
            return false;
        }

        if (this.hasPendingConversation()) {
            return false;
        }

        Float time = this.conversationHistory.get(guest.getDescriptor().name);
        return time == null || time > 10f;
    }

    public void tryStartConversation(Guest guest, Player player) {
        if (!this.canStartConversation(guest)) {
            return;
        }

        String[] availableConversations = guest.getAvailableConversations();
        this.conversationHistory.put(guest.getDescriptor().name, 0f);
        this.pendingConversation = new PendingConversation(player,
                guest,
                availableConversations[0]);
    }

    public void update(float delta) {
        if (this.hasPendingConversation() &&
                this.textBox.isConsumed() &&
                Gdx.input.isTouched()) {
            this.pendingConversation = null;
            this.textBox.reset();
        }

        for (Map.Entry<String, Float> entry : this.conversationHistory.entrySet()) {
            String busy = this.hasPendingConversation()
                    ? this.pendingConversation.guest.getDescriptor().name
                    : null;
            if (entry.getKey() != busy) {
                this.conversationHistory.put(entry.getKey(), entry.getValue() + delta);
            }
        }
    }

    public void draw(Batch batch) {
        if (this.hasPendingConversation()) {
            this.textBox.draw(batch, this.pendingConversation.text, 300f, 200f); // TODO
        }
    }

    public boolean hasPendingConversation() {
        return this.pendingConversation != null;
    }

    public boolean hasPendingConversationWith(Guest guest) {
        return this.pendingConversation != null &&
                this.pendingConversation.guest.getDescriptor().name == guest.getDescriptor().name;
    }
}
