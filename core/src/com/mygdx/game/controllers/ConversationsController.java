package com.mygdx.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.DialogTextBox;
import com.mygdx.game.GameCamera;
import com.mygdx.game.Guest;
import com.mygdx.game.Player;
import com.mygdx.game.Scenario;

import java.util.HashMap;
import java.util.Map;

class PendingConversation {
    final Player player;
    final Guest guest;

    private int currentIndex;

    PendingConversation(Player player,
                        Guest guest) {
        this.player = player;
        this.guest = guest;
        this.currentIndex = 0;
    }

    String current() {
        return this.guest.getDescriptor().conversations[this.currentIndex];
    }

    boolean moveNext() {
        int next = this.currentIndex + 1;

        if (next >= this.guest.getDescriptor().conversations.length) {
            return false;
        }

        this.currentIndex = next;
        return true;
    }
}

public class ConversationsController {
    private final Scenario scenario;
    private final DialogTextBox textBox;
    private final Map<String, Float> conversationHistory;
    private PendingConversation pendingConversation;

    public ConversationsController(Scenario scenario, DialogTextBox textBox) {
        this.scenario = scenario;
        this.textBox = textBox;
        this.conversationHistory = new HashMap<>();
    }

    boolean canStartConversation(Guest guest) {
        if (!guest.wannaTalk()) {
            return false;
        }

        if (this.hasPendingConversation()) {
            return false;
        }

        Float time = this.conversationHistory.get(guest.getDescriptor().name);
        return time == null || time > 10f;
    }

    void tryStartConversation(Guest guest, Player player) {
        if (!this.canStartConversation(guest)) {
            return;
        }

        this.conversationHistory.put(guest.getDescriptor().name, 0f);
        this.pendingConversation = new PendingConversation(player, guest);
        this.textBox.setText(this.pendingConversation.current());
    }

    public void update(float delta) {
        this.updateTextBox();

        for (Map.Entry<String, Float> entry : this.conversationHistory.entrySet()) {
            String busy = this.hasPendingConversation()
                    ? this.pendingConversation.guest.getDescriptor().name
                    : null;
            if (entry.getKey() != busy) {
                this.conversationHistory.put(entry.getKey(), entry.getValue() + delta);
            }
        }
    }

    private void updateTextBox() {
        if (!this.hasPendingConversation()) {
            return;
        }

        if (this.textBox.isConsumed() && Gdx.input.isTouched()) {
            boolean hasNext = this.pendingConversation.moveNext();

            if (hasNext) {
                this.textBox.setText(this.pendingConversation.current());
            } else {
                this.pendingConversation = null;
                this.textBox.resetAll();
            }
        }
    }

    public void draw(Batch batch) {
        if (this.hasPendingConversation()) {
            Vector2 position = calculateTextPosition(this.scenario.getCamera(),
                    this.pendingConversation.player);
            this.textBox.setX(position.x);
            this.textBox.setY(position.y);
            this.textBox.draw(batch);
        }
    }

    private Vector2 calculateTextPosition(GameCamera camera, Player player) {
        final float offset = 20f;

        float cx = player.getX() > camera.position.x
                ? camera.position.x - (camera.viewportWidth / 2f) + offset
                : camera.position.x + (camera.viewportWidth / 2f) - offset - this.textBox.getWidth();
        float cy = player.getY() > camera.position.y
                ? camera.position.y - (camera.viewportHeight / 2f) + offset
                : camera.position.y + (camera.viewportHeight / 2f) - offset - this.textBox.getHeight();

        return new Vector2(cx, cy);
    }

    boolean hasPendingConversation() {
        return this.pendingConversation != null;
    }

    public boolean hasPendingConversationWith(Guest guest) {
        return this.pendingConversation != null &&
                this.pendingConversation.guest.getDescriptor().name == guest.getDescriptor().name;
    }
}
