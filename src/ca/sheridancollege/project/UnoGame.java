package ca.sheridancollege.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class UnoGame extends Game {
    private final GroupOfCards deck = new GroupOfCards(108);
    private UnoCard currentCard;
    private int currentPlayerIndex = 0;
    private boolean isReversed = false;

    public UnoGame(String name) {
        super(name);
        initializeDeck();
    }

    private void initializeDeck() {
        String[] colors = { "Red", "Green", "Blue", "Yellow" };
        String[] values = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Draw Two", "Skip", "Reverse" };

        // Add numbered and action cards
        for (String color : colors) {
            for (String value : values) {
                deck.getCards().add(new UnoCard(color, value));
                if (!value.equals("0")) { // Add one more card of the same type (except for "0")
                    deck.getCards().add(new UnoCard(color, value));
                }
            }
        }

        // Add Wild cards
        for (int i = 0; i < 4; i++) {
            deck.getCards().add(new UnoCard("Wild", "Wild Card"));
            deck.getCards().add(new UnoCard("Wild", "Wild Draw Four"));
        }
        deck.shuffle();
    }

    @Override
    public void play() {
        Scanner scanner = new Scanner(System.in);
        setupGame(scanner);

        boolean gameRunning = true;
        while (gameRunning) {
            UnoPlayer currentPlayer = (UnoPlayer) getPlayers().get(currentPlayerIndex); // Safe to cast as all players are UnoPlayer
            System.out.println("\n" + currentPlayer.getName() + "'s turn.");
            System.out.println("Current card: " + currentCard);
            System.out.println("Your hand: " + currentPlayer.getHand());

            System.out.print("Choose a card to play (index) or draw (-1): ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the leftover newline character

            if (choice == -1) {
                UnoCard drawnCard = (UnoCard) deck.getCards().remove(deck.getCards().size() - 1);
                currentPlayer.addCard(drawnCard);
                System.out.println("You drew: " + drawnCard);
            } else if (choice >= 0 && choice < currentPlayer.getHand().size()) {
                UnoCard playedCard = currentPlayer.getHand().get(choice);
                if (isValidMove(currentCard, playedCard)) {
                    currentCard = playedCard;
                    currentPlayer.removeCard(playedCard);
                    System.out.println("You played: " + playedCard);

                    handleActionCard(playedCard, scanner);

                    if (currentPlayer.getHand().isEmpty()) {
                        declareWinner(currentPlayer); // Pass the winner here
                        gameRunning = false;
                    } else if (currentPlayer.hasOneCard()) {
                        System.out.println(currentPlayer.getName() + " says UNO!");
                    }
                } else {
                    System.out.println("Invalid move! Card does not match color or value.");
                }
            } else {
                System.out.println("Invalid choice.");
            }

            updateTurn();
        }

        scanner.close();
    }

    private void setupGame(Scanner scanner) {
        System.out.print("Enter the number of players (2-10): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();  // Clear the newline character after reading the integer
        System.out.println("Number of players entered: " + numPlayers);
    
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String name = scanner.nextLine();  // Now this will correctly read the name
            getPlayers().add(new UnoPlayer(name)); // Add UnoPlayer to the players list
        }
    
        // Deal 7 cards to each player
        for (Player player : getPlayers()) {
            UnoPlayer unoPlayer = (UnoPlayer) player;
            for (int j = 0; j < 7; j++) {
                unoPlayer.addCard((UnoCard) deck.getCards().remove(deck.getCards().size() - 1));
            }
        }
    
        // Draw the first card to start the game
        currentCard = (UnoCard) deck.getCards().remove(deck.getCards().size() - 1);
        System.out.println("Game started! Initial card: " + currentCard);
    }
    

    private boolean isValidMove(UnoCard currentCard, UnoCard playedCard) {
        return playedCard.getColor().equals(currentCard.getColor()) ||
                playedCard.getValue().equals(currentCard.getValue()) ||
                playedCard.getColor().equals("Wild");
    }

    private void handleActionCard(UnoCard card, Scanner scanner) {
        switch (card.getValue()) {
            case "Reverse":
                isReversed = !isReversed;
                break;
            case "Skip":
                updateTurn();
                break;
            case "Draw Two":
                drawCardsForNextPlayer(2);
                updateTurn();
                break;
            case "Wild Card":
                chooseNewColor(scanner);
                break;
            case "Wild Draw Four":
                chooseNewColor(scanner);
                drawCardsForNextPlayer(4);
                updateTurn();
                break;
        }
    }

    private void chooseNewColor(Scanner scanner) {
        System.out.print("Choose a new color (Red, Green, Blue, Yellow): ");
        String newColor = scanner.next();
        currentCard = new UnoCard(newColor, "Wild");
    }

    private void drawCardsForNextPlayer(int count) {
        int nextPlayerIndex = getNextPlayerIndex();
        UnoPlayer nextPlayer = (UnoPlayer) getPlayers().get(nextPlayerIndex);
        for (int i = 0; i < count; i++) {
            nextPlayer.addCard((UnoCard) deck.getCards().remove(deck.getCards().size() - 1));
        }
    }

    private int getNextPlayerIndex() {
        int direction = isReversed ? -1 : 1;
        return (currentPlayerIndex + direction + getPlayers().size()) % getPlayers().size();
    }

    private void updateTurn() {
        currentPlayerIndex = getNextPlayerIndex();
    }

    @Override
    public void declareWinner(Player winner) {
        System.out.println("The winner is " + winner.getName());
    }

    public static void main(String[] args) {
        UnoGame game = new UnoGame("UNO");
        game.play();
    }
}

