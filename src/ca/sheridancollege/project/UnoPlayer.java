
package ca.sheridancollege.project;
import java.util.ArrayList;

class UnoPlayer extends Player {
    private final ArrayList<UnoCard> hand = new ArrayList<>();

    public UnoPlayer(String name) {
        super(name);
    }

    public ArrayList<UnoCard> getHand() {
        return hand;
    }

    public void addCard(UnoCard card) {
        hand.add(card);
    }

    public void removeCard(UnoCard card) {
        hand.remove(card);
    }

    public boolean hasOneCard() {
        return hand.size() == 1;
    }

    @Override
    public void play() {
        // Gameplay logic to be handled in UnoGame
    }
}